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
