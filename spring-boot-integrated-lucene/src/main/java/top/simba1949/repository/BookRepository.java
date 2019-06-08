package top.simba1949.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.simba1949.common.Book;

/**
 * @author SIMBA1949
 * @date 2019/6/7 13:09
 */
public interface BookRepository extends JpaRepository<Book, Integer> {
}
