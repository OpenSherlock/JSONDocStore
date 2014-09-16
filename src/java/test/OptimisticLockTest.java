/**
 * 
 */
package test;

import org.topicquests.common.api.IResult;
import org.topicquests.persist.json.JSONDocStoreEnvironment;
import org.topicquests.persist.json.api.IJSONDocStoreModel;
import org.topicquests.persist.json.api.IJSONDocStoreOntology;
import org.topicquests.persist.json.es.OptimisticLockUtility;

/**
 * @author park
 *
 */
public class OptimisticLockTest {
	private JSONDocStoreEnvironment environment;
	private IJSONDocStoreModel model;
	/** an initial document */
	private final String classA = "{ \"id\": \"TestA\", \"foo\": \"bar\", \""+IJSONDocStoreOntology.VERSION_PROPERTY+"\": \"39787\", \"Ta\": \"ta\" }";
	/** update that same document with a new version */
	private final String classB = "{ \"id\": \"TestA\",  \"foo\": \"bar\", \""+IJSONDocStoreOntology.VERSION_PROPERTY+"\": \"39788\", \"Ta\": \"tax\" }";
	/** an attempt to update the old document, same version -- should fail */
	private final String classC = "{  \"id\": \"TestA\", \"foo\": \"bar\", \""+IJSONDocStoreOntology.VERSION_PROPERTY+"\": \"39787\", \"Ta\": \"ty\" }";
	private final String id = "TestA";
	private final String
		INDEX = "testindex",
		TYPE  = IJSONDocStoreOntology.CORE_TYPE;
	/**
	 * 
	 */
	public OptimisticLockTest() {
		environment = new JSONDocStoreEnvironment();
		model = environment.getModel();
		IResult rx = model.putDocument(id, INDEX, TYPE, classA, true);
		System.out.println("AAA "+rx.getErrorString());
		rx = model.getDocument(INDEX, TYPE, id);
		System.out.println("BBB "+rx.getErrorString()+" | "+rx.getResultObject());
		rx =  model.putDocument(id, INDEX, TYPE, classB, true);
		System.out.println("CCC "+rx.getErrorString());
		rx = model.getDocument(INDEX, TYPE, id);
		System.out.println("DDD "+rx.getErrorString()+" | "+rx.getResultObject());
		rx =  model.putDocument(id, INDEX, TYPE, classC, true);
		System.out.println("EEE "+rx.getErrorString());
		System.out.println("FFF "+OptimisticLockUtility.getStoredVersion(rx.getErrorString()));
		environment.shutDown();		
	}

}
/**
AAA 
BBB  | { "id": "TestA", "foo": "bar", "_version": "39787", "Ta": "ta" }
CCC 
DDD  | { "id": "TestA",  "foo": "bar", "_version": "39787", "Ta": "tax" }
EEE ; OptimisticLockException 39788 39787
FFF 39788
*/
