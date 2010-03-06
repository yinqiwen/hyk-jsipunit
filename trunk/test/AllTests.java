import control.ControlTest;
import outofdlg.OutDlgTest;
import reinvite.ReinviteTest;
import cancel.CancelTest;
import example.ExampleTest;
import addon.EchoTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This file is part of the hyk-jsipunit project.
 * Copyright (c) 2010 Yin QiWen <yinqiwen@gmail.com>
 *
 * Description: AllTest.java 
 *
 * @author yinqiwen [ 2010-3-6 | ÏÂÎç07:15:27 ]
 *
 */

/**
 *
 */
public class AllTests
{
	public static Test suite() 
	{
		TestSuite suite = new TestSuite("AllTests");
		suite.addTestSuite(EchoTest.class);
		suite.addTestSuite(CancelTest.class);
		suite.addTestSuite(ControlTest.class);
		suite.addTestSuite(ExampleTest.class);
		suite.addTestSuite(OutDlgTest.class);
		suite.addTestSuite(ReinviteTest.class);
		return suite;
	}
}
