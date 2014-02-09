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
package org.topicquests.persist.json.es;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.topicquests.common.ResultPojo;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.persist.json.api.ITreeHandler;

/**
 * @author park
 *
 */
public class TreeWriter implements ITreeHandler {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;
	//NOT thread safe: need new instance
	private List<String>visited;
	private boolean isStarted = false;

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.IQueryEngine#init(org.topicquests.persist.json.JSONDocStoreEnvironment)
	 */
	@Override
	public IResult init(JSONDocStoreEnvironment env) {
		environment = env;
		IResult result = new ResultPojo();
		model = environment.getModel();
		visited = new ArrayList<String>();
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.ITreeHandler#writeTree(java.lang.String, java.io.Writer)
	 */
	@Override
	public IResult writeTree(String rootNodeIdentifier, String identityKey, List<String> childProperties, boolean includeRoot, Writer out, String index, String type) {
		IResult result = new ResultPojo();
		try {
			out.write("[ ");
			IResult r = _doWriteTree(rootNodeIdentifier,identityKey, childProperties, includeRoot, out, index, type);
			if (r.hasError())
				result.addErrorString(r.getErrorString());
			out.write(" ]");
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	IResult _doWriteTree(String rootNodeIdentifier, String identityKey, List<String> childProperties, boolean includeRoot, Writer out, String index, String type) {
		environment.logDebug("TreeWriter.writeTree- "+rootNodeIdentifier+" "+childProperties);
		IResult result = new ResultPojo();
		JSONObject jo;
		IResult temp;
		String theChild;
		try {
			if (includeRoot && !visited.contains(rootNodeIdentifier)) {
				temp = model.getDocument(index, type, rootNodeIdentifier);
				if (temp.hasError()) {
					result.addErrorString(temp.getErrorString());
					return result;
				}
				theChild = (String)temp.getResultObject();
				if (isStarted)
					out.write(" , ");
				out.write(theChild);
				isStarted = true;
				visited.add(rootNodeIdentifier);
				
			}
			Iterator<String>keys = childProperties.iterator();
			String key;
			List<String> snappers;
			Iterator<String>children;
			String locator;
			while (keys.hasNext()) {
				//exit on an error
				if (result.hasError())
					break;
				key = keys.next();
				temp = model.listDocumentsByProperty(index, 
									key, rootNodeIdentifier, 0, -1, type);
				snappers = (List<String>)temp.getResultObject();
				environment.logDebug("TreeWriter.writeTree-1 "+rootNodeIdentifier+" "+key+" "+snappers);
				if (temp.hasError())
					result.addErrorString(temp.getErrorString());
				if (snappers != null && !snappers.isEmpty()) {
					children = snappers.iterator();
					JSONParser parser = new JSONParser();
					while (children.hasNext()) {
						theChild = children.next();
						jo = (JSONObject)parser.parse(theChild);
						//this is implementation specific from TopicQuests
						locator = (String)jo.get(identityKey);
						if (!visited.contains(locator)) {
							if (isStarted)
								out.write(" , ");
							out.write(theChild);
							isStarted = true;
							visited.add(locator);
							//recursive call and write the node identified by <code>locator</code>
							temp = _doWriteTree(locator, identityKey, childProperties,true, out, index, type);
							if (temp.hasError())
								result.addErrorString(temp.getErrorString());
						}
					}
				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.persist.json.api.ITreeHandler#writeTypologyTree(java.lang.String, java.io.Writer)
	 */
	@Override
	public IResult writeTypologyTree(String treeRootNodeIdentifier, String identityKey, List<String> childProperties, Writer out, String index, String type) {
		IResult result = new ResultPojo();
		try {
			out.write("[ ");
			IResult r = doWriteTypologyTree(treeRootNodeIdentifier,identityKey, childProperties, out, index, type);
			if (r.hasError())
				result.addErrorString(r.getErrorString());
			out.write(" ]");
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	IResult doWriteTypologyTree(String treeRootNodeIdentifier, String identityKey, List<String> childProperties, Writer out, String index, String type) {
		environment.logDebug("TreeWriter.writeTypology- "+treeRootNodeIdentifier+" "+childProperties);
		IResult result = new ResultPojo();
		try {
			Iterator<String>keys = childProperties.iterator();
			IResult temp;
			String key;
			List<String> snappers;
			Iterator<String>children;
			String theChild;
			JSONObject jo;
			String locator;
			while (keys.hasNext()) {
				//exit on an error
				if (result.hasError())
					break;
				key = keys.next();
				temp = model.listDocumentsByProperty(index, 
						key, treeRootNodeIdentifier, 0, -1, type);
				snappers = (List<String>)temp.getResultObject();
				if (temp.hasError()) {
					result.addErrorString(temp.getErrorString());
					return result;
				}
				if (snappers != null && !snappers.isEmpty()) {
					children = snappers.iterator();
					JSONParser parser = new JSONParser();
					while (children.hasNext()) {
						theChild = children.next();
						jo = (JSONObject)parser.parse(theChild);
						locator = (String)jo.get(identityKey);
						if (!visited.contains(locator)) {
							//We don't write the uppers, just the children
							//out.write(theChild);
							
							temp = _doWriteTree(locator, identityKey, childProperties,false, out, index, type);
							visited.add(locator);
							if (temp.hasError())
								result.addErrorString(temp.getErrorString());
						}
					}
				}
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}

	@Override
	public void clearVisitedList() {
		isStarted = false;
		visited.clear();
	}

}
