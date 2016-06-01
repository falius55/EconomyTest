import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

/*
	<applet code="EconomyTest" width=500 height=150></Applet>
 */

/*
 * class EconomyTest
 * createCompornent()
 * class ChoiceItemListener
 * class ButtunActionListener
 * jump()
 * run()
 * actBuy()
 * actInvest()
 * selectTable()
 * selectGraph()
 * startAuto()
 * stopAuto()
 * putSet()
 * clearSet()
 * setSample()
 * newData()
 * addData()
 * nextDay()
 * setAccount()
 * printSet()
 * actionAuto()
 * movement()
 * createInstance()
 * getSumMapData()
 * printResult()
 * printResult(TextArea)
 */

// 市場の結果を出力するクラス
public class EconomyTest extends Applet{
	// インスタンス
	private static GregorianCalendar cal;
	private static Human[] humans;
	private static Producer[] producers;
	private static PrivateBank bank;
	private static Gorvement nation;
	private static Auto t;
	private static java.util.List<Subject> setNameList;
	private static java.util.List<String> setBuyList;

	private static java.util.List<Map<String,Object>> dataList;
	private static int printFlag = 0;
	private static boolean appletFlag = false;

	// public void init(){
	// 	appletFlag = true;
	// 	// 初期化
	// 	createInstance();
	// 	// awt
	// 	createCompornent(false);
	// 	printResult(textArea);
	// 	}

	public void start(){
	}
	public static void main(String args[]){
		// 初期化
		createInstance();

		// 日中活動
		movement(args); // シミュレートする日数

		// 結果表示
		printResult();

		// awt
		createCompornent(false);
		printResult(textArea);

	}


	///////////////////////////////////////////////////////////////////////////////
	// AWT
	///////////////////////////////////////////////////////////////////////////////
	private static Frame frm;
	private static Panel pnl1;
	private static Panel pnl2;
	private static Panel pnl3;
	private static Panel pnl4;
	private static Panel pnl5;
	private static Panel pnl6;
	private static Panel pnl7;
	private static Panel pnl8;
	private static Panel pnl9;
	private static Panel pnl10;
	private static Panel pnl11;
	private static Panel pnl12;
	private static Panel pnl13;
	private static Panel pnl14;
	private static Label lblDate;
	private static TextArea textArea;
	private static TextArea setArea;
	private static TextArea logArea;
	private static TextField numText;
	private static Choice choiceHuman;
	private static Choice choiceProducer;
	private static Choice choiceBuyName;
	private static Choice choiceInvestName;
	private static Choice choiceStore;
	private static Choice choiceStore2;
	private static Choice choiceGraph;
	private static Button btnBuy;
	private static Button btnInvest;
	private static Button btnStart;
	private static Button btnStop;
	private static Button btnNext;
	private static Button btnClose;
	private static Button btnSelectMicro;
	private static Button btnSelectMacro;
	private static Button btnRight;
	private static Button btnLeft;
	private static Button btnTable;
	private static Button btnGraph;
	private static Button btnJump;
	private static Button btnSetHuman;
	private static Button btnSetProducer;
	private static Button btnSetClear;
	private static Button btnSampleSet;
	private static Human selectHuman;
	private static Producer selectProducer;
	private static Graph graph;

