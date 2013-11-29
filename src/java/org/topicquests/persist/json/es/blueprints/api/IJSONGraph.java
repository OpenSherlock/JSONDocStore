/**
 * 
 */
package org.topicquests.persist.json.es.blueprints.api;

import java.util.Map;

import org.topicquests.common.api.IResult;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author park
 *
 */
public interface IJSONGraph extends Graph {

	/**
	 * Provide the ability to update a {@link Vertex}
	 * @param v
	 * @return
	 */
	IResult updateVertex(Vertex v);
	
	/**
	 * Provide the ability to update an {@link Edge}
	 * @param e
	 * @return
	 */
	IResult updateEdge(Edge e);
	
	/**
	 * Extended ability to create a {@link Vertex} with <code>properties</code>
	 * @param id
	 * @param properties
	 * @return
	 */
	Vertex addVertex(String id, Map<String,Object>properties);
	
	/**
	 * Extended ability to create an {@link Edge} with <code>properties</code>
	 * @param id
	 * @param outVertex
	 * @param inVertex
	 * @param label
	 * @param properties
	 * @return
	 */
	Edge addEdge(String id, Vertex outVertex, Vertex inVertex,
			String label, Map<String,Object>properties);
}
