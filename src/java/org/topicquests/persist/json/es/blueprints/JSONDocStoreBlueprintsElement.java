/**
 * 
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
