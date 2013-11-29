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

import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.persist.json.es.blueprints.api.IJSONGraph;

import com.tinkerpop.blueprints.Graph;

/**
 * @author park
 *
 */
public class JSONDocStoreBlueprintsGraphEnvironment {
	private JSONDocStoreEnvironment environment;
	private Map<String,Object>props;
	/** customizable model */
	private IJSONDocStoreModel model;
	private IJSONGraph theGraph;
	/**
	 * 
	 */
	public JSONDocStoreBlueprintsGraphEnvironment(JSONDocStoreEnvironment env) {
		environment = env;
		props = env.getProperties();
		theGraph = new JSONDocStoreBlueprintsGraph(this);
	}

	public IJSONGraph getGraph() {
		return theGraph;
	}
	
	public JSONDocStoreEnvironment getBlobStoreEnvironment() {
		return environment;
	}
}
