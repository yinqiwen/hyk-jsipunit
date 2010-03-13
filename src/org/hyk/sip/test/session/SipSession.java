package org.hyk.sip.test.session;

import gov.nist.javax.sip.stack.SIPServerTransaction;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.Transaction;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.log4j.Logger;
import org.hyk.sip.test.constant.Constants;
import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.script.SessionAction;
import org.hyk.sip.test.script.URIType;
import org.hyk.sip.test.script.expression.ExpressionListener;

import bsh.Interpreter;

public class SipSession implements Runnable
{
    private static Logger logger = Logger.getLogger(SipSession.class);
     
    SipProvider provider = null;
    SessionAction action;
    
    boolean ready = false;
    
    String failCause;
    
    private List<String> callIds = new LinkedList<String>();
    private LinkedList<Message> recvMsg = new LinkedList<Message>();
    private ScheduledThreadPoolExecutor timer;
    private SipSessionGroup group;
    private Dialog dialog;
    private MaxForwardsHeader max_forwards ;
    private ContactHeader contact_header;
    private ViaHeader via_header;
    private CallIdHeader callId;
    private CSeqHeader cseq;
    private FromHeader fromHeader;
    private ToHeader toHeader;
    private String mytag;
    private Response reliableResponse;
    private HashMap<String, ServerTransaction> serverTransactions = new HashMap<String, ServerTransaction>();
    private HashMap<String, ClientTransaction> clientTransactions = new HashMap<String, ClientTransaction>();
    //protected Interpreter interpreter = new Interpreter();
   
	private Semaphore semaphore = new Semaphore(1);
    
    SipSession(SipProvider provider, SessionAction action, ScheduledThreadPoolExecutor timer, SipSessionGroup group) throws Exception
    {
        this.provider = provider;
        this.action = action;
        if(null == provider)
        {
            throw new Exception("NULL SipProvider with location:" + action.getLocation());
        }
        this.timer = timer;
        this.group = group;
        max_forwards = Constants.headerFactory
                .createMaxForwardsHeader(Constants.MAX_FORWARDS_DEFAULT);
        mytag = group.getSessionManager().getTagGenerator().generateTag();
        if(!action.isPassiveMode())
        {
            ready = true;
            URIType type = action.getURIType();
            URI fromUri;
            URI toUri;
            if(0 == type.compareTo(URIType.TEL))
            {
                fromUri = Constants.addressFactory.createTelURL(action.getId());
                toUri = Constants.addressFactory.createTelURL(action.getRemotePhone());
                //contactUri = Constants.addressFactory.createTelURL(action.getId());
            }
            else
            {
                String localHost = group.getSessionManager().getHykSipunitStack(action.getLocation()).getHost();
                fromUri = Constants.addressFactory.createSipURI(action.getId(),localHost);
               
                ((SipURI)fromUri).setPort(group.getSessionManager().getHykSipunitStack(action.getLocation()).getPort());
                //((SipURI)contactUri).setPort(group.getSessionManager().getHykSipunitStack(action.getLocation()).getPort());
                //System.out.println(group);
               // System.out.println(group.getSessionManager());
                String remoteHost = group.getSessionManager().getRemoteAddress(action.getRemoteLocation()).getHost();
                toUri = Constants.addressFactory.createSipURI(action.getRemotePhone(),remoteHost);
                ((SipURI)toUri).setPort(group.getSessionManager().getRemoteAddress(action.getRemoteLocation()).getPort());
                if(0 == type.compareTo(URIType.SIPS))
                {
                    ((SipURI)fromUri).setSecure(true);
                    ((SipURI)toUri).setSecure(true);
                    //((SipURI)contactUri).setSecure(true);
                }
            }
           
            fromHeader = Constants.headerFactory.createFromHeader(Constants.addressFactory.createAddress(fromUri), null);
            toHeader = Constants.headerFactory.createToHeader(Constants.addressFactory.createAddress(toUri), null);
        }
        SipURI contactUri;
        contactUri = Constants.addressFactory.createSipURI(null,group.getSessionManager().getHykSipunitStack(action.getLocation()).getHost());
        contactUri.setPort(group.getSessionManager().getHykSipunitStack(action.getLocation()).getPort());
        contact_header = Constants.headerFactory.createContactHeader(Constants.addressFactory.createAddress(contactUri));
    }
    
