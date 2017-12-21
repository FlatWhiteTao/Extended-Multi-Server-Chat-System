/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */
public class UserDB {
	 
	 private String username;
	 private String password;
	 private boolean online = false;
	
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
	
	 public boolean isOnline() {
		return online;
	}
	
	 public void setOnline(boolean online) {
		this.online = online;
	}
	
	
	 
}
