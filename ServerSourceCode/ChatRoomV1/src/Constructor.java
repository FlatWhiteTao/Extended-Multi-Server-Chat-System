
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.json.simple.JSONObject;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */
public class Constructor {
	
	 
	 
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		System.setProperty("javax.net.ssl.keyStore", "sslserverkey");
		System.setProperty("javax.net.ssl.keyStorePassword","12345qwe");
		System.setProperty("javax.net.ssl.trustStore", "sslservertrust");
		System.setProperty("javax.net.ssl.trustStorePassword","12345qwe");
		
		//System.setProperty("javax.net.debug","all");
		TestArgs4J arg4j = new TestArgs4J();
		arg4j.doMain(args);//parse args
		String configpath = null;
		String userDBpath = null;
		String serverid = null;
		String serverType = null;
		String newServerid = null;
		String newHostname = null;
		String newCport = null;
		String newSport = null;
		configpath = arg4j.getConfigpath();
		serverid = arg4j.getServerid();
		serverType = arg4j.getServerType();
		userDBpath = arg4j.getUserDBpath();
		
		newHostname = arg4j.getNewHostname();
		newCport = arg4j.getNewCport();
		newSport = arg4j.getNewSport();
		
		File file = new File(configpath);
		
		Vector<ServerBoard> serverBoards = new Vector<ServerBoard>();  
		Vector <UserDB> userDBcollection = new Vector<UserDB>();
		BufferedReader reader = null;
		if(serverType.equals("a")){//authentic one
			if(userDBpath==null||!serverid.equals("s0")){
				System.out.println("type or number of parameters wrong");
				System.exit(0);
			}
			else{
				userDBcollection = ReadUserDB.readUser(userDBpath);
			}
		}
		if(serverType.equals("n")){//new server
			newServerid = arg4j.getServerid();
			newHostname = arg4j.getNewHostname();
			newCport = arg4j.getNewCport();
			newSport = arg4j.getNewSport();
			if((newServerid == null)||(newHostname == null)||(newCport == null)||(newSport == null)){
				System.out.println("type or number of parameters wrong");
				System.exit(0);
			}
		}
        try {
            reader = new BufferedReader(new FileReader(file));
            String tmpString = null;
            while ((tmpString = reader.readLine()) != null) {
            	String[] tmpWords = tmpString.split("\t");
            	ServerBoard serverBoard = new ServerBoard(tmpWords[0], tmpWords[1], tmpWords[2], tmpWords[3]);
            	//0-serverid,1-hostname,2-clientport,3-serverport
            	serverBoards.add(serverBoard);
            }
            reader.close();
            //use serverBoards to store all servers basic info
            if(serverType.equals("n")){//a new server
            	if(serverid.equals("s0")){
            		System.out.println("type or number of parameters wrong");
            		System.exit(0);
            	}
            	int ifpass = 1;
            	for(int i = 0;i<serverBoards.size();i++){
            		if(serverBoards.get(i).getHostname().equals(newHostname)&&
            				(serverBoards.get(i).getServerid().equals(newServerid))||
            				(serverBoards.get(i).getClientport().equals(newCport)||
            				(serverBoards.get(i).getServerport().equals(newSport)) ))
            			ifpass = 0;
            	}// if this new server is valid
            	if(ifpass == 1){
            		Servers server = new Servers(newServerid,newHostname,
            				Integer.parseInt(newCport),Integer.parseInt(newSport));
            		//construct a new server
            		ServerBoard newserverBoard = new ServerBoard(newServerid,newHostname,newCport,newSport);
            		serverBoards.add(newserverBoard);
            		Vector<String> tmpMainHall = new Vector<String>();
        			tmpMainHall.add("MainHall-".concat(serverid));
        			tmpMainHall.add(serverid);
        			server.getServer_allrooms().add(tmpMainHall);
            		for(int j = 0;j < serverBoards.size();j++){
            			server.getServer_Servers().add(serverBoards.get(j).getServerinfolist());
            			//0-serverid,1-hostname,2-clientport,3-serverport
            		}
            		FileWriter writer = new FileWriter(configpath, true);
            		writer.write(newServerid+"\t"+newHostname+"\t"+newCport+"\t"+newSport+"\n");
    				writer.close();
    				server.server_startnr();
    				JSONObject introMsg = new JSONObject();
    				introMsg.put("type", "introduce");
    				introMsg.put("newServerid",newServerid);
    				introMsg.put("newHostname",newHostname);
    				introMsg.put("newCport",newCport);
    				introMsg.put("newSport",newSport);
    				String introJSONText = introMsg.toJSONString();
    				server.introduce(introJSONText);//introduce this server to other servers
            	}
            	else{
            		System.out.println("this new server info has been used or is invalid");
            		System.exit(0);
            	}
            }
            else if(serverType.equals("a")||serverType.equals("b")){//authentic or basic
            	for(int i = 0;i < serverBoards.size();i++){
                	if(serverBoards.get(i).getServerid().equals(serverid)){
                		Servers server = new Servers(serverBoards.get(i).getServerid(),//construct a new server
                				serverBoards.get(i).getHostname(),
                				Integer.parseInt(serverBoards.get(i).getClientport()),
                				Integer.parseInt(serverBoards.get(i).getServerport()));
                		for(int j = 0;j < serverBoards.size();j++){
                			server.getServer_Servers().add(serverBoards.get(j).getServerinfolist());
                			//0-serverid,1-hostname,2-clientport,3-serverport
                			if(!serverType.equals("a")){// authentic server do not store roomlist
                				if(!serverBoards.get(j).getServerid().equals("s0"))
                					server.getServer_allrooms().add(serverBoards.get(j).getRoominfolist());
                			}
                			//0-roomid,1-serverid
                			//initiate allservers_list and allrooms_list of each server
                		}
                		//next, handle it if it is an authentic server
                		if(serverType.equals("a")){
                			server.setServer_UserDB(userDBcollection);//set userDB in s0
                		}
                		server.server_start();
                	}
                }
            }
            else if(serverType.equals("r")){//restart a crashed server
            	for(int i = 0;i < serverBoards.size();i++){
                	if(serverBoards.get(i).getServerid().equals(serverid)){//construct a restart server
                		Servers server = new Servers(serverBoards.get(i).getServerid(),
                				serverBoards.get(i).getHostname(),
                				Integer.parseInt(serverBoards.get(i).getClientport()),
                				Integer.parseInt(serverBoards.get(i).getServerport()));
                		String reHostname = serverBoards.get(i).getHostname();
                		String reClientport = serverBoards.get(i).getClientport();
                		String reServerport = serverBoards.get(i).getServerport();
                		Vector<String> tmpMainHall = new Vector<String>();
            			tmpMainHall.add("MainHall-".concat(serverid));
            			tmpMainHall.add(serverid);
            			server.getServer_allrooms().add(tmpMainHall);
                		for(int j = 0;j < serverBoards.size();j++){
                			server.getServer_Servers().add(serverBoards.get(j).getServerinfolist());
                			//0-serverid,1-hostname,2-clientport,3-serverport
                		}
                		server.server_startnr();
                		JSONObject restartMsg = new JSONObject();
                		restartMsg .put("type", "restart");
                		restartMsg .put("serverid",serverid);
                		restartMsg .put("reHostname",reHostname);
                		restartMsg .put("reClientport",reClientport);
                		restartMsg .put("reServerport",reServerport);
        				String restartJSONText = restartMsg .toJSONString();
        				server.restart(restartJSONText);//introduce this server to other servers
                	}
                }
            }
            
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                	//e1.printStackTrace();
                }
            }
        }
	}
}
