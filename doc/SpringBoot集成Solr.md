# SpringBoot集成Solr

## 添加文档、索引操作

步骤：

1. 创建 HttpSolrClient 对象，通过它与 Solr 服务器连接
2. 创建 SolrInputDocument 对象，然后通过它来添加域
3. 通过 HttpSolrClient 对象将 SolrInputDocument 添加到索引库中
4. 提交

```java
String solrUrl = "http://localhost:8080/solr/collection1";
HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();
// 创建 SolrInputDocument 对象，
SolrInputDocument solrInputDocument = new SolrInputDocument();
// 添加文档、索引，如果 id 存在则先删除在添加实现修改
solrInputDocument.addField("id", "o168");
solrInputDocument.addField("content", "五花马，千金裘，呼儿将出换美酒");

// HttpSolrClient 实现对 Solr 的操作
httpSolrClient.add(solrInputDocument);
httpSolrClient.commit();
```

## 删除操作

```java
String solrUrl = "http://localhost:8080/solr/collection1";
HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();

// 根据 id 删除数据
// httpSolrClient.deleteById("o168");
// 根据条件删除数据， content 是文档中的域的名字
// httpSolrClient.deleteByQuery("content", "五花马，千金裘，呼儿将出换美酒");
// 删除所有
httpSolrClient.deleteByQuery("*:*");

httpSolrClient.commit();
```

## 查询

### 查询语法

FieldName 表示 Field 的名称，keyWords 表示要查询的关键字

1. 简单查询（q）：

   ```
   FieldName:keyWords
   ```

2. 过滤查询（fq）：

   ```
   # start end ， * 表示无限
   # FieldName:[start TO end] 
   # 可以使用 AND 连接
   # FieldName:[start0 TO end0] AND FieldName:[start1 TO end1] 
   # FieldName:[* TO end]
   # FieldName:[start TO *]
   
   solrQuery = solrQuery.setFilterQueries("product_price:[10 TO 20]");
   ```

3. 排序查询

   ```
   # desc asc
   solrQuery.setSort("id", SolrQuery.ORDER.desc);
   ```

4. 分页查询

   ```
   # start 开始位置 rows 偏移量
   solrQuery.setStart(1);
   solrQuery.setRows(10);
   ```

5. 指定返回字段(fl)

   ```
   # 多个可用逗号或者空格分隔
   product_name,product_price
   ```

6. 设置默认搜索域(df)

   ```
   // 设置默认搜索域
   solrQuery.set("df", "product_name");
   ```

7. 指定格式输出(wt)

   ```
   
   ```

8. 是否高亮(hl)

   ```
   
   ```

### 简单查询

```java
String solrUrl = "http://localhost:8080/solr/collection1";
HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();

SolrQuery solrQuery = new SolrQuery("*:*");
QueryResponse queryResponse = httpSolrClient.query(solrQuery);

SolrDocumentList results = queryResponse.getResults();
List<Product> products = new ArrayList<>();
results.forEach(item->{
    Product product = new Product();
    product.setCatalog_name((String) item.get("product_catalog_name"));
    product.setPrice((Double) item.get("product_price"));
    product.setName((String) item.get("product_name"));
    product.setPid(Integer.valueOf(String.valueOf(item.get("id"))));
    product.setPicture((String) item.get("product_picture"));
    System.out.println(product.toString());
    products.add(product);
});
```

### 简单查询，使用 Solr 注解

ProductAnnotation

```java
package top.simba1949.common;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/6/9 19:03
 */
@Data
public class ProductAnnotation {
    /**
	 * @Field(value = "") 中 value 要和文档中域名对应，而且JavaBean类型要和文档中域的类型一一对应
	 */
    @Field(value = "id")
    private String pid;
    @Field(value = "product_name")
    private String name;
    private Integer catalog;
    @Field(value = "product_catalog_name")
    private String catalog_name;
    @Field(value = "product_price")
    private Double price;
    private Integer number;
    @Field(value = "product_description")
    private String description;
    @Field(value = "product_picture")
    private String picture;
    private Date release_time;

    @Override
    public String toString() {
        return "Product{" +
            "pid=" + pid +
            ", name='" + name + '\'' +
            ", catalog=" + catalog +
            ", catalog_name='" + catalog_name + '\'' +
            ", price=" + price +
            ", number=" + number +
            ", description='" + description + '\'' +
            ", picture='" + picture + '\'' +
            ", release_time=" + release_time +
            '}';
    }
}
```

直接使用 queryResponse.getBeans(ProductAnnotation.class) 获取返回结果

```java
String solrUrl = "http://localhost:8080/solr/collection1";
HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();

SolrQuery solrQuery = new SolrQuery("*:*");
QueryResponse queryResponse = httpSolrClient.query(solrQuery);
List<ProductAnnotation> list = queryResponse.getBeans(ProductAnnotation.class);
```

### 分页 & 排序查询

```java
String solrUrl = "http://localhost:8080/solr/collection1";
HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();

SolrQuery solrQuery = new SolrQuery("product_keywords:音乐");
// 设置分页，从第一条开始查询，查询多少条
solrQuery.setStart(1);
solrQuery.setRows(10);
// 设置排序
solrQuery.setSort("id", SolrQuery.ORDER.desc);
QueryResponse queryResponse = httpSolrClient.query(solrQuery);
List<ProductAnnotation> beans = queryResponse.getBeans(ProductAnnotation.class);
```

### 高亮查询

```java
String solrUrl = "http://localhost:8080/solr/collection1";
HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();

SolrQuery solrQuery = new SolrQuery("product_keywords:音乐");
// 开启高亮
solrQuery.setHighlight(true);
// 设置高亮字段 hl.fl 表示要高亮的字段，后面可以跟多个需要高亮的域名
solrQuery.setParam("hl.fl", "product_name");
// 设置高亮样式
solrQuery.setHighlightSimplePre("<e style=\"color:red;\">");
solrQuery.setHighlightSimplePost("</e>");

QueryResponse queryResponse = httpSolrClient.query(solrQuery);
// 获取查询结果
SolrDocumentList list= queryResponse.getResults();
// 获取高亮
Map<String, Map<String, List<String>>>  map = queryResponse.getHighlighting();
List<Product> products = new ArrayList<>();
for (SolrDocument doc : list) {
    Integer id = Integer.valueOf(doc.get("id").toString());
    Product product = new Product();
    product.setPid(id);
    if (null != map){
        // 获取高亮信息
        String productName = map.get(id.toString()).toString();
        product.setName(productName);
    }
    // 其余属性略
    products.add(product);
}
```











