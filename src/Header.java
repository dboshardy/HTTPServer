import java.io.*;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
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
    private final HashMap<String,String> mRedirectMap;
    private HashMap<String,File> mDirMap = new HashMap<String, File>();
    private ArrayList<File> mDirectory = new ArrayList<File>();

    public Header(ArrayList<String> headers,ArrayList<File> directory,HashMap<String,File> dirmap,HashMap<String,String> redirmap) {
        mHeaders = headers;
        mDirectory = directory;
        mDirMap = dirmap;
        mRedirectMap = redirmap;
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
    private boolean hasFile(){
        boolean output = false;
        if(mDirMap.containsKey(mFile)){
            output = true;
        }
        return output;
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
		for(String line : mHeaders) {
			System.out.println(line);	
		}
        String request = input[0]; // first word of first line of header is either GET or HEAD
        mFile = input[1];
        mProtocol = input[2];
		
        mHost = mHeaders.get(2).replace("Host: ", "").trim();
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
	
	private int whichStatusCode() {
		//if file exists, return 200
		if(hasFile()){
			return 200;
		}
		//if file does not exist, check redirect
		else {
			if(mRedirectMap.containsKey(mFile)){
				return 301;
			}
			else {
				return 404;
			}
		}
	}
	
	//write the response header and 
	private String writeResponse() {
		StringBuilder msg = new StringBuilder();
		
		//Status Line
		msg.append(writeStatus());
		//System.out.println(writeStatus(statusCode))
		
		if(mRequestType.equals("GET") || mRequestType.equals("HEAD")) {
			//send out Content-Type
			if(whichStatusCode()==200) {
				// Content Type
				msg.append(writeContentType());
				// Content Length
				msg.append(writeContentLength());
				// Connection Type
				msg.append("Connection: close\r\n");
				//Extra line break
				msg.append("\r\n");
				//System.out.println(writeContentType(fileName))
					/*if(requestType.equals("GET")) {
						sendBytes(fileName, os);
					}*/
			} else if(whichStatusCode()==301) {
				//redirect Location:
				msg.append(writeLocation(mRedirectMap.get(mFile)));
			} else {
				//do nothing b/c requested resource does not exist
				msg.append("\r\n");
			}
		}
		
		return msg.toString();	
	}
	
    // reads redirect into hashmap for fast lookup
    // read file and parse lines into hashtable
    // format: /file http://www.url.to.redirect.com
	
	private String writeContentLength() {
		File f = new File(mFile);
		return "Content-Length: "+f.length()+"\r\n";
	}
	
	private String writeContentType() {
		return "Content-Type: "+getContentType()+"\r\n";
	}

    private String getContentType() {
        if (mFile.endsWith(".htm") || mFile.endsWith(".html")) {
			return "text/html\r\n";
		} else if (mFile.endsWith(".txt")) {
			return "text/plain\r\n";
		} else if (mFile.endsWith(".jpg") || mFile.endsWith(".jpeg")) {
			return "image/jpeg\r\n";
		} else if (mFile.endsWith(".png")) {
			return "image/png\r\n";
		} else if (mFile.endsWith(".pdf")) {
			return "application/pdf\r\n";	
		} else {
			return "application/octet-stream\r\n";
		}
    }
	
	private String writeLocation(String url) {
		return "Location: "+url+"\r\n";
	}
	
	// writes the status code line based on a given status code
	private String writeStatus() {
		if (whichStatusCode() == 200) {
			return "HTTP/1.0 200 OK\r\n";
		} else if (whichStatusCode()==301) {
			return "HTTP/1.0 301 Moved Permanently\r\n";
		} else if (whichStatusCode()==400) {
			return "HTTP/1.0 400 Bad Request\r\n";
		} else if (whichStatusCode()==404) {
			return "HTTP/1.0 404 Not Found\r\n";
		} else if (whichStatusCode()==301) {
			return "HTTP/1.0 505 HTTP Version Not Supported\r\n";
		} else {
			return "HTTP/1.0 500 Error\r\n";
		}
	}
	
	// send bytes of a file from file input stream to output stream
	private void sendBytes(FileInputStream fis, OutputStream os) throws Exception {

		byte[] buffer = new byte[1024];
		int bytes = 0;

		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
		return;
	}

}


