/**
 * 
 */
package addon;

import org.hyk.sip.test.HykSipUnitTestCase;

/**
 * @version 0.1.0
 * @author yinqiwen
 *
 */
public class EchoTest extends HykSipUnitTestCase
{
	@Override
    public String[] getScriptLocations()
    {
        return new String[]{"caller.xml"};
    }
    
}
