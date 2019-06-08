package top.simba1949.controller;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.simba1949.common.Book;
import top.simba1949.lucene.CreateLuceneIndex;
import top.simba1949.lucene.SearchLucene;
import top.simba1949.repository.BookRepository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/6/7 13:10
 */
@RestController
@RequestMapping("book")
public class BookController {

	@Autowired
	private CreateLuceneIndex luceneIndex;
	@PostConstruct
	public void creatIndex() throws IOException {
		luceneIndex.createIndex();
	}
	@Resource
	private BookRepository bookRepository;

	@GetMapping("all")
	public void queryList(){
		List<Book> all = bookRepository.findAll();
		all.forEach(item -> System.out.println(item));
	}

	@Autowired
	private SearchLucene searchLucene;
	@GetMapping("search")
	public List<Book> searchKey(String field, String searchKey, int num) throws IOException, ParseException {
		return searchLucene.search(field, searchKey, num);
	}
}
