/**
 * 
 */
package org.alfresco.latch.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManagerFactory;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class KeyStoreUtil {

	
	public static TrustManagerFactory getTrustManagerInstance(KeyStore ks) throws KeyStoreException, NoSuchAlgorithmException{
		return getTrustManagerInstance(ks, null);
	}
	public static TrustManagerFactory getTrustManagerInstance(KeyStore ks,String algotihm)
			throws KeyStoreException, NoSuchAlgorithmException {
		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(algotihm!=null?algotihm:TrustManagerFactory.getDefaultAlgorithm());
		
		trustManagerFactory.init(ks);
		return trustManagerFactory;
	}
	
	public static KeyStore loadKeyStore(InputStream inputStream, char[] password) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException{
		
		KeyStore keyStore=KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(inputStream, password);
		
		return keyStore;
	}

}
