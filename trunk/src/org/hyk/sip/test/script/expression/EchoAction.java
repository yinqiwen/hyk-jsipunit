/**
 * 
 */
package org.hyk.sip.test.script.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;
import org.xmappr.Attribute;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class EchoAction extends Action
{
	@Attribute
    private String message;

    public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public int execute(SipSession session)
    {
       
        return 1;
    }

    public void init() throws Exception
    {
        System.out.println("####");
        
    }
    
    public static void main(String[] args)
    {
        String m = "Hello,$name $x $y";
        
        Pattern p = Pattern.compile("[^\\$]*((\\$[^\\s]*)[^\\$]*)*");
        Matcher ma = p.matcher(m);
        System.out.println(ma.matches());
        System.out.println(ma.group(2));
        System.out.println(ma.start(1));
        
    }
    
}
