/**
 * 
 */
package org.alfresco.latch.webscript;

import java.io.IOException;

import javax.mail.MethodNotSupportedException;

import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class AbstractSettingsLatch extends AbstractWebScript {

	protected static final String SUCCESS="success";
	protected static final String PARAM_APPID = "appID";
	protected static final String PARAM_SECRET = "secret";
	protected static final String PARAM_ENABLED = "enabled";
	protected static final String LATCH="latch";
	protected static final String SETTINGS="settings";
	protected static final String CONFIGURED="configured";
	protected static final String MESSAGE="message";

	protected AttributeService attributeService;
	protected AuthorityService authorityService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.extensions.webscripts.WebScript#execute(org.
	 * springframework.extensions.webscripts.WebScriptRequest,
	 * org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {
		try {
			executeImpl(req, res);
		} catch (Exception genericException) {
			throw new WebScriptException(genericException.getMessage(),
					genericException);
		}
	}

	public void executeImpl(WebScriptRequest req, WebScriptResponse res)
			throws Exception {
		throw new MethodNotSupportedException("This method should be implemented");
	}

	/**
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	protected boolean isConfigured(JSONObject json) throws JSONException {
		
		Boolean enabled=json.has(PARAM_ENABLED)?json.getBoolean(PARAM_ENABLED):null;
		String appID= json.has(PARAM_APPID)?json.getString(PARAM_APPID):null;
		String secret= json.has(PARAM_SECRET)?json.getString(PARAM_SECRET):null;
		
		return !(enabled==null || StringUtils.isEmpty(appID) || StringUtils.isEmpty(secret));
	}
	
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
	
	public void setAuthorityService(AuthorityService authorityService){
		this.authorityService=authorityService;
	}

}
