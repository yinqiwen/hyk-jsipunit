/**
 * 
 */
package org.hyk.sip.test.script.message;

import java.util.ArrayList;
import java.util.List;

import javax.sip.header.Header;
import javax.sip.message.Request;

import org.hyk.sip.test.script.Action;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.Elements;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public abstract class MessaeAction extends Action
{
	
    protected String request;
	
    protected int response = -1;
	@Elements({
	    @Element(name="header", targetType=String.class)
	  })
    protected List<String> headerValues = new ArrayList<String>();
	
    protected String body;
    protected List<Header> headers = new ArrayList<Header>();
      
    @Attribute
    public void setRequest(String request)
    {
        if(request.equalsIgnoreCase("reinvite"))
        {
            request = Request.INVITE;
        }
        this.request = request;
    }

    @Attribute
    public void setResponse(int response)
    {
        this.response = response;
    }

    @Element
    public void setBody(String body)
    {
        this.body = body;
        if(null != this.body && !this.body.trim().equals(""))
        {
            String[] lines = this.body.split("\\r\\n|[\\r\\n]");
            StringBuilder buffe = new StringBuilder();
            for (int i = 0; i < lines.length; i++)
            {
                buffe.append(lines[i].trim()).append("\r\n");
            }
            this.body = buffe.toString();
        }
    }
 
    public void addHeader(String header)
    {
        headerValues.add(header);
    }
    
    public abstract void init() throws Exception;
}
