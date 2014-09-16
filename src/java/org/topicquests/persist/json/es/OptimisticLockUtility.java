/**
 * 
 */
package org.topicquests.persist.json.es;

import org.topicquests.persist.json.api.IJSONDocStoreOntology;

/**
 * @author park
 *
 */
public class OptimisticLockUtility {

	
	/**
	 * Return <code>true</code> if <code>errorString</code> contains the
	 * optimistic lock message
	 * @param errorString
	 * @return
	 */
	public static boolean isOptimisticLockError(String errorString) {
		return (errorString.indexOf(IJSONDocStoreOntology.OPTIMISTIC_LOCK_EXCEPTION) > -1);
	}
	
	/**
	 * Returns the version number of the stored object based on <code>errorString</code>
	 * @param errorString
	 * @return can return <code>null</code>
	 */
	public static String getStoredVersion(String errorString) {
		String result = null;
		String x = errorString;
		int where = errorString.indexOf(IJSONDocStoreOntology.OPTIMISTIC_LOCK_EXCEPTION);
		if (where > -1) {
			where += IJSONDocStoreOntology.OPTIMISTIC_LOCK_EXCEPTION.length();
			x = x.substring(where).trim();
			String [] y = x.split(" ");
			result = y[0];
		}
		return result;
	}
}
