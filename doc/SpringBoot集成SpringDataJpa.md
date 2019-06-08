# SpringBoot集成SpringDataJpa

## 单数据源——yml配置实现

项目名：single-data-source-yml-conf

> springboot 会根据 yml 配置文件中数据源信息自动配置

### pom 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>single-data-source-yml-conf</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!--继承springboot默认包-->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
    </parent>

    <!--版本管理-->
    <properties>
        <lombok.version>1.18.8</lombok.version>
    </properties>

    <dependencies>
        <!--web support-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--spring-boot-devtools-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional> <!-- 表示依赖不会传递 -->
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        <!--actuator-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- springboot test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

    </dependencies>

    <build>
        <defaultGoal>install</defaultGoal>
        <plugins>
            <!--compiler plugin-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.0</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <!--the plugin of resources copy-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <!--打包插件-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <!-- 如果没有该配置，devtools不会生效 -->
                    <fork>true</fork>
                </configuration>
            </plugin>
        </plugins>

        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.xml</include>
                    <include>**/*.properties</include>
                    <include>**/*.yml</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

### application.yml 配置信息

```yaml
server:
  port: 8081
spring:
  # 数据源设置
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    # characterEncoding=utf-8 设置编码，serverTimezone=Asia/Shanghai 设置时区
    url: jdbc:mysql://localhost:3306/search?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 19491001
  jpa:
    database: mysql
    show-sql: true
```

### logback-spring.xml 日志配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <property name="APP" value="logbackLearn" />
    <contextName>${APP}</contextName>

    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %class.%method.%L %msg  %ex{full} %n</pattern>
        </encoder>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="console" />
    </root>
</configuration>
```

### BookDto

```java
package top.simba1949.common;

import lombok.Data;

import javax.persistence.*;

/**
 * @Entity 标记为实体
 * @Table(name = "book") 设置表格
 * @Id 标注主键
 * @GeneratedValue(strategy = GenerationType.IDENTITY) 主键生成策略
 * @Column(name = "name") 标记列，数据库中列名和 JavaBean 属性名相同时，可以不要 name 配置
 *
 *
 * @author SIMBA1949
 * @date 2019/6/7 14:26
 */
@Data
@Entity
@Table(name = "book")
public class BookDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column
	private Double price;
	@Column
	private String pic;
	@Column
	private String desc;
}
```

### BookRepository

```java
package top.simba1949.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.simba1949.common.BookDto;

/**
 * @author SIMBA1949
 * @date 2019/6/7 14:28
 */
public interface BookRepository extends JpaRepository<BookDto, Integer> {
}
```

### BookController

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.common.BookDto;
import top.simba1949.repository.BookRepository;

import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/6/8 7:14
 */
@RestController
@RequestMapping("book")
public class BookController {


	@Autowired
	private BookRepository bookRepository;

	@GetMapping("all")
	public List<BookDto> getAll(){
		List<BookDto> all = bookRepository.findAll();
		return all;
	}
}
```

### 启动类 App

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author SIMBA1949
 * @date 2019/6/7 14:25
 */
@SpringBootApplication
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
```

## 单数据源——Java 配置实现

项目名：single-data-source-java-conf

> 由于 sprinboot 会自动配置，需要在启动类排除数据源配置，@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) 自己通过 Java 代码的方式进行配置

### pom 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>single-data-source-java-conf</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!--继承springboot默认包-->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
    </parent>

    <!--版本管理-->
    <properties>
        <lombok.version>1.18.8</lombok.version>
    </properties>

    <dependencies>
        <!--web support-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--spring-boot-devtools-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional> <!-- 表示依赖不会传递 -->
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        <!--actuator-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- springboot test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

    </dependencies>

    <build>
        <defaultGoal>install</defaultGoal>
        <plugins>
            <!--compiler plugin-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.0</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <!--the plugin of resources copy-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <!--打包插件-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <!-- 如果没有该配置，devtools不会生效 -->
                    <fork>true</fork>
                </configuration>
            </plugin>
        </plugins>

        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.xml</include>
                    <include>**/*.properties</include>
                    <include>**/*.yml</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

### application.yml 配置

```yaml
server:
  port: 8081
# 自定义数据源设置
datasource:
  search:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/search?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 19491001
spring:
  jpa:
    hibernate:
      # 方言设置
      dialect:
        search: org.hibernate.dialect.MySQLDialect
```

### logback-spring.xml 配置信息

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <property name="APP" value="logbackLearn" />
    <contextName>${APP}</contextName>

    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %class.%method.%L %msg  %ex{full} %n</pattern>
        </encoder>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="console" />
    </root>
</configuration>
```

