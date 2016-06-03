package testing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	
	public static Test suite(){
		TestSuite tests = new TestSuite(AllTests.class);
		tests.addTestSuite(ControllerTest.class);
		tests.addTestSuite(PhysicsBodyTest.class);
		tests.addTestSuite(PhysicsWorldTest.class);
		return tests;
	}
}
