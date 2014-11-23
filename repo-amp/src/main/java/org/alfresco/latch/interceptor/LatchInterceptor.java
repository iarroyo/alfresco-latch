package org.alfresco.latch.interceptor;

import org.alfresco.latch.service.LatchService;
import org.alfresco.repo.security.authentication.AuthenticationDisallowedException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;

import com.elevenpaths.latch.Error;
import com.elevenpaths.latch.LatchResponse;
import com.google.gson.JsonObject;

public class LatchInterceptor implements MethodInterceptor {

	private LatchService latchService;
	private boolean extremeLatchAccess;
	private static final String AUTHENTICATED_METHOD = "authenticate";

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {

		if (latchService.getLatchConfig().isAvailable()) {
			proceed(mi);
		}

		return mi.proceed();
	}

	/**
	 * @param mi
	 * @throws Throwable
	 */
	private void proceed(MethodInvocation mi)
			throws Throwable {
		// Filter authentication method
		String methodName = mi.getMethod().getName();
		Object[] arguments = mi.getArguments();
		if (methodName.equals(AUTHENTICATED_METHOD)) {

			// Get latch accountID
			String userName = (String) arguments[0];
			String accountID = latchService.getAccountID(userName);

			// has the user the latch "accountID"
			if (StringUtils.isNotEmpty(accountID)) {

				// latch status
				LatchResponse latchReponse = latchService.getLatchStatusNOOtp(accountID);

				// process response
				processReponse(latchReponse, userName);

			}

		}

	}

	/**
	 * Process the latch response checking if the latch is open or close.
	 * 
	 * @param latchReponse
	 */
	private void processReponse(LatchResponse latchResponse, String userName) {

		JsonObject data = latchResponse.getData();
		Error error = latchResponse.getError();
		
		// Not block user account if any error happen in the latch server
		if(data!=null || error!=null){
			if(error!=null){
				
				// Error 201: Account not paired
				// https://latch.elevenpaths.com/www/developers/doc_api
				//102 = Invalid application signature  >> Bad secret key or application id or latch app removed
				if(error.getCode()==HttpStatus.SC_CREATED || error.getCode()==HttpStatus.SC_PROCESSING){
					// If the account is externally unpaired
					latchService.externallyUnpairedAccount(userName);
				}else if((error.getCode()/100)==5 && extremeLatchAccess){
					throw new AuthenticationDisallowedException("Extreme latch access enabled. Latch Service is not responding.");
				}
				
			}else if(data!=null && !latchService.isLatchON(latchResponse)){
				//AuthenticationCredentialsNotFoundException
				throw new AuthenticationDisallowedException("Latch has blocked the authenticaton");
			}
		}

	}

	/**
	 * @param latchService
	 *            the latchService to set
	 */
	public void setLatchService(LatchService latchService) {
		this.latchService = latchService;
	}
	
	public void setExtremeLatchAccess(boolean access){
		this.extremeLatchAccess=access;
	}

}
