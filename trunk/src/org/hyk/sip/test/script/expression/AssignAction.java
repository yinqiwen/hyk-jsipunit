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
public class AssignAction extends Action
{
	@XmlAttribute
    private String expression;
    

	public int execute(SipSession session)
    {
        //expr.setExpression(expression);
        try
        {
        	interpreter.eval(expression);
        } 
		catch(EvalError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return 1;
    }

    public void init() throws Exception
    {
        // TODO Auto-generated method stub
        
    }
    
}
