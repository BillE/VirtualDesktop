package com.dataclear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dataclear.SystemCommandExecutor;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DataClear extends Application {
    private static final String TITLE_TEXT = "DataClear Navigation";
    
    private static final String NETFLIX_TEXT = "Netflix";
    private static final String BROWSER_TEXT = "Local Browser";
    private static final String DESKTOP_TEXT = "Secure Desktop";
    
    private static final String NETFLIX_URL = "http://www.netflix.com";
    private static final String BROWSER_URL = "http://www.dataclear.com";
    private static final String DESKTOP_URL = "http://10.233.3.24/Citrix/StoreWeb/";
    
    private static final String FIREFOX_PATH = "/usr/bin/firefox";
    private static final String CHROME_PATH = "/opt/google/chrome/google-chrome";
    
    private static final String USER_NAME_PROMPT = "User Name:";
    private static final String PASSWORD_PROMPT = "Password:";
    private static final String WELCOME_TEXT = "Welcome";
    private static final String SIGN_IN_BUTTON_TEXT = "Sign In";
    private static final String AUTHORIZING_TEXT = "Authorizing";
    
    private static final String GET_WIFI_LIST_COMMAND = "nmcli dev wifi list";
    
    /**
     * sudo /usr/bin/nmcli dev wifi connect <SSID> password <password>
     */
    private static final String CONNECT_TO_WIFI_COMMAND = "sudo /usr/bin/nmcli dev wifi connect";
    private static final String CONNECT_TO_WIFI_COMMAND_PWD = "password";
    
    // private static final String CONNECT_TO_WIFI_COMMAND_IFACE = "iface wlp2s0";
    private static final String WIFI_PASSWORD_PROMPT = "Enter WIFI password";
    private static final String WIFI_SUBMIT_BUTTON_TEXT = "Submit";
    
    private static final ObservableList<String> wifiConnections = 
            FXCollections.observableArrayList();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // check internet connection
        primaryStage.setTitle(TITLE_TEXT);
        if (! isConnected()) {
            setChooseWifiStage(primaryStage);
        } else {
            setLoginStage(primaryStage);
        }
    }
    
    /**
     * Check to see if device is connected.
     * We may have to put in a time delay for startup when the WiFi connection
     * may not have been made yet.
     * 
     * @return results of connection attempt
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean isConnected() throws IOException, InterruptedException {
        // you need a shell to execute a command pipeline
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add("ping -q -c 1 -W 1 8.8.8.8");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        int result = commandExecutor.executeCommand();

        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
        
        // System.out.println("STDERR");
        
        if (stderr.length() > 1 ) {
            // && stderr.indexOf(ERROR_STRING) != -1
            System.out.println("Internet connection is down.");
            System.out.println(stderr);
            return false;
        }
        return true;
    }
    
    private void setChooseWifiStage(Stage primaryStage) throws IOException, InterruptedException {
        // get all available wifi SSIDs
        // you need a shell to execute a command pipeline
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(GET_WIFI_LIST_COMMAND);

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        int result = commandExecutor.executeCommand();

        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        // System.out.println("Please choose your WIFI connection from the list below: ");
        
        String[] lines = stdout.toString().split("\\n");
        ArrayList<String> SSIDs = new ArrayList<String>();
        
        // TODO: sort SSIDs by signal strength
        for (String s: lines) {
            String[] arr = s.split("\\s+");
            
            // TODO: put this output into a list
            if (arr.length > 8) {
                SSIDs.add(arr[1]);
                System.out.println("SSID: " + arr[1] + "; SECURITY: " + arr[8] 
                        + "; STRENGTH: " + arr[6]);
            }
        }
        
        /**
         * Set the UI
         */
       FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        
        //Creating Hbox and Vbox Panes
        VBox vbox = new VBox(10);
        
        root.setAlignment(Pos.CENTER);
        
        //setting padding to hbox and vbox
        vbox.setPadding(new Insets(5));
        
        //setting border to hbox and vbox using css
        // hbox.setStyle("-fx-border-color: blue;-fx-border-style: solid;-fx-border-width: 5;");
        RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, 
                CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DODGERBLUE),
            new Stop(1, Color.BLACK)
        });

        Label label = new Label("WIFI SELECTION SCREEN");
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        label.setTextFill(gradient1);
        
        // Populate list view from detected SSIDs
        /**
         *  TODO: THIS IS FOR TESTING ONLY! In production, 
         *  throw an error that there are no SSIDs available
         */
        if (SSIDs.size() > 0) {
            wifiConnections.addAll(SSIDs);
        } else {
            wifiConnections.addAll(
                    "ASUS", 
                    "SOMETHING-ELSE", 
                    "NSA-VAN");
        }
        
        final ListView<String> listView = new ListView<String>(wifiConnections);
        listView.setPrefSize(50, 250);
        listView.setEditable(true);
        
        listView.setItems(wifiConnections);
        // listView.setCellFactory(ComboBoxListCell.forListView(names));           
               
        // StackPane root = new StackPane();
        root.getChildren().add(listView);
        
        Label passwordLabel = new Label(WIFI_PASSWORD_PROMPT);
        PasswordField wifiPassword = new PasswordField();
        
        Button submitButton = new Button(WIFI_SUBMIT_BUTTON_TEXT);
        
        //add Label,HBox and VBox to root Pane
        root.getChildren().addAll(label, vbox);
        root.getChildren().addAll(passwordLabel, wifiPassword, submitButton);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle(TITLE_TEXT);
        primaryStage.setScene(scene);
        // primaryStage.setFullScreen(true);
        primaryStage.show();
        
        /*
         * Handle button press action
         */

        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (listView.getSelectionModel().getSelectedItem() == null ) {
                    // set error string: select SSID
                    System.out.println("NO SSID SELECTED.");
                    return;
                }
  
                String SSID = listView.getSelectionModel().getSelectedItem().toString();
                String password = wifiPassword.getText();
                
                // if (SSID.length() < 1) 
                try {
                    if (connectToWifi(SSID, password)) {
                        // move to next screen
                    } else {
                        // display error
                    }
                } catch (IOException | InterruptedException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                    // display error
                }
                
            }
        });
        
        // TODO: find out why this list is different from what's on Ubuntu
        /*
         * ADD:
         * nmcli conn add type wifi con-name <connectionname> ifname wlp3s0 ssid <wifinetworkname>
         *
         * BRING UP:
         * nmcli conn up <connectionname>
         */
        
        // TODO: ensure that wireless network interface is up before doing check
        
        // Result of submit button
        // 1. attempt to connect
        // 2. if connect success, show main navigation screen
        // 3. if connect failure, stay on wifi screen with error message and clear pwd field
    }
    
    /**
     * TODO: add logic (stubbed out with TRUE for now)
     * @return login result
     */
    private boolean isValidLogin(String username, String password) {
        return true;
    }
    
    /**
     * TODO: implement logic
     * @throws InterruptedException 
     * @throws IOException 
     * 
     */
    private boolean connectToWifi(String SSID, String password) throws IOException, InterruptedException {
        System.out.println("Trying to connect WIFI with SSID " + SSID 
                + " and password " + password);
        
        // nmcli d wifi connect <SSID> password <password> iface <WifiInterface>
        // get all available wifi SSIDs
        // you need a shell to execute a command pipeline
 
        String wifiCommand = CONNECT_TO_WIFI_COMMAND + " " + SSID + " " 
                + CONNECT_TO_WIFI_COMMAND_PWD + " " + password; 
        
        System.out.println("CONNECT COMMAND: " + wifiCommand);
        
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(wifiCommand);
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        
        int result = commandExecutor.executeCommand();

        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        // TODO: grep result and return true/false
        
        System.out.println("STDOUT: " + stdout);
        
        System.out.println("STDERR: " + stderr);
        
        return true;
    }
    
    private void setLoginStage(Stage primaryStage) {       
        // Create a GridPane Layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        
        // Add Text, Labels, and Text Fields
        Text scenetitle = new Text(WELCOME_TEXT);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label(USER_NAME_PROMPT);
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label(PASSWORD_PROMPT);
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        
        Button btn = new Button(SIGN_IN_BUTTON_TEXT);
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
        primaryStage.show();
        
        /**
         * Handle the submitted button action
         * 
         */
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText(AUTHORIZING_TEXT);
                setNavigationStage(primaryStage);
            }
        });       
    }
    
    private void setNavigationStage(Stage primaryStage) {
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        
        //Creating Hbox and Vbox Panes
        HBox hbox = new HBox(10);
        
        root.setAlignment(Pos.CENTER);
        
        //setting padding to hbox and vbox
        hbox.setPadding(new Insets(5));
        
        //setting border to hbox and vbox using css
        // hbox.setStyle("-fx-border-color: blue;-fx-border-style: solid;-fx-border-width: 5;");
        RadialGradient gradient1 = new RadialGradient(0, 0, 0.5, 0.5, 1, true, 
                CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.DODGERBLUE),
            new Stop(1, Color.BLACK)
        });

        Label label = new Label(TITLE_TEXT);
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        label.setTextFill(gradient1);
        
        //Controls to be added to the HBox and VBox
        Button netflixButton = new Button(NETFLIX_TEXT);
        Button browserButton = new Button(BROWSER_TEXT);
        Button desktopButton = new Button(DESKTOP_TEXT);
        desktopButton.setDefaultButton(true);

        //Add Some Buttons to HBox Pane
        hbox.getChildren().addAll(netflixButton, browserButton, desktopButton);
        
        //add Label,HBox and VBox to root Pane
        root.getChildren().addAll(label, hbox);
        Scene scene = new Scene(root, 400, 300, Color.BLACK);

        primaryStage.setTitle(TITLE_TEXT);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        
        netflixButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) { 
                // launch browser
                try {
                    Runtime.getRuntime().exec(CHROME_PATH + " " + NETFLIX_URL);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        
        browserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) { 
                // launch browser
                try {
                    Runtime.getRuntime().exec(FIREFOX_PATH + " " + BROWSER_URL);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        desktopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                // launch browser
                try {
                    Runtime.getRuntime().exec(FIREFOX_PATH + " " + DESKTOP_URL);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