	public static void createCompornent(boolean bl){

		// if(bl){
		// Scanner sc = new Scanner(System.in);
		// System.out.println("\nstart awt?(y/n)");
		// if(!(sc.next().equals("y"))){
		// 	return;
		// }
		// }

		selectHuman = humans[0];
		selectProducer = producers[0];
		//////////////////////////
		// create compornent
		//////////////////////////

		// create Frame
		frm = new Frame("Economy Test");

		// set layout
		frm.setLayout(new BorderLayout());

		// set frame size
		frm.setSize(new Dimension(850,1000));

		// create label
		lblDate = new Label(MultiTask.getStringCalendar(cal));

		// create textarea
		textArea = new TextArea();
		setArea = new TextArea();
		logArea = new TextArea();
		logArea.append(MultiTask.getStringCalendar(cal)+"\n");

		// create textfield
		numText = new TextField("0",8);

		// create choice
		choiceHuman = new Choice();
		choiceProducer = new Choice();
		choiceBuyName = new Choice();
		choiceInvestName = new Choice();
		choiceStore = new Choice();
		choiceStore2 = new Choice();
		choiceGraph = new Choice();
		// choice addItem
		for(int i=0;i<humans.length;i++){
			choiceHuman.addItem("humans["+i+"]");
			choiceProducer.addItem(producers[i].getIndustries());
			choiceStore.addItem(producers[i].getIndustries());
			choiceStore2.addItem(producers[i].getIndustries());
			// for(String key:producers[i].getProductMap().keySet()){
			// 	choiceInvestName.addItem(key);
			// 	}
		}
		for(String key:producers[0].getProductMap().keySet()){
			choiceBuyName.addItem(key);
			choiceInvestName.addItem(key);
		}
		for(int i=0;i<printDataTitle.length;i++){
			choiceGraph.addItem(printDataTitle[i]);
		}

		// create button
		btnBuy = new Button("actBuy");
		btnInvest = new Button("actInvest");
		btnStart = new Button("start");
		btnStop = new Button("stop");
		btnNext = new Button("next");
		btnClose = new Button("close");
		btnSelectMicro = new Button("Micro");
		btnSelectMacro = new Button("Macro");
		btnRight = new Button("-->");
		btnLeft = new Button("<--");
		btnTable = new Button("Table");
		btnGraph = new Button("Graph");
		btnJump = new Button("Jump");
		btnSetHuman = new Button("set human");
		btnSetProducer = new Button("set producer");
		btnSetClear = new Button("clear set");
		btnSampleSet = new Button("sample set");

		// create graph
		graph = new Graph(dataList,logArea);
		graph.setKind(printDataTitle[0]);

		/////////////////////////////////////////////
		// set listener
		/////////////////////////////////////////////
		// choice
		ChoiceItemListener cil = new ChoiceItemListener();
		choiceHuman.addItemListener(cil);
		choiceProducer.addItemListener(cil);
		choiceStore.addItemListener(cil);
		choiceStore2.addItemListener(cil);
		choiceGraph.addItemListener(cil);

		// button
		btnClose.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
				try{
				System.exit(0);
				}catch(Exception ex){}
				}
				});
		ButtonActionListener btListener = new ButtonActionListener();
		btnBuy.addActionListener(btListener);
		btnInvest.addActionListener(btListener);
		btnStart.addActionListener(btListener);
		btnStop.addActionListener(btListener);
		btnNext.addActionListener(btListener);
		btnSelectMicro.addActionListener(btListener);
		btnSelectMacro.addActionListener(btListener);
		btnRight.addActionListener(btListener);
		btnLeft.addActionListener(btListener);
		btnTable.addActionListener(btListener);
		btnGraph.addActionListener(btListener);
		btnJump.addActionListener(btListener);
		btnSetHuman.addActionListener(btListener);
		btnSetProducer.addActionListener(btListener);
		btnSetClear.addActionListener(btListener);
		btnSampleSet.addActionListener(btListener);

		////////////////////////////////////////////////////
		// set panel
		////////////////////////////////////////////////////
		Button btnDummy1 = new Button();
		Button btnDummy2 = new Button();
		Button btnDummy3 = new Button();
		Button btnDummy4 = new Button();
		// set layout to panel
		pnl1 = new Panel(new GridLayout(5,3));

		pnl1.add(choiceHuman);
		pnl1.add(choiceProducer);
		pnl1.add(btnNext);
		pnl1.add(choiceStore);
		pnl1.add(choiceStore2);
		pnl1.add(btnStart);
		pnl1.add(choiceBuyName);
		pnl1.add(choiceInvestName);
		pnl1.add(btnStop);
		pnl1.add(btnSetHuman);
		pnl1.add(btnSetProducer);
		pnl1.add(btnDummy2);
		pnl1.add(btnBuy);
		pnl1.add(btnInvest);
		pnl1.add(btnClose);

		// frm	-lblDate
		//			-pnl12	-pnl13	-numText
		//									-btnJump
		//						-pnl1(btn)
		//			-pnl2	-pnl7		-pnl11	-lblLog
		//											-logArea
		//								-pnl10	-pnl14	-lblSet
		//														-btnSetClear
		//											-setArea
		//					-pnl8		-pnl9(btnTable,btnGraph)
		//								-pnl3	-pnl4(micro,macro) or -pnl5	-pnl6(right,choiceGraph,left) 
		//										-textArea         				-graph 				

		pnl2 = new Panel(new GridLayout(1,2));
		pnl3 = new Panel(new BorderLayout());
		pnl4 = new Panel(new FlowLayout());
		pnl5 = new Panel(new BorderLayout());
		pnl6 = new Panel(new FlowLayout());
		pnl7 = new Panel(new GridLayout(2,1));
		pnl8 = new Panel(new BorderLayout());
		pnl9 = new Panel(new FlowLayout());
		pnl10 = new Panel(new BorderLayout());
		pnl11 = new Panel(new BorderLayout());
		pnl12 = new Panel(new BorderLayout());
		pnl13 = new Panel(new FlowLayout());
		pnl14 = new Panel(new FlowLayout());
		Label lblSet = new Label("Set");
		Label lblLog = new Label("Log");
		pnl14.add(lblSet);
		pnl14.add(btnSampleSet);
		pnl14.add(btnSetClear);
		pnl10.add(pnl14,BorderLayout.NORTH);
		pnl10.add(setArea,BorderLayout.CENTER);
		pnl11.add(lblLog,BorderLayout.NORTH);
		pnl11.add(logArea,BorderLayout.CENTER);
		pnl7.add(pnl11);
		pnl7.add(pnl10);
		pnl6.add(btnLeft);
		pnl6.add(choiceGraph);
		pnl6.add(btnRight);
		pnl5.add(pnl6,BorderLayout.NORTH);
		pnl5.add(graph,BorderLayout.CENTER);
		pnl4.add(btnSelectMicro);
		pnl4.add(btnSelectMacro);
		pnl3.add(pnl4,BorderLayout.NORTH);
		pnl3.add(textArea,BorderLayout.CENTER);
		pnl9.add(btnTable);
		pnl9.add(btnGraph);
		pnl8.add(pnl3,BorderLayout.CENTER);
		pnl8.add(pnl9,BorderLayout.SOUTH);
		pnl2.add(pnl8);
		pnl2.add(pnl7);
		pnl13.add(numText);
		pnl13.add(btnJump);
		pnl12.add(pnl13,BorderLayout.NORTH);
		pnl12.add(pnl1);

		btnDummy1.setVisible(false);
		btnDummy2.setVisible(false);
		btnDummy3.setVisible(false);
		btnDummy4.setVisible(false);
		btnClose.setVisible(!appletFlag);
		textArea.setEditable(false);
		setArea.setEditable(false);
		logArea.setEditable(false);
		////////////////////////////////////////////////////////////
		// set frame
		////////////////////////////////////////////////////////////
		frm.add(lblDate, BorderLayout.NORTH);
		frm.add(pnl2, BorderLayout.CENTER);
		frm.add(pnl12, BorderLayout.EAST);

		/////////////////////////////////////////////////////////////
		// apearance frame
		/////////////////////////////////////////////////////////////
		frm.setVisible(true);

	}

	/////////////////////////////////////////////////////////////
	// inner class
	/////////////////////////////////////////////////////////////

	static class ChoiceItemListener implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			int i = ((Choice)e.getSource()).getSelectedIndex();
			if((Choice)e.getSource()==choiceHuman){
				// change attention instance
				selectHuman = humans[i];
			}else if((Choice)e.getSource()==choiceProducer){
				// choiceProducer is same with choiceHuman
				selectProducer = producers[i];
			}else if((Choice)e.getSource()==choiceStore){
				// change attention store
				choiceBuyName.removeAll();
				// change selectable item list
				for(String key:producers[i].getProductMap().keySet()){
					choiceBuyName.addItem(key);
				}
			}else if((Choice)e.getSource()==choiceStore2){
				// choiceStore2 is same choiceStore
				choiceInvestName.removeAll();
				for(String key:producers[i].getProductMap().keySet()){
					choiceInvestName.addItem(key);
				}
			}else if((Choice)e.getSource()==choiceGraph){
				// kind of graph
				graph.setKind(printDataTitle[i]);
			}else{
				System.out.println("ChoiceItemListener err!");
			}
		}
	}
	static class ButtonActionListener implements ActionListener, Runnable{
		public void actionPerformed(ActionEvent e){
			String str;
			String btnLabel = ((Button)e.getSource()).getLabel();
			switch(btnLabel){
				case "actBuy":
					actBuy();
					break;
				case "actInvest":
					actInvest();
					break;
					// table↓
				case "Micro":
					printFlag = MICRO_DATA;
					printResult(textArea);
					break;
				case "Macro":
					printFlag = MACRO_DATA;
					printResult(textArea);
					break;
					// graph↓
				case "-->":
					graph.setNext(25);
					break;
				case "<--":
					graph.setPrev(25);
					break;
					// select Table and graph
				case "Table":
					selectTable();
					break;
				case "Graph":
					selectGraph();
					break;
					// nextday
				case "next":
					nextDay(true);
					break;
					// auto↓
				case "start":
					startAuto();
					break;
				case "stop":
					stopAuto();
					break;
				case "Jump":
					jump(); // in own method
					break;
				case "set human":
					putSet(selectHuman,choiceBuyName.getSelectedItem());
					break;
				case "set producer":
					putSet(selectProducer,choiceInvestName.getSelectedItem());
					break;
				case "clear set":
					clearSet();
					break;
				case "sample set":
					setSample();
					break;
				default:
					System.out.println("ButtonActionListener err!");
					break;
			}
		}

		// push jump button
		public void jump(){
			// stop auto thread
			try{
				if(t!=null && t.isAlive()){
					t.stopRunning();
					t.join();
				}
			}catch(Exception ex){
				System.err.println(ex);
				}
			// start own thread(include non wait time auto method and controll with jumpday choice)
			(new Thread(this)).start();
		}
		public void run(){
			int jumpDay = 0;
			// get junpday num
			try{
				jumpDay = Integer.parseInt(numText.getText());
			}catch(Exception ex){
				logArea.append("set number err!");
				return;
			}
			// wait time is not exist
			for(int i=0;i<jumpDay;i++){
				actionAuto(0);
			}
			logArea.append(jumpDay + "日経過しました。\n");
			logArea.append(MultiTask.getStringCalendar(cal)+"\n");
		}
	}

	static public void actBuy(){
		String str = choiceBuyName.getSelectedItem();
		logArea.append(choiceHuman.getSelectedItem() + "が" + str + "を購入しました。\n");
		logArea.append(str + ":" + selectHuman.actBuy(str) + "\n");
		newData(true);
	}
	static public void actInvest(){
		String str = choiceInvestName.getSelectedItem();
		logArea.append(choiceProducer.getSelectedItem() + "が" + str + "に投資しました。\n");
		logArea.append(str + ":" + selectProducer.invest(str) + "\n");
		newData(true);
	}
	static public void selectTable(){
		pnl8.remove(0);
		pnl8.add(pnl3,0);
		pnl8.doLayout();
		pnl3.doLayout();
		pnl4.doLayout();
		textArea.repaint();
	}
	static public void selectGraph(){
		pnl8.remove(0);
		pnl8.add(pnl5,0);
		pnl8.doLayout();
		pnl5.doLayout();
		pnl6.doLayout();
		graph.repaint();
	}
	// start auto class extends thread
	static public void startAuto(){
		if(t==null || !(t.isAlive())){
			t = new Auto(); // include wait time 500ms auto method and controll with runningflag(t.stoprunning())
			t.start();
		}
	}
	static public void stopAuto(){
		try{
			t.stopRunning();
		}catch(Exception ex){
			System.err.println(ex);
		}
	}
	static public void putSet(Subject instance,String strWont){
		// Subejct is include human, producer, and nation
		//					and include getName()
		setNameList.add(instance);
		setBuyList.add(strWont);
		setArea.append(instance.getName()+":"+strWont+"\n");
	}
	static public void clearSet(){
		setNameList.clear();
		setBuyList.clear();
		setArea.setText("");
	}

	static public void setSample(){
		clearSet();
		for(int j = 0;j < 10;j++){
			// 全員が毎日買うもの
			putSet(humans[j],"パン");
			putSet(humans[j],"サラダ");
			putSet(humans[j],"おにぎり");
			putSet(humans[j],"お米");
			putSet(humans[j],"インスタントラーメン");
			putSet(humans[j],"電気");
			putSet(humans[j],"水道");
			putSet(humans[j],"ガス");
		}
		// 毎日買う
		putSet(nation,"ペン");
		putSet(nation,"コピー用紙");
		putSet(nation,"米5kg");
		putSet(nation,"土木工事");
		putSet(nation,"住民票発行契約");
		putSet(humans[0],"ラーメン");
		putSet(humans[0],"ハンバーグ");
		putSet(humans[0],"文庫本");
		putSet(humans[0],"雑誌");
		putSet(humans[0],"インク");
		putSet(humans[1],"ラーメン");
		putSet(humans[1],"ワイン");
		putSet(humans[1],"ワイン");
		putSet(humans[1],"ハードカバー");
		putSet(humans[1],"ハンバーグ");
		putSet(humans[2],"ラーメン");
		putSet(humans[2],"清涼飲料水");
		putSet(humans[2],"菓子パン");
		putSet(humans[2],"ハードカバー");
		putSet(humans[2],"日本酒");
		putSet(humans[2],"インク");
		putSet(humans[3],"ラーメン");
		putSet(humans[3],"ビール");
		putSet(humans[3],"ワイン");
		putSet(humans[3],"文庫本");
		putSet(humans[4],"ハンバーグ");
		putSet(humans[4],"文庫本");
		putSet(humans[4],"日本酒");
		putSet(humans[5],"ハンバーグ");
		putSet(humans[5],"清涼飲料水");
		putSet(humans[5],"菓子パン");
		putSet(humans[5],"カレーライス");
		putSet(humans[6],"ハンバーグ");
		putSet(humans[7],"ハンバーグ");
		putSet(humans[7],"ハードカバー");
		putSet(humans[7],"清涼飲料水");
		putSet(humans[8],"ハンバーグ");
		putSet(humans[9],"ラーメン");
	}

	// only new not add
	// true:onAWT false:nonAWT
	synchronized public static void newData(boolean bl){
		if(dataList.size()!=0){
			dataList.remove(dataList.size()-1);
			dataList.add(getSumMapData());
		}
		if(bl){
			graph.newData(getSumMapData());
			printResult(textArea);
			graph.repaint();
		}
	}
	// new after add to dataList
	synchronized public static void addData(boolean bl){
		dataList.add(getSumMapData());
		if(bl) graph.addData(getSumMapData());

		newData(bl);
	}
	synchronized public static void nextDay(boolean bl){
		// save value of end of the day
		addData(bl);
		// 日付を加算
		cal.add(Calendar.DATE,1);

		// 日付を表示
		MultiTask.printCalendar(cal);
		if(bl == true){
			lblDate.setText(MultiTask.getStringCalendar(cal));
			logArea.append(MultiTask.getStringCalendar(cal)+"\n");
		}

		setAccount();

		newData(bl);
	}
	public static void setAccount(){
		for(int i = 0;i < 10;i++){
			// 国民の金銭管理
			// 求職活動
			// 借金の返済
			// 給料、貯金を下ろす
			humans[i].setSettlement(cal);
			// 生産者の基本活動
			// 決算
			producers[i].accountSettlement(cal);
		}
		// 銀行決算
		bank.accountSettlement(cal);
	}
	// renew set area
	public static void printSet(){
		setArea.setText("");
		for(int i=0;i<setNameList.size();i++){
			setArea.append(setNameList.get(i)+":"+setBuyList.get(i)+"\n");
		}
	}
	synchronized public static void actionAuto(int wait){
		for(int i=0;i<setNameList.size();i++){
			if(setNameList.get(i) instanceof Gorvement){
				((Gorvement)setNameList.get(i)).actBuy(setBuyList.get(i));
				if(wait!=0) logArea.append("nationが" + setBuyList.get(i) + "を購入しました。\n");
			}else if(setNameList.get(i) instanceof Human){
				((Human)setNameList.get(i)).actBuy(setBuyList.get(i));
				if(wait!=0) logArea.append("humans["+((Human)setNameList.get(i)).getID() + "]が" + setBuyList.get(i) + "を購入しました。\n");
			}else if(setNameList.get(i) instanceof Producer){
				((Producer)setNameList.get(i)).invest(setBuyList.get(i));
				if(wait!=0) logArea.append("producers["+((Producer)setNameList.get(i)).getID() + "]が" + setBuyList.get(i) + "に投資しました。\n");
			}else{
			}
		}
		try{
			Thread.sleep(wait);
		}catch(Exception e){
			logArea.append("actionAuto() err!");
		}
		if(wait==0){
			// save value of end of the day
			addData(true);
			// 日付を加算
			cal.add(Calendar.DATE,1);
			// 日付を表示
			lblDate.setText(MultiTask.getStringCalendar(cal));
			System.out.println("in wait0if!");
			setAccount();
			newData(true);
		}else{
			nextDay(true);
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	// nonAWT
	///////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////
	// 毎日の活動
	///////////////////////////////////////////////////////////////////////////////
	public static void movement(String[] strDays){
		if(strDays.length == 0) return;
		int days = Integer.parseInt(strDays[0]);
		// 日数分繰り返す
		// ブロック内は一日の取引
		for(int i=0;i<days;i++){
			samplePerttern(false);
		}
	}

	// true:AWT
	public static void samplePerttern(boolean bl){
		// 国民の基本活動
		for(int j = 0;j < 10;j++){
			// 全員が毎日買うもの
			humans[j].actBuy("パン");
			humans[j].actBuy("サラダ");
			humans[j].actBuy("おにぎり");
			humans[j].actBuy("お米");
			humans[j].actBuy("インスタントラーメン");
			humans[j].actBuy("電気");
			humans[j].actBuy("水道");
			humans[j].actBuy("ガス");
		}
		// 毎日買う
		nation.actBuy("ペン");
		nation.actBuy("コピー用紙");
		nation.actBuy("米5kg");
		nation.actBuy("土木工事");
		nation.actBuy("住民票発行契約");
		humans[0].actBuy("ラーメン");
		humans[0].actBuy("ハンバーグ");
		humans[0].actBuy("文庫本");
		humans[0].actBuy("雑誌");
		humans[0].actBuy("インク");
		humans[1].actBuy("ラーメン");
		humans[1].actBuy("ワイン");
		humans[1].actBuy("ワイン");
		humans[1].actBuy("ハードカバー");
		humans[1].actBuy("ハンバーグ");
		humans[2].actBuy("ラーメン");
		humans[2].actBuy("清涼飲料水");
		humans[2].actBuy("菓子パン");
		humans[2].actBuy("ハードカバー");
		humans[2].actBuy("日本酒");
		humans[2].actBuy("インク");
		humans[3].actBuy("ラーメン");
		humans[3].actBuy("ビール");
		humans[3].actBuy("ワイン");
		humans[3].actBuy("文庫本");
		humans[4].actBuy("ハンバーグ");
		humans[4].actBuy("文庫本");
		humans[4].actBuy("日本酒");
		humans[5].actBuy("ハンバーグ");
		humans[5].actBuy("清涼飲料水");
		humans[5].actBuy("菓子パン");
		humans[5].actBuy("カレーライス");
		humans[6].actBuy("ハンバーグ");
		humans[7].actBuy("ハンバーグ");
		humans[7].actBuy("ハードカバー");
		humans[7].actBuy("清涼飲料水");
		humans[8].actBuy("ハンバーグ");
		humans[9].actBuy("ラーメン");

		nextDay(bl);
	}


	///////////////////////////////////////////////////////////////////////////////
	// 初期化
	///////////////////////////////////////////////////////////////////////////////

	public static void createInstance(){
		cal = new GregorianCalendar();
		dataList = Collections.synchronizedList(new ArrayList<Map<String,Object>>());
		setNameList = Collections.synchronizedList(new ArrayList<Subject>());
		setBuyList = Collections.synchronizedList(new ArrayList<String>());
		// 経済主体の生成
		humans = new Human[10];
		producers = new Producer[10];
		// 民間銀行
		bank = new PrivateBank();
		for(int i = 0;i < 10;i++){
			producers[i] = new Producer(i);
			bank.setAccount(producers[i]);
		}
		for(int i = 0;i < 10;i++){
			humans[i] = new Human(i,producers);
			producers[i].setInstance(producers);
			bank.setAccount(humans[i]);
		}
		// 中央政府
		nation = new Gorvement(humans,producers);
		bank.setAccount(nation,true);
		// 業種設定
		producers[0].setIndustries("電気量販店");
		producers[1].setIndustries("委託会社");
		producers[2].setIndustries("農家");
		producers[3].setIndustries("加工");
		producers[4].setIndustries("スーパー");
		producers[5].setIndustries("情報");
		producers[6].setIndustries("書店");
		producers[7].setIndustries("飲食店");
		producers[8].setIndustries("コンビニ");
		producers[9].setIndustries("インフラ");

		setAccount();

	}


	///////////////////////////////////////////////////////////////////////////////
	// 結果表示用メソッド
	///////////////////////////////////////////////////////////////////////////////
	public static Map<String,Object> getSumMapData(){
		Map<String,Object> map = new HashMap<String,Object>();
		long mGDP = 0; // 付加価値総額
		long C = 0; // 個人の最終消費支出
		long S = 0; // 貯蓄
		long R = 0; // 利潤
		long T = nation.getTaxation(); // 税額
		long I = 0; // 投資
		long G = nation.getAmountPay(); // 政府支出
		long M = 0; // 貨幣供給量
		long totalUnpaidConsumptionTax = 0;
		long totalY = 0;
		long totalCash = 0;
		long Ys = 0;
		long amountDepreciation = 0;
		long totalConsumptionTax = 0;
		long totalPuttingCorporationTax = 0;
		long totalPuttingConsumptionTax = 0;
		long totalPuttingIncomeTax = 0;
		for(int i=0;i<10;i++){
			map.put("humans["+i+"].mapData",getHumanMapData(humans[i]));
			map.put("producers["+i+"].mapData1",getProducerMapData(i,producers[i]));
			map.put("producers["+i+"].mapData2",getProducerMapData2(i,producers[i]));

			// 手持ち金集計
			totalCash += humans[i].getCash();
			// 総所得集計
			totalY += humans[i].getTotalY();
			C += humans[i].getTotalPay();
			Ys += producers[i].getAmountDepreciation(); // 分配面から見たGDPには固定資本減耗が含まれる。
			amountDepreciation += producers[i].getAmountDepreciation();
			R += producers[i].getBenefit();
			I += producers[i].getAmountInvestment();
			mGDP += producers[i].getValueAdded();
			totalUnpaidConsumptionTax += producers[i].getUnpaidConsumptionTax();
			totalConsumptionTax += producers[i].getConsumptionTax();
			totalPuttingCorporationTax += producers[i].puttingCorporationTax;
			totalPuttingConsumptionTax += producers[i].puttingConsumptionTax;
			totalPuttingIncomeTax += humans[i].puttingIncomeTax;
		}
		Ys += totalY;
		Ys += R; // 法人所得を加算
		Ys += T; // 政府所得を加算
		// Sは生産物のうち消費に回らなかった分のうち民間の取り分
		S = mGDP - C - T;
		M = bank.getDeposit() + totalCash;
		M += nation.exchequer;
		map.put("民間預金残高" , bank.getDeposit()); // balance + money
		map.put("歳入総額" , nation.getRevenue());
		map.put("国庫保有金額" , nation.getExchequer());
		map.put("生産面から見たGDP:Y" , mGDP); // 付加価値総額:totalSales - totalPurchase
		map.put("支出面から見たGDP:Yd:C+I+G" , (C+I+G));
		map.put("分配面から見たGDP:Ys" , Ys); // 国民の所得＋生産者の所得＋政府所得＋固定資本減耗
		map.put("個人総所得" , totalY);
		map.put("法人総所得" , R); // getBenefit() = totalSales - totalCost
		map.put("政府所得" , T);
		map.put("納付済消費税" , totalPuttingConsumptionTax);
		map.put("納付済法人税" , totalPuttingCorporationTax);
		map.put("納付済所得税" , totalPuttingIncomeTax);
		map.put("減価償却累計額" , amountDepreciation);
		map.put("未払消費税" , totalUnpaidConsumptionTax);
		map.put("需要:Y=C+I+G" , (C+I+G));
		map.put("供給:Y=C+S+T" , (C+S+T));
		map.put("C" , C);
		map.put("I" , I);
		map.put("G" , G);
		map.put("T" , T); // 未払消費税は除く(消費税は実際の納税時に加算)
		map.put("S" , S); // GDP - C - T
		map.put("M" , M); // cash + balance + producer.money + nation.exchequer
		map.put("国民純生産:NNP" , (mGDP - amountDepreciation));
		map.put("国民所得" , (mGDP - amountDepreciation - totalConsumptionTax + nation.amountSubsidy));
		map.put("国民所得のGDP比" , (((double)mGDP - amountDepreciation - totalConsumptionTax + nation.amountSubsidy)/mGDP));
		map.put("貸付金総額" , bank.getLoan());
		map.put("銀行保有現預金" , bank.getMoney()); // money + savings
		map.put("銀行保有現金" , bank.getCash());
		map.put("日銀当座預金" , bank.getSavings());
		map.put("銀行必要現金額" , bank.getRequireCapital());
		map.put("公債残高" , nation.getBond());
		map.put("通貨発行量" , CentralBank.getAmountCurrency());
		map.put("流通現金" , (bank.getMoney()+totalCash+nation.exchequer));
		map.put("貨幣乗数" , ((double)M/CentralBank.getAmountCurrency()));
		map.put("putTax" , dataList.size()==0?T:T - (long)dataList.get(dataList.size()-1).get("T"));

		return map;
	}

	private static String[] printDataTitle = new String[]{
		"民間預金残高", // balance + money System.out.println("歳入総額:" + nation.getRevenue());
			"国庫保有金額",
			"生産面から見たGDP:Y", // 付加価値総額:totalSales - totalPurchase
			"支出面から見たGDP:Yd:C+I+G",
			"分配面から見たGDP:Ys", // 国民の所得＋生産者の所得＋政府所得＋固定資本減耗
			"個人総所得",
			"法人総所得", // getBenefit() = totalSales - totalCost
			"政府所得",
			"納付済消費税",
			"納付済法人税",
			"納付済所得税",
			"減価償却累計額",
			"未払消費税",
			"需要:Y=C+I+G",
			"供給:Y=C+S+T",
			"C",
			"I",
			"G",
			"T", // 未払消費税は除く(消費税は実際の納税時に加算)
			"S", // GDP - C - T
			"M", // cash + balance + producer.money + nation.exchequer
			"国民純生産:NNP",
			"国民所得",
			"国民所得のGDP比",
			"貸付金総額",
			"銀行保有現預金", // money + savings
			"銀行保有現金",
			"日銀当座預金",
			"銀行必要現金額",
			"公債残高",
			"通貨発行量",
			"流通現金",
			"貨幣乗数",
	};
	public static void printResult(){
		Map<String,Object> map = getSumMapData();
		java.util.List<Map<String,Object>> humanList = new ArrayList<Map<String,Object>>();
		java.util.List<Map<String,Object>> producerList = new ArrayList<Map<String,Object>>();
		java.util.List<Map<String,Object>> producerList2 = new ArrayList<Map<String,Object>>();
		for(int i=0;i<humans.length;i++){
			// テーブル表示用データ集計
			humanList.add(getHumanMapData(humans[i]));
			producerList.add(getProducerMapData(i,producers[i]));
			producerList2.add(getProducerMapData2(i,producers[i]));
		}
		// Humanのテーブル表示
		MultiTask.printResult(getHumanKeyListData(),humanList);
		// Producerのテーブル表示
		MultiTask.printResult(getProducerKeyListData(),producerList);
		MultiTask.printResult(getProducerKeyListData2(),producerList2);

		for(int i=0;i<printDataTitle.length;i++){
			System.out.println(printDataTitle[i]+":"+map.get(printDataTitle[i]));
		}

	}

	// 国民の表示キーリストを作成する。
	public static java.util.List<String> getHumanKeyListData(){
		java.util.List<String> keyList = new ArrayList<String>();
		keyList.add("名前");
		keyList.add("手持ち");
		keyList.add("貯金");
		keyList.add("仕事");
		keyList.add("借入れ");
		keyList.add("総支出");
		keyList.add("総所得(税抜)");
		keyList.add("支払所得税(総額)");
		keyList.add("年収(額面)");
		return keyList;
	}

	// 国民の表示データマップを作成する。
	public static Map<String,Object> getHumanMapData(Human human){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("名前","humans["+ human.getID() + "]");
		map.put("手持ち",human.getCash());
		map.put("貯金",human.getBalance());
		map.put("仕事",human.getJob());
		map.put("借入れ",human.getArrears());
		map.put("総支出",human.getTotalPay());
		map.put("総所得(税抜)",human.getTotalY());
		map.put("支払所得税(総額)",human.getPuttingIncomeTax());
		map.put("年収(額面)",human.getIncomeOfYear());
		return map;
	}
	// 生産者の表示キーリストを作成する。
	public static java.util.List<String> getProducerKeyListData(){
		java.util.List<String> keyList = new ArrayList<String>();
		keyList.add("名前");
		keyList.add("業種");
		keyList.add("総売上高");
		keyList.add("総費用");
		keyList.add("利益額");
		keyList.add("平均売上高");
		keyList.add("平均費用");
		keyList.add("保有現金");
		keyList.add("未払消費税");
		return keyList;
	}
	// 生産者の表示データマップを作成する。
	public static Map<String,Object> getProducerMapData(int id,Producer producer){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("名前","producers["+ id + "]");
		map.put("業種",producer.getIndustries());
		map.put("総売上高",producer.totalSales);
		map.put("総費用",producer.totalCost);
		map.put("利益額",producer.getBenefit());
		map.put("平均売上高",producer.averageSales);
		map.put("平均費用",producer.averageCost);
		map.put("保有現金",producer.getMoney());
		map.put("未払消費税",producer.getUnpaidConsumptionTax());
		return map;
	}
	// 生産者の表示キーリスト２を作成する。
	public static java.util.List<String> getProducerKeyListData2(){
		java.util.List<String> keyList = new ArrayList<String>();
		keyList.add("名前");
		keyList.add("業種");
		keyList.add("借金");
		keyList.add("減価償却累計額");
		return keyList;
	}
	// 生産者の表示データマップを作成する。
	public static Map<String,Object> getProducerMapData2(int id,Producer producer){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("名前","producers["+ id + "]");
		map.put("業種",producer.getIndustries());
		map.put("借金",producer.getArrears());
		map.put("減価償却累計額",producer.getAmountDepreciation());
		return map;
	}
	//////////////////
	// textarea
	//////////////////
	static int MICRO_DATA = 0;
	static int MACRO_DATA = 1;
	public static void printResult(TextArea textArea){
		if(textArea == null) return;
		textArea.setText("");
		Map<String,Object> map = getSumMapData();

		if(printFlag == MICRO_DATA){
			java.util.List<Map<String,Object>> humanList = new ArrayList<Map<String,Object>>();
			java.util.List<Map<String,Object>> producerList = new ArrayList<Map<String,Object>>();
			java.util.List<Map<String,Object>> producerList2 = new ArrayList<Map<String,Object>>();
			for(int i=0;i<humans.length;i++){
				// テーブル表示用データ集計
				humanList.add(getHumanMapData(humans[i]));
				producerList.add(getProducerMapData(i,producers[i]));
				producerList2.add(getProducerMapData2(i,producers[i]));
			}
			// Humanのテーブル表示
			MultiTask.pPrintResult(textArea,getHumanKeyListData(),humanList);
			// Producerのテーブル表示
			MultiTask.pPrintResult(textArea,getProducerKeyListData(),producerList);
			MultiTask.pPrintResult(textArea,getProducerKeyListData2(),producerList2);
		}else if(printFlag == MACRO_DATA){
			for(int i=0;i<printDataTitle.length;i++){
				textArea.append(printDataTitle[i]+":"+map.get(printDataTitle[i])+"\n");
			}
		}else{
		}
	}


}
