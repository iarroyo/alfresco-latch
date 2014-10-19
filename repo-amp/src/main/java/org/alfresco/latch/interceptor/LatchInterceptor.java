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
				LatchResponse latchReponse = latchService.getLatchStatus(
						accountID);

				// process response
				processReponse(latchReponse, userName);

			}

		}

	}

	/**
	 * @param latchReponse
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
				// If the account is externally unpaired
				latchService.externallyUnpairedAccount(userName);
			}
			
			//latch status ON
			if(data!=null && latchService.isLatchON(latchResponse)){
				//TODO Second authentication factor
			}else{
				//AuthenticationCredentialsNotFoundException, not give any clue.
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

}
