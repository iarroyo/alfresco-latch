package org.alfresco.latch.webscript;

import java.io.IOException;

import org.alfresco.latch.service.LatchService;
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
public class PutRefererRequestLatch extends AbstractWebScript {

	
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
		
		try {
			
			Content c = req.getContent();
			if (c == null) {
				throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,"Missing POST body.");
			}
			
			JSONObject json;
			json = new JSONObject(c.getContent());
			
			//TODO set as constants
			if(json.has("username") && json.has("referer")){
				String userName= json.getString("username");
				String referer=json.getString("referer");
			
				latchService.putRefererRequest(userName,referer);
			}
			
		} catch (JSONException e) {
			throw new WebScriptException(e.getMessage(),e);
		}
		
	}

	/**
	 * @param latchService the latchService to set
	 */
	public void setLatchService(LatchService latchService) {
		this.latchService = latchService;
	}

}