### 数据源配置

```java
package top.simba1949.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SIMBA1949
 * @date 2019/6/8 8:02
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		// 配置连接工厂
		entityManagerFactoryRef = "entityManagerFactory4Search",
		// 配置事务管理器
		transactionManagerRef = "transactionManager4Search",
		// 设置持久层所在位置
		basePackages = "top.simba1949.repository"
)
public class SearchDbConfig {

	@Autowired
	private JpaProperties jpaProperties;
	/**
	 * 获取对应的数据库方言
	 */
	@Value("${spring.jpa.hibernate.dialect.search}")
	private String dialect;
	@Value("${datasource.search.driver-class-name}")
	private String driverClassName;
	@Value("${datasource.search.url}")
	private String url;
	@Value("${datasource.search.username}")
	private String username;
	@Value("${datasource.search.password}")
	private String password;

	/**
	 * 创建数据源
	 * @return
	 */
	@Bean(name = "searchDbDataSource")
	@Qualifier("searchDbDataSource")
	public DataSource searchDbDataSource(){
		return DataSourceBuilder.create()
				.driverClassName(driverClassName)
				.url(url)
				.username(username)
				.password(password)
				.build();
	}

	/**
	 * 数据库连接工厂设置
	 * @param builder
	 * @return
	 */
	@Bean(name = "entityManagerFactory4Search")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder){
		return builder
				// 设置数据源
				.dataSource(searchDbDataSource())
				// 设置数据源属性
				.properties(getVendorProperties(searchDbDataSource()))
				//设置实体类所在位置.扫描所有带有 @Entity 注解的类
				.packages("top.simba1949.common")
				// Spring会将EntityManagerFactory注入到Repository之中.有了 EntityManagerFactory之后,
				// Repository就能用它来创建 EntityManager 了,然后 EntityManager 就可以针对数据库执行操作
				.persistenceUnit("primaryPersistenceUnit")
		.build();
	}

	private Map<String, String> getVendorProperties(DataSource searchDbDataSource) {
		Map<String, String> map = new HashMap<>(16);
		// 设置数据库对应的方言
		map.put("hibernate.dialect", dialect);
		jpaProperties.setProperties(map);
		return jpaProperties.getProperties();
	}

	/**
	 * 配置事务管理器
	 * @param builder
	 * @return
	 */
	@Bean(name = "transactionManager4Search")
	public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder  builder){
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory(builder).getObject());
		return txManager;
	}
}
```

### BookDto

```java
package top.simba1949.common;

import lombok.Data;

import javax.persistence.*;

/**
 * @Entity 标记为实体
 * @Table(name = "book") 设置表格
 * @Id 标注主键
 * @GeneratedValue(strategy = GenerationType.IDENTITY) 主键生成策略
 * @Column(name = "name") 标记列，数据库中列名和 JavaBean 属性名相同时，可以不要 name 配置
 *
 *
 * @author SIMBA1949
 * @date 2019/6/7 14:26
 */
@Data
@Entity
@Table(name = "book")
public class BookDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column
	private Double price;
	@Column
	private String pic;
	@Column
	private String desc;
}
```

### BookRepository

```java
package top.simba1949.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.simba1949.common.BookDto;

/**
 * @author SIMBA1949
 * @date 2019/6/7 14:28
 */
public interface BookRepository extends JpaRepository<BookDto, Integer> {
}
```

### BookController

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.common.BookDto;
import top.simba1949.repository.BookRepository;

import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/6/8 7:14
 */
@RestController
@RequestMapping("book")
public class BookController {


	@Autowired
	private BookRepository bookRepository;

	@GetMapping("all")
	public List<BookDto> getAll(){
		List<BookDto> all = bookRepository.findAll();
		return all;
	}
}
```

### 启动类 App

```java
package top.simba1949;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * springboot 会自动从 springboot 配置文件中寻找数据源配置
 * 自定义数据源需要排除 springboot 的数据源配置
 * 
 * @author SIMBA1949
 * @date 2019/6/8 7:54
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
```

## 多数据源配置

### pom 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>top.simba1949</groupId>
    <artifactId>multi-data-source-conf</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!--继承springboot默认包-->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
    </parent>

    <!--版本管理-->
    <properties>
        <lombok.version>1.18.8</lombok.version>
    </properties>

    <dependencies>
        <!--web support-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--spring-boot-devtools-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional> <!-- 表示依赖不会传递 -->
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        <!--actuator-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- springboot test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

    </dependencies>

    <build>
        <defaultGoal>install</defaultGoal>
        <plugins>
            <!--compiler plugin-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.0</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <!--the plugin of resources copy-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <!--打包插件-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <!-- 如果没有该配置，devtools不会生效 -->
                    <fork>true</fork>
                </configuration>
            </plugin>
        </plugins>

        <!--IDEA是不会编译src的java目录的xml文件，如果需要读取，则需要手动指定哪些配置文件需要读取-->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.xml</include>
                    <include>**/*.properties</include>
                    <include>**/*.yml</include>
                </includes>
            </resource>
        </resources>
    </build>
</project>
```

