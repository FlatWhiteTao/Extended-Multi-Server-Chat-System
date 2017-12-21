import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class ClientConnection extends Thread {
	public static final String IdRegex = "^[a-zA-Z][a-zA-Z0-9]{2,15}";
	private String connectionid;
	private Servers theServer;
	private Rooms theRoom;
	private String clientid;
	private BufferedReader in;
	private BufferedWriter out;
	private SSLSocket clientSocket;
	private int quitCount;
	private String loginUsername;
	private int test = 0;/////
	
	public String getConnectionid() {
		return connectionid;
	}
	public void setConnectionid(String connectionid) {
		this.connectionid = connectionid;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public Socket getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(SSLSocket clientSocket) {
		this.clientSocket = clientSocket;
	}
	public Servers getTheServer() {
		return theServer;
	}
	public void setTheServer(Servers theServer) {
		this.theServer = theServer;
	}
	public Rooms getTheRoom() {
		return theRoom;
	}
	public void setTheRoom(Rooms theRoom) {
		this.theRoom = theRoom;
	}
	
	
	public ClientConnection (SSLSocket aClientSocket,Servers the_Server) {//constructer
		try {
			this.clientSocket = aClientSocket;
			this.theServer = the_Server;
			this.theRoom = null;
			this.clientid = "0";
			this.connectionid = "";
			this.quitCount = 0;
			this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
		} catch(IOException e) {
			//System.out.println("Connection:"+e.getMessage());
		}//without close() cause it is a long connection
	}
	
	public void parser(String data) {//parse the JSONObject type and switch
		JSONParser parser = new JSONParser();
		try{
			JSONObject jsOBJ = (JSONObject) parser.parse(data);
			String type = (String) jsOBJ.get("type");
			switch(type){
			case "login":
				login(jsOBJ);
				break;
			case "newidentity":
				newIdentity(jsOBJ);
				break;
			case "list":
				System.out.println("replying list msg 000");
				list(jsOBJ);
				break;
			case "who":
				who(jsOBJ);
				break;
			case "createroom":
				createroom(jsOBJ);
				break;
			case "join":
				join(jsOBJ);
				break;
			case "movejoin":
				movejoin(jsOBJ);
				break;
			case "deleteroom":
				deleteroom(jsOBJ);
				break;
			case "message":
				message(jsOBJ);
				break;
			case "quit":
				quitCount = 1;
				quit(jsOBJ,0);
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
	public void run(){
		try { // an echo server
			String indata = null;
			while((indata = in.readLine()) != null) {
				
				/*test++;
				for(int i = 0;i<theServer.getServer_lockclients().size();i++){
					System.out.println("+++"+theServer.getServer_lockclients().get(i));
				}
				for(int j = 0;j<theServer.getServer_clients().size();j++){
					System.out.println("---"+theServer.getServer_clients().get(j).getIdentity());
				}
				System.out.println("clientindata: "+indata+test);//////////*/
				parser(indata);
			}// read a line of data from the stream
		}catch (EOFException e){
			//System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {
			JSONObject jsOBJ = new JSONObject();
			jsOBJ.put("type", "quit");
			if(quitCount==0)
				quit(jsOBJ,1);
			this.closeConnection();
		}
	}
	
	public synchronized void sendMsg(String outdata) {//the server sends msg to the connected client in this connection
		try {
			/*test++;
			System.out.println("clientoutdata: "+outdata+test);
			for(int i = 0;i<theServer.getServer_lockclients().size();i++){
				System.out.println("++++++"+theServer.getServer_lockclients().get(i));
			}
			for(int j = 0;j<theServer.getServer_clients().size();j++){
				System.out.println("------"+theServer.getServer_clients().get(j).getIdentity());
			}*/
			out.write(outdata+"\n");
			out.flush();
		} catch (IOException e) {
		}
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public void login(JSONObject JsonLG){
		String username = (String) JsonLG.get("username");
		String password = (String) JsonLG.get("password");
		if(theServer.getServerid().equals("s0")){
			JSONObject authentication = new JSONObject();
			authentication.put("type", "login");
			authentication.put("username", username);
			authentication.put("password", password);
			authentication.put("authentactied","false");
			System.out.println("s0, illegal server");//////////////
			this.sendMsg(authentication.toJSONString());
			quitCount = 1;
			closeConnection();
		}
		this.theServer.getUserVerify().put("usernmae", -1);
		
		UserDB userLogin = new UserDB();
		userLogin.setUsername(username);
		userLogin.setPassword(password);
		userLogin.setOnline(false);
		
		String address = null;
		int serverPort = 0;
		
		// pack login request to json
		JSONObject verification = new JSONObject();
		verification.put("type", "login");
		verification.put("username", username);
		verification.put("password", password);
		verification.put("serverId", this.theServer.getServerid());
		
		//get s0's address and serverport	
		for(int i = 0;i < this.theServer.getServer_Servers().size();i++){
		    if(this.theServer.getServer_Servers().get(i).get(0).equals("s0")){
		    	address = this.theServer.getServer_Servers().get(i).get(1);
		    	serverPort = Integer.parseInt(this.theServer.getServer_Servers().get(i).get(3));
		    	break;
		    }
		}
		 // send message to S0 to verify the login request
		SSendToS sendToS0 = new SSendToS(address,
		serverPort, verification.toJSONString());
		sendToS0.start();
		
		// wait for the verification result via server's data change
		while(true){
			if(theServer.getUserVerify().get(username)==null)
				continue;
			if(this.theServer.getUserVerify().get(username)==1){
				System.out.println("this user should be accepted");
				this.loginUsername = username;
				JSONObject authentication = new JSONObject();
				authentication.put("type", "login");
				authentication.put("username", username);
				authentication.put("password", password);
				authentication.put("authentactied","true");
				this.sendMsg(authentication.toJSONString());
				this.theServer.getUserVerify().put(username, -1);
				break;
			}
			else if(this.theServer.getUserVerify().get(username)==2){
				System.out.println("this user should not be accepted");
				JSONObject authentication = new JSONObject();
				authentication.put("type", "login");
				authentication.put("username", username);
				authentication.put("password", password);
				authentication.put("authentactied","false");
				this.sendMsg(authentication.toJSONString());
				this.theServer.getUserVerify().put(username, -1);
				quitCount = 1;
				closeConnection();
				break;
			}
		}
		
}
		
	

	@SuppressWarnings("unchecked")
	public void newIdentity(JSONObject JsonNI) {
		String newid = (String) JsonNI.get("identity");
		if(!Pattern.matches(IdRegex,newid)){//wrong name
			System.out.println("illegal identity!");
			JSONObject sTocMsg = new JSONObject();
			sTocMsg.put("type", "newidentity");
			sTocMsg.put("approved", "false");
			String sTocJSONText = sTocMsg.toJSONString();
			quitCount = 1;
			sendMsg(sTocJSONText);//send approved false msg to the client
			releaseUsername(this.loginUsername);
			this.closeConnection();
		}
		else{
			if(theServer.IfLockClient(newid)||theServer.IfExistClient(newid)){
				//exist in the server or locked by any one server
				System.out.println("locked or used by me!");
				JSONObject sTocMsg = new JSONObject();
				sTocMsg.put("type", "newidentity");
				sTocMsg.put("approved", "false");
				String sTocJSONText = sTocMsg.toJSONString();
				quitCount = 1;
				sendMsg(sTocJSONText);//send approved false msg to the client
				releaseUsername(this.loginUsername);
				this.closeConnection();
			}
			else{//vote for if the clientid does not exist or be locked in this server
				int serverNum = theServer.getServerNumber();
				int pass = 0;
				if(serverNum>1){// if single server
					theServer.lockClient(newid);
					JSONObject voteClientIdMsg = new JSONObject();
					voteClientIdMsg.put("type", "lockidentity");
					voteClientIdMsg.put("serverid",theServer.getServerid());
					voteClientIdMsg.put("identity",newid);
					String votecJSONText = voteClientIdMsg.toJSONString();
					theServer.addVotec(newid);//add a vote counter for this clientid
					theServer.broadcast(votecJSONText);//broadcast for voting
				}
				else{
					pass = 1;
				}
				while(pass == 0){//still voting?
					pass = theServer.queryVotec(newid);
				}
				if(pass == 1){//all true
					JSONObject sTocMsg = new JSONObject();
					sTocMsg.put("type", "newidentity");
					sTocMsg.put("approved", "true");
					String sTocJSONText = sTocMsg.toJSONString();
					sendMsg(sTocJSONText);
					//send approved true msg to client
					this.clientid = newid;
					theRoom = theServer.getMainhall();
					theRoom.addConnections(this);
					//set room and connection
					ClientBoard aNewClient = new ClientBoard(clientid,"MainHall",theServer.getServerid());
					aNewClient.setUsername(this.loginUsername);
					// send message to s0 To update this user name's serverId
					JSONObject mapUS = new JSONObject();
					mapUS.put("type", "mapUS");
					mapUS.put("username", this.loginUsername);
					
					mapUS.put("serverId", this.theServer.getServerid());
					this.theServer.sendMsgToS0(mapUS.toJSONString());
					theServer.addServer_Clients(aNewClient);
					//add the clientid to this server clientlist
					theRoom.addClient(aNewClient);
					//add the clientid to this server mainhall clientlist
					theServer.unlockClient(clientid);
					//remove clientid from the locklist of this server
					JSONObject roomchangeMsg = new JSONObject();//roomchange msg
					roomchangeMsg.put("type", "roomchange");
					roomchangeMsg.put("identity",clientid);
					roomchangeMsg.put("former","");
					roomchangeMsg.put("roomid",theRoom.getRoomid());
					String roomchangeJSONText = roomchangeMsg.toJSONString();
					theServer.broadcastRoom(roomchangeJSONText,theRoom);
					//broadcast roomchange msg
					JSONObject releaseClientIdMsg = new JSONObject();//release msg
					releaseClientIdMsg.put("type", "releaseidentity");
					releaseClientIdMsg.put("serverid",theServer.getServerid());
					releaseClientIdMsg.put("identity",clientid);
					String releasecJSONText = releaseClientIdMsg.toJSONString();
					theServer.broadcast(releasecJSONText);//broadcast release msg
				}
				else{//some false
					JSONObject releaseClientIdMsg = new JSONObject();//release msg
					releaseClientIdMsg.put("type", "releaseidentity");
					releaseClientIdMsg.put("serverid",theServer.getServerid());
					releaseClientIdMsg.put("identity",newid);
					String releasecJSONText = releaseClientIdMsg.toJSONString();
					theServer.broadcast(releasecJSONText);//broadcast release msg;
					JSONObject sTocMsg = new JSONObject();
					sTocMsg.put("type", "newidentity");
					sTocMsg.put("approved", "false");
					String sTocJSONText = sTocMsg.toJSONString();
					quitCount = 1;
					theServer.unlockClient(newid);
					sendMsg(sTocJSONText);
					releaseUsername(this.loginUsername);
					this.closeConnection();
					//remove clientid from the locklist
				}
				System.out.println("111num of votecounts: "+theServer.getVotecCounters().size());
				for(int i = 0;i<theServer.getVotecCounters().size();i++)
					System.out.println(theServer.getVotecCounters().get(i).getVoteId());
				theServer.removeVotec(newid);
				System.out.println("222num of votecounts: "+theServer.getVotecCounters().size());
				for(int i = 0;i<theServer.getVotecCounters().size();i++)
					System.out.println(theServer.getVotecCounters().get(i).getVoteId());
				//remove the clientid voteCounter
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void list(JSONObject jsList){
		System.out.println("replying list msg");
		JSONObject sTocMsg = new JSONObject();
		sTocMsg.put("type", "roomlist");
		System.out.println("1");
		ArrayList<String> roomlist = new ArrayList<String>();
		System.out.println("1.1"+theServer.getServer_allrooms().size());
		for(int i = 0;i < theServer.getServer_allrooms().size();i++){
			System.out.println("1.2"+theServer.getServer_allrooms().get(i).get(0));
			roomlist.add(theServer.getServer_allrooms().get(i).get(0));//0-roomid,1-serverid
			System.out.println("2s");
		}
		sTocMsg.put("rooms", roomlist);
		System.out.println("3");
		String sTocJSONText = sTocMsg.toJSONString();
		sendMsg(sTocJSONText);
	}
	
	@SuppressWarnings("unchecked")
	public void who(JSONObject jsWho){
		JSONObject sTocMsg = new JSONObject();
		sTocMsg.put("type", "roomcontents");
		sTocMsg.put("roomid", theRoom.getRoomid());
		ArrayList<String> identities = new ArrayList<String>();
		for(int i = 0;i < theRoom.getRoom_clients().size();i++)//get clientid of the same room
			identities.add(theRoom.getRoom_clients().get(i).getIdentity());
		sTocMsg.put("identities", identities);
		sTocMsg.put("owner", theRoom.getOwner());
		String sTocJSONText = sTocMsg.toJSONString();
		sendMsg(sTocJSONText);
	}
	
	@SuppressWarnings("unchecked")
	public void createroom(JSONObject jsCR){
		String newRoomid = (String) jsCR.get("roomid");
		if(!Pattern.matches(IdRegex,newRoomid)){//wrong name
			JSONObject sTocMsg = new JSONObject();
			sTocMsg.put("type", "createroom");
			sTocMsg.put("roomid", newRoomid);
			sTocMsg.put("approved", "false");
			String sTocJSONText = sTocMsg.toJSONString();
			sendMsg(sTocJSONText);
			//send approved false msg to the client
		}
		else{
			if(theServer.IfLockRoom(newRoomid)||theServer.IfExistRoom(newRoomid)||theRoom.getOwner().equals(clientid)){
				//exist in the server or locked by any one server
				JSONObject sTocMsg = new JSONObject();
				sTocMsg.put("type", "createroom");
				sTocMsg.put("roomid", newRoomid);
				sTocMsg.put("approved", "false");
				String sTocJSONText = sTocMsg.toJSONString();
				sendMsg(sTocJSONText);
				//send approved false msg to the client
			}
			else{
				int serverNum = theServer.getServerNumber();
				int pass = 0;
				if(serverNum>1){
					theServer.lockRoom(newRoomid);
					JSONObject voteRoomIdMsg = new JSONObject();
					voteRoomIdMsg.put("type", "lockroomid");
					voteRoomIdMsg.put("serverid",theServer.getServerid());
					voteRoomIdMsg.put("roomid",newRoomid);
					String voterJSONText = voteRoomIdMsg.toJSONString();
					theServer.addVoter(newRoomid);
					//add a vote counter for this roomid
					theServer.broadcast(voterJSONText);
					//broadcast for voting
				}
				else{
					pass = 1;
				}
				while(pass == 0)//still voting?
					pass = theServer.queryVoter(newRoomid);
				if(pass == 1){//all true
					JSONObject sTocMsg = new JSONObject();
					sTocMsg.put("type", "createroom");
					sTocMsg.put("roomid", newRoomid);
					sTocMsg.put("approved", "true");
					String sTocJSONText = sTocMsg.toJSONString();
					sendMsg(sTocJSONText);
					//send approved true msg to the client
					JSONObject roomchangeMsg = new JSONObject();//roomchange msg
					roomchangeMsg.put("type", "roomchange");
					roomchangeMsg.put("identity",clientid);
					roomchangeMsg.put("former",theRoom.getRoomid());
					roomchangeMsg.put("roomid",newRoomid);
					String roomchangeJSONText = roomchangeMsg.toJSONString();
					theRoom.removeConnections(this);
					theServer.broadcastRoom(roomchangeJSONText,theRoom);
					//broadcastRoom roomchange msg
					sendMsg(roomchangeJSONText);
					//send roomchange to the client
					//the client received  the roomchange msg two times
					ClientBoard tmpcb = null;
					tmpcb = theRoom.getClientInRoom(clientid);
					theRoom.deleteClient(clientid);
					//remove this client from former room 
					Rooms aNewRoom = new Rooms(newRoomid,this.clientid,theServer.getServerid());
					this.theRoom = aNewRoom;
					theRoom.addConnections(this);
					theRoom.setOwner(clientid);
					tmpcb.setRoomid(theRoom.getRoomid());
					//update the client info
					theRoom.getRoom_clients().add(tmpcb);
					//add this client  to the current room
					theServer.getServer_rooms().add(theRoom);
					//add the room to this server roomlist 
					Vector<String> aNewRoomInfo = new Vector<String>();
					aNewRoomInfo.add(theRoom.getRoomid());
					aNewRoomInfo.add(theRoom.getServerid());
					theServer.addToAllRoomList(aNewRoomInfo);
					//add the room to this server  all_roomslist
					theServer.unlockRoom(newRoomid);
					//remove roomid from the lockroomlist
					JSONObject releaseRoomIdMsg = new JSONObject();//release msg
					releaseRoomIdMsg.put("type", "releaseroomid");
					releaseRoomIdMsg.put("serverid",theServer.getServerid());
					releaseRoomIdMsg.put("roomid",newRoomid);
					releaseRoomIdMsg.put("approved","true");
					String releaserJSONText = releaseRoomIdMsg.toJSONString();
					theServer.broadcast(releaserJSONText);//broadcast release msg
				}
				else{//some false
					JSONObject sTocMsg = new JSONObject();
					sTocMsg.put("type", "createroom");
					sTocMsg.put("roomid", newRoomid);
					sTocMsg.put("approved", "false");
					String sTocJSONText = sTocMsg.toJSONString();
					sendMsg(sTocJSONText);
					theServer.unlockRoom(newRoomid);
					//remove roomid from the lockroomlist
					JSONObject releaseRoomIdMsg = new JSONObject();//release msg
					releaseRoomIdMsg.put("type", "releaseroomid");
					releaseRoomIdMsg.put("serverid",theServer.getServerid());
					releaseRoomIdMsg.put("roomid",newRoomid);
					releaseRoomIdMsg.put("approved","false");
					String releaserJSONText = releaseRoomIdMsg.toJSONString();
					theServer.broadcast(releaserJSONText);//broadcast release msg
				}
				theServer.removeVoter(newRoomid);
				//remove the roomid voteCounter
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void join(JSONObject jsJoin){
		String joinRoomid = (String) jsJoin.get("roomid");
		if(!theServer.IfExistRoom(joinRoomid)||theRoom.getOwner().equals(clientid)){
			//if it is a non-existent room, or owning this room
			JSONObject sTocMsg = new JSONObject();
			sTocMsg.put("type", "roomchange");
			sTocMsg.put("identity", clientid);
			sTocMsg.put("former", joinRoomid);
			sTocMsg.put("roomid", joinRoomid);
			String sTocJSONText = sTocMsg.toJSONString();
			sendMsg(sTocJSONText);
			//send the join_failed msg to the client
		}
		else{
			if(!theServer.IfRoomInServer(joinRoomid)){//in another server
				Vector<String> aJoinServerInfo = new Vector<String>();
				aJoinServerInfo = theServer.getDiffServerr(joinRoomid);
				String newServerHost = aJoinServerInfo.get(0);
				String newServerPort = aJoinServerInfo.get(1);
				JSONObject sTocMsg = new JSONObject();
				sTocMsg.put("type", "route");
				sTocMsg.put("roomid", joinRoomid);
				sTocMsg.put("username", this.loginUsername);
				sTocMsg.put("host",newServerHost);
				sTocMsg.put("port", newServerPort);
				String sTocJSONText = sTocMsg.toJSONString();
				sendMsg(sTocJSONText);
				//send the newServerInfo to the client (route)
				theRoom.removeConnections(this);
				theRoom.deleteClient(clientid);
				//remove this client from former room
				theServer.deleteClient(clientid);
				//remove this client from this server
				JSONObject sTocMsgFormer = new JSONObject();
				sTocMsgFormer.put("type", "roomchange");
				sTocMsgFormer.put("identity", clientid);
				sTocMsgFormer.put("former", theRoom.getRoomid());
				sTocMsgFormer.put("roomid", joinRoomid);
				String sTocJSONTextFormer = sTocMsgFormer.toJSONString();
				theServer.broadcastRoom(sTocJSONTextFormer,theRoom);
				//broadcast to former room
			}
			else{//in the same server
				JSONObject sTocMsgFormer = new JSONObject();
				sTocMsgFormer.put("type", "roomchange");
				sTocMsgFormer.put("identity", clientid);
				sTocMsgFormer.put("former", theRoom.getRoomid());
				sTocMsgFormer.put("roomid", joinRoomid);
				String sTocJSONTextFormer = sTocMsgFormer.toJSONString();
				theRoom.removeConnections(this);
				theServer.broadcastRoom(sTocJSONTextFormer, theRoom);
				//broadcast the roomchange msg to former room
				ClientBoard tmpcb = null;
				tmpcb = theRoom.getClientInRoom(clientid);
				theRoom.deleteClient(clientid);
				//remove this client from former room
				this.theRoom = theServer.getRoomInServer(joinRoomid);
				theRoom.addConnections(this);
				tmpcb.setRoomid(theRoom.getRoomid());
				//update the client info
				theRoom.addClient(tmpcb);
				//the client join the new room
				theServer.broadcastRoom(sTocJSONTextFormer, theRoom);
				//broadcast to new room
				//sendMsg(sTocJSONTextFormer);
				//send roomchange to the client
				//the client receive the roomchange msg two times
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void movejoin(JSONObject jsMJ){
		String formerRoomid = (String) jsMJ.get("former");
		String joinRoomid = (String) jsMJ.get("roomid");
		String clientid = (String) jsMJ.get("identity");
		String username = (String) jsMJ.get("username");
		this.clientid = clientid;
		this.loginUsername = username;
		System.out.println("***********"+this.loginUsername);
		if(theServer.IfRoomInServer(joinRoomid))
			theRoom = theServer.getRoomInServer(joinRoomid);
		else{//if the room is deleted, then move the client to the MainHall
			theRoom = theServer.getMainhall();
			joinRoomid = theRoom.getRoomid();
		}
			//set client and server attributes of this connection
			ClientBoard aClientBoard = new ClientBoard(clientid,joinRoomid,theServer.getServerid());
			
			theRoom.addClient(aClientBoard);
			//add the client to the room
			theServer.addServer_Clients(aClientBoard);
			//add the client to the server
			JSONObject sTocMsgr = new JSONObject();
			sTocMsgr.put("type", "roomchange");
			sTocMsgr.put("identity", clientid);
			sTocMsgr.put("former", formerRoomid);
			sTocMsgr.put("roomid", joinRoomid);
			String sTocrJSONText = sTocMsgr.toJSONString();
			//broad roomchange to all the members of joinRoom
			JSONObject sTocMsg = new JSONObject();
			sTocMsg.put("type", "serverchange");
			sTocMsg.put("approved", "true");
			sTocMsg.put("serverid", theServer.getServerid());
			String sTocJSONText = sTocMsg.toJSONString();
			sendMsg(sTocJSONText);
			// update S0's US map.
			JSONObject updateUSMap = new JSONObject();
			updateUSMap.put("type", "mapUS");
			updateUSMap.put("username", this.loginUsername);
			updateUSMap.put("serverId", theServer.getServerid());
			theServer.sendMsgToS0(updateUSMap.toJSONString());
			theRoom.addConnections(this);
			theServer.broadcastRoom(sTocrJSONText, theRoom);
	}
	
	public void closeConnection(){
		if(in!=null)
			try { 
				in.close();
			} catch (IOException e){
			}
		if(out!=null)
			try { 
				out.close();
			} catch (IOException e){
			}
		if(clientSocket!=null)
			try { 
				clientSocket.close();
			} catch (IOException e){
			}
		//close this connection
	}
	
	@SuppressWarnings("unchecked")
	public void deleteroom(JSONObject jsDR){
		String deleteRoomid = (String) jsDR.get("roomid");
		if(!(theRoom.getOwner().equals(clientid)&&theRoom.getRoomid().equals(deleteRoomid))){
			//this client is not the owner of the room(and correct roomid)
			JSONObject sTocMsg = new JSONObject();
			sTocMsg.put("type", "deleteroom");
			sTocMsg.put("roomid", deleteRoomid);
			sTocMsg.put("approved", "false");
			String sTocJSONText = sTocMsg.toJSONString();
			sendMsg(sTocJSONText);
			//send approved false msg to the client
		}
		else{//this client is the owner of the room
			JSONObject sTocMsgapp = new JSONObject();
			sTocMsgapp.put("type", "deleteroom");
			sTocMsgapp.put("roomid", deleteRoomid);
			sTocMsgapp.put("approved", "true");
			String sTocappJSONText = sTocMsgapp.toJSONString();
			theServer.deleteRoom(deleteRoomid);
			theServer.deleteRoomi(deleteRoomid);//delete the room record in this server
			JSONObject deleteRoomMsg = new JSONObject();
			deleteRoomMsg.put("type", "deleteroom");
			deleteRoomMsg.put("serverid", theServer.getServerid());
			deleteRoomMsg.put("roomid", deleteRoomid);
			String deleteRoomJSONText = deleteRoomMsg.toJSONString();
			theServer.broadcast(deleteRoomJSONText);
			//broadcast deleteRoom msg to all other servers
			//to remove the deleteRoom from all other servers' allroom_list
			Rooms tmpDeleteRoom = theRoom;
			for(int i = 0;i<tmpDeleteRoom.getRoom_connections().size();i++){
				if(tmpDeleteRoom.getRoom_clients().get(i).getIdentity().equals(clientid))
					continue;
				JSONObject sTocdMsg = new JSONObject();
				sTocdMsg.put("type", "roomchange");
				sTocdMsg.put("identity", tmpDeleteRoom.getRoom_connections().get(i).getClientid());
				sTocdMsg.put("former", deleteRoomid);
				sTocdMsg.put("roomid", theServer.getMainhall().getRoomid());
				String sTocdJSONText = sTocdMsg.toJSONString();
				theServer.broadcastRoom(sTocdJSONText, tmpDeleteRoom);
				
				String tmpClientId = tmpDeleteRoom.getRoom_connections().get(i).getClientid();
				theServer.getMainhall().addClient(tmpDeleteRoom.getRoom_clients().get(i));
				theServer.getMainhall().addConnections(tmpDeleteRoom.getRoom_connections().get(i));
				
				//add the connection to the destination room
				tmpDeleteRoom.getRoom_connections().get(i).setTheRoom(theServer.getMainhall());
				//set(replace) the destination room as theRoom of the connection
				tmpDeleteRoom.removeClient(tmpDeleteRoom.getRoom_clients().get(i));//getRoom_clients().remove(i);
				tmpDeleteRoom.removeConnections(tmpDeleteRoom.getRoom_connections().get(i));
				//remove the connection from the source room
				theServer.broadcastRoomE(sTocdJSONText, theServer.getMainhall(),tmpClientId);
				i--;
				//all clients except the owner of the deleted room
			}
			JSONObject sTocdMsg = new JSONObject();
			sTocdMsg.put("type", "roomchange");
			sTocdMsg.put("identity", tmpDeleteRoom.getRoom_connections().get(0).getClientid());
			sTocdMsg.put("former", deleteRoomid);
			sTocdMsg.put("roomid", theServer.getMainhall().getRoomid());
			String sTocdJSONText = sTocdMsg.toJSONString();
			theServer.broadcastRoom(sTocdJSONText, tmpDeleteRoom);
			String tmpClientId = tmpDeleteRoom.getRoom_connections().get(0).getClientid();
			theServer.getMainhall().addClient(tmpDeleteRoom.getRoom_clients().get(0));
			theServer.getMainhall().addConnections(tmpDeleteRoom.getRoom_connections().get(0));
			//add the connection to the destination room
			tmpDeleteRoom.getRoom_connections().get(0).setTheRoom(theServer.getMainhall());
			//set(replace) the destination room as theRoom of the connection
			tmpDeleteRoom.removeClient(tmpDeleteRoom.getRoom_clients().get(0));
			tmpDeleteRoom.removeConnections(tmpDeleteRoom.getRoom_connections().get(0));
			//remove the connection from the source room
			theServer.broadcastRoomE(sTocdJSONText, theServer.getMainhall(),tmpClientId);
			//the owner
			//method ing
			//broadcast the roomchange of per client of deleted room to all clients of deleted room
			tmpDeleteRoom.getRoom_clients().clear();
			tmpDeleteRoom.getRoom_connections().clear();
			tmpDeleteRoom = null;
			sendMsg(sTocappJSONText);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void releaseUsername(String username){
		JSONObject releaseUser = new JSONObject();
		releaseUser.put("type","releaseusername");
		releaseUser.put("username", username);
		
		String address = null;
		int serverPort = 0;
		for(int i = 0;i < this.theServer.getServer_Servers().size();i++){
		    if(this.theServer.getServer_Servers().get(i).get(0).equals("s0")){
		    	address = this.theServer.getServer_Servers().get(i).get(1);
		    	serverPort = Integer.parseInt(this.theServer.getServer_Servers().get(i).get(3));
		    	break;
		    }
		}
		 // send message to S0 to release the username when quit
		SSendToS sendToS0 = new SSendToS(address,
		serverPort, releaseUser.toJSONString());
		sendToS0.start();
		
	}
	
	@SuppressWarnings("unchecked")
	public void message(JSONObject jsMSG){
		String content = (String) jsMSG.get("content");
		JSONObject sTocMsg = new JSONObject();
		sTocMsg.put("type", "message");
		sTocMsg.put("identity", clientid);
		sTocMsg.put("content", content);
		String sTocJSONText = sTocMsg.toJSONString();
		theServer.broadcastRoomE(sTocJSONText, theRoom, clientid);
	}
	
	@SuppressWarnings("unchecked")
	public void quit(JSONObject jsQuit,int ioe){
		if(!theRoom.getOwner().equals(clientid)){//this client is not the owner of the room
			System.out.println("not this room owner!");
			theServer.deleteClient(clientid);
			theRoom.deleteClient(clientid);
			theRoom.getRoom_connections().remove(this);
			//remove the client from the server_clientlist and room_clientlist
			JSONObject sTocMsg = new JSONObject();
			sTocMsg.put("type", "roomchange");
			sTocMsg.put("identity", clientid);
			sTocMsg.put("former", theRoom.getRoomid());
			sTocMsg.put("roomid", "");
			String sTocJSONText = sTocMsg.toJSONString();
			if(ioe==0)
				theServer.broadcastRoom(sTocJSONText, theRoom);
			else
				theServer.broadcastRoomE(sTocJSONText, theRoom, clientid);
			//broadcast roomchange msg of this client to all clients in the room
		}
		else{//this client is the owner of the room
			JSONObject sTocMsgapp = new JSONObject();
			sTocMsgapp.put("type", "deleteroom");
			sTocMsgapp.put("roomid", theRoom.getRoomid());
			sTocMsgapp.put("approved", "true");
			String sTocappJSONText = sTocMsgapp.toJSONString();
			String deleteRoomid = theRoom.getRoomid();
			theServer.deleteRoom(deleteRoomid);
			theServer.deleteRoomi(deleteRoomid);//delete the room record in this server
			JSONObject deleteRoomMsg = new JSONObject();
			deleteRoomMsg.put("type", "deleteroom");
			deleteRoomMsg.put("serverid", theServer.getServerid());
			deleteRoomMsg.put("roomid", deleteRoomid);
			String deleteRoomJSONText = deleteRoomMsg.toJSONString();
			theServer.broadcast(deleteRoomJSONText);
			//broadcast delete msg to servers
			theServer.deleteClient(clientid);
			//remove the client from the server_clientlist
			Rooms tmpDeleteRoom = theRoom;
			JSONObject sTocMsg = new JSONObject();
			sTocMsg.put("type", "roomchange");
			sTocMsg.put("identity", clientid);
			sTocMsg.put("former", theRoom.getRoomid());
			sTocMsg.put("roomid", "");
			String sTocJSONText = sTocMsg.toJSONString();
			for(int i = 0;i<tmpDeleteRoom.getRoom_connections().size();i++){
				if(tmpDeleteRoom.getRoom_clients().get(i).getIdentity().equals(clientid))
					continue;
				JSONObject sTocdMsg = new JSONObject();
				sTocdMsg.put("type", "roomchange");
				sTocdMsg.put("identity", tmpDeleteRoom.getRoom_connections().get(i).getClientid());
				sTocdMsg.put("former", deleteRoomid);
				sTocdMsg.put("roomid", theServer.getMainhall().getRoomid());
				String sTocdJSONText = sTocdMsg.toJSONString();
				String tmpClientId = tmpDeleteRoom.getRoom_connections().get(i).getClientid();
				if(ioe==1){
					tmpDeleteRoom.removeConnections(this);
					theServer.broadcastRoom(sTocdJSONText, tmpDeleteRoom);
					tmpDeleteRoom.addPosConnections(this,0);
				}
				else
					theServer.broadcastRoom(sTocdJSONText, tmpDeleteRoom);
				theServer.getMainhall().addClient(tmpDeleteRoom.getRoom_clients().get(i));
				theServer.getMainhall().addConnections(tmpDeleteRoom.getRoom_connections().get(i));
				//add the connection to the destination room
				tmpDeleteRoom.getRoom_connections().get(i).setTheRoom(theServer.getMainhall());
				//set(replace) the destination room as theRoom of the connection
				tmpDeleteRoom.removeClient(tmpDeleteRoom.getRoom_clients().get(i));//getRoom_clients().remove(i);
				tmpDeleteRoom.removeConnections(tmpDeleteRoom.getRoom_connections().get(i));
				//remove the connection from the source room
				if(theServer.getMainhall().getRoom_clients().size()!=1)
					theServer.broadcastRoomE(sTocdJSONText, theServer.getMainhall(),tmpClientId);
				i--;
				//all clients except the owner of the deleted room
			}
			tmpDeleteRoom.removeClient(tmpDeleteRoom.getRoom_clients().get(0));
			tmpDeleteRoom.removeConnections(tmpDeleteRoom.getRoom_connections().get(0));
			theServer.broadcastRoomE(sTocJSONText, theServer.getMainhall(),clientid);
			//the owner
			// method ing
			tmpDeleteRoom.getRoom_clients().clear();
			tmpDeleteRoom.getRoom_connections().clear();
			tmpDeleteRoom = null;
			if(ioe==0)
				sendMsg(sTocappJSONText);
			//remove all clients from the deleted room
		}//delete the room if its owner is this client
		JSONObject sTocMsge = new JSONObject();
		sTocMsge.put("type", "roomchange");
		sTocMsge.put("identity", clientid);
		sTocMsge.put("former", theRoom.getRoomid());
		sTocMsge.put("roomid", "");
		String sToceJSONText = sTocMsge.toJSONString();
		if(ioe==0){
			sendMsg(sToceJSONText);
		}
		else;
		System.out.println("ioe = "+ioe);
		releaseUsername(this.loginUsername);
		closeConnection();
		//send roomchange to the client and close this connection
	}
}//this is all the client connection
