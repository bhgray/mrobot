/** In order for this plugin to function properly,
 * it must be loaded by the SimpleEdit application
 * and the proper system properties set before execution.
 *
 * For example, the following command, entered on a single line,
 * invokes the JVM and tells the system to display the About menu item
 * in the Mac OS X application menu.
 *
 * java -Dcom.apple.mrj.application.apple.menu.about.name=SimpleEdit
 * com.wiverson.macosbook.plugin.FinderIntegrationPlugin
 */
package org.havalinasw.mplugins;

import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJOpenApplicationHandler;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJQuitHandler;
import com.apple.mrj.MRJOpenDocumentHandler;
import com.apple.mrj.MRJAboutHandler;

public class FinderIntegration 
    implements  MRJOpenApplicationHandler, 
                MRJQuitHandler, 
                MRJPrefsHandler, 
                MRJAboutHandler
{
    
    /** Creates a new instance of FinderIntegration */
    public FinderIntegration(  )
    {
    }
    
    // Only want to install this once per application
    // to avoid getting multiple event notifications
    private static boolean installed = false;

    public void execute(  )
    {
        if(!installed)
        {            
           // Enables the menu item
           MRJApplicationUtils.registerPrefsHandler(this);

           // Overrides the default System.exit(  ) behavior.
           MRJApplicationUtils.registerQuitHandler(this);
            
           // Requires com.apple.mrj.application.apple.menu.about.name=Application
           // system property to be set to appear.
           MRJApplicationUtils.registerAboutHandler(this);
            
           // These require the application to be properly bundled for Mac OS X
           // for the events to be dispatched
           MRJApplicationUtils.registerOpenApplicationHandler(this);
            
           installed = true;
        }
    }

    // We only need one instance of the About dialog.
    static JDialog AboutDialog = null;

    public void handleAbout(  )
    {
        new DoAbout().start(  );
    }
        
    /** It may seem a bit strange to create a new Thread
     * just to display an about box.
     *
     * Unfortunately, due to the way the System interacts
     * between the native Carbon libraries and the JVM,
     * displaying a dialog will lock the user interface,
     * leaving kill to terminate the app.
     *
     * This simple thread just hangs on to a singleton
     * dialog, creating a new dialog if it's the first
     * time the dialog is displayed, hiding and reshowing
     * the dialog as needed.
     * 
     * This isn't needed in the later Apple JVM's (including 
     * JDK 1.4), but some earlier releases required this.
     *
     */
    class DoAbout extends Thread
    {
        public void run(  )
        {
            if(AboutDialog == null)
            {
                AboutDialog = new JDialog(  );
                AboutDialog.setResizable(false);
                AboutDialog.setTitle("About MRobot");
                AboutDialog.setSize(350, 150);
                Dimension screensize = 
                   java.awt.Toolkit.getDefaultToolkit().getScreenSize(  );
                int width = 
                   new Double((screensize.getWidth() - 350) / 2).intValue(  );
                int height = 
                   new Double((screensize.getHeight() / 2) - 150).intValue(  );
                AboutDialog.move(width, height);
                JLabel myAppTitle = new JLabel(  );
                myAppTitle.setHorizontalAlignment(myAppTitle.CENTER);
                myAppTitle.setText("MRobot (c) 2007");
                AboutDialog.getContentPane(  ).add(myAppTitle);
            }
            AboutDialog.show(  );
        }
    }

    /** Note that the application requires Mac OS X bundling
     * (as described in a later chapter) to be enabled.
     * The techniques for writing these handlers are similar
     * to the rest of the add-ons.
     * 
     * Typically, you will want to use these handlers to call
     * your standard File -> Open... routines, simply bypassing
     * the standard file dialogs.
     */
    
    public void handleOpenApplication(  )
    {
        new DoOpenApplication().start(  );
    }
    
    class DoOpenApplication extends Thread
    {
        public void run(  )
        {
            System.out.println("Open Application");
        }
    }
 
     public void handlePrefs(  ) throws java.lang.IllegalStateException
    {
        new org.havalinasw.mplugins.FinderIntegration.DoPrefs().start(  );
    }
    
    // This is the one preference we are tracking, which
    // only relates to Mac OS X specific behavior anyways.
    // Note that we aren't persisting the user's preferences.
    public static boolean pref_askToClose = true;
    
    static JDialog PrefsDialog = null;
    class DoPrefs extends Thread
    {
        public void run(  )
        {
            if(PrefsDialog == null)
            {
                PrefsDialog = new JDialog(  );
                PrefsDialog.setResizable(false);
                PrefsDialog.setTitle("Simple Edit Preferences");
                PrefsDialog.setSize(300, 150);
                Dimension screensize = 
                     java.awt.Toolkit.getDefaultToolkit().getScreenSize(  );
                int width = 
                     new Double((screensize.getWidth()  - 300) / 2).intValue(  );
                int height = 
                     new Double((screensize.getHeight() / 2) - 150).intValue(  );
                PrefsDialog.move(width, height);
                
                javax.swing.JCheckBox myQuitPrefButton = 
                     new javax.swing.JCheckBox("Confirm Before Quit");
                myQuitPrefButton.setHorizontalAlignment(
                     javax.swing.SwingConstants.CENTER);
                myQuitPrefButton.setSelected(true);
                myQuitPrefButton.addItemListener(new ItemListener(  )
                {
                    public void itemStateChanged(ItemEvent evt)
                    {
                        pref_askToClose = 
                             (evt.getStateChange(  ) == ItemEvent.SELECTED);
                    }
                });
                PrefsDialog.getContentPane(  ).add(myQuitPrefButton);
            }
            
            PrefsDialog.show(  );
        }
    }

    /* Note that the Quit thread is slightly more complex
     * than the other threads.
     *
     * There is a bug which manifests as of Mac OS X 10.1, JDK 1.3.1
     * Update 1 which causes it to generate multiple events
     * for a single selection of the Quit menu item on the native
     * application menu.
     *
     * If you know you'll only be running on JDK 1.4 or later, this
     * isn't necessary.
     *
     * Therefore, to avoid a deadlock, a new thread is created,
     * tracked, and communicated with.  It's arguably overkill for
     * what is supposed to be a modal quit confirmation dialog...
     * but it works.
     */
    org.havalinasw.mplugins.FinderIntegration.DoQuit quitThread = null;

    public void handleQuit(  ) throws java.lang.IllegalStateException
    {
        if(pref_askToClose)
        {
            if(quitThread == null)
            {
                quitThread = new DoQuit(  );
                // Make sure the application doesn't hang around
                // waiting for this thread.
                quitThread.setDaemon(true);
                quitThread.start(  );
            }
            else
                quitThread.show(  );
        } else
        {
            // If the user set a preference not to be 
            // prompted, go ahead and bail out.
            System.exit(0);
        }
    }
    
    class DoQuit extends Thread
    {
        private  QuitConfirmJDialog myQuitDialog = null;
        // Operations on ints are inherently atomic,
        // and we aren't doing anything too fancy
        // requires fancier semaphores & locking.
        int showDialog = 0;
        public void show(  )
        {
            showDialog = 1;
        }
        
        public void run(  )
        {
            if(myQuitDialog == null)
                myQuitDialog = 
                     new QuitConfirmJDialog(new javax.swing.JFrame(  ), true);
            
            showDialog = 1;
            // Now that the Quit dialog is ready, go ahead and sit
            // around waiting for a semaphore notification to redisplay.
            while(true)
            {
                if(showDialog == 1)
                {
                    myQuitDialog.show(  );
                    showDialog = 0;
                }
            }
        }
    }
}