/**
 * 
 */
package org.hyk.sip.test.session;

import javax.sip.message.Request;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public interface SessionMatcher
{
    public boolean match(String id, Request incomingRequest);
}
