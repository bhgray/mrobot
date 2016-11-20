package org.havalinasw.mplugins;

import org.havalinasw.MGUI.MRobotPlugin;
import org.havalinasw.MGUI.MainFrame;

public class FinderIntegrationPlugin implements MRobotPlugin
{
    
    /** Creates a new instance of FinderIntegrationPlugin */
    public FinderIntegrationPlugin(  )
    {
    }
    
    // Does nothing useful in this context.
    public void doAction(MainFrame frame, java.awt.event.ActionEvent evt)
    {
        return;
    }
    
    // Returns a status string only.
    public String getAction(  )
    {
        if(isMacOS)
            return "Mac OS Installed";
        else
            return "Mac OS Not Available";
    }
    
    boolean isMacOS = false;
    
    /** Checks to see if Mac OS is available. If so,
     * goes ahead and loads the class by name that
     * actually performs the initialization. If not,
     * the class is never loaded. This helps prevent
     * classloader problems on non-Mac OS systems.
     */
    
    public void init(MainFrame frame)
    {
        if(System.getProperty("mrj.version") != null)
            isMacOS = true;
        
        if(isMacOS)
        {
            try
            {
               // This requests the classloader to find a
               // given class by name.  We are using this to
               // establish a firewall between the application
               // and Mac OS X dependencies.  This helps isolate
               // the application logic for organizational purposes,
               // as well as ensure that we won't try to drag Mac OS
               // references into our crossplatform code.
               Class myClass = 
                Class.forName("org.teachopensource.mplugins.FinderIntegration");
                
               Object myObject = myClass.getConstructor((Class)null).newInstance((Object)null);
               myClass.getDeclaredMethod("execute", (Class)null).invoke(myObject, (Object)null);;
            } catch (Exception e)
            {
               System.out.println("Unable to load FinderIntegration module.");
               e.printStackTrace(  );
            }
        }
    }
}

					    