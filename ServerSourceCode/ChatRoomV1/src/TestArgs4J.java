import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class TestArgs4J {
	 @Option(name = "-n", usage = "server id", aliases = {"serverid"}, required = true)
	   private String serverid = null;
	 @Option(name = "-l", usage = "configuration file path", aliases = {"Server_conf"}, required = true)
	   private String configpath = null;
	 @Option(name = "-t", usage = "server type", aliases = {"Server_type"}, required = true)
	   private String serverType = null;
	 @Option(name = "-d", usage = "userdb path", aliases = {"user_db"}, required = false)
	   private String userDBpath = null;
	 @Option(name = "-h", usage = "hostname of the new server", aliases = {"newserver_hostid"}, required = false)
	   private String newHostname = null;
	 @Option(name = "-cp", usage = "portid for client of the new server", aliases = {"new_client_portid"}, required = false)
	   private String newCport = null;
	 @Option(name = "-sp", usage = "portid for server of the new server", aliases = {"new_server_portid"}, required = false)
	   private String newSport = null;
	 
	 
	public String getServerid() {
		return serverid;
	}
	public void setServerid(String serverid) {
		this.serverid = serverid;
	}
	public String getConfigpath() {
		return configpath;
	}
	public void setConfigpath(String configpath) {
		this.configpath = configpath;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getUserDBpath() {
		return userDBpath;
	}
	public void setUserDBpath(String userDBpath) {
		this.userDBpath = userDBpath;
	}
	public String getNewHostname() {
		return newHostname;
	}
	public void setNewHostname(String newHostname) {
		this.newHostname = newHostname;
	}
	public String getNewCport() {
		return newCport;
	}
	public void setNewCport(String newCport) {
		this.newCport = newCport;
	}
	public String getNewSport() {
		return newSport;
	}
	public void setNewSport(String newSport) {
		this.newSport = newSport;
	}
	
	public void doMain(String[] command)  {
		 CmdLineParser parser = new CmdLineParser(this);
		 try {
		       // parse the arguments.
		       parser.parseArgument(command);
		 } catch (CmdLineException e) {
		       //System.err.println(e.getMessage());  //print exception msg
		       return;
		 }catch (Exception e) {
				//e.printStackTrace();
		 }
	 }
}
