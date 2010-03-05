/**
 * 
 */
package org.hyk.sip.test.launcher;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.hyk.sip.test.HykSipUnitTestCase;

/**
 * @version 0.1.0
 * @author Silvis Kingwon
 *
 */
public class Launcher extends HykSipUnitTestCase
{
    private static final String USAGE = "hyk-jsipunit: A sip test framework\r\n\r\n" + 
                                        "Usage: hst -d <directory>\r\n"
                                       +"           -v";
    private static String[] scripts = null;
    
    protected void onSetUp()
    {
        isScriptInClasspath = false;
    }
    
    public String[] getScriptLocations()
    {
        return scripts;
    }
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        if(null == args || (args.length != 1 && args.length != 2))
        {
            //do nothing
        }
        else
        {
            if(args.length == 1)
            {
                if(args[0].trim().equals("-v"))
                {
                    
                }
            }
            else
            {
                if(args[0].trim().equals("-d"))
                {
                    String directory = args[1].trim();
                    File dir = new File(directory);
                    if(dir.isDirectory())
                    {
                        Launcher launcher = new Launcher();
                        String[] files = dir.list();
                        //System.out.println(dir.getAbsolutePath());
                        List<String> xmlFiles = new LinkedList<String>();
                        for (int i = 0; i < files.length; i++)
                        {
                            if(files[i].endsWith(".xml"))
                            {
                                xmlFiles.add(dir.getAbsolutePath() + System.getProperty("file.separator") + files[i]);
                            }
                        }
                        scripts = new String[xmlFiles.size()];
                        xmlFiles.toArray(scripts);
                        junit.textui.TestRunner.main(new String[]{"org.hyk.sip.test.launcher.Launcher"});
                        return;
                    }
                }
            }
        }
               
        System.err.println("Wrong argument!");
        System.out.println(USAGE);
    }

    
}
