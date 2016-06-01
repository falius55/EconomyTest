import java.io.*;
import java.util.*;

class PrivateBank{
	long cash = 0; // 現金
	long savings = 0; // 中央銀行に持つ当座預金
	long keepBond = 0; // 公債残高
	long loantoProducer = 0; // 企業向け貸付金
	long loantoHuman = 0; // 個人向け貸付金
	// 国家のインスタンス
	Gorvement nation;
	// 口座リスト
	ArrayList<Human> humanAccount;
	ArrayList<Producer> producerAccount;
	ArrayList<Gorvement> localGorvementAccount;
	
	// コンストラクタ
	public PrivateBank(){
		humanAccount = new ArrayList<Human>();
		producerAccount = new ArrayList<Producer>();
		localGorvementAccount = new ArrayList<Gorvement>();
		CentralBank.setInstance(this);
		}

	// 決算
	public void accountSettlement(GregorianCalendar cal){
		// 日次決算
		if(cash < getRequireCapital()){
			getSavings(getRequireCapital());
			}else if(cash > getRequireCapital()*2){
				setSavings(cash - getRequireCapital()*2);
				}else{
					}
		// 月次決算
		// 月末かどうかの判断
		if(cal.get(Calendar.MONTH) == 1  && cal.get(Calendar.DATE) == 29 ||
			cal.get(Calendar.MONTH) == 1 && !(cal.isLeapYear(cal.get(Calendar.YEAR))) && cal.get(Calendar.DATE) == 28 ||
			cal.get(Calendar.MONTH) == 3 && cal.get(Calendar.DATE) == 30 ||
			cal.get(Calendar.MONTH) == 5 && cal.get(Calendar.DATE) == 30 ||
			cal.get(Calendar.MONTH) == 8 && cal.get(Calendar.DATE) == 30 ||
			cal.get(Calendar.MONTH) == 10 && cal.get(Calendar.DATE) == 30 ||
			cal.get(Calendar.DATE) == 31){
			}
		// 年次決算
		if(cal.get(Calendar.MONTH)+1==12 && cal.get(Calendar.DATE)==31){
				
			}
		}
	// 口座開設
	public void setAccount(Human human){
		humanAccount.add(human);
		human.setInstance(this);
		cash += human.getBalance();
		}
	public void setAccount(Producer producer){
		producerAccount.add(producer);
		producer.setInstance(this);
		}
	public void setAccount(Gorvement gorvement,boolean isNation){
		if(isNation){
		nation = gorvement;
		nation.setInstance(this);
		}else{
		localGorvementAccount.add(gorvement);
			}
		}

	//**** 基本活動 ****//
	// 融資する
	// 返り値は、借りることに成功した金額
	public long setFinance(Object obj,long amount){
		// 呼び出し元の確認
		switch(obj.getClass().getSimpleName().toString()){
			case "Human":
			loantoHuman += amount;
			break;
			case "Producer":
			loantoProducer += amount;
			break;
			default:
			// 呼び出し元のオブジェクトを取得できなければ0を返す
			return 0;
			}
		// 保有現金が融資額に満たなければ中央銀行から引き落とし
		if(cash < amount){
			// 引き落としに失敗したら0を返す。
			amount = getSavings(amount);
			}
		cash -= amount;
		return amount;
		}
	// 返済を受ける
	public long getSatisfaction(Object obj,long amount){
		// 呼び出し元の確認
		switch(obj.getClass().getSimpleName().toString()){
			case "Human":
			loantoHuman -= amount;
			break;
			case "Producer":
			loantoProducer -= amount;
			break;
			default:
			// 呼び出し元のオブジェクトを取得できなければ0を返す
			return 0;
			}
			cash += amount;
			return amount;
		}
	// 貯金する
	public long setBalance(long amount){
		cash += amount;
		return amount;
		}
	// 貯金を下ろす
	public long getBalance(long amount){
		if(cash - amount < getRequireCapital()){
			amount = getSavings(amount);
			}
		cash -= amount;
		return amount;
		}

	// 中央銀行にお金を預ける。
	public long setSavings(long amount){
		if(cash < amount) return 0;
		cash -= amount;
		savings += amount;
		return amount;
		}
	// 中央銀行からお金を引き出す。
	public long getSavings(long amount){
		if(savings < amount) return 0;
		savings -= amount;
		cash += amount;
		return amount;
		}

	// ゲッター
	// 債務総額（人、企業、自治体からの預金総額）
	public long getDeposit(){
		long deposit = 0;
		for(int i = 0;i<humanAccount.size();i++){
			deposit += humanAccount.get(i).getBalance();
			}
		for(int i = 0;i<producerAccount.size();i++){
			deposit += producerAccount.get(i).getMoney();
			}
		for(int i = 0;i<localGorvementAccount.size();i++){
			deposit += localGorvementAccount.get(i).getExchequer();
			}
		return deposit;
		}
	// 貸付金総額
	public long getLoan(){
		return loantoHuman + loantoProducer;
		}
	public long getLoantoHuman(){
		return loantoHuman;
		}
	public long getLoantoProducer(){
		return loantoProducer;
		}
	// 必要資本額
	public long getRequireCapital(){
		return (long)(getMoney() * CentralBank.getCapitalRate());
		
		}
	// 保有現預金
	public long getMoney(){
		return cash + savings;
		}
	public long getCash(){
		return cash;
		}
	public long getSavings(){
		return savings;
		}
	}
