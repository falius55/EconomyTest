package economy.account;

import economy.enumpack.AccountTitle;
import economy.enumpack.AccountType;

/**
 * 会計帳簿を表すインターフェース
 * @param T 科目一覧の列挙型
 */
public interface Account<T extends Enum<T> & AccountTitle> {

	/**
	 * 指定した科目種別の総額を計算する
	 * @param type 科目種別
	 * @return 集計結果
	 */
	int get(AccountType type);

	/**
	 * 指定した勘定科目の金額を返す
	 * @param item 勘定科目
	 * @return 指定した勘定科目の金額
	 */
	int get(T item);

	/**
	 * 引数の会計を、自分の会計に吸収併合する。結婚、合併など
	 */
	Account<T> merge(Account<T> another);

	// 扱っている科目一覧を返す
	T[] items();

	/**
	 * 指定がないときに増減させる標準資産科目を返す。通常は現金を想定するが、サブタイプごとに定義する
	 * @return 標準資産科目
	 */
	T defaultItem();

	/**
	 * 標準資産科目(defaultItem()によって定義)を相手科目として、指定された科目を増加させる
	 * @param item 勘定科目
	 * @param amount 金額
	 * @throws IllegalArgumentException サブタイプで定義した標準科目が資産科目でない場合
	 */
	void add(T item, int amount);
}