### application.yml 配置

```yaml
server:
  port: 8081
# 自定义数据源设置
datasource:
  # 数据源一配置
  search:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/search?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 19491001
  # 数据源二配置
  rbac:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/rbac?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 19491001
spring:
  jpa:
    hibernate:
      # 方言设置
      dialect:
        search: org.hibernate.dialect.MySQLDialect
```

### logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <property name="APP" value="logbackLearn" />
    <contextName>${APP}</contextName>

    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %class.%method.%L %msg  %ex{full} %n</pattern>
        </encoder>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="console" />
    </root>
</configuration>
```

### 数据源配置

#### 数据源一配置(需要使用@Primary 声明主数据源配置 )

```java
package top.simba1949.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SIMBA1949
 * @date 2019/6/8 8:02
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		// 配置连接工厂
		entityManagerFactoryRef = "entityManagerFactory4Search",
		// 配置事务管理器
		transactionManagerRef = "transactionManager4Search",
		// 设置持久层所在位置
		basePackages = "top.simba1949.repository.search"
)
public class SearchDbConfig {

	@Autowired
	private JpaProperties jpaProperties;
	/**
	 * 获取对应的数据库方言
	 */
	@Value("${spring.jpa.hibernate.dialect.search}")
	private String dialect;
	@Value("${datasource.search.driver-class-name}")
	private String driverClassName;
	@Value("${datasource.search.url}")
	private String url;
	@Value("${datasource.search.username}")
	private String username;
	@Value("${datasource.search.password}")
	private String password;

	/**
	 * @Primary 需要声明主数据库配置信息
	 *
	 * 创建数据源
	 * @return
	 */
	@Bean(name = "searchDbDataSource")
	@Qualifier("searchDbDataSource")
	@Primary
	public DataSource searchDbDataSource(){
		return DataSourceBuilder.create()
				.driverClassName(driverClassName)
				.url(url)
				.username(username)
				.password(password)
				.build();
	}

	/**
	 * 数据库连接工厂设置
	 * @Primary 需要声明主数据库配置信息
	 *
	 * @param builder
	 * @return
	 */
	@Bean(name = "entityManagerFactory4Search")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder){
		return builder
				// 设置数据源
				.dataSource(searchDbDataSource())
				// 设置数据源属性
				.properties(getVendorProperties(searchDbDataSource()))
				//设置实体类所在位置.扫描所有带有 @Entity 注解的类
				.packages("top.simba1949.common.search")
				// Spring会将EntityManagerFactory注入到Repository之中.有了 EntityManagerFactory之后,
				// Repository就能用它来创建 EntityManager 了,然后 EntityManager 就可以针对数据库执行操作
				.persistenceUnit("primaryPersistenceUnit")
		.build();
	}

	private Map<String, String> getVendorProperties(DataSource searchDbDataSource) {
		Map<String, String> map = new HashMap<>(16);
		// 设置数据库对应的方言
		map.put("hibernate.dialect", dialect);
		jpaProperties.setProperties(map);
		return jpaProperties.getProperties();
	}

	/**
	 * 配置事务管理器
	 * @Primary 需要声明主数据库配置信息
	 *
	 * @param builder
	 * @return
	 */
	@Bean(name = "transactionManager4Search")
	@Primary
	public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder  builder){
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory(builder).getObject());
		return txManager;
	}
}
```

#### 数据源二配置

```java
package top.simba1949.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SIMBA1949
 * @date 2019/6/8 10:15
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		// 配置连接工厂
		entityManagerFactoryRef = "entityManagerFactory4Rbac",
		// 配置事务管理器
		transactionManagerRef = "transactionManager4Rbac",
		// 设置持久层所在位置
		basePackages = "top.simba1949.repository.rbac"
)
public class RbacDbConfig {
	@Autowired
	private JpaProperties jpaProperties;
	/**
	 * 获取对应的数据库方言
	 */
	@Value("${spring.jpa.hibernate.dialect.search}")
	private String dialect;
	@Value("${datasource.rbac.driver-class-name}")
	private String driverClassName;
	@Value("${datasource.rbac.url}")
	private String url;
	@Value("${datasource.rbac.username}")
	private String username;
	@Value("${datasource.rbac.password}")
	private String password;

