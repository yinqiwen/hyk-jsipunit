/**
 * 
 */
package org.hyk.sip.test;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.sip.address.URI;
import javax.sip.message.Request;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.hyk.sip.test.script.SessionAction;
import org.hyk.sip.test.session.SessionManager;
import org.hyk.sip.test.session.SessionMatcher;



/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public abstract class HykSipUnitTestCase extends TestCase
{ 
    private static SessionManager sessionManager;
    
    protected SessionManager getSessionManager() throws Exception
    {
    	if(null == sessionManager)
    	{
    		JAXBContext context = JAXBContext.newInstance(SessionManager.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            sessionManager = (SessionManager)unmarshaller.unmarshal(new File("hyk-jsipunit.xml"));
            sessionManager.init();
            if(this instanceof SessionMatcher)
            {
            	sessionManager.setSessionMatcher((SessionMatcher) this);
            }
            else
            {
            	sessionManager.setSessionMatcher(new RequestURIMatcher());
            }
    	}
    	return sessionManager;
    }
	
    protected boolean isScriptInClasspath = true;
    class RequestURIMatcher implements SessionMatcher
    {

        public boolean match(String id, Request incomingRequest)
        {
            URI uri = incomingRequest.getRequestURI();
            return uri.toString().contains(id);
        }
    }
    
    public abstract String[] getScriptLocations();
    
    private SessionManager proceed(String[] scripts) throws Exception
    {
        String path = this.getClass().getPackage().getName().replaceAll("\\.", "/");
        SessionAction[] actions = new SessionAction[scripts.length];
        for (int i = 0; i < scripts.length; i++)
        {
        	InputStream is = null;
            if(isScriptInClasspath)
            {
            	is = getClass().getResourceAsStream(scripts[i]);
            }
            else
            {
                scripts[i] = "file:"+ scripts[i];
            }
            
            JAXBContext context = JAXBContext.newInstance(SessionAction.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
            actions[i] = (SessionAction)unmarshaller.unmarshal(is);
            actions[i].init();
        }     
        SessionManager manager = getSessionManager();
        manager.execute(actions);
        return manager;
    }
    
    public void testSipMessage() throws Exception
    {
        String[] scripts = getScriptLocations();
        SessionManager manager = proceed(scripts);
        assertNull("Expected succeed to execute, but got fail with casue:" + manager.getExecuteResult(), manager.getExecuteResult());
    }
}