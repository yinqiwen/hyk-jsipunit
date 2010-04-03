/**
 * 
 */
package cancel;

import org.hyk.sip.test.HykSipUnitTestCase;

/**
 * @version 0.1.0
 * @author yinqiwen
 *
 */
public class CancelTest extends HykSipUnitTestCase
{
	@Override
    public String[] getScriptLocations()
    {
        return new String[]{"callee.xml", "caller.xml"};
    }   
}
