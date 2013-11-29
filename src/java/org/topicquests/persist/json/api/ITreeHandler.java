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
package org.topicquests.persist.json.api;

import java.io.Writer;
import java.util.List;

import org.topicquests.common.api.IResult;

/**
 * @author park
 * <p>A simple way to dump rooted trees</p>
 * 
 *
 */
public interface ITreeHandler extends IQueryEngine {

	/**
	 * <p>Write the JSON nodes as a List starting from the node
	 * identified by <code>rootNodeIdentifier</code></p>
	 * <p>Caller must surround the output with '[' at the beginning and
	 * ']' at the end.</p>
	 * <p>Caller must flush and close <code>out</code></p>
	 * <p>Notes: 
	 * <li>This is a recursive call</li>
	 * <li>This is used by <code>writeTypologyTree</code></li>
	 * </p>
	 * @param rootNodeIdentifier
	 * @param childProperties
	 * @param includeRoot dumps <code>rootNodeIdentifier</code>if <code>true</code>
	 * @param out
	 * @param index
	 * @param type
	 * @return
	 */
	IResult writeTree(String rootNodeIdentifier, List<String> childProperties, boolean includeRoot, Writer out, String index, String type);
	
	/**
	 * <p>A <em>typology</em> is defined as all the child nodes of
	 * a node identified by <code>treeRootNodeIdentifier</code>. This
	 * method dumps all child nodes of those nodes.</p>
	 * <p>Caller must surround the output with '[' at the beginning and
	 * ']' at the end.</p>
	 * <p>Caller must flush and close <code>out</code></p>
	 * @param treeRootNodeIdentifier
	 * @param childProperties
	 * @param out
	 * @param index
	 * @param type TODO
	 * @return
	 */
	IResult writeTypologyTree(String treeRootNodeIdentifier, List<String> childProperties, Writer out, String index, String type);
	/**
	 * Clear the visited list so this can be reused
	 */
	void clearVisitedList();
}
