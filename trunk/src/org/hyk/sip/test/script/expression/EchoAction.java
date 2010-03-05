/**
 * 
 */
package org.hyk.sip.test.script.expression;

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
public class EchoAction extends Action
{
	@XmlAttribute
    private String message;

	public int execute(SipSession session)
    {
       
        return 1;
    }

    public void init() throws Exception
    {
        
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
