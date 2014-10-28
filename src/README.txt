Author: Drew Boshardy (dboshardy), Trevor Yuen (tyuen11)
Date: March 19, 2014

>This is hard coded into HTTPServer.java. Please change it if necessary.
Hopefully we addressed this by assuming the www directory is in the same directory as the .java files.
Otherwise we're not sure what to do as the previous instruction said to put it in "/home/$USERNAME/54001/project1/www"

	SecureHTTPServer contains the driver. It handles the client input stream and the server output stream.
	HTTPServer also builds the hashmap for both the redirects and the file paths.
	The input stream is passed to the Header class which parses the input and writes the output content.

	The Header class will write the header based on whether the file can be found or if the redirect exists.
	It will appropriately create the header as a string and pass it back to HTTPServer.
	It will also locate the file to be sent.


All of the above still applies.

NOTE:

Running instructions have changed.  First make using the makefile, then run:

    java SecureHTTPServer --port=blah

It will then spawn two threads, one using HTTP with the port number given, the other using HTTPS with the port number
given plus 1.  For example, given the argument "--port=1234", the HTTP server will start on port 1234, while the secure
server will start on port 1235.

Currently, everything passes all tests except for the persistence test in which it fails only for the null response.  The server sends the header and keeps the connection open, but for some reason it never sends the file.  See the discussion on Piazza.  It is truly a mystery.  If you figure out why it is doing this in the process of grading it, we would definitely love to know.
