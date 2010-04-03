/**
 * 
 */
package example;

import org.hyk.sip.test.HykSipUnitTestCase;

/**
 * @version 0.1.0
 * @author yinqiwen
 *
 */
public class ExampleTest extends HykSipUnitTestCase
{
	@Override
    public String[] getScriptLocations()
    {
        return new String[]{"caller.xml","callee.xml"};
    }
    
}
