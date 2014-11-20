/**
 * 
 */
package org.alfresco.latch.service;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.latch.config.TwoFAConfig;
import org.alfresco.latch.config.TwoFAConfig.REFERER_REQ;
import org.alfresco.latch.dao.LatchDAO;
import org.alfresco.latch.sdk.LatchSDK;
import org.alfresco.repo.cache.SimpleCache;
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
	private SimpleCache<String, TwoFAConfig> twoFACache;

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
		
		if(latchConfig.isAvailable()){
			
			Latch latch= new LatchSDK(latchConfig); 
			latchResponse= latch.status(accountID);
		}
		
		return latchResponse;
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#getLatchStatusNOOtp(java.lang.String)
	 */
	@Override
	public LatchResponse getLatchStatusNOOtp(String accountID) {
		LatchResponse latchResponse= new LatchResponse();
		
		if(latchConfig.isAvailable()){
			
			LatchSDK latch= new LatchSDK(latchConfig); 
			latchResponse= latch.statusNOOtp(accountID);
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
	
	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#is2FAEnabled(com.elevenpaths.latch.LatchResponse)
	 */
	@Override
	public JsonObject isTwoFAEnabled(LatchResponse latchResponse) {
		JsonObject twoFA= null;
		JsonObject data= latchResponse.getData();
		String appID=this.latchConfig.getAppID();
		
		if(data.has(OPERATIONS)){
			JsonObject operations= data.getAsJsonObject(OPERATIONS);
			JsonObject operation= operations.getAsJsonObject(appID);
			if(operation.has(TWO_FACTOR)){
				twoFA= operation.getAsJsonObject(TWO_FACTOR);
			}
			
		}
		
		return twoFA;
	}
	
	public Boolean twoFACacheHasUserToken(String username){
		return twoFACache.get(username)!=null && twoFACache.get(username).getToken()!=null;
	}
	
	public Boolean twoFACacheHasTemporalToken(String username){
		
		Boolean res=Boolean.FALSE;
		if(twoFACache.contains(username)){
			TwoFAConfig twoFAConfig=twoFACache.get(username);
			res= StringUtils.isNotBlank(twoFAConfig.getTemporalToken());
		}
		return res;
	}
	
	public void updateTemporalUserToken(String username, String token){
		TwoFAConfig twoFAConfig=twoFACache.get(username);
		twoFAConfig.setTemporalToken(token);
		//update 2fa config
		twoFACache.put(username, twoFAConfig);
	}
	
	public void updateLatchUserToken(String username, String token){
		TwoFAConfig twoFAConfig=twoFACache.get(username);
		if(twoFAConfig==null){
			twoFAConfig= new TwoFAConfig();
		}
		twoFAConfig.setToken(token);
		//update 2fa config
		twoFACache.put(username, twoFAConfig);
	}
	
	public Boolean checkTwoFATokens(String username) {
		Boolean res=Boolean.FALSE;
		if(twoFACache.contains(username)){
			TwoFAConfig twoFAConfig=twoFACache.get(username);
			res= twoFAConfig.getToken()!=null && twoFAConfig.getToken().equals(twoFAConfig.getTemporalToken());
		}
		return res; 
	}
	
	public void removeTwoFAConfig(String username) {
		if(twoFACache.contains(username)){
			twoFACache.remove(username);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#putRefererRequest(java.lang.String, java.lang.String)
	 */
	@Override
	public void putRefererRequest(String userName, String referer) {
		TwoFAConfig twoFAConfig=twoFACache.get(userName);
		if(twoFAConfig==null){
			twoFAConfig= new TwoFAConfig();
		}
		
		twoFAConfig.setRefererReq(REFERER_REQ.valueOf(referer));
		twoFACache.put(userName, twoFAConfig);
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.latch.service.LatchService#isValidRequest(java.lang.String)
	 */
	@Override
	public boolean isValidRequest(String username) {
		Boolean res=Boolean.FALSE;
		if(twoFACache.contains(username)){
			TwoFAConfig twoFAConfig=twoFACache.get(username);
			res=twoFAConfig.getRefererReq().equals(TwoFAConfig.REFERER_REQ.SHARE);
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
	
	public void setTwoFASharedCache(SimpleCache<String, TwoFAConfig> twoFACache){
		this.twoFACache=twoFACache;
	}



}
