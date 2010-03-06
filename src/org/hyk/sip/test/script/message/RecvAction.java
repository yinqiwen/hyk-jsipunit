package org.hyk.sip.test.script.message;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.sip.header.CSeqHeader;
import javax.sip.header.Header;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.session.SipSession;

public class RecvAction extends MessaeAction implements Runnable
{
	@XmlAttribute
	private long					time				= 60000;

	@XmlAttribute
	private boolean					reliable;

	@XmlAttribute
	private boolean					optional			= false;

	private HashMap<String, String>	headerPatternMap	= new HashMap<String, String>();
	private ScheduledFuture			timerTask;
	private SipSession				session;

	public void init() throws Exception
	{
		for(int i = 0; i < headerValues.size(); i++)
		{
			String header = headerValues.get(i).getFormat().trim();
			int index = header.indexOf(':');
			String name = header.substring(0, index).trim();
			String value = header.substring(index + 1).trim();
			headerPatternMap.put(name, value);
		}
	}

	private int failMatching(String cause, SipSession session, Message recv)
	{
		if(optional)
		{
			session.addRecvMessage(recv);
			return 1;
		}
		else
		{
			session.terminated(cause);
			return 0;
		}

	}

	public int execute(SipSession session)
	{
		this.session = session;
		Message recv = session.getRecvMessage();
		if(null == recv && null == timerTask)
		{
			timerTask = session.getTimer().schedule(this, time, TimeUnit.MILLISECONDS);
			return 0;
		}
		if(null != timerTask)
		{
			timerTask.cancel(true);
		}
		if(null != recv)
		{
			CSeqHeader cseq = (CSeqHeader)recv.getHeader(CSeqHeader.NAME);
			String method = cseq.getMethod();
			if(!request.equals("*") && !method.equalsIgnoreCase(request))
			{
				// session.terminated(toString()
				// +",but got message with method:" + method);
				return failMatching(toString() + ",but got message with method:" + method, session, recv);
			}

			if(response >= 0)
			{
				if(recv instanceof Request)
				{
					// session.terminated(toString() +",but got a request:" +
					// recv);
					return failMatching(toString() + ",but got a request:" + recv, session, recv);
				}
				Response res = (Response)recv;
				// 0 means match all response
				if(0 != response && res.getStatusCode() != response)
				{
					// session.terminated(toString() + ", but got response " +
					// res.getStatusCode());
					// return 0;
					return failMatching(toString() + ", but got response " + res.getStatusCode(), session, recv);
				}
				if(reliable)
				{
					session.setReliableResponse(res);
				}

			}
			return 1;
		}
		return 0;
	}

	public void run()
	{
		session.terminated("expected recv message with method:" + request + ",but timeout!");

	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		if(response >= 0)
		{
			buffer.append("Expected receive response:").append(response).append(" for request:").append(request);
		}
		else
		{
			buffer.append("Expected receive request:").append(request);
		}

		return buffer.toString();
	}
}
