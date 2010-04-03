/**
 * 
 */
package org.hyk.sip.test.session;

/**
 * @version 0.1.0
 * @author yinqiwen
 *
 */
public class TagGenerator
{
    private int seed = 1;
    public synchronized String generateTag()
    {
        String ret = Integer.toString(seed);
        seed++;
        if(seed == Integer.MAX_VALUE)
        {
            seed = 0;
        }
        return ret;
    }
}
