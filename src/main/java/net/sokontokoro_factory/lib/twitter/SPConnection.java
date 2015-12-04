package net.sokontokoro_factory.lib.twitter;

import java.net.URI;
import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SPConnection {
	
	private String endpoint;
	private HashMap<String, String> requestHeaderMap;
	private String body;
	
	public SPConnection(String endpoint, HashMap<String, String> requestHeaderMap, String body){
		this.endpoint = endpoint;
		this.requestHeaderMap = requestHeaderMap;
		this.body = body;
	};
	
	public String get(){
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String responseBody = "";

		try {
			httpClient = HttpClients.createDefault();
			// URL
			HttpGet httpGet = new HttpGet(new URI(endpoint));
			// リクエストヘッダ
			for(String key: requestHeaderMap.keySet()){
				httpGet.setHeader(key, requestHeaderMap.get(key));				
			}
			// 実行
			response = httpClient.execute(httpGet);

			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return responseBody;
	}
	public String post(){
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String responseBody = "";

		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(new URI(endpoint));

			// リクエストヘッダ
			for(String key: requestHeaderMap.keySet()){
				httpPost.setHeader(key, requestHeaderMap.get(key));				
			}
			response = httpClient.execute(httpPost);

			// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			// {
			responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// response.close();
			// httpClient.close();
		}
		return responseBody;
	}

}
