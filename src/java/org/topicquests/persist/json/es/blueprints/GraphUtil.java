/**
 * 
 */
package org.topicquests.persist.json.es.blueprints;

import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.topicquests.persist.json.JSONDocStoreEnvironment;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author park
 *
 */
public class GraphUtil {
	private static GraphUtil instance;
	
	private JSONDocStoreBlueprintsGraph graph;
	private JSONDocStoreEnvironment jsonEnvironment;
	private JSONParser parser;

	/**
	 * 
	 */
	public GraphUtil(JSONDocStoreBlueprintsGraph g,JSONDocStoreEnvironment e) {
		graph = g;
		jsonEnvironment = e;
		parser = new JSONParser();
		instance = this;
	}
	
	public void logDebug(String msg) {
		jsonEnvironment.logDebug(msg);
	}
	
	public String getUUID() {
		UUID x = UUID.randomUUID();
		return x.toString();
	}
	
	public static GraphUtil getInstance() {
		return instance;
	}

	/**
	 * Can throw RuntimeException if parsing fails
	 * @param jsonString
	 * @return
	 */
	public JSONObject jsonToJSONObject(String jsonString) {
		try {
			return  (JSONObject)parser.parse(jsonString);
		} catch(Exception e) {
			jsonEnvironment.logError(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public Edge jsonToEdge(String edgeJsonString) {
			JSONObject jo = jsonToJSONObject(edgeJsonString);
			return JsonObjectToEdge(jo);
	}
	
	public Edge JsonObjectToEdge(JSONObject jo) {
		return new JSONDocStoreBlueprintsEdge(jo,graph);
	}
	
	
	public Vertex jsonToVertex(String vertexJsonString) {
		jsonEnvironment.logDebug("GraphUtil.jsonToVertes- "+vertexJsonString);
		try {
			JSONObject jo = (JSONObject)parser.parse(vertexJsonString);
			Vertex result = new JSONDocStoreBlueprintsVertex(jo,graph);
			jsonEnvironment.logDebug("GraphUtil.jsonToVertes+ "+result);
			return result;
		} catch(Exception e) {
			jsonEnvironment.logError(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}
