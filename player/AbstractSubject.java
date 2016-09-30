package economy.player;

import java.util.List;
import java.util.ArrayList;

import economy.player.Subject;
import economy.player.Bank;
import economy.account.Account;
import economy.account.DebtMediator;
import economy.market.MarketInfomation;

/**
 * Subjectインターフェースの骨格実装クラス
 * 会計操作
 */
abstract public class AbstractSubject implements Subject {
	final Account<? extends Enum<?>> account;
	private Bank mainBank;

	private List<DebtMediator> debtList; // 借金のリスト
	private List<DebtMediator> claimList; // 貸金のリスト

	AbstractSubject(Account<? extends Enum<?>> account) {
		this.account = account;
		debtList = new ArrayList<DebtMediator>();
		claimList = new ArrayList<DebtMediator>();
	}
	/**
	 * 貯金します
	 * 対象はメインバンク
	 * 銀行が実行すると中央銀行に預けます
	 * 中央銀行が実行すると、お金が市場から消えます
	 */
	@Override // TODO:中央銀行はさらにオーバーライド
		public Subject saveMoney(int amount) {
			account.saveMoney(amount);
			mainBank.keep(amount);
			return this;
		}

	/**
	 * お金をおろします
	 * 対象はメインバンク
	 * 銀行が実行すると中央銀行からおろします
	 * 中央銀行が実行すると、新たなお金を作成します
	 */
	@Override
	public Subject downMoney(int amount) {
		account.downMoney(amount);
		mainBank.paidOut(amount);
		return this;
	}

	/**
	 * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成します
	 * 借金が不成立の場合は想定外
	 * {@code
	 * 	DebtMediator debt = subject.offerDebt(100000);
	 * 	subject.acceptDebt(debt);
	 * 	}
	 */
	public DebtMediator offerDebt(int amount) {
		DebtMediator debt = new DebtMediator(account, amount);
		debtList.add(debt);
		return debt;
	}
	/**
	 * 借金の申し込むを受け入れ、お金を貸します
	 * @return 貸した金額
	 */
	public int acceptDebt(DebtMediator debt) {
		claimList.add(debt);
		debt.accepted(account, MarketInfomation.INSTANCE.nowDate());
		return debt.amount();
	}

	@Override
	public void payTax(int amount) {
	}
	/**
	 * 借金を返済します
	 * 中央銀行が実行すると、お金が市場から消えます
	 */
	public void repay(int amount) {
	}
	/**
	 * 返済を受けます
	 */
	@Override
	public void repaid(int amount) {
	}
}
