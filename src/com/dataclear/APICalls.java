package com.dataclear;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class APICalls {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String DATACLEAR_API_URL = "https://www.dataclear.com/APIv3.0.asp";
    private static final String CHANGE_PASSWORD_COMMAND = "ChangePass";
    private static final String SECURE_PASSWORD_COMMAND = "SecurePass";
    private static final String AUTHORIZE_USER_COMMAND = "NamePassVerify";

    public static final String TRUE_LOGIN_RESULT = "TRUE";
    public static final String FALSE_LOGIN_RESULT = "FALSE";
    public static final String CHANGE_LOGIN_RESULT = "CHANGE";
    
    public static final String TRUE_CHANGE_PASSWORD_RESULT = "Success";
    public static final String FALSE_CHANGE_PASSWORD_RESULT = "Failure";
    public static final String PASSWORD_COMPLEXITY_RESULT = "COMPLEXITY FAILURE";

    public static boolean isSecurePassword(String loginID) throws Exception  {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(DATACLEAR_API_URL);
        
        String strData = SECURE_PASSWORD_COMMAND + "|" +  loginID;
        String encryptedCommand = ChilkatLibrary.encrypt(strData);
        
        // add header
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("strData", encryptedCommand));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        System.out.println(result);
        return false;        
    }
    
    public static String authorizeUser(String username, String password) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(DATACLEAR_API_URL);        
        
        // TODO: checking API call: we shouldn't need old and new password
        //      putting password in twice now, for testing.
        String strData = AUTHORIZE_USER_COMMAND + "|" +  username + "|" + password + "|" + password;
 
        try {
            String encryptedCommand = ChilkatLibrary.encrypt(strData);
            post.setHeader("User-Agent", USER_AGENT);

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("strData", encryptedCommand));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            HttpResponse response = client.execute(post);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            
            return result.toString();
        } catch (Exception e) {
            throw new Exception();
        }
        

    }
     
    public static String changePassword(String loginID, String oldPassword, String newPassword) throws Exception  {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(DATACLEAR_API_URL);
        
        String strData = CHANGE_PASSWORD_COMMAND + "|" +  loginID + "|" + oldPassword + "|" + newPassword;
        
        try {
            String encryptedCommand = ChilkatLibrary.encrypt(strData);
            
            post.setHeader("User-Agent", USER_AGENT);
    
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("strData", encryptedCommand));
    
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
    
            HttpResponse response = client.execute(post);
    
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
    
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            
            System.out.println(result);
            
            // TODO: confirm result
            return result.toString(); 
        } catch (Exception e) {
            throw new Exception();
        }
    }
    
    // FOR TESTING ONLY
    public static void main (String[] args) {
        
        try {
            String result = authorizeUser("test2","P@$$w0rd!!!@#$");
            switch (result) {
                case APICalls.TRUE_LOGIN_RESULT:
                    System.out.println("True result.");
                    break;
                    
                case APICalls.FALSE_LOGIN_RESULT:
                    System.out.println("False result.");
                    break;
                case APICalls.CHANGE_LOGIN_RESULT:
                    System.out.println("Change result.");
                    break;
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String loginID = "whereisbill";
        String password = "da_DonAustin2112";
        
        try {
            String result = authorizeUser(loginID, password);
            System.out.println(result);
            result = authorizeUser(loginID, "asdfadfsasdf");
            System.out.println(result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
               
                /*
        try {
            String loginID = "whereisbill";
            isSecurePassword(loginID);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String loginID = "whereisbill";
            String newPassword = "";
            String oldPassword = "";
            
            changePassword(loginID, oldPassword, newPassword);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
         
    }

}
