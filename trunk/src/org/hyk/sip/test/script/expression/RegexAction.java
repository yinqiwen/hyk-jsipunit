/**
 * 
 */
package org.hyk.sip.test.script.expression;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class RegexAction extends Action
{
	@XmlAttribute
    private String pattern;

	@XmlAttribute
    private String input;
	@XmlAttribute
    private String assign;
    private String[] vars;

    public int execute(SipSession session)
    {
        //Expression expression = new Expression(session.getSipSessionGroup().getVariableManager());
        Pattern p = Pattern.compile(pattern);
        Object value = null;
        try
        {
            value = interpreter.eval(input);
            if(null != value)
            {
                Matcher m = p.matcher(value.toString());
                if(m.matches())
                {
                    int varnum = m.groupCount();
                    for (int i = 0; i < varnum; i++)
                    {
                        //Variable var = session.getSipSessionGroup().getVariableManager().createVariable(vars[i]);
                        //var.setValue(m.group(i+1));
                    }
                }
                else
                {
                    throw new Exception();
                }
            }
            
        } catch (Throwable e)
        {
            e.printStackTrace();
            session.terminated("RegexAction execute failed with pattern:" + pattern + "input:" + value +"!");
        }
        return 1;
    }
    

    public void init() throws Exception
    {
        vars = assign.split(",");       
    }
    
}
