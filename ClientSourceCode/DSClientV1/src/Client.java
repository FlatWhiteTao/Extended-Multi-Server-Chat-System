

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */
public class Client {
	public static SSLSocket socket;
	public static void main(String[] args) throws IOException, ParseException {
		
		System.setProperty("javax.net.ssl.keyStoreStore", "sslclientkey");
		System.setProperty("javax.net.ssl.keyPassword","12345qwe");
		System.setProperty("javax.net.ssl.trustStore", "sslclienttrust");
		System.setProperty("javax.net.ssl.trustStorePassword","12345qwe");
		//System.setProperty("javax.net.debug","all");
		

		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
		String identity = null;
		boolean debug = false;
		//Socket socket = null;
		//SSLSocket socket = null;
		
		try {
			//load command line args
			ComLineValues values = new ComLineValues();
			CmdLineParser parser = new CmdLineParser(values);
			//SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			
			try {
				parser.parseArgument(args);
				String hostname = values.getHost();
				identity = values.getIdeneity();
				int port = values.getPort();
				debug = values.isDebug();
				
				///socket = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
				socket = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
			} catch (CmdLineException e) {
				e.printStackTrace();
			}
			
			State state = new State(identity,"","","");
			
			// start sending thread
			MessageSendThread messageSendThread = new MessageSendThread(socket, state, debug);
			Thread sendThread = new Thread(messageSendThread);
			sendThread.start();
			
			// start receiving thread
			Thread receiveThread = new Thread(new MessageReceiveThread(socket, state, messageSendThread, debug));
			receiveThread.start();
			
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
		} catch (IOException e) {
			System.out.println("Communication Error: " + e.getMessage());
		}
	}
}
