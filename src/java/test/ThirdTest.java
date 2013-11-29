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
import org.topicquests.persist.json.es.SimpleElasticSearchQueryEngine;

/**
 * @author park
 *
 */
public class ThirdTest {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;
	private SimpleElasticSearchQueryEngine queryEngine;
	private final String
	ID	= "MyFirstDoc", //My*Doc",
	INDEX = "testindex",
	TYPE 	= "foo";

	/**
	 * 
	 */
	public ThirdTest() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		queryEngine = (SimpleElasticSearchQueryEngine)environment.getQueryEngine();
		
		IResult rx = queryEngine.wildCardQuery(INDEX, TYPE, ID);
	//	IResult rx = model.getDocument(INDEX, TYPE, ID+"*");
		System.out.println(rx.hasError()+"  "+rx.getResultObject());
		if (rx.hasError())
			System.out.println(rx.getErrorString());
		environment.shutDown();

		
	}

}
