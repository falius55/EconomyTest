import java.io.*;
import java.util.*;

// 生産者クラス
public class Producer implements Subject{
	// メンバ変数
	// 商品リスト
	Map<String,Integer> productMap;
	// 原材料リスト
	Map<String,String> materialMap;
	// インスタンス
	Gorvement nation;
	PrivateBank bank;
	ArrayList<Producer> producerList;
	// 状態
	String industries = ""; // 業種フラグ
	ArrayList<Human> employList; // 従業員のリスト
	int id = 0;
	// 生産品
	// 会計
	int salary = 200000; // 従業員への給与額
	long totalSales = 0; // 総売上高
	int monthSales = 0; // 当月売上高
	int preMonthSales = 0; // 前月売上高
	int twoPreMonthSales = 0; // 前々月売上高
	int averageSales = 0; // ３ヶ月平均売上高
	int countSold = 0; // 販売数
	long variableCost = 0; // 可変費用累計額
	long fixedAssets = 0; // 固定資産額
	long totalCost = 0; // 総費用
	int monthCost = 0; // 当月費用
	int preMonthCost = 0; // 前月費用
	int twoPreMonthCost = 0; // 前々月費用
	int averageCost = 0; // ３ヶ月平均費用
	long marginalCost = 0; // 限界費用
	int unpaidConsumptionTax = 0; // 未払消費税
	long consumptionTax = 0; // 消費税総額
	long puttingCorporationTax = 0;
	long puttingConsumptionTax = 0;
	long money = 0; // 保有現預金 現金概念がないため、増減には必ず預金の増減が伴う
	long totalPurchase = 0; // 仕入れ総額
	long arrears = 0; // 借金
	long amountInvestment = 0; // 投資総額

	// コンストラクタ
	public Producer(int id){
		employList = new ArrayList<Human>();
		this.id = id;
		}
	
	// ****販売****//
	// その商品を売っているかどうかを探す
	// 呼び出し元
	// Human.findStore
	// Producer.findStore
	public boolean findProduct(String strProduct){
		if(productMap.containsKey(strProduct)){
			return true;
			}else{
				return false;
				}
		}
	// 販売する
	// 中間投入材の購入
	// 返り値は値段
	// 呼び出し元
	// Human.actBuy
	// Producer.actBuy
	synchronized public int actSale(String strWont){
		int cost = 0;
		// 原料を買う
		if(materialMap.containsKey(strWont)){
			cost = actBuy(materialMap.get(strWont));
			if(cost == -1) return -1;
			}
		// 集計
		int price = productMap.get(strWont);
		countSold++; // 販売数
		unpaidConsumptionTax += Tax.getConsumptionTax(price - cost); // 未払消費税
		consumptionTax += Tax.getConsumptionTax(price - cost); // 消費税
		totalSales += price; // 総売上
		monthSales += price; // 当月売上高
		money += bank.setBalance(price);
		// System.out.println("actSale:"+strWont+"("+price+")");
		return price;
		}
		
	// インスタンスが返ってくる。見つからなければnullが返る
	// findStoreでのborrowはinvestからは二重融資を受けてしまうので、インスタンスを返す形に変更
	// 呼び出し元
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
	synchronized public int actBuy(String strWont){
		int price;
		Producer producer = findStore(strWont);
		if(producer == null){
			return -1;
			}else{
				price = producer.actSale(strWont);
				}
		// 買えれば、買う
		if(price == -1) return -1; // 売っている店がなければreturn
		// 買えるだけのお金がなければ融資を受ける。
		if(money < price) borrow(price - money);
		// それでも足りなければ失敗。
		// すでにactSaleしてる……
		if(money < price) return -1;
			// 集計
			money -= bank.getBalance(price);
			totalPurchase += price;
			totalCost += price;
			monthCost += price;
			return price;
		}
	
	// 投資する
	synchronized public int invest(String strWont){
		int price;
		Producer producer = findStore(strWont);
		if(producer == null){
			return -1;
			}else{
				price = producer.actSale(strWont);
				}
		// 買えれば、買う
		if(price == -1) return -1; // 売っている店がなければreturn
			// 融資を受ける。
			borrow(price);
			// 集計
			money -= bank.getBalance(price);
			fixedAssets += price;
			amountInvestment += price;
			return price;
		}

