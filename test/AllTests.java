import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
@RunWith(Suite.class)
@Suite.SuiteClasses( 
		{EchoTest.class, 
		 CancelTest.class, 
		 ControlTest.class, 
		 ExampleTest.class, 
		 OutDlgTest.class, 
		 ReinviteTest.class})
public class AllTests
{
}
