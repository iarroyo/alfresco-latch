/**
 * 
 */
package org.alfresco.latch.webscript;

import java.io.IOException;
import java.util.Map;

import org.alfresco.latch.service.LatchService;
import org.alfresco.repo.security.authentication.TicketComponent;
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
public class LatchTicketAuthentication extends AbstractWebScript{

	private LatchService latchService;
	private TicketComponent ticketComponent;
	
	/* (non-Javadoc)
	 * @see org.springframework.extensions.webscripts.WebScript#execute(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res)
			throws IOException {

		Map<String,String> params= req.getServiceMatch().getTemplateVars();
		String userName= params.get("username");
		JSONObject ticketJSON = new JSONObject();
		
		if (StringUtils.isNotBlank(userName) && latchService.twoFACacheHasTemporalToken(userName)) {

			try {
				if (latchService.checkTwoFATokens(userName)) {
					//authenticate
					String ticket= ticketComponent.getNewTicket(userName);
					ticketJSON.put("ticket", ticket);
				}
			} catch (JSONException e) {
				throw new WebScriptException(e.getMessage(),e);
			} finally {
				latchService.removeTwoFAConfig(userName);
			}

		}
		
		res.getWriter().write(ticketJSON.toString());
		
	}
	
	public void setLatchService(LatchService latchService){
		this.latchService=latchService;
	}
	
	public void setTicketComponent(TicketComponent ticketComponent){
		this.ticketComponent=ticketComponent;
	}

}
