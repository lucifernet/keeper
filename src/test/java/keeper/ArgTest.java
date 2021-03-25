package keeper;



import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.timcircle.keeper.util.ArgumentUtil;


public class ArgTest {

	private static final String NEW_STRING_VALUE = "this is new value";
	private static final int NEW_INT_VALUE = 42604299;
	
	static Map<String, Object> globalArgs = new HashMap<>();
	static Map<String, Object> privateArgs = new HashMap<>();
	static {
		globalArgs.put("key1", NEW_STRING_VALUE);
		globalArgs.put("key2", NEW_INT_VALUE);
		privateArgs.put("command", "$key1");
		privateArgs.put("int_value", "$key2");
		
		Map<String, Object> childMap = new HashMap<String, Object>();
		childMap.put("child_key", "$key2");
		privateArgs.put("child", childMap);
	}	
	
	@SuppressWarnings("rawtypes")
	@Test
	public void test1() {
		Map<String, Object> result = ArgumentUtil.generateArgs(globalArgs, privateArgs);
		String value = (String)result.get("command");
		assertEquals(value, NEW_STRING_VALUE);
		
		int newIntValue = (int)result.get("int_value");
		assertEquals(newIntValue, NEW_INT_VALUE);
		
		Map child = (Map) result.get("child");
		Object obj = child.get("child_key");
		assertEquals(obj, NEW_INT_VALUE);
		
	}
}
