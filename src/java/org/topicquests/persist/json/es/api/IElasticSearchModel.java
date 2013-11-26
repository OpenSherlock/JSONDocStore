/**
 * 
 */
package org.topicquests.persist.json.es.api;

import org.elasticsearch.client.Client;
import org.topicquests.persist.json.api.IJSONDocStoreModel;

/**
 * @author park
 * Extend {@link IJSONDocStoreModel} for use with ElasticSearch
 */
public interface IElasticSearchModel extends IJSONDocStoreModel {
	
	/**
	 * Support for Query DSLs
	 * @return
	 */
	 Client getClient();
	 	 
}
