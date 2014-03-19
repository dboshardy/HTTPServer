import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
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
 *
 */

public class HTTPServer {
    public ArrayList<String> mInputs = new ArrayList<String>();
    public HashMap<String, String> mRedirectMap = new HashMap<String, String>();
    private String mResponse;
    private ArrayList<File> mDirectory = new ArrayList<File>();
    private HashMap<String, File> mDirectoryMap = new HashMap<String, File>();
    private File mFileToSend;

    public void start(int portNum) throws IOException {
        URL url = getClass().getResource("www");
        File dir = new File(url.getPath());
        constructDirectory(dir, dir.getName());
        readRedirect();
        initializeServer(portNum);

    }


    private void initializeServer(int portNum) throws IOException {
        ServerSocket myHTTPServerSocket = null;

        //create new server socket
        try {
            myHTTPServerSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            System.out.println("Could not create socket. Please specify a port number using the format \'--port=###\'");
            e.printStackTrace();
        }
        while (true) {

            //create new client socket
            Socket myClientSocket = null;
            try {
                myClientSocket = myHTTPServerSocket.accept();
            } catch (IOException e) {
                System.out.println("Could not connect to server socket.");
                e.printStackTrace();
            }

            //create new output writer
            OutputStream output = null;
            try {
                output = myClientSocket.getOutputStream();
            } catch (IOException e) {
                System.out.println("Could not connect to client socket.");
                e.printStackTrace();
            }

            //create new input reader
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
            } catch (IOException e) {
                System.out.println("Could not create inputstream.");
                e.printStackTrace();
            }

            //handle input from client socket
            String inputFromClient = "";
            try {
                do {
                    inputFromClient = input.readLine();
                    System.out.println("INPUT: " + inputFromClient);
                    mInputs.add(inputFromClient);
                    mInputs.removeAll(Collections.singleton(null));
                    mInputs.removeAll(Collections.singleton(""));
                    if(inputFromClient.equals("")){ break;}
                } while (!(inputFromClient ==null));


            } catch (IOException e) {
                System.out.println("Error."); //CHANGE ME
                e.printStackTrace();
            }

            System.out.println("We are out of the loop");
            Header header = new Header(mInputs, mDirectory, mDirectoryMap, mRedirectMap);
            try {
                String strHeader = header.writeResponse();
                System.out.println(strHeader);
                byte[] bHeaderByte = strHeader.getBytes();
                output.write(bHeaderByte);
                mFileToSend = header.getFileToSend();
                if (!(mFileToSend == null)) {
                    byte[] bFileToSend = new byte[(int) mFileToSend.length()];
                    FileInputStream inputStream = new FileInputStream(mFileToSend);
                    inputStream.read(bFileToSend);
                    output.write(bFileToSend, 0, bFileToSend.length);
                }
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (header.getConnectionType().equals("Keep-Alive")) {
                } else {
                    myClientSocket.close();
                    System.out.println("Connection Closed!");
                    inputFromClient = "";
                    mInputs.clear();
                }
            } catch (IOException e) {
                System.out.println("Error.");
                e.printStackTrace();
            }
        }
    }

    private void constructDirectory(File dir, String fileName) {

        // construct directory in field variable
        ArrayList<File> tempList = new ArrayList<File>();
        Collections.addAll(tempList, dir.listFiles());
        for (File file : tempList) {
            if (file.isDirectory()) {
                constructDirectory(file, fileName + "/");
            } else {
                String outputFileName = null;
                try {
                    outputFileName = new String(file.getCanonicalPath());
                    outputFileName = outputFileName.substring(outputFileName.indexOf("www/")).replace("www/", "/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(outputFileName);
                mDirectoryMap.put(outputFileName, file);
            }
        }
        mDirectory.addAll(tempList);
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Invalid options.  Please specify the port number you wish to connect with using \'--port=####\'");
        }


        //get port number
        int portNumber = getPortNumber(args[0]);
        try {
            HTTPServer server = new HTTPServer();
            server.start(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    // reads redirect into hashmap for fast lookup
    // read file and parse lines into hashtable
    // format: /file http://www.url.to.redirect.com

    private void readRedirect() {
        File file = mDirectoryMap.get("/redirect.defs");
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

}
