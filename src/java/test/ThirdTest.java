/**
 * 
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
