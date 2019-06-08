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
