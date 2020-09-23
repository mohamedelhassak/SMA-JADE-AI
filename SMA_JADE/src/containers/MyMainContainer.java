package containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class MyMainContainer {
	
	public static void main(String[] args) throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.GUI, "true");
		AgentContainer mainContainer = runtime.createAgentContainer(profileImpl);
		
		mainContainer.start();
	}

}
