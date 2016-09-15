package economy.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

/**
 * データを格納し、表を作成するクラス
 * {@code
 * TableBuilder tb = new TableBuilder("名前","性別","年齢");
 * tb.insert("Anna")
 * 	.add("性別", "女")
 * 	.add("年齢", 16);
 * tb.insert("Alex")
 * 	.add("性別", "男")
 * 	.add("年齢", 21);
 * tb.insert("Kai")
 * 	.add(1, "男")
 * 	.add(2, 12);
 * tb.print();
 * }
 */
public class TableBuilder {
	// 行の名前(１列目のデータ)からの、列名からデータへのマップ、へのマップ
	private Map<String, Map<String, Object>> dataMap;
	// 順序を保証するためのリスト
	private List<String> columnTitleList; // 列名のリスト
	private List<String> rowTitleList; // 行タイトルのリスト


	private String firstColumn;

	/**
	 * @param firstColumn 一列目の名前。各行のタイトルになる
	 * @param column 二列目以降各列の名前
	 */
	public TableBuilder(String firstColumn, String... column) {
		dataMap = new HashMap<String, Map<String, Object>>();
		this.firstColumn = firstColumn;
		columnTitleList = new ArrayList<String>();
		columnTitleList.add(firstColumn);
		columnTitleList.addAll(Arrays.asList(column));
		rowTitleList = new ArrayList<String>();
	}

	/**
	 * 指定行へのデータ追加を開始する
	 * @param rowTitleObj 追加行の識別名を文字列表現として持つオブジェクト。初めて挿入する識別名なら自動的に一列目のデータとして挿入される
	 * @return 指定された行へのデータ挿入を請け負う内部クラスのインスタンス
	 */
	public InsertAgency insert(Object rowTitleObj) {
		String rowTitle = rowTitleObj.toString();
		if (dataMap.containsKey(rowTitle)) {
			return new InsertAgency(columnTitleList, dataMap.get(rowTitle));
		} else {
			rowTitleList.add(rowTitle);
			Map<String, Object> newData = new HashMap<String, Object>();
			newData.put(firstColumn, rowTitle);
			dataMap.put(rowTitle, newData);
			return new InsertAgency(columnTitleList, newData);
		}
	}
	/**
	 * 指定行の識別名で指定された列にデータを追加する
	 * 一列目のデータを変更しても、insert(String)に使用するキーは変わらない
	 * @param rowTitleObj 追加行の識別名を文字列表現として持つオブジェクト。初めて挿入する識別名なら自動的に一列目のデータとして挿入される
	 * @param column 追加する列の名前
	 * @param data 追加するデータ
	 * @return 指定された行へのデータ挿入を請け負う内部クラスのインスタンス
	 */
	public InsertAgency add(Object rowTitleObj, String column, Object data) {
		return insert(rowTitleObj).add(column, data);
	}
	/**
	 * 指定行のインデックスで指定された列にデータを追加する
	 * 一列目のデータを変更しても、insert(String)に使用するキーは変わらない
	 * @param rowTitleObj 追加行の識別名を文字列表現として持つオブジェクト。初めて挿入する識別名なら自動的に一列目のデータとして挿入される
	 * @param columnIndex 追加する列のインデックス(０なら一列目)
	 * @param data 追加するデータ
	 * @return 指定された行へのデータ挿入を請け負う内部クラスのインスタンス
	 */
	public InsertAgency add(Object rowTitleObj, int columnIndex, Object data) {
		return insert(rowTitleObj).add(columnIndex, data);
	}

	/**
	 * データ挿入を請け負うクラス
	 */
	public static class InsertAgency {
		private List<String> columnTitles;
		private Map<String, Object> row;

		/**
		 * @param columnTitles 列名のリスト
		 * @param row 追加行の各データのマップ
		 */
		private InsertAgency(List<String> columnTitles, Map<String, Object> row) {
			this.columnTitles = columnTitles;
			this.row = row;
		}

		/**
		 * 指定された列に、データを挿入する。nullが渡された場合は、その列のデータは空であるものとする
		 * @param column 挿入する列の名前
		 * @param data 挿入するデータ。nullが渡されるとデータを空にする
		 * @return このオブジェクトの参照
		 * @throws IllegalArgumentException インスタンス作成時に設定した列名以外の名前の列に挿入しようとした場合
		 */
		public InsertAgency add(String column, Object data) {
			if (!columnTitles.contains(column)) throw new IllegalArgumentException(String.format("列名'%s'はこの表に設定されていません", column));
			row.put(column, data);
			return this;
		}
		/**
		 * 指定されたインデックスの列に、データを挿入する
		 * @param columnIndex 挿入する列のインデックス
		 * @param data 挿入するデータ。nullが渡されるとデータを空にする
		 * @return このオブジェクトの参照
		 * @throws IndexOutOfException 指定されたインデックスの列が設定されていない場合
		 */
		public InsertAgency add(int columnIndex, Object data) {
			return add(columnTitles.get(columnIndex), data);
		}
	}

	/**
	 * テーブルを作成する
	 * @return 作成されたテーブルを一行ずつ格納したリスト
	 */
	public List<String> build() {
		Map<String, Integer> lenMap = columnLenMap();

		// 区切り文字列及びヘッダ文字列を作成する。
		StringBuilder separatorBuf = new StringBuilder("+");
		StringBuilder header = new StringBuilder("|");
		for (String column : columnTitleList) {
			separatorBuf.append(padding("", lenMap.get(column), '-')).append('+');
			header.append(padding(column, lenMap.get(column))).append('|');
		}
		String separator = separatorBuf.toString();

		List<String> table = new ArrayList<String>(); // 結果を格納するリスト
		table.add(separator);
		table.add(header.toString());

		// 各行の文字列を作成する
		for (String rowTitle : rowTitleList) {
			Map<String, Object> row = dataMap.get(rowTitle);
			StringBuilder sbRow = new StringBuilder("|");
			for (String column : columnTitleList) {
				String data = row.get(column) == null ? "" : row.get(column).toString(); // データに明示的にnullを渡された時も想定するのでrow.containsKey(column)では判断できず、直接取り出して確認するしかない
				sbRow.append(padding(data, lenMap.get(column))).append('|');
			}
			table.add(sbRow.toString());
		}
		table.add(separator);
		return table;
	}

	/**
	 * 標準出力に表を出力する
	 * @return このオブジェクトの参照
	 */
	public TableBuilder print() {
		for (String line : build())
			System.out.println(line);
		return this;
	}

	/**
	 * 各列から、その列の最大の長さへのマップを作成する
	 */
	private Map<String, Integer> columnLenMap() {
		Map<String, Integer> lenMap = new HashMap<String, Integer>();
		// 列名の文字数で初期化
		for (String column : columnTitleList)
			lenMap.put(column, length(column));

		// 各行のデータをイテレートし、それぞれ比較して大きい数値で更新していく
		// 結果を入れているマップのキーで、各行の同じ列のデータを取り出す
		for (Map<String, Object> row : dataMap.values())
			row.forEach((String column, Object dataObj) -> {
				String data = dataObj == null ? "" : dataObj.toString(); // 明示的にデータにnullを渡された時を想定
				if (length(data) > lenMap.get(column).intValue())
					lenMap.put(column, length(data));
			});
		return lenMap;
	}
	/**
	 * 文字列の長さを取得する(半角文字の何文字分か)
	 */
	private int length(String str) {
		int length = 0;
		for(int i = 0; i < str.length(); i++) {
			String s = str.substring(i,i+1);
			// 全角文字は３バイトとして出てくるが、長さは半角２文字分
			length += s.getBytes().length == 1 ? 1 : 2;
		}
		return length;
	}
	/**
	 * 空白でパディングする
	 */
	private String padding(String str, int length) {
		return padding(str, length, ' ');
	}
	/**
	 * 文字列の後ろに指定された文字を連結して、指定された長さになるまでパディングします
	 * @param str パディングする文字列
	 * @param length パディング後の長さ
	 * @param padChar パディングに使用する文字
	 */
	private String padding(String str, int length, char padChar) {
		StringBuilder ret = new StringBuilder(str);
		int padCount = length - length(str) + 1;
		for (int i = 0; i < padCount; i++) {
			ret.append(padChar);
		}
		return ret.toString();
	}

	public static void main(String[] arg) {
		TableBuilder tb = new TableBuilder("名前","性別","年齢");
		tb.insert("Anna")
			.add("性別", "女")
			.add("年齢", 16);
		tb.insert("Alex")
			.add("性別", "男")
			.add("年齢", 21);
		tb.insert("Kai")
			.add(1, "男")
			.add(2, 12);
		tb.print();
	}
}
