import java.io.*;
import java.util.*;

public class Gorvement implements Subject{
	public int id;
	public long revenue = 0; // 歳入
	public long exchequer = 0; // 国庫
	public long bond = 0; // 公債残高
	public long amountSubsidy = 0; // 移転的支出(補助金総額)
	public long amountWelfare = 0; // 社会保障総額
	public boolean isNation = false; // 国家はtrue,自治体はfalse
	public long taxation = 0; // 徴税額
	public long amountPay = 0; // 
	
	// 他のインスタンス
	ArrayList<Producer> producerList; // 登録業者名簿
	ArrayList<Human> humanID; // 国民情報
	ArrayList<PrivateBank> bankList; // 登録銀行

	// コンストラクタ
	// nation
	public Gorvement(Human[] humans,Producer[] producers){
		this(-1,humans,producers);
		for(int i=0;i<humans.length;i++){
			// 社会保障として、国民全員の資産が2,000,000円になるようお金を配る(国家)。
			if(humans[i].getCash() + humans[i].balance < 2000000){
				putSubsidy(humans[i],2000000-(int)(humans[i].getCash()+humans[i].balance));
				}
			}
	}
	public Gorvement(int id,Human[] humans,Producer[] producers){
		this.id = id;
		// 公債発行
		if(isNation()){
			flotationBondtoCentral(50000000);
			}else{
				flotationBondtoPrivate(5000000);
				}
		// 各インスタンスを取得
		humanID = new ArrayList<Human>();
		for(int i=0;i<humans.length;i++){
			humanID.add(humans[i]);
			humans[i].setNationInstance(this);
			}
		producerList = new ArrayList<Producer>();
		for(int i=0;i<producers.length;i++){
			producerList.add(producers[i]);
			producers[i].setNationInstance(this);
			}

		bankList = new ArrayList<PrivateBank>();
		CentralBank.setInstance(this);
		}

	// 政府支出
	// 購入
	// インスタンスが返ってくる。見つからなければnullが返る
	public Producer findStore(String strWont){
		for(int i = 0;i<producerList.size();i++){
			// 買えるところを探す。
			// 見つかったかどうか
			if(producerList.get(i).findProduct(strWont)){
				return producerList.get(i);
				}
			}
			return null;
		}
	// 購入する
	// 取引の成立不成立を判断する。
	synchronized public boolean actBuy(String strWont){
		Producer producer = findStore(strWont);
		int price;
		if(producer==null){
			return false;
			}else{
			price = producer.actSale(strWont);
			}
		// 買えれば、買う
		if(price == -1) return false; // 売っている店がなければreturn
			// 手持ちが足りなければ、市中消化の国債発行
			if(exchequer < price) flotationBondtoPrivate(price);
			// 集計
			exchequer -= price;
			amountPay += price;
			return true;
		}
	// 徴税する
	public void putTax(int amount){
		revenue += amount;
		exchequer += amount;
		taxation += amount;
		}
	
	// 補助金(社会保障含む)
	public void putSubsidy(Object obj,int amount){
		// 国庫にその金があるか
		if(exchequer >= amount){
			exchequer -= amount;
		switch(obj.getClass().getSimpleName().toString()){
			case "Human":
			Human human = (Human)obj;
			amountWelfare += amount;
			human.setIncome(amount);
			break;
			case "Producer":
			amountSubsidy += amount;
			//TODO:生産者への所得移転処理
			break;
			default:
			break;
			}
			}

		}

	// 公債発行
	// 中央銀行引受
	public void flotationBondtoCentral(int amount){
		if(id>=0){
			System.err.println("自治体の公債は中央銀行では引き受けられません。");
			return;
			}
		CentralBank.keepBond += amount;
		int newBond = CentralBank.createCurrency(amount);
		bond += newBond;
		exchequer += newBond;
		revenue += newBond;
		}
	// 市中消化
	public void flotationBondtoPrivate(int amount){
		int i;
		// 購入可能な銀行を探索
		for(i = 0;i<bankList.size();i++){
			if(bankList.get(i).savings > amount) break;
			}
			// 購入可能な銀行がなければ中央銀行に引き受けさせる。
			if(i==bankList.size()){
				flotationBondtoCentral(amount);
				return;
				}
		// 購入
		int newBond = amount;
		bankList.get(i).keepBond += newBond;
		bankList.get(i).savings -= newBond;
		bond += newBond;
		exchequer += newBond;
		revenue += newBond;
		}
		
	// 銀行登録
	public void setInstance(PrivateBank privateBank){
		bankList.add(privateBank);
		}

	public boolean isNation(){
		if(id==-1)	return true;
		 	 else		return false;
		}
	// ゲッター
	// 公債残高
	public long getBond(){
		return bond;
		}
	public long getRevenue(){
		return revenue;
		}
	public long getExchequer(){
		return exchequer;
		}
	public long getTaxation(){
		return taxation;
		}
	public long getAmountPay(){
		return amountPay;
		}
	// 統計
	// 国民預金平均
	public int getAverageOfBalance(){
		int sum = 0;
		for(int i=0;i<humanID.size();i++){
			sum += humanID.get(i).getBalance();
			}
		return sum/humanID.size();
		}
	public String getName(){
		if(isNation()){
			return "nation";
			}else{
				return "gorvements["+id+"]";
				}
		}
	}
