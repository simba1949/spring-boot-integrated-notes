package top.simba1949.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.common.search.BookDto;
import top.simba1949.repository.search.BookRepository;

import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/6/8 7:14
 */
@RestController
@RequestMapping("book")
public class BookController {


	@Autowired
	private BookRepository bookRepository;

	@GetMapping("all")
	public List<BookDto> getAll(){
		List<BookDto> all = bookRepository.findAll();
		return all;
	}
}
