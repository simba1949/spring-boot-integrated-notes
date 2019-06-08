package top.simba1949.lucene;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;
import top.simba1949.common.Book;

import java.io.IOException;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/6/8 17:54
 */
public class LuceneLearn02 {

	private SearchLucene searchLucene;
	@Before
	public void init(){
		searchLucene = new SearchLucene();
	}

	@Test
	public void searchByTermQueryTest() throws IOException, ParseException {
		// 相当于
		List<Book> books = searchLucene.searchByTermQuery("name", "黄", 1000);
		books.forEach(item->System.out.println(item));
	}

	@Test
	public void searchBooleanQueryTest() throws IOException {
		List<Book> books = searchLucene.searchBooleanQuery();
		books.forEach(item-> System.out.println(item));
	}

	@Test
	public void searchQueryParserTest() throws IOException, ParseException {
		List<Book> books = searchLucene.searchQueryParser();
		books.forEach(item-> System.out.println(item));
	}

	@Test
	public void searchMultiFieldQueryParserTest() throws IOException, ParseException {
		List<Book> books = searchLucene.searchMultiFieldQueryParser();
		books.forEach(item-> System.out.println(item));
	}

}
