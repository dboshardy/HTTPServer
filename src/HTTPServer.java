import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by drew on 1/24/14.
 */


public class HTTPServer {
    public ArrayList<String> mHeader = new ArrayList<String>();
    public HashMap<String,String> mRedirectMap = new HashMap<String,String>();

    public HTTPServer(int portNum) {
        ServerSocket myHTTPServerSocket = null;

        //create new server socket
        try {
            myHTTPServerSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            System.out.println("Could not create socket. Please specify a port number using the format \'--port=###\'");
            e.printStackTrace();
        }

        //create new client socket
        Socket myClientSocket = null;
        try {
            myClientSocket = myHTTPServerSocket.accept();
        } catch (IOException e) {
            System.out.println("Could not connect to socket.");
            e.printStackTrace();
        }

        //create new output writer
        PrintWriter output = null;
        try {
            output = new PrintWriter(myClientSocket.getOutputStream(),true);
        } catch (IOException e) {
            System.out.println("Could not connect to socket.");
            e.printStackTrace();
        }

        //create new input reader
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Could not connect to socket.");
            e.printStackTrace();
        }

        //handle input from client socket
        String inputFromClient = null;
        try {
            while((inputFromClient = input.readLine()) != null){
                mHeader.add(inputFromClient);
                parseInput(inputFromClient);
            }
        } catch (IOException e) {
            System.out.println("Error."); //CHANGE ME
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Invalid options.  Please specify the port number you wish to connect with using \'--port=####\'");
        }


        //get port number
        int portNumber = getPortNumber(args[1]);
        HTTPServer server = new HTTPServer(portNumber);
    }

    //this parses the port number from the options entered
    public static int getPortNumber(String port){
        int myPort = 0;
        if(port.contains("--port=")){
            myPort= Integer.parseInt(port.replace("--port=",""));
        }
        System.out.println(myPort);
        return myPort;
    }

    // Parses input and returns some string...
    private String parseInput(String input){
        //do parsing
        return null;
    }

    // returns either GET or HEAD or POST
    private String getRequest(){
        return null;

    }

    // returns true if directory has file
    private boolean hasFile(){
        return false;
    }

    // reads redirect into hashmap for fast lookup
    // read file and parse lines into hashtable
    // format: /file http://www.url.to.redirect.com
    private void readRedirect(String input){
        File file = new File("www/redirect.defs");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] words = line.split(" ");
            mRedirectMap.put(words[0],words[1]); // put /file as key and http://.... as value
        }
    }
    private String getContentType(String input){
        return null;
    }
    private File streamFile(){
        return null;
    }



}
