package com.neko;
import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.io.*;
import java.util.*;
/**
 * Hello world!
 *
 */
public class App {
    static Connection conn;
    static Statement stmt;
    static Scanner keyboard;
    static String userSQLInput;
    public static void main( String[] args ) throws IOException{

        keyboard = new Scanner(System.in);
        String username = "uname123", password = "passwd123";
        int userInput;

        try{
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            System.out.println("Registered driver");
            conn = DriverManager.getConnection("DB SERVER ADDRESS",username, password);
            System.out.println("logged in as: "+username);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
        }catch(Exception e){
            System.out.println("exception when registering and signing in");
            return;
        }
        menuloop: while(true){

            System.out.println("Please select what you want to do\n" +
                    "1: insert data\n" +
                    "2: update data\n" +
                    "3: query data\n" +
                    "4: import from file\n" +
                    "99: exit / go back");
            keyboard.reset();
            userInput = keyboard.nextInt();
            keyboard.nextLine();
            keyboard.useDelimiter(";");
            System.out.print("\033[H\033[2J");
            switch (userInput) {
                case 1:
                    sqlInsert(null);
                    break;
                case 2:
                    sqlUpdate(null);
                    break;
                case 3:
                    sqlQuery(null);
                    break;
                case 4:
                    sqlFile();
                    break;
                case 99:
                    System.out.println("now Exiting");
                    try{conn.close();}catch(Exception e){System.out.println("error closing connection");}
                    break menuloop;
                default:
                    System.out.println("not a valid selection selected: " + userInput);
                    break;
            }
        }

    }
    public static void sqlInsert(String input){
        try{
            if(input == null) {
                System.out.println("Please enter SQL To insert");
                userSQLInput = keyboard.next();
                keyboard.nextLine();
            }else{
                userSQLInput = input;
            }
            stmt.executeUpdate(userSQLInput);
            System.out.println("inserted successfully");
        }catch(Exception e){
            System.out.println("SQL insert Exception: "+e);
        }
    }
    public static void sqlUpdate(String input){
        try{
            if(input == null) {
                System.out.println("Please enter SQL To Update");
                userSQLInput = keyboard.next();
                keyboard.nextLine();
            }else{
                userSQLInput = input;
            }
            stmt.executeUpdate(userSQLInput);
        }catch(Exception e){
            System.out.println("SQL update Exception: "+e);
        }
    }
    public static void sqlQuery(String input){
        try{
            if(input == null) {
                System.out.println("Please enter SQL To Query");
                userSQLInput = keyboard.next();
                keyboard.nextLine();
            }else{
                userSQLInput = input;
            }
            ResultSet rset = stmt.executeQuery(userSQLInput);
            ResultSetMetaData rsmd = rset.getMetaData();
            for(int i = 1; i<rsmd.getColumnCount(); i++){
                System.out.printf("| %-25s ",rsmd.getColumnName(i));
            }
            System.out.print("\n");
            while(rset.next()){
                //System.out.print("| ");
                for(int i = 1; i<rsmd.getColumnCount(); i++){
                    System.out.printf("| %-25s ",rset.getString(i));
                }
                System.out.println("|");

            }
            System.out.print("\n");
            System.out.println("SQL Query Successful");
        }catch(Exception e){
            System.out.println("SQL query Exception: "+e);
        }
    }
    public static void sqlFile(){
        String nextStore;
        //ask what filename is
        //open file
        //if hasnext
        //set default delimiter
        //nextStore = next
        //set ; delimiter
        //if nextStore = insert... submit nextStore + next into executeupdate
        //if nextStore = update... submit nextStore + next into executeUpdate
        //if nextStore = query... submit nextStore + next into executeQuery
        System.out.println("please insert the /path/to/the/filename: ");
        //the mock data file is at /home/nekomancer/Downloads/MOCK_DATA.sql
        File sqlFile = new File(keyboard.nextLine());
        System.out.print("\033[H\033[2J");
        try {
            Scanner fileSc = new Scanner(sqlFile);
            while (fileSc.hasNext()) {
                nextStore = fileSc.next();
                fileSc.useDelimiter(";");
                switch (nextStore) {
                    case "create":
                    case "insert":
                    case "update":
                    case "delete":
                    case "drop":
                        //System.out.println("drop: \n" + nextStore + fileSc.next());
                        sqlUpdate((nextStore+fileSc.next()) );
                        break;
                    case "select":
                        //System.out.println("select: \n" + nextStore + fileSc.next());
                        sqlQuery((nextStore+fileSc.next()) );
                        break;
                    case "default":
                        System.out.println(nextStore + " not handled yet");
                        break;
                }
                fileSc.reset();
            }
        }catch(Exception e){
            if(e instanceof java.io.FileNotFoundException){
                System.err.println("that file does not exist");
            }else{
                System.out.println("Exception: "+e);
            }
        }
    }
}
