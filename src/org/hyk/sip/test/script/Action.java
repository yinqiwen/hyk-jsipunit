/**
 * 
 */
package org.hyk.sip.test.script;

import org.hyk.sip.test.session.SipSession;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 * 
 */
public abstract class Action
{
	// protected static Interpreter interpreter = new Interpreter();

	public abstract void init() throws Exception;

	public abstract int execute(SipSession session);

	protected boolean evalBooleanCondition(String condition, SipSession session)
	{
		Boolean b = false;
		try
		{
			b = (Boolean)(session.getInterpreter().eval(condition));
		}
		catch(EvalError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;

	}

	// protected Expression createExpression(String expression, VariableManager
	// varManager)
	// {
	// Expression expr = new Expression(varManager);
	// expr.setExpression(expression);
	// return expr;
	// }
}
