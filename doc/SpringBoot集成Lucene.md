# Lucene 学习笔记

## 前言

lucene 官网：http://lucene.apache.org/

luke github官网：<https://github.com/DmitryKey/luke/releases>

lucene 8.1.1 官方文档：<https://lucene.apache.org/core/8_1_1/index.html>

## 实现索引流程

1. 采集数据
2. 创建 Document 文档对象，
3. 创建分词器
4. 创建 IndexWriterConfig 配置信息类
5. 创建 Directory 对象，声明索引库存储的位置
6. 创建 IndexWriter 对象
7. 把 Document 对象写入到索引库中
8. 释放资源

### Field

> 分词的目的是为了索引，索引的目的是为了搜索，存储的目的是为了展示

|                           Field 类                           | 数据类型               | Analyzed 是否分词 | Indexed 是否索引 | Stored 是否存储 | 说明                                                         |
| :----------------------------------------------------------: | ---------------------- | ----------------- | ---------------- | --------------- | ------------------------------------------------------------ |
|     StringField(String name, String value, Store stored)     | 字符串                 | N                 | Y                | Y/N             | StringField 用于构建一个字符串 Field，但是不会进行分词，会将整个字符串存储在索引中。 |
| FeatureField(String fieldName, String featureName, float featureValue) |                        |                   |                  |                 |                                                              |
|            StoredField(String name, double value)            | 重载方法，支持多种类型 | N                 | N                | Y               | StoredField 用来构建不同类型 Field，不分词，不索引，但是 Field 要存在文档中 |
|      TextField(String name, String value, Store store)       | 字符串或者流           | Y                 | Y                | Y/N             | 如果是一个 Reader ,Lucene 猜测内容比较多，会采用 Unstored 的策略 |

### 代码实现

```java
public void createIndex() throws IOException {
    // 1.采集数据，从数据库中查询数据
    List<Book> all = bookRepository.findAll();
    // 2.创建 document 对象
    List<Document> documents = new ArrayList<>();
    for (Book item : all) {
        // document 等同于数据库中的一张表
        Document document = new Document();
        // TextField 等同于数据库中表的字段
        // new TextField(第一个参数域名，第二个参数是域值，第三个参数表示是否存储)
        document.add(new TextField("id", String.valueOf(item.getId()), Field.Store.YES));
        document.add(new TextField("name", item.getName(), Field.Store.YES));
        document.add(new TextField("price", String.valueOf(item.getPrice()), Field.Store.YES));
        document.add(new TextField("pic", item.getPic(), Field.Store.YES));
        document.add(new TextField("description", item.getDescription(), Field.Store.YES));

        documents.add(document);
    }
    // 3.创建分词器
    StandardAnalyzer analyzer = new StandardAnalyzer();
    // 4.创建 IndexWriterConfig 并配置分词器
    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    // 5.创建 Directory 对象，声明索引库存储的位置
    // String path = this.getClass().getClassLoader().getResource("").getPath();
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
```

## 实现搜索流程

1. 创建 Query 搜索对象
2. 创建 Directory 流对象，声明索引库的位置
3. 创建索引读取对象 IndexReader
4. 创建索引搜索对象 IndexSearcher
5. 使用索引搜索对象，执行搜索，返回结果集 TopDocs
6. 解析结果集
7. 释放资源

### 代码实现

```java
package top.simba1949.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
}
```

### IndexSearcher 

|                 方法                  |                             说明                             |
| :-----------------------------------: | :----------------------------------------------------------: |
|           search(query, n)            |           根据 query 对象，返回评分最高的 n 条记录           |
|        search(query,filter,n)         |    根据 query 对象，添加过滤策略，返回评分最高的 n 条记录    |
|        search(query, n, sort)         |    根据 query 对象，添加排序策略，返回评分最高的 n 条策略    |
| search(booleanQuery, filter, n, sort) | 根据 booleanQuery 对象，添加过滤策略，添加排序策略，返回评分最高的 n 条记录 |

## 删除搜索的文档

```java
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
```

## 删除所有的索引域和文档域

```java
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
```

## 更新索引和文档域

```java
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
```

## lucene 提供两种查询方式

### commonSearch

```java
/**
 * 通用查询
 * @param query
 * @param num
 * @return
 * @throws IOException
 */
public List<Book> commonSearch(Query query, int num) throws IOException {
    // 1.Query 查询条件传入
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
```

### 使用 lucene 提供 Query 子类

#### TermQuery

> TermQuery 词项查询，TermQuery 不适用分词器，搜索关键字进行精确匹配 Field 域中的词

```java
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
```

#### BooleanQuery

```java
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
```

* MUST 和 MUST 的关系：表示与关系，即交集
* MUST 和 MUST_NOT 的关系：包含前者，不包含后者
* MUST_NOT 和 MUST_NOT 的关系：没有意义
* SHOULD 和 MUST 的关系：MUST，SHOULD没有意义
* SHOULD 和 MUST_NOT 的关系：相当于 MUST 和 MUST_NOT
* SHOULD 和 SHOULD 的关系：表示或关系，即并集

### 使用 QueryParse 解析查询表达式

#### 基础语法

1. 关键字查询：

   

   ```
   语法：域名 + ":" + 搜索关键字
   例如：name:java
   ```

2. 范围查询

   ```
   语法：域名 + ":" + [最小值 TO 最大值]
   例如：size:[1 TO 10]
   ```

3. 组合条件查询

   ```
   BooleanClause.Occur.MUST_NOT 相当于 ！
   BooleanClause.Occur.MUST 相当于 AND
   BooleanClause.Occur.SHOULD 相当于 OR
   ```

#### PhraseQuery

```java
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
```

#### MultiPhraseQuery 

```java
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
```

## TopDocs

lucene 搜索结果可通过 TopDocs 遍历，TopDocs 类提供少量属性

| 方法或者属性 |          说明          |
| :----------: | :--------------------: |
|  totalHits   | 匹配搜索条件的总记录数 |
|  scoreDocs   |      顶部匹配记录      |

## 相关度排序

### 什么是相关度排序

> 相关度排序就是查询结果按照与查询关键字的相关性进行排序，越相关的越靠前

### 相关度打分

Licene 对查询关键字和索引文档的相关度进行打分，得分高的就排在前面。

如何打分呢？ Lucene 是在用户进行检索式实时根据搜索的关键字计算出来的，分两步

1. 计算出词（Term）的权重
2. 根据词的权重值，计算文档相关度得分

### 什么是词的权重

通过索引部分的学习，明确索引的最小单位是一个Term(索引词典中的一个词)。搜索也是从索引域中查询Term，再根据Term找到文档。Term对文档的重要性称为权重，影响Term权重有两个因素：

* Term Frequency (tf)：
  指此Term在此文档中出现了多少次。tf 越大说明越重要。 词(Term)在文档中出现的次数越多，说明此词(Term)对该文档越重要，如“Lucene”这个词，在文档中出现的次数很多，说明该文档主要就是讲Lucene技术的。
* Document Frequency (df)：
  指有多少文档包含此Term。df 越大说明越不重要。比如，在一篇英语文档中，this出现的次数更多，就说明越重要吗？不是的，有越多的文档包含此词(Term), 说明此词(Term)太普通，不足以区分这些文档，因而重要性越低。

### 加权查询

TODO

### 高亮查询

TODO

