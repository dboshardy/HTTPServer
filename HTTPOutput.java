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

public class HTTPOutput {
	public static void main(String[] args) {
	
	}
	
	//Check if file exists, and return appropriate status code
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