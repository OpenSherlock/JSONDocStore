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
