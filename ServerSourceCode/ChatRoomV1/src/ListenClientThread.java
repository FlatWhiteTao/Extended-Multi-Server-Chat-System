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

public class ListenClientThread extends Thread{
	
	
	SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
			.getDefault();
	
	private SSLServerSocket listeningClientSocket = null;
	private SSLSocket clientSocket =null;
	
	private Servers theServer;
	
	public Servers getTheServer() {
		return theServer;
	}

	public void setTheServer(Servers theServer) {
		this.theServer = theServer;
	}

	public ListenClientThread(int port,Servers the_Server)  {
		try {
			//this.listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);//Listen for incoming connections for ever 
			this.listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);//Listen for incoming connections for ever 
			this.theServer = the_Server;
		}catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			while (true) {
				//Accept an incoming client connection request 
				//clientSocket = (SSLSocket) listeningClientSocket.accept(); //This method will block until a connection request is received
				clientSocket = (SSLSocket) listeningClientSocket.accept();  //This method will block until a connection request is received
				ClientConnection c = new ClientConnection(clientSocket,theServer);
				c.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(listeningClientSocket != null) {
				try {
					listeningClientSocket.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			if(clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
	}
}
