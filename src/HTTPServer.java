import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


/**
 * Created by drew on 1/24/14.
 * <p/>
 * pseudocode:
 * <p/>
 * receive input
 * put input into arraylist to parse
 * parse type of requesting
 * if get
 *      check for file
 * if found, return file found and file
 * if not found, return 404
 * if redirect return redirect
 * if head return headers only
 */

public class HTTPServer {
    public ArrayList<String> mInputs = new ArrayList<String>();
    public HashMap<String, String> mRedirectMap = new HashMap<String, String>();
    private final String FILE_NOT_FOUND = "404";
    private final String FILE_FOUND = "200";
    private final String REDIRECT = "300";
    private String mResponse;
    private ArrayList<File> mDirectory = new ArrayList<File>();
    private HashMap<String, File> mDirectoryMap = new HashMap<String, File>();

    public HTTPServer(int portNum) {

        // read file path
        File dir = new File("/home/$USERNAME/54001/projec1/www"); //File("/home/$USER/54001/project1/www");
        // construct directory in field variable
        Collections.addAll(mDirectory,dir.listFiles());
        for(File file : mDirectory){
            mDirectoryMap.put(file.getName(),file);
        }

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
            output = new PrintWriter(myClientSocket.getOutputStream(), true);
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
            while ((inputFromClient = input.readLine()) != null) {
                mInputs.add(inputFromClient);
                Header header = new Header(mInputs);
				header.toString();
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
        int portNumber = getPortNumber(args[0]);
        HTTPServer server = new HTTPServer(portNumber);


    }


    //this parses the port number from the options entered
    public static int getPortNumber(String port) {
        int myPort = 0;
        if (port.contains("--port=")) {
            myPort = Integer.parseInt(port.replace("--port=", ""));
        }
        System.out.println(myPort);
        return myPort;
    }

    private void readRedirect(String input) {
        File file = new File("www/redirect.defs");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] words = line.split(" ");
            mRedirectMap.put(words[0], words[1]); // put /file as key and http://.... as value
        }
    }
    private boolean hasFile(String file){
        boolean output = false;
        if(mDirectoryMap.containsKey(file)){
            output = true;
        }
        return output;
    }

}
