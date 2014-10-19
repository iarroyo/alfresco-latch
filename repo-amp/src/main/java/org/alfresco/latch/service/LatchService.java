package org.alfresco.latch.service;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.service.cmr.repository.NodeRef;

import com.elevenpaths.latch.LatchErrorException;
import com.elevenpaths.latch.LatchResponse;

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
	public static final String STATUS_ON="ON";
	
	public LatchConfig getLatchConfig();
	
	public String getAccountID(String userName);

	public String getAccountID(NodeRef userRef);
	
	public LatchResponse getLatchStatus(String accountID);
	
	public void pairAccount(String userName, String token) throws LatchErrorException;
	public void unpairAccount(String userName) throws LatchErrorException;
	
	public void externallyUnpairedAccount(String userName);
	
	public Boolean isLatchON(LatchResponse latchResponse);


	
}
