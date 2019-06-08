package top.simba1949.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.simba1949.common.Book;
import top.simba1949.repository.BookRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author SIMBA1949
 * @date 2019/6/8 13:59
 */
@Component
public class SearchLucene {

	@Autowired
	private BookRepository bookRepository;

	public List<Book> search(String field, String searchKey, int num) throws ParseException, IOException {
		// 创建分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 1. 创建 Query 对象
		QueryParser queryParser = new QueryParser(field, analyzer);
		Query query = queryParser.parse(field + ":" + searchKey);
		// 2. 创建 Directory 流对象，声明索引的位置
		String path = "T:/Tools/luke/Workspace";
		Directory directory = FSDirectory.open(new File(path + "/lucene").toPath());
		// 3. 创建索引读取对象 IndexReader
		IndexReader indexReader = DirectoryReader.open(directory);
		// 4. 创建索引搜索对象 IndexSearch
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 5. 使用索引搜索对象，执行搜索，返回结果集 TopDocs
		TopDocs topDocs = indexSearcher.search(query, num);
		// 6. 解析结果集
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		Set<Book> searchBooks = new HashSet<>();
		for (ScoreDoc scoreDoc : scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);

			Book bookTemp = new Book();
			bookTemp.setId(Integer.valueOf(document.get("id")));
			bookTemp.setName(document.get("name"));
			bookTemp.setPrice(Double.valueOf(document.get("price")));
			bookTemp.setPic(document.get("pic"));
			bookTemp.setDescription(document.get("description"));

			searchBooks.add(bookTemp);
		}
		// 7. 释放资源
		indexReader.close();
		List<Book> books = new ArrayList<>(searchBooks);
		return books;
	}

	/**
	 * 通用查询
	 * @param query
	 * @param num
	 * @return
	 * @throws IOException
	 */
	public List<Book> commonSearch(Query query, int num) throws IOException {
		// 2. 创建 Directory 流对象，声明索引的位置
		String path = "T:/Tools/luke/Workspace";
		Directory directory = FSDirectory.open(new File(path + "/lucene").toPath());
		// 3. 创建索引读取对象 IndexReader
		IndexReader indexReader = DirectoryReader.open(directory);
		// 4. 创建索引搜索对象 IndexSearch
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 5. 使用索引搜索对象，执行搜索，返回结果集 TopDocs，默认查询 1000 条
		TopDocs topDocs = indexSearcher.search(query, num);
		// 6. 解析结果集
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		Set<Book> searchBooks = new HashSet<>();
		for (ScoreDoc scoreDoc : scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);

			Book bookTemp = new Book();
			if (null != document.get("id") && !"".equals(document.get("id"))){
				bookTemp.setId(Integer.valueOf(document.get("id")));
			}
			if (null != document.get("name")){
				bookTemp.setName(document.get("name"));
			}
			if (null != document.get("price") && !"".equals(document.get("price"))){
				bookTemp.setPrice(Double.valueOf(document.get("price")));
			}
			if (null != document.get("pic")){
				bookTemp.setPic(document.get("pic"));
			}
			if (null != document.get("description")){
				bookTemp.setDescription(document.get("description"));
			}

			searchBooks.add(bookTemp);
		}
		// 7. 释放资源
		indexReader.close();
		List<Book> books = new ArrayList<>(searchBooks);
		return books;
	}

	/**
	 * 使用 TermQuery 查询
	 * @param field
	 * @param searchKey
	 * @param num
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public List<Book> searchByTermQuery(String field, String searchKey, int num) throws ParseException, IOException {
		// 1. 创建 Query 对象
		Query query = new TermQuery(new Term(field, searchKey));

		List<Book> books = commonSearch(query, num);
		return books;
	}

	/**
	 * 使用 BooleanQuery
	 * @return
	 * @throws IOException
	 */
	public List<Book> searchBooleanQuery() throws IOException {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(new TermQuery(new Term("name", "君")), BooleanClause.Occur.SHOULD);
		builder.add(new TermQuery(new Term("name", "spring")), BooleanClause.Occur.SHOULD);


		List<Book> books = commonSearch(builder.build(), 100);
		return books;
	}

	/**
	 * QueryParser 查询
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public List<Book> searchQueryParser() throws ParseException, IOException {
		Analyzer analyzer = new StandardAnalyzer();
		// 第一个参数表示默认查询域，第二个参数表示分词器
		QueryParser queryParser = new QueryParser("name", analyzer);
		Query parse = queryParser.parse("name:spring");
		return commonSearch(parse, 100);
	}

	/**
	 * MultiFieldQueryParser 查询
	 * @return
	 * @throws ParseException
	 */
	public List<Book> searchMultiFieldQueryParser() throws ParseException, IOException {
		String[] fields = {"name", "desc"};
		Analyzer analyzer = new StandardAnalyzer();
		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, analyzer);
		Query query = multiFieldQueryParser.parse("spring");
		return commonSearch(query, 100);
	}

}
