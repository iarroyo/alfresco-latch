/**
 * 
 */
package org.alfresco.latch.webscript;

import java.io.IOException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class GetSettingsLatch extends AbstractSettingsLatch {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.latch.webscript.AbstractSettingsLatch#executeImpl(org.
	 * springframework.extensions.webscripts.WebScriptRequest,
	 * org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void executeImpl(WebScriptRequest req, WebScriptResponse res)
			throws IOException, JSONException {

		JSONObject response = new JSONObject();
		JSONObject settings=new JSONObject();
		
		if (attributeService.exists(LATCH, SETTINGS)) {
			settings = new JSONObject((String)attributeService.getAttribute(LATCH,
					SETTINGS));
			if(authorityService.isAdminAuthority(AuthenticationUtil.getFullyAuthenticatedUser())){
				response=settings;
			}else{
				response.put(PARAM_ENABLED, settings.has(PARAM_ENABLED)?settings.getBoolean(PARAM_ENABLED):Boolean.FALSE);
			}
		}else{
			response.put(PARAM_ENABLED, Boolean.FALSE);
		}
		
		response.put(CONFIGURED,isConfigured(settings));

		res.getWriter().write(response.toString());

	}

}
