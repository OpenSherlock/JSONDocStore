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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * @author park
 *
 */
public class FirstKBTest {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;

	/**
	 * 
	 */
	public FirstKBTest() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		IResult rx = null;
		//Fetch all the subclasses of the Tree Root
		rx = model.listDocumentsByProperty(BuildComplexKnowledgeBase.INDEX, 
				BuildComplexKnowledgeBase.SUBOF, 
				BuildComplexKnowledgeBase.ROOTID, 0, -1, 
				BuildComplexKnowledgeBase.TYPE);
		displayErrorMessage(1,rx);
		System.out.println(rx.getResultObject());
		rx = model.listDocumentsByProperty(BuildComplexKnowledgeBase.INDEX, 
				BuildComplexKnowledgeBase.INSTANCEOF, 
				BuildComplexKnowledgeBase.ASSOCIATIONTYPEID, 0, -1, 
				BuildComplexKnowledgeBase.TYPE);
		displayErrorMessage(2,rx);
		System.out.println(rx.getResultObject());
		//fetch the association topic based on a fuzzy query
		String fuzzyLox = BuildComplexKnowledgeBase.CO2ID+"."+BuildComplexKnowledgeBase.CAUSALID+"*";
		String query = "{\"wildcard\": {\""+
				BuildComplexKnowledgeBase.LOCATOR+"\": \""+fuzzyLox+"\"}}";
		//query2 from http://stackoverflow.com/questions/16933800/elasticsearch-how-to-use-multi-match-with-wildcard
		//fails entirely
		//Q {"query": {"query_string": {"query": "CO2.CausalAssociation*","fields": ["locator"]}}}
		String query2 = "{\"query\": {"+
				"\"query_string\": {"+
				"\"query\": \""+fuzzyLox+"\","+
				"\"fields\": [\""+BuildComplexKnowledgeBase.LOCATOR+"\"]}}}";
		//query3 is query2 enhanced from http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html
		//fails entirely
		//Q {"query": {"query_string": {"analyze_wildcard":"true", "query": "CO2.CausalAssociation*", "fields": ["locator"]}}}
		String query3 = "{\"query\": {"+
				"\"query_string\": {"+
				"\"analyze_wildcard\":\"true\", "+
				"\"query\": \""+fuzzyLox+"\", "+
				"\"fields\": [\""+BuildComplexKnowledgeBase.LOCATOR+"\"]}}}";
		//Query4 is query2 with fields removed
		// {"query": {"query_string": {"query": "CO2.CausalAssociation*"}}}
		// QueryParsingException[[testindex] No query registered for [query]];
		String query4 = "{\"query\": {"+
				"\"query_string\": {"+
				"\"query\": \""+fuzzyLox+"\"}}}";
		
		//http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/query-dsl-queries.html
		//{"wildcard" : { "locator" : {"wildcard" : "CO2.CausalAssociation*" }}}
		//found nothing -- starting to think we are missing field definitions or something
	//	QueryBuilder qb = QueryBuilders.wildcardQuery(BuildComplexKnowledgeBase.LOCATOR, fuzzyLox);
	//	String query5 = qb.toString();
	//	System.out.println("Q "+query5);
		rx = model.listDocumentsByWildcardPropertyValue(BuildComplexKnowledgeBase.INDEX, BuildComplexKnowledgeBase.LOCATOR, fuzzyLox, 0, -1,  BuildComplexKnowledgeBase.TYPE); //(BuildComplexKnowledgeBase.INDEX, qb, 0, -1, BuildComplexKnowledgeBase.TYPE);

		
		
		//rx = model.getDocumentByProperty(BuildComplexKnowledgeBase.INDEX, 
		//		BuildComplexKnowledgeBase.LOCATOR, fuzzyLox, 
		//		BuildComplexKnowledgeBase.TYPE);
		displayErrorMessage(3,rx);
		System.out.println(rx.getResultObject());
		environment.shutDown();
	}
	void displayErrorMessage(int which, IResult rx) {
		System.out.println(which+" "+rx.hasError());
		if (rx.hasError())
			System.out.println(rx.getErrorString());
	}
//[{"locator":"MoleculeType","subOf":"TheRoot","nameString":"Molecule Type"}, 
//	{"locator":"ProcessType","subOf":"TheRoot","nameString":"Process Type"}, 
//	{"locator":"AssociationType","subOf":"TheRoot","nameString":"Association Type"}]
//FUZZY Query failed QUERY = {"match": {"locator": "CO2.CausalAssociation*"}}
//[{"locator":"CausalAssociation","subOf":"AssociationType","nameString":"Causal Association Type"}, {"locator":"CO2","subOf":"MoleculeType","nameString":"Carbon Dioxide"}, {"locator":"CO2.CausalAssociation.ClimateChange","intanceOf":"associationType","nameString":"CO2 causes Climate Change","sourceTopic":"CO2","targetTopic":"ClimateChange"}]
//FUXXY query failed [] QUERY = {"fuzzy": {"locator": "CO2.CausalAssociation*"}
	// and {"fuzzy": {"locator": "CO2.CausalAssociation"}}
}
