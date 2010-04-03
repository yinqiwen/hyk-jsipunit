/**
 * 
 */
package org.hyk.sip.test.launcher;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.hyk.sip.test.HykSipUnitTestCase;

/**
 * @version 0.1.0
 * @author yinqiwen
 *
 */
public class Launcher extends HykSipUnitTestCase
{
    private static String[] scripts = null;
    private static String testCaseName;
    
    @Override
	protected void onSetUp()
    {
        isScriptInClasspath = false;
    }
    
    @Override
    protected String getTestCaseName()
	{
		return testCaseName;
	}
    
    @Override
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
    	//OptionBuilder.
    	Option dirInput = OptionBuilder.withArgName("dir").hasArg().withDescription("scripts' directory").withLongOpt("directory").create("d");
		//Option version = OptionBuilder.withLongOpt("version").hasArg(false).withDescription("print the version information and exit").create("v");
		Options options = new Options();
		options.addOption(dirInput);
		//options.addOption( version );
		CommandLineParser parser = new GnuParser();
    	
		CommandLine line = parser.parse(options, args);
		if(line.hasOption("d"))
		{
			String dirStr = line.getOptionValue("d");
			testCaseName = dirStr;
			File dir = new File(dirStr);
            if(dir.isDirectory())
            {
                String[] files = dir.list();
                List<String> xmlFiles = new LinkedList<String>();
                for (int i = 0; i < files.length; i++)
                {
                    if(files[i].endsWith(".xml"))
                    {
                        xmlFiles.add(dir.getAbsolutePath() + System.getProperty("file.separator") + files[i]);
                    }
                }
                if(!xmlFiles.isEmpty())
                {
                	 scripts = new String[xmlFiles.size()];
                     xmlFiles.toArray(scripts);
                     junit.textui.TestRunner.main(new String[]{"org.hyk.sip.test.launcher.Launcher"});
                     return;
                }
                System.out.println(dirStr + " MUST contains XML script files!");        	
            }
            else
            {
            	System.out.println(dirStr + " MUST be a directory!");
            }
		}
		
		HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "hjst.sh/hjst.bat", options );
        System.exit(-1);
		
    }

    
}
