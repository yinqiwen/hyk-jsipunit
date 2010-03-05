/**
 * 
 */
package org.hyk.sip.test.dto;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class TestConfig
{
    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }
    public int getCaps()
    {
        return caps;
    }
    public void setCaps(int caps)
    {
        this.caps = caps;
    }
    public int getLimit()
    {
        return limit;
    }
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    private String type;
    private int caps;
    private int limit;
}
