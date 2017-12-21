import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;


import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class SSendToS extends Thread{
	
	private SSLSocket serverSocket;//ssl
	private BufferedReader in;
	private BufferedWriter out;
	private String Msg;
	
	public SSLSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(SSLSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public String getMsg() {
		return Msg;
	}

	public void setMsg(String msg) {
		Msg = msg;
	}

	public SSendToS(String hostname, int serverport, String Msg){
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		this.Msg = Msg;
		InetAddress address = null;
		try {
			if(hostname.equals("localhost"))
				address = InetAddress.getLocalHost();
			else
				address = InetAddress.getByName(hostname);
			serverSocket =(SSLSocket) sslsocketfactory.createSocket(address, serverport);
			this.in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
			this.out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
		} catch(Exception e){
			//e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			System.out.println("serveroutdata: "+Msg);/////////////////////////
			out.write(Msg+"\n");
			out.flush();
		} catch (IOException e) {
			//e.printStackTrace();
		}finally{
			if(in!=null)
				try { 
					in.close();
				} catch (IOException e){
					/*close failed*/
				}
			if(out!=null)
				try { 
					out.close();
				} catch (IOException e){
					/*close failed*/
				}
			if(serverSocket!=null)
				try { 
					serverSocket.close();
				} catch (IOException e){
					/*close failed*/
				}
			
		}
	}
}
