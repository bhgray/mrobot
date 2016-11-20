/*
 * RobotWorker.java
 *
 * Created on June 12, 2007, 1:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.havalinasw.botplugins;

import java.awt.Color;
import kareltherobot.*;

/**
 *
 * @author bhgray
 */
public interface RobotWorker {
    public void init();
    public boolean setParam(String key, Object param);
    public void solve();
    public void work();
}
