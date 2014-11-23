/**
 * 
 */
package org.alfresco.repo.web.scripts.bean;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.latch.config.TwoFAConfig;
import org.alfresco.latch.exception.LatchException;
import org.alfresco.latch.service.LatchService;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.security.authentication.AuthenticationDisallowedException;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.WebScriptException;

import com.elevenpaths.latch.Error;
import com.elevenpaths.latch.LatchResponse;
import com.google.gson.JsonObject;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class CustomLoginPost extends LoginPost{
	
    private AuthenticationService authenticationService;
    private LatchService latchService;
    
    private static Log logger = LogFactory.getLog(CustomLoginPost.class);
    
    /**
     * @param authenticationService
     */
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    @Override
	protected Map<String, Object> login(String username, String password) {
        try
        {
        	// get ticket
        	authenticationService.authenticate(username, password.toCharArray());

        	logger.info("LogginPost process");
        	
        	if (latchService.getLatchConfig().isAvailable() &&  latchService.isValidRequest(username)) {
        		processLatch(username);
        	}
        	
            // add ticket to model for javascript and template access
            Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
            model.put("username", username);
            model.put("ticket",  authenticationService.getCurrentTicket());
            
            return model;
        }
        catch(LatchException e)
        {
            throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Second Factor Authentication enabled");
        }
        catch(AuthenticationException e)
        {
        	latchService.removeTwoFAConfig(username);
            throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, "Login failed");
        }catch (Exception e) {
        	//remove token to unknown exception
        	latchService.removeTwoFAConfig(username);
        	throw new WebScriptException(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
        finally
        {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
	}
	
	/**
	 * @param username
	 */
	private void processLatch(String userName) {
		
		String accountID = latchService.getAccountID(userName);
	
		// has the user the latch "accountID"
		if (StringUtils.isNotEmpty(accountID)) {
			
			// latch status
			LatchResponse latchResponse = latchService.getLatchStatus(accountID);
	
			// process response
			processReponse(latchResponse, userName);
	
		}
		
	}

	/**
	 * Process the latch response checking the second authentication factor.
	 * 
	 * @param latchReponse
	 * @param userName
	 */
	private void processReponse(LatchResponse latchResponse, String userName) {

		JsonObject data = latchResponse.getData();
		Error error = latchResponse.getError();
		
		// Not block user account if any error happen in the latch server
		if(data!=null || error!=null){
			// Error 201: Account not paired
			// https://latch.elevenpaths.com/www/developers/doc_api
			//102 = Invalid application signature  >> Bad secret key or application id or latch app removed
			if(error!=null && (error.getCode()==HttpStatus.SC_CREATED || error.getCode()==HttpStatus.SC_PROCESSING)){
				//FIXME i would like remove the paired account in case the latch app is deleted.
				//I cannot do that because the webscript(login.post.desc.xml) call to the authenticate method 
				// with the transaction in read only mode.
				
				// thinking about that.
				// latchService.externallyUnpairedAccount(userName);
			}else{
			
				//latch status ON
				if(data!=null && latchService.isLatchON(latchResponse)){
					
					JsonObject twoFA=latchService.isTwoFAEnabled(latchResponse);
					if(twoFA!=null){
						String token=twoFA.get("token").getAsString();
						latchService.updateLatchUserToken(userName, token);
						throw new LatchException("Invalid authentication. Second Authentication Factor is enabled");
					}
					
				}else{
					//AuthenticationCredentialsNotFoundException
					throw new AuthenticationDisallowedException("Latch has blocked the authentication");
				}
			}
		}

	}

	public void setLatchService(LatchService latchService){
		this.latchService=latchService;
	}

}
