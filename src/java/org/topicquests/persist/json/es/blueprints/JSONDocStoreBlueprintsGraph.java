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

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.persist.json.api.IJSONDocStoreOntology;
import org.topicquests.persist.json.es.blueprints.api.IBlueprintsGraphOntology;
import org.topicquests.persist.json.es.blueprints.api.IJSONGraph;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author park
 * @see com.tinkerpop.blueprints.impl.tg.TinkerGraph
 * from which some of the code ideas are taken
 */
public  class JSONDocStoreBlueprintsGraph implements IJSONGraph {
	public static final String
		EDGE_TYPE = "EdgeType",
		VERTEX_TYPE = "VERTEX_TYPE";
	private JSONDocStoreBlueprintsGraphEnvironment environment;
	private JSONDocStoreEnvironment jsonEnvironment;
	private IJSONDocStoreModel jsonModel;
	private GraphUtil util;
	//having these here means this is an in-memory graph
	//TODO: we don't want to do that.
   // protected Map<String, Vertex> vertices = new HashMap<String, Vertex>();
   // protected Map<String, Edge> edges = new HashMap<String, Edge>();
	private JSONParser parser;
	private final String
		//hardwired into jsonblobstore-props.xml
		VERTEX_INDEX	= "vertices",
		EDGE_INDEX		= "edges",
		CORE_TYPE		= IJSONDocStoreOntology.CORE_TYPE;
    private static final Features FEATURES = new Features();
    private static final Features PERSISTENT_FEATURES;
    static {
        FEATURES.supportsDuplicateEdges = true;
        FEATURES.supportsSelfLoops = true;
        FEATURES.supportsSerializableObjectProperty = true;
        FEATURES.supportsBooleanProperty = true;
        FEATURES.supportsDoubleProperty = true;
        FEATURES.supportsFloatProperty = true;
        FEATURES.supportsIntegerProperty = true;
        FEATURES.supportsPrimitiveArrayProperty = true;
        FEATURES.supportsUniformListProperty = true;
        FEATURES.supportsMixedListProperty = true;
        FEATURES.supportsLongProperty = true;
        FEATURES.supportsMapProperty = true;
        FEATURES.supportsStringProperty = true;

        FEATURES.ignoresSuppliedIds = false;
        FEATURES.isPersistent = true; //false;
        FEATURES.isWrapper = false;

        FEATURES.supportsIndices = true;
        FEATURES.supportsKeyIndices = true;
        FEATURES.supportsVertexKeyIndex = true;
        FEATURES.supportsEdgeKeyIndex = true;
        FEATURES.supportsVertexIndex = true;
        FEATURES.supportsEdgeIndex = true;
        FEATURES.supportsTransactions = false;
        FEATURES.supportsVertexIteration = true;
        FEATURES.supportsEdgeIteration = true;
        FEATURES.supportsEdgeRetrieval = true;
        FEATURES.supportsVertexProperties = true;
        FEATURES.supportsEdgeProperties = true;
        FEATURES.supportsThreadedTransactions = false;

        PERSISTENT_FEATURES = FEATURES.copyFeatures();
        PERSISTENT_FEATURES.isPersistent = true;
    }
    
