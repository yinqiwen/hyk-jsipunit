/**
 * 
 */
package org.hyk.sip.test.script;

import java.util.ArrayList;
import java.util.List;

import org.hyk.sip.test.script.control.ControlAction;
import org.hyk.sip.test.script.control.ElseAction;
import org.hyk.sip.test.script.control.EndWhileAction;
import org.hyk.sip.test.script.control.FiAction;
import org.hyk.sip.test.script.control.IfAction;
import org.hyk.sip.test.script.control.ResetAction;
import org.hyk.sip.test.script.control.WhileAction;
import org.hyk.sip.test.script.expression.AssertAction;
import org.hyk.sip.test.script.expression.AssignAction;
import org.hyk.sip.test.script.expression.EchoAction;
import org.hyk.sip.test.script.expression.RegexAction;
import org.hyk.sip.test.script.expression.WaitAction;
import org.hyk.sip.test.script.message.RecvAction;
import org.hyk.sip.test.script.message.SendAction;
import org.hyk.sip.test.session.SipSession;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.Elements;
import org.xmappr.RootElement;

import com.sun.media.rtsp.protocol.PauseMessage;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 * 
 */
@RootElement("phone")
public class SessionAction
{
	@Attribute("id")
	private String			id;

	@Attribute("location")
	private String			location;

	@Attribute("remote-phone")
	private String			remotePhone;

	@Attribute("remote-location")
	private String			remoteLocation;

	private URIType			uriType	= URIType.SIP;
	private boolean			isPassiveMode;

	@Elements( {@Element(name = "send", targetType = SendAction.class), @Element(name = "recv", targetType = RecvAction.class),
			@Element(name = "pause", targetType = WaitAction.class), @Element(name = "assign", targetType = AssignAction.class),
			@Element(name = "echo", targetType = EchoAction.class), @Element(name = "if", targetType = IfAction.class),
			@Element(name = "else", targetType = ElseAction.class), @Element(name = "fi", targetType = FiAction.class),
			@Element(name = "while", targetType = WhileAction.class), @Element(name = "done", targetType = EndWhileAction.class),
			@Element(name = "assert", targetType = AssertAction.class), @Element(name = "regex", targetType = RegexAction.class),
			@Element(name = "reset", targetType = ResetAction.class)})
	private List<Action>	actions;
	public void setActions(List<Action> actions)
	{
		this.actions = actions;
	}

	private int				cursor	= 0;
	private int				actionSize;

	public List<Action> getActions()
	{
		return actions;
	}

	public void reset()
	{
		cursor = 0;
	}

	public void setRemoteLocation(String remoteLocation)
	{
		this.remoteLocation = remoteLocation;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setRemotePhone(String remotePhone)
	{
		this.remotePhone = remotePhone;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public void init() throws Exception
	{
		System.out.println("####" + id);
		actionSize = actions.size();
		for(int i = 0; i < actions.size(); i++)
		{
			Action ac = actions.get(i);
			if(ac instanceof SendAction)
			{
				isPassiveMode = false;
				break;
			}
			if(ac instanceof RecvAction)
			{
				isPassiveMode = true;
				break;
			}
			ac.init();

		}

		ControlAction.sortAndInitControlAction(actions);
	}

	public String getRemotePhone()
	{
		return remotePhone;
	}

	public String getRemoteLocation()
	{
		return remoteLocation;
	}

	public URIType getURIType()
	{
		return uriType;
	}

	public boolean isPassiveMode()
	{
		return isPassiveMode;
	}

	public boolean execute(SipSession session, int next)
	{
		try
		{
			cursor += next;
			while(cursor < actionSize)
			{
				Action action = actions.get(cursor);
				int ret = action.execute(session);
				if(ret != 0)
				{
					cursor += ret;
				}
				else
				{
					// cursor++;
					break;
				}
			}
			if(cursor >= actionSize)
			{
				session.getSipSessionGroup().notifySessionComplete(session);
			}
		}
		catch(Throwable e)
		{
			session.terminated("Failed to execute this action:" + actions.get(cursor), e);
		}

		return false;
	}
}
