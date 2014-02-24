Author: Drew Boshardy (dboshardy), Trevor Yuen (tyuen11)
Date: Feb 23, 2014

Assumes that the file is located in /home/$USERNAME/54001/project1/www/.
This is hard coded into HTTPServer.java. Please change it if necessary.

HTTPServer contains the driver. It handles the client input stream and the server output stream.
HTTPServer also builds the hashmap for both the redirects and the file paths.
The input stream is passed to the Header class which parses the input and writes the output content.

The Header class will write the header based on whether the file can be found or if the redirect exists.
It will appropriately create the header as a string and pass it back to HTTPServer.
It will also locate the file to be sent.
