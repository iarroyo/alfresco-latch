/**
 * 
 */
package org.alfresco.latch.dao;

import org.alfresco.latch.config.LatchConfig;
import org.alfresco.service.cmr.repository.NodeRef;

import com.elevenpaths.latch.LatchErrorException;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public interface LatchDAO {
	
	public static final String ACCOUNT_ID="accountId";
	
	/**
	 * Get the accountID for the given username.
	 * 
	 * @param userName
	 * @return
	 */
	public String getAccountID(String userName);
	
	/**
	 * Get the accountID for the given nodeRef
	 * 
	 * @param userRef
	 * @return
	 */
	public String getAccountID(NodeRef userRef);
	
	/**
	 * Pair the user account with latch.
	 * 
	 * @param latchConfig
	 * @param userName
	 * @param token
	 * @throws LatchErrorException
	 */
	public void pairAccount(LatchConfig latchConfig, String userName, String token) throws LatchErrorException;
	
	/**
	 * Unpair the user account with latch.
	 * 
	 * @param latchConfig
	 * @param userName
	 * @throws LatchErrorException
	 */
	public void unpairAccount(LatchConfig latchConfig, String userName) throws LatchErrorException;
	
	/**
	 * Unpair the user account when the account has been unpaired externally.
	 * @param userName
	 */
	public void externallyUnpairedAccount(String userName);

}
