import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by drew on 1/24/14.
 * <p/>
 * pseudocode:
 * <p/>
 * receive input
 * put input into arraylist to parse
 * parse type of request
 * if get
 * check for file
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
                parseInput(inputFromClient);
                Header headers = new Header(mInputs);
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
    public static int getPortNumber(String port) {
        int myPort = 0;
        if (port.contains("--port=")) {
            myPort = Integer.parseInt(port.replace("--port=", ""));
        }
        System.out.println(myPort);
        return myPort;
    }

    // Parses input and returns some string...
    private String parseInput(String input) {

        return null;
    }


    // Header class to handle header manipulation and writing
    private class Header {
        private ArrayList<String> mHeaders = new ArrayList<String>();
        private String mProtocol;
        private String mFile;
        private String mHost;
        private String mURL;
        private String mRequestType;

        private Header(ArrayList<String> Headers) {
            Headers = mHeaders;
            parseRequest();
        }

        private void parseRequest() {
            String[] input = mHeaders.get(0).split(" ");
            String request = input[0]; // first word of first line of header is either GET or HEAD
            mFile = input[1];
            mProtocol = input[2];
            mHost = mHeaders.get(1).replace("Host: ", "").trim();
            mURL = mHost + mFile;
            parseRequestType(request);

        }

        private void parseRequestType(String request) {

            if (request.equals("GET")) {
                mRequestType = "GET";
            } else if (request.equals("HEAD")) {
                mRequestType = "HEAD";
            } else {
				mRequestType = "OTHER" //If request type is not recognized
			}

        }

        private String writeResponse() {
            return null;
        }

        // returns true if directory has file
        private boolean hasFile(String strFile) {
            return false;
        }

        // reads redirect into hashmap for fast lookup
        // read file and parse lines into hashtable
        // format: /file http://www.url.to.redirect.com
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

        private String getContentType(String input) {
            return null;
        }

		/*******************************************
		Code additions from trevor
		*********************************************/
		private static int whichStatusCode(boolean fileExists, String fileName) {
			//if file exists, return 200
			if(fileExists){
				return 200;
			}
			//if file does not exist, check redirect
			else {
				String redirect = redirectExists(fileName);
				if(redirect.equals("")) {
					return 404;
				}
				else {
					return 301;
				}
			}
		}
		
		
		/*
		Alternatively, load all the redirects into String array of two columns
		*/
		private static String redirectExists(String fileName) {
			Scanner S = null;
			try {
				S = new Scanner(new File("/Users/yuentrevor/Documents/CSPP/Networks/www/redirect.defs"));
			} catch(Exception e){
				throw new RuntimeException(e);
			}

			while(S.hasNext()){
				String line = S.nextLine();
				String[] str = line.split(" ");
				
				if(fileName.equals(str[0])){
					return str[1];
				}
			}
			
			return "";
		}
		
		private static String writeResponse(String requestType, String fileName, int statusCode, OutputStream os) {
			
			
			//send out status line
			os.write(writeStatus(statusCode).getBytes())
			//System.out.println(writeStatus(statusCode))
			
			if(requestType.equals("GET") || requestType.equals("HEAD")) {
				//send out Content-Type
				if(statusCode==200) {
					os.write(writeContentType(fileName).getBytes())
					os.write("\r\n".getBytes())
					//System.out.println(writeContentType(fileName))
						if(requestType.equals("GET")) {
							sendBytes(fileName, os)
						}
				} else if(statusCode==301) {
					//redirect
				} else {
					//do nothing b/c requested resource does not exist
					os.write("\r\n".getBytes())
				}
			}
			
			return;	
		}
		
		
		//send bytes of a file given a file input stream
		private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {

			byte[] buffer = new byte[1024];
			int bytes = 0;

			while ((bytes = fis.read(buffer)) != -1) {
				os.write(buffer, 0, bytes);
			}
			return;
		}
		
		//write the status line based on the status code
		private static String writeStatus(int statusCode) {
			if (statusCode == 200) {
				return "HTTP/1.0 200 OK\r\n";
			} else if (statusCode==301) {
				return "HTTP/1.0 301 Moved Permanently\r\n";
			} else if (statusCode==400) {
				return "HTTP/1.0 400 Bad Request\r\n";
			} else if (statusCode==404) {
				return "HTTP/1.0 404 Not Found\r\n";
			} else if (statusCode==301) {
				return "HTTP/1.0 505 HTTP Version Not Supported\r\n";
			} else {
				return "HTTP/1.0 500 Error\r\n";
			}
		}
		
		private static String writeContentType(String fileName) {
			return "Content-Type: "+contentType(fileName)
		}
		
		private static String contentType(String fileName) {
			if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
				return "text/html\r\n";
			} else if (fileName.endsWith(".txt")) {
				return "text/plain\r\n";
			} else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
				return "image/jpeg\r\n";
			} else if (fileName.endsWith(".png")) {
				return "image/png\r\n";
			} else if (fileName.endsWith(".pdf")) {
				return "application/pdf\r\n";	
			} else {
				return "application/octet-stream\r\n";
			}
		}
    }


}
