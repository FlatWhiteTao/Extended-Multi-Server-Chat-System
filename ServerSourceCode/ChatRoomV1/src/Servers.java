import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONObject;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */


public class Servers {
	
	private String serverid;
	private int clientport;
	private int serverport;
	private String hostname;
	private Rooms Mainhall;
	private Vector<ClientBoard> server_clients = new Vector<ClientBoard>();//store clients in this server
	private Vector<Rooms> server_rooms = new Vector<Rooms>();//store rooms in this server
	private Vector<Vector<String>> server_allrooms = new Vector<Vector<String>>(); // store rooms info sin all servers
	private Vector<Vector<String>> server_Servers = new Vector<Vector<String>>();//store all servers info
	private Vector<String> server_lockclients = new Vector<String>();//store loc identities
	private Vector<String> server_lockrooms = new Vector<String>();//store loc rooms
	private Vector<VoteCounter> votecCounters = new Vector<VoteCounter>();//a vote counter of clientid
	private Vector<VoteCounter> voterCounters = new Vector<VoteCounter>();//a vote counter of roomid
	private Vector<UserDB> server_UserDB = new Vector<UserDB>();// userDb only s0 store it
	private HashMap<String,Integer> userVerify = new HashMap<String,Integer>();
	private HashMap<String,String> mapUS = new HashMap<String,String>();
	private int running;
	 
	
	
	public HashMap<String, String> getMapUS() {
		return mapUS;
	}

	public synchronized void setMapUS(String username, String serverId) {
		this.mapUS.put(username, serverId);
	}

	public HashMap<String, Integer> getUserVerify() {
		return userVerify;
	}
	
