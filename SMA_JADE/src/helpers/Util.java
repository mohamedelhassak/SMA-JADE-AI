package helpers;

import javafx.scene.control.Alert;

public  class Util {
     public  static void showMessage(String msg,String name){
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle("Info...");
         alert.setHeaderText("Agent Status..");
         alert.setContentText("Agent '"+name+"' "+msg);
         alert.showAndWait();
     }
}
