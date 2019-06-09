package top.simba1949.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import top.simba1949.common.Product;
import top.simba1949.common.ProductAnnotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 1. 创建 HttpSolrClient 对象，通过它与 Solr 服务器连接
 * 2. 创建 SolrInputDocument 对象，然后通过它来添加域
 * 3. 通过 HttpSolrClient 对象将 SolrInputDocument 添加到索引库中
 * 4. 提交
 *
 * @author SIMBA1949
 * @date 2019/6/9 18:38
 */
public class SolrLearn {

	private HttpSolrClient httpSolrClient;

	@Before
	public void init(){
		String solrUrl = "http://localhost:8080/solr/collection1";
		httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();
	}

	/**
	 * 添加操作
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Test
	public void createIndexTest() throws IOException, SolrServerException {
		// 创建 SolrInputDocument 对象，
		SolrInputDocument solrInputDocument = new SolrInputDocument();
		// 添加域
		solrInputDocument.addField("id", "o168");
		solrInputDocument.addField("content_ik", "五花马，千金裘，呼儿将出换美酒");

		// HttpSolrClient 实现对 Solr 的操作
		httpSolrClient.add(solrInputDocument);
		httpSolrClient.commit();
	}

	/**
	 * 删除索引
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Test
	public void deleteIndexTest() throws IOException, SolrServerException {
		// 根据 id 删除数据
//		httpSolrClient.deleteById("o168");
		// 根据条件删除数据
//		httpSolrClient.deleteByQuery("product_name:幸福");
		// 删除所有
		httpSolrClient.deleteByQuery("*:*");

		httpSolrClient.commit();
	}

	/**
	 * 简单查询
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Test
	public void simpleSearchTest() throws IOException, SolrServerException {
		SolrQuery solrQuery = new SolrQuery("*:*");
		QueryResponse queryResponse = httpSolrClient.query(solrQuery);
		parseSolrQueryResponse(queryResponse);
	}

	@Test
	public void simpleSearchAnnotationTest() throws IOException, SolrServerException {
		SolrQuery solrQuery = new SolrQuery("*:*");
		QueryResponse queryResponse = httpSolrClient.query(solrQuery);
		List<ProductAnnotation> list = queryResponse.getBeans(ProductAnnotation.class);
	}

	private void parseSolrQueryResponse(QueryResponse queryResponse){
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
	}

	@Test
	public void pageAndSortSearchTest() throws IOException, SolrServerException {
		SolrQuery solrQuery = new SolrQuery("product_keywords:音乐");
		// 设置分页，从第一条开始查询，查询多少条
		solrQuery.setStart(1);
		solrQuery.setRows(10);
		// 设置排序
		solrQuery.setSort("id", SolrQuery.ORDER.desc);
		QueryResponse queryResponse = httpSolrClient.query(solrQuery);
		List<ProductAnnotation> beans = queryResponse.getBeans(ProductAnnotation.class);
	}

	@Test
	public void filterSearchTest() throws IOException, SolrServerException {
		SolrQuery solrQuery = new SolrQuery("product_keywords:音乐");
		solrQuery = solrQuery.setFilterQueries("product_price:[10 TO 20]");

		QueryResponse queryResponse = httpSolrClient.query(solrQuery);
		List<ProductAnnotation> beans = queryResponse.getBeans(ProductAnnotation.class);
	}

	@Test
	public void defaultFieldSearchTest() throws IOException, SolrServerException {
		SolrQuery solrQuery = new SolrQuery("product_keywords:音乐");
		// 设置默认搜索域
		solrQuery.set("df", "product_name");

		QueryResponse queryResponse = httpSolrClient.query(solrQuery);
		List<ProductAnnotation> beans = queryResponse.getBeans(ProductAnnotation.class);
	}

	@Test
	public void highLightSearchTest() throws IOException, SolrServerException {
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
			products.add(product);
		}
	}
}

