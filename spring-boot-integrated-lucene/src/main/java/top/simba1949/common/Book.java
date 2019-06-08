package top.simba1949.common;

import lombok.Data;

import javax.persistence.*;

/**
 * @author SIMBA1949
 * @date 2019/6/7 13:00
 */
@Data
@Entity
@Table(name = "book")
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column
	private String name;
	@Column
	private Double price;
	@Column
	private String pic;
	@Column
	private String description;

	@Override
	public boolean equals(Object obj) {
		// 比较地址值是否相同
		if(this == obj){
			return true;
		}
		// 非空
		if(null == obj){
			return false;
		}
		Book book = (Book) obj;
		if (null != this.id && !this.id.equals(book.getId())) {
			return false;
		}
		if(null != this.name && !this.name.equals(book.getName())) {
			return false;
		}
		if (null != this.price && !this.price.equals(book.getPrice())) {
			return false;
		}
		if (null != this.pic && !this.pic.equals(book.getPic())){
			return false;
		}
		if (null != this.description && !this.description.equals(book.getDescription())){
			return false;
		}

		return true;
	}

	/**
	 * 31是质子数中一个“不大不小”的存在,
	 * 如果你使用的是一个如2的较小质数，那么得出的乘积会在一个很小的范围，很容易造成哈希值的冲突。
	 * 而如果选择一个100以上的质数，得出的哈希值会超出int的最大范围，这两种都不合适。
	 * @return
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (id == null ? 0 : id.hashCode());
		result = 31 * result + (name == null ? 0 : name.hashCode());
		result = 31 * result + (price == null ? 0 : price.hashCode());
		result = 31 * result + (pic == null ? 0 : pic.hashCode());
		result = 31 * result + (description == null ? 0 : description.hashCode());
		return result;
	}
}
