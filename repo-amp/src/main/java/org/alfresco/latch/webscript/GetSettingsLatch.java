/**
 * 
 */
package org.alfresco.latch.webscript;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

import org.alfresco.latch.service.LatchService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.antlr.grammar.v3.ANTLRParser.attrScope_return;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.elevenpaths.latch.LatchErrorException;
import com.google.gson.JsonObject;

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
		JSONObject settings=null;
		
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
