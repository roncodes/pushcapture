package com.ron.camanon.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

/**
 * Implementation of a subset of the RTSP protocol (RFC 2326)
 * This allow remote control of an android device cameras & microphone
 * For each connected client, a Session is instantiated
 * The Session will start or stop streams according to what the client wants
 */
@SuppressLint("DefaultLocale")
public class RtspServer {
	
	private final static String TAG = "RtspServer";

	// Message types for UI thread
	public static final int MESSAGE_LOG = 2;
	public static final int MESSAGE_ERROR = 6;

	private final Handler handler;
	private final int port;
	private boolean running = false;
	private RequestListenerThread listenerThread;

	public RtspServer(int port, Handler handler) {
		this.handler = handler;
		this.port = port;
	}
	
	public void start() throws IOException {
		if (running) return;
		running = true;
		listenerThread = new RequestListenerThread(port,handler);
		listenerThread.start();
	}
	
	public void stop() {
		running = false;
		try {
			listenerThread.server.close();
		} catch (Exception e) {
			Log.e(TAG,"Error when close was called on serversocket: "+e.getMessage());
		}
	}
	
	public static class RequestListenerThread extends Thread implements Runnable {
		
		private final ServerSocket server;
		private final Handler handler;
		
		public RequestListenerThread(final int port, final Handler handler) throws IOException {
			this.server = new ServerSocket(port);
			this.handler = handler;
		}
		
		public void run() {
			Log.i(TAG,"Listening on port "+server.getLocalPort());
			while (!Thread.interrupted()) {
				try {
					new WorkerThread(server.accept(), handler).start();
				} catch (SocketException e) {
					break;
				} catch (IOException e) {
					Log.e(TAG,e.getMessage());
					continue;
				}
			}
			Log.i(TAG,"RequestListener stopped !");
		}
		
	}
	
	// One thread per client
	static class WorkerThread extends Thread implements Runnable {
		
		private final Socket client;
		private final OutputStream output;
		private final BufferedReader input;
		private final Handler handler;
		
		// Each client has an associated session
		private Session session;
		
		public WorkerThread(final Socket client, final Handler handler) throws IOException {
			this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.output = client.getOutputStream();
			this.session = new Session(client.getLocalAddress(),client.getInetAddress());
			this.client = client;
			this.handler = handler;
		}
		
		public void run() {
			Request request;
			Response response;
			
			log("Connection from "+client.getInetAddress().getHostAddress());

			while (!Thread.interrupted()) {

				request = null;
				response = null;
				
				// Parse the request
				try {
					request = Request.parseRequest(input);
				} catch (SocketException e) {
					// Client has left
					break;
				} catch (Exception e) {
					// We don't understand the request :/
					response = new Response();
					response.status = Response.STATUS_BAD_REQUEST;
				}

				// Do something accordingly like starting the streams, sending a session descriptor
				if (request != null) {
					try {
						response = processRequest(request);
					}
					catch (Exception e) {
						// This alerts the main thread that something has gone wrong in this thread
						handler.obtainMessage(MESSAGE_ERROR, e).sendToTarget();
						Log.e(TAG,e.getMessage()!=null?e.getMessage():"An error occurred");
						e.printStackTrace();
						response = new Response(request);
					}
				}

				// We always send a response
				// The client will receive an "INTERNAL SERVER ERROR" if an exception has been thrown at some point
				try {
					response.send(output);
				} catch (IOException e) {
					Log.e(TAG,"Response was not sent properly");
					break;
				}
				
			}

			// Streaming stops when client disconnects
			session.stopAll();
			session.flush();

			try {
				client.close();
			} catch (IOException ignore) {}
			
			log("Client disconnected");
			
		}
		
		@SuppressLint("DefaultLocale")
		public Response processRequest(Request request) throws IllegalStateException, IOException {
			Response response = new Response(request);
			
			/* ********************************************************************************** */
			/* ********************************* Method DESCRIBE ******************************** */
			/* ********************************************************************************** */
			if (request.method.toUpperCase().equals("DESCRIBE")) {
				
					// Parse the requested URI and configure the session
					UriParser.parse(request.uri,session);
					String requestContent = session.getSessionDescriptor();
					String requestAttributes = 
							"Content-Base: "+client.getLocalAddress().getHostAddress()+":"+client.getLocalPort()+"/\r\n" +
							"Content-Type: application/sdp\r\n";
					
					response.attributes = requestAttributes;
					response.content = requestContent;
					
					// If no exception has been thrown, we reply with OK
					response.status = Response.STATUS_OK;
				
			}
			
			/* ********************************************************************************** */
			/* ********************************* Method OPTIONS ********************************* */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("OPTIONS")) {
				response.status = Response.STATUS_OK;
				response.attributes = "Public: DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE\r\n";
				response.status = Response.STATUS_OK;
			}

