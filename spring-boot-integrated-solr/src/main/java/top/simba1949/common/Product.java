package top.simba1949.common;

import lombok.Data;

import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/6/9 19:03
 */
@Data
public class Product {
	private Integer pid;
	private String name;
	private Integer catalog;
	private String catalog_name;
	private Double price;
	private Integer number;
	private String description;
	private String picture;
	private Date release_time;

	@Override
	public String toString() {
		return "Product{" +
				"pid=" + pid +
				", name='" + name + '\'' +
				", catalog=" + catalog +
				", catalog_name='" + catalog_name + '\'' +
				", price=" + price +
				", number=" + number +
				", description='" + description + '\'' +
				", picture='" + picture + '\'' +
				", release_time=" + release_time +
				'}';
	}
}
