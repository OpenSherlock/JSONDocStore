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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.api.IJSONDocStoreOntology;
import org.topicquests.persist.json.es.api.IElasticSearchModel;

/**
 * @author park
 * ElasticSearch model for remote and cluster servers
 */
public class ElasticSearchClusterModel extends AbstractBaseElasticSearchModel
		implements IElasticSearchModel {
	private TransportClient client;

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#shutDown()
	 */
	@Override
	public void shutDown() {
		if (!isShutDown) {
			client.close();
			isShutDown = true;
		}
	}


	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.es.AbstractBaseElasticSearchModel#doInit()
	 */
	@Override
	protected IResult doInit() {
		IResult result = new ResultPojo();
		List<List<String>>clusters = (List<List<String>>)environment.getProperties().get("Clusters");
		int len = clusters.size();
		//CREATE the CLIENT
		ImmutableSettings.Builder settings = settings = ImmutableSettings.settingsBuilder();
		 settings.put("ignore_indices", "missing");
		 settings.build();
		client = new TransportClient(settings);
		String name, port;
		List<String>entry;
		for (int i=0;i<len;i++) {
			entry = clusters.get(i);
			name = entry.get(0);
			port = entry.get(1);
		//	System.out.println("Foo "+entry);
			environment.logDebug("ElasticSearchClusterModel.doInit "+name+" "+port);
			if (name.equals("localhost"))
				client.addTransportAddress(new InetSocketTransportAddress(name, Integer.parseInt(port)));
			else {
				try {
					InetAddress  a = InetAddress.getByName(name);
					environment.logDebug("ElasticSearchClusterModel.doInit-1 "+a);
					client.addTransportAddress(new InetSocketTransportAddress(a, Integer.parseInt(port)));
				} catch (Exception e) {
					result.addErrorString(e.getMessage());
					environment.logError(e.getMessage(), e);
					e.printStackTrace();
				}
			}
		}
		// We wait now for the yellow (or green) status
//		System.out.println("cAAA-1");
//		client.admin().cluster().prepareHealth()
//        	.setWaitForYellowStatus().execute().actionGet();
//		System.out.println("cAAA-2 "+client);
		//Validate Indexes
		checkIndexes(result);
		return result;
	}
	
	/**
	 * Create given indices if necessary
	 * @param result
	 */
	protected void checkIndexes(IResult result) {
		List<List<String>>indexes = (List<List<String>>)environment.getProperties().get("IndexNames");
		int len = indexes.size();
		List<String>indices = new ArrayList<String>();
		environment.setIndices(indices);
		//ImmutableSettings.Builder settings = null;
		IndicesExistsRequestBuilder ib;
		IndicesExistsResponse ir;
		CreateIndexRequest cir;
		CreateIndexResponse afr;
		String idx = null;
		for (int i=0;i<len;i++) {
			idx = indexes.get(i).get(1);
			indices.add(idx);
			ib = getClient().admin().indices().prepareExists(idx);
			ir = ib.get(REQUEST_DELAY);
			System.out.println("BAR "+idx+" "+ir.isExists());
			if (!ir.isExists()) {
				try {
			//		settings = ImmutableSettings.settingsBuilder();
			//		 settings.put("path.data", "data/");
			//		 settings.build();
					 cir = new CreateIndexRequest(idx);
					 cir.mapping(IJSONDocStoreOntology.CORE_TYPE, createMapping(idx));
			//			.settings(settings);
					 afr = getClient().admin().indices().create(cir).get();
					System.out.println("FOO "+afr.isAcknowledged());
				} catch (Exception x) {
					environment.logError(x.getMessage(), x);
					throw new RuntimeException(x);
				}
			}
		}	
	}


	@Override
	protected CountRequestBuilder prepareCount(String... indices) {
		return client.prepareCount(indices);
	}


	@Override
	protected ActionFuture<DeleteResponse> delete(DeleteRequest request) {
		return client.delete(request);
	}


	@Override
	protected GetRequestBuilder prepareGet(String index, String type, String id) {
		GetRequestBuilder result = null;
		try {
			result= client.prepareGet(index, type, id);
		} catch (Exception e) {
			environment.logError(e.getMessage(),e);
			result= client.prepareGet(index, type, id);
		}
		return result;
	}


	@Override
	protected SearchRequestBuilder prepareSearch(String... indices) {
		SearchRequestBuilder result = null;
		try {
			result =  client.prepareSearch(indices);
		} catch (Exception e) {
			environment.logError(e.getMessage(),e);
			result =  client.prepareSearch(indices);
		}
		return result;
	}


	@Override
	protected IndexRequestBuilder prepareIndex(String index, String type,
			String id) {
		IndexRequestBuilder result = null;
		try {
			result = client.prepareIndex(index, type, id);
		} catch (Exception e) {
			environment.logError(e.getMessage(),e);
			result = client.prepareIndex(index, type, id);
		}
		return result;
	}


	@Override
	public Client getClient() {
		return client;
	}



}
