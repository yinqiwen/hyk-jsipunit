/**
 * 
 */
package org.hyk.sip.test;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.sip.address.URI;
import javax.sip.message.Request;

import junit.framework.TestCase;

import org.hyk.sip.test.script.SessionAction;
import org.hyk.sip.test.session.SessionManager;
import org.hyk.sip.test.session.SessionMatcher;
import org.xmappr.Xmappr;



/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public abstract class HykSipUnitTestCase extends TestCase
{
   // private static HashMap beanBuilders = new HashMap();
    
    static
    {
        //Runtime.getRuntime().addShutdownHook(new BeanBulderFinallizer());
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
        	Reader reader = null;
            if(isScriptInClasspath)
            {
                //scripts[i] = "classpath:" + path + "/" + scripts[i];
                reader = new InputStreamReader(getClass().getResourceAsStream(scripts[i]));
            }
            else
            {
                scripts[i] = "file:"+ scripts[i];
            }
            
            
            Xmappr xm = new Xmappr(SessionAction.class);
            //xm.
            actions[i] = (SessionAction)xm.fromXML(reader);
            actions[i].init();
           
           //actions[i] = (SessionAction) scriptBuilder.build(scripts[i], docBuilder).get(0);
        }     
        
        Xmappr xm = new Xmappr(SessionManager.class);
        SessionManager manager = (SessionManager)xm.fromXML(new FileReader("hyk-jsipunit.xml"));
        manager.init();
        if(this instanceof SessionMatcher)
        {
            manager.setSessionMatcher((SessionMatcher) this);
        }
        else
        {
            manager.setSessionMatcher(new RequestURIMatcher());
        }
        
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