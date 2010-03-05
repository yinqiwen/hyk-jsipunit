/**
 * 
 */
package org.hyk.sip.test.script.control;

import java.util.List;
import java.util.Stack;

import org.hyk.sip.test.script.Action;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public abstract class ControlAction extends Action
{
    int index;
    
    public static void sortAndInitControlAction(List<Action> actions) throws Exception
    {
        Stack<IfAction> ifActions = new Stack<IfAction>();
        Stack<ElseAction> elseActions = new Stack<ElseAction>();
        Stack<WhileAction> whileActions = new Stack<WhileAction>();
        for (int i = 0; i < actions.size(); i++)
        {
            Action ac =  actions.get(i);
            if(ac instanceof ControlAction)
            {
                ((ControlAction) ac).index = i;
                ControlAction cac = (ControlAction) ac;
                if(cac instanceof IfAction)
                {
                    ifActions.push((IfAction) cac);
                }
                else if(cac instanceof ElseAction)
                {
                    if(ifActions.isEmpty())
                    {
                        throw new Exception("No if action before this else action.");
                    }
                    IfAction pre = ifActions.pop();
                    pre.elseActionDistance = (cac.index - pre.index);
                    elseActions.push((ElseAction) cac);
                }
                else if(cac instanceof FiAction)
                {
                    ElseAction pre;
                    if(!elseActions.isEmpty())
                    {   
                        pre = elseActions.pop();
                        pre.fiActionDistance = (cac.index - pre.index);
                    }
                    else
                    {
                        if(ifActions.isEmpty())
                        {
                            throw new Exception("No if action before this else action.");
                        }
                        IfAction ipre = ifActions.pop();                       
                        ipre.fiActionDistance = (cac.index - ipre.index);
                        
                    }
                }
                else if(cac instanceof WhileAction)
                {
                    whileActions.push((WhileAction) cac);
                }
                else if(cac instanceof EndWhileAction)
                {
                    if(whileActions.isEmpty())
                    {
                        throw new Exception("No while action before this endwhile action.");
                    }
                    WhileAction wac = whileActions.pop();
                    wac.endInstance = (cac.index - wac.index);
                    ((EndWhileAction) cac).preWhileDistance = (wac.index - cac.index);

                }
            }
        }
        
        if(!ifActions.isEmpty() || !elseActions.isEmpty() || !whileActions.isEmpty())
        {
            throw new Exception("if/else/fi or while/endwhile are not pair occurenced!");
        }
    }
}
