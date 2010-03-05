/**
 * 
 */
package org.hyk.sip.test.script.control;

import java.text.ParseException;

import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.session.SipSession;

import bsh.EvalError;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class WhileAction extends ControlAction
{
	@XmlAttribute
    private String condition;


	int endInstance;
  
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
                return endInstance + 1;
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
