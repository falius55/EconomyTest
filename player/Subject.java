package economy.player;

import economy.account.DebtMediator;

/**
 * 経済主体を表すクラスのインターフェース
 */
public interface Subject {

	/**
	 * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
	 */
	DebtMediator offerDebt(int amount);
	/**
	 * 借金の申し込むを受け入れ、お金を貸す
	 * @return 貸した金額
	 */
	int acceptDebt(DebtMediator debt);

	/**
	 * 借金を返済する
	 */
	void repay(int amount);
	/**
	 * 返済を受ける
	 */
	void repaid(int amount);
}
