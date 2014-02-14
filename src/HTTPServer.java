import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by drew on 1/24/14.
 */
public class HTTPServer {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Invalid options.  Please specify the port number you wish to connect with using \'--port=####\'");
        }


        //get port number
        int portNumber = getPortNumber(args[1]);
        ServerSocket myHTTPServerSocket = null;

        //create new server socket
        try {
            myHTTPServerSocket = new ServerSocket(portNumber);
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
                output.println(inputFromClient);
                System.out.println("Received: \""+inputFromClient+"\" from "+myClientSocket.getInetAddress());
                System.out.println("Echoing: \""+inputFromClient+"\"");
            }
        } catch (IOException e) {
            System.out.println("Could not get input from client.");
            e.printStackTrace();
        }
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
}
