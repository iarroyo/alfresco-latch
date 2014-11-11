/**
 * 
 */
package org.alfresco.web.site.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.web.site.SlingshotUserFactory;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.uri.UriUtils;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class LatchSlingshotLoginController extends SlingshotLoginController{
	

	private ConnectorService connectorService;
	private RemoteClient remoteClient;
	private UserFactory userFactory;
	
	private static final String LATCH_2FA_TOKEN_WEBSCRIPT="/api/latch/twofa/";
	private static final String LATCH_2FA_TEMPORAL_TOKEN_WEBSCRIPT=LATCH_2FA_TOKEN_WEBSCRIPT+"temptoken";
	private static final String LATCH_USER_TICKET_WEBSCRIPT="/api/latch/ticket/";
	private static final String LATCH_REQUEST_REFERER="/api/latch/referer/request";
	
	private static final String MIMETYPE_APPLICATION_JSON = "application/json";
	
	
	
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		request.setCharacterEncoding("UTF-8");

		String username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");

		boolean success = false;
		boolean authenticated = false;
		try {
			// check whether there is already a user logged in
			HttpSession session = request.getSession(false);
			// handle SSO which doesn't set a user until later
			if (session != null && request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID) != null) {
				// destroy old session and log out the current user
				AuthenticationUtil.logout(request, response);
			}
			
	    	if(hasLatch2FA(request)){
	    		username=(String) request.getSession().getAttribute("username");
	    		String token= (String) request.getParameter("token");
	    		
	    		if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(token)){
		    		saveTemporalLatchToken(request.getSession(), username, token);
		    		
		    		Connector connector=connectorService.getConnector(SlingshotUserFactory.ALFRESCO_ENDPOINT_ID);
		    		
		    		//the user token and latch token are equals
		    		String ticket= getUserTicket(connector, username);
		    		
		    		if(StringUtils.isNotBlank(ticket)){
		    			authenticated=Boolean.TRUE;
		    			//inject alfTicket
		    			connector.getConnectorSession().setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, ticket);
		    		}
	    		}
	        	
	        }else{
	        	
	        	JSONObject refererJSON= new JSONObject();
	        	refererJSON.put("username", username);
	        	refererJSON.put("referer", "SHARE");
	        	
	        	remoteClientCall(HttpMethod.POST, MIMETYPE_APPLICATION_JSON, LATCH_REQUEST_REFERER, refererJSON.toString());
	        	
	        	// see if we can authenticate the user
	            authenticated = userFactory.authenticate(request, username,password);
	            
	            refererJSON.put("referer", "NONE");
	            remoteClientCall(HttpMethod.POST, MIMETYPE_APPLICATION_JSON, LATCH_REQUEST_REFERER, refererJSON.toString());
	        }
	    	
			if (authenticated) {
				AuthenticationUtil.login(request, response, username, false);

				// mark the fact that we succeeded
				success = true;
			}
		} catch (Throwable err) {
			throw new ServletException(err);
		}

		// If they succeeded in logging in, redirect to the success page
		// Otherwise, redirect to the failure page
		if (success) {
			onSuccess(request, response);
		} else {
			onFailure(request, response);
		}

		return null;
	}
	
	
	
	   /**
	 * @param connector 
	 * @param username
	 * @return
	 * @throws ConnectorServiceException 
	 * @throws JSONException 
	 */
	private String getUserTicket(Connector connector, String username) throws ConnectorServiceException, JSONException {
		
		String ticket= null;
		
        Response res=remoteClientGetCall(LATCH_USER_TICKET_WEBSCRIPT + URLEncoder.encode(username));
        
	    if (Status.STATUS_OK == res.getStatus().getCode())
	    {
	    	JSONObject response= new JSONObject(res.getText());
	    	if(response.has("ticket")){
	    		ticket=response.getString("ticket");
	    	}
	    }
		return ticket;
	}



	/**
     * Overrides the inherited method to retrieve the groups that the authenticated user is a member
     * of and stores them as a comma delimited {@link String} in the {@link HttpSession}. This {@link String}
     * is then retrieved when loading a user in the {@link SlingshotUserFactory} and stored as a user
     * property. 
     * 
     * @param request The {@link HttpServletRequest}
     * @param response The {@link HttpServletResponse}
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        
        try
        {
            // Get the authenticated user name and use it to retrieve all of the groups that the user is a 
            // member of...
            String username = (String) request.getParameter("username");
            if(username==null){
            	username=(String) request.getSession().getAttribute("username");
            }
            Connector conn = FrameworkUtil.getConnector(request.getSession(), username, AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
            ConnectorContext c = new ConnectorContext(HttpMethod.GET);
            c.setContentType("application/json");
            Response res = conn.call("/api/people/" + URLEncoder.encode(username) + "?groups=true", c);
            if (Status.STATUS_OK == res.getStatus().getCode())
            {
                // Assuming we get a successful response then we need to parse the response as JSON and then
                // retrieve the group data from it...
                // 
                // Step 1: Get a String of the response...
                String resStr = res.getResponse();
                
                // Step 2: Parse the JSON...
                JSONParser jp = new JSONParser();
                Object userData = jp.parse(resStr.toString());

                // Step 3: Iterate through the JSON object getting all the groups that the user is a member of...
                StringBuilder groups = new StringBuilder();
                if (userData instanceof JSONObject)
                {
                    Object groupsArray = ((JSONObject) userData).get("groups");
                    if (groupsArray instanceof org.json.simple.JSONArray)
                    {
                        for (Object groupData: (org.json.simple.JSONArray)groupsArray)
                        {
                            if (groupData instanceof JSONObject)
                            {
                                Object groupName = ((JSONObject) groupData).get("itemName");
                                if (groupName != null)
                                {
                                    groups.append(groupName.toString() + ",");
                                }
                            }
                        }
                    }
                }
                
                // Step 4: Trim off any trailing commas...
                if (groups.length()>0)
                {
                    groups.delete(groups.length()-1, groups.length());
                }
                
                // Step 5: Store the groups on the session...
                HttpSession session = request.getSession(false);
                session.setAttribute(SESSION_ATTRIBUTE_KEY_USER_GROUPS, groups.toString());
            }
            else
            {
                HttpSession session = request.getSession(false);
                session.setAttribute(SESSION_ATTRIBUTE_KEY_USER_GROUPS, "");
            }
        }
        catch (ConnectorServiceException e1)
        {
            throw new Exception("Error creating remote connector to request user group data");
        }

        String successPage = (String) request.getParameter("success");
        if (successPage != null)
        {
            response.sendRedirect(UriUtils.relativeUri(successPage));
        }
        else
        {
            response.sendRedirect(request.getContextPath());
        }
    }
	
	/* (non-Javadoc)
	 * @see org.springframework.extensions.surf.mvc.LoginController#onFailure(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void onFailure(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String username= request.getParameter("username");
		
		if(StringUtils.isNotBlank(username)){
			request.getSession().setAttribute("username", username);
		}else{
			username= (String) request.getSession().getAttribute("username");
		}
		
		Boolean twoFARedirect=Boolean.FALSE;
		
        Response res = remoteClientGetCall(LATCH_2FA_TOKEN_WEBSCRIPT + URLEncoder.encode(username));
        		
        if (Status.STATUS_OK == res.getStatus().getCode())
	    {
	    	JSONObject data=new JSONObject(res.getResponse());
	      	if(data.has("success")){
	      		twoFARedirect=data.getBoolean("success");
	      	}
	    }

	    if(twoFARedirect){
			response.sendRedirect(request.getContextPath()+"/page/latch-twofa?username="+username);
		}else{
			super.onFailure(request, response);
		}
	}
	
	/**
	 * @param httpSession 
	 * @param username
	 * @param password
	 */
	private void saveTemporalLatchToken(HttpSession httpSession, String username, String token) {
		try {
			
			
			JSONObject latchToken= new JSONObject();
			latchToken.put("username", username);
			latchToken.put("token", token);
			
			Response response=remoteClientCall(HttpMethod.POST, MIMETYPE_APPLICATION_JSON, LATCH_2FA_TEMPORAL_TOKEN_WEBSCRIPT, latchToken.toString());
			
			ConnectorContext c = new ConnectorContext(HttpMethod.POST);
            c.setContentType(MIMETYPE_APPLICATION_JSON);
            
            if (Status.STATUS_OK != response.getStatus().getCode())
            {
            	//TODO throw exception
            }
		} catch (ConnectorServiceException e) {
			logger.error("Exception in LatchSlingshotUserFactory.saveTemporalLatchToken()", e);
		} catch (JSONException e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * @param request
	 * @return
	 */
	private boolean hasLatch2FA(HttpServletRequest request) {
		String referer=request.getHeader("referer");
		Boolean res=StringUtils.isNotBlank(referer) && referer.matches("(.*)/page/latch-twofa\\?username=(.*)");
		res=res && StringUtils.isNotEmpty(request.getParameter("token"));
		return res;
	}
	
	public Response remoteClientGetCall(String uri) throws ConnectorServiceException{
		return remoteClientCall(HttpMethod.GET,null,uri,null);
	}
	
	public Response remoteClientCall(HttpMethod httpMethod, String contentType, String uri, String body) throws ConnectorServiceException{
		
		Response response=null;
		Connector connector=connectorService.getConnector(SlingshotUserFactory.ALFRESCO_ENDPOINT_ID);
		remoteClient.setEndpoint(connector.getEndpoint());
		remoteClient.setRequestMethod(httpMethod);
		
		if(contentType!=null){
			remoteClient.setDefaultContentType(contentType);
		}
		
		if(body!=null){
			response=remoteClient.call(uri,body);
		}else{
			response=remoteClient.call(uri);
		}
		
		return response;
	}
	
	
	
   public void setConnectorService(ConnectorService connectorService){
	   this.connectorService=connectorService;
   }
   
   public void setRemoteClient(RemoteClient remoteClient){
	   this.remoteClient=remoteClient;
   }
   
   /**
    * <p>This method is provided to allow the Spring framework to set a <code>UserFactory</code> required for authenticating
    * requests</p>
    * 
    * @param userFactory
    */
   public void setUserFactory(UserFactory userFactory) 
   {
       this.userFactory = userFactory;
   }
   

}
