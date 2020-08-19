/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.*;
import java.time.LocalTime;

/**
 *
 * @author kevin
 */
public class Logger {
    
    private static final String loginLog = "src/log/login.txt";
    
    public static void recordLogin(User user) throws IOException {
        
        //Create filewriter object
        FileWriter fWriter = new FileWriter(loginLog, true);
        
        //Ceate PrintWriter object
        PrintWriter outFile = new PrintWriter(fWriter);
        
        //Record login
        outFile.println("Timestamp: " + LocalTime.now().toString() );
        outFile.println("User: " + user.getUserName() );
        
        //Close PrintWriter
        outFile.close();
        
    }
    
}
