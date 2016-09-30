package economy.account;

import economy.enumpack.AccountTitle;
import economy.enumpack.AccountType;

/**
 * 会計帳簿を表すインターフェース
 * @param T 科目一覧の列挙型
 */
public interface Account<T extends Enum<T> & AccountTitle> {
	/**
	 * 引数の会計を、自分の会計に吸収併合する。結婚、合併など
	 */
	Account<T> merge(Account<T> another);

	/**
	 * 扱っている科目一覧を返します
	 */
	T[] items();

	/**
	 * 指定がないときに増減させる標準資産科目を返します。通常は現金を想定していますが、サブタイプごとに定義してください
	 * @return 標準資産科目
	 */
	T defaultItem();

	/**
	 * お金を銀行に預けた時の処理を行います
	 */
	Account<T> saveMoney(int amount);

	/**
	 * お金をおろした時の処理を行います
	 */
	Account<T> downMoney(int amount);
	/**
	 * 借金処理を行います
	 */
	Account<T> borrow(int amount);
	/**
	 * 返済処理を行います
	 */
	Account<T> repay(int amount);
	/**
	 * 貸金処理を行います
	 */
	Account<T> lend(int amount);
	/**
	 * 返済を受けた時の処理を行います
	 */
	Account<T> repaid(int amount);

	default <E extends AccountTitle> Account<T> saleBy(E item, int amount) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 納税処理を行います
	 * 公的機関ではサポートされません
	 * @throws UnssuportedOperationException 公的機関の会計で実行された場合
	 */
	Account<T> payTax(int amount);

	/**
	 * 指定した科目種別の総額を集計します
	 * @param type 科目種別
	 * @return 集計結果
	 */
	int get(AccountType type);
	/**
	 * 指定した勘定科目の金額を返します
	 * @param item 勘定科目
	 * @return 指定した勘定科目の金額
	 */
	int get(T item);
}