	/**
	 * 创建数据源
	 * @return
	 */
	@Bean(name = "rbacDbDataSource")
	@Qualifier("rbacDbDataSource")
	public DataSource rbacDbDataSource(){
		return DataSourceBuilder.create()
				.driverClassName(driverClassName)
				.url(url)
				.username(username)
				.password(password)
				.build();
	}

	/**
	 * 数据库连接工厂设置
	 * @param builder
	 * @return
	 */
	@Bean(name = "entityManagerFactory4Rbac")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder){
		return builder
				// 设置数据源
				.dataSource(rbacDbDataSource())
				// 设置数据源属性
				.properties(getVendorProperties(rbacDbDataSource()))
				//设置实体类所在位置.扫描所有带有 @Entity 注解的类
				.packages("top.simba1949.common.rbac")
				// Spring会将EntityManagerFactory注入到Repository之中.有了 EntityManagerFactory之后,
				// Repository就能用它来创建 EntityManager 了,然后 EntityManager 就可以针对数据库执行操作
				.persistenceUnit("primaryPersistenceUnit")
				.build();
	}

	private Map<String, String> getVendorProperties(DataSource searchDbDataSource) {
		Map<String, String> map = new HashMap<>(16);
		// 设置数据库对应的方言
		map.put("hibernate.dialect", dialect);
		jpaProperties.setProperties(map);
		return jpaProperties.getProperties();
	}

	/**
	 * 配置事务管理器
	 * @param builder
	 * @return
	 */
	@Bean(name = "transactionManager4Rbac")
	public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder  builder){
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory(builder).getObject());
		return txManager;
	}
}
```

### common

#### UserDto

```java
package top.simba1949.common.rbac;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据库设计时，需要避免和数据库关键字一样
 * 
 * @author SIMBA1949
 * @date 2019/6/8 10:02
 */
@Data
@Entity
@Table(name = "t_user")
public class UserDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "username")
	private String username;
	@Column(name = "password")
	private String password;
	@Column
	private Date birthday;
	@Column(name = "description")
	private String desc;
}
```

#### BookDto

```java
package top.simba1949.common.search;

import lombok.Data;

import javax.persistence.*;

/**
 * @Entity 标记为实体
 * @Table(name = "book") 设置表格
 * @Id 标注主键
 * @GeneratedValue(strategy = GenerationType.IDENTITY) 主键生成策略
 * @Column(name = "name") 标记列，数据库中列名和 JavaBean 属性名相同时，可以不要 name 配置
 *
 *
 * @author SIMBA1949
 * @date 2019/6/7 14:26
 */
@Data
@Entity
@Table(name = "book")
public class BookDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column
	private Double price;
	@Column
	private String pic;
	@Column
	private String desc;
}
```

### repository

#### UserRepository

```java
package top.simba1949.repository.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import top.simba1949.common.rbac.UserDto;

/**
 * @author SIMBA1949
 * @date 2019/6/8 10:23
 */
public interface UserRepository extends JpaRepository<UserDto, Integer> {
}
```

#### BookRepository

```java
package top.simba1949.repository.search;

import org.springframework.data.jpa.repository.JpaRepository;
import top.simba1949.common.search.BookDto;

/**
 * @author SIMBA1949
 * @date 2019/6/7 14:28
 */
public interface BookRepository extends JpaRepository<BookDto, Integer> {
}
```

### controller

#### UserController

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.common.rbac.UserDto;
import top.simba1949.repository.rbac.UserRepository;

/**
 * @author SIMBA1949
 * @date 2019/6/8 10:24
 */
@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@PostMapping
	public String insert(@RequestBody UserDto userDto){
		UserDto user = userRepository.saveAndFlush(userDto);
		return user.toString();
	}
}
```

#### BookController

```java
package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.common.search.BookDto;
import top.simba1949.repository.search.BookRepository;

import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/6/8 7:14
 */
@RestController
@RequestMapping("book")
public class BookController {


	@Autowired
	private BookRepository bookRepository;

	@GetMapping("all")
	public List<BookDto> getAll(){
		List<BookDto> all = bookRepository.findAll();
		return all;
	}
}
```













