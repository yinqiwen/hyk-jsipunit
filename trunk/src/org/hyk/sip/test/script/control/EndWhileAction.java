/**
 * 
 */
package org.hyk.sip.test.script.control;

import org.hyk.sip.test.session.SipSession;

/**
 * @version 0.1.0
 * @author yinqiwen
 *
 */
public class EndWhileAction extends ControlAction
{
    int preWhileDistance;
    
    public int execute(SipSession session)
    {
        return preWhileDistance;
    }

    public void init() throws Exception
    {
        // TODO Auto-generated method stub
        
    }
    
}
