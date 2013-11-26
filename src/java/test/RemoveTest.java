/**
 * 
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
