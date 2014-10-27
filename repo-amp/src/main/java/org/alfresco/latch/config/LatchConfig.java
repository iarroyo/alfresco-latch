/**
 * 
 */
package org.alfresco.latch.config;

import org.alfresco.service.cmr.attributes.AttributeService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.Resource;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class LatchConfig {

	protected static final String LATCH = "latch";
	protected static final String SETTINGS = "settings";
	protected static final String PARAM_APPID = "appID";
	protected static final String PARAM_SECRET = "secret";
	protected static final String PARAM_ENABLED = "enabled";

	private Boolean enable=Boolean.FALSE;
	private String appID;
	private String secret;
	private Resource keystore;
	private String keyStorePass;

	private AttributeService attributeService;

	/**
	 * @return the isAvailable
	 */
	public Boolean isAvailable() {
		Boolean available = this.enable;
		available = available && !StringUtils.isEmpty(appID);
		available = available && !StringUtils.isEmpty(secret);
		return available;
	}

	public void init() throws JSONException {

		if (attributeService.exists(LATCH, SETTINGS)) {
			JSONObject settings = new JSONObject((String)attributeService.getAttribute(
					LATCH, SETTINGS));
			this.enable = settings.getBoolean(PARAM_ENABLED);
			this.appID = settings.getString(PARAM_APPID);
			this.secret = settings.getString(PARAM_SECRET);
		}
	}

	/**
	 * @return the enable
	 */
	public Boolean getEnable() {
		return enable;
	}

	/**
	 * @param enable
	 *            the enable to set
	 */
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	/**
	 * @return the appID
	 */
	public String getAppID() {
		return appID;
	}

	/**
	 * @param appID
	 *            the appID to set
	 */
	public void setAppID(String appID) {
		this.appID = appID;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret
	 *            the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public Resource getKesyStore(){
		return this.keystore;
	}
	
	public void setKeyStore(Resource resource){
		this.keystore=resource;
	}
	
	public String getKeyStorePass(){
		return this.keyStorePass;
	}
	
	public void setkeyStorePass(String password){
		this.keyStorePass=password;
	}

	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	

}
