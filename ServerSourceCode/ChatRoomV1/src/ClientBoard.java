/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */
public class ClientBoard {
	//only record base info of clients
	private String identity;
	private String roomid;
	private String serverid;
	private String username;
	private String password;
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public String getServerid() {
		return serverid;
	}
	public void setServerid(String serverid) {
		this.serverid = serverid;
	}
	public ClientBoard(String identity, String roomid, String serverid){//constructor
		this.identity = identity;
		this.roomid = roomid;
		this.serverid = serverid;
	}
}
