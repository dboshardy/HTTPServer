import java.util.ArrayList;

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

    public Header(ArrayList<String> Headers) {
        Headers = mHeaders;
        parseRequest();
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
        }
        return mRequestType;

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

    private String getContentType(String input) {
        return null;
    }
}


