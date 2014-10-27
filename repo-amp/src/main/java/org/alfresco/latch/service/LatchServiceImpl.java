/**
 * 
 */
package org.alfresco.latch.service;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.latch.dao.LatchDAO;
import org.alfresco.latch.sdk.LatchSDK;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang.StringUtils;

import com.elevenpaths.latch.Latch;
import com.elevenpaths.latch.LatchErrorException;
import com.elevenpaths.latch.LatchResponse;
import com.google.gson.JsonObject;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class LatchServiceImpl implements LatchService {

	private LatchConfig latchConfig;
	private LatchDAO latchDAO;

	/* (non-Javadoc)
	 * @see org.alfresco.latch.services.LatchService#getAccountID(java.lang.String)
	 */
	@Override
	public String getAccountID(String userName) {
		return latchDAO.getAccountID(userName);
	}

	/* (non-Javadoc)
	 * @see org.alfresco.latch.services.LatchService#getAccountID(org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	public String getAccountID(NodeRef userRef) {
		return latchDAO.getAccountID(userRef);
	}

	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#getLatchStatus(java.lang.String, java.lang.String)
	 */
	@Override
	public LatchResponse getLatchStatus(String accountID) {

		LatchResponse latchResponse= new LatchResponse();
		
		String appID= this.latchConfig.getAppID();
		String secret= this.latchConfig.getSecret();
		
		if(latchConfig.isAvailable()){
			
			Latch latch= new LatchSDK(latchConfig); 
			latchResponse= latch.status(accountID);
		}
		
		return latchResponse;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#pairAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public void pairAccount(String userName, String token) throws LatchErrorException {

		latchDAO.pairAccount(latchConfig, userName, token);
	}

	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#unpairAccount(java.lang.String)
	 */
	@Override
	public void unpairAccount(String userName) throws LatchErrorException {

		latchDAO.unpairAccount(latchConfig, userName);
		
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#externallyUnpairedAccount(java.lang.String)
	 */
	@Override
	public void externallyUnpairedAccount(String userName) {
		
		latchDAO.externallyUnpairedAccount(userName);
		
	}

	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#isLatchON(com.elevenpaths.latch.LatchResponse, java.lang.String)
	 */
	@Override
	public Boolean isLatchON(LatchResponse latchResponse) {
		
		Boolean res= Boolean.FALSE;
		JsonObject data= latchResponse.getData();
		String appID=this.latchConfig.getAppID();
		
		if(data.has(OPERATIONS)){
			JsonObject operations= data.getAsJsonObject(OPERATIONS);
			if(operations.has(appID)){
				JsonObject operation= operations.getAsJsonObject(appID);
				String status = operation.get(STATUS).getAsString();
				res= status!=null && status.toUpperCase().equals(STATUS_ON)? Boolean.TRUE:Boolean.FALSE;
			}
			
		}
		return res;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.latch.services.LatchService#isAvailable()
	 */
	@Override
	public LatchConfig getLatchConfig() {
		return this.latchConfig;
	}

	/**
	 * @param latchConfig
	 *            the latchConfig to set
	 */
	public void setLatchConfig(LatchConfig latchConfig) {
		this.latchConfig = latchConfig;
	}

	/**
	 * @return the latchDAO
	 */
	public LatchDAO getLatchDAO() {
		return latchDAO;
	}

	/**
	 * @param latchDAO the latchDAO to set
	 */
	public void setLatchDAO(LatchDAO latchDAO) {
		this.latchDAO = latchDAO;
	}




}
