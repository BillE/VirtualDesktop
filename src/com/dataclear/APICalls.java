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
    private static final String DATACLEAR_API_URL = "https://www.dataclear.com/APIv3.asp";
    private static final String CHANGE_PASSWORD_COMMAND = "ChangePass";
    private static final String SECURE_PASSWORD_COMMAND = "SecurePass";

    public static boolean isSecurePassword(String loginID) throws Exception  {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(DATACLEAR_API_URL);
        
        String strData = SECURE_PASSWORD_COMMAND + "|" +  loginID;
        
        String encryptedCommand = ChilkatLibrary.encrypt(strData);
        System.out.println("ENCRYPTED: ");
        System.out.println(encryptedCommand);
        String decryptedCommand = ChilkatLibrary.decrypt(encryptedCommand);
        System.out.println("DECRYPTED: ");
        System.out.println(decryptedCommand);
        
        // add header
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("strData", encryptedCommand));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        System.out.println(result);
        System.out.println(response.toString());
        return false;        
    }
    
    public static boolean changePassword(String loginID, String oldPassword, String newPassword) throws Exception  {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(DATACLEAR_API_URL);
        
        String strData = CHANGE_PASSWORD_COMMAND + "|" +  loginID + "|" + oldPassword + "|" + newPassword;
        
        String encryptedCommand = ChilkatLibrary.encrypt(strData);
        System.out.println("ENCRYPTED: ");
        System.out.println(encryptedCommand);
        String decryptedCommand = ChilkatLibrary.decrypt(encryptedCommand);
        System.out.println("DECRYPTED: ");
        System.out.println(decryptedCommand);
        
        // add header
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("strData", encryptedCommand));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        
        System.out.println(result);
        System.out.println(response.toString());
        return false;
    }
    
    // FOR TESTING ONLY
    public static void main (String[] args) {
        try {
            // String loginID = "daustin";
            String loginID = "whereisbill";
            isSecurePassword(loginID);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            String loginID = "whereisbill";
            String newPassword = "da_AZou81234";
            String oldPassword = "D@t@Cl3@r!";
            
            changePassword(loginID, oldPassword, newPassword);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
