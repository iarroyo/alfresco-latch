/**
 * 
 */
package org.alfresco.latch.model;

import org.alfresco.service.namespace.QName;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public interface LatchModel {
	
    /** Latch Model URI */
    static final String LATCH_MODEL_1_0_URI = "http://www.alfresco.org/model/latch/1.0";

    /** Latch Model Prefix */
    static final String LATCH_MODEL_PREFIX = "latch";
	
	static final QName ASPECT_LATCH = QName.createQName(LATCH_MODEL_1_0_URI, "latcheable");
	
	static final QName PROP_ACCOUNT_ID = QName.createQName(LATCH_MODEL_1_0_URI, "accountID");

}
