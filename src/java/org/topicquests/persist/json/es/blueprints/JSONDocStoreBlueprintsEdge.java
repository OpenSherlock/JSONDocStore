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

import org.json.simple.JSONObject;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.persist.json.es.blueprints.api.IBlueprintsGraphOntology;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author park
 *
 */
public class JSONDocStoreBlueprintsEdge extends JSONDocStoreBlueprintsElement
		implements Edge {
	   private String label;
	   private  Vertex inVertex;
	   private  Vertex outVertex;
	/**
	 * @param env
	 */
	public JSONDocStoreBlueprintsEdge(final String stringId, final Vertex outVertex, final Vertex inVertex, final String label, final JSONDocStoreBlueprintsGraph g) {
		super(stringId, g);
		super.util.logDebug("JSONDocStoreBlueprintsEdge- "+outVertex+" "+inVertex);
		setType(JSONDocStoreBlueprintsGraph.EDGE_TYPE);
		setLabel(label);
		this.label = label;
		this.inVertex = inVertex;
		//set the property with those toJSONString
		setProperty(IBlueprintsGraphOntology.IN_VERTEX_ID_PROPERTY_TYPE,inVertex.getId());
		setProperty(IBlueprintsGraphOntology.OUT_VERTEX_ID_PROPERTY_TYPE,outVertex.getId());
		this.outVertex = outVertex;
		super.util.logDebug("JSONDocStoreBlueprintsEdge+ "+this.toString());		
	}

	public JSONDocStoreBlueprintsEdge(JSONObject jo, final JSONDocStoreBlueprintsGraph g) {
		super(jo,g);
		label = (String)jo.get(ITopicQuestsOntology.LABEL_PROPERTY);
		String id = (String)getProperty(IBlueprintsGraphOntology.IN_VERTEX_ID_PROPERTY_TYPE);
		
		inVertex = graph.getVertex(id);
		id = (String)getProperty(IBlueprintsGraphOntology.OUT_VERTEX_ID_PROPERTY_TYPE);
		outVertex = graph.getVertex(id);
		
	}
	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Edge#getVertex(com.tinkerpop.blueprints.Direction)
	 */
	@Override
	public Vertex getVertex(Direction direction)
			throws IllegalArgumentException {
	       if (direction.equals(Direction.IN))
	            return this.inVertex;
	        else if (direction.equals(Direction.OUT))
	            return this.outVertex;
	        else
	            throw new IllegalArgumentException("Both directions not accepted");
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Edge#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}

}
