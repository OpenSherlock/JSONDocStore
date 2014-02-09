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

	/**
	 * 
	 */
	public GraphUtil(JSONDocStoreBlueprintsGraph g,JSONDocStoreEnvironment e) {
		graph = g;
		jsonEnvironment = e;
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
			return  (JSONObject)new JSONParser().parse(jsonString);
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
		jsonEnvironment.logDebug("GraphUtil.jsonToVertes- "+(vertexJsonString != null));
		try {
			JSONObject jo = (JSONObject)new JSONParser().parse(vertexJsonString);
			Vertex result = new JSONDocStoreBlueprintsVertex(jo,graph);
			jsonEnvironment.logDebug("GraphUtil.jsonToVertes+ ");
			return result;
		} catch(Exception e) {
			jsonEnvironment.logError(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}
