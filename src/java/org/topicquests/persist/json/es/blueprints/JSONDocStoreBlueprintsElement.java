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

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import org.json.simple.JSONObject;
import org.topicquests.persist.json.es.blueprints.api.IBlueprintsGraphOntology;

/**
 * @author park
 *
 */
public class JSONDocStoreBlueprintsElement extends JSONObject
		implements Element {
	protected JSONDocStoreBlueprintsGraph myGraph;
	protected String id = null;
	protected final Graph graph;
	//public final String ID_PROPERTY = IBlueprintsGraphOntology.ID_PROPERTY;
	protected GraphUtil util;

	/**
	 * @param stringId
	 * 
	 */
	public JSONDocStoreBlueprintsElement(final String stringId, final JSONDocStoreBlueprintsGraph g) {
		graph = g;
		id = stringId;
		util = GraphUtil.getInstance();
		if (id == null)
			id = util.getUUID();
		util.logDebug("JSONDocStoreBlueprintsElement-1 "+stringId+" "+id);
		this.setProperty(IBlueprintsGraphOntology.ID_PROPERTY, id);
	}

	public JSONDocStoreBlueprintsElement(JSONObject jo, final JSONDocStoreBlueprintsGraph g) {
		graph = g;
		util = GraphUtil.getInstance();
		id = (String)jo.get(IBlueprintsGraphOntology.ID_PROPERTY);
		util.logDebug("JSONDocStoreBlueprintsElement-2 "+id);
		Iterator<String>itr = jo.keySet().iterator();
		String key;
		while(itr.hasNext()) {
			key = itr.next();
			this.setProperty(key, jo.get(key));
		}
		
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Element#getProperty(java.lang.String)
	 */
	@Override
	public <T> T getProperty(String key) {
		return (T) super.get(key);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Element#getPropertyKeys()
	 */
	@Override
	public Set<String> getPropertyKeys() {
		return super.keySet();
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Element#setProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setProperty(String key, Object value) {
		super.put(key, value);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Element#removeProperty(java.lang.String)
	 */
	@Override
	public <T> T removeProperty(String key) {
		return (T)super.remove(key);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Element#remove()
	 */
	@Override
	public void remove() {
        if (this instanceof Vertex)
            this.graph.removeVertex((Vertex) this);
        else
            this.graph.removeEdge((Edge) this);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.Element#getId()
	 */
	@Override
	public Object getId() {
		return this.id;
	}

}
