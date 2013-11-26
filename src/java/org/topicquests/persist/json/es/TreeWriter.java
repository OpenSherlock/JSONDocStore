/**
 * 
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
import org.topicquests.common.api.ITopicQuestsOntology;
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
	private List<String>visited;
	private JSONParser parser = new JSONParser();
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
	public IResult writeTree(String rootNodeIdentifier, List<String> childProperties, boolean includeRoot, Writer out, String index, String type) {
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
					while (children.hasNext()) {
						theChild = children.next();
						jo = (JSONObject)parser.parse(theChild);
						//this is implementation specific from TopicQuests
						locator = (String)jo.get(ITopicQuestsOntology.LOCATOR_PROPERTY);
						if (!visited.contains(locator)) {
							if (isStarted)
								out.write(" , ");
							out.write(theChild);
							isStarted = true;
							visited.add(locator);
							//recursive call and write the node identified by <code>locator</code>
							temp = writeTree(locator, childProperties, true,out, index, type);
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
	public IResult writeTypologyTree(String treeRootNodeIdentifier, List<String> childProperties, Writer out, String index, String type) {
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
					while (children.hasNext()) {
						theChild = children.next();
						jo = (JSONObject)parser.parse(theChild);
						//this is implementation specific from TopicQuests
						locator = (String)jo.get(ITopicQuestsOntology.LOCATOR_PROPERTY);
						if (!visited.contains(locator)) {
							//We don't write the uppers, just the children
							//out.write(theChild);
							
							temp = writeTree(locator, childProperties, false,out, index, type);
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
