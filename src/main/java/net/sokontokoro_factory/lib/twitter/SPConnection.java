package net.sokontokoro_factory.lib.twitter;

import java.net.URI;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SPConnection {
	
	private String endpoint;
	private String requestHeader;
	private String body;
	
	public SPConnection(String endpoint, String requestHeader, String body){
		this.endpoint = endpoint;
		this.requestHeader = requestHeader;
		this.body = body;
	};
	
	public String get(){
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String responseBody = "";

		try {

			// Http request
			httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(new URI(endpoint));
			httpGet.setHeader("Authorization", requestHeader);
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
			HttpPost postMethod = new HttpPost(new URI(endpoint));

			postMethod.setHeader("Authorization", requestHeader);
			response = httpClient.execute(postMethod);

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
