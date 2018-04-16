
package FTAgents;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class RAGUI extends JFrame {
	private Resource4 myAgent;
	
	RAGUI(Resource4 a) {
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
		        jcomp1 = new JButton("Accept");
		        jcomp1.addActionListener
		        	(new ActionListener() 
		        		{
							public void actionPerformed(ActionEvent ev) 
							{
								try 
								{//insert wheat to do when accept
									myAgent.humanAccept();
								}
								catch (Exception e) 
								{
									JOptionPane.showMessageDialog(RAGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
								}
							}
						} 
					);

		        jcomp2 = new JButton("Reject");
		        jcomp2.addActionListener
		        	(new ActionListener() 
		        		{
							public void actionPerformed(ActionEvent ev) 
							{
								try 
								{//insert what to do when decline
									myAgent.humanDecline();
								}
								catch (Exception e) 
								{
									JOptionPane.showMessageDialog(RAGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
								}
							}
						} 
					);
		        String ServiceList = myAgent.getServiceList();
				jcomp3 = new JLabel (ServiceList);
		       	
		       	//adjust size and set layout
        		setPreferredSize (new Dimension (250, 130));
		        setLayout (null);

		        //add components
		        add(jcomp1);
		        add(jcomp2);
		        add(jcomp3);

		        //set component bounds (only needed by Absolute Positioning)
		        jcomp1.setBounds (20, 100, 100, 20);
		        jcomp2.setBounds (125, 100, 100, 20);
		        jcomp3.setBounds (40, 20, 245, 55);
			}
		}

        this.getContentPane().add(new panel());
        this.pack();
        this.setVisible(true);	
/*
		JPanel p = new JPanel();
        String ServiceList = myAgent.getServiceList();
		p.add(new JLabel(ServiceList));
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
							myAgent.humanAccept();
						}
						catch (Exception e) 
						{
							JOptionPane.showMessageDialog(RAGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
						}
					}
				} 
			);

        jcomp2 = new JButton("Reject");
        jcomp2.addActionListener
        	(new ActionListener() 
        		{
					public void actionPerformed(ActionEvent ev) 
					{
						try 
						{//insert what to do when decline
							myAgent.humanDecline();
						}
						catch (Exception e) 
						{
							JOptionPane.showMessageDialog(RAGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
						}
					}
				} 
			);
		p = new JPanel();
        p.add(jcomp1);
        p.add(jcomp2);
		getContentPane().add(p, BorderLayout.SOUTH);
*/

/* BELOW IS AN ATTEMPT AT A SPACED OUT GUI
		//create components
		JButton jcomp1;
    	JButton jcomp2;
    	JLabel jcomp3;
		
		//construct components
        jcomp1 = new JButton("Accept");
        jcomp1.addActionListener
        	(new ActionListener() 
        		{
					public void actionPerformed(ActionEvent ev) 
					{
						try 
						{//insert wheat to do when accept
						}
						catch (Exception e) 
						{
							JOptionPane.showMessageDialog(RAGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
						}
					}
				} 
			);

        jcomp2 = new JButton("Reject");
        jcomp2.addActionListener
        	(new ActionListener() 
        		{
					public void actionPerformed(ActionEvent ev) 
					{
						try 
						{//insert what to do when decline

						}
						catch (Exception e) 
						{
							JOptionPane.showMessageDialog(RAGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
						}
					}
				} 
			);
        String ServiceList = myAgent.getServiceList();
        jcomp3 = new JLabel(ServiceList);

        //adjust size and set layout
        setPreferredSize (new Dimension(340, 270));
        //setLayout (null);

        //add components
        add(jcomp1);
        add(jcomp2);
        add(jcomp3);

        //set component bounds (only needed by Absolute Positioning)
        jcomp1.setBounds(60, 120, 100, 25);
        jcomp2.setBounds(170, 120, 100, 25);
        jcomp3.setBounds(170, 120, 100, 25);
        //jcomp3.setBounds(60, 5, 245, 55);
*/


		// What to do when the user closes 
		// the GUI using the button on the upper right corner	

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


//set CLASSPATH=C:\Users\garyz\Desktop\jade\lib\jade.jar;C:\Users\garyz\Desktop\jade\lib\commons-codec\commons-codec-1.3.jar;C:\Users\garyz\Desktop\jade\classes;.