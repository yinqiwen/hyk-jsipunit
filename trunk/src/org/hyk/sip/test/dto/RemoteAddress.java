/**
 * 
 */
package org.hyk.sip.test.dto;

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
    public void setName(String name)
    {
        this.name = name;
    }
    public String getHost()
    {
        return host;
    }
    public void setHost(String host)
    {
        this.host = host;
    }
    public int getPort()
    {
        return port;
    }
    public void setPort(int port)
    {
        this.port = port;
    }
    private String name;
    private String host;
    private int port;
}
