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
package org.topicquests.persist.json;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.nex.config.ConfigPullParser;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.persist.json.api.IQueryEngine;
import org.topicquests.persist.json.api.ITreeHandler;
import org.topicquests.persist.json.es.blueprints.JSONDocStoreBlueprintsGraphEnvironment;
import org.topicquests.util.LoggingPlatform;
import org.topicquests.util.Tracer;

/**
 * @author park
 * <p>Theoretically speaking, there should be no backside implementation
 * referenced here; everything this platform uses is customizable.</p>
 * <p>The first implementation is using ElasticSearch</p>
 */
public class JSONDocStoreEnvironment {
	public LoggingPlatform log = LoggingPlatform.getInstance("logger.properties");
	private Map<String,Object>props;
	/** customizable model */
	private IJSONDocStoreModel model;
	/** customizable query DSL */
	private IQueryEngine queryEngine;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private List<String>indices;
	private ITreeHandler treeEngine;
	private JSONDocStoreBlueprintsGraphEnvironment graphEnvironment;
	
	/*
	 * 
	 */
	public JSONDocStoreEnvironment() {
		ConfigPullParser p = new ConfigPullParser("jsonblobstore-props.xml");
		props = p.getProperties();
		String mpath = getStringProperty("Model");
		String qpath = getStringProperty("QueryEngine");
		String tpath = getStringProperty("TreeEngine");
		indices = null; // TO BE SET by implementation
		try {
			model = (IJSONDocStoreModel)Class.forName(mpath).newInstance();
			model.init(this);
			queryEngine = (IQueryEngine)Class.forName(qpath).newInstance();
			queryEngine.init(this);
			treeEngine = (ITreeHandler)Class.forName(tpath).newInstance();
			treeEngine.init(this);
		} catch (Exception e) {
			logError(e.getMessage(),e);
			throw new RuntimeException(e);
		}
		graphEnvironment = new JSONDocStoreBlueprintsGraphEnvironment(this);
	}

	public JSONDocStoreBlueprintsGraphEnvironment getGraphEnvironment() {
		return graphEnvironment;
	}
	
	/**
	 * The full system API is here
	 * @return
	 */
	public IJSONDocStoreModel getModel() {
		return model;
	}
	
	public IQueryEngine getQueryEngine() {
		return queryEngine;
	}
	
	public ITreeHandler getTreeHandler() {
		return treeEngine;
	}
	public Map<String, Object> getProperties() {
		return props;
	}
	
	public String getStringProperty(String key) {
		return (String)props.get(key);
	}
	
	/**
	 * Dump an entire tree structure into <code>out</code>
	 * @param rootNodeIdentifier
	 * @param identityKey
	 * @param childProperties
	 * @param includeRoot
	 * @param out
	 * @param index
	 * @param type
	 * @return
	 */
	public IResult dumpTree(String rootNodeIdentifier, String identityKey, List<String> childProperties, boolean includeRoot, Writer out, String index, String type) {
		ITreeHandler h = getTreeHandler();
		return h.writeTree(rootNodeIdentifier, identityKey, childProperties, includeRoot, out, index, type);
	}
	
	public void shutDown() {
		//sanity view
		System.out.println(indices);
		model.shutDown();
	}

	/**
	 * Required to be set by implementation
	 * @param i
	 */
	public void setIndices(List<String>i) {
		indices = i;
	}
	
	/**
	 * Might be used by external routines to examine
	 * contents of all or some indices
	 * @return
	 */
	public List<String>getIndices() {
		return indices;
	}
	/////////////////////////////
	// Utilities
	public void logDebug(String msg) {
		log.logDebug(msg);
	}
	
	public void logError(String msg, Exception e) {
		log.logError(msg,e);
	}
	
	public void record(String msg) {
		log.record(msg);
	}

	public Tracer getTracer(String name) {
		return log.getTracer(name);
	}
}
