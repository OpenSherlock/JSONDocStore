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

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.util.ConfigurationHelper;
import org.topicquests.util.TextFileHandler;

import test.BuildComplexKnowledgeBase;

/**
 * @author park
 * A common class serving two ElasticSearch model providers
 */
public abstract class AbstractBaseElasticSearchModel {
	protected JSONDocStoreEnvironment environment;
	private JSONObject innerMapping;
	protected boolean isShutDown = false;
	/** Default request delay; can be set otherwise with config property */
	protected String REQUEST_DELAY = "10000";

	/**
	 * Pass init function to extension
	 * @return
	 */
	protected abstract IResult doInit();
	
	protected abstract 	CountRequestBuilder prepareCount(String... indices);

	protected abstract ActionFuture<DeleteResponse> delete(DeleteRequest request);
	
	protected abstract GetRequestBuilder prepareGet(String index,  String type, String id);
	
	protected abstract SearchRequestBuilder prepareSearch(String... indices);
	
	protected abstract IndexRequestBuilder prepareIndex(String index, String type,  String id);

	
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#init(java.lang.String)
	 */
	public IResult init(JSONDocStoreEnvironment env) {
		environment = env;
		String x = environment.getStringProperty("RequestDelay");
		if (x != null)
			REQUEST_DELAY = x;
		TextFileHandler h = new TextFileHandler();
		String mappings = h.readFile(ConfigurationHelper.findPath("mappings.json"));
		try {
			innerMapping = (JSONObject)new JSONParser().parse(mappings);
		} catch (Exception e) {
			//stop here
			throw new RuntimeException(e);
		}
		return doInit();
	}
	

	
	
