import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class ListenServerThread extends Thread{
	SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
			.getDefault();
	
	private SSLServerSocket listeningServerSocket;
	private SSLSocket serverSocket;
	private Servers theServer;
	
	public Servers getTheServer() {
		return theServer;
	}

	public void setTheServer(Servers theServer) {
		this.theServer = theServer;
	}

	public ListenServerThread(int port,Servers the_Server) {
		try {
			//this.listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);//Listen for incoming connections for ever 
			this.listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);//Listen for incoming connections for ever 
			this.theServer = the_Server;
		}catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			while (true) {
				//Accept an incoming client connection request 
				serverSocket = (SSLSocket)listeningServerSocket.accept();//This method will block until a connection request is received
				//serverSocket = (SSLSocket)listeningServerSocket.accept(); //This method will block until a connection request is received
				ServerConnection s = new ServerConnection(serverSocket,theServer);
				s.start();
			}
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			if(listeningServerSocket != null) {
				try {
					listeningServerSocket.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			if(serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
	}
}
