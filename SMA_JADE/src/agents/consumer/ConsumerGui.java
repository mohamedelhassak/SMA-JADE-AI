package agents.consumer;


import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerGui extends Application {
	
	private ConsumerAgent agent;
	protected ObservableList<String> list;

	public static void main(String[] args) throws Exception {
		
		launch(args);
	}
	
	
	public    void startContainer() throws StaleProxyException {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer simpleContainer = runtime.createAgentContainer(profileImpl);
		
		AgentController agentContainer = simpleContainer
				.createNewAgent("Consumer", "agents.consumer.ConsumerAgent", new Object[] {this});
		agentContainer.start();
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		//create content
		HBox box = new HBox();
		box.setPadding(new Insets(10));
		box.setSpacing(20);
		Label label = new Label("Livre : ");
		TextField textField = new TextField();
		Button button = new Button("Acheter");
		box.getChildren().addAll(label,textField,button);
		
		VBox vbox = new VBox();
		list = FXCollections.observableArrayList();
		ListView<String> messages = new ListView<String>(list);
		vbox.getChildren().add(messages);
		vbox.setPadding(new Insets(10));
		
		
		BorderPane panel = new BorderPane();
		panel.setTop(box);
		panel.setCenter(vbox);
		Scene scene= new Scene(panel,400,400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Counsumer");
		primaryStage.show();
		
		
		button.setOnAction(evt->{
			String livre = textField.getText();
			if(!livre.equals("")) {
				GuiEvent event = new GuiEvent(this, 1);
				event.addParameter(livre);
				agent.onGuiEvent(event );
				
				textField.clear();
				System.out.println(livre);
			}
			
			
		});
		
		
		
	}


	public ConsumerAgent getAgent() {
		return agent;
	}


	public void setAgent(ConsumerAgent agent) {
		this.agent = agent;
	}
	
	public void logMessage(String str ,ACLMessage msg) {
		Platform.runLater(()->{
			list.add(" from " + 
					msg.getSender().getName() +"===>'"+ msg.getContent() +"' "+ str);
		});
		
		
	}
	
}


