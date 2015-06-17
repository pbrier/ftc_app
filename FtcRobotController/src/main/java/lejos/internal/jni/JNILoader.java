package lejos.internal.jni;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Simple (for the end user) native library loader. The method in JniLoader enables developers to ship native libraries in jar files which are extracted and loaded at runtime, making native loading transparent for the end-user (as long as they are on a platform supported by you). Native libraries can also be placed in java.library.path for slightly faster startup times (tens of milliseconds during JVM startup).
 *
 */
public class JNILoader
{
	private static final String NATIVEDIR_PROPERTY = "org.lejos.jniloader.basedir";
	
	private final OSInfo osinfo;
	private final String subdir;
	/**
	 * Constructor for JNILoader class.
	 * @throws IOException
	 */
	public JNILoader() throws IOException
	{
		this(null, new OSInfo());
	}
	/**
	 * Constructor for JNILoader class.
	 * @param subdir directory of loaded libraries
	 * @param info Operating System information(name and architecture properties)
	 * @throws IOException
	 */
	public JNILoader(String subdir, OSInfo info)
	{
		this.osinfo = info;
		this.subdir = subdir;
	}

	private static File getBaseFolder(Class<?> caller, String subdir) throws JNIException, URISyntaxException
	{
		String s = System.getProperty(NATIVEDIR_PROPERTY);
		if (s != null)
			return new File(s);
		
		// getName also works as expected for nested classes (returns package.Outer$Inner)
		String clname = caller.getName();
		String clpath = '/' + clname.replace('.', '/') + ".class";
		URL url = caller.getResource(clpath);
		if (url == null)
			throw new JNIException(clpath + " not found in classpath");
			
		File tmp;
		URI uri = url.toURI();
		String scheme = uri.getScheme();
		if ("file".equals(scheme))
		{
			tmp = new File(uri).getParentFile();
			for (int i = clname.indexOf('.'); i >= 0; i = clname.indexOf('.', i + 1))
			{
				tmp = tmp.getParentFile();
			}
			/* 
			 * At this point, tmp is equal to the folder which is part of the classpath
			 * In order to match the layout of the Eclipse project (bin folder and native
			 * are on the same level), we call getParentFile() one more time below.
			 * Out in the wild, pccomm should only exist as a JAR. We assume that using
			 * the pccomm classes from within Eclipse is the only case, where the classes
			 * are located in a folder instead of a JAR.  
			 */
		}
		else if ("jar".equalsIgnoreCase(scheme))
		{
			String jarpath = uri.getRawSchemeSpecificPart();
			int i = jarpath.indexOf('!');
			if (i < 0)
				throw new RuntimeException("no ! in JAR path");

			jarpath = jarpath.substring(0, i);
			tmp = new File(new URI(jarpath));
		}
		else
		{
			throw new JNIException("unknown scheme in URL "+uri);
		}
		tmp = tmp.getParentFile();
		if (subdir != null)
			tmp = new File(tmp, subdir);

		return tmp;
	}
	
	public OSInfo getOSInfo()
	{
		return this.osinfo;
	}

	public void loadLibrary(Class<?> caller, String libname) throws JNIException
	{
		File basefolder;
		try
		{
			basefolder = getBaseFolder(caller, this.subdir);
		}
		catch (URISyntaxException e)
		{
			throw new JNIException("internal error", e);
		}

		String libfile = System.mapLibraryName(libname);
		String arch = osinfo.getArch();
		String os = osinfo.getOS();
		File folder = new File(new File(basefolder, os), arch);

		// try to find libfile in basefolder/os/arch, basefolder/os, and basefolder
		for (int i = 0; i < 3; i++)
		{
			File libpath = new File(folder, libfile);
			if (libpath.exists())
			{
				String libpath2 = libpath.getAbsolutePath();
				try
				{
					System.load(libpath2);
					return;
				}
				catch (Exception e)
				{
					throw new JNIException("cannot load library " + libpath2
							+ ", architecture " + os + "/" + arch, e);
				}
				catch (UnsatisfiedLinkError e)
				{
					throw new JNIException("cannot load library " + libpath2
							+ ", architecture " + os + "/" + arch, e);
				}
			}
			folder = folder.getParentFile();
		}
		throw new JNIException("library " + libfile + " was not found in " + basefolder
				+ ", architecture " + os + "/" + arch);
	}
}
