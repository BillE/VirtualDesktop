package com.dataclear;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MacAddressTest {

    /**
     * For testing only
     * 
     * @param args
     */
    public static void main(String[] args){
        try {
            System.out.println("Mac Address: ");
            System.out.println( getMacAddress() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    /**
     * return mac address of local device
     * 
     * @return
     * @throws Exception
     */
    public static String getMacAddress() throws Exception {
       InetAddress ip;
       String macAddress = "";
    
       try {
           ip = InetAddress.getLocalHost();
           System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
    
            byte[] mac = network.getHardwareAddress();
    
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            macAddress = sb.toString();

       } catch (UnknownHostException e) {
           throw new Exception(e);
       } catch (SocketException e){
           throw new Exception(e);
       }

       return macAddress;
   }

}
