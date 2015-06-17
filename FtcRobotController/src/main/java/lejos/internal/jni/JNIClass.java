package lejos.internal.jni;

/**
 * @summary Interface for simple native library loader.
 *
 */
public interface JNIClass
{
	/**
	 * Return false, if the 
	 * @param jnil instance of JNILoader
	 * @return false, if the class is not suitable for this platform
	 * @throws JNIException if something went wrong loading the JNI library
	 */
	boolean initialize(JNILoader jnil) throws JNIException;
}