    public Interpreter getInterpreter()
	{
		return group.interpreter;
	}

    
    public boolean isReady()
    {
        return ready;
    }
    
    public SessionAction getAction()
    {
        return action;
    }

    public void setReliableResponse(Response res)
    {
        reliableResponse = res;
    }
    
    public ScheduledThreadPoolExecutor getTimer()
    {
        return timer;
    }
    
    private void storeMessageVariable(Message message) 
    {
        //VariableManager varManager = group.getVariableManager();
        //Variable recv = varManager.createVariable(VAR_RECV_MESSAGE);
        //recv.setValue(message);
        group.recvMsgs.put(action.getId(), message);
        if(null != message.getRawContent())
        {
            group.recvBodys.put(action.getId(), new String(message.getRawContent()));
              
        }   
        else
        {
            group.recvBodys.remove(action.getId());
        }
    }
    
    public void reset()
    {
        ready = false;
        action.reset();
        group.getSessionManager().addSessionGroupIfNotReady(group);
    }
    
    public synchronized void addRecvMessage(Message message)
    {
        try
        {
            recvMsg.add(message);
            storeMessageVariable(message);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public synchronized Message getRecvMessage()
    {
        if(recvMsg.isEmpty())
        {
            return null;
        }
        return recvMsg.removeFirst();
    }
    
    public void addTransaction(Transaction transc)
    {
        String method = transc.getRequest().getMethod();
        dialog = transc.getDialog();
        if(transc instanceof SIPServerTransaction)
        {  
            serverTransactions.put(method, (ServerTransaction) transc);
        }
    }
    
    public SipSessionGroup getSipSessionGroup()
    {
        return group;
    }
    
    public boolean execute(int next)
    {
        if(!semaphore.tryAcquire())
        {
            return false;
        }
        try
        {
            action.execute(this,next);
            return true;
        }
        finally
        {
            semaphore.release();
        }
    }
    
    public void run()
    {
        execute(0);      
    }
    
    private void putElmenet(Message msg, List<Header> addedHeaders, String body) throws ParseException
    {
        for (int i = 0; i < addedHeaders.size(); i++)
        {
            Header header = addedHeaders.get(i);
            if (header.getName().equals(ContentTypeHeader.NAME))
            {
                if (body == null)
                {
                    body = "";
                }
                msg.setContent(body.getBytes(), (ContentTypeHeader) header);
            }
            else
            {
                msg.addHeader(header);
            }
        }
    }
    
    public boolean sendRequest(String method, List<Header> addedHeaders, String body, boolean outDialog) throws ParseException, SipException, InvalidArgumentException
    {
        Request msg = null;
        if(null == dialog || outDialog)
        {
            List vias = new LinkedList();
            vias.add(via_header);
            callId = provider.getNewCallId();
            cseq = Constants.headerFactory.createCSeqHeader(cseq == null ? 1 : (cseq
                    .getSeqNumber() + 1), method);
            fromHeader.setTag(mytag);
            msg = Constants.messageFactory.createRequest(toHeader.getAddress().getURI(),
                    method, callId, cseq, fromHeader, toHeader, vias,
                    max_forwards);
            msg.addHeader(contact_header);
            msg.addHeader(Constants.allow);
            callIds.add(callId.getCallId() + provider.hashCode());
            group.getSessionManager().registerSession(callId.getCallId()+provider.hashCode(), this);
        }
        else
        {
            if(method.equalsIgnoreCase(Request.PRACK))
            {
                msg= dialog.createPrack(reliableResponse);
            }
            else if(method.equalsIgnoreCase(Request.CANCEL))
            {
                ClientTransaction invite = clientTransactions.get(Request.INVITE);
                if(null != invite)
                {
                    msg = invite.createCancel();                   
                }
                else
                {
                    throw new SipException("No invite transaction found for cancel!");
                }
            }
            else if(method.equalsIgnoreCase(Request.ACK))
            {
                ClientTransaction invite = clientTransactions.get(Request.INVITE);
                if(null != invite)
                {
                    Request invitemsg = invite.getRequest();   
                    CSeqHeader cseq = (CSeqHeader) invitemsg.getHeader(CSeqHeader.NAME);
                    msg = dialog.createAck(cseq.getSeqNumber());
                }
                else
                {
                    throw new SipException("No invite transaction found for cancel!");
                }
            }
            else
            {
                msg = dialog.createRequest(method);     
            }
            
        }
        
        putElmenet(msg,addedHeaders,body);
        ClientTransaction transc = null;
        if(!method.equals(Request.ACK))
        {
            transc = provider.getNewClientTransaction(msg);
        }
        if(null == dialog || method.equalsIgnoreCase(Request.CANCEL))
        {
            transc.sendRequest();   
        }
        else
        {
            if(method.equals(Request.ACK))
            {
                dialog.sendAck(msg);
                
            }
            else
            {            
                dialog.sendRequest(transc);
            }
            
        }
        clientTransactions.put(method, transc);
        group.getSessionManager().postMessageSent(msg, this);
        return true;
    }
    
    public boolean sendResponse(int response, String method, List<Header> addedHeaders, String body, boolean reliable) throws ParseException, SipException, InvalidArgumentException
    {
        Response msg = null;      
        ServerTransaction serverTansc = serverTransactions.get(method);
        if(null == serverTansc)
        {
        	Exception e = new Exception();
        	e.printStackTrace();
        	
        	return false;
        }
        dialog = serverTansc.getDialog();
        if(null != serverTansc)
        {
            if(reliable)
            {
                if(null != dialog)
                {
                    msg = dialog.createReliableProvisionalResponse(response);
                    ToHeader to = (ToHeader) msg.getHeader(ToHeader.NAME);
                    to.setTag(mytag);
                }
            }
            else
            {
                msg = Constants.messageFactory.createResponse(response, serverTansc.getRequest());   
            }
            putElmenet(msg, addedHeaders, body);
            if(response == Response.OK && method.equalsIgnoreCase(Request.INVITE))
            {
                msg.addHeader(contact_header);
            }
            if(reliable)
            {
                dialog.sendReliableProvisionalResponse(msg);
            }
            else
            {
                serverTansc.sendResponse(msg);
            }           
            group.getSessionManager().postMessageSent(msg, this);
            return true;
        }
        else
        {            
            throw new SipException("Failed to get a server transaction to send response!");
        }      
    }
    
    public void terminated(String cause)
    {
        terminated(cause, null);
    }
    
    public void terminated(String cause, Throwable e)
    {
        if(logger.isInfoEnabled())
        {
            logger.error(cause, e);
        }
        this.failCause = "Phone:" + action.getId() + " execute failed with cause:" + cause;       
        if(null != dialog)
        {
            dialog.delete();
        }
        
        for (int i = 0; i < callIds.size(); i++)
        {
            String key = callIds.get(i);
            group.getSessionManager().removeSession(key);
        }
        
        group.notifySessionFailed(this);
    }
    
    public void notifyExpressionExecuted()
    {
    	if(null != action.getCurrentAction())
    	{
    		Action ac = action.getCurrentAction();
    		if(ac instanceof ExpressionListener)
    		{
    			((ExpressionListener)ac).afterExpressionExecuted();
    		}
    	}
    }
}