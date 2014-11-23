/**
 * 
 */
package org.alfresco.latch.config;

import java.util.Date;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 * 
 * Object representing the Second Factor Authentication configuration.
 *
 */
public class TwoFAConfig {
	
	public enum REFERER_REQ{
		NONE,
		SHARE
	}
	
	private String token;
	private String temporalToken;
	private Date generated;
	private REFERER_REQ refererReq=REFERER_REQ.NONE;
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the temporalToken
	 */
	public String getTemporalToken() {
		return temporalToken;
	}
	/**
	 * @param temporalToken the temporalToken to set
	 */
	public void setTemporalToken(String temporalToken) {
		this.temporalToken = temporalToken;
	}
	/**
	 * @return the generated
	 */
	public Date getGenerated() {
		return generated;
	}
	/**
	 * @param generated the generated to set
	 */
	public void setGenerated(Date generated) {
		this.generated = generated;
	}
	/**
	 * @return the refererReq
	 */
	public REFERER_REQ getRefererReq() {
		return refererReq;
	}
	/**
	 * @param refererReq the refererReq to set
	 */
	public void setRefererReq(REFERER_REQ refererReq) {
		this.refererReq = refererReq;
	}
	

}
