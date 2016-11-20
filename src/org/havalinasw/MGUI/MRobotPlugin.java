/*
 * MRobotPlugin.java
 *
 * Created on June 12, 2007, 6:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.havalinasw.MGUI;

/**
 *
 * @author bhgray
 */
public interface MRobotPlugin {
    
    // Returns a list of actions which will be registered
    // The tool will then be notified if an action is
    // selected.
    public String getAction(  );
    
    // Notification of an action which was registered by
    // this tool.
    public void doAction(MainFrame frame, java.awt.event.ActionEvent evt);
    
    // Called once when the plugin is first loaded
    public void init(MainFrame frame);
    
}
