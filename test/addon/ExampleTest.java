/**
 * 
 */
package addon;

import java.util.HashMap;
import java.util.Map;

import org.hyk.sip.test.HykSipUnitTestCase;
import org.hyk.sip.test.script.Action;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class ExampleTest extends HykSipUnitTestCase
{

    protected Map<String, Class<? extends Action>> getAddonActions()
    {
        Map<String, Class<? extends Action>> map = new HashMap<String, Class<? extends Action>>();
        map.put("testAction", TestAction.class);
        return map;
    }
    
    public String[] getScriptLocations()
    {
        return new String[]{"caller.xml"};
    }
    
}
