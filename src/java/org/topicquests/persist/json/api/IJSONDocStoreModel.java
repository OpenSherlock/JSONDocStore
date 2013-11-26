/**
 * 
 */
package org.topicquests.persist.json.api;

import org.json.simple.JSONObject;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author park
 *
 */
public interface IJSONDocStoreModel {
	
	IResult init(JSONDocStoreEnvironment env);

	IResult putDocument(String id, String index, String type, String jsonString);
	
	IResult putDocument(String id, String index, String type, JSONObject document);
	
	/**
	 * Return a JSON String representing the document
	 * @param index
	 * @param type
	 * @param documentId
	 * @return
	 */
	IResult getDocument(String index, String type, String documentId);
	
	/**
	 * Will return a Boolean based on existence of a document answering to <code>documentId</code>
	 * @param index
	 * @param type
	 * @param documentId
	 * @return
	 */
	IResult documentExists(String index, String type, String documentId);
	
	/**
	 * Find a specific document according to a <code>key/value</code> pair
	 * @param index
	 * @param key
	 * @param value
	 * @param types
	 * @return
	 */
	IResult getDocumentByProperty(String index, String key, String value, String... types);
	
	/**
	 * Works only on keys (fields) which are set to <em>not-analyzed</em>
	 * in the file <code>mappings.json</code>
	 * @param index
	 * @param key
	 * @param wildcardQuery
	 * @param start TODO
	 * @param count TODO
	 * @param types
	 * @return
	 */
	IResult listDocumentsByWildcardPropertyValue(String index, String key, String wildcardQuery, int start, int count, String...types);
	
	IResult listDocumentsByKeywordProperty(String index, String key, String query, int start, int count, String...types);
	
	/**
	 * <p>List all documents according to a <code>key/value</code> pair</p>
	 * <p>This can be run recursively to walk down a tree structure</p>
	 * @param index
	 * @param key
	 * @param value
	 * @param start
	 * @param count
	 * @param types
	 * @return
	 */
	IResult listDocumentsByProperty(String index, String key, String value, int start, int count, String... types);

	/**
	 * Return a Long count of documents in the given <code>index</code>
	 * @param indices
	 * @return
	 */
	IResult countDocuments(String... indices);
	
	/**
	 * Remove a document identified by <code>documentId</code>
	 * @param index
	 * @param type
	 * @param documentId
	 * @return
	 */
	IResult removeDocument(String index, String type, String documentId);
	
	/**
	 * <p> Process a JSON-String query</p>
	 * <p>Note: <code>queryString</code> must be in appropriate JSON query form</p>
	 * @param index
	 * @param queryString
	 * @param start 
	 * @param count -1 means all
	 * @param types
	 * @see http://exploringelasticsearch.com/book/searching-data/the-query-dsl-and-the-search-api.html
	 * @return empty List or List of JSON String documents
	 */
	IResult runQuery(String index, String queryString, int start, int count, String... types);

	IResult runQuery(String index, QueryBuilder qb, int start, int count, String... types);

	/**
	 * Required behavior when system is shut down.
	 */
	void shutDown();
}
