/**
 * 
 */
package org.alfresco.latch.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.springframework.core.io.Resource;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class HttpClientFactory {

	public static HttpClient build(Resource resourcePath, String password) {

		DefaultHttpClient httpClient = null;

		if (resourcePath != null && StringUtils.isNotEmpty(password)) {
			try {

				SSLContext sslContext = SSLContext.getInstance("TLS");
				KeyStore keyStore = KeyStoreUtil.loadKeyStore(
						resourcePath.getInputStream(), password.toCharArray());

				sslContext.init(null,
						KeyStoreUtil.getTrustManagerInstance(keyStore)
								.getTrustManagers(), null);

				SSLSocketFactory sf = new SSLSocketFactory(sslContext);
				Scheme httpsScheme = new Scheme("https", 443, sf);
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(httpsScheme);
				httpClient = new DefaultHttpClient(
						new ThreadSafeClientConnManager(schemeRegistry));

			} catch (KeyManagementException e) {
				//avoid block any service if any error happen
			} catch (KeyStoreException e) {
				//avoid block any service if any error happen
			} catch (NoSuchAlgorithmException e) {
				//avoid block any service if any error happen
			} catch (CertificateException e) {
				//avoid block any service if any error happen
			} catch (IOException e) {
				//avoid block any service if any error happen
			}
		} else {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

}
