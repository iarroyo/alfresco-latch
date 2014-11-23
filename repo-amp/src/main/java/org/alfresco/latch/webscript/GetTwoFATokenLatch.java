package org.alfresco.latch.webscript;

import java.io.IOException;
import java.util.Map;

import org.alfresco.latch.service.LatchService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class GetTwoFATokenLatch extends AbstractWebScript {

	
	private LatchService latchService;

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
		
		JSONObject response = new JSONObject();
		Boolean success=Boolean.FALSE;

		try {
			
			Map<String,String> tempVars=req.getServiceMatch().getTemplateVars();
			String userName=tempVars.get("username");
			
			if(StringUtils.isNotBlank(userName)){
				success=latchService.twoFACacheHasUserToken(userName);
			}
			
			response.put("success", success);
			
		} catch (JSONException e) {
			//TODO logger
		}
		
		res.getWriter().write(response.toString());

	}

	/**
	 * @param latchService the latchService to set
	 */
	public void setLatchService(LatchService latchService) {
		this.latchService = latchService;
	}

}
