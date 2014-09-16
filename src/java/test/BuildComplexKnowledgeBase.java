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

import org.json.simple.JSONObject;
import org.topicquests.common.api.IResult;
import org.topicquests.common.api.ITopicQuestsOntology;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.persist.json.api.IJSONDocStoreOntology;

/**
 * @author park
 * <p>Here, we will build a complex (but small) knowledgebase in
 * <code>testindex</code> which will be used in other tests to
 * develop query methods.</p>
 * <p>Obviously, this KB is targeted at algorithms suited for building
 * topic maps.</p>
 */
public class BuildComplexKnowledgeBase {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;
	//So we can use these in other tests
	public static final String
			//Index-related
			INDEX 				= "testindex",
			//cannot be empty
			TYPE 				= IJSONDocStoreOntology.CORE_TYPE,
			//Tree root
			ROOTID 				= "TheRoot",
			//Types
			MOLECULETYPEID		= "MoleculeType",
			CO2ID 				= "CO2",
			CLIMATECHANGEID 	= "ClimateChange",
			ASSOCIATIONTYPEID	= "AssociationType",
			PROCESSTYPEID		= "ProcessType",
			ATMOSPROCESSID		= "AtmosphericProcess",
			CAUSALID			= "CausalAssociation",
			//Property Keys
			LOCATOR 			= ITopicQuestsOntology.LOCATOR_PROPERTY,
			SUBOF				= ITopicQuestsOntology.SUBCLASS_OF_PROPERTY_TYPE,
			INSTANCEOF 			=  ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE,
			ASSOCIATIONS 		= ITopicQuestsOntology.TUPLE_LIST_PROPERTY,
			SOURCETOPIC			= "sourceTopic",
			TARGETTOPIC			= "targetTopic",
			NAMESTRING			= "nameString";

	/**
	 * 
	 */
	public BuildComplexKnowledgeBase() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		IResult rx = null;
		JSONObject jo = new JSONObject();
		//Build a root object
		jo.put(BuildComplexKnowledgeBase.LOCATOR,BuildComplexKnowledgeBase.ROOTID);
		jo.put(NAMESTRING, "KnowledgeBase Tree Root");
		rx = model.putDocument(ROOTID, INDEX, TYPE, jo, false);
		displayErrorMessage(1,rx);
		//Build the types
		//AssociationType
		jo.clear();
		jo.put(LOCATOR, ASSOCIATIONTYPEID);
		jo.put(SUBOF, ROOTID);
		jo.put(NAMESTRING, "Association Type");
		rx = model.putDocument(ASSOCIATIONTYPEID, INDEX, TYPE, jo, false);
		displayErrorMessage(2,rx);
		//Causal AssociationType
		jo.clear();
		jo.put(LOCATOR, CAUSALID);
		jo.put(SUBOF, ASSOCIATIONTYPEID);
		jo.put(NAMESTRING, "Causal Association Type");
		rx = model.putDocument(CAUSALID, INDEX, TYPE, jo, false);
		displayErrorMessage(3,rx);
		//MoleculeType
		jo.clear();
		jo.put(LOCATOR, MOLECULETYPEID);
		jo.put(SUBOF, ROOTID);
		jo.put(NAMESTRING, "Molecule Type");
		rx = model.putDocument(MOLECULETYPEID, INDEX, TYPE, jo, false);
		displayErrorMessage(4,rx);
		//ProcessType
		jo.clear();
		jo.put(LOCATOR, PROCESSTYPEID);
		jo.put(SUBOF, ROOTID);
		jo.put(NAMESTRING, "Process Type");
		rx = model.putDocument(PROCESSTYPEID, INDEX, TYPE, jo, false);
		displayErrorMessage(5,rx);
		//ProcessType
		jo.clear();
		jo.put(LOCATOR, ATMOSPROCESSID);
		jo.put(SUBOF, PROCESSTYPEID);
		jo.put(NAMESTRING, "Atmospheric Process");
		rx = model.putDocument(ATMOSPROCESSID, INDEX, TYPE, jo, false);
		displayErrorMessage(6,rx);
		//CO2
		JSONObject cox = new JSONObject();
		cox.put(LOCATOR, CO2ID);
		cox.put(SUBOF, MOLECULETYPEID);
		cox.put(NAMESTRING, "Carbon Dioxide");
		rx = model.putDocument(CO2ID, INDEX, TYPE, cox, false);
		displayErrorMessage(7,rx);
		//Climate Change
		JSONObject ccx = new JSONObject();
		ccx.put(LOCATOR, CLIMATECHANGEID);
		ccx.put(SUBOF, ATMOSPROCESSID);
		ccx.put(NAMESTRING, "Climate Change");
		rx = model.putDocument(CLIMATECHANGEID, INDEX, TYPE, ccx, false);
		displayErrorMessage(8,rx);
		//Create an association: co2 cause climate change
		String ASSOCIATION_ID = CO2ID+"."+CAUSALID+"."+CLIMATECHANGEID;
		jo.clear();
		jo.put(LOCATOR, ASSOCIATION_ID);
		jo.put(INSTANCEOF, ASSOCIATIONTYPEID);
		jo.put(SOURCETOPIC, CO2ID);
		jo.put(TARGETTOPIC, CLIMATECHANGEID);
		jo.put(NAMESTRING, "CO2 causes Climate Change");
		rx = model.putDocument(ASSOCIATION_ID, INDEX, TYPE, jo, false);
		displayErrorMessage(9,rx);
		//now wire the association
		cox.put(ASSOCIATIONS, ASSOCIATION_ID);
		rx = model.putDocument(CO2ID, INDEX, TYPE, cox, false);
		displayErrorMessage(10,rx);
		ccx.put(ASSOCIATIONS, ASSOCIATION_ID);
		rx = model.putDocument(CLIMATECHANGEID, INDEX, TYPE, ccx, false);
		displayErrorMessage(11,rx);
		rx = model.getDocument(INDEX, TYPE, ASSOCIATION_ID);
		//now fetch something
		displayErrorMessage(10,rx);
		System.out.println(rx.getResultObject());
		
		environment.shutDown();		
	}
	
	void displayErrorMessage(int which, IResult rx) {
		System.out.println(which+" "+rx.hasError());
		if (rx.hasError())
			System.out.println(rx.getErrorString());
	}
// {"locator":"CO2.CausalAssociation.ClimateChange","intanceOf":"associationType","nameString":"CO2 causes Climate Change","sourceTopic":"CO2","targetTopic":"ClimateChange"}

}
