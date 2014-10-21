/**
 * 
 */
package org.alfresco.latch.ssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 * 
 * This class is deprecated to HttpClient 4.3.x
 * We are using it because does it compatible between 
 * alfresco 4.2.x (use httpClient 4.1.x) and alfresco 5.x (use httpClient 4.3.3)
 *
 */
public class EasySSLSocketFactory implements SchemeSocketFactory,
		LayeredSchemeSocketFactory {

	private SSLContext sslcontext = null;

	private static SSLContext createEasySSLContext() throws IOException {
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			X509TrustManager trustManager = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {

				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			context.init(null, new TrustManager[] { trustManager }, null);
			return context;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.http.conn.scheme.SchemeSocketFactory#connectSocket(java.net
	 * .Socket, java.net.InetSocketAddress, java.net.InetSocketAddress,
	 * org.apache.http.params.HttpParams)
	 */
	@Override
	public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress,
			InetSocketAddress localAddress, HttpParams params)
			throws IOException, UnknownHostException, ConnectTimeoutException {

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);
		SSLSocket sslsock = (SSLSocket) ((socket != null) ? socket
				: createSocket(params));
		if (localAddress != null) {
			// we need to bind explicitly
			sslsock.bind(localAddress);
		}

		sslsock.connect(remoteAddress, connTimeout);
		sslsock.setSoTimeout(soTimeout);
		return sslsock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.http.conn.scheme.SchemeSocketFactory#createSocket(org.apache
	 * .http.params.HttpParams)
	 */
	@Override
	public Socket createSocket(HttpParams httpParams) throws IOException {
		if (this.sslcontext == null) {
			this.sslcontext = createEasySSLContext();
		}
		return this.sslcontext.getSocketFactory().createSocket();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.http.conn.scheme.SchemeSocketFactory#isSecure(java.net.Socket)
	 */
	@Override
	public boolean isSecure(Socket arg0) throws IllegalArgumentException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.http.conn.scheme.LayeredSchemeSocketFactory#createLayeredSocket
	 * (java.net.Socket, java.lang.String, int, boolean)
	 */
	@Override
	public Socket createLayeredSocket(final Socket socket, final String host,
			final int port, final boolean autoClose) throws IOException,
			UnknownHostException {
		
		if (this.sslcontext == null) {
			this.sslcontext = createEasySSLContext();
		}
		
		return this.sslcontext.getSocketFactory().createSocket(
				socket, host, port, autoClose);
	}

}
