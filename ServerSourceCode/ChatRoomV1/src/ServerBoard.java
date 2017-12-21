import java.util.Vector;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class ServerBoard {
	//only record base info of servers
	private String serverid;
	private String hostname;
	private String clientport;
	private String serverport;
	private Vector <String> serverinfolist = new Vector <String>();
	private Vector <String> roominfolist = new Vector <String>();
	/*private Vector<UserDB> server_UserDB = new Vector<UserDB>();
	
	
	public Vector<UserDB> getServer_UserDB() {
		return server_UserDB;
	}
	public void setServer_UserDB(Vector<UserDB> server_UserDB) {
		this.server_UserDB = server_UserDB;
	}*/
	public String getServerid() {
		return serverid;
	}
	public void setServerid(String serverid) {
		this.serverid = serverid;
	}
	public String getClientport() {
		return clientport;
	}
	public void setClientport(String clientport) {
		this.clientport = clientport;
	}
	public String getServerport() {
		return serverport;
	}
	public void setServerport(String serverport) {
		this.serverport = serverport;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public Vector<String> getServerinfolist() {
		return serverinfolist;
	}
	public void setServerinfolist(Vector<String> serverinfolist) {
		this.serverinfolist = serverinfolist;
	}
	public Vector<String> getRoominfolist() {
		return roominfolist;
	}
	public void setRoominfolist(Vector<String> roominfolist) {
		this.roominfolist = roominfolist;
	}
	public ServerBoard(String serverid,  String hostname ,String clientport, String serverport) {
		try {
			this.serverid = serverid;
			this.hostname = hostname;
			this.clientport = clientport;
			this.serverport = serverport;
			serverinfolist.add(serverid);
			serverinfolist.add(hostname);
			serverinfolist.add(clientport);
			serverinfolist.add(serverport);//0-serverid,1-hostname,2-clientport,3-serverport
			if(!serverid.equals("s0")){
				roominfolist.add("MainHall-".concat(serverid));
				roominfolist.add(serverid);//0-roomid,1-serverid
			}
			//to initiate allservers_list and allrooms_list of each server
		} catch (Exception e) {
			e.printStackTrace();
	 }
	}
}
