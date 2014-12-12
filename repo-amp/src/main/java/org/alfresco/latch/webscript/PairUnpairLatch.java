package org.alfresco.latch.webscript;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

import org.alfresco.latch.exception.LatchException;
import org.alfresco.latch.service.LatchService;
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
public class PairUnpairLatch extends AbstractWebScript {

	enum LATCH {
		PAIR, UNPAIR
	}

	private static final String OPERATION = "operation";
	private static final String SUCCESS="success";
	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_TOKEN = "token";
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
		
		JSONObject response= new JSONObject();

		try {

			Map<String, String> templateVars = req.getServiceMatch()
					.getTemplateVars();
			
			
			// Extract old and new password details from JSON POST
	        Content c = req.getContent();
	        if (c == null)
	        {
	            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Missing POST body.");
	        }
	        JSONObject json;
	        json = new JSONObject(c.getContent());

			String operation = templateVars.get(OPERATION);
			String userName = json.getString(PARAM_USERNAME);
			String token = json.getString(PARAM_TOKEN);

			if (operation == null || userName == null || token == null) {

				throw new InvalidParameterException(String.format(
						"operation: %s, userName: %s, token: %s", operation,
						userName, token));
			}

			switch (LATCH.valueOf(operation.toUpperCase())) {
			case PAIR:
				latchService.pairAccount(userName, token);
				break;

			case UNPAIR:
				latchService.unpairAccount(userName);
				break;

			default:
				throw new WebScriptException("Unknown operation " + operation);
			}
			
			response.put(OPERATION, operation);
			response.put(SUCCESS, Boolean.TRUE);
			
			res.getWriter().write(response.toString());

		} catch (LatchException latchException){
			throw new WebScriptException(latchException.getMessage());
		} catch (Exception genericException) {
			throw new WebScriptException(genericException.getMessage(), genericException);
		}

	}

	public void setLatchService(LatchService latchService) {
		this.latchService = latchService;
	}

}
