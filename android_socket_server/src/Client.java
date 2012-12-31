import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
    final int PORT_NUMBER = 9000;
    final String HOSTNAME = "127.0.0.1";

    //Attempt to connect
    try {
        Socket sock = new Socket(HOSTNAME, PORT_NUMBER);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        //Output
        out.println("This is a test");
        out.flush();

        out.close();
        sock.close();
    } catch(Exception e) {
        e.printStackTrace();
    }
    }
}