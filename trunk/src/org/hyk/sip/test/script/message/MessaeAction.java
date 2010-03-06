/**
 * 
 */
package org.hyk.sip.test.script.message;

import java.util.ArrayList;
import java.util.List;

import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.script.VarString;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 * 
 */
public abstract class MessaeAction extends Action
{

	protected String		request;

	protected int			response	= -1;
	
	@XmlElements(@XmlElement(name = "header"))
	protected List<VarString>	headerValues = new ArrayList<VarString>();

	protected VarString		body;
	protected List<Header>	headers		= new ArrayList<Header>();

	@XmlAttribute
	public void setRequest(String request)
	{
		if(request.equalsIgnoreCase("reinvite"))
		{
			request = Request.INVITE;
		}
		this.request = request;
	}

	@XmlAttribute
	public void setResponse(int response)
	{
		this.response = response;
	}

	@XmlElement
	public void setBody(VarString body)
	{
		this.body = body;
		
		if(null != this.body )
		{
			String[] lines = this.body.getFormat().split("\\r\\n|[\\r\\n]");
			StringBuilder buffe = new StringBuilder();
			for(int i = 0; i < lines.length; i++)
			{
				buffe.append(lines[i].trim()).append("\r\n");
			}
			this.body.setFormat(buffe.toString());
		}
	}

	public abstract void init() throws Exception;
}
