/**
 * 
 */
package org.hyk.sip.test.dto;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class RemoteAddress
{
    public String getName()
    {
        return name;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }
    
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String host;
    @XmlAttribute
    private int port;
}
