import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by drew on 2/23/14.
 */
public class Header {
    private ArrayList<String> mHeaders = new ArrayList<String>();
    private String mProtocol;
    private String mFile;
    private String mHost;
    private String mURL;
    private String mRequestType;

    public Header(ArrayList<String> headers) {
        mHeaders = headers;
        parseRequest();
    }

    public ArrayList<String> getHeaders() {
        return mHeaders;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public String getFile() {
        return mFile;
    }

    public String getHost() {
        return mHost;
    }

    public String getURL() {
        return mURL;
    }

    public String getRequestType() {
        return mRequestType;
    }

    //for future debugging
    public String toString() {
        System.out.println(mProtocol);
        System.out.println(mFile);
        System.out.println(mHost);
        System.out.println(mURL);
        return null;
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

    private String parseRequestType(String request) {

        if (request.equals("GET")) {
            mRequestType = "GET";
        } else if (request.equals("HEAD")) {
            mRequestType = "HEAD";
        } else {
			mRequestType = "OTHER";
		}
        return mRequestType;

    }
/*
	//write the response header and 
	private static String writeResponse(String requestType, String fileName, int statusCode, OutputStream os) {
		//Status Line
		os.write(writeStatus(statusCode).getBytes());
		//System.out.println(writeStatus(statusCode))
		
		if(requestType.equals("GET") || requestType.equals("HEAD")) {
			//send out Content-Type
			if(statusCode==200) {
				// Content Type
				os.write(writeContentType(fileName).getBytes());
				// Content Length
				os.write(writeContentLength(fileName).getBytes());
				// Connection Type
				os.write("Connection: close\r\n".getBytes());
				//Extra line break
				os.write("\r\n".getBytes());
				//System.out.println(writeContentType(fileName))
					if(requestType.equals("GET")) {
						sendBytes(fileName, os);
					}
			} else if(statusCode==301) {
				//redirect Location:
				os.write(writeLocation(fileName).getBytes());
			} else {
				//do nothing b/c requested resource does not exist
				os.write("\r\n".getBytes());
			}
		}
		
		return;	
	}
	
    // returns true if directory has file
    private boolean hasFile(String strFile) {
        return false;
    }

    // reads redirect into hashmap for fast lookup
    // read file and parse lines into hashtable
    // format: /file http://www.url.to.redirect.com
	
	private static String writeContentLength(String fileName) {
		File f = new File(fileName);
		return "Content-Length: "+f.length()+"\r\n";
	}
	
	private static String writeContentType(String fileName) {
		return "Content-Type: "+getContentType(fileName)+"\r\n";
	}

    private String getContentType(String input) {
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
	
	private String writeLocation(String url) {
		return "Location: "+url+"\r\n";
	}
	
	// writes the status code line based on a given status code
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
	
	// send bytes of a file from file input stream to output stream
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {

		byte[] buffer = new byte[1024];
		int bytes = 0;

		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
		return;
	}
*/
}