	////////////////////////////////////
	// Core API
	////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#putDocument(java.lang.String)
	 */
	public IResult putDocument(String id, String index, String type, String jsonString) {
		environment.logDebug("AbstractBaseElasticSearchModel.putDocument- "+index+" "+type+" "+id+" "+jsonString.length());
		IResult result = new ResultPojo();
		IndexRequestBuilder idxb = prepareIndex(index,type,id);
		idxb.setSource(jsonString);
		IndexResponse resp = idxb.execute().actionGet(REQUEST_DELAY);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#putDocument(org.json.simple.JSONObject)
	 */
	public IResult putDocument(String id, String index, String type, JSONObject document) {
		environment.logDebug("AbstractBaseElasticSearchModel.putDocument-2 "+index+" "+type+" "+id+" "+(document != null));
		IResult result = new ResultPojo();
		IndexRequestBuilder idxb = prepareIndex(index,type,id);
		idxb.setSource(document);
		environment.logDebug("AbstractBaseElasticSearchModel.putDocument-3 "+idxb);
		IndexResponse resp = null;
		try {
			resp = idxb.execute().actionGet(REQUEST_DELAY);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
			environment.logDebug("AbstractBaseElasticSearchModel.putDocument-4 "+idxb);
			resp = idxb.execute().actionGet(REQUEST_DELAY);			
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#getDocument(java.lang.String)
	 */
	public IResult getDocument(String index, String type, String documentId) {
		environment.logDebug("AbstractBaseElasticSearchModel.getDocument- "+index+" "+type+" "+documentId);
		IResult result = new ResultPojo();
		String idx = index;
		String  typ = type;
		GetResponse resp = null;
		GetRequestBuilder rbldr=null;
		try { 
			rbldr =  prepareGet(idx, typ, documentId);
					//refresh should be left to default fault for heavy get usage
			rbldr.setRefresh(true);
			environment.logDebug("AbstractBaseElasticSearchModel.getDocument-2 "+rbldr);
			resp    =    rbldr.execute().actionGet(REQUEST_DELAY);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
			environment.logDebug("AbstractBaseElasticSearchModel.getDocument-3 "+rbldr);
			//try again
			resp    =    rbldr.execute().actionGet(REQUEST_DELAY);
		}
		if (resp != null) {
			String rx = resp.getSourceAsString();
			result.setResultObject(rx);
		}
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#documentExists(java.lang.String)
	 */
	public IResult documentExists(String index, String type, String documentId) {
		IResult result = new ResultPojo();
		String idx = index;
		if (idx == null)
			idx = "";
		String typ = type;
		if (typ == null)
			typ = "";
		try {
			GetRequestBuilder rb = prepareGet(idx, typ, documentId);
			rb.setRefresh(true);
			GetResponse resp =  rb.get(REQUEST_DELAY);
			result.setResultObject(new Boolean(resp.isExists()));
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#countDocuments(java.lang.String)
	 */
	public IResult countDocuments(String... indices) {
		IResult result = new ResultPojo();
		if (indices == null)
			throw new RuntimeException("AbstractBaseModel.countDocuments cannot have null indices");
		try {
			CountRequestBuilder rb = prepareCount(indices);
			CountResponse resp =  rb.execute().actionGet(REQUEST_DELAY);
			result.setResultObject(new Long(resp.getCount()));
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#removeDocument(java.lang.String)
	 */
	public IResult removeDocument(String index, String type, String documentId) {
		IResult result = new ResultPojo();
		DeleteRequest drq = new DeleteRequest(index, type, documentId).refresh(true);
		try {
			ActionFuture<DeleteResponse> ad = delete(drq);
			DeleteResponse dr = ad.get();
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#runQuery(java.lang.String)
	 */
	public IResult runQuery(String index, String queryString, int start, int count, String... types) {
		environment.logDebug("AbstractBaseElasticSearchModel.runQuery1- "+queryString);
		IResult result = new ResultPojo();
		try {
			
			SearchRequestBuilder builder = prepareSearch(index)
					//if you leave searchType out, you get big errors
	                // .setSearchType(SearchType.QUERY_THEN_FETCH)
	                .setTypes(types)
	                .setQuery(queryString)
	                .setFrom(start);
	                if (count > -1) 
	                	builder.setSize(count);
	                
			SearchResponse resp = builder
					.execute()
					.actionGet(REQUEST_DELAY);
	                //.get("1000");
			List<String> expected = new ArrayList<String>();
			result.setResultObject(expected);
			environment.logDebug("AbstractBaseElasticSearchModel.runQuery-1 "+resp.getHits().getTotalHits());
			for (SearchHit hit : resp.getHits().getHits()) {
				expected.add(hit.getSourceAsString());
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#runQuery(java.lang.String)
	 */
	public IResult runQuery(String index, QueryBuilder qb, int start,
			int count, String... types) {
		environment.logDebug("AbstractBaseElasticSearchModel.runQuery2- "+qb.toString());
		IResult result = new ResultPojo();
		try {
			
			SearchRequestBuilder builder = prepareSearch(index)
					//if you leave searchType out, you get big errors
	                // .setSearchType(SearchType.QUERY_THEN_FETCH)
	                .setTypes(types)
	                .setQuery(qb)
	                .setFrom(start);
	                if (count > -1) 
	                	builder.setSize(count);
	                
			SearchResponse resp = builder
					.execute()
					.actionGet(REQUEST_DELAY);
	                //.get("1000");
			List<String> expected = new ArrayList<String>();
			result.setResultObject(expected);
			environment.logDebug("AbstractBaseElasticSearchModel.runQuery-1 "+resp.getHits().getTotalHits());
			for (SearchHit hit : resp.getHits().getHits()) {
				expected.add(hit.getSourceAsString());
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#getDocumentByProperty(java.lang.String)
	 */
	public IResult getDocumentByProperty(String index, String key, String value, String... types) {
		StringBuilder buf = new StringBuilder("{\"match\": {\"");
		buf.append(key);
		buf.append("\": \"");
		buf.append(value);
		buf.append("\"}}");
		String queryString = buf.toString(); // "{\"match\": {\""+key+"\": \""+value+"\"}}";
		return this.runQuery(index, queryString, 0, -1, types);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#listDocumentsByPerperty(java.lang.String)
	 */
	public IResult listDocumentsByProperty(String index, String key, String value,
			int start, int count, String... types) {
		String x = "";
		for (String y : types) {
			x+=y;
		}
		StringBuilder buf = new StringBuilder("{\"match\": {\"");
		buf.append(key);
		buf.append("\": \"");
		buf.append(value);
		buf.append("\"}}");
		String queryString = buf.toString(); //"{\"match\": {\""+key+"\": \""+value+"\"}}";
		environment.logDebug("AbstractBaseElasticSearchModel.listDocumentsByProperty "+index+" "+x+" "+key+" "+queryString);
		return this.runQuery(index, queryString, start, count, types);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#getDocumentsByFuzzyProperty(java.lang.String)
	 */
	public IResult listDocumentsByWildcardPropertyValue(String index, String key,
			String wildcardQuery, int start, int count, String... types) {
		QueryBuilder qb = QueryBuilders.wildcardQuery(key, wildcardQuery);
		IResult result = runQuery(index,qb,start,count,types);

		return result;
	}


	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#getDocumentsByKeywordProperty(java.lang.String)
	 */
	public IResult listDocumentsByKeywordProperty(String index, String key,
			String query, int start, int count, String... types) {
		QueryBuilder qb = QueryBuilders.matchQuery(key, query);
		IResult result = runQuery(index,qb,start,count,types);

		return result;
	}

	protected String createMapping(String index) {
		JSONObject jo = new JSONObject();
		jo.put(index, this.innerMapping);
		return jo.toJSONString();
	}
}
