package economy.player;

import economy.account.DebtMediator;

/**
 * 経済主体を表すクラスのインターフェース
 */
public interface Subject {

	/**
	 * 貯金する
	 * 対象はメインバンク
	 * 銀行が実行すると中央銀行に預ける
	 * 中央銀行ではサポートされません
	 */
	Subject saveMoney(int amount);

	/**
	 * お金をおろす
	 * 対象はメインバンク
	 * 銀行が実行すると中央銀行からおろす
	 * 中央銀行ではサポートされません
	 */
	Subject downMoney(int amount);

	/**
	 * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
	 * 政府から実行された場合は公債を発行します
	 * 銀行から実行された場合は、コールナイトオーバー物で一時的な借受を申し込みます
	 * 中央銀行から実行されると、お金を作成して負債として計上します
	 */
	DebtMediator offerDebt(int amount);
	/**
	 * 借金の申し込むを受け入れ、お金を貸す
	 * @return 貸した金額
	 */
	int acceptDebt(DebtMediator debt);

	/**
	 * 借金を返済する
	 * 中央銀行が実行すると、お金が市場から消える
	 */
	void repay(int amount);
	/**
	 * 返済を受ける
	 */
	void repaid(int amount);

	/**
	 * 納税します
	 * 公的機関ではサポートされません
	 */
	void payTax(int amount);
}
