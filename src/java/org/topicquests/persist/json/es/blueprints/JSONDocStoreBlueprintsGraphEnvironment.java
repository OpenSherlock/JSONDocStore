/**
 * 
 */
package org.topicquests.persist.json.es.blueprints;

import java.util.*;

import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.persist.json.es.blueprints.api.IJSONGraph;

import com.tinkerpop.blueprints.Graph;

/**
 * @author park
 *
 */
public class JSONDocStoreBlueprintsGraphEnvironment {
	private JSONDocStoreEnvironment environment;
	private Map<String,Object>props;
	/** customizable model */
	private IJSONDocStoreModel model;
	private IJSONGraph theGraph;
	/**
	 * 
	 */
	public JSONDocStoreBlueprintsGraphEnvironment(JSONDocStoreEnvironment env) {
		environment = env;
		props = env.getProperties();
		theGraph = new JSONDocStoreBlueprintsGraph(this);
	}

	public IJSONGraph getGraph() {
		return theGraph;
	}
	
	public JSONDocStoreEnvironment getBlobStoreEnvironment() {
		return environment;
	}
}
