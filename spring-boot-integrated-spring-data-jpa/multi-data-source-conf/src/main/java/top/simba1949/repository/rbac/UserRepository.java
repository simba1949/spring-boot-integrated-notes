package top.simba1949.repository.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import top.simba1949.common.rbac.UserDto;

/**
 * @author SIMBA1949
 * @date 2019/6/8 10:23
 */
public interface UserRepository extends JpaRepository<UserDto, Integer> {
}
