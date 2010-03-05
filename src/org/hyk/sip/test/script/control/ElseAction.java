/**
 * 
 */
package org.hyk.sip.test.script.control;

import org.hyk.sip.test.session.SipSession;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class ElseAction extends ControlAction
{
    int fiActionDistance = -1;

   
    public int execute(SipSession session)
    {
        return fiActionDistance;
    }

    
    public void init() throws Exception
    {
        
    }
}
