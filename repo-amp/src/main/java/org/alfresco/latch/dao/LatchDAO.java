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
	
	public String getAccountID(String userName);
	public String getAccountID(NodeRef userRef);
	
	public void pairAccount(LatchConfig latchConfig, String userName, String token) throws LatchErrorException;
	public void unpairAccount(LatchConfig latchConfig, String userName) throws LatchErrorException;
	public void externallyUnpairedAccount(String userName);

}
