import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;


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

public class SecureHTTPServer {
    public ArrayList<String> mInputs = new ArrayList<String>();
    public HashMap<String, String> mRedirectMap = new HashMap<String, String>();
    private String mResponse;
    private ArrayList<File> mDirectory = new ArrayList<File>();
    private HashMap<String, File> mDirectoryMap = new HashMap<String, File>();
    private File mFileToSend;
    private int mPortNum;
    private static int portNumber;

    public void start(int portNum){
        URL url = getClass().getResource("www");
        File dir = new File(url.getPath());
        constructDirectory(dir, dir.getName());
        readRedirect();
        try {
            initializeServer(portNum);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static class HTTPServerRunnable implements Runnable {
        private static int portNum;
        public HTTPServerRunnable(int portNum){
            this.portNum = portNum;
        }
        public void run(){
            HTTPServer httpserver = new HTTPServer();
            try {
                httpserver.start(portNum);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static class SecureHTTPServerRunnable implements Runnable {
        private static int portNum;
        public SecureHTTPServerRunnable(int portNum){
            this.portNum = portNum;
        }
        public void run(){
            SecureHTTPServer securehttpserver = new SecureHTTPServer();
            securehttpserver.start(portNum);

        }
    }

    private void initializeServer(int portNum) throws IOException, CertificateException, NoSuchAlgorithmException {
        SSLServerSocket myHTTPServerSocket = null;
        try {
            KeyStore mKeyStore = null;
            try {
                String pw = "password";
                String keyStorePw = "password";
                char[] keyManPassword = keyStorePw.toCharArray();
                char[] password = pw.toCharArray();
                mKeyStore = KeyStore.getInstance("JKS");
                URL keyURL = getClass().getResource("server.jks");
                mKeyStore.load(new FileInputStream(keyURL.getPath()), password);

                KeyManagerFactory managerFactory =
                        KeyManagerFactory.getInstance("SunX509");
                managerFactory.init(mKeyStore, keyManPassword);
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(managerFactory.getKeyManagers(), null, null);

                //create new server socket
                SSLServerSocketFactory serverFactory = sc.getServerSocketFactory();

                myHTTPServerSocket = (SSLServerSocket) serverFactory.createServerSocket(portNum);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Could not create socket. Please specify a port number using the format \'--port=###\'");
            e.printStackTrace();
        }
        while (true) {

            //create new client socket
            SSLSocket myClientSocket = null;
            try {
                myClientSocket = (SSLSocket) myHTTPServerSocket.accept();
                System.out.println(myClientSocket.toString());
            } catch (IOException e) {
                System.out.println("Could not connect client to server socket.");
                e.printStackTrace();
            }

            //create new output writer
            OutputStream output = null;
            try {
                output = myClientSocket.getOutputStream();
            } catch (IOException e) {
                System.out.println("Could not get output stream.");
                e.printStackTrace();
            }

            //create new input reader
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
            } catch (IOException e) {
                System.out.println("Could not get input stream.");
                e.printStackTrace();
            }

            //handle input from client socket
            String inputFromClient = "";
            try {

                do {
                    inputFromClient = input.readLine();
                    System.out.println("INPUT: "+inputFromClient);
                    mInputs.add(inputFromClient);
                    mInputs.removeAll(Collections.singleton(null));
                    mInputs.removeAll(Collections.singleton(""));
                    if(inputFromClient.equals("")){ break;}
                } while (inputFromClient != null);


            } catch (IOException e) {
                System.out.println("Error."); //CHANGE ME
                e.printStackTrace();
            }

            System.out.println("We are out of the loop");
            SSLHeader header = new SSLHeader(mInputs, mDirectory, mDirectoryMap, mRedirectMap);
            try {
                String strHeader = header.writeResponse();
                byte[] bHeaderByte = strHeader.getBytes();
                output.write(bHeaderByte);
                mFileToSend = header.getFileToSend();
                if (!(mFileToSend == null)) {
                    byte[] bFileToSend = new byte[(int) mFileToSend.length()];
                    FileInputStream inputStream = new FileInputStream(mFileToSend);
                    inputStream.read(bFileToSend);
                    output.write(bFileToSend, 0, bFileToSend.length);
                }
                System.out.println(header.toString());
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(header.getConnectionType().equals("Keep-Alive")){
            } else {
                myClientSocket.close();
                System.out.println("Connection Closed!");
                mInputs.clear();
                inputFromClient = "";
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

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Invalid options.  Please specify the port number you wish to connect with using \'--port=####\'");
        }

        //get port number
        int portNumber = getPortNumber(args[0]);
        /*
        SecureHTTPServer secureHTTPServer = new SecureHTTPServer();
        HTTPServer httpServer = new HTTPServer();
        secureHTTPServer.start(portNumber);
        httpServer.start(portNumber);
        */

        Thread rThread = new Thread(new HTTPServerRunnable(portNumber));
        Thread sThread = new Thread(new SecureHTTPServerRunnable(portNumber+1));
        rThread.start();
        sThread.start();
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
