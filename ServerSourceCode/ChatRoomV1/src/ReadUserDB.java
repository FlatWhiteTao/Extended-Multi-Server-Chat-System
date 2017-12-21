import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */

public class ReadUserDB {
	
	public static Vector<UserDB> readUser(String userDBpath){
		File file = new File(userDBpath);
		Vector <UserDB> userDBcollection = new Vector<UserDB>();
		try {
			BufferedReader readUser = new BufferedReader(new FileReader(file));
			String temp = null;
    		while((temp = readUser.readLine())!=null){
    		
    			String[] userInfo = temp.split("\t");
    			UserDB registerUsr = new UserDB();
    			registerUsr.setUsername(userInfo[0]);
    			registerUsr.setPassword(userInfo[1]);
    			registerUsr.setOnline(false);
    			userDBcollection.add(registerUsr);
    		}
			readUser.close();
		} catch (IOException e) {
        //e.printStackTrace();
		}
		return userDBcollection;
	}
}
