/**
 * 
 */
package org.hyk.sip.test.script.control;

import java.text.ParseException;

import org.hyk.sip.test.session.SipSession;
import org.xmappr.Attribute;

import bsh.EvalError;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class IfAction extends ControlAction
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

	int elseActionDistance = -1;
    int fiActionDistance = -1;

    public int execute(SipSession session)
    {
        //Expression expression = new Expression(session.getSipSessionGroup().getVariableManager());
        //expression.setExpression(condition);
        try
        {
            Boolean b = (Boolean) (interpreter.eval(condition));
            if(b)
            {
                return 1;
            }
            else
            {
                if(-1 != elseActionDistance)
                {
                    return elseActionDistance + 1;
                }
                else
                {
                    return fiActionDistance;
                }
            }
        }
        catch(EvalError e)
        {
        	e.printStackTrace();
        }
        return 1;
    }
   
    public void init() throws Exception
    {
        // TODO Auto-generated method stub
        
    }
}
