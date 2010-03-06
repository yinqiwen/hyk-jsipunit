package org.hyk.sip.test.script.expression;


import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;

public class WaitAction extends Action implements Runnable, ExpressionListener
{
	@XmlAttribute
    private String condition;

	@XmlAttribute
    private long time = 60000;
    private SipSession session;
    private ScheduledFuture timerTask;
    
    public void init() throws Exception
    {
        // TODO Auto-generated method stub
        
    }
   
    
    public int execute(SipSession session) 
    {
        this.session = session;
        if(null != condition)
        {
            try
            {
            	Boolean b = (Boolean) (session.getInterpreter().eval(condition));
                if(b)
                {
                   return 1; 
                }
            } catch (Exception e)
            {               
                e.printStackTrace();
            }
        }
       
        timerTask =  session.getTimer().schedule(this, time, TimeUnit.MILLISECONDS);
        return 0;
    }

    public void run()
    {
    	if(null == condition || evalBooleanCondition(condition, session))
        {
            session.execute(1);
        }
        else
        {
            session.terminated("Timeout when waiting condition:" + condition +" succeed!");  
        }
    }


	@Override
	public void afterExpressionExecuted()
	{
		if(null != condition && evalBooleanCondition(condition, session))
		{
			timerTask.cancel(true);
            session.execute(1);
		}
		
	}

}
