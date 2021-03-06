/*
 * MainFrame.java
 *
 * Created on June 8, 2007, 3:53 PM
 */

package org.havalinasw.MGUI;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.lang.reflect.Constructor;
import org.havalinasw.botplugins.BotPluginClassLoader;
import java.awt.Dimension;
import java.awt.Component;
import java.io.File;
import java.util.Hashtable;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Icon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import java.util.Iterator;
import javax.swing.JPanel;
import java.awt.Color;
import org.havalinasw.Mazes.Maze;
import kareltherobot.*;
import org.havalinasw.Mazes.MazeCell;
import org.havalinasw.botplugins.RobotWorker;

/**
 *
 * @author  bhgray
 */
@SuppressWarnings("serial")
public class MainFrame extends javax.swing.JFrame implements Directions {
    
    /** Creates new form MainFrame */
    public MainFrame() {
        appInfo = new KJRInfo();
        panels = new ArrayList<JPanel>();
        initComponents();
        //setupMenuBar();
        tabIcon = null;
        numInitPanel = new RobotNumInit();
        worldInitPanel = new WorldInit();
        createTabs();
        initPlugins();
        currentStatus = Status.INIT;
        setPreferredSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800, 600));
    }
    
    private void createTabs() {
        infoPanels.addTab("Number", tabIcon, numInitPanel, "Sets the Number of Robots");
        infoPanels.addTab("World", tabIcon, worldInitPanel, "Creates the Robot World");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dragonIcon = new javax.swing.JLabel();
        masterFairLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        mainContent = new javax.swing.JPanel();
        infoPanels = new javax.swing.JTabbedPane();
        finishButton = new javax.swing.JButton();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MasterFair Robots");
        setMinimumSize(new java.awt.Dimension(800, 600));
        dragonIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/teachopensource/MGUI/dragon.gif")));
        getContentPane().add(dragonIcon);

        masterFairLabel.setFont(new java.awt.Font("Chalkboard", 1, 18));
        masterFairLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        masterFairLabel.setText("MasterFair 2007");
        getContentPane().add(masterFairLabel);

        titleLabel.setFont(new java.awt.Font("Chalkboard", 1, 18));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("AI Seminar");
        getContentPane().add(titleLabel);

        mainContent.setLayout(new java.awt.GridLayout());

        mainContent.setMaximumSize(new java.awt.Dimension(1280, 800));
        mainContent.setMinimumSize(new java.awt.Dimension(200, 100));
        infoPanels.setMinimumSize(new java.awt.Dimension(200, 100));
        mainContent.add(infoPanels);

        getContentPane().add(mainContent);

        finishButton.setBackground(java.awt.Color.white);
        finishButton.setText("Finish");
        finishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishButtonActionPerformed(evt);
            }
        });

        getContentPane().add(finishButton);

        pack();
    }
    
    private void finishButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // gather all the information into appInfo
        int tabCount = infoPanels.getTabCount();
        if (tabCount < 3) {
            JOptionPane.showMessageDialog(this,
                    "You must first initialize at least one robot.",
                    "Robot Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (int i = 1; i < infoPanels.getTabCount(); i++) {
            Component currentPanel = infoPanels.getComponentAt(i);
            if (currentPanel instanceof RobotInit) {
                int xPos = ((RobotInit)currentPanel).getXPosition();
                int yPos = ((RobotInit)currentPanel).getYPosition();
                //direction facing
                String facing = ((RobotInit)currentPanel).getDirection();
                Direction facingDirection = null;
                if (facing.equals("North")) {
                    facingDirection = Directions.North;
                } else if (facing.equals("South")) {
                    facingDirection = Directions.South;
                } else if (facing.equals("West")) {
                    facingDirection = Directions.West;
                } else {
                    facingDirection = Directions.East;
                }
                int beepers = ((RobotInit)currentPanel).getNumBeepers();
                Color robotColor = ((RobotInit)currentPanel).getColor();
                String classFile = ((RobotInit)currentPanel).getBotClass();
                appInfo.addRobot(xPos, yPos, facingDirection, beepers, robotColor, classFile);
            } else if (currentPanel instanceof WorldInit) {
                int xDim = ((WorldInit)currentPanel).getXDimension();
                int yDim = ((WorldInit)currentPanel).getYDimension();
                appInfo.setWorldDimension(xDim, yDim);
                // appInfo.setWorldFileLocation(); is already called in WorldInit
                appInfo.setIsMaze(((WorldInit)currentPanel).isMaze());
            }
            if (MainFrame.DEBUG) {
                System.out.println(currentPanel);
            }
        }
        
        initWorld();
        initControllers();
        setVisible(false);
        
        
    }
    
    
    private void startRobotTasks() {
        Iterator it = appInfo.getBotIterator();
        while (it.hasNext()) {
            KJRBotInfo nextBot = (KJRBotInfo)it.next();
            nextBot.getBot().work();
        }
    }
    
    private void initControllers() {
        Iterator it = appInfo.getBotIterator();
        RemoteControl rec = null;
        while (it.hasNext()) {
            KJRBotInfo nextBot = (KJRBotInfo)it.next();
            String myClass = nextBot.getBotClass();
            
            Class c = appInfo.getBotPluginForName(myClass);
            if (c.getName().compareTo("kareltherobot.Robot") == 0) {
                rec = new RemoteControl(
                        nextBot.getY(),
                        nextBot.getX(),
                        nextBot.getDirection(),
                        nextBot.getBeepers(),
                        nextBot.getColor(),
                        nextBot.getBotClass());
                continue;
            }
            Class[] intf = c.getInterfaces();
            for (int j=0; j<intf.length; j++) {
                if (intf[j].getName().equals("org.teachopensource.botplugins.RobotWorker")) {
                    RobotWorker bot = null;
                    Constructor con;
                    try {
                        con = c.getConstructor(int.class, int.class, Direction.class, int.class, Color.class);
                        bot = (RobotWorker)con.newInstance(
                                nextBot.getY(),
                                nextBot.getX(),
                                nextBot.getDirection(),
                                nextBot.getBeepers(),
                                nextBot.getColor());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void initWorld() {
        World.reset();
        String worldLoc = appInfo.getWorldFileLocation();
        Maze m = null;
        if (appInfo.isMaze()) {
            Dimension worldDim = appInfo.getWorldDimension();
            m = new Maze((int)worldDim.getWidth(), (int)worldDim.getHeight());
            m.makeMaze(new MazeCell(1, 1), new MazeCell(m.getXMax(), m.getYMax()), 0, 0);
        }
        if (MainFrame.DEBUG) {
            System.out.println("World file:  " + worldLoc);
        }
        if ( worldLoc != null && worldLoc.length() > 2) {
            // World loaded from file -- might be a maze
            World.readWorld(worldLoc);
            if (appInfo.isMaze()) {
                // yes, I know that m is NOT the maze that they loaded from file, but
                // all the mazes are generated with the same starting point, so it should be OK
                translateBots(m);
            }
        } else if (appInfo.isMaze()) {
            // write the maze to a *.kwld file
            File destination = new File(System.getProperty("user.dir") + File.separator + WorldInit.WORLDS_DIRECTORY + File.separator + appInfo.getMazeFileName() + ".kwld");
            try {
                MainFrame.saveTextFile(m.toKarelString(), destination);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            translateBots(m);
            World.getWorld(m.toKarelString());
        } else {
            Dimension worldDim = appInfo.getWorldDimension();
            World.setSize(worldDim.height, worldDim.width);
        }
        
        World.setDelay(1);
        World.setVisible(true);
    }
    
    
    private void translateBots(Maze m) {
        // really we want a single bot placed at the start, then the others can be randomly placed
        Iterator i = appInfo.getBotIterator();
        KJRBotInfo currentBot = null;
        Random rand = new Random();
        Point p = null;
        if (i.hasNext()) {
            currentBot = (KJRBotInfo)i.next();
            p = m.translateKarelPoint(new Point(m.getStart().X(), m.getStart().Y()));
            if (MainFrame.DEBUG) {
                System.out.println("Translating Robot to point " + p);
            }
            currentBot.setX((int)p.getX());
            currentBot.setY((int)p.getY());
        }
        while (i.hasNext()) {
            // place all the others randomly
            p = new Point(rand.nextInt(m.getKarelMaxX())+m.getKarelMinX()-1, rand.nextInt(m.getKarelMaxY())+m.getKarelMinY()-1);
            currentBot = (KJRBotInfo)i.next();
            currentBot.setX((int)p.getX());
            currentBot.setY((int)p.getY());
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        argsconfig = args;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    
    void setupRobotInitPanels() {
        infoPanels.removeAll();
        createTabs();
        int robotNumber = numInitPanel.getNumRobots();
        for (int i = 1; i <= robotNumber; i++) {
            RobotInit newPanel = new RobotInit(i);
            infoPanels.addTab("Robot" + i, tabIcon, newPanel, "Options for Robot #" + i);
        }
    }
    
    public static KJRInfo getAppInfo() {
        return appInfo;
    }
    
    private void initPlugins() {
        // get the directory and loop through all the class files there
        File dir = new File(System.getProperty("user.dir") + File.separator + PLUGINS_DIRECTORY);
        if (MainFrame.DEBUG) {
            System.out.println("Plugins: " + dir);
        }
        ClassLoader cl = new BotPluginClassLoader(dir);
        if (dir.exists() && dir.isDirectory()) {
            // we'll only load classes directly in this directory -
            // no subdirectories, and no classes in packages are recognized
            String[] files = dir.list();
            for (int i=0; i<files.length; i++) {
                try {
                    // only consider files ending in ".class"
                    if (! files[i].endsWith(".class"))
                        continue;
                    
                    Class c = cl.loadClass(files[i].substring(0, files[i].indexOf(".")));
                    Class[] intf = c.getInterfaces();
                    for (int j=0; j<intf.length; j++) {
                        if (MainFrame.DEBUG) {
                            System.out.println("Plugin interface:  " + intf[j].getName());
                        }
                        if (intf[j].getName().equals("org.teachopensource.botplugins.RobotWorker")) {
                            // the problem:  can't change a robot after you construct
                            // so we will store the binary class names instead so that we
                            // can construct on the fly
                            appInfo.addBotPlugin(c.getName(), c);
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("File " + files[i] + " does not contain a valid BotPluginFunction class.");
                }
            }
            appInfo.addBotPlugin(kareltherobot.Robot.class.getName(),kareltherobot.Robot.class);
        }
        
    }
    
    public static void saveTextFile(String contents, File file) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.print(contents);
        out.close();
    }
  
    public static void copyToDir(File file,File dir){
        try {
            FileChannel srcChannel = new FileInputStream(file).getChannel();
            FileChannel dstChannel = new FileOutputStream(dir).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private javax.swing.JLabel dragonIcon;
    private javax.swing.JButton finishButton;
    private javax.swing.JTabbedPane infoPanels;
    private javax.swing.JPanel mainContent;
    private javax.swing.JLabel masterFairLabel;
    private javax.swing.JLabel titleLabel;
    
    private static KJRInfo appInfo;
    private Icon tabIcon;
    private RobotNumInit numInitPanel;
    private ArrayList<JPanel> panels;
    private WorldInit worldInitPanel;
    public static Status currentStatus;
    private JPanel currentPanel;
    private final static int NUMROBOTSPANEL = 0;
    public final static boolean DEBUG = false;
    public static enum Status {INIT, ROBOTINIT, WORLDINIT};
    public static final String PLUGINS_DIRECTORY = "bots";
    
    
    // hashtable for plugins
    private static Hashtable plugins = null;
    // initial plugin config
    private static String[] argsconfig;
    
    
}
