package org.havalinasw.mplugins;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Frame;

public class QuitConfirmJDialog extends JDialog
{
    
    /**
     * Creates new form QuitConfirmJDialog
     */
    public QuitConfirmJDialog(Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents(  );
    }
    
    private void initComponents(  )
    {
        buttonPanel = new javax.swing.JPanel(  );
        cancelButton = new javax.swing.JButton(  );
        okButton = new javax.swing.JButton(  );
        jLabel1 = new javax.swing.JLabel(  );

        setTitle("Confirm Quit");
        setModal(true);
        setResizable(false);
        addFocusListener(new java.awt.event.FocusAdapter(  )
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                formFocusHandler(evt);
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter(  )
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                closeDialog(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener(  )
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonHandler(evt);
            }
        });

        buttonPanel.add(cancelButton);

        okButton.setText("OK");
        this.getRootPane(  ).setDefaultButton(okButton);
        okButton.addActionListener(new java.awt.event.ActionListener(  )
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okButtonHandler(evt);
            }
        });

        buttonPanel.add(okButton);

        getContentPane(  ).add(buttonPanel, java.awt.BorderLayout.SOUTH);

        jLabel1.setText("Are you sure you want to quit?");
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane(  ).add(jLabel1, java.awt.BorderLayout.CENTER);

        pack(  );
        java.awt.Dimension screenSize =
        java.awt.Toolkit.getDefaultToolkit().getScreenSize(  );
        setSize(new java.awt.Dimension(366, 116));
        setLocation((screenSize.width-366)/2,(screenSize.height-116)/2);
    }

    private void formFocusHandler(java.awt.event.FocusEvent evt)
    {
        okButton.requestFocus(  );
    }

    private void cancelButtonHandler(java.awt.event.ActionEvent evt)
    {
        okButton.requestFocus(  );
        this.hide(  );
    }

    private void okButtonHandler(java.awt.event.ActionEvent evt)
    {
        System.exit(0);
    }
    
    private void closeDialog(java.awt.event.WindowEvent evt)
    {
        this.hide(  );
    }
    
    public static void main(String args[])
    {
        new QuitConfirmJDialog(new JFrame(), false).show(  );        
    }
    
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    
}