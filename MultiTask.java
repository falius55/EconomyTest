import java.io.*;
import java.util.*;
import java.awt.*;
import java.util.regex.*;

// 汎用性の高いメソッドの集まり
class MultiTask{

	// カレンダークラスを表示する
	public static void printCalendar(Calendar cal){
		char youbi[] = {' ','日','月','火','水','木','金','土'};
		String str = "";

		str += cal.get(Calendar.YEAR)			+ "年";
		str += (cal.get(Calendar.MONTH)+1)	+ "月";
		str += cal.get(Calendar.DATE)			+ "日(";
		
		str += youbi[cal.get(Calendar.DAY_OF_WEEK)];
		str += ")";

		System.out.println(str);
		}
	public static String getStringCalendar(Calendar cal){
		char youbi[] = {' ','日','月','火','水','木','金','土'};
		String str = "";

		str += cal.get(Calendar.YEAR)			+ "年";
		str += (cal.get(Calendar.MONTH)+1)	+ "月";
		str += cal.get(Calendar.DATE)			+ "日(";
		
		str += youbi[cal.get(Calendar.DAY_OF_WEEK)];
		str += ")";

		return str;
		}

/////////////////////////////////////////////////////////////////////////////////////////
	// 結果出力
	// MapのListを、表形式で一覧表示する。
	// 縦にlistのインデックス、横にkeyの順番で並ぶ。
	// keyListで、キーの並ぶ順番を示す。
/////////////////////////////////////////////////////////////////////////////////////////
	public static void printResult(java.util.List<String> keyList,java.util.List<Map<String,Object>> list){
		/////////////////////////////////////////////////////////////
		// 各項目の最大長を求める。
		/////////////////////////////////////////////////////////////
		Map<String,Integer> lenMap = new HashMap<String,Integer>();
		// いったん、タイトル長を暫定最大長としてlenMapにputする。
		// すべてのkeyにアクセス
		for(String key : list.get(0).keySet()){
				lenMap.put(key,length(key));
			}
			// すべてのMapにアクセス →　lenMapに入れる暫定最大長を順次更新していく。
			for(int i = 0;i < list.size(); i++){
				// listから操作対象Mapを取り出す。
				Map<String, Object> map = list.get(i);
				for(String key : map.keySet()){ // すべてのkeyにアクセス
					String value = map.get(key).toString();
					// 対象の値が暫定値より長ければlenMapにputする。
					if(length(value) > lenMap.get(key)){
						lenMap.remove(key);
						lenMap.put(key,length(value));
						}
					}
				}
		////////////////////////////////////////////////////////////////
		// データをテーブルの形で出力する。
		////////////////////////////////////////////////////////////////

		// 区切り文字列及びヘッダ文字列を作成する。
		String separator = "+";
		String heder = "|";
		 // すべてのkeyにアクセス。順番が重要なため、MapではなくListを利用。
		for(int i = 0;i<keyList.size();i++){
			separator += padding("",lenMap.get(keyList.get(i)), '-') + "+";
			heder += padding(keyList.get(i),lenMap.get(keyList.get(i))) + "|";
			}
			// 区切り文字列及びヘッダ文字列を出力する。
			System.out.println( separator );
			System.out.println( heder );
		// データを出力する。
		for(int i = 0;i<list.size();i++){
				Map<String, Object> map = list.get(i);
				String data = "|";
				for(int j = 0;j<keyList.size();j++){
					data += padding(map.get(keyList.get(j)).toString(),lenMap.get(keyList.get(j))) + "|";
					}
					System.out.println( data );
			}
			// 区切り文字列を出力する。
			System.out.println( separator );
		}
		//////////////////////
		// textarea
		//////////////////////
		public static void printResult(TextArea textArea,java.util.List<String> keyList,java.util.List<Map<String,Object>> list){
		/////////////////////////////////////////////////////////////
		// 各項目の最大長を求める。
		/////////////////////////////////////////////////////////////
		Map<String,Integer> lenMap = new HashMap<String,Integer>();
		// いったん、タイトル長を暫定最大長としてlenMapにputする。
		// すべてのkeyにアクセス
		for(String key : list.get(0).keySet()){
				lenMap.put(key,length(key));
			}
			// すべてのMapにアクセス →　lenMapに入れる暫定最大長を順次更新していく。
			for(int i = 0;i < list.size(); i++){
				// listから操作対象Mapを取り出す。
				Map<String, Object> map = list.get(i);
				for(String key : map.keySet()){ // すべてのkeyにアクセス
					String value = map.get(key).toString();
					// 対象の値が暫定値より長ければlenMapにputする。
					if(length(value) > lenMap.get(key)){
						lenMap.remove(key);
						lenMap.put(key,length(value));
						}
					}
				}
		////////////////////////////////////////////////////////////////
		// データをテーブルの形で出力する。
		////////////////////////////////////////////////////////////////

		// 区切り文字列及びヘッダ文字列を作成する。
		String separator = "+";
		String heder = "|";
		 // すべてのkeyにアクセス。順番が重要なため、MapではなくListを利用。
		for(int i = 0;i<keyList.size();i++){
			separator += tPadding("",lenMap.get(keyList.get(i)), '-') + "+";
			heder += tPadding(keyList.get(i),lenMap.get(keyList.get(i))) + "|";
			}
			// 区切り文字列及びヘッダ文字列を出力する。
			textArea.append( separator + "\n" );
			textArea.append( heder + "\n" );
		// データを出力する。
		for(int i = 0;i<list.size();i++){
				Map<String, Object> map = list.get(i);
				String data = "|";
				for(int j = 0;j<keyList.size();j++){
					data += tPadding(map.get(keyList.get(j)).toString(),lenMap.get(keyList.get(j))) + "|";
					}
					textArea.append( data + "\n" );
			}
			// 区切り文字列を出力する。
			textArea.append( separator + "\n" );
		}

