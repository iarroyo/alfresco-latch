/**
 * 
 */
package org.alfresco.latch.sdk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.latch.exception.LatchException;
import org.alfresco.latch.util.HttpClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

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

	private HttpClient httpClient;
	
	public void setHttpClient(HttpClient httpClient){
		this.httpClient=httpClient;
	}

	/**
	 * @param appId
	 * @param secretKey
	 */
	public LatchSDK(LatchConfig latchConfig) {
		super(latchConfig.getAppID(), latchConfig.getSecret());
		setHttpClient(HttpClientFactory.build(latchConfig.getKesyStore(), latchConfig.getKeyStorePass()));
	}

	@Override
	public JsonElement HTTP_GET(String URL, Map<String, String> headers) {

		JsonElement responseJSON = null;
		try {

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
