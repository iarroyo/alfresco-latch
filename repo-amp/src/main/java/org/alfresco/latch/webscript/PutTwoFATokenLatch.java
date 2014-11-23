package org.alfresco.latch.webscript;

import java.io.IOException;

import org.alfresco.latch.service.LatchService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class PutTwoFATokenLatch extends AbstractWebScript {

	
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
		String message=null;

		try {
			
			Content c = req.getContent();
			if (c == null) {
				throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Missing POST body.");
			}
			
			JSONObject json;
			json = new JSONObject(c.getContent());
			
			//TODO set as constants
			String userName= json.has("username")?json.getString("username"):null;
			String token= json.has("token")?json.getString("token"):null;
			
			if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(token)){
				//logger
				message="Invalid mandatory parameters username: +" +userName+ ", token: " +token;
			}else{
				
				if(latchService.twoFACacheHasUserToken(userName)){
					latchService.updateTemporalUserToken(userName, token);
					success=Boolean.TRUE;
				}else{
					message="Latch token not found";
				}
				
			}
			
			response.put("success", success);
			if(StringUtils.isNotBlank(message)){
				response.put("message",message);
			}
			
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
