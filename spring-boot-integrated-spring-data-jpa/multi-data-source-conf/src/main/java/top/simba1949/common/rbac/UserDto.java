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