		public static void pPrintResult(TextArea textArea,java.util.List<String> keyList,java.util.List<Map<String,Object>> list){
		/////////////////////////////////////////////////////////////
		// 各項目の最大長を求める。
		/////////////////////////////////////////////////////////////
		Map<String,Integer> lenMap = new HashMap<String,Integer>();
		// いったん、タイトル長を暫定最大長としてlenMapにputする。
		// すべてのkeyにアクセス
		for(String key : list.get(0).keySet()){
				lenMap.put(key,pLength(key));
			}
			// すべてのMapにアクセス →　lenMapに入れる暫定最大長を順次更新していく。
			for(int i = 0;i < list.size(); i++){
				// listから操作対象Mapを取り出す。
				Map<String, Object> map = list.get(i);
				for(String key : map.keySet()){ // すべてのkeyにアクセス
					String value = map.get(key).toString();
					// 対象の値が暫定値より長ければlenMapにputする。
					if(pLength(value) > lenMap.get(key)){
						lenMap.remove(key);
						lenMap.put(key,pLength(value));
						}
					}
				}
		////////////////////////////////////////////////////////////////
		// データをテーブルの形で出力する。
		////////////////////////////////////////////////////////////////

		// 区切り文字列及びヘッダ文字列を作成する。
		String separator = "+";
		String heder = "|";
		 // すべてのkeyにアクセス。順番が重要なため、MapではなくListを利用。
		for(int i = 0;i<keyList.size();i++){
			separator += pPadding("",lenMap.get(keyList.get(i)), '-') + "+";
			heder += pPadding(keyList.get(i),lenMap.get(keyList.get(i))) + "|";
			}
			// 区切り文字列及びヘッダ文字列を出力する。
			textArea.append( separator + "\n" );
			textArea.append( heder + "\n" );
		// データを出力する。
		for(int i = 0;i<list.size();i++){
				Map<String, Object> map = list.get(i);
				String data = "|";
				for(int j = 0;j<keyList.size();j++){
					data += pPadding(map.get(keyList.get(j)).toString(),lenMap.get(keyList.get(j))) + "|";
					}
					textArea.append( data + "\n" );
			}
			// 区切り文字列を出力する。
			textArea.append( separator + "\n" );
		}
		///////////////////////////////////////////////////////////////////////////
		// 以下、ユーティリティメソッド
		///////////////////////////////////////////////////////////////////////////

