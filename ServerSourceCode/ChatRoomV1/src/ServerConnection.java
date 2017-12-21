import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.net.ssl.SSLSocket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class ServerConnection extends Thread {
	private Servers theServer;//socket receiver server
	//private String serverid;//socket sender server
	private BufferedReader in;
	private BufferedWriter out;
	private SSLSocket serverSocket;
	private String connectionid;
	
	public String getConnectionid() {
		return connectionid;
	}
	public void setConnectionid(String connectionid) {
		this.connectionid = connectionid;
	}
	public Servers getTheServer() {
		return theServer;
	}
	public void setTheServer(Servers theServer) {
		this.theServer = theServer;
	}
	public Socket getServerSocket() {
		return serverSocket;
	}
	public void setServerSocket(SSLSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public ServerConnection (SSLSocket aServerSocket,Servers the_Server) {
		try {
			this.serverSocket = aServerSocket;
			this.theServer = the_Server;
			this.in = new BufferedReader(new InputStreamReader(aServerSocket.getInputStream(), "UTF-8"));
			this.out = new BufferedWriter(new OutputStreamWriter(aServerSocket.getOutputStream(), "UTF-8"));
		} catch(IOException e) {
			//System.out.println("Connection:"+e.getMessage());
		}
	}
	
	//switch functions
	public synchronized void cvote(JSONObject jsCvote){//it is a vote for clientid n.
		String clientid = (String)jsCvote.get("identity");// it is a vote for which clientid
		String locked = (String)jsCvote.get("locked");//it is a true or false vote
		theServer.votecCount(clientid,locked);
		//still voting
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void votec(JSONObject jsVotec){//the server would vote for it v.
		String fromServerid = (String)jsVotec.get("serverid");
		String fromHostName = null;
		String fromServerPort = null;
		String clientid = (String)jsVotec.get("identity");
		int tf = 0;
		JSONObject clientIdVoteMsg = new JSONObject();
		clientIdVoteMsg.put("type", "lockidentity");
		clientIdVoteMsg.put("serverid",theServer.getServerid());
		clientIdVoteMsg.put("identity",clientid);
		for(int i =0;i<theServer.getServer_clients().size();i++)
			if(theServer.getServer_clients().get(i).getIdentity().equals(clientid)){
				System.out.println("it is used by me haha!");
				tf = 1;
			}
		for(int j = 0;j<theServer.getServer_lockclients().size();j++)
			if(theServer.getServer_lockclients().get(j).equals(clientid)){
				tf = 1;
				System.out.println("it is locked by me haha!");
			}
		if(tf==1){
			//the server does contain the cilentid
			clientIdVoteMsg.put("locked","false");
		}
		else{//this server does not contain the clientid
			clientIdVoteMsg.put("locked","true");
			theServer.lockClient(clientid);
		}
		String cvoteJSONText = clientIdVoteMsg.toJSONString();
		for(int i = 0;i<theServer.getServer_Servers().size();i++){
			if(theServer.getServer_Servers().get(i).get(0).equals(fromServerid)){
				fromHostName = theServer.getServer_Servers().get(i).get(1);
				fromServerPort = theServer.getServer_Servers().get(i).get(3);
			}
		}
		SSendToS aServerSendToServers = new SSendToS(fromHostName,
				Integer.parseInt(fromServerPort), cvoteJSONText);
		aServerSendToServers.start();
	}
	
	public synchronized void rvote(JSONObject jsRvote){//it is a vote for roomid n.
		String roomid = (String)jsRvote.get("roomid");// it is a vote for which roomid
		String locked = (String)jsRvote.get("locked");//it is a true or false vote
		theServer.voterCount(roomid,locked);
		//still voting
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void voter(JSONObject jsVoter){//the server would vote for it v.
		String roomid = (String)jsVoter.get("roomid");
		String fromServerid = (String)jsVoter.get("serverid");
		String fromHostName = null;
		String fromServerPort = null;
		int tf = 0;
		JSONObject roomIdVoteMsg = new JSONObject();
		roomIdVoteMsg.put("type", "lockroomid");
		roomIdVoteMsg.put("serverid",theServer.getServerid());
		roomIdVoteMsg.put("roomid",roomid);
		for(int i =0;i<theServer.getServer_lockrooms().size();i++)
			if(theServer.getServer_lockrooms().get(i).equals(roomid))
				tf = 1;
		if(tf==1){
			//the server does lock the roomid
			roomIdVoteMsg.put("locked","false");
		}
		else{//this server does not lock the roomid
			roomIdVoteMsg.put("locked","true");
			theServer.lockRoom(roomid);
		}
		//need not to check roomlist
		String rvoteJSONText = roomIdVoteMsg.toJSONString();
		for(int i = 0;i<theServer.getServer_Servers().size();i++){
			if(theServer.getServer_Servers().get(i).get(0).equals(fromServerid)){
				fromHostName = theServer.getServer_Servers().get(i).get(1);
				fromServerPort = theServer.getServer_Servers().get(i).get(3);
			}
		}
		SSendToS aServerSendToServers = new SSendToS(fromHostName,
				Integer.parseInt(fromServerPort), rvoteJSONText);
		aServerSendToServers.start();
		//sendMsg(rvoteJSONText);
	}
	@SuppressWarnings("unchecked")
	public void login(JSONObject JsLG){
		//s0 receives the LoginRequest and verify 
		String username = (String) JsLG.get("username");
		String password = (String) JsLG.get("password");
		String fromServerId = (String) JsLG.get("serverId");
		
		
		UserDB userLogin = new UserDB();
		userLogin.setUsername(username);
		userLogin.setPassword(password);
		userLogin.setOnline(false);

		JSONObject approval = new JSONObject();
		approval.put("type", "verify");
		approval.put("username", username);
		approval.put("password", password);
		approval.put("verification","false");
		
		for(int i =0; i< theServer.getServer_UserDB().size(); i++){
			System.out.println("userDBDBBBBBBBBBBBBBBB");
			System.out.println("userDB "+i+" username:"+theServer.getServer_UserDB().get(i).getUsername());
			System.out.println("userDB "+i+" password:"+theServer.getServer_UserDB().get(i).getPassword());
			System.out.println("userDB "+i+" isonline:"+theServer.getServer_UserDB().get(i).isOnline());
			System.out.println("********MAPUS**********"+this.theServer.getMapUS().get(theServer.getServer_UserDB().get(i).getUsername()));
			
			if(userLogin.getUsername().equals(theServer.getServer_UserDB().get(i).getUsername())
					&& userLogin.getPassword().equals(this.theServer.getServer_UserDB().get(i).getPassword())
					&& !this.theServer.getServer_UserDB().get(i).isOnline()){	
				System.out.println("wojinlaile");	
				approval.put("verification","true");
				theServer.getServer_UserDB().get(i).setOnline(true);
				break;
				}
		}
		String fromHostName = null;
		String fromServerPort = null;
		for(int k = 0;k<theServer.getServer_Servers().size();k++){
			if(theServer.getServer_Servers().get(k).get(0).equals(fromServerId)){
				 fromHostName = theServer.getServer_Servers().get(k).get(1);
				 fromServerPort = theServer.getServer_Servers().get(k).get(3);
			}
		}
		SSendToS aServerSendToServers = new SSendToS(fromHostName,
				Integer.parseInt(fromServerPort), approval.toJSONString());
		aServerSendToServers.start();
	}
	
	
	public void verify(JSONObject JsVF){
		String username = (String) JsVF.get("username");
		String verification = (String) JsVF.get("verification");
		if(verification.equals("true")){
			theServer.getUserVerify().put(username, 1);
		}
		if(verification.equals("false")){
			theServer.getUserVerify().put(username, 2);
		}
	}
	
	public void releaseidentity(JSONObject jsRI){
		String clientid = (String)jsRI.get("identity");
		for(int j = 0;j<theServer.getServer_clients().size();j++){
			System.out.println("******"+theServer.getServer_clients().get(j).getIdentity());
		}		
		theServer.unlockClient(clientid);
		for(int j = 0;j<theServer.getServer_clients().size();j++){
			System.out.println("&&&&&&"+theServer.getServer_clients().get(j).getIdentity());
		}
	}
	
	public void releaseroomid(JSONObject jsRR){
		String roomid = (String)jsRR.get("roomid");
		String roomofserverid = (String)jsRR.get("serverid");
		String ifApprove = (String)jsRR.get("approved");
		if(ifApprove.equals("true")){
			theServer.unlockRoom(roomid);
			Vector<String> aNewRoomInfo = new Vector<String>();
			aNewRoomInfo.add(roomid);
			aNewRoomInfo.add(roomofserverid);//0-roomid,1-serverid
			theServer.addToAllRoomList(aNewRoomInfo);
			//unlock and add to allroom_list
		}
		else
			theServer.unlockRoom(roomid);
		//just unlock
	}
	
	public void deleteroom(JSONObject jsDR){
		String roomid = (String)jsDR.get("roomid");
		theServer.deleteRoomi(roomid);
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public void introduce(JSONObject jsINTRO){
		String newServerid = (String)jsINTRO.get("newServerid");
		String newHostname = (String)jsINTRO.get("newHostname");
		String newCport = (String)jsINTRO.get("newCport");
		String newSport = (String)jsINTRO.get("newSport");
		Vector<String> newServerBoard = new Vector<String>();
		newServerBoard.add(newServerid);
		newServerBoard.add(newHostname);
		newServerBoard.add(newCport);
		newServerBoard.add(newSport);
		theServer.addAServer(newServerBoard);//add the new server into its server
		Vector<String> newRoomBoard = new Vector<String>();
		newRoomBoard.add("MainHall-".concat(newServerid));
		newRoomBoard.add(newServerid);
		theServer.addARoom(newRoomBoard);
		JSONObject fbMsg = new JSONObject();
		fbMsg.put("type", "feedback");
		ArrayList<String> rooms = new ArrayList<String>();
		for(int i = 0;i < theServer.getServer_rooms().size();i++)//add its room info into the feedbackJson
			rooms.add(theServer.getServer_rooms().get(i).getRoomid());
		fbMsg.put("serverid", theServer.getServerid());
		fbMsg.put("rooms", rooms);
		String fbJSONText = fbMsg.toJSONString();
		SSendToS aServerSendToServers = new SSendToS(newHostname,
				Integer.parseInt(newSport), fbJSONText);//send feedback
		aServerSendToServers.start();
	}
	
	public void feedback(JSONObject jsFB){
		String fromServerid = (String)jsFB.get("serverid");
		JSONArray array = (JSONArray) jsFB.get("rooms");
		if(!fromServerid.equals("s0")){
	    	String[] arr = new String[array.size()];// to be tested
	    	for(int i = 0;i<array.size();i++){
	    		arr[i] = (String) array.get(i);
	    		Vector<String> newRoomBoard = new Vector<String>();
	    		newRoomBoard.add(arr[i]);
	    		newRoomBoard.add(fromServerid);
	    		theServer.addARoom(newRoomBoard);
	    	}
		}
		else
			;
	}
	
	@SuppressWarnings("unchecked")
	public void restart(JSONObject jsRS){
		String serverid = (String)jsRS.get("serverid");
		String reHostname = (String)jsRS.get("reHostname");
		String reClientport = (String)jsRS.get("reClientport");
		String reServerport = (String)jsRS.get("reServerport");
		Vector<String> newServerBoard = new Vector<String>();
		newServerBoard.add(serverid);
		newServerBoard.add(reHostname);
		newServerBoard.add(reClientport);
		newServerBoard.add(reServerport);
		theServer.addAServer(newServerBoard);//add the new server into its server
		Vector<String> newRoomBoard = new Vector<String>();
		newRoomBoard.add("MainHall-".concat(serverid));
		newRoomBoard.add(serverid);
		theServer.addARoom(newRoomBoard);
		JSONObject fbMsg = new JSONObject();
		fbMsg.put("type", "feedback");
		ArrayList<String> rooms = new ArrayList<String>();
		for(int i = 0;i < theServer.getServer_rooms().size();i++)//add its room info into the feedbackJson
			rooms.add(theServer.getServer_rooms().get(i).getRoomid());
		fbMsg.put("serverid", theServer.getServerid());
		fbMsg.put("rooms", rooms);
		String fbJSONText = fbMsg.toJSONString();
		SSendToS aServerSendToServers = new SSendToS(reHostname,
				Integer.parseInt(reServerport), fbJSONText);//send feedback
		aServerSendToServers.start();
	}
	
	
	public void parser(String data) {
		JSONParser parser = new JSONParser();
		try{
			JSONObject jsOBJ = (JSONObject) parser.parse(data);
			String type = (String) jsOBJ.get("type");
			switch(type){
			case "lockidentity":
				if(jsOBJ.containsKey("locked"))//this msg is a vote for this server
					cvote(jsOBJ);
				else//this server need to vote for this msg
					votec(jsOBJ);
				break;
			case "releaseidentity":
				releaseidentity(jsOBJ);
				break;
			case "lockroomid":
				if(jsOBJ.containsKey("locked"))
					rvote(jsOBJ);
				else
					voter(jsOBJ);
				break;
			case "releaseroomid":
				releaseroomid(jsOBJ);
				break;
			case "deleteroom":
				deleteroom(jsOBJ);
				break;
			case "login":
				login(jsOBJ);
				break;
			case "verify":
				verify(jsOBJ);
				break;
				//other cases to be written
			case "releaseusername":
				releaseusername(jsOBJ);
				break;
			case "heartbeat":
				heartbeat(jsOBJ);
				break;
			case "mapUS":
				mapUS(jsOBJ);
				break;
			case "changeLogin":
				changeLogin(jsOBJ);
				break;
			case "introduce":
				introduce(jsOBJ);
				break;
			case "feedback":
				feedback(jsOBJ);
				break;
			case "restart":
				restart(jsOBJ);
				break;
			case "ifstart":
				ifstart(jsOBJ);
				break;
			case "confirmS":
				confirmS(jsOBJ);
				break;
			default :
				//System.out.println("wrong type!");
				break;
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void ifstart(JSONObject jsIS){
		//String ifserverid = (String)jsIS.get("ifserverid");
		int tmprunning = theServer.getRunning();
		tmprunning++;
		theServer.setRunning(tmprunning);
		if(tmprunning == theServer.getServer_Servers().size()-1){
			JSONObject confirmSMsg = new JSONObject();
			confirmSMsg.put("type", "confirmS");
			String confirmSJSONText = confirmSMsg .toJSONString();
			theServer.broadcast(confirmSJSONText);
			theServer.startHB();
		}
	}
	
	
	public void confirmS(JSONObject jsCS){
		theServer.startHB();
	}
	
	
	public void changeLogin(JSONObject JsCL){
		String serverId = (String) JsCL.get("serverId");
		
		List<String> keyFilter = new ArrayList<String>();
		
			Set keySet = this.getTheServer().getMapUS().entrySet();
			
			Iterator iter = keySet.iterator();
			while (iter.hasNext()){
				Map.Entry entry = (Map.Entry)iter.next();
				String key =  (String)entry.getKey();
				if(this.getTheServer().getMapUS().get(key).equals(serverId)){
					keyFilter.add(key);
				}
			}
		    
			for(int m=0; m<this.getTheServer().getServer_UserDB().size();m++){
		    	for(String username: keyFilter){
		    		if(this.getTheServer().getServer_UserDB().get(m).getUsername().equals(username))
		    			this.getTheServer().getServer_UserDB().get(m).setOnline(false);
		    			keyFilter.remove(username);
		    			break;
		    	}
		  }
	}
	
	
	public void mapUS(JSONObject JsMP){
		String username = (String) JsMP.get("username");
		String serverId = (String) JsMP.get("serverId");
		this.theServer.setMapUS(username, serverId);
	}
	
	@SuppressWarnings("unchecked")
	public void heartbeat(JSONObject JsHB){
		//String fromServerId = (String) JsHB.get("serverId");
		JSONObject heartbeatSignal = new JSONObject();
		heartbeatSignal.put("type", "heartbeat");
		heartbeatSignal.put("status", "alive");
		heartbeatSignal.put("serverId", this.theServer.getServerid());
		try {
			System.out.println("heartbeatoutdata"+heartbeatSignal.toJSONString());
			out.write(heartbeatSignal.toJSONString() + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void releaseusername(JSONObject JsRU) {
		String username = (String)JsRU.get("username");
		String thisServerId = this.theServer.getServerid();
		System.out.println("this sever "+thisServerId+" would relaease username"+username);
		for(UserDB userdb: this.theServer.getServer_UserDB()){
			if (userdb.getUsername().equals(username)){
				userdb.setOnline(false);
				System.out.println("username "+username + "has been offline");
			}
				
		}
		
	}
	public void close(){
		if(serverSocket!=null)
			try { 
				serverSocket.close();
			} catch (IOException e){
				/*close failed*/
			}
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
	}
	
	public void run(){
		try { // an echo server
			String indata = null;
			while((indata = in.readLine()) != null) {
				System.out.println("serverindata: "+indata);
				parser(indata);
				this.close();
			}
		}catch (EOFException e){
			//System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {
			//System.out.println("readline:"+e.getMessage());
		} finally{
			if(serverSocket!=null)
				try { 
					serverSocket.close();
				} catch (IOException e){
					/*close failed*/
				}
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
		}
	}
}
