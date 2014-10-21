/**
 * 
 */
package org.alfresco.latch.sdk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.alfresco.latch.exception.LatchException;
import org.alfresco.latch.ssl.EasySSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.elevenpaths.latch.Latch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class LatchSDK extends Latch {

	/**
	 * @param appId
	 * @param secretKey
	 */
	public LatchSDK(String appId, String secretKey) {
		super(appId, secretKey);
	}

	@Override
	public JsonElement HTTP_GET(String URL, Map<String, String> headers) {

		JsonElement responseJSON = null;
		try {

			SchemeRegistry schemeRegistry = new SchemeRegistry();
	        schemeRegistry.register(new Scheme("https",443,new EasySSLSocketFactory()));

	        DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(schemeRegistry));
			HttpGet httpget = new HttpGet(URL);

			// Add headers
			for (String key : headers.keySet()) {
				httpget.addHeader(key, headers.get(key));
			}

			ResponseHandler<JsonElement> rh = new ResponseHandler<JsonElement>() {

				@Override
				public JsonElement handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {

					StatusLine statusLine = response.getStatusLine();
					HttpEntity entity = response.getEntity();
					if (statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
						throw new HttpResponseException(
								statusLine.getStatusCode(),
								statusLine.getReasonPhrase());
					}
					if (entity == null) {
						throw new ClientProtocolException(
								"Response contains no content");
					}
					Gson gson = new GsonBuilder().create();
					Reader reader = new InputStreamReader(entity.getContent());
					return gson.fromJson(reader, JsonElement.class);
				}
			};

			responseJSON = httpClient.execute(httpget, rh);

		} catch (ClientProtocolException e) {
			throw new LatchException(e.getMessage(), e);
		} catch (IOException e) {
			throw new LatchException(e.getMessage(), e);
		}

		return responseJSON;
	}


}
