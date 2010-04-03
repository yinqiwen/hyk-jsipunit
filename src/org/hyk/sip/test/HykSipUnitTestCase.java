/**
 * 
 */
package org.hyk.sip.test;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.sip.address.URI;
import javax.sip.message.Request;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.TestCase;

import org.hyk.sip.test.script.SessionAction;
import org.hyk.sip.test.session.SessionManager;
import org.hyk.sip.test.session.SessionMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.1.0
 * @author yinqiwen
 * 
 */
public abstract class HykSipUnitTestCase extends TestCase
{
	private static SessionManager	sessionManager;

	protected Logger				logger	= LoggerFactory.getLogger(getClass());

	protected void onSetUp(){}
	protected void onTearDown(){}
	
	@Override
	protected final void setUp() throws Exception
	{
		super.setUp();
		onSetUp();
	}
	
	@Override
	protected final void tearDown() throws Exception
	{
		super.tearDown();
		onTearDown();
	}
	
	protected SessionManager getSessionManager() throws Exception
	{
		if(null == sessionManager)
		{
			JAXBContext context = JAXBContext.newInstance(SessionManager.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			sessionManager = (SessionManager)unmarshaller.unmarshal(ClassLoader.getSystemResourceAsStream("hyk-jsipunit.xml"));
			sessionManager.init();
			if(this instanceof SessionMatcher)
			{
				sessionManager.setSessionMatcher((SessionMatcher)this);
			}
			else
			{
				sessionManager.setSessionMatcher(new RequestURIMatcher());
			}
		}
		return sessionManager;
	}

	protected boolean	isScriptInClasspath	= true;

	class RequestURIMatcher implements SessionMatcher
	{

		public boolean match(String id, Request incomingRequest)
		{
			URI uri = incomingRequest.getRequestURI();
			return uri.toString().contains(id);
		}
	}

	public abstract String[] getScriptLocations();
	
	protected String getTestCaseName()
	{
		return getClass().getName();
	}

	private SessionManager proceed(String[] scripts) throws Exception
	{
		String path = this.getClass().getPackage().getName().replaceAll("\\.", "/");
		SessionAction[] actions = new SessionAction[scripts.length];
		for(int i = 0; i < scripts.length; i++)
		{
			InputStream is = null;
			if(isScriptInClasspath)
			{
				is = getClass().getResourceAsStream(scripts[i]);
			}
			else
			{
				is = new FileInputStream(scripts[i]);
			}

			JAXBContext context = JAXBContext.newInstance(SessionAction.class);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(HykSipUnitTestCase.class.getResource("script/script.xsd"));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new ValidationEventHandler()
			{
				public boolean handleEvent(ValidationEvent event)
				{
					return false;
				}
			});
			actions[i] = (SessionAction)unmarshaller.unmarshal(is);
			actions[i].init();
			is.close();
		}
		SessionManager manager = getSessionManager();
		manager.execute(actions);
		return manager;
	}

	// @Test
	public void testSipMessage() throws Exception
	{
		if(logger.isInfoEnabled())
		{
			String msg = String.format("#############################Start hyk-sipunit test case:%s################################", getTestCaseName());
			logger.info(msg);
		}
		String[] scripts = getScriptLocations();
		SessionManager manager = proceed(scripts);
		if(logger.isInfoEnabled())
		{
			String msg = String.format("#############################End hyk-sipunit test case:%s################################", getTestCaseName());
			logger.info(msg);
		}
		assertNull("Expected succeed to execute, but got fail with casue:" + manager.getExecuteResult(), manager.getExecuteResult());	
	}
}
