package Agents;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CNCBaseClass extends Agent{
	private ClientPython opc=null;
	private int CNCNum;
	private String CNCNotInCycleTag;
	
	protected void setup(){
		//The only argument is the CNC number
		Object[] args = getArguments();
		String arg1 = args[0].toString();
		CNCNum = Integer.parseInt(arg1);
		if(CNCNum==3){
			CNCNotInCycleTag="FromCNC3.Bools.5";
		}
		if(CNCNum==4){
			CNCNotInCycleTag="FromCNC4.Bools.5";
		}
		
		opc=new ClientPython();
		opc.SimulatingTags();
		addBehaviour(new CheckInCycle());
	}
	
	private class CheckInCycle extends CyclicBehaviour{
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchContent("free?");
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null){
				ACLMessage reply = msg.createReply();
				if(opc.getValue(CNCNotInCycleTag).equals("1")){
					reply.setContent("free");
				}
				else{
					reply.setContent("busy");
				}
				send(reply);
			}
			else{
				block();
			}
		}
	}
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println(getAID().getName()+" terminating.");
	}

}
