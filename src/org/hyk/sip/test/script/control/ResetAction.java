/**
 * 
 */
package org.hyk.sip.test.script.control;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class ResetAction extends Action
{
    
    public int execute(SipSession session)
    {
        session.reset();
        return 0;
    }
    

    public void init() throws Exception
    {
        // TODO Auto-generated method stub
        
    }
    
}
