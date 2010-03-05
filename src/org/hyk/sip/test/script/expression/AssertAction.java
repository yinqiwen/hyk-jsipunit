/**
 * 
 */
package org.hyk.sip.test.script.expression;

import java.text.ParseException;
import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;
import org.xmappr.Attribute;

import bsh.EvalError;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class AssertAction extends Action
{
	@Attribute
    private String condition;
   
    public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	public int execute(SipSession session)
    {
        try
        {
            Boolean b = (Boolean) (interpreter.eval(condition));
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
