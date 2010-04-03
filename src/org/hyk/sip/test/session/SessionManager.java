/**
 * 
 */
package org.hyk.sip.test.session;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.TransportNotSupportedException;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hyk.sip.test.constant.Constants;
import org.hyk.sip.test.common.RemoteAddress;
import org.hyk.sip.test.script.SessionAction;
import org.hyk.sip.test.stack.HykSipunitStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.1.0
 * @author yinqiwen
 *
 */
@XmlRootElement(name="hyk-jsipunit")
public class SessionManager implements SipListener
{
	
	static class ThreadPool
	{
		@XmlAttribute(name="coresize")
		public int size;
		ScheduledThreadPoolExecutor threadpool;
		
		void init()
		{
			threadpool = new ScheduledThreadPoolExecutor(size);
		}
	}
    private Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private HashMap<String, HykSipunitStack> stacks = new HashMap<String, HykSipunitStack>();
    private HashMap<String, RemoteAddress> remotes = new HashMap<String, RemoteAddress>();
    private SessionAction[] sessionActions = null;

    private Map<String, SipSession> sessions = new ConcurrentHashMap<String, SipSession>();
    //private Queue<SipSessionGroup> groups = new ConcurrentLinkedQueue<SipSessionGroup>();
    private Map<Integer, SipSessionGroup> notReadyGroups = new ConcurrentHashMap<Integer, SipSessionGroup>();
    
    @XmlElement(name="threadpool")
    private ThreadPool threadpool;
    
    private int limitCaseNumber;
    private TagGenerator tagGenerator = new TagGenerator();
    private volatile int executedCaseNumber;
    
    private SessionMatcher sessionMather;
    
    private String result;
    
    public SipProvider getSipProviderByName(String name)
    {
        return stacks.get(name).getSipProvider();
    }
    
    public void setSessionMatcher(SessionMatcher matcher)
    {
        sessionMather = matcher;
    }
    
    @XmlElement(name="sipstack")
    public void setSipStack(HykSipunitStack stack)
    {
        stacks.put(stack.getName(), stack);
    }
    
    public HykSipunitStack getHykSipunitStack(String name)
    {
        return stacks.get(name);
    }
    
    public RemoteAddress getRemoteAddress(String name)
    {
        return remotes.get(name);
    }
    
    @XmlElement(name="remote")
    public void setRemote(RemoteAddress remote)
    {
        remotes.put(remote.getName(), remote);
    }
    
    public ScheduledThreadPoolExecutor getThreadPool()
    {
        return threadpool.threadpool;
    }
    
    public void registerSession(String key, SipSession session)
    {
        sessions.put(key, session);
    }
    
    public void removeSession(String key)
    {
        sessions.remove(key);
    }
    
    public String getExecuteResult()
    {
        return result;
    }
    
    public void init() throws TooManyListenersException, PeerUnavailableException, ParseException, TransportNotSupportedException, ObjectInUseException, UnknownHostException, InvalidArgumentException
    {
    	threadpool.init();
        Iterator<HykSipunitStack> values = stacks.values().iterator();
        while(values.hasNext())
        {
            HykSipunitStack stack =  values.next();
            stack.init();
            stack.getSipProvider().addSipListener(this);
            
        }
        Constants.init(SipFactory.getInstance());
        limitCaseNumber = 1;
    }
    
    private boolean processMessageIfMatch(SipProvider provider, Transaction transc, Request msg, SipSessionGroup group)
    {
        SipSession session = group.matchSession(sessionMather,  msg);
        CallIdHeader callid = (CallIdHeader) msg.getHeader(CallIdHeader.NAME);
        String callidstr = callid.getCallId().trim();
        if(null != session)
        {
            session.addRecvMessage(msg);
            session.addTransaction(transc);
            sessions.put(callidstr + provider.hashCode(), session);
            session.execute(0);
            return true;
        }
		return false;
    }
    
