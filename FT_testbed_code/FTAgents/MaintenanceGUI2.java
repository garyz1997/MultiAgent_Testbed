package FTAgents;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MaintenanceGUI2 extends JFrame {
	private Resource4 myAgent;
    	private JButton jcomp1;
    	private JButton jcomp2;
    	private JLabel jcomp3;
	
	MaintenanceGUI2(Resource4 a) 
	{
		super(a.getLocalName());
		
		myAgent = a;
		

		class panel extends JPanel 
		{
		    private JButton jcomp1;
		    private JButton jcomp2;
		    private JLabel jcomp3;

		    public panel() 
		    {
		        //construct components
		        jcomp1 = new JButton ("Accept");
		        jcomp1.addActionListener
		        	(new ActionListener() 
		        		{
							public void actionPerformed(ActionEvent ev) 
							{
								try 
								{//insert what to do when accept
									myAgent.humanAcceptMaintenance();
								}
								catch (Exception e) 
								{
									JOptionPane.showMessageDialog(MaintenanceGUI2.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
								}
							}
						} 
					);
		        jcomp2 = new JButton ("Reject");
		        jcomp2.addActionListener
		        	(new ActionListener() 
		        		{
							public void actionPerformed(ActionEvent ev) 
							{
								try 
								{//insert what to do when decline
									myAgent.humanDeclineMaintenance();
								}
								catch (Exception e) 
								{
									JOptionPane.showMessageDialog(MaintenanceGUI2.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
								}
							}
						} 
					);
		        jcomp3 = new JLabel ("<html><center>Resource 2 needs maintenance</html>");

		        //adjust size and set layout
		        setPreferredSize (new Dimension (250, 100));
		        setLayout (null);

		        //add components
		        add (jcomp1);
		        add (jcomp2);
		        add (jcomp3);

		        //set component bounds (only needed by Absolute Positioning)
		        jcomp1.setBounds (20, 70, 100, 20);
		        jcomp2.setBounds (125, 70, 100, 20);
		        jcomp3.setBounds (30, 20, 200, 25);
		    }
		}
        this.getContentPane().add(new panel());
        this.pack();
        this.setVisible(true);		
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//showGui();
			}
		} );
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
