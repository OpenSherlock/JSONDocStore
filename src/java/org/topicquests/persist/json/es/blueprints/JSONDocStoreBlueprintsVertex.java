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
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.persist.json.es.blueprints.api.IBlueprintsGraphOntology;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;

/**
 * @author park
 *
 */
public class JSONDocStoreBlueprintsVertex extends
		JSONDocStoreBlueprintsElement implements Vertex {

	/**
	 * @param env
	 */
	public JSONDocStoreBlueprintsVertex(final String stringId, final JSONDocStoreBlueprintsGraph g) {
		super(stringId, g);
		setType(JSONDocStoreBlueprintsGraph.VERTEX_TYPE);
	}

	public JSONDocStoreBlueprintsVertex(JSONObject jo, final JSONDocStoreBlueprintsGraph g) {
		super(jo,g);
	}
	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Vertex#getEdges(com.tinkerpop.blueprints.Direction, java.lang.String[])
	 */
	@Override
	public Iterable<Edge> getEdges(Direction direction, String... labels) {
		
		Object o;
        if (direction.equals(Direction.IN))
            o = getProperty(IBlueprintsGraphOntology.IN_EDGE_ID_LIST_PROPERTY_TYPE);
        else 
            o = getProperty(IBlueprintsGraphOntology.OUT_EDGE_ID_LIST_PROPERTY_TYPE);
        ArrayList<Edge>result = new ArrayList<Edge>();
        Edge jo;
        if (o != null) {
        	if (o instanceof String) {
        		jo = graph.getEdge((String)o);
        		if (checkLabels(jo,labels))
        			result.add(jo);
        	}
        	else {
        		List<String>edges = (List<String>)o;
        		Iterator<String>itr = edges.iterator();
        		while (itr.hasNext()) {
            		jo = graph.getEdge(itr.next());
            		if (checkLabels(jo,labels))
            			result.add(jo);
        		}
        	}
        }
		return result;
	}

	boolean checkLabels(Edge jo, String... labels) {
		boolean result = false;
		//In a graph, an object has just one label value
		String myLabel = (String)jo.getLabel();
		for (String lx:labels) {
			if (lx.equals(myLabel))
				return true;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Vertex#getVertices(com.tinkerpop.blueprints.Direction, java.lang.String[])
	 */
	@Override
	public Iterable<Vertex> getVertices(Direction direction, String... labels) {
		List<Vertex>result = new ArrayList<Vertex>();
		Iterable<Edge>edges = getEdges(direction,labels);
		//if direction out, get invertex
		//if direction in, getvertexout
		Iterator<Edge> itr = edges.iterator();
		Edge edg;
		while (itr.hasNext()) {
			edg = itr.next();
		    if (direction.equals(Direction.IN))
		          result.add(edg.getVertex(Direction.OUT));
		    else if (direction.equals(Direction.OUT))
		         result.add(edg.getVertex(Direction.OUT));
		    //NOTICE what else
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Vertex#query()
	 */
	@Override
	public VertexQuery query() {
		return new DefaultVertexQuery(this);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Vertex#addEdge(java.lang.String, com.tinkerpop.blueprints.Vertex)
	 */
	@Override
	public Edge addEdge(String label, Vertex inVertex) {
		// Add an outgoing edge
		Edge result = graph.addEdge(null, this, inVertex, label);
		//Note: Graph takes care of adding this edge to in and out vertices
		return result;
	}
	public void addOutEdge(JSONDocStoreBlueprintsEdge edge) {
		JSONArray edges = getListEdgeList(IBlueprintsGraphOntology.OUT_EDGE_ID_LIST_PROPERTY_TYPE);
		edges.add(edge.getId());
		super.util.logDebug("JSONDocStoreBlueprintsVertex.addOutEdge-1 "+(edges != null));
		super.setProperty(IBlueprintsGraphOntology.OUT_EDGE_ID_LIST_PROPERTY_TYPE, edges);
		//super.util.logDebug("JSONDocStoreBlueprintsVertex.addOutEdge-2 ");
	}
	
	public void addInEdge(JSONDocStoreBlueprintsEdge edge) {
		JSONArray edges = getListEdgeList(IBlueprintsGraphOntology.IN_EDGE_ID_LIST_PROPERTY_TYPE);
		edges.add(edge.getId());
		super.setProperty(IBlueprintsGraphOntology.IN_EDGE_ID_LIST_PROPERTY_TYPE, edges);
	}
	
	JSONArray getListEdgeList(String key) {
		JSONArray result = null;
		Object o = getProperty(key);
		super.util.logDebug("JSONDocStoreBlueprintsVertex.getListEdgeList "+key+" "+o);
		if (o instanceof List)
			result = (JSONArray)o;
		else {
			result = new JSONArray();
			if (o != null)
				result.add((String)o);
		}
		return result;
	}
}
