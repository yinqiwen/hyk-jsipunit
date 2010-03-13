/**
 * 
 */
package org.hyk.sip.test.script.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;

import bsh.EvalError;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 * 
 */
public class EchoAction extends Action
{
	@XmlAttribute
	private String	message;

	@XmlAttribute
	private String	var;

	public int execute(SipSession session)
	{
		String echoMessage = null;
		if(null != var)
		{
			try
			{
				String[] formats = var.split(",");
				Object[] args = new Object[formats.length];
				for(int i = 0; i < formats.length; i++)
				{
					args[i] = session.getInterpreter().get(formats[i]);
					if(null == args[i])
					{
						args[i] = formats[i];
					}
				}
				session.getInterpreter().set("args", args);
				session.getInterpreter().set("format", message);
				echoMessage = (String)session.getInterpreter().eval("java.lang.String.format(format, args)");
			}
			catch(EvalError e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			echoMessage = message;
		}

		System.out.println(echoMessage);

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
