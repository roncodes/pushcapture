import java.io.*;
import java.net.*;

public class Server {
	
	public static void main(String[] args) throws IOException {
		
		while(true) {
	        try {
		        //Listen on port
		        ServerSocket serverSock = new ServerSocket(9001);
		        System.out.println("Listening...");
	
		        //Get connection
		        Socket clientSock = serverSock.accept();
		        System.out.println("Connected client");
		        
		        // Create output file
		        File file = new File("C:/Users/Ron/Desktop/output.mp4");
	
		        // Get input
		        BufferedReader br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
		        
		        // Write input to mp4 file
		        PrintStream out = new PrintStream(new FileOutputStream("C:/Users/Ron/Desktop/output.mp4"));
		        System.setOut(out);
		        
		        // Start pushing output with ffmpeg
		        Runtime.getRuntime().exec("C:/Users/Ron/Desktop/ffmpeg/bin/ffmpeg.exe -i output.mp4 -re -acodec copy -vcodec copy -f flv rtmp://74.63.224.195/live/myStream");
		        
		        while ((br.readLine()) != null || br.readLine() != "") {
		    	    System.out.println(br.readLine());
		    	}
	
		        br.close();
		        serverSock.close();
		        clientSock.close();
		        file.delete();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
	    }
		
    }
	
}
