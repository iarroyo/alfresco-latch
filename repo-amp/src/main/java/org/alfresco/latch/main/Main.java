/**
 * 
 */
package org.alfresco.latch.main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

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
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String URL = "https://latch.elevenpaths.com/api/0.6/pair/xxx";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization",
				"11PATHS JrxVscFjWgPZLzeWyUdq 1hnotIa5i5IDQ2UuLVDeaFXJqSE=");
		headers.put("X-11paths-Date", "2014-10-13 20:59:51");

		JsonElement responseJSON = null;
		try {

			SSLContextBuilder builder = new SSLContextBuilder();
//			builder.useTLS();
			builder.loadTrustMaterial(null, new TrustStrategy() {

				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates,
						String s) throws CertificateException {
					return true; // =)
				}
			});
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					builder.build(),
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			CloseableHttpClient httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf).build();
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
			
			System.out.println(responseJSON);
		} catch (ClientProtocolException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		} catch (KeyStoreException e) {
			System.out.println(e);
		} catch (KeyManagementException e) {
			System.out.println(e);
		}

	}

}
