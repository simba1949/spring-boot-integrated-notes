package top.simba1949.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.simba1949.common.BookDto;

/**
 * @author SIMBA1949
 * @date 2019/6/7 14:28
 */
public interface BookRepository extends JpaRepository<BookDto, Integer> {
}
