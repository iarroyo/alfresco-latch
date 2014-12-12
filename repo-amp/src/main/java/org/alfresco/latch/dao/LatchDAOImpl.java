/**
 * 
 */
package org.alfresco.latch.dao;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.latch.exception.LatchException;
import org.alfresco.latch.model.LatchModel;
import org.alfresco.latch.sdk.LatchSDK;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.PropertyMap;
import org.apache.commons.lang.StringUtils;

import com.elevenpaths.latch.Latch;
import com.elevenpaths.latch.LatchResponse;
import com.google.gson.JsonObject;
import com.elevenpaths.latch.Error;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class LatchDAOImpl implements LatchDAO {

	private NodeService nodeService;
	private PersonService personService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.latch.dao.LatchDAO#getAccountID(java.lang.String)
	 */
	@Override
	public String getAccountID(String userName) {
		NodeRef userRef = personService.getPerson(userName);
		return getAccountID(userRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.alfresco.latch.dao.LatchDAO#getAccountID(org.alfresco.service.cmr
	 * .repository.NodeRef)
	 */
	@Override
	public String getAccountID(NodeRef userRef) {
		String accountID = (String) nodeService.getProperty(userRef,
				LatchModel.PROP_ACCOUNT_ID);
		return accountID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.latch.dao.LatchDAO#pairAccount(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void pairAccount(LatchConfig latchConfig, String userName,
			String token) throws LatchException {

		NodeRef userRef = personService.getPerson(userName);

		// Check if the user is already paired
		if (!nodeService.hasAspect(userRef, LatchModel.ASPECT_LATCH)) {
			performPairing(latchConfig, token, userRef);
		}

	}

	/**
	 * @param latchConfig
	 * @param token
	 * @param userName
	 * @throws LatchErrorException 
	 */
	private void performPairing(LatchConfig latchConfig, String token,
			NodeRef userRef) throws LatchException {

		if (latchConfig.isAvailable()) {
			Latch latch = new LatchSDK(latchConfig);
			LatchResponse latchResponse = latch.pair(token);
			processPairingResponse(latchResponse, userRef);
		}

	}

	/**
	 * @param data
	 * @param userName
	 * @throws LatchErrorException 
	 */
	private void processPairingResponse(LatchResponse latchResponse, NodeRef userRef) throws LatchException {

		JsonObject data = latchResponse.getData();
		Error error =latchResponse.getError();
		
		if (error!=null){
			//TODO set better error description
			//102 = Invalid application signature  >> Settings error: Bad secret key or application id
			throw new LatchException(error.getMessage());
		}else if(data != null && data.has(ACCOUNT_ID)) {
			
			String accountID = data.get(ACCOUNT_ID).getAsString();
			if (StringUtils.isNotEmpty(accountID)) {

				PropertyMap properties = new PropertyMap();
				properties.put(LatchModel.PROP_ACCOUNT_ID, accountID);

				//Set property, include aspect automatically but it is more clear
				//to someone that unknown it. ;)
				nodeService.addAspect(userRef, LatchModel.ASPECT_LATCH, properties);
			}else{
				throw new LatchException("Latch pairing process. Unknown error.");
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.latch.dao.LatchDAO#unpairAccount(java.lang.String)
	 */
	@Override
	public void unpairAccount(LatchConfig latchConfig, String userName) throws LatchException {

		NodeRef userRef = personService.getPerson(userName);
		if(nodeService.hasAspect(userRef, LatchModel.ASPECT_LATCH)){
			
			String accountID= (String) nodeService.getProperty(userRef, LatchModel.PROP_ACCOUNT_ID);
			
			if(latchConfig.isAvailable()){
				Latch latch= new LatchSDK(latchConfig);
				LatchResponse latchResponse=latch.unpair(accountID);
				Error error =latchResponse.getError();
				if(error!=null){
					//TODO set better error description
					//102 = Invalid application signature  >> Settings error: Bad secret key or application id
					throw new LatchException(error.getMessage());
				}else{
					unpairData(userRef);
				}
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.alfresco.latch.dao.LatchDAO#externallyUnpairedAccount(java.lang.String)
	 */
	@Override
	public void externallyUnpairedAccount(String userName) {
		NodeRef userRef = personService.getPerson(userName);
		unpairData(userRef);
	}
	
	/**
	 * Remove the latch account from the user.
	 * 
	 * @param userRef
	 */
	private void unpairData(NodeRef userRef){
		nodeService.removeAspect(userRef, LatchModel.ASPECT_LATCH);
	}

	public void setNodeService(NodeService nodeService){
		this.nodeService=nodeService;
	}
	
	public void setPersonService(PersonService personService){
		this.personService=personService;
	}


}
