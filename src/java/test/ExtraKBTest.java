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
public class ExtraKBTest {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;

	/**
	 * 
	 */
	public ExtraKBTest() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		IResult rx = 
				model.listDocumentsByProperty(BuildComplexKnowledgeBase.INDEX, 
				BuildComplexKnowledgeBase.SUBOF, 
				BuildComplexKnowledgeBase.ASSOCIATIONTYPEID, 0, -1, BuildComplexKnowledgeBase.TYPE);
		System.out.println("DONE "+rx.hasError()+" "+rx.getResultObject());
		if (rx.hasError())
			System.out.println(rx.getErrorString());
		environment.shutDown();
	}

}