	public String getServerid() {
		return serverid;
	}
	public Vector<UserDB> getServer_UserDB() {
		return server_UserDB;
	}
	public void setServer_UserDB(Vector<UserDB> server_UserDB) {
		this.server_UserDB = server_UserDB;
	}
	public void setServerid(String serverid) {
		this.serverid = serverid;
	}
	public int getClientport() {
		return clientport;
	}
	public void setClientport(int clientport) {
		this.clientport = clientport;
	}
	public int getServerport() {
		return serverport;
	}
	public void setServerport(int serverport) {
		this.serverport = serverport;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Rooms getMainhall() {
		return Mainhall;
	}
	public void setMainhall(Rooms mainhall) {
		Mainhall = mainhall;
	}
	public Vector<ClientBoard> getServer_clients() {
		return server_clients;
	}
	public void setServer_clients(Vector<ClientBoard> server_clients) {
		this.server_clients = server_clients;
	}
	public void setVotecCounters(Vector<VoteCounter> votecCounters) {
		this.votecCounters = votecCounters;
	}
	public void setVoterCounters(Vector<VoteCounter> voterCounters) {
		this.voterCounters = voterCounters;
	}
	public Vector<Rooms> getServer_rooms() {
		return server_rooms;
	}
	public void setServer_rooms(Vector<Rooms> server_rooms) {
		this.server_rooms = server_rooms;
	}
	public Vector<Vector<String>> getServer_allrooms() {
		return server_allrooms;
	}
	public void setServer_allrooms(Vector<Vector<String>> server_allrooms) {
		this.server_allrooms = server_allrooms;
	}
	public Vector<String> getServer_lockclients() {
		return server_lockclients;
	}
	public void setServer_lockclients(Vector<String> server_lockclients) {
		this.server_lockclients = server_lockclients;
	}
	public Vector<String> getServer_lockrooms() {
		return server_lockrooms;
	}
	public void setServer_lockrooms(Vector<String> server_lockrooms) {
		this.server_lockrooms = server_lockrooms;
	}
	public Vector<Vector<String>> getServer_Servers() {
		return server_Servers;
	}
	public void setServer_Servers(Vector<Vector<String>> server_Servers) {
		this.server_Servers = server_Servers;
	}
	public Vector<VoteCounter> getVotecCounters() {
		return votecCounters;
	}
	public void setVotecCounter(Vector<VoteCounter> votecCounters) {
		this.votecCounters = votecCounters;
	}
	public Vector<VoteCounter> getVoterCounters() {
		return voterCounters;
	}
	public void setVoterCounter(Vector<VoteCounter> voterCounters) {
		this.voterCounters = voterCounters;
	}
	public Servers(String serverid, String hostname, int clientport, int serverport) {//constructor
		try {
		this.serverid = serverid;
		this.clientport = clientport;
		this.serverport = serverport;
		this.hostname = hostname;
		Mainhall = new Rooms("MainHall-".concat(serverid),"",serverid);
		running = 0;
		server_rooms.add(Mainhall);//Mainhall should be in server_rooms list too
		//fserver_start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getRunning() {
		return running;
	}

	public void setRunning(int running) {
		this.running = running;
	}

	

	

	public synchronized void removeVotec(String clientid){//remove the vote counter of clientid
		for(int i = 0;i < votecCounters.size();i++)
			if(votecCounters.get(i).getVoteId().equals(clientid))
				votecCounters.remove(i);
	}
	
	public synchronized void removeVoter(String roomid){//remove the vote counter of roomid
		for(int i = 0;i < voterCounters.size();i++)
			if(voterCounters.get(i).getVoteId().equals(roomid))
				voterCounters.remove(i);
	}
	
	
	public synchronized void addVotec(String clientid){
		VoteCounter avotecCounter = new VoteCounter(clientid,server_Servers.size());
		this.votecCounters.add(avotecCounter);//add the vote counter of the clientid
	}
	
	public synchronized void addVoter(String roomid){
		VoteCounter avoterCounter = new VoteCounter(roomid,server_Servers.size());
		this.voterCounters.add(avoterCounter);//add the vote counter of the roomid
	}
	
	public synchronized int queryVotec(String clientid){
		for(int i = 0;i < votecCounters.size();i++){
			//find the vote counter of the clientid and get the state
			if(votecCounters.get(i).getVoteId().equals(clientid))
				if(votecCounters.get(i).getCurrentNumber()==votecCounters.get(i).getTotalNumber())
					if(votecCounters.get(i).getPass().equals("true"))
						return 1;//all true
					else
						return 2;//some false
		}
		return 0;//still voting
	}
	
	public synchronized int queryVoter(String roomid){
		for(int i = 0;i < voterCounters.size();i++){
			//find the vote counter of the roomid and get the state
			if(voterCounters.get(i).getVoteId().equals(roomid))
				if(voterCounters.get(i).getCurrentNumber()==voterCounters.get(i).getTotalNumber())	
					if(voterCounters.get(i).getPass().equals("true"))
						return 1;//all true
					else
						return 2;//some false
		}
		return 0;//still voting
	}
	
	public synchronized int votecCount( String clientid, String pass){
		for(int i = 0;i < votecCounters.size();i++)//find the vote counter of the clientid
			if(votecCounters.get(i).getVoteId().equals(clientid)){
				votecCounters.get(i).setCurrentNumber(votecCounters.get(i).getCurrentNumber()+1);
				if(pass.equals("false")){//only if "locked" is false, the vote counter records false
					votecCounters.get(i).setPass(pass);
					return 2;
				}
				if(votecCounters.get(i).getCurrentNumber()==votecCounters.get(i).getTotalNumber())
					if(votecCounters.get(i).getPass().equals("true")){//all true
						return 1;
					}
			}
		return 0;//or still voting
	}
	
	public synchronized int voterCount( String roomid, String pass){
		for(int i = 0;i < voterCounters.size();i++)//find the vote counter of the roomid
			if(voterCounters.get(i).getVoteId().equals(roomid)){
				voterCounters.get(i).setCurrentNumber(voterCounters.get(i).getCurrentNumber()+1);
				if(pass.equals("false")){//only if "locked" is false, the vote counter records false
					voterCounters.get(i).setPass(pass);
					return 2;
				}
				if(voterCounters.get(i).getCurrentNumber()==voterCounters.get(i).getTotalNumber())
					if(voterCounters.get(i).getPass().equals("true"))//all true
						return 1;
					else//some false
						return 2;
			}
		return 0;//or still voting
	}
	
	public void broadcast(String JSONText){//broadcast to all other servers
		for(int i = 0;i < server_Servers.size();i++){
			if(server_Servers.get(i).get(0).equals(serverid))
				continue;
			/*if(server_Servers.get(i).get(0).equals("s0"))
				continue;*/
			else{
				SSendToS aServerSendToServers = new SSendToS(server_Servers.get(i).get(1),
						Integer.parseInt(server_Servers.get(i).get(3)), JSONText);
				aServerSendToServers.start();
			}
		}
	}
	
	public void broadcastNOs0(String JSONText){//broadcast to all other servers
		for(int i = 0;i < server_Servers.size();i++){
			if(server_Servers.get(i).get(0).equals(serverid))
				continue;
			if(server_Servers.get(i).get(0).equals("s0"))
				continue;
			else{
				SSendToS aServerSendToServers = new SSendToS(server_Servers.get(i).get(1),
						Integer.parseInt(server_Servers.get(i).get(3)), JSONText);
				aServerSendToServers.start();
			}
		}
	}
	
	public void broadcastRoom(String JSONText, Rooms room){//broadcast to all clients in a room
		for(int i = 0;i < room.getRoom_connections().size();i++)
			try {
				room.getRoom_connections().get(i).sendMsg(JSONText);
			} catch (Exception e){
				//e.printStackTrace();
			}
	}
	
	public void broadcastRoomE(String JSONText, Rooms room, String clientid){//broadcast to all clients in a room
		for(int i = 0;i < room.getRoom_connections().size();i++)
			try {
				if(room.getRoom_connections().get(i).getClientid().equals(clientid))
					continue;
				room.getRoom_connections().get(i).sendMsg(JSONText);
			} catch (Exception e){
				//e.printStackTrace();
			}
	}
	
	public synchronized void lockClient(String clientid){
		server_lockclients.add(clientid);
	}
	
	public synchronized void lockRoom(String roomid){
		server_lockrooms.add(roomid);
	}
	
	public synchronized void unlockClient(String clientid){
		System.out.println("~~~~~"
				+ " identity-"+clientid+"- from server-"+this.serverid+"-");
		server_lockclients.remove(clientid);
	}
	
	public synchronized void unlockRoom(String roomid){
		server_lockrooms.remove(roomid);
	}
	
	public synchronized void addServer_Clients(ClientBoard aNewClient){
		server_clients.add(aNewClient);
		//add the clientid to this server clientlist
	}
	public void server_startnr() {
		try {
		HeartBeat heartbeat = new HeartBeat(this);
		ListenClientThread ListenClientThread = new ListenClientThread(this.getClientport(),this);
		ListenClientThread.start();
		//creat a thread for listening client
		ListenServerThread ListenServerThread = new ListenServerThread(this.getServerport(),this);
		ListenServerThread.start();
		//creat a thread for listening server
		}catch (Exception e) {
			//e.printStackTrace();
		}
	}
	public void server_start() {
		try {
		//HeartBeat heartbeat = new HeartBeat(this);
		ListenClientThread ListenClientThread = new ListenClientThread(this.getClientport(),this);
		ListenClientThread.start();
		//creat a thread for listening client
		ListenServerThread ListenServerThread = new ListenServerThread(this.getServerport(),this);
		ListenServerThread.start();
		JSONObject ifstartMsg = new JSONObject();
		ifstartMsg.put("type", "ifstart");
		ifstartMsg.put("ifserverid",serverid);
		String ifstartJSONText = ifstartMsg .toJSONString();
		String s0hostname = null;
		String s0serverport = null;
		System.out.println("size: "+server_Servers.size());
		for(int i = 0;i<server_Servers.size();i++){
			if(server_Servers.get(i).get(0).equals("s0")){
				s0hostname = server_Servers.get(i).get(1);
				s0serverport = server_Servers.get(i).get(3);
				break;
			}
		}
		if(!this.serverid.equals("s0")){
			System.out.println("s0hostname = "+s0hostname+" s0serverport"+s0serverport+"ifstartJSONText = "+ifstartJSONText);
			SSendToS sendToS0 = new SSendToS(s0hostname, Integer.parseInt(s0serverport), ifstartJSONText);
			sendToS0.start();
		}
		//creat a thread for listening server
		}catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void startHB(){
		HeartBeat heartbeat = new HeartBeat(this);
	}
	
	public synchronized boolean IfLockClient(String clientid){
		for(int i = 0;i<this.getServer_lockclients().size();i++)
			if(server_lockclients.get(i).equals(clientid))
				return true;
		return false;
	}//judge if clientid in the clientid_locklist
	
	public synchronized boolean IfExistClient(String clientid){
		for(int i = 0;i < server_clients.size();i++)
			if(server_clients.get(i).getIdentity().equals(clientid))
				return true;
		return false;
	}//judge if clientid in the clientid_list
	
	public synchronized boolean IfLockRoom(String roomid){
		for(int i = 0;i<this.getServer_lockrooms().size();i++)
			if(server_lockrooms.get(i).equals(roomid))
				return true;
		return false;
	}//judge if roomid in the roomid_locklist
	
	public synchronized boolean IfExistRoom(String roomid){
		for(int i = 0;i < server_allrooms.size();i++)
			if(server_allrooms.get(i).get(0).equals(roomid))
				return true;
		return false;
	}//judge if roomid in the roomid_list
	
	public synchronized  boolean IfRoomInServer(String roomid){
		for(int i = 0;i < server_rooms.size();i++){
			if(server_rooms.get(i).getRoomid().equals(roomid))
				return true;
		}
		return false;
	}
	
	public synchronized Rooms getRoomInServer(String roomid){
		for(int i = 0;i < server_rooms.size();i++)
			if(server_rooms.get(i).getRoomid().equals(roomid))
				return server_rooms.get(i);
		return null;
	}//get the room via roomid
	
	public synchronized Vector<String> getDiffServerr(String roomid){
		String newServerClientPort = null;
		String newServerHost = null;
		String newServerid = null;
		for(int i = 0;i<server_allrooms.size();i++)
			if(server_allrooms.get(i).get(0).equals(roomid)){
				newServerid = server_allrooms.get(i).get(1);
				break;
			}
		for(int j = 0;j<server_Servers.size();j++)
			if(server_Servers.get(j).get(0).equals(newServerid)){
				newServerHost = server_Servers.get(j).get(1);
				newServerClientPort = server_Servers.get(j).get(2);
				break;
			}
		Vector<String> aJoinServerInfo = new Vector<String>();
		aJoinServerInfo.add(newServerHost);
		aJoinServerInfo.add(newServerClientPort);
		return aJoinServerInfo;
	}//get server info which the room is in via roomid
	
	public synchronized void deleteRoomi(String roomid){
		for(int i = 0;i<server_allrooms.size();i++)
			if(server_allrooms.get(i).get(0).equals(roomid)){
				server_allrooms.remove(i);
				break;
			}
	}//remove the room info from allroom_list via roomid
	
	public synchronized void deleteRoom(String roomid){
		for(int i = 0;i<server_rooms.size();i++)
			if(server_rooms.get(i).getRoomid().equals(roomid)){
				server_rooms.remove(i);
				break;
			}
	}//remove the room from room_list via roomid
	
	public synchronized void deleteClient(String clientid){
		for(int i = 0;i<server_clients.size();i++)
			if(server_clients.get(i).getIdentity().equals(clientid)){
				server_clients.remove(i);
				break;
			}
	}//remove the clientBoard(info) from clientlist via clientid
	
	public int getServerNumber(){
		int i = server_Servers.size();
		return i;
	}
	public int getAllRoomsNum(){
		int i = this.getServer_allrooms().size();
		return i;
	}
	
	public synchronized void addToAllRoomList(Vector<String> aNewRoom){
		this.getServer_allrooms().add(aNewRoom);
	}
	public void processCrash(String serverId){
		System.out.println("handling server crash!!");
		
		// remove the that server in the this server info list
		for(int i=0;i<this.server_Servers.size();i++){
			if(this.server_Servers.get(i).get(0).equals(serverId)){
				this.server_Servers.remove(i);
				break;
			}
		}
		// remove the rooms that belong to crash server from the global room list
		
		for(int j=0;j< this.server_allrooms.size(); j++){
			System.out.println(this.server_allrooms.get(j).get(1)+"allroomlist1");
		}
		
		
		System.out.println("this.server_allrooms.size = "+this.server_allrooms.size());
		for(int j=0;j< this.server_allrooms.size(); j++){
			if(this.server_allrooms.get(j).get(1).equals(serverId)){
				System.out.println("this room would be removed: "+this.server_allrooms.get(j).get(0));
				this.server_allrooms.remove(j);
				j--;
			}
		}
		
		for(int j=0;j< this.server_allrooms.size(); j++){
			System.out.println(this.server_allrooms.get(j).get(1)+"allroomlist2");
		}
		
		List<String> keyFilter = new ArrayList<String>();
		if (this.getServerid().equals("s0")){
			Set keySet = this.getMapUS().entrySet();
			
			Iterator iter = keySet.iterator();
			while (iter.hasNext()){
				Map.Entry entry = (Map.Entry)iter.next();
				String key =  (String)entry.getKey();
				if(this.getMapUS().get(key).equals(serverId)){
					keyFilter.add(key);
				}
			}
			
		    
			for(int m=0; m<this.server_UserDB.size();m++){
		    	for(String username: keyFilter){
		    		if(this.server_UserDB.get(m).getUsername().equals(username))
		    			{this.server_UserDB.get(m).setOnline(false);
		    			 this.mapUS.put(username, null);
		    			 break;}
		    	}
		    }
		}
		
		/*if (!this.getServerid().equals("s0")){
			JSONObject changeLogin = new JSONObject();
			changeLogin.put("type", "changeLogin");
			changeLogin.put("serverId", serverId);
			this.sendMsgToS0(changeLogin.toJSONString());
		}*/
		
	}
	
	public synchronized void introduce(String introJSONText){
		broadcast(introJSONText);
	}
	
	public synchronized void restart(String restartJSONText){
		broadcast(restartJSONText);
	}
	
	public synchronized void addAServer(Vector<String> aNew){
		server_Servers.add(aNew); 
	}
	
	public synchronized void addARoom(Vector<String> aNew){
		server_allrooms.add(aNew); 
	}
	
	public synchronized void sendMsgToS0(String Msg){
		for(int i=0; i<this.server_Servers.size();i++){
			if(this.server_Servers.get(i).get(0).equals(("s0"))){
				SSendToS sendToS0 = new SSendToS(server_Servers.get(i).get(1),
						Integer.parseInt(server_Servers.get(i).get(3)), Msg);
				sendToS0.start();
			}
		}
	}
	
}