			/* ********************************************************************************** */
			/* ********************************** Method SETUP ********************************** */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("SETUP")) {
				Pattern p; Matcher m;
				int p2, p1, ssrc, trackId, src;
				
				p = Pattern.compile("trackID=(\\w+)",Pattern.CASE_INSENSITIVE);
				m = p.matcher(request.uri);
				
				if (!m.find()) {
					response.status = Response.STATUS_BAD_REQUEST;
					return response;
				} 
				
				trackId = Integer.parseInt(m.group(1));
				
				if (!session.trackExists(trackId)) {
					response.status = Response.STATUS_NOT_FOUND;
					return response;
				}
				
				p = Pattern.compile("client_port=(\\d+)-(\\d+)",Pattern.CASE_INSENSITIVE);
				m = p.matcher(request.headers.get("transport"));
				
				if (!m.find()) {
					int port = session.getTrackDestinationPort(trackId);
					p1 = port;
					p2 = port+1;
				}
				else {
					p1 = Integer.parseInt(m.group(1)); 
					p2 = Integer.parseInt(m.group(2));
				}
				
				ssrc = session.getTrackSSRC(trackId);
				src = session.getTrackLocalPort(trackId);
				session.setTrackDestinationPort(trackId, p1);

				session.start(trackId);
				response.attributes = "Transport: RTP/AVP/UDP;"+session.getRoutingScheme()+";destination="+session.getDestination().getHostAddress()+";client_port="+p1+"-"+p2+";server_port="+src+"-"+(src+1)+";ssrc="+Integer.toHexString(ssrc)+";mode=play\r\n" +
						"Session: "+ "1185d20035702ca" + "\r\n" +
						"Cache-Control: no-cache\r\n";
				response.status = Response.STATUS_OK;

				// If no exception has been thrown, we reply with OK
				response.status = Response.STATUS_OK;
				
			}

			/* ********************************************************************************** */
			/* ********************************** Method PLAY *********************************** */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("PLAY")) {
				String requestAttributes = "RTP-Info: ";
				if (session.trackExists(0)) requestAttributes += "url=rtsp://"+client.getLocalAddress().getHostAddress()+":"+client.getLocalPort()+"/trackID="+0+";seq=0,";
				if (session.trackExists(1)) requestAttributes += "url=rtsp://"+client.getLocalAddress().getHostAddress()+":"+client.getLocalPort()+"/trackID="+1+";seq=0,";
				requestAttributes = requestAttributes.substring(0, requestAttributes.length()-1) + "\r\nSession: 1185d20035702ca\r\n";
				
				response.attributes = requestAttributes;

				// If no exception has been thrown, we reply with OK
				response.status = Response.STATUS_OK;
			
			}

			/* ********************************************************************************** */
			/* ********************************** Method PAUSE ********************************** */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("PAUSE")) {
				response.status = Response.STATUS_OK;
			}

			/* ********************************************************************************** */
			/* ********************************* Method TEARDOWN ******************************** */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("TEARDOWN")) {
				response.status = Response.STATUS_OK;
			}
			
			/* ********************************************************************************** */
			/* ********************************* Unknown method ? ******************************* */
			/* ********************************************************************************** */
			else {
				Log.e(TAG,"Command unknown: "+request);
				response.status = Response.STATUS_BAD_REQUEST;
			}
			
			return response;
			
		}
		
		private void log(String message) {
			handler.obtainMessage(MESSAGE_LOG, message).sendToTarget();
			Log.v(TAG,message);
		}
		
	}
	
	static class Request {
		
		// Parse method & uri
		public static final Pattern regexMethod = Pattern.compile("(\\w+) (\\S+) RTSP",Pattern.CASE_INSENSITIVE);
		// Parse a request header
		public static final Pattern rexegHeader = Pattern.compile("(\\S+):(.+)",Pattern.CASE_INSENSITIVE);
		
		public String method;
		public String uri;
		public HashMap<String,String> headers = new HashMap<String,String>();
		
		/** Parse the method, uri & headers of a RTSP request */
		public static Request parseRequest(BufferedReader input) throws IOException, IllegalStateException, SocketException {
			Request request = new Request();
			String line;
			Matcher matcher;

			// Parsing request method & uri
			if ((line = input.readLine())==null) throw new SocketException("Client disconnected");
			matcher = regexMethod.matcher(line);
			matcher.find();
			request.method = matcher.group(1);
			request.uri = matcher.group(2);

			// Parsing headers of the request
			while ( (line = input.readLine()) != null && line.length()>3 ) {
				matcher = rexegHeader.matcher(line);
				matcher.find();
				request.headers.put(matcher.group(1).toLowerCase(),matcher.group(2));
			}
			if (line==null) throw new SocketException("Client disconnected");
			
			// It's not an error, it's just easier to follow what's happening in logcat with the request in red
			Log.e(TAG,request.method+" "+request.uri);
			
			return request;
		}
	}
	
	static class Response {
		
		// Status code definitions
		public static final String STATUS_OK = "200 OK";
		public static final String STATUS_BAD_REQUEST = "400 Bad Request";
		public static final String STATUS_NOT_FOUND = "404 Not Found";
		public static final String STATUS_INTERNAL_SERVER_ERROR = "500 Internal Server Error";
		
		public String status = STATUS_INTERNAL_SERVER_ERROR;
		public String content = "";
		public String attributes = "";
		private final Request request;
		
		public Response(Request request) {
			this.request = request;
		}
		
		public Response() {
			// Be carefull if you modify the send() method because request might be null !
			request = null;
		}
		
		public void send(OutputStream output) throws IOException {
			int seqid = -1;
			
			try {
				seqid = Integer.parseInt(request.headers.get("cseq").replace(" ",""));
			} catch (Exception e) {
				Log.e(TAG,"Error parsing CSeq: "+(e.getMessage()!=null?e.getMessage():""));
			}
			
			String response = 	"RTSP/1.0 "+status+"\r\n" +
					"Server: MajorKernelPanic RTSP Server\r\n" +
					(seqid>=0?("Cseq: " + seqid + "\r\n"):"") +
					"Content-Length: " + content.length() + "\r\n" +
					attributes +
					"\r\n" + 
					content;
			
			Log.d(TAG,response.replace("\r", ""));
			
			output.write(response.getBytes());
		}
	}
		
	
	
}