	// ****採用****//
	// 採用活動中かどうかを判断する
	boolean recruited = false;
	public boolean isRecruit(){
		if(recruited) return false;
		if((money > salary*(employList.size()+1)) && getBenefit() > salary*(employList.size()+1)){ 
			return true;
			}
		
		return false;
		}
	// 採用する
	public void onRecruit(int job,Human human){
		if(job == -1){
			// 解雇する
			for(int i=0;i<employList.size();i++){
				if(employList.get(i) == human){
					employList.remove(i);
					employList.trimToSize();
					break;
				}
			}
			}else{
				// 採用
				employList.add(human);
				recruited = true;
		}
		}
		// 解雇するかどうかを判断する
		public boolean isFire(){
		if((money < salary*employList.size()) || getBenefit() < salary*employList.size()){ 
			return true;
			}
			return false;
		}

	//****会計****//
	// 融資を受ける
	// 生産者は現金を手で受け取らず振込で受け取るため、関数内ですぐさま預金操作が必要。
	public void borrow(long amount){
		amount = bank.setFinance(this,amount);
		arrears += amount;
		// すぐさま預金に入れるのでsetBalance()
		// setFinance()で銀行側moneyが減少している。
		money += bank.setBalance(amount);
		}
	// 借金を返済する。
	public long satisfaction(long amount){
		if(arrears <= 0 || money < amount) return 0;
			// getSatisfaction()で銀行側moneyが増えるので、getBalance()してから返済
			money -= bank.getBalance(amount);
			amount = bank.getSatisfaction(this,amount); // 銀行側債権管理のためthisを渡す。失敗すると0が返ってくる。
			arrears -= amount;
			return amount;
		}
	// 決算
	public void accountSettlement(GregorianCalendar cal){
		// 日次決算
		// 月次決算
		if(cal.get(Calendar.MONTH) == 1  && cal.get(Calendar.DATE) == 29 ||
			cal.get(Calendar.MONTH) == 1 && !(cal.isLeapYear(cal.get(Calendar.YEAR))) && cal.get(Calendar.DATE) == 28 ||
			cal.get(Calendar.MONTH) == 3 && cal.get(Calendar.DATE) == 30 ||
			cal.get(Calendar.MONTH) == 5 && cal.get(Calendar.DATE) == 30 ||
			cal.get(Calendar.MONTH) == 8 && cal.get(Calendar.DATE) == 30 ||
			cal.get(Calendar.MONTH) == 10 && cal.get(Calendar.DATE) == 30 ||
				cal.get(Calendar.DATE) == 31){
				// 借金の返済
				// 投資分以外の借金を算定
				long amount = (long)(arrears - fixedAssets);
				if(amount == 0){
					// 投資分以外の借金がなければ減価償却
					amount = fixedAssets/12; // 1ヶ月の償却費
					amount = satisfaction(amount);
					fixedAssets -= amount;
					totalCost += amount;
					monthCost += amount;
					}else{
						if(money > amount){
							satisfaction(amount);
							}else{
								satisfaction(money);
								}
						}
				// 納税額の計算
				// 法人税
				int tax = (int)(Tax.getCorporationTax((monthSales - monthCost)*12)/12);
				puttingCorporationTax += tax;
				// 未払消費税
				tax += unpaidConsumptionTax;
				puttingConsumptionTax += unpaidConsumptionTax;
				// 納税
				if(money < tax) borrow(tax);
				nation.putTax(tax);
				totalCost += tax;
				monthCost += tax;
				// 納税は民間預金残高から国庫に移動するためgetBalance()
				money -= bank.getBalance(tax);
				unpaidConsumptionTax = 0;
				// 平均売上高、平均費用の計算
				// 売上高計算
				averageSales = (monthSales + preMonthSales + twoPreMonthSales) / 3;
				twoPreMonthSales = preMonthSales;
				preMonthSales = monthSales;
				monthSales = 0;
				// 費用計算
				averageCost = (monthCost + preMonthCost + twoPreMonthCost) / 3;
				twoPreMonthCost = preMonthCost;
				preMonthCost = monthCost;
				monthCost = 0;
				// 解雇
				if(isFire() && !(employList.isEmpty())){
					employList.get(0).setJob(-1);
					}else{
						recruited = false;
						}
				// 新規に投資する
				setInvest();
				}
			// 年次決算
			if(cal.get(Calendar.MONTH) == 11 && cal.get(Calendar.DATE) == 31){
				}
		}

