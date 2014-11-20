package org.alfresco.latch.service;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.service.cmr.repository.NodeRef;

import com.elevenpaths.latch.LatchErrorException;
import com.elevenpaths.latch.LatchResponse;
import com.google.gson.JsonObject;

/**
 * 
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public interface LatchService {
	
	public static final String OPERATIONS="operations";
	public static final String STATUS="status";
	public static final String TWO_FACTOR="two_factor";
	public static final String STATUS_ON="ON";
	
	public LatchConfig getLatchConfig();
	
	public String getAccountID(String userName);

	public String getAccountID(NodeRef userRef);
	
	public LatchResponse getLatchStatus(String accountID);
	
	public LatchResponse getLatchStatusNOOtp(String accountID);
	
	public void pairAccount(String userName, String token) throws LatchErrorException;
	public void unpairAccount(String userName) throws LatchErrorException;
	
	public void externallyUnpairedAccount(String userName);
	
	public Boolean isLatchON(LatchResponse latchResponse);

	/**
	 * @param latchResponse
	 * @return
	 */
	public JsonObject isTwoFAEnabled(LatchResponse latchResponse);
	
	public Boolean twoFACacheHasUserToken(String username);
	
	public Boolean twoFACacheHasTemporalToken(String username);
	
	public void updateTemporalUserToken(String username, String token);
	
	public void updateLatchUserToken(String username, String token);
	
	public Boolean checkTwoFATokens(String username);
	
	public void removeTwoFAConfig(String username);

	public boolean isValidRequest(String username);

	public void putRefererRequest(String userName, String referer);

}
