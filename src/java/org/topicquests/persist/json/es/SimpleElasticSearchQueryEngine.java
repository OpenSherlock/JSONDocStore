/*
 * Copyright 2013, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.persist.json.es;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IQueryEngine;
import org.topicquests.persist.json.es.api.IElasticSearchModel;

/**
 * @author park
 * <p>An experimental Query DSL for ElasticSearch</p>
 * @see http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/query-dsl-queries.html
 */
public class SimpleElasticSearchQueryEngine implements IQueryEngine {
	private JSONDocStoreEnvironment environment;
	private Client client;
	
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IQueryEngine#init(org.topicquests.persist.json.JSONDocStoreEnvironment)
	 */
	@Override
	public IResult init(JSONDocStoreEnvironment env) {
		environment = env;
		IResult result = new ResultPojo();
		client = ((IElasticSearchModel)environment.getModel()).getClient();
		System.out.println("SimpleElasticSearchQueryEngine- "+client);
		return result;
	}
	//////////////////////////////////
	// Various experimental Query APIs
	//////////////////////////////////
	
	
	/**
	 * WILD+CARD Queries, with '*' (multi character) or '?' (single character)
	 * do not appear to work, per ThirdTest
	 * Cannot use a wildcard in a regular query: can put them in query strings for
	 * runQuery
	 * @see http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-wildcard-query.html
	 * @return
	 */
	public IResult wildCardQuery(String index, String type, String id) {
	//	provider.open();
		IResult result = new ResultPojo();
		try {
			GetResponse resp =  client.prepareGet(index, type, id)
		        .execute()
		        .actionGet(10000);
			String rx = resp.getSourceAsString();
			result.setResultObject(rx);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
	
		return result;
	}
	
}
