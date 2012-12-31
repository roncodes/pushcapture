package com.ron.camanon.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * HTTP server based on this one: http://hc.apache.org/httpcomponents-core-ga/examples.html
 * You may add some logic to this server with addRequestHandler()
 * By default it serves files from /assets/www
 */
@SuppressWarnings("unused")
public class BasicHttpServer {

        public static final String TAG = "HttpServer";
        
        private final int port;
        private RequestListenerThread requestListenerThread;
    private HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
        private boolean running = false;
    
    public BasicHttpServer(final int port, final AssetManager assetManager) {
        this.port = port;
        addRequestHandler("*", new HttpFileHandler(assetManager));
    }

    /** 
     * You may add some HttpRequestHandlers before calling start()
     * All HttpRequestHandlers added after start() will be ignored
     * @param pattern Patterns may have three formats: * or *<uri> or <uri>*
     * @param handler A request handler
     */ 
    public void addRequestHandler(String pattern, HttpRequestHandler handler) {
        registry.register(pattern, handler);
    }
    
    public void start() throws IOException {
        if (running) return;
        requestListenerThread = new RequestListenerThread(port, registry);
        requestListenerThread.start();
        running = true;
    }
    
    public void stop() {
        if (!running) return;
        try {
                requestListenerThread.serversocket.close();
                requestListenerThread = null;
                } catch (Exception e) {
                        Log.e(TAG,"Error when close was called on serversocket: "+e.getMessage());
                }
        running = false;
    }
    
    private static class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params; 
        private final HttpService httpService;
        
        public RequestListenerThread(int port, HttpRequestHandlerRegistry registry) throws IOException {
            this.serversocket = new ServerSocket(port);
            this.params = new BasicHttpParams();
            this.params
                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "MajorKernelPanic HTTP Server");

            // Set up the HTTP protocol processor
            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            httpproc.addInterceptor(new ResponseDate());
            httpproc.addInterceptor(new ResponseServer());
            httpproc.addInterceptor(new ResponseContent());
            httpproc.addInterceptor(new ResponseConnControl());
            
            // Set up the HTTP service
            this.httpService = new HttpService(
                    httpproc, 
                    new DefaultConnectionReuseStrategy(), 
                    new DefaultHttpResponseFactory());
            this.httpService.setHandlerResolver(registry);
            this.httpService.setParams(params);
        }
        
        public void run() {
            Log.i(TAG,"Listening on port " + this.serversocket.getLocalPort());
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    Log.d(TAG,"Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn, socket);
                    t.setDaemon(true);
                    t.start();
                                } catch (SocketException e) {
                                        break;
                } catch (InterruptedIOException ex) {
                        Log.e(TAG,"Interrupted !");
                        break;
                } catch (IOException e) {
                    Log.d(TAG,"I/O error initialising connection thread: " 
                            + e.getMessage());
                    break;
                }
            }
            Log.i(TAG,"RequestListener stopped !");
        }
    }
    
    static class HttpFileHandler implements HttpRequestHandler  {
        
        private final static  String[] extensions = new String[] {
                "htm", "html", "gif", "jpg", "png", "js", "css",
        };

        private final static String[] mimeMediaTypes = new String[] {
                "text/html", "text/html", "image/gif", "image/jpeg",
                "image/png", "text/javascript", "text/css"
        };

        private final AssetManager assetManager;
        
        public HttpFileHandler(final AssetManager assetManager) {
            super();
            this.assetManager = assetManager;
        }
        
        public void handle(
                final HttpRequest request, 
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

                ByteArrayOutputStream buffer = new ByteArrayOutputStream(64000);
                byte[] tmp = new byte[4096]; 
            int length; 
            
                
            final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported"); 
            }

            @SuppressWarnings("deprecation")
			final String url = URLDecoder.decode(request.getRequestLine().getUri());
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                byte[] entityContent = EntityUtils.toByteArray(entity);
                Log.d(TAG,"Incoming entity content (bytes): " + entityContent.length);
            }
            
            InputStream stream = null;
            try {
                Log.i(TAG,"Requested: \""+url+"\"");
                stream =  assetManager.open("www"+(url.equals("/")?"/index.htm":url),AssetManager.ACCESS_STREAMING);
            }
            catch (IOException e) {
                // File does not exist
                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                EntityTemplate body = new EntityTemplate(new ContentProducer() {
                        public void writeTo(final OutputStream outstream) throws IOException {
                                OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
                                writer.write("<html><body><h1>");
                                writer.write("File ");
                                writer.write("www"+url);
                                writer.write(" not found");
                                writer.write("</h1></body></html>");
                                writer.flush();
                        }
                });
                body.setContentType(getMimeMediaType(url)+"; charset=UTF-8");
                response.setEntity(body);
                Log.d(TAG,"File " + "www" + url + " not found");
                return;

            }

            // File exist
            // AAPT compresses assets so first we need to uncompress them to determine their length
            while ((length = stream.read(tmp)) != -1) buffer.write(tmp, 0, length);
            response.setEntity(new InputStreamEntity(new ByteArrayInputStream(buffer.toByteArray()), buffer.size()));
            response.setStatusCode(HttpStatus.SC_OK);
            Log.d(TAG,"Serving file " + "www" + url);
            stream.close();
            buffer.flush(); 
            buffer.reset();            
        }
        
        private String getMimeMediaType(String fileName) {
                String extension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()-1);
                for (int i=0;i<extensions.length;i++) {
                        if (extensions[i]==extension) 
                                return mimeMediaTypes[i];
                }
                return mimeMediaTypes[0];
        }
        
    }
    
    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;
        private final Socket socket;
        
        public WorkerThread(
                final HttpService httpservice, 
                final HttpServerConnection conn,
                final Socket socket) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
            this.socket = socket;
        }
        
        public void run() {
            Log.d(TAG,"New connection thread");
            HttpContext context = new ModifiedHttpContext(socket);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                Log.e(TAG,"Client closed connection");
            } catch (SocketTimeoutException ex) {
                Log.e(TAG,"Socket timeout");
            } catch (IOException ex) {
                Log.e(TAG,"I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                Log.e(TAG,"Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {}
            }
        }
    }
}
