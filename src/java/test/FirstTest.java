/**
 * 
 */
package test;

import org.topicquests.persist.json.JSONDocStoreEnvironment;

/**
 * @author park
 *
 */
public class FirstTest {
	private JSONDocStoreEnvironment environment;
	/**
	 * 
	 */
	public FirstTest() {
		environment = new JSONDocStoreEnvironment();
		//TODO
		environment.shutDown();
	}

}
