package agents.consumer;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {
	
	
	private transient ConsumerGui ConsumerGui;

	@Override
	protected void setup() {
		if(getArguments().length == 1) {
			ConsumerGui = (ConsumerGui) getArguments()[0];
			ConsumerGui.setAgent(this); 
		}

		ParallelBehaviour paraBeh = new ParallelBehaviour();
		paraBeh.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage msg = receive();
				if(msg !=null) {
					switch (msg.getPerformative()) {
					case ACLMessage.CONFIRM:
						ConsumerGui.logMessage("meilleure prix pour vous.. ",msg);
						break;
					case ACLMessage.INFORM:
						ConsumerGui.logMessage("votre demande est encore.... ",msg);
						break;

					default:
						break;
					}
					
				}
				else { block();}
				
			}
		});
		
		addBehaviour(paraBeh);
		
	}
	
	

	@Override
	public void onGuiEvent(GuiEvent args) {

		if(args.getType() == 1) {
			String livre = (String) args.getParameter(0);

			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent(livre);
			msg.addReceiver(new AID("Acheteur",AID.ISLOCALNAME));
			send(msg);
		}

	}
}