		// 文字列のバイト数を取得する。
		private static int length(String str){
			int length = 0;
			// getBytes()では、漢字一文字が3バイト扱いになってしまう……
			for(int i = 0;i<str.length();i++){
				String s = str.substring(i,i+1);
				switch(s.getBytes().length){
						case 1:
						length += 1;
						break;
						case 2:
						length += 2;
						break;
						case 3:
						length += 2;
						break;
						default:
						length += 2;
						break;
					}
				}
			return length;
			}

		// 文字列を空白でパディングする。
		private static String padding(String str, int length){
			return padding(str,length,' ');
			}
		// 文字列を指定した文字でパディングする。
		private static String padding(String str, int length, char padChar){
			int spaceCount = length - length(str);

			StringBuffer buf = new StringBuffer();
			for(int i = 0; i<spaceCount; i++){
				buf.append(padChar);
				}
				return str + buf.toString();
			}
		// 文字列を空白でパディングする。
		private static String tPadding(String str, int length){
			return tPadding(str,length,' ');
			}
		// 文字列を指定した文字でパディングする。
		private static String tPadding(String str, int length, char padChar){
			int spaceCount = length - length(str);

			StringBuffer buf = new StringBuffer();
			for(int i = 0; i<spaceCount; i++){
				buf.append(padChar);
				}
				return str + buf.toString();
			}
		// 文字列を空白でパディングする。
		private static String pPadding(String str, int length){
			return pPadding(str,length,' ');
			}
		// 文字列を指定した文字でパディングする。
		private static String pPadding(String str, int length, char padChar){
			int spaceCount = length - pLength(str);

			StringBuffer buf = new StringBuffer();
			for(int i = 0; i<spaceCount; i++){
				buf.append(padChar);
				}
				return str + buf.toString();
			}

		private static int pLength(String str){
			int length = 0;
			 Pattern pNum = Pattern.compile("[0-9]");
			 Pattern pAlpha = Pattern.compile("[a-zA-Z]");
			 Pattern pKigou = Pattern.compile("[\\[\\]\\(\\)]");
			// getBytes()では、漢字一文字が3バイト扱いになってしまう……
			for(int i = 0;i<str.length();i++){
				String s = str.substring(i,i+1);
				Matcher mNum = pNum.matcher(s);
				Matcher mAlpha = pAlpha.matcher(s);
				Matcher mKigou = pKigou.matcher(s);
				if(mNum.find()){
					length += 2;
					}else if(mAlpha.find()){
						length += 2;
					}else if(mKigou.find()){
						length += 1;
					}else{
						length += 3;
					}
				}
			return length -1;
			}
		public static void printGraph(String arg,java.util.List<Integer> list){
			// listは、添字が、何日目か。値がデータを表す。
			int date = Integer.parseInt(arg);
			// 1行目を出力
			System.out.println("△" + padding("",date));

			Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			// 最大値と最小値を求める。
			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;
			for(int i=0;i<list.size();i++){
				if(list.get(i) > max) max = list.get(i);
				if(list.get(i) < min) min = list.get(i);
				}
			// そのデータを何回目に出力すればいいか
			// mapは、keyが何日目か。valueが出力する順番。
			for(int i=0;i<list.size();i++){
				map.put(i,max-list.get(i));
				}
			// データの出力(1行ずつ)
			for(int i=0,k=0;i<max;i+=10000,k++){
					java.util.List<Integer> putList = new ArrayList<Integer>();
					// 上から順番に出力すべきデータを取り出す。
					// putListの値は何日目か。
				for(int l=0;l<list.size();l++){
					 if(max-list.get(l) <=  (k+1)*10000 && max-list.get(l) > k*10000){
						 putList.add(l);
						 }
					}
					putList.add(date);
					Collections.sort(putList);
					String str = "";
					for(int j=putList.size()-2;j >= 0;j--){
						str = padding("-",putList.get(j+1)-putList.get(j)) + str;
						}
						str = padding("|",putList.get(0)) + str;
					System.out.println(str);
			}
			String str = padding("",date,'_');
			System.out.println(str);
			
			}
}
