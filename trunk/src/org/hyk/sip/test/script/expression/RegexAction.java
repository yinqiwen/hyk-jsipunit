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
        String value = null;
        try
        {
        	if(null != session.getInterpreter().get(input))
        	{
        		value = session.getInterpreter().get(input).toString();
        	}
        	else
        	{
        		value = input;
        	}
            if(null != value)
            {
                Matcher m = p.matcher(value);
                if(m.matches())
                {
                    int varnum = m.groupCount();
                    String[] assn = assign.split(",");
                    for (int i = 0; i < varnum; i++)
                    {
                    	String pv = m.group(i+1);
                    	session.getInterpreter().set(assn[i], pv);
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
