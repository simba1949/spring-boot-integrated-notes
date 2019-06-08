package top.simba1949.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author SIMBA1949
 * @date 2019/6/8 16:52
 */

public class LuceneLearn {
	/**
	 * 测试删除指定索引的 document
	 */
	@Test
	public void deleteIndexTest() throws IOException {
		// 创建一个分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 创建lucene存储文件对象 Directory
		String path = "T:/Tools/luke/Workspace";
		Directory directory = FSDirectory.open(new File(path + "/lucene").toPath());
		// 创建 IndexWriterConfig 并配置分词器
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 创建 IndexWriter 对象，用于操作索引库
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		// 根据 Term 删除指定搜索的 document
		indexWriter.deleteDocuments(new Term("name", "java"));
		// 提交
		indexWriter.commit();
		// 释放资源
		indexWriter.close();
	}

	/**
	 * 删除删除所有的索引库和文档域
	 * @throws IOException
	 */
	@Test
	public void deleteAllIndexTest() throws IOException {
		// 创建一个分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 创建lucene存储文件对象 Directory
		String path = "T:/Tools/luke/Workspace";
		Directory directory = FSDirectory.open(new File(path + "/lucene").toPath());
		// 创建 IndexWriterConfig 并配置分词器
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 创建 IndexWriter 对象，用于操作索引库
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		// 删除所有的索引库和文档域
		indexWriter.deleteAll();
		// 提交
		indexWriter.commit();
		// 释放资源
		indexWriter.close();
	}

	/**
	 * 更新索引和文档域
	 */
	@Test
	public void updateIndexTest() throws IOException {
		// 创建一个分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 创建lucene存储文件对象 Directory
		String path = "T:/Tools/luke/Workspace";
		Directory directory = FSDirectory.open(new File(path + "/lucene").toPath());
		// 创建 IndexWriterConfig 并配置分词器
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 创建 IndexWriter 对象，用于操作索引库
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		Document document = new Document();
		document.add(new StoredField("id", "9567"));
		document.add(new TextField("name", "君不见黄河之水天上来", Field.Store.YES));
		// 执行更新，会被所有符合条件的 Document 删除，在新增
		indexWriter.updateDocument(new Term("name", "黄河"), document);
		// 释放资源
		indexWriter.close();
	}
}
