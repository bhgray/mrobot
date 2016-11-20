package org.havalinasw.MGUI;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Dimension;

public class LabInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public LabInfoPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(378, 252);
		this.setLayout(new GridBagLayout());
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
