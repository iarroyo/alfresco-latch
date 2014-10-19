/**
 * 
 */
package org.alfresco.latch.sdk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.alfresco.latch.exception.LatchException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.extensions.surf.util.URLEncoder;

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
			SSLContextBuilder builder = new SSLContextBuilder();
			
			//accept SSLcertificates without HostName validation
			builder.loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates,
						String s) throws CertificateException {
					return true;
				}
			});
			
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
		            sslsf).build();
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
					ContentType contentType = ContentType.getOrDefault(entity);
					Charset charset = contentType.getCharset();
					Reader reader = new InputStreamReader(entity.getContent(),
							charset);
					return gson.fromJson(reader, JsonElement.class);
				}
			};

			responseJSON = httpclient.execute(httpget, rh);
			
		} catch (ClientProtocolException e) {
			throw new LatchException(e.getMessage(), e);
		} catch (IOException e) {
			throw new LatchException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new LatchException(e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new LatchException(e.getMessage(), e);
		} catch (KeyManagementException e) {
			throw new LatchException(e.getMessage(), e);
		}

		return responseJSON;
	}

}
