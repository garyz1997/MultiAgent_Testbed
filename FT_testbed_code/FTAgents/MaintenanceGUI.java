package FTAgents;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MaintenanceGUI extends JFrame {
	private Resource2 myAgent;
	
	MaintenanceGUI(Resource2 a) {
		super(a.getLocalName());
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.add(new JLabel("Resource 2 needs maintenance"));
		getContentPane().add(p, BorderLayout.CENTER);
		p.setLayout(new GridLayout(1, 2));
		JButton jcomp1;
    	JButton jcomp2;
		
		//construct components
        jcomp1 = new JButton("Accept");
        jcomp1.addActionListener
        	(new ActionListener() 
        		{
					public void actionPerformed(ActionEvent ev) 
					{
						try 
						{//insert wheat to do when accept
							myAgent.humanAcceptMaintenance();
						}
						catch (Exception e) 
						{
							JOptionPane.showMessageDialog(MaintenanceGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
						}
					}
				} 
			);

        jcomp2 = new JButton("Reject");
        jcomp2.addActionListener
        	(new ActionListener() k
        		{
					public void actionPerformed(ActionEvent ev) 
					{
						try 
						{//insert what to do when decline
							myAgent.humanDeclineMaintenance();
						}
						catch (Exception e) 
						{
							JOptionPane.showMessageDialog(MaintenanceGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
						}
					}
				} 
			);
		p = new JPanel();
        p.add(jcomp1);
        p.add(jcomp2);
		getContentPane().add(p, BorderLayout.SOUTH);


		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//showGui();
			}
		} );
		
		//prevents users from resizing window
		setResizable(false);

		
	}
	
	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}