	/**
	 * 
	 */
	public JSONDocStoreBlueprintsGraph(JSONDocStoreBlueprintsGraphEnvironment env) {
		environment = env;
		jsonEnvironment = environment.getBlobStoreEnvironment();
		jsonModel = jsonEnvironment.getModel();
		parser = new JSONParser();
		util = new GraphUtil(this,jsonEnvironment);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#getFeatures()
	 */
	@Override
	public Features getFeatures() {
		return FEATURES;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#addVertex(java.lang.Object)
	 */
	@Override
	public Vertex addVertex(Object id) {
		JSONDocStoreBlueprintsVertex result = new JSONDocStoreBlueprintsVertex((String)id,this);
		IResult r = jsonModel.putDocument((String)id, VERTEX_INDEX, CORE_TYPE, result);
		return result;
	}
	@Override
	public Vertex addVertex(String id, Map<String, Object> properties) {
		JSONDocStoreBlueprintsVertex result = new JSONDocStoreBlueprintsVertex((String)id,this);
		Iterator<String>itr = properties.keySet().iterator();
		String key;
		while (itr.hasNext()) {
			key = itr.next();
			result.setProperty(key, properties.get(key));
		}
		IResult r = jsonModel.putDocument((String)id, VERTEX_INDEX, CORE_TYPE, result);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#getVertex(java.lang.Object)
	 */
	@Override
	public Vertex getVertex(Object id) {
		IResult r = jsonModel.getDocument(VERTEX_INDEX, CORE_TYPE, (String)id);
		Vertex result = null;
		if (r.getResultObject() != null) {
			result = util.jsonToVertex((String)r.getResultObject());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#removeVertex(com.tinkerpop.blueprints.Vertex)
	 */
	@Override
	public void removeVertex(Vertex vertex) {
		IResult r = jsonModel.removeDocument(VERTEX_INDEX, CORE_TYPE, (String)vertex.getId());
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#getVertices()
	 */
	@Override
	public Iterable<Vertex> getVertices() {
		return getVertices(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE,
				JSONDocStoreBlueprintsGraph.EDGE_TYPE);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#getVertices(java.lang.String, java.lang.Object)
	 */
	@Override
	public Iterable<Vertex> getVertices(String key, Object value) {
		IResult r = jsonModel.listDocumentsByKeywordProperty(VERTEX_INDEX, key, (String)value, 0, -1, CORE_TYPE);
		List<Vertex>result = new ArrayList<Vertex>();
		if (r.getResultObject() != null) {
			List<String>l = (List<String>)r.getResultObject();
			Iterator<String>itr = l.iterator();
			while(itr.hasNext())
				result.add(util.jsonToVertex(itr.next()));
		}
		
		return result;
	}


	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#addEdge(java.lang.Object, com.tinkerpop.blueprints.Vertex, com.tinkerpop.blueprints.Vertex, java.lang.String)
	 */
	@Override
	public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex,
			String label) {
		JSONDocStoreBlueprintsEdge result = new JSONDocStoreBlueprintsEdge((String)id,outVertex, inVertex, label, this);
//		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-1 "+result.getId());
//		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-2 ");
		String idx = (String)result.getId();
		IResult r = jsonModel.putDocument(idx, EDGE_INDEX, CORE_TYPE, result);
		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-3 "+r.getErrorString());
		JSONDocStoreBlueprintsVertex v = (JSONDocStoreBlueprintsVertex)outVertex;
		v.addOutEdge(result);
		examineVertex(v);
//		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-4 ");
//		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-4.1 ");
		r = jsonModel.putDocument((String)v.getId(), VERTEX_INDEX, CORE_TYPE, v);
//		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-5 "+r.getErrorString());
		v = (JSONDocStoreBlueprintsVertex)inVertex;
		v.addInEdge(result);
//		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-6 "+v.getId());
		r = jsonModel.putDocument((String)v.getId(), VERTEX_INDEX, CORE_TYPE, v);
		return result;
	}
	
	@Override
	public Edge addEdge(String id, Vertex outVertex, Vertex inVertex,
			String label, Map<String, Object> properties) {
		JSONDocStoreBlueprintsEdge result = new JSONDocStoreBlueprintsEdge((String)id,outVertex, inVertex, label, this);
		Iterator<String>itr = properties.keySet().iterator();
		String key;
		while (itr.hasNext()) {
			key = itr.next();
			result.setProperty(key, properties.get(key));
		}
		String idx = (String)result.getId();
		IResult r = jsonModel.putDocument(idx, EDGE_INDEX, CORE_TYPE, result);
		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.addEdge-3 "+r.getErrorString());
		JSONDocStoreBlueprintsVertex v = (JSONDocStoreBlueprintsVertex)outVertex;
		v.addOutEdge(result);
		examineVertex(v);
		r = jsonModel.putDocument((String)v.getId(), VERTEX_INDEX, CORE_TYPE, v);
		v = (JSONDocStoreBlueprintsVertex)inVertex;
		v.addInEdge(result);
		r = jsonModel.putDocument((String)v.getId(), VERTEX_INDEX, CORE_TYPE, v);
		return result;
	}

	void examineVertex(JSONObject v) {
		jsonEnvironment.logDebug("JSONDocStoreBlueprintsGraph.examineVertex "+v.get("id"));
		Iterator<String>itr = v.keySet().iterator();
		String key;
		while (itr.hasNext()) {
			key = itr.next();
			jsonEnvironment.logDebug("KEY: "+key);
			Object o = v.get(key);
			if (o instanceof String)
				jsonEnvironment.logDebug("VAL: "+o);
			else if (o instanceof JSONObject)
				examineVertex((JSONObject)o);
			else if (o instanceof JSONArray) {
				Iterator<Object> itx = ((JSONArray)o).iterator();
				Object x;
				while (itx.hasNext()) {
					x = itx.next();
					if (x instanceof String)
						jsonEnvironment.logDebug("VAL: "+x);
					else if (x instanceof JSONObject)
						examineVertex((JSONObject)x);
					else
						jsonEnvironment.logDebug("Val: WTF>");
				}
			}
				
		}
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#getEdge(java.lang.Object)
	 */
	@Override
	public Edge getEdge(Object id) {
		IResult r = jsonModel.getDocument(EDGE_INDEX, CORE_TYPE, (String)id);
		Edge result = null;
		if (r.getResultObject() != null) {
			result = util.jsonToEdge((String)r.getResultObject());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#removeEdge(com.tinkerpop.blueprints.Edge)
	 */
	@Override
	public void removeEdge(Edge edge) {
		IResult r = jsonModel.removeDocument(EDGE_INDEX, CORE_TYPE, (String)edge.getId());
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#getEdges()
	 */
	@Override
	public Iterable<Edge> getEdges() {
		return getEdges(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE,
				JSONDocStoreBlueprintsGraph.EDGE_TYPE);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#getEdges(java.lang.String, java.lang.Object)
	 */
	@Override
	public Iterable<Edge> getEdges(String key, Object value) {
		IResult r = jsonModel.listDocumentsByProperty(EDGE_INDEX, key, (String)value, 0, -1, CORE_TYPE);
		System.out.println("GETEDGES "+key+" "+value+" "+r);
		List<Edge>result = new ArrayList<Edge>();
		if (r.getResultObject() != null) {
			List<String>l = (List<String>)r.getResultObject();
			Iterator<String>itr = l.iterator();
			while(itr.hasNext())
				result.add(util.jsonToEdge(itr.next()));
		}	
		return result;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#query()
	 */
	@Override
	public GraphQuery query() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Graph#shutdown()
	 */
	@Override
	public void shutdown() {

	}

	@Override
	public IResult updateVertex(Vertex v) {
		return jsonModel.putDocument((String)v.getId(), VERTEX_INDEX, CORE_TYPE, ((JSONDocStoreBlueprintsVertex)v).toJSONString());
	}

	@Override
	public IResult updateEdge(Edge e) {
		return jsonModel.putDocument((String)e.getId(), VERTEX_INDEX, CORE_TYPE, ((JSONDocStoreBlueprintsEdge)e).toJSONString());
	}





}
