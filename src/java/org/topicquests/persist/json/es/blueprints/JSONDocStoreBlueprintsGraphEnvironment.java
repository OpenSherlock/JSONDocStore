/**
 * 
 */
package org.topicquests.persist.json.es.blueprints;

import java.util.*;

import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;

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
	private Graph theGraph;
	/**
	 * 
	 */
	public JSONDocStoreBlueprintsGraphEnvironment(JSONDocStoreEnvironment env) {
		environment = env;
		props = env.getProperties();
		theGraph = new JSONDocStoreBlueprintsGraph(this);
	}

	public Graph getGraph() {
		return theGraph;
	}
	
	public JSONDocStoreEnvironment getBlobStoreEnvironment() {
		return environment;
	}
}
