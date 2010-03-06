/**
 * 
 */
package org.hyk.sip.test.script.expression;

import java.text.ParseException;

import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;

import bsh.EvalError;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class AssertAction extends Action
{
	@XmlAttribute
    private String condition;

	public int execute(SipSession session)
    {
        try
        {
            Boolean b = (Boolean) (session.getInterpreter().eval(condition));
            if(b)
            {
                return 1;
            }
            else
            {
                session.terminated("Assert condition:" + condition + " failure!");
            }
        } 
		catch(EvalError e)
		{
			e.printStackTrace();
		} 
        return 0;
    }
    
    public void init() throws Exception
    {
        // TODO Auto-generated method stub
        
    }
    
}
