/**
 * 
 */
package addon;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class TestAction extends Action
{
    
    public int execute(SipSession session)
    {
        System.out.println("TestAction executed!");
        return 1;
    }

    public void init() throws Exception
    {
        // TODO Auto-generated method stub
        
    }
    
}
