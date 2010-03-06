package org.hyk.sip.test.script.message;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.script.VarString;
import org.hyk.sip.test.session.SipSession;

import bsh.EvalError;


public class SendAction extends MessaeAction
{
	@XmlAttribute
    private boolean reliable;
	@XmlAttribute
    private boolean dialog = true;


	private List<Header> headers = new ArrayList<Header>();
    private List<VarString> headerVars = new LinkedList<VarString>();
    
    public void init() throws Exception
    {
        HeaderFactory headerFactory = SipFactory.getInstance().createHeaderFactory();
        for (int i = 0; i < headerValues.size(); i++)
        {
        	VarString varHeader = headerValues.get(i);
        	
        	if(varHeader.containsVar())
        	{
        		headerVars.add(varHeader);
        	}
        	else
        	{
				String header = varHeader.getFormat().trim();
				int index = header.indexOf(':');
				String name = header.substring(0, index).trim();
				String value = header.substring(index + 1).trim();
				Header h = headerFactory.createHeader(name, value);
				headers.add(h);
        	}
        }
    }

    private List<Header> getAddedHeaders(SipSession session) throws PeerUnavailableException, ParseException
    {
        if (headerVars.isEmpty())
        {
            return headers;
        } 
        List<Header> hs = new ArrayList<Header>();
        HeaderFactory headerFactory = SipFactory.getInstance()
                .createHeaderFactory();
        hs.addAll(headers);
        for (int i = 0; i < headerVars.size(); i++)
        {
        	String header = headerVars.get(i).replaceVar(session.getInterpreter());
        	int index = header.indexOf(':');
			String name = header.substring(0, index).trim();
			String value = header.substring(index + 1).trim();
			Header h = headerFactory.createHeader(name, value);
            hs.add(h);      
        }
        //headerVars.clear();
        return hs;
    }
    
    public int execute(SipSession session)
    {
        try
        {
            String sentBody = null;
            if(null != body)
            {
            	sentBody = body.replaceVar(session.getInterpreter());  
            }
            List<Header> sendHeaders = getAddedHeaders(session);
            if(response > 0)
            {
                session.sendResponse(response, request, sendHeaders, sentBody, reliable);
            }
            else
            {
                session.sendRequest(request, sendHeaders, sentBody, !dialog);   
            }
        } catch (Exception e)
        {
            session.terminated("Failed to " + toString(), e);  
            return 0;
        } 
        return 1;
    }
    
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        if(response >= 0)
        {
            buffer.append("send response:").append(response).append(" for request:").append(request);
        }
        else
        {
            buffer.append("send request:").append(request);
        }
        return buffer.toString();
    }
}
