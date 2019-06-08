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
