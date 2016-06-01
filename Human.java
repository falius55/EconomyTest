import java.io.*;
import java.util.*;

// 人の行動に関するクラス
public class Human implements Subject{
	// メンバ変数
	// 状態
	private int id; // 識別番号
	private int job = -1; // 職業 -1:無職 他は会社番号
	private int countDependents = 0; // 扶養家族の人数
	private int monthlyPay = 0; // 月給
	private int wontWallet = 50000; // 手持ちをいくらにしたいか
	private int borderWallet = 5000; // 手持ちの下限額
	// ストック
	long balance = 0; // 預金残高
	private int cash = 0; // 手持ちのお金
	private long arrears = 0; // 借金
	// フロー
	private long totalPay = 0; // 支払総額
	private long totalY = 0; // 総所得（税抜き）
	private int incomeOfYear = 0; // (昨年)年収
	private int countIncome = 0; // 年収計算用変数
	long puttingIncomeTax = 0; // 支払所得税
	// 他のインスタンス
	private ArrayList<Producer> producerList;
	private Gorvement nation;
	private PrivateBank bank;

	// コンストラクタ
	public Human(int id,Producer producers[]){
		producerList = new ArrayList<Producer>();
		this.id = id;
		for(int i = 0;i < producers.length;i++){
			producerList.add(producers[i]);
			} 
		
		}

	// インスタンスが返ってくる。見つからなければnullが返る
	// 呼び出し元
	// actBuy
	public Producer findStore(String strWont){
		for(int i = 0;i<producerList.size();i++){
			// 買えるところを探す。
			// 見つかったかどうか。
			if(producerList.get(i).findProduct(strWont)){
				return producerList.get(i);
				}
			}
			return null;
		}
	// 購入する
	// 取引の成立不成立を判断する。
	// findStore(strWont):return Producer
	// Producer.actSale(strWont):return price
	synchronized public int actBuy(String strWont){
		Producer producer = findStore(strWont);
		int price = 0;
		if(producer == null){
			return 0;
			}else{
			price = producer.actSale(strWont);
			}
		// 買えれば、買う
		if(price == -1) return 0; // 売っている店がなければreturn
			// 手持ちが足りなければ足りない分の預金を引き出す。
			if(cash < price){
				chargeBank(price - cash);
				// それでも足りなければ、融資を受ける。
				if(cash < price){
					borrow(price - cash);
					// それでもダメなら、falseを返す。
					// すでにactSaleしてるんだが……
					if(cash < price) return 0;
				}
			}
			// 集計
			cash -= price;
			totalPay += price;
			return price;
		}
		// 就職活動
		// 1日の最初に実行
	public boolean seekJob(){
		for(int i = 0;i<producerList.size();i++){
			if(producerList.get(i).isRecruit()){
				// その会社が採用活動中ならば
				setJob(i);
		System.out.println("humans["+ id +"]がproducers["+ i + "]に就職しました。");
					return true;
				}
			}
			return false;
		}
		// 就職する
		// 引数に-1を渡せば退職
		public void setJob(int job){
			this.job = job;
			if(this.job == -1) return;
			producerList.get(this.job).onRecruit(this.job,this);
			}
	
	// 融資を受ける
	public void borrow(long amount){
		if(bank == null) return;
		amount = bank.setFinance(this,amount);
		arrears += amount;
		// 現金で受け取るのでsetBalance()しない
		cash += amount;
		}
	// 借金を返済する。
	public void satisfaction(){
		if(arrears <= 0) return;
		long amount = 0;
		// 返済額算定
		// 返せるだけ返す
		if(cash > 0 && cash > arrears){
			amount = (int)arrears;
			}else if(cash > 0){
				amount = cash;
				}else{
					}
			amount = bank.getSatisfaction(this,amount);
			arrears -= amount;
			cash -= amount;
		}

	// 預金を引き出す
	public void chargeBank(int amount){
		// 預金が指定額に満たなければ、預金全額を引き出す。
		if(balance < amount) amount = (int)balance;
		balance -= bank.getBalance(amount);
		cash += amount;
		}

	// セッター
	// 自動選択消費
	public void setAutoBuy(long amount){
		for(int i=0;i<producerList.size();i++){
			for(String key:producerList.get(i).getProductMap().keySet()){
				int data = producerList.get(i).getProductMap().get(key);
				if(data < amount){
					actBuy(key);
					amount -= data;
					}
				}
			}
		}
	// 自動予算算定
	public long getBudget(){
		if(getBalance() > nation.getAverageOfBalance()){
			return (getBalance() - nation.getAverageOfBalance()) / 10;
			}
		return cash/5;
		}
	// 1日の最初に実行
	public void setSettlement(Calendar cal){
		// 求職活動
		if(job == -1){
			seekJob();
			}
		// 借金の返済
		satisfaction();
		// 預金を下ろす
		//if(balance / 300000 * 10000 < wontWallet) wontWallet = balance / 60;
		if(cash < borderWallet && balance > 0){
			int downMoney = wontWallet-cash;
			if(downMoney%1000 != 0){
				downMoney -= downMoney%1000;
				downMoney += 1000;
				}
			chargeBank(downMoney);
			}
		// 毎月21日
		// 月給
		if(cal.get(Calendar.DATE)==21 && job != -1){
			monthlyPay = producerList.get(job).getMonthlyPay();
			int tax = Tax.getIncomeTax(monthlyPay*12)/12;
			setIncome(monthlyPay,tax);
			System.out.println(tax + "円分の源泉徴収があったので、手取りは"+(monthlyPay-tax)+"円となりました。");
			}
		// 年末
		if(cal.get(Calendar.MONTH)+1==12 && cal.get(Calendar.DATE)==31){
			// 年収計算
			incomeOfYear = countIncome;
			countIncome = 0;
		}
		}
	// 所得の受け取り
	public void setIncome(int amount,int tax){
			nation.putTax(tax);
			puttingIncomeTax += tax;
			if(bank == null){
			balance += (amount - tax);
				}else{
			balance += bank.setBalance(amount - tax);
				}
			totalY += amount - tax;
			countIncome += amount;
			System.out.println("humans[" + id + "]が、" + amount + "円の収入を得ました。");
		}
	// 非課税収入　所得にも含まない
	public void setIncome(int amount){
			if(bank == null){
			balance += amount;
				}else{
			balance += bank.setBalance(amount);
				}
			System.out.println("humans[" + id + "]が、" + amount + "円の収入を得ました。");
		}
	// 銀行口座
	public void setInstance(PrivateBank bank){
		this.bank = bank;
		}
	// 中央政府のインスタンス受け取り
	public void setNationInstance(Gorvement nation){
		this.nation = nation;
		}
	// ゲッター
	public String getName(){
		return "humans["+id+"]";
		}
	public long getBalance(){
		return balance;
		}
	public long getCash(){
		return cash;
		}
	public long getArrears(){
		return arrears;
		}
	public long getTotalPay(){
		return totalPay;
		}
	public long getTotalY(){
		return totalY;
		}
	public int getJob(){
		return job;
		}
	public int getID(){
		return id;
		}
	public int getIncomeOfYear(){
		return incomeOfYear;
		}
	public long getPuttingIncomeTax(){
		return puttingIncomeTax;
		}
	}
