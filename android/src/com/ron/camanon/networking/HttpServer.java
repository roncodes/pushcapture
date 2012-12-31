package com.ron.camanon.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import com.ron.camanon.http.BasicHttpServer;
import com.ron.camanon.http.ModifiedHttpContext;


import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.util.Log;

/**
 * This is an HTTP interface for camanon
 * For example: "http://xxx.xxx.xxx.xxx:8080/camanon.sdp?h264"
 * You can also specify a stream id and then start a maximum of 2 streams in parallel
 * For example: "http://xxx.xxx.xxx.xxx:8080/camanon.sdp?id=0&h264"
 * would start a video stream from the phone's camera to some remote client
 * and return an appropriate sdp file
 */
@SuppressWarnings("unused")
public class HttpServer extends BasicHttpServer{

	/** This messsage will be sent to the handler if an error occurs **/
	public static final int MESSAGE_ERROR = 0x07;
	
	/** Maximal number of streams that you can start from the HTTP server **/
	protected static final int MAX_STREAM_NUM = 2;
	
	protected Context context;
	
	public HttpServer(int port, Context context, Handler handler) {
		super(port, context.getAssets());
		this.context = context;
		addRequestHandler("/camanon.sdp*", new DescriptorRequestHandler(handler));
	} 
	
	public void stop() {
		super.stop();
		// If user has started a session with the HTTP Server, we need to stop it
		for (int i=0;i<DescriptorRequestHandler.session.length;i++) {
			if (DescriptorRequestHandler.session[i] != null) {
				DescriptorRequestHandler.session[i].stopAll();
				DescriptorRequestHandler.session[i].flush();
			}
		}
		
	}
	
	/** 
	 * Allow user to start streams (a session contains one or more streams) from the HTTP server by requesting 
	 * this URL: http://ip/spydroid.sdp (the RTSP server is not needed here) 
	 **/
	static class DescriptorRequestHandler implements HttpRequestHandler {

		private static Session[] session = new Session[MAX_STREAM_NUM];
		private Handler handler;
		
		public DescriptorRequestHandler(Handler handler) {
			this.handler = handler;
		}
		
		public synchronized void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException {
			Socket socket = ((ModifiedHttpContext)context).getSocket();
			String uri = request.getRequestLine().getUri();
			int id = 0;
			
			try {
				
				// A stream id can be specified in the URI, this id is associated to a session
				List<NameValuePair> params = URLEncodedUtils.parse(URI.create(uri),"UTF-8");
				uri = "";
				if (params.size()>0) {
					for (Iterator<NameValuePair> it = params.iterator();it.hasNext();) {
						NameValuePair param = it.next();
						if (param.getName().equals("id")) {
							try {	
								id = Integer.parseInt(param.getValue());
							} catch (Exception ignore) {}
						}
					}	
				}	

				params.remove("id");
				uri = "http://c?" + URLEncodedUtils.format(params, "UTF-8");
				
				// Stop all streams if a Session already exists
				if (session[id] != null) {
					if (session[id].getRoutingScheme()=="unicast") {
						session[id].stopAll();
						session[id].flush();
						session[id] = null;
					}
				}

				// Create new Session
				session[id] = new Session(socket.getLocalAddress(), socket.getInetAddress());

				// Parse URI and configure the Session accordingly 
				UriParser.parse(uri, session[id]);

				final String sessionDescriptor = session[id].getSessionDescriptor().replace("Unnamed", "Stream-"+id);

				response.setStatusCode(HttpStatus.SC_OK);
				EntityTemplate body = new EntityTemplate(new ContentProducer() {
					public void writeTo(final OutputStream outstream) throws IOException {
						OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
						writer.write(sessionDescriptor);
						writer.flush();
					}
				});
				body.setContentType("text/plain; charset=UTF-8");
				response.setEntity(body);

				// Start all streams associated to the Session
				session[id].startAll();

			} catch (Exception e) {
				response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				Log.e(TAG,e.getMessage()!=null?e.getMessage():"An unknown error occurred");
				e.printStackTrace();
				handler.obtainMessage(MESSAGE_ERROR, e);
			}

		}

	}

}

