package org.alfresco.latch.webscript;


import org.alfresco.latch.config.LatchConfig;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
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
public class SaveSettingsLatch extends AbstractSettingsLatch {

	private LatchConfig latchConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.latch.webscript.AbstractSettingsLatch#executeImpl(org.
	 * springframework.extensions.webscripts.WebScriptRequest,
	 * org.springframework.extensions.webscripts.WebScriptResponse)
	 */
	@Override
	public void executeImpl(WebScriptRequest req, WebScriptResponse res)
			throws Exception {

		JSONObject response = new JSONObject();

		// Extract old and new password details from JSON POST
		Content c = req.getContent();
		if (c == null) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR,
					"Missing POST body.");
		}
		JSONObject json;
		json = new JSONObject(c.getContent());

		Boolean configured = Boolean.TRUE;
		Boolean success = Boolean.FALSE;

		if (!isConfigured(json)) {
			configured = Boolean.FALSE;
			response.put(MESSAGE, "latch.parameters.mandatory");
		} else {
			if (!attributeService.exists(LATCH, SETTINGS)) {
				// Add settings
				attributeService.createAttribute(json.toString(), LATCH,
						SETTINGS);
			}else{
				attributeService.setAttribute(json.toString(), LATCH, SETTINGS);
			}
			// reload latch configuration
			latchConfig.init();
			success = Boolean.TRUE;
		}

		response.put(CONFIGURED, configured);
		response.put(SUCCESS, success);

		res.getWriter().write(response.toString());

	}

	public void setLatchConfig(LatchConfig latchConfig) {
		this.latchConfig = latchConfig;
	}

}
