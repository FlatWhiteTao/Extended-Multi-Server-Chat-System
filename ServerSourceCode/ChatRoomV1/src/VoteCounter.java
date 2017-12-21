/**
 * 
 * @author Mengda ZHANG; Tao Wang;Danping Zeng
 * @Student_Number 734524; 707458 ; 777619
 * @login_name  mengdaz1, taow3, danpingz
 */
public class VoteCounter {
	private String voteId;
	private int totalNumber;
	private String pass;
	private int currentNumber;
	
	public String getVoteId() {
		return voteId;
	}
	public void setVoteId(String voteId) {
		this.voteId = voteId;
	}
	public int getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
	public int getCurrentNumber() {
		return currentNumber;
	}
	public void setCurrentNumber(int currentNumber) {
		this.currentNumber = currentNumber;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public VoteCounter(String voteId, int totalNumber){
		this.voteId = voteId;
		this.totalNumber = totalNumber-1;
		this.currentNumber = 0;
		this.pass = "true";
	}
}
