package top.simba1949.common;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/6/9 19:03
 */
@Data
public class ProductAnnotation {
	/**
	 * @Field(value = "") 中 value 要和文档中域名对应，而且JavaBean类型要和文档中域的类型一一对应
	 */
	@Field(value = "id")
	private String pid;
	@Field(value = "product_name")
	private String name;
	private Integer catalog;
	@Field(value = "product_catalog_name")
	private String catalog_name;
	@Field(value = "product_price")
	private Double price;
	private Integer number;
	@Field(value = "product_description")
	private String description;
	@Field(value = "product_picture")
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
