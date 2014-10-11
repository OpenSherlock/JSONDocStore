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
//import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
//import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
//import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
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
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreOntology;
import org.topicquests.util.ConfigurationHelper;
import org.topicquests.util.TextFileHandler;

//import test.BuildComplexKnowledgeBase;

/**
 * @author park
 * A common class serving two ElasticSearch model providers
 * @see http://www.programcreek.com/java-api-examples/index.php?api=org.elasticsearch.action.search.SearchType
 */
public abstract class AbstractBaseElasticSearchModel {
	protected JSONDocStoreEnvironment environment;
//	private JSONObject innerMapping;
	private TextFileHandler handler;
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
		handler = new TextFileHandler();
		return doInit();
	}
	
	protected IResult getMappings(String fileName) {
		IResult result = new ResultPojo();
		String mappings = handler.readFile(ConfigurationHelper.findPath(fileName));
		try {
			JSONObject jo = (JSONObject)new JSONParser().parse(mappings);
			environment.logDebug("AbstractBaseElasticSearchModel "+fileName+" "+mappings);
			result.setResultObject(jo);
		} catch (Exception e) {
			//stop here
			throw new RuntimeException(e);
		}
		
		return result;
	}

	
	
	////////////////////////////////////
	// Core API
	////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#putDocument(java.lang.String)
	 */
	public IResult putDocument(String id, String index, String type, String jsonString, boolean checkVersion) {
		environment.logDebug("AbstractBaseElasticSearchModel.putDocument- "+index+" "+type+" "+id+" "+jsonString.length());
		IResult result = new ResultPojo();
		if (checkVersion) {
			String vers = fetchVersionProperty(jsonString);
			if (vers != null) {
				String vStored = checkVersion(id,index,type,vers);
				if (vStored != null) {
					//Optimistic Lock Failure
					result.addErrorString(IJSONDocStoreOntology.OPTIMISTIC_LOCK_EXCEPTION+" "+vStored+" "+vers);
					return result;
				}
			}
		}		IndexRequestBuilder idxb = prepareIndex(index,type,id);
		idxb.setSource(jsonString);
		IndexResponse resp = idxb.execute().actionGet(REQUEST_DELAY);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#putDocument(org.json.simple.JSONObject)
	 */
	public IResult putDocument(String id, String index, String type, JSONObject document, boolean checkVersion) {
		environment.logDebug("AbstractBaseElasticSearchModel.putDocument-2 "+index+" "+type+" "+id+" "+(document != null));
		//TODO should deal with a null document
		IResult result = new ResultPojo();
		if (checkVersion) {
			String vers = (String)document.get(IJSONDocStoreOntology.VERSION_PROPERTY);
			if (vers != null) {
				String vStored = checkVersion(id,index,type,vers);
				if (vStored != null) {
					//Optimistic Lock Failure
					result.addErrorString(IJSONDocStoreOntology.OPTIMISTIC_LOCK_EXCEPTION+" "+vStored+" "+vers);
					return result;
				}
			}
		}
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

	/**
	 * <p>Compare <code>version</code> to the {@link IJSONDocStoreOntology$VERSION_PROPERTY}
	 * associated with <code>docId</code>.</p>
	 * <p>Returns <code>null</code> if either the two values compare, or if there is now found
	 * version.</p>
	 * <p>Returns the found version value if they do not compare</p>
	 * @param docId
	 * @param index
	 * @param type
	 * @param version
	 * @return
	 */
	String checkVersion(String docId, String index, String type, String version) {
		String result = null;
		IResult r = this.getDocument(index, type, docId);
		if (r.getResultObject() != null) {
			String jdoc = (String)r.getResultObject();
			String vStored = fetchVersionProperty(jdoc);
			if (vStored != null) {
				long v1 = Long.parseLong(vStored);
				long v2 = Long.parseLong(version);
				//new version must not be less than old version
				if (v2 < v1)
					result = vStored;
			}
		}
		return result;
	}
	
	/**
	 * (tested). Returns <code>null</code> if nothing found
	 * @param jsonDoc
	 * @return
	 */
	String fetchVersionProperty(String jsonDoc) {
		String result = null;
		String x = jsonDoc;
		int where = x.indexOf(IJSONDocStoreOntology.VERSION_PROPERTY);
		if (where > 0) {
			//trim to the property field
			x = x.substring(where);
			where = x.indexOf(':');
			//skip past the ":"
			x = x.substring(where+1).trim();
			where = x.indexOf('"',1);
			//pluck the version number out of there
			result = x.substring(1, where);
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
			// ????
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
		result.setResultObject(new Boolean(false)); // default
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
		environment.logDebug("AbstractBaseElasticSearchModel.runQuery1- "+index+" "+queryString);
		IResult result = new ResultPojo();
		try {
			
			SearchRequestBuilder builder = prepareSearch(index)
	                .setSearchType(SearchType.QUERY_THEN_FETCH)
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
	                .setSearchType(SearchType.QUERY_THEN_FETCH)
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
		TermQueryBuilder termQuery = QueryBuilders.termQuery(key, value);
	//	StringBuilder buf = new StringBuilder("{\"match\": {\"");
	//	buf.append(key);
	//	buf.append("\": \"");
	//	buf.append(value);
	//	buf.append("\"}}");
	//	String queryString = buf.toString(); // "{\"match\": {\""+key+"\": \""+value+"\"}}";
		return this.runQuery(index, termQuery, 0, -1, types);
	}

	/**
	var query = {};
    if (count > -1) {
    	query.size = count;
    	query.from = start;
    }

	var m = {};
	var q = {};
	q[property] = value;
	//supposed to be term but that didn't work
	m.term = q;
	query.query = m;
	return query;
	{ size:0,from:0,query:{term:{key:value}}}
	 */

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#listDocumentsByPerperty(java.lang.String)
	 */
	public IResult listDocumentsByProperty(String index, String key, String value,
			int start, int count, String... types) {
		//NOTE: this performs a termQuery which might fail if the fields were not defined properly
		return listDocumentsByKeywordProperty(index,key,value,start,count,types);
/*		String x = "";
		for (String y : types) {
			x+=y;
		}
		//fails: {"size":30, "from":0,"query":{"term": {"sbOf": "TypeType"}}}
		// 
		//fails: {"size":30, "from":0,"term": {"sbOf": "TypeType"}}
		StringBuilder buf = new StringBuilder();
		if (count > -1) {
			buf.append("{\"size\":"+count+", \"from\":"+start+",");
		}
		//TODO THIS FAILS TO HONOR START & COUNT
//		StringBuilder buf1 = new StringBuilder("\"query\":{\"term\": {");
		StringBuilder buf1 = new StringBuilder("\"term\": {");
//		buf1.append("\""+key+"\": \""+value+"\"}}}");
		buf1.append("\""+key+"\": \""+value+"\"}}");
		buf.append(buf1);
		String queryString = buf.toString(); //"{\"match\": {\""+key+"\": \""+value+"\"}}";
		environment.logDebug("AbstractBaseElasticSearchModel.listDocumentsByProperty "+index+" "+x+" "+key+" "+queryString);
		return this.runQuery(index, queryString, start, count, types); */
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
		QueryBuilder qb = QueryBuilders.termQuery(key, query);
		IResult result = runQuery(index,qb,start,count,types);
		return result;
	}
	
	//Used in both extension classes
//	protected String createMapping(String index, JSONObject mapping) {
//		JSONObject jo = new JSONObject();
//		jo.put(index, mapping);
//		return jo.toJSONString();
//	}
}
