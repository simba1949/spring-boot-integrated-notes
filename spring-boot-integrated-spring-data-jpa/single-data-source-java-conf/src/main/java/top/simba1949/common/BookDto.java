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
