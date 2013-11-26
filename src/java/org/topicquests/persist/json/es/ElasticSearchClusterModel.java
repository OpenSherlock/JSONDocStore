/**
 * 
 */
package org.topicquests.persist.json.es;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
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
			environment.logDebug("ElasticSearchClusterModel.doInit "+name+" "+port);
			client.addTransportAddress(new InetSocketTransportAddress(name, Integer.parseInt(port)));
		}
		// We wait now for the yellow (or green) status
		System.out.println("AAA-1");
//		client.admin().cluster().prepareHealth()
//        	.setWaitForYellowStatus().execute().actionGet();
		System.out.println("AAA-2 "+client);
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
			ir = ib.get("1000");
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
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IElasticSearchModel#getClient(java.lang.String)
	 */
	public Client getClient() {
		return client;
	}



}