    private void processMessage(Message msg, SipProvider provider, Transaction transc)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("\n=====================Receive Message===================\n" + msg);
        }
        //System.out.println("Recv1=====" + msg);
        CallIdHeader callid = (CallIdHeader) msg.getHeader(CallIdHeader.NAME);
        String callidstr = callid.getCallId().trim();
        SipSession  matchSession = sessions.get(callidstr + provider.hashCode());
        if(null != matchSession)
        {
            //System.out.println("Recv2=====" + msg);
            matchSession.addRecvMessage(msg);
            matchSession.addTransaction(transc);
            matchSession.execute(0);
            return;
        }
		if(msg instanceof Request)
		{
		    Iterator<Integer> keys = notReadyGroups.keySet().iterator();
		    while(keys.hasNext())
		    {
		        Integer key = keys.next();
		        SipSessionGroup group = notReadyGroups.get(key);
		        if(processMessageIfMatch(provider, transc, (Request) msg, group))
		        {
		            if(group.isAllSessionReady())
		            {
		                notReadyGroups.remove(key);
		            }
		            return;
		        }
		        else
		        {
		            continue;
		        }
		    }
		    try
		    {
		        SipSessionGroup group = createSipSessionGroup();
		        if(processMessageIfMatch(provider, transc, (Request) msg, group))
		        {
		            return;
		        }
		    } catch (Exception e)
		    {
		        if(logger.isDebugEnabled())
		        {
		            logger.debug("Exception occured when create session group.", e);
		        }
		    }
		}
		else
		{
		    //do nothing  
		}
		if(logger.isDebugEnabled())
		{
		    logger.debug("Discard unmatched message:\n" + msg);
		}
    }
    
    public void processDialogTerminated(DialogTerminatedEvent event)
    {
        if(logger.isDebugEnabled())
        {
            logger.error("Receive DialogTerminatedEvent from sipstack!"  + event.getDialog());
        } 
        
    }

    public void processIOException(IOExceptionEvent event)
    {
        if(logger.isDebugEnabled())
        {
            logger.error("Receive IOException from sipstack!"  + event);
        }       
    }

    public void processRequest(RequestEvent event)
    {
        ServerTransaction transc = event.getServerTransaction();
        SipProvider provider = (SipProvider) event.getSource();
        if(null == transc)
        {
            try
            {
                transc = provider.getNewServerTransaction(event.getRequest());
            } catch (TransactionAlreadyExistsException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TransactionUnavailableException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        processMessage(event.getRequest(), provider, transc);
        
    }

    public void processResponse(ResponseEvent event)
    {
        processMessage(event.getResponse(),(SipProvider) event.getSource(), event.getClientTransaction());      
    }

    public void processTimeout(TimeoutEvent event)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Receive TimeoutEvent from sipstack!"  + event);
        } 
    }

    public void processTransactionTerminated(TransactionTerminatedEvent event)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Receive TransactionTerminatedEvent from sipstack!"  + event);
        }        
    }
    
    public String execute(SessionAction[] sessionActions) throws Exception
    {
        this.sessionActions = sessionActions;
        executedCaseNumber = 0;
        createSipSessionGroup();
        while(executedCaseNumber < limitCaseNumber)
        {
            synchronized (this)
            {
                this.wait(100);
            }   
        }
        return null;       
    }
    
    public void notifySessionGroupComplete(SipSessionGroup group)
    {
        synchronized (this)
        {
            executedCaseNumber++;
            result = group.failCause.toString();
            if(result.trim().equals(""))
            {
                result = null;
            }
            this.notify();
        }
    }
    
    private SipSessionGroup createSipSessionGroup() throws Exception
    {
        SipSessionGroup group = new SipSessionGroup(this.sessionActions, this);
        addSessionGroupIfNotReady(group);
        threadpool.threadpool.execute(group);
        return group;
    }
    
    public void addSessionGroupIfNotReady(SipSessionGroup group)
    {
        if(!group.isAllSessionReady())
        {
            notReadyGroups.put(group.hashCode(), group);
        }
    }
    
    public TagGenerator getTagGenerator()
    {
        return tagGenerator;
    }
    
    public void postMessageSent(Message sent, SipSession session)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("\n=====================Send Message===================\n" + sent);
        }
    }
}
