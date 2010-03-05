/**
 * 
 */
package org.hyk.sip.test.script;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

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

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 * 
 */
@XmlRootElement(name = "phone")
public class SessionAction
{

	@XmlAttribute
	private String			id;

	@XmlAttribute
	private String			location;

	@XmlAttribute(name="remote-phone")
	private String			remotePhone;

	@XmlAttribute(name="remote-location")
	private String			remoteLocation;

	private URIType			uriType	= URIType.SIP;
	private boolean			isPassiveMode;

	@XmlElements( {@XmlElement(name = "send", type = SendAction.class), @XmlElement(name = "recv", type = RecvAction.class),
		@XmlElement(name = "pause", type = WaitAction.class), @XmlElement(name = "assign", type = AssignAction.class),
		@XmlElement(name = "echo", type = EchoAction.class), @XmlElement(name = "if", type = IfAction.class),
		@XmlElement(name = "else", type = ElseAction.class), @XmlElement(name = "fi", type = FiAction.class),
		@XmlElement(name = "while", type = WhileAction.class), @XmlElement(name = "done", type = EndWhileAction.class),
		@XmlElement(name = "assert", type = AssertAction.class), @XmlElement(name = "regex", type = RegexAction.class),
		@XmlElement(name = "reset", type = ResetAction.class)})
	private List<Action>	actions;

	private int				cursor	= 0;
	private int				actionSize;

//	
//	public List<Action> getActions()
//	{
//		return actions;
//	}
//
//	public void setActions(List<Action> actions)
//	{
//		this.actions = actions;
//	}
//	
//	public void addActions(Action actions)
//	{
//		//this.actions = actions;
//	}

	public void reset()
	{
		cursor = 0;
	}

//	
//	public void setRemoteLocation(String remoteLocation)
//	{
//		this.remoteLocation = remoteLocation;
//	}



	public String getId()
	{
		return id;
	}

	public String getRemotePhone()
	{
		return remotePhone;
	}

	public String getRemoteLocation()
	{
		return remoteLocation;
	}

	public String getLocation()
	{
		return location;
	}

	public void init() throws Exception
	{
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
