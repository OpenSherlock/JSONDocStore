/**
 * 
 */
package org.topicquests.persist.json.es.blueprints.api;

/**
 * @author park
 *
 */
public interface IBlueprintsGraphOntology {
	public static final String
		//blueprints uses ID property
		ID_PROPERTY 								= "id",
		IN_VERTEX_ID_PROPERTY_TYPE					="InVertexId",
		OUT_VERTEX_ID_PROPERTY_TYPE					="OutVertexId",
		IN_EDGE_ID_LIST_PROPERTY_TYPE				= "inEdgeIdList",
		OUT_EDGE_ID_LIST_PROPERTY_TYPE				= "outEdgeIdList";

}