	// ****セッター****//
	// 業種設定 + 商品リスト作成
	// 価格は税込み
	public void setIndustries(String industries){
		this.industries = industries;
			productMap = new HashMap<String,Integer>();
			materialMap = new HashMap<String,String>();
			if(this.industries.equals("スーパー")){
				productMap.put("パン",108);
				materialMap.put("パン","小麦粉");
				productMap.put("おにぎり",120);
				materialMap.put("おにぎり","精米100g");
				productMap.put("サラダ",158);
				materialMap.put("サラダ","野菜");
				productMap.put("お米",1480);
				materialMap.put("お米","精米5kg");
				productMap.put("インスタントラーメン",138);
				materialMap.put("インスタントラーメン","レトルト加工");
				}else if(this.industries.equals("コンビニ")){
				productMap.put("清涼飲料水",150);
				productMap.put("菓子パン",150);
				productMap.put("お菓子",150);
				productMap.put("ワイン",500);
				materialMap.put("ワイン","アルコール発酵");
				productMap.put("ビール",232);
				materialMap.put("ビール","アルコール発酵");
				productMap.put("日本酒",260);
				materialMap.put("日本酒","アルコール発酵");
				productMap.put("アイス",148);
				productMap.put("住民票発行契約",8000);
				productMap.put("配本契約",40000);
				}else if(this.industries.equals("飲食店")){
				productMap.put("スバゲッティ",480);
				productMap.put("ハンバーグ",980);
				materialMap.put("ハンバーグ","牛肉加工");
				productMap.put("ラーメン",780);
				productMap.put("カレーライス",1080);
				materialMap.put("カレーライス","米5kg");
				productMap.put("出前契約",50000);
				}else if(this.industries.equals("電気量販店")){
				productMap.put("パソコン",Tax.getInConsumptionTax(98000));
				materialMap.put("パソコン","機械加工");
				productMap.put("プリンター",Tax.getInConsumptionTax(68000));
				materialMap.put("プリンター","機械加工");
				productMap.put("インク",1480);
				productMap.put("スマートフォン",Tax.getInConsumptionTax(78000));
				materialMap.put("スマートフォン","機械加工");
				productMap.put("冷蔵庫",Tax.getInConsumptionTax(58000));
				materialMap.put("冷蔵庫","機械加工");
				productMap.put("電池",280);
				productMap.put("機械保守契約",Tax.getInConsumptionTax(18000));
				materialMap.put("機械保守契約","機械加工");
				}else if(this.industries.equals("書店")){
				productMap.put("ペン",148);
				productMap.put("消しゴム",118);
				productMap.put("コピー用紙",480);
				productMap.put("紙",180);
				productMap.put("雑誌",280);
				productMap.put("文庫本",480);
				productMap.put("ハードカバー",1280);
				productMap.put("漫画",480);
				productMap.put("雑誌購読契約",45000);
				}else if(this.industries.equals("委託会社")){
				productMap.put("土木工事",50000);
				materialMap.put("土木工事","機械加工");
				productMap.put("印刷",220);
				}else if(this.industries.equals("情報")){
				productMap.put("ソフトウェアライセンス",Tax.getInConsumptionTax(7800));
				productMap.put("システム構築",65000);
				productMap.put("ネットワーク構築",78000);
				}else if(this.industries.equals("農家")){
				productMap.put("米100g",10);
				productMap.put("米5kg",500);
				productMap.put("小麦",20);
				productMap.put("野菜",30);
				productMap.put("牛肉",250);
				productMap.put("農家専属契約",48000);
				}else if(this.industries.equals("加工")){
				productMap.put("小麦粉",50);
				productMap.put("精米100g",50);
				materialMap.put("精米100g","米100g");
				productMap.put("精米5kg",900);
				materialMap.put("精米5kg","米5kg");
				productMap.put("牛肉加工",600);
				materialMap.put("牛肉加工","牛肉");
				productMap.put("レトルト加工",60);
				productMap.put("機械加工",1200);
				productMap.put("アルコール発酵",100);
				}else if(this.industries.equals("インフラ")){
				productMap.put("電気",220);
				productMap.put("ガス",320);
				productMap.put("水道",180);
				productMap.put("電気契約",22000);
				productMap.put("ガス契約",32000);
				productMap.put("水道契約",18000);
				}else if(this.industries.equals("輸入会社")){
				productMap.put("石炭",120);
				}else{
					}
		}
	// 投資
	public void setInvest(){
		switch(industries){
			case "スーパー":
			invest("ネットワーク構築");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			break;
			case "コンビニ":
			invest("システム構築");
			invest("ネットワーク構築");
			invest("冷蔵庫");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			break;
			case "飲食店":
			invest("農家専属契約");
			invest("雑誌購読契約");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			invest("ネットワーク構築");
			invest("冷蔵庫");
			break;
			case "電気量販店":
			invest("ネットワーク構築");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			break;
			case "書店":
			invest("配本契約");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			break;
			case "委託会社":
			invest("出前契約");
			invest("システム構築");
			invest("プリンター");
			invest("パソコン");
			invest("機械保守契約");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			break;
			case "情報":
			invest("パソコン");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			break;
			case "農家":
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			break;
			case "加工":
			invest("機械保守契約");
			invest("電気契約");
			invest("ガス契約");
			invest("水道契約");
			invest("システム構築");
			break;
			case "インフラ":
			invest("雑誌購読契約");
			invest("ソフトウェアライセンス");
			invest("出前契約");
			invest("システム構築");
			invest("パソコン");
			break;
			case "輸入会社":
			break;
			default:
			break;
			}
		}
	// 口座開設
	public void setInstance(PrivateBank bank){
		this.bank = bank;
		}
	// 国のインスタンス受取
	public void setNationInstance(Gorvement nation){
		this.nation = nation;
		}
	// 生産者リスト
	public void setInstance(Producer[] producers){
		producerList = new ArrayList<Producer>();
		for(int i = 0;i<producers.length;i++){
			producerList.add(producers[i]);
			}
		}
	// ****ゲッター****//
	// 業種
	public String getIndustries(){
		return industries;
		}
	// 売上個数
	public int getSales(){
		return countSold;
		}
	// 支払い給与額
	public int getMonthlyPay(){
		long salary = (money/employList.size()+1);
		if(salary < 150000) salary = 150000;
		if(salary > 500000) salary = 500000;
		totalCost += salary;
		monthCost += salary;
		if(money < salary) borrow(salary);
		money -= bank.getBalance(salary);
		return (int)salary;
		}
	// 付加価値
	public long getValueAdded(){
		return totalSales - totalPurchase;
		}
	// 利潤
	public long getBenefit(){
		return totalSales - totalCost;
		}
	// 借金
	public long getArrears(){
		return arrears;
		}
	// 減価償却累計額
	public long getAmountDepreciation(){
		return amountInvestment - fixedAssets;
		}
	// 投資総額
	public long getAmountInvestment(){
		return amountInvestment;
		}
	// 未払消費税
	public int getUnpaidConsumptionTax(){
		return unpaidConsumptionTax;
		}
	// 消費税総額
	public long getConsumptionTax(){
		return consumptionTax;
		}
	// 保有現預金
	public long getMoney(){
		return money;
		}
	// 商品リスト
	public Map<String,Integer> getProductMap(){
		return productMap;
		}
	// id
	public int getID(){
		return id;
		}
	public String getName(){
		return "producers["+id+"]";
		}
	}
