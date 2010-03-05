/**
 * 
 */
package org.hyk.sip.test.stack;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TransportNotSupportedException;
import javax.xml.bind.annotation.XmlAttribute;


/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class HykSipunitStack
{
    private static final String DEFAULT_IMPL = "gov.nist";
    private static final int DEFAULT_PORT = 5060;
    
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String host;
    @XmlAttribute
    private int port = DEFAULT_PORT;
    private String impl = DEFAULT_IMPL;
    
    private SipProvider sipProvider = null;
    private SipStack stack = null;
    private SipFactory factory = null;
    private Properties properties = new Properties();
    
    public String getName()
    {
        return name;
    }
    public HykSipunitStack()
    {
        
    }
    
    public void setName(String name)
    {
        this.name = name;   
    }
    public void setHost(String host)
    {
        this.host = host;   
    }
    public void setPort(int port)
    {
        this.port = port;   
    }
    
    public void setImpl(String impl)
    {
        this.impl = impl;
    }
   
    
    public void init() throws PeerUnavailableException, TransportNotSupportedException, InvalidArgumentException, ObjectInUseException, UnknownHostException
    {
        if(!properties.contains("gov.nist.javax.sip.TRACE_LEVEL"))
        {
            properties.put("gov.nist.javax.sip.TRACE_LEVEL", "0");
        }
        if(!properties.contains("gov.nist.javax.sip.PASS_INVITE_NON_2XX_ACK_TO_LISTENER"))
        {
            properties.put("gov.nist.javax.sip.PASS_INVITE_NON_2XX_ACK_TO_LISTENER", "true");
        }
        factory = SipFactory.getInstance();
        factory.setPathName(impl);
        properties.setProperty("javax.sip.STACK_NAME", name);
        stack = factory.createSipStack(properties);
        if(null == host)
        {
            host = InetAddress.getLocalHost().getHostAddress();
        }
        ListeningPoint lp = stack.createListeningPoint(host,port, "udp");
        sipProvider = stack.createSipProvider(lp);
    }
    
    public void setProperty(String key, String value)
    {
        properties.put(key, value);
    }
    
    public SipProvider getSipProvider()
    {
        return sipProvider;
    }
    
    public SipStack getSipStack()
    {
        return stack;
    }
    
    public String getHost()
    {
        return host;
    }
    
    public int getPort()
    {
        return port;
    }
}
