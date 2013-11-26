/**
 * 
 */
package org.topicquests.persist.json.api;

import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;

/**
 * @author park
 * <p>Use to create custom Query DSLs
 */
public interface IQueryEngine {

	/**
	 * To create a QueryEngine initialize this way, then
	 * create any API to suit
	 * @param esClient
	 */
	IResult init(JSONDocStoreEnvironment env);
	
	
}
