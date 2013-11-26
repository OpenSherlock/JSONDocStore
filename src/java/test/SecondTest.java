/**
 * 
 */
package test;

import org.json.simple.JSONObject;
import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;

/**
 * @author park
 *
 */
public class SecondTest {
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
	public SecondTest() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		JSONObject jo = new JSONObject();
		jo.put("id", ID);
		jo.put("type", TYPE);
		jo.put("cargo", "Now is the time for all good men to come to the aid of their country.");
		IResult rx = model.putDocument(ID, INDEX, TYPE, jo);
		 rx = model.getDocument(INDEX, null, ID);
		System.out.println(rx.hasError()+"  "+rx.getResultObject());
		//TODO
		environment.shutDown();
	}

}
