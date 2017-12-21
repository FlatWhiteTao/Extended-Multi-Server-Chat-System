import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */
public class HeartBeat extends Thread {
	private Servers server;
	
	public HeartBeat(Servers server){
		this.server = server;
		try {
			Thread.sleep(1000);//to update
			this.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void run(){
		while(true){
			JSONParser parser = new JSONParser();
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			JSONObject heartBeatMsg = new JSONObject();
			heartBeatMsg.put("type", "heartbeat");
			heartBeatMsg.put("serverId", server.getServerid());
			//SSLSocket socket = null;
			SSLSocket serverSocket = null;
			String localServerId = server.getServerid();
			BufferedReader in;
			DataOutputStream out;
			// find other servers excluding local server and s0
			for(int i = 0;i < this.server.getServer_Servers().size();i++){
				System.out.println(server.getServer_Servers().size()+"0000000000000");
				if(!this.server.getServer_Servers().get(i).get(0).equals(localServerId)
						&&!this.server.getServer_Servers().get(i).get(0).equals("s0")){
					
					String toServerId = this.server.getServer_Servers().get(i).get(0);
					String hostname = this.server.getServer_Servers().get(i).get(1);
					String serverPort = this.server.getServer_Servers().get(i).get(3);
					InetAddress address = null;
					try {
						//socket = (SSLSocket) sslsocketfactory.createSocket(address,Integer.parseInt(serverPort));
						 if(hostname.equals("localhost"))
							address = InetAddress.getLocalHost();
						 else
							address = InetAddress.getByName(hostname);
						 serverSocket = (SSLSocket) sslsocketfactory.createSocket(hostname, Integer.parseInt(serverPort));
						 
						 in = new BufferedReader(new InputStreamReader(
								 serverSocket.getInputStream(), "UTF-8"));
                         out =new DataOutputStream( serverSocket.getOutputStream());
                        out.write((heartBeatMsg.toJSONString() + "\n").getBytes("UTF-8"));
                        out.flush();
                        serverSocket.setSoTimeout(10000);
						JSONObject signal = new JSONObject();
						try {
							String indata = null;
							indata = in.readLine();
							if(indata==null){
								System.out.println("the server "+toServerId+" is crashed1!!");
								server.processCrash(toServerId);
							}
							else{
								System.out.println("heartbeatindata = "+indata);
								signal = (JSONObject) parser.parse(indata);
								String status = (String) signal.get("status");
								if(!status.equals("alive")){
									System.out.println("the server "+toServerId+" is crashed2!!");
									server.processCrash(toServerId);
								}
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("the server "+toServerId+" is crashed1!!");
                    	System.out.println("this heatbeat thread is closed!");
						server.processCrash(toServerId);
						//e.printStackTrace();
					} finally {
						/*(in!=null)
							try { 
								in.close();
							} catch (IOException e){
							}
						if(out!=null)
							try { 
								out.close();
							} catch (IOException e){
							}*/
                        if (serverSocket != null) {
                            try {
                            	
                            	serverSocket.close();
                            } catch (IOException e) {
                                //e.printStackTrace();
                            }
                        }
                    }
				}
			}
			try {
                Thread.sleep(3000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
		}
	}
}
