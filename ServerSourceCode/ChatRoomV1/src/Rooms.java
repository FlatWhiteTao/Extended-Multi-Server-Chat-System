import java.util.Vector;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class Rooms {
	
	private String roomid;
	private String owner;
	private String serverid;
	private Vector<ClientBoard> room_clients = new Vector<ClientBoard>();  
	private Vector<ClientConnection> room_connections = new Vector<ClientConnection>();
	
	public Rooms(String roomid, String owner, String serverid) {
		this.roomid = roomid;
		this.owner = owner;
		this.serverid = serverid;// TODO Auto-generated constructor stub
	}
	
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getServerid() {
		return serverid;
	}
	public void setServerid(String serverid) {
		this.serverid = serverid;
	}
	public Vector<ClientBoard> getRoom_clients() {
		return room_clients;
	}
	public void setRoom_clients(Vector<ClientBoard> room_clients) {
		this.room_clients = room_clients;
	}
	public Vector<ClientConnection> getRoom_connections() {
		return room_connections;
	}
	public void setRoom_connections(Vector<ClientConnection> room_connections) {
		this.room_connections = room_connections;
	}
	
	public synchronized void deleteClient(String clientid){//via clientid
		for(int i = 0;i<room_clients.size();i++)
			if(room_clients.get(i).getIdentity().equals(clientid)){
				room_clients.remove(i);
				break;
			}
	}//delete a clientBoard via clientid
	
	public synchronized ClientBoard getClientInRoom(String clientid){
		ClientBoard aClient = null;
		for(int i = 0;i<room_clients.size();i++)
			if(room_clients.get(i).getIdentity().equals(clientid)){
				aClient = room_clients.get(i);
				return aClient;
			}
		return aClient;
	}//get a clientBoard via clientid
	
	public synchronized void addClient(ClientBoard aClientBoard){
		this.getRoom_clients().add(aClientBoard);
	}
	public synchronized void removeClient(ClientBoard aClientBoard){//via clientboard
		this.getRoom_clients().remove(aClientBoard);
	}
	public synchronized int getClientsNum(){
		int i = this.getRoom_clients().size();
		return i;
	}
	
	public synchronized void addConnections(ClientConnection c){
		this.getRoom_connections().add(c);
	}
	public synchronized void addPosConnections(ClientConnection c, int i){
		this.getRoom_connections().add(i, c);
	}
	
	public synchronized void removeConnections(ClientConnection c){//via clientboard
		this.getRoom_connections().remove(c);
	}
	public synchronized int getConnectionsNum(){
		int i = this.getRoom_connections().size();
		return i;
	}
	
}
