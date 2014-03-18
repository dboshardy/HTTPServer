import java.awt.image.DirectColorModel;
import java.io.*;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by drew on 2/23/14.
 */
public class SSLHeader {
    private ArrayList<String> mHeaders = new ArrayList<String>();
    private String mProtocol;
    private String mFile;
    private String mHost;
    private String mURL;
    private String mRequestType;
    private final HashMap<String, String> mRedirectMap;
    private HashMap<String, File> mDirMap = new HashMap<String, File>();
    private ArrayList<File> mDirectory = new ArrayList<File>();
    private File mFileToSend;

    public SSLHeader(ArrayList<String> headers, ArrayList<File> directory, HashMap<String, File> dirmap, HashMap<String, String> redirmap) {
        mHeaders = headers;
        mDirectory = directory;
        mDirMap = dirmap;
        mRedirectMap = redirmap;
        System.out.println(mDirMap);
        parseRequest();
    }

    public File getFileToSend() {
        return mFileToSend;
    }

    public HashMap<String, String> getRedirectMap() {
        return mRedirectMap;
    }

    public HashMap<String, File> getDirMap() {
        return mDirMap;
    }

    public ArrayList<File> getDirectory() {
        return mDirectory;
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

    private boolean hasFile() {
        System.out.println("File name: " + mFile);
        boolean output = false;
        if (mDirMap.containsKey(mFile)) {
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
        for (String line : mHeaders) {
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
        if (mRequestType.equals("OTHER")) {
            return 403;
        } else if (hasFile()) {
            //if file exists, return 200
            return 200;
        } else {
            //if file does not exist, check redirect
            if (mRedirectMap.containsKey(mFile)) {
                return 301;
            } else {
                return 404;
            }
        }
    }

    //write the response header and
    public String writeResponse() {
        StringBuilder msg = new StringBuilder();

        //Status Line
        msg.append(writeStatus());

        if (mRequestType.equals("GET") || mRequestType.equals("HEAD")) {
            //send out Content-Type
            if (whichStatusCode() == 200) {
                // Content Type
                msg.append(writeContentType());
                // Content Length
                msg.append(writeContentLength());
                // Connection Type
                msg.append("Connection: close\r\n");
                //Extra line break
                msg.append("\r\n");
                if(mRequestType.equals("HEAD")){
                    mFileToSend = null;
                }
                else{
                    mFileToSend = mDirMap.get(mFile);
                }
            } else if (whichStatusCode() == 301) {
                //redirect Location:
                msg.append(writeLocation(mRedirectMap.get(mFile)));
                msg.append("\r\n");
                mFileToSend = null;
            } else {
                //do nothing b/c requested resource does not exist
                msg.append("\r\n");
                mFileToSend = null;
            }
        } else {
            msg.append("\r\n");
            mFileToSend = null;
        }
        return msg.toString();
    }


    private String writeContentLength() {
            File f = mDirMap.get(mFile);
            return "Content-Length: " + f.length() + "\r\n";
    }

    private String writeContentType() {
        return "Content-Type: " + getContentType();
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
        return "Location: " + url + "\r\n";
    }

    // writes the status code line based on a given status code
    private String writeStatus() {
        if (whichStatusCode() == 200) {
            return "HTTP/1.1 200 OK\r\n";
        } else if (whichStatusCode() == 301) {
            return "HTTP/1.1 301 Moved Permanently\r\n";
        } else if (whichStatusCode() == 400) {
            return "HTTP/1.1 400 Bad Request\r\n";
        } else if (whichStatusCode() == 404) {
            return "HTTP/1.1 404 Not Found\r\n";
        } else if (whichStatusCode() == 403) {
            return "HTTP/1.1 403 Forbidden\r\n";
        } else if (whichStatusCode() == 501) {
            return "HTTP/1.1 505 HTTPS Version Not Supported\r\n";
        } else {
            return "HTTP/1.1 500 Error\r\n";
        }
    }

}


