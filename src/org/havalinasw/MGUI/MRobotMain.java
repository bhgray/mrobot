package org.havalinasw.MGUI;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTabbedPane;

import org.havalinasw.MGUI.MainFrame.Status;
import org.havalinasw.Mazes.Maze;
import org.havalinasw.Mazes.MazeCell;
import org.havalinasw.botplugins.BotPluginClassLoader;
import org.havalinasw.botplugins.RobotWorker;

import com.sun.tools.javac.Main;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import kareltherobot.Directions;
import kareltherobot.World;
import kareltherobot.Directions.Direction;

public class MRobotMain extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JLabel appIcon = null;

    private JButton finishButton = null;

    private JTabbedPane infoPanels = null;
    
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
        
    /**
     * This method initializes finishButton 
     *  
     * @return javax.swing.JButton  
     */
    private JButton getFinishButton() {
        if (finishButton == null) {
            finishButton = new JButton();
            finishButton.setText("Finish");
            finishButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    finishButtonActionPerformed(e);
                }
            });
        }
        return finishButton;
    }

    /**
     * This method initializes infoPanels   
     *  
     * @return javax.swing.JTabbedPane  
     */
    private JTabbedPane getInfoPanels() {
        if (infoPanels == null) {
            infoPanels = new JTabbedPane();
        }
        return infoPanels;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MRobotMain thisClass = new MRobotMain();
                thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                thisClass.setVisible(true);
            }
        });
    }

    /**
     * This is the default constructor
     */
    public MRobotMain() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(800, 600);
        this.setPreferredSize(new Dimension(800, 600));
        this.setMinimumSize(new Dimension(800, 600));
        this.setContentPane(getJContentPane());
        this.setTitle("JFrame");
        appInfo = new KJRInfo();
        panels = new ArrayList<JPanel>();
        tabIcon = null;
        numInitPanel = new RobotNumInit();
        worldInitPanel = new WorldInit();
        createTabs();
        initPlugins();
        currentStatus = Status.INIT;
        getFinishButton().setEnabled(false);

    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            appIcon = new JLabel();
            appIcon.setText("MasterFair 2007");
            appIcon.setHorizontalAlignment(SwingConstants.CENTER);
            appIcon.setIcon(new ImageIcon(getClass().getResource("/org/teachopensource/MGUI/dragon.gif")));
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(appIcon, BorderLayout.NORTH);
            jContentPane.add(getFinishButton(), BorderLayout.SOUTH);
            jContentPane.add(getInfoPanels(), BorderLayout.CENTER);
        }
        return jContentPane;
    }
    
    private void createTabs() {
        infoPanels.addTab("Number", tabIcon, numInitPanel, "Sets the Number of Robots");
        infoPanels.addTab("World", tabIcon, worldInitPanel, "Creates the Robot World");
    }
    
    private void compilePlugins(File locationDir) {
        if (locationDir.exists() && locationDir.isDirectory()) {
            String[] files = locationDir.list();
            for (int i=0; i< files.length; i++) {
                int compileReturnCode = -1;
                ArrayList<String> args = new ArrayList<String>();
                args.add("-cp");
                args.add("lib/KarelJRobot.jar:/Users/bhgray/Desktop/mrobot.jar");
                try {
                    if ( ! files[i].endsWith(".java")) 
                        continue;
                    args.add(files[i]);
                    //OutputStream out = new OutputStream(System.out);
                    String[] argsArray = new String[10];
                    argsArray = Arrays.trim(args.toArray(argsArray));
                    int status = Main.compile(args.toArray(argsArray));
                    //compileReturnCode = com.sun.tools.javac.Main.compile(new String[] {files[i]});
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(compileReturnCode);
                }
            }
        }
    }
    
       private void initPlugins() {
            // get the directory and loop through all the class files there
            File dir = new File(System.getProperty("user.dir") + File.separator + PLUGINS_DIRECTORY);
            if (MainFrame.DEBUG) {
                System.out.println("Plugins: " + dir);
            }
            compilePlugins(dir);
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
        
       private void finishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishButtonActionPerformed
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
        void setupRobotInitPanels() {
            infoPanels.removeAll();
            createTabs();
            int robotNumber = numInitPanel.getNumRobots();
            for (int i = 1; i <= robotNumber; i++) {
                RobotInit newPanel = new RobotInit(i);
                infoPanels.addTab("Robot" + i, tabIcon, newPanel, "Options for Robot #" + i);
            }
            getFinishButton().setEnabled(true);
        }
        
        public static KJRInfo getAppInfo() {
            return appInfo;
        }

}
