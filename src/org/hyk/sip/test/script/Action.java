/**
 * 
 */
package org.hyk.sip.test.script;

import org.hyk.sip.test.session.SipSession;

import bsh.Interpreter;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public abstract class Action
{   
	protected static Interpreter interpreter = new Interpreter();
	
    public abstract void init() throws Exception;
    public abstract int execute(SipSession session);
    
//    protected Expression createExpression(String expression, VariableManager varManager)
//    {
//        Expression expr = new Expression(varManager);
//        expr.setExpression(expression);
//        return expr;
//    }
}
