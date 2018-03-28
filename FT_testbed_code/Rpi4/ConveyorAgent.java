import jade.core.Agent;
import java.util.*;
import jade.core.behaviours.*;
import java.io.*;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;


public class ConveyorAgent extends Agent
{
    protected void setup()
    {
    	// Registration with the DF 
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("ConveyorAgent"); 
		sd.setName(getName());
		sd.setOwnership("Rpi4");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this,dfd);
			WaitPingAndRunBehaviour PingBehaviour = new  WaitPingAndRunBehaviour(this);
			addBehaviour(PingBehaviour);
		} catch (FIPAException e) {
			System.out.println("Agent "+getLocalName()+" - Cannot register with DF");
			doDelete();
		}
    	//System.out.println("Hello from JADE!");
		//runPython("python conv4.py");
		//System.out.println("Goodbye from JADE!");
    }

	private class WaitPingAndRunBehaviour extends CyclicBehaviour {

		public WaitPingAndRunBehaviour(Agent a) {
			super(a);
		}

		public void action() {//run conv4 when receive ping
			ACLMessage msg = myAgent.receive();
			if(msg != null){
				ACLMessage reply = msg.createReply();

				if(msg.getPerformative()== ACLMessage.REQUEST){
					String content = msg.getContent();
					if ((content != null) && (content.indexOf("ping") != -1)){
						System.out.println("Agent "+getLocalName()+" - Received PING Request from "+msg.getSender().getLocalName());
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent("running conveyor 4");
						runPython("python conv4.py");
					}
					else{
						System.out.println("Agent "+getLocalName()+" - Unexpected request ["+content+"] received from "+msg.getSender().getLocalName());
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("( UnexpectedContent ("+content+"))");
					}

				}
				else {
					System.out.println("Agent "+getLocalName()+" - Unexpected message ["+ACLMessage.getPerformative(msg.getPerformative())+"] received from "+msg.getSender().getLocalName());
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("( (Unexpected-act "+ACLMessage.getPerformative(msg.getPerformative())+") )");   
				}
				send(reply);
			}
			else {
				block();
			}
		}
	}

	protected void sendping(String receiver)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				//msg.setPerformative(ACLMessage.REQUEST);
		msg.setContent("ping");  
		msg.addReceiver( new AID(receiver, AID.ISLOCALNAME) );
		send(msg);
	}



    protected void runPython(String fileName)
	{
		try
		{
			Process p = Runtime.getRuntime().exec(fileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String output = in.readLine();
			while (output != null)
			{
				System.out.println(output);
				output = in.readLine();
			}
		}
		catch (IOException ioe)
		{
			System.out.println("ERROR RUNNING PYTHON FILE!");
		}		
	}
}
