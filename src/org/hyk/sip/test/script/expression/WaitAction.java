package org.hyk.sip.test.script.expression;


import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAttribute;

import org.hyk.sip.test.script.Action;
import org.hyk.sip.test.session.SipSession;

public class WaitAction extends Action implements Runnable
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
            //expression.setExpression(condition);
            try
            {
            	Boolean b = (Boolean) (interpreter.eval(condition));
                if(b)
                {
                   return 1; 
                }
                //session.getSipSessionGroup().getVariableManager().registerVariableListener(expression.getFirstVariable().getName(), this);
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
        //if(null == expression)
    	if(true)
        {
            session.execute(1);
        }
        else
        {
            session.terminated("Timeout when waiting condition:" + condition +" succeed!");
            //System.out.println("Timeout!" + expression.getVar().getName());   
        }
    }

//    public void notifyVariableUpdate(Variable var)
//    {
//        try
//        {
//            Boolean b = (Boolean) (expression.execute(condition).getValue());
//            //System.out.println("Notify" + b);
//            if(b)
//            {
//                timerTask.cancel(true);
//                session.execute(1);
//            }
//            
//        } catch (ParseException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (VariableInstantialException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//    }

}
