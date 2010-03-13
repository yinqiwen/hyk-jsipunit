/**
 * This file is part of the hyk-jsipunit project.
 * Copyright (c) 2010 Yin QiWen <yinqiwen@gmail.com>
 *
 * Description: VarString.java 
 *
 * @author yinqiwen [ 2010-3-6 | ÏÂÎç03:00:58 ]
 *
 */
package org.hyk.sip.test.script;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import bsh.EvalError;
import bsh.Interpreter;

/**
 *
 */
public class VarString
{

	private String	format;

	@XmlValue
	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getFormat()
	{
		return format;
	}

	@XmlAttribute(name = "var")
	private String	varNames;

	public boolean containsVar()
	{
		return null != varNames;
	}
	
	public String replaceVar(Interpreter interpreter)
	{
		if(null != varNames)
		{
			try
			{
				String[] names = varNames.split(",");
				Object[] args = new Object[names.length];
				for(int i = 0; i < names.length; i++)
				{
					args[i] = interpreter.get(names[i]);
					if(null == args[i])
					{
						args[i] = names[i];
					}
				}
				interpreter.set("args", args);
				interpreter.set("format", format);
				return (String)interpreter.eval("java.lang.String.format(format, args)");
			}
			catch(EvalError e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return format;
	}
}
