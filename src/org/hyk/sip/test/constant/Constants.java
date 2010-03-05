/**
 * 
 */
package org.hyk.sip.test.constant;

import java.text.ParseException;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.AddressFactory;
import javax.sip.header.AllowHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class Constants
{
    public static HeaderFactory headerFactory;
    public static MessageFactory messageFactory;
    public static AddressFactory addressFactory;
    public static Header allow;
    public static final int MAX_FORWARDS_DEFAULT = 70;
    private static boolean isInit = false;
    
    public static void init(SipFactory factory) throws PeerUnavailableException, ParseException
    {
        if(isInit) return;
        headerFactory = factory.createHeaderFactory();
        messageFactory = factory.createMessageFactory();
        addressFactory = factory.createAddressFactory();
        allow = headerFactory.createAllowHeader("INVITE,PRACK,UPDATE,INFO,MESSAGE,REFER,PUBLISH,REGISTER,NOTIFY");
        isInit = true;
    }
}
