package keeper;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.timcircle.keeper.cond.ICondition;
import com.timcircle.keeper.util.JarUtil;

public class LoadFromJarTest {

	@Test
	public void testLoad() throws ClassNotFoundException, IOException {
		String pathToJarLib = "D:/z/ldap/";
		String targetClass = "ldap.Main";
		Class<?> c = JarUtil.loadFromJar(pathToJarLib, targetClass);
		assertNotNull(c);
		assertEquals(targetClass, c.getName());
	}

	@Test
	public void testInit() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		String pathToJarLib = "D:\\z\\KeeperExt\\";
		String targetClass = "com.space4m.keeper.ext.OpenFileCountCondition";
		ICondition c = JarUtil.initFromJar(pathToJarLib, targetClass, ICondition.class);
		assertNotNull(c);
		assertEquals(targetClass, c.getClass().getName());
	}
}
