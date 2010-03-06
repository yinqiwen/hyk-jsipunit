/**
 * 
 */
package org.hyk.sip.test.session;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.sip.message.Message;
import javax.sip.message.Request;

import org.hyk.sip.test.script.SessionAction;

import bsh.Interpreter;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class SipSessionGroup implements Runnable
{
    //private VariableManager varManager = new VariableManager();
    private SessionAction[] actions;
    private SipSession[] sessions;
    private SessionManager manager;
    private ScheduledThreadPoolExecutor threadpool;
    private int sessionCompleteNum = 0;
    Map<String, Message> recvMsgs = new ConcurrentHashMap<String, Message>();
    Map<String, String> recvBodys = new ConcurrentHashMap<String, String>();
    private static final String RECV_MSGS = "RECV_MSGS";
    private static final String RECV_BODYS = "RECV_BODYS";
    protected Interpreter interpreter = new Interpreter();
    

	StringBuffer failCause = new StringBuffer();
    
    public SipSessionGroup(SessionAction[] actions, SessionManager manager) throws Exception
    {
        this.actions = actions;
        this.manager = manager;
        this.threadpool = manager.getThreadPool();
        sessions = new SipSession[actions.length];
        for (int i = 0; i < actions.length; i++)
        {
            sessions[i] = new SipSession(manager.getSipProviderByName(actions[i].getLocation()), actions[i], threadpool, this);
        }
        interpreter.set(RECV_MSGS, recvMsgs);
        interpreter.set(RECV_BODYS, recvBodys);
//        varRecvMsgs = varManager.createVariable(RECV_MSGS);
//        varRecvMsgs.setValue(recvMsgs);
//        varRecvBodys = varManager.createVariable(RECV_BODYS);
//        varRecvBodys.setValue(recvBodys);
    }
    
    public SessionManager getSessionManager()
    {
        return manager;
    }
    
    public Interpreter getInterpreter()
	{
		return interpreter;
	}

    
//    public String replaceVariableReference(String input)
//    {
//        Map<String, Variable> vars = varManager.getAllVars();
//        Iterator<String> keys = vars.keySet().iterator();
//        while(keys.hasNext())
//        {
//            String key = keys.next();
//            
//            if(input.contains(key))
//            {           
//                input = input.replace(key, vars.get(key).getValue().toString() );
//            }
//        }
//        return input;
//    }
    
    public boolean isAllSessionReady()
    {
        boolean ready = true;
        for (int i = 0; i < sessions.length; i++)
        {
            ready &= sessions[i].isReady();
        }
        return ready;
    }
    
    public synchronized void notifySessionComplete(SipSession session)
    {
        sessionCompleteNum++;
        if(sessionCompleteNum >= sessions.length)
        {
            manager.notifySessionGroupComplete(this);
        }
    }
    
    public synchronized void notifySessionFailed(SipSession session)
    {
        sessionCompleteNum++;
        failCause.append(session.failCause).append("\n");
        if(sessionCompleteNum >= sessions.length)
        {
            manager.notifySessionGroupComplete(this);
        }
    }
    
    public SipSession matchSession(SessionMatcher matcher, Request req)
    {
        for (int i = 0; i < sessions.length; i++)
        {
            SipSession session = sessions[i];
            if(!session.isReady())
            {
                if(matcher.match(session.action.getId(),req))
                {
                    session.ready = true;
                    return session;
                }
            }
        }
        return null;
    }
    
    public void notifyExpressionExecuted()
    {
    	 for (int i = 0; i < sessions.length; i++)
         {
             SipSession session = sessions[i];
             session.notifyExpressionExecuted();
         }
    }
    
    public void run()
    {
        for (int i = 0; i < actions.length; i++)
        {
            threadpool.execute(sessions[i]);
        }       
    }
    
}