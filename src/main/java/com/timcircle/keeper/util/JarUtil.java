package com.timcircle.keeper.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtil {

	public static <T> T initFromJar(String pathToJarLib, String targetClass, Class<T> clazz)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		Class<?> c = loadFromJar(pathToJarLib, targetClass);
		if (clazz.isAssignableFrom(c)) {
			Object instance = c.newInstance();
			return clazz.cast(instance);
		}
		throw new ClassCastException();
	}

	public static Class<?> loadFromJar(String pathToJarLib, String targetClass)
			throws IOException, ClassNotFoundException {
		File jarlibs = new File(pathToJarLib);
		if (!jarlibs.exists()) {
			throw new FileNotFoundException("Directory not exists : " + pathToJarLib);
		}
		for (File file : jarlibs.listFiles()) {
			if (!file.isFile())
				continue;
			if (!file.getName().endsWith(".jar"))
				continue;

			JarFile jarFile = new JarFile(file);

			try {
				Enumeration<JarEntry> e = jarFile.entries();

				URL[] urls = { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
				URLClassLoader cl = URLClassLoader.newInstance(urls);

				while (e.hasMoreElements()) {
					JarEntry je = e.nextElement();
					if (je.isDirectory() || !je.getName().endsWith(".class")) {
						continue;
					}
					// -6 because of .class
					String className = je.getName().substring(0, je.getName().length() - 6);
					className = className.replace('/', '.');
					if (!className.equals(targetClass))
						continue;
					Class<?> c = cl.loadClass(className);
					return c;
				}
			} finally {
				jarFile.close();
			}
		}
		throw new ClassNotFoundException();
	}
}
