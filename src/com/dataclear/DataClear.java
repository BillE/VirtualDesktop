package com.dataclear;

/**
 * Controller application for the DataClear Black Box. 
 * 
 * args: 
 *      settings: brings up settings window
 * 
 * 
 * Even though this is written in Java, it's intended for a specific device running
 * Ubuntu. It is not designed to be portable. There are many calls to *nix specific
 * commands. 
 * 
 * @author <wildboar@protonmail.com>
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class DataClear extends Application {
    // TODO: strings in another file
    // TODO: check in build for for all the dependencies
    private static final String TITLE_TEXT = "DataClear Secure Desktop Computing";
    
    private static final String NETFLIX_TEXT = "Netflix";
    private static final String BROWSER_TEXT = "Local Browser";
    private static final String DESKTOP_TEXT = "Secure Desktop";
    
    private static final String NETFLIX_URL = "http://www.netflix.com";
    private static final String BROWSER_URL = "http://www.dataclear.com";
    private static final String DESKTOP_URL = "http://10.233.3.24/Citrix/StoreWeb/";
    private static final String OPENVPN_SCRIPT_PATH = "/home/dataclear/Scripts/dataclear.ovpn";
    
    private static final String FIREFOX_PATH = "/usr/bin/firefox";
    private static final String CHROME_PATH = "/opt/google/chrome/google-chrome";
    
    private static final String USER_NAME_PROMPT = "User Name:";
    private static final String PASSWORD_PROMPT = "Password:";
    private static final String CURRENT_PASSWORD_PROMPT = "Current Password:";
    private static final String NEW_PASSWORD_PROMPT = "New Password:";
    
    private static final String WELCOME_TEXT = "Welcome To DataClear";
    private static final String SIGN_IN_BUTTON_TEXT = "Sign In";
    private static final String UPDATE_PASSWORD_BUTTON_TEXT = "Update Password";
    private static final String AUTHORIZING_TEXT = "Authorizing...";
    private static final String AUTHORIZATION_FAILURE_TEXT = "Login Failed. Please try again.";
    private static final String PASSWORD_MATCH_ERROR = "New passwords do not match. Please try again.";
    private static final String COMPLEXITY_FAILURE_TEXT = "Password too simple. Please try again.";
    private static final String UNKNOWN_ERROR_TEXT = "Please try again.";
    
    private static final String GET_WIFI_LIST_COMMAND = "nmcli dev wifi list";
    private static final String GET_WIFI_CONNECTED_COMMAND = "iwgetid -r";
    private static final String CONNECT_TO_WIFI_COMMAND = "sudo /usr/bin/nmcli dev wifi connect";
    private static final String DELETE_WIFI_CONNECTION = "sudo /usr/bin/nmcli connection delete id";
    
    private static final String CONNECT_TO_WIFI_COMMAND_PWD = "password";
    private static final String START_VPN_COMMAND = "sudo /usr/sbin/service openvpn start dataclear";
    private static final String STOP_VPN_COMMAND = "sudo /usr/sbin/service openvpn stop dataclear";
    private static final String PING_COMMAND = "ping -q -c 1 -W 1";
    private static final String PING_TEST_HOST = "8.8.8.8";
    private static final String PING_VPN_HOST = "10.233.3.24";
    
    private static final String WIFI_CHANGE_WIFI_TEXT = "Would you like to change to a new Wifi source?";
    private static final String WIFI_PASSWORD_PROMPT = "WiFi password";
    private static final String WIFI_SUBMIT_BUTTON_TEXT = "Submit";
    private static final String WIFI_CONNECTION_ERROR = "Error connecting to WiFi. Try again.";
    private static final String WIFI_NO_SELECTION_ERROR = "No SSID selected.";
    private static final String WIFI_UNAVAILABLE = "No WiFi connections available.";
    private static final String WIFI_CONNECTION_MESSAGE = "Connected to internet.";
    private static final String CHANGE_WIFI_BUTTON = "Change Wifi";
    private static final String CURRENT_SSID_TEXT = "(Connected)";
    private static final String BACK_BUTTON_TEXT = "Back To Settings";
    private static final String COMING_SOON_TEXT = "This feature not yet availabe. Please try again later.";
    private static final String CONNECTING_TEXT = "Connecting to DataClear...";
    private static final String CONNECTED_TEXT = "Connected!";
    
    private static final String WIFI_SETTINGS = "WIFI Settings";
    private static final String CHANGE_PASSWORD = "Change Password";
    private static final String VIEW_HELP = "View Help";
    private static final String FACTORY_RESET = "Factory Reset";
    
    private static final String VPN_CONNECTION_ERROR = "Unable to make a secure connection.";
    private static final String SYSTEM_FAILURE = "System unavailable. Please try later.";
    private static final String AVAILABLE_WIFI_TEXT = "Available WiFi";
    private static final String CHOOSE_WIFI_TEXT = "Please select WiFi and enter password below";
    
    private static final String DATACLEAR_HELP_URL = "https://dataclear.com/help.html";
    
    private static final int HEIGHT_OFFSET = 50;
    private static final int VPN_TIMEOUT = 10000; 
    private static boolean isSettings = false;
    
    private Rectangle2D screenBounds;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        final Parameters p = getParameters();
        List<String> rawParams = p.getRaw();
        String action = "";
        
        screenBounds = Screen.getPrimary().getVisualBounds();
        
        if (! rawParams.isEmpty() ) {
            action = rawParams.get(0);
        }
        
        primaryStage.setTitle(TITLE_TEXT);

        if (action.equals("wifi")) {
            setChooseWifiStage(primaryStage);
        } else if (action.equals("settings")) {
            isSettings = true;
            setSettingsStage(primaryStage);
        } else if (isConnected() ) {
            setLoginStage(primaryStage);
        } else {
            setChooseWifiStage(primaryStage); 
        }
    }
    
    private static class VPN implements Runnable {
        private volatile boolean running = true;
        private String username;
        private String password;
        
        // TODO: start this in a separae thread. Kill the thread when the internal network
        // can be reached by ping
        
        public VPN(String username, String password) {
            setUsername(username);
            setPassword(password);
        }

        public void setUsername(String username) {
            this.username = username;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public void run() {
            List<String> commands = new ArrayList<String>();
            String VPNCommand = OPENVPN_SCRIPT_PATH + " " + username + " " + password;
            commands.add("/bin/sh");
            commands.add("-c");
            commands.add(VPNCommand);
            // System.out.println(VPNCommand);
            SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
            
            try {
                int result = commandExecutor.executeCommand();

                StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
                StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
                
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e2) {
                // return false;
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
    
    /**
     * Attempt to connect to DataClear VPN. 
     * 
     * @return connection result
     * @throws Exception
     */
    private boolean connectToVPN(String username, String password) {
        VPN vpn = new VPN(username, password);
        
        Thread t1 = new Thread(vpn, "T1");
        t1.start();
        
        // test for connection by pinging. if no success after timeout, return error
        long startTime = System.currentTimeMillis(); //fetch starting time
        // System.out.println("Starting vpn timeout loop.");
        while(System.currentTimeMillis() - startTime < VPN_TIMEOUT) {
            // test every second:
            long millis = System.currentTimeMillis();            
            try {
                Thread.sleep(1000 - millis % 1000);
                if (isPingable(PING_VPN_HOST)) {
                    t1.interrupt();
                    t1.join();
                    return true;
                }
                
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;        
    }
    
    /**
     * Check to see if there is an active WIFI connection
     * 
     * @return result
     */
    private boolean isConnected () {
        return isPingable(PING_TEST_HOST);
    }
    
    /**
     * See if a given host is reachable
     * 
     * @return results of connection attempt
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean isPingable(String host)  {
        // you need a shell to execute a command pipeline
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(PING_COMMAND + " " + host);

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        try {
            int result = commandExecutor.executeCommand();

            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
            
            if (stderr.length() > 1 ) {
                return false;
            }
            
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }
    
    /**
     * Call to API to change password
     * 
     * @param loginID
     * @param oldPassword
     * @param newPassword
     * @return result
     */
    private String changePassword(String loginID, String oldPassword, String newPassword) {
        try {
            return APICalls.changePassword(loginID, oldPassword, newPassword);
        } catch (Exception e) {
            return APICalls.FALSE_CHANGE_PASSWORD_RESULT;
        }
    }
    
    /**
     * Get currently active WiFi connection.
     * 
     * @return SSID
     * @throws Exception
     */
    private static String getCurrentSSID() throws Exception {
        String SSID = "";
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(GET_WIFI_CONNECTED_COMMAND);

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        int result = commandExecutor.executeCommand();

        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        String[] lines = stdout.toString().split("\\n");
        ArrayList<String> SSIDs = new ArrayList<String>();
        
        SSID = lines[0];
        return SSID;
    }
    
    /**
     * Show available and connected wifi SSIDs. Button to change connection
     * goes to another screen. This scene appears after clicking "wifi" from
     * the settings menu. 
     * 
     * @param primaryStage
     */
    private void setChangeWifiStage(Stage primaryStage) throws Exception  {
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        VBox vbox = new VBox(10);
        root.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5));        
        
        Label messageText = new Label(WIFI_CHANGE_WIFI_TEXT);
        Button changeWifiButton = new Button(CHANGE_WIFI_BUTTON);
        Button backButton = new Button(BACK_BUTTON_TEXT);
        
        final String currentSSID = getCurrentSSID();
        
        ObservableList<String> wifiConnections = FXCollections.observableArrayList();
        
        // list of all sources, including connected
        ArrayList<String> SSIDs = getAvailableWifi();
        
        // Find connected SSID and mark as connected
        for (int i = 0; i < SSIDs.size(); i++) {
            String SSID = SSIDs.get(i);
            if (SSID.equals(currentSSID)) {
                SSIDs.set(i,  SSID + " " + CURRENT_SSID_TEXT);
            }
        }
        
        final ListView<String> listView = new ListView<String>(wifiConnections);
        
        if (SSIDs.size() > 0) {
            wifiConnections.addAll(SSIDs);
            listView.setPrefSize(50, getPrefHeight(SSIDs.size()));
            listView.setEditable(true);
            listView.setItems(wifiConnections);
            root.getChildren().add(listView);
            root.getChildren().add(vbox);
            root.getChildren().add(messageText);
            root.getChildren().add(changeWifiButton); 
        } else {
            root.getChildren().add(messageText);
            messageText.setText(WIFI_UNAVAILABLE);
        }
               
        if (isSettings) {
            root.getChildren().add(backButton);
        }
        Scene scene = new Scene(root, screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);

        primaryStage.setScene(scene);
        primaryStage.show();
        
        changeWifiButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    setChooseWifiStage(primaryStage);
                } catch (Exception ex) { }
            }
        });
        
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    setSettingsStage(primaryStage);
                } catch (Exception ex) { }
            }
        });
        
    }
    
        /**
         * Show login screen and set logic.
         * If the user has an insecure login, force password change
         */
        private void setLoginStage(Stage primaryStage) {       
            // Create a GridPane Layout
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
    
            Scene scene = new Scene(grid,  screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
            
            primaryStage.setScene(scene);
            
            // Add Text, Labels, and Text Fields
            Text scenetitle = new Text(WELCOME_TEXT);
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);
    
            Label userName = new Label(USER_NAME_PROMPT);
            grid.add(userName, 0, 1);
    
            TextField userTextField = new TextField();
            grid.add(userTextField, 1, 1);
    
            Label passwordLabel = new Label(PASSWORD_PROMPT);
            grid.add(passwordLabel, 0, 2);
    
            PasswordField passwordBox = new PasswordField();
            grid.add(passwordBox, 1, 2);
            
            Button signInButton = new Button(SIGN_IN_BUTTON_TEXT);
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(signInButton);
            grid.add(hbBtn, 1, 4);
            
            Text messageText = new Text();
            grid.add(messageText, 1, 6);
            
            Text secondMessageText = new Text();
            grid.add(secondMessageText, 1, 7);
            
            primaryStage.show();
            
            /**
             * Change message text as button as being pressed.
             */
            signInButton.addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    setMessageText(messageText, CONNECTING_TEXT);
                }
              });
            
            /**
             * Clear message label when user is editing fields
             */
            userTextField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
    
                    setMessageText(messageText, "");
                }
            });
            
            passwordBox.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
    
                    setMessageText(messageText, "");
                }
            });         
            
            
            passwordBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    if (keyEvent.getCode() == KeyCode.ENTER)  {
                        setMessageText(messageText, CONNECTING_TEXT);
                        handleLogin(messageText, secondMessageText, 
                                userTextField, passwordBox, primaryStage);
                    }
                }
            });
            
            /**
             * Attempt connection when button is clicked.
             * 
             */
            signInButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    // Call out to server to authorize
                    handleLogin(messageText, secondMessageText, 
                            userTextField, passwordBox, primaryStage);
                }
            });
        }
        
        private void handleLogin(Text messageText, Text secondMessageText, 
                TextField userTextField, PasswordField passwordBox, Stage primaryStage) {
            try {
                String result = APICalls.authorizeUser(userTextField.getText(), passwordBox.getText());
                switch (result) {
                    case APICalls.TRUE_LOGIN_RESULT:
                        // login successful
                        if ( connectToVPN(userTextField.getText(), passwordBox.getText()) ) {
                            // connected message
                            
                            new Thread(() -> {
                                   secondMessageText.setText("Connected!");
                                   try {
                                       Thread.sleep(2000);
                                   } catch (InterruptedException e) {
                                       e.printStackTrace();
                                   }
                                   
                            }).start();
                            
                            
                            System.exit(0);
                        } else {
                            messageText.setText(VPN_CONNECTION_ERROR);
                        }
                        break;
                        
                    case APICalls.FALSE_LOGIN_RESULT:
                        // login unsuccessful
                        // set error string
                        messageText.setText(AUTHORIZATION_FAILURE_TEXT);
                        break;
                        
                    case APICalls.CHANGE_LOGIN_RESULT:
                        // login successful but insecure (not to change password)
                        setChangePasswordStage(primaryStage);
                        break;
                        
                    default:
                        messageText.setText(SYSTEM_FAILURE);
                        break;
                }
                
            } catch (Exception e1) {
                messageText.setText(UNKNOWN_ERROR_TEXT);
            }
        }
        
    /**
     * Show settings UI and control navigation to other sections
     * 
     * @param primaryStage
     * @throws IOException
     * @throws InterruptedException
     */
    private void setSettingsStage(Stage primaryStage) throws IOException, InterruptedException {
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        VBox vbox = new VBox(10);
        root.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5));
        
        Button wifiButton = new Button(WIFI_SETTINGS);
        Button changePasswordButton = new Button(CHANGE_PASSWORD);
        Button helpButton = new Button(VIEW_HELP);
        Button factoryResetButton = new Button(FACTORY_RESET);
        
        Text messageText = new Text("");
        
        root.getChildren().addAll(vbox);
        root.getChildren().addAll(wifiButton, changePasswordButton, helpButton, factoryResetButton, messageText);
        Scene scene = new Scene(root, screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        /*
         * Handle button press action
         */
        wifiButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    setChangeWifiStage(primaryStage);
                } catch (Exception ex) {

                }
            }
        });
        
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setChangePasswordStage(primaryStage);
            }
        });
        
        helpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // TODO: show help using WebView 
                setHelpStage(primaryStage);
            }
        });
        
        factoryResetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // TODO: placeholder for now
                setFactoryResetStage(primaryStage);
            }
        });
    }
    
    /**
     * Set UI that allows user to select and connect to WIFI
     * 
     * @param primaryStage
     * @throws IOException
     * @throws InterruptedException
     */
    private void setChooseWifiStage(Stage primaryStage) throws IOException, InterruptedException {
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        VBox vbox = new VBox(10);
        root.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5));
        
        Label welcomeLabel = new Label(WELCOME_TEXT);
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        
        Text messageText = new Text();
        
        ObservableList<String> wifiConnections = FXCollections.observableArrayList();
        ArrayList<String> SSIDs = getAvailableWifi();
        wifiConnections.addAll(SSIDs);
        final ListView<String> listView = new ListView<String>(wifiConnections);
        
        Label passwordLabel = new Label(WIFI_PASSWORD_PROMPT);
        PasswordField wifiPassword = new PasswordField();
        Button submitButton = new Button(WIFI_SUBMIT_BUTTON_TEXT);
        Button backButton = new Button(BACK_BUTTON_TEXT);        
        Label availableWiFiLabel =  new Label(AVAILABLE_WIFI_TEXT);
        Label ChooseWifiLabel = new Label(CHOOSE_WIFI_TEXT);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(passwordLabel, 0, 0);
        grid.add(wifiPassword, 1, 0);
        
        if (SSIDs.size() > 0) {
            listView.setPrefSize(50, 250);
            listView.setEditable(true);
            listView.setItems(wifiConnections);
            root.getChildren().add(welcomeLabel);
            root.getChildren().addAll(availableWiFiLabel, listView, ChooseWifiLabel);
            root.getChildren().add(grid);
            root.getChildren().add(submitButton);
        } else {
            messageText.setText(WIFI_UNAVAILABLE);
        }
    
        root.getChildren().add(messageText);
        if (isSettings) {
            root.getChildren().add(backButton);
        }
        
        Scene scene = new Scene(root, screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        submitButton.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                messageText.setText(AUTHORIZING_TEXT);
            }
          });
        
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() { 
            @Override 
            public void handle(final WindowEvent windowEvent) { 
                windowEvent.consume(); 
                }
            });
                
        /*
         * Handle button press action
         */
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                handleConnectToWifi(listView, messageText, wifiPassword, primaryStage);
            }
        });
        
        wifiPassword.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    messageText.setText(AUTHORIZING_TEXT);
                    handleConnectToWifi(listView, messageText, wifiPassword, primaryStage);
                }
            }
        });
        
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    setSettingsStage(primaryStage);
                } catch (Exception ex) { }
            }
        });
    }
    
    private void handleConnectToWifi(ListView<String> listView, Text messageText, 
            PasswordField wifiPassword, Stage primaryStage) {
        if (listView.getSelectionModel().getSelectedItem() == null ) {
            messageText.setText(WIFI_NO_SELECTION_ERROR);
            return;
        }

        String SSID = listView.getSelectionModel().getSelectedItem().toString();
        String password = wifiPassword.getText();
        
        try {
            connectToWifi(SSID, password);
            if (isConnected()) {
                // move to next screen
                if (isSettings) {
                    messageText.setText(WIFI_CONNECTION_MESSAGE);
                } else {
                    setLoginStage(primaryStage);
                }
            } else {
                // display error
                messageText.setText(WIFI_CONNECTION_ERROR);
            }
        } catch (IOException | InterruptedException e1) {
            messageText.setText(WIFI_CONNECTION_ERROR);
        }
    }
    
    
    /**
     * Show the UI that allows for changing password
     * 
     * @param primaryStage
     */
    private void setChangePasswordStage(Stage primaryStage) {
        // Create a GridPane Layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
    
        Scene scene = new Scene(grid, screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        primaryStage.setScene(scene);
        
        // Add Text, Labels, and Text Fields
        Text scenetitle = new Text(WELCOME_TEXT);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
    
        Label userName = new Label(USER_NAME_PROMPT);
        grid.add(userName, 0, 1);
    
        TextField userNameTextField = new TextField();
        grid.add(userNameTextField, 1, 1);
    
        Label currentPasswordLabel = new Label(CURRENT_PASSWORD_PROMPT);
        grid.add(currentPasswordLabel, 0, 2);
    
        PasswordField currentPasswordBox = new PasswordField();
        grid.add(currentPasswordBox, 1, 2);
        
        Label newPasswordLabelOne = new Label(NEW_PASSWORD_PROMPT);
        grid.add(newPasswordLabelOne, 0, 3);
    
        PasswordField newPasswordBoxOne = new PasswordField();
        grid.add(newPasswordBoxOne, 1, 3); 
        
        Label newPasswordLabelTwo = new Label(NEW_PASSWORD_PROMPT);
        grid.add(newPasswordLabelTwo, 0, 4);
    
        PasswordField newPasswordBoxTwo = new PasswordField();
        grid.add(newPasswordBoxTwo, 1, 4);         
        
        Button btn = new Button(UPDATE_PASSWORD_BUTTON_TEXT);
        Button backButton = new Button(BACK_BUTTON_TEXT);
        
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);
        
        Text messageText = new Text("");
        grid.add(messageText, 1, 6);
        
        if (isSettings) {
            grid.add(backButton, 1, 7);
        }
        
        primaryStage.show(); 
        
        /**
         * Change message text on errors.
         */
        btn.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                messageText.setText(AUTHORIZING_TEXT);
            }
          });
        
        currentPasswordBox.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
    
                setMessageText(messageText, "");
            }
        });
        
        newPasswordBoxOne.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
    
                setMessageText(messageText, "");
            }
        });  
        
        newPasswordBoxTwo.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
    
                setMessageText(messageText, "");
            }
        });        
        
        /**
         * Handle button click
         */
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                
                if (! newPasswordBoxOne.getText().equals(newPasswordBoxTwo.getText()) ) {
                    messageText.setText(PASSWORD_MATCH_ERROR);
                    return;
                }
                
                // Call out to server to authorize
                try {
                    String result = changePassword(
                            userNameTextField.getText(), 
                            currentPasswordBox.getText(), 
                            newPasswordBoxOne.getText());
                    switch (result) {
                        case APICalls.TRUE_CHANGE_PASSWORD_RESULT:
                            setLoginStage(primaryStage);
                            break;
                            
                        case APICalls.FALSE_CHANGE_PASSWORD_RESULT:
                            messageText.setText(AUTHORIZATION_FAILURE_TEXT);
                            break;
                            
                        case APICalls.PASSWORD_COMPLEXITY_RESULT:
                            messageText.setText(COMPLEXITY_FAILURE_TEXT);
                            break;
                            
                        default:
                            messageText.setText(SYSTEM_FAILURE);
                            break;
                    }
                    
                } catch (Exception e1) {
                    messageText.setText(UNKNOWN_ERROR_TEXT);
                }
            }
        }); 
        
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    setSettingsStage(primaryStage);
                } catch (Exception ex) { }
            }
        });
        
    }

    /**
     * return to navigation for settings
     * 
     * @param primaryStage
     */
    private void returnToSettingsStage(Stage primaryStage) {
        try {
            setSettingsStage(primaryStage);
        } catch (Exception e) { }
    }
    
    /**
     * Show help page.
     * 
     * TODO: change URL to help.asp
     * TODO: add "back to settings" button
     * 
     * @param primaryStage
     */
    private void setHelpStage(Stage primaryStage) {
        StackPane root = new StackPane();
        final ProgressBar progress = new ProgressBar(); // or you can use ImageView with animated gif instead

        WebView webView = new WebView();
        final Button backButton = new Button(BACK_BUTTON_TEXT);

        root.getChildren().addAll(webView, progress, backButton);
        
        final WebEngine engine = webView.getEngine();
        engine.load(DATACLEAR_HELP_URL);
        

        // updating progress bar using binding
        progress.progressProperty().bind(engine.getLoadWorker().progressProperty());

        engine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                             // hide progress bar then page is ready
                             progress.setVisible(false);
                        }
                    }
                });


        Scene scene = new Scene(root, screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        primaryStage.setScene(scene);
        primaryStage.show(); 
        
    }
    
    /**
     * Get a list of all available WiFi connections
     * TODO: sort by strength.
     * 
     * @return list of SSIDs
     */
    private ArrayList<String> getAvailableWifi() {
        ArrayList<String> SSIDs = new ArrayList<String>();
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(GET_WIFI_LIST_COMMAND);

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        try {
            int result = commandExecutor.executeCommand();
            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            String[] lines = stdout.toString().split("\\n");
            
            for (String s: lines) {
                String[] arr = s.split("\\s+");
                if (arr.length > 8) {
                    SSIDs.add(arr[1]);
                }
            }
        } catch (Exception e) {}
        
        return SSIDs;
    }
    
    /**
     * Connect to WIFI
     * 
     * @throws InterruptedException 
     * @throws IOException 
     * @return result
     */
    private boolean connectToWifi(String SSID, String password) throws IOException, InterruptedException {
        String wifiCommand = CONNECT_TO_WIFI_COMMAND + " " + SSID + " " 
                + CONNECT_TO_WIFI_COMMAND_PWD + " " + password + " name " + SSID; 
        
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(wifiCommand);
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        
        int result = commandExecutor.executeCommand();

        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
        
        // reported result of zero and no Error
        if (result == 0 && stdout.indexOf("Error") == -1) {
            return true;
        }
        
        // On failure delete the connection you just made
        String deleteWifiCommand = DELETE_WIFI_CONNECTION + " " + SSID;
        commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(deleteWifiCommand);
        commandExecutor = new SystemCommandExecutor(commands);    
        
        result = commandExecutor.executeCommand();

        stdout = commandExecutor.getStandardOutputFromCommand();
        stderr = commandExecutor.getStandardErrorFromCommand();
        
        return false;
    }
    
    /**
     * Helper method to set a message, used mostly for error reporting
     * 
     * @param textField
     * @param textValue
     */
    private void setMessageText(Text textField, String textValue) {
        textField.setText(textValue);
    }
    
    /**
     * Calculate x and y coordinates based on number of rows
     * 
     * @param rows
     * @return
     */
    private int getPrefHeight(int rows) {
        if (rows == 0) {
            return 0;
        }
        
        return HEIGHT_OFFSET * rows;
        
    }

    /**
     * Not yet implemented. Will allow user to reset device.
     * 
     * @param primaryStage
     */
    private void setFactoryResetStage(Stage primaryStage) {
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        
        //Creating Hbox and Vbox Panes
        HBox hbox = new HBox(10);
        
        root.setAlignment(Pos.CENTER);
        
        //setting padding to hbox and vbox
        hbox.setPadding(new Insets(5));
        
        Label label = new Label(COMING_SOON_TEXT);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        
        Button backButton = new Button(BACK_BUTTON_TEXT);
        
        //Add Some Buttons to HBox Pane
        hbox.getChildren().add(label);
        
        if (isSettings) {
            hbox.getChildren().add(backButton);
        }
        
        //add Label,HBox and VBox to root Pane
        root.getChildren().addAll(label, hbox);
        if (isSettings) {
            root.getChildren().add(backButton);
        }
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root,  screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);

        primaryStage.setScene(scene);
        primaryStage.show();  
        
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    setSettingsStage(primaryStage);
                } catch (Exception ex) { }
            }
        });
    }
    
    /**
     * Shows navigation buttons.
     * 
     * @deprecated replaced by icons on desktop
     * @param primaryStage
     */
    private void setNavigationStage(Stage primaryStage) {
        System.exit(0);
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10);
        
        //Creating Hbox and Vbox Panes
        HBox hbox = new HBox(10);
        
        root.setAlignment(Pos.CENTER);
        
        //setting padding to hbox and vbox
        hbox.setPadding(new Insets(5));
        
        //setting border to hbox and vbox using css
        // hbox.setStyle("-fx-border-color: blue;-fx-border-style: solid;-fx-border-width: 5;");
        Label label = new Label(TITLE_TEXT);
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        
        //Controls to be added to the HBox and VBox
        Button netflixButton = new Button(NETFLIX_TEXT);
        Button browserButton = new Button(BROWSER_TEXT);
        Button desktopButton = new Button(DESKTOP_TEXT);
        desktopButton.setDefaultButton(true);

        //Add Some Buttons to HBox Pane
        hbox.getChildren().addAll(netflixButton, browserButton, desktopButton);
        
        //add Label,HBox and VBox to root Pane
        root.getChildren().addAll(label, hbox);
        // Scene scene = new Scene(root, 400, 300, Color.BLACK);
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        // Scene scene = new Scene(root);

        primaryStage.setTitle(TITLE_TEXT);
        primaryStage.setScene(scene);
        primaryStage.show();
        setMaximizeScreenOptions(primaryStage);
        
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
    
    /**
     * Single location to set height, width, etc.
     * 
     * @param primaryStage
     */
    private void setMaximizeScreenOptions(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("file:" + System.getProperty("user.home") + "Images/eye.png" ));
        primaryStage.setMaximized(true);
    }
    
    private void setNormalScreenOptions(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("file:" + System.getProperty("user.home") + "Images/eye.png" ));
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
