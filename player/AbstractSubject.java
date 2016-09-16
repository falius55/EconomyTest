package economy.player;

import java.util.List;
import java.util.ArrayList;

import economy.player.Subject;
import economy.player.Bank;
import economy.account.DebtMediator;
import economy.market.MarketInfomation;

/**
 * Subjectインターフェースの骨格実装クラス
 */
abstract public class AbstractSubject implements Subject {
	private Account account;
	private Bank mainBank;

	private List<DebtMediator> debtList; // 借金のリスト
	private List<DebtMediator> claimList; // 貸金のリスト

	public AbstractSubject(Account account) {
		this.account = account;
		debtList = new ArrayList<DebtMediator>();
		claimList = new ArrayList<DebtMediator>();
	}
	/**
	 * 貯金する
	 * 対象はメインバンク
	 * 銀行が実行すると中央銀行に預ける
	 * 中央銀行が実行すると、お金が市場から消える
	 */
	@Override // TODO:中央銀行はさらにオーバーライド
		public Subject saveMoney(int amount) {
			account.saveMoney(amount);
			mainBank.keep(amount);
			return this;
		}

	/**
	 * お金をおろす
	 * 対象はメインバンク
	 * 銀行が実行すると中央銀行からおろす
	 * 中央銀行が実行すると、新たなお金を作成する
	 */
	@Override
	Subject downMoney(int amount) {
		account.downMoney(amount);
		mainBank.paidOut(amount);
		return this;
	}

	/**
	 * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
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
	 * 借金の申し込むを受け入れ、お金を貸す
	 * @return 貸した金額
	 */
	public int acceptDebt(DebtMediator debt) {
		claimList.add(debt);
		debt.accepted(account, MarketInfomation.INSTANCE.nowDate());
		return debt.amount();
	}
}
