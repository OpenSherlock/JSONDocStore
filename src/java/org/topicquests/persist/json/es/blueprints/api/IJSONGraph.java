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
