package top.simba1949.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.simba1949.common.Book;
import top.simba1949.repository.BookRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 分词是为了做索引，索引是为了做搜索，存储是为了获取完整信息，不需要显示的可以不用存储
 *
 * @author SIMBA1949
 * @date 2019/6/8 11:39
 */
@Component
public class CreateLuceneIndex {

	@Autowired
	private BookRepository bookRepository;

	public void createIndex() throws IOException {
		// 1.采集数据
		List<Book> all = bookRepository.findAll();
		// 2.创建 document 对象
		List<Document> documents = new ArrayList<>();
		for (Book item : all) {
			// document 等同于数据库中的一张表
			Document document = new Document();
			// Field
			// TextField 等同于数据库中表的字段
			// new TextField(第一个参数域名，第二个参数是域值，第三个参数表示是否存储)

			// 主键：不分词、不索引、但是存储
			document.add(new StoredField("id", String.valueOf(item.getId())));
			// 名称：分词、索引、存储
			document.add(new TextField("name", item.getName(), Field.Store.YES));
			// 价格：不分词、不索引、存储
			document.add(new StoredField("price", String.valueOf(item.getPrice())));
			// 图片：不分词、不索引、存储
			document.add(new StoredField("pic", item.getPic()));
			// 详情：分词，索引，不存储
			TextField descriptionField = new TextField("description", item.getDescription(), Field.Store.NO);
			document.add(descriptionField);

			documents.add(document);
		}
		// 3.创建分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 4.创建 IndexWriterConfig 配置分词器
		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		// 5.创建 Directory 对象，声明索引库存储的位置
		String path = "T:/Tools/luke/Workspace";
		Directory directory = FSDirectory.open(new File(path + "/lucene").toPath());

		// 6.创建 IndexWriter 对象，用于操作索引库
		IndexWriter indexWriter = new IndexWriter(directory, config);
		indexWriter.addDocuments(documents);
		// 7.把 Document 对象写入到索引库中，提交数据
		indexWriter.commit();
		// 8.释放资源
		indexWriter.close();
	}
}
