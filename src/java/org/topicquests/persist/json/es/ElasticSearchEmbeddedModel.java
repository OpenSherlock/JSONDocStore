/**
 * 
 */
package org.topicquests.persist.json.es;

import java.util.*;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.node.Node;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.api.IJSONDocStoreOntology;
import org.topicquests.persist.json.es.api.IElasticSearchModel;
import org.topicquests.util.TextFileHandler;
import org.elasticsearch.node.NodeBuilder;

/**
 * @author park
 * <p>This builds an embedded Node -- locally in /data/elasticsearch</p>
 * @see http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/index_.html
 * @see http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/get.html
 */
public class ElasticSearchEmbeddedModel extends AbstractBaseElasticSearchModel
		implements IElasticSearchModel {
	private Client client;
	private Node node;
	
	/**
	 * fillin abstract method
	 */
	@Override
	public IResult doInit() {
		IResult result = new ResultPojo();
		ImmutableSettings.Builder settings = null;
		//for settings, see org.elasticsearch.env.Environment
		settings = ImmutableSettings.settingsBuilder();
				 settings.put("node.name", "test-node");
				 settings.put("path.data", "data/");
				 settings.put("path.work", "data/work/");
				 settings.put("index.number_of_shards", 1);
				 settings.put("http.enabled", false);
				 settings.put("client.transport.ignore_cluster_name", true);
				 settings.build();
		 node = NodeBuilder.nodeBuilder()
				        .settings(settings)
				        //is client holds data
				        //setting this gets this error: node is not configured to store local location
				        // using this is a lot of trouble
				        //.client(true)
				        //leave alone defaults to "elasticsearch"
				   //     .clusterName("test-cluster")
				        //hold data
				       .data(true)
				       //look only in local JVM
				       .local(true)
				       //build and start node
				       .node();

		client = node.client();
		System.out.println("AAA-1");
		// We wait now for the yellow (or green) status
        node.client().admin().cluster().prepareHealth()
        		.setWaitForGreenStatus().execute().actionGet();
                // .setWaitForYellowStatus().execute().actionGet();
	    ClusterHealthStatus s = node.client().admin().cluster().prepareHealth().get().getStatus();
	    String color = "RED";
	    if (s.value() == (byte)0)
	    	color = "GREEN";
	    else if (s.value() == (byte)1)
	    	color = "YELLOW";
		environment.logDebug("ElasticSearchEmbeddedModel starting "+color);
		checkIndexes(result);
		node.client().admin().cluster().prepareHealth()
			.setWaitForGreenStatus().execute().actionGet(50000);
	    s = node.client().admin().cluster().prepareHealth().get().getStatus();
	    color = "RED";
	    if (s.value() == (byte)0)
	    	color = "GREEN";
	    else if (s.value() == (byte)1)
	    	color = "YELLOW";
		environment.logDebug("ElasticSearchEmbeddedModel started "+color);
		if (s.value() == (byte)2)
			throw new RuntimeException("ElasticSearchEmbeddedModel RED");
		return result;
	}
	
	/**
	 * Create given indices if needed
	 * @param result
	 */
	protected void checkIndexes(IResult result) {
		List<List<String>>indexes = (List<List<String>>)environment.getProperties().get("IndexNames");
		List<String>indices = new ArrayList<String>();
		environment.setIndices(indices);
		int len = indexes.size();
		ImmutableSettings.Builder settings = null;
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
			String mapx;
			if (!ir.isExists()) {
				try {
					settings = ImmutableSettings.settingsBuilder();
					 settings.put("path.data", "data/");
					 settings.build();
					 mapx = createMapping(idx);
								 cir = new CreateIndexRequest(idx)
						 .mapping(IJSONDocStoreOntology.CORE_TYPE, mapx)
						.settings(settings);
					 afr = getClient().admin().indices().create(cir).get();
					 environment.logDebug("ElasticSearchEmbeddedModel-1 "+afr.isAcknowledged()+" "+mapx);
				} catch (Exception x) {
					environment.logError(x.getMessage(), x);
					throw new RuntimeException(x);
				}
			}
		}	
	}
		
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IJSONDocStoreModel#shutDown()
	 */
	@Override
	public void shutDown() {
		if (!isShutDown) {
	        if (client != null) {
	            client.close();
	        }
	
	        if ((node != null) && (!node.isClosed())) {
	            node.close();
	        }
	        isShutDown = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IElasticSearchModel#getClient(java.lang.String)
	 */
	public Client getClient() {
		return client;
	}


}
