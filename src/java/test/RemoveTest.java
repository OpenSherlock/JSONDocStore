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
package test;

import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;

/**
 * @author park
 *
 */
public class RemoveTest {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;
	private final String
			ID	= "MyFirstDoc", //"MySecondDoc",
			INDEX = "testindex",
			//cannot be empty
			TYPE 	= "foo";

	/**
	 * 
	 */
	public RemoveTest() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		IResult rx = model.removeDocument(INDEX, TYPE, ID);
		System.out.println("DONE "+rx.getErrorString());
		environment.shutDown();
	}

}
