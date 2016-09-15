package economy.player;

import economy.player.Subject;
import economy.player.Bank;
import economy.account.DebtMediator;
import economy.market.MarketInfomation;

abstract public class AbstractSubject implements Subject {
		Account account;
		Bank mainBank;

		List<DebtMediator> debtList; // 借金のリスト
		List<DebtMediator> claimList; // 貸金のリスト

		public AbstractSubject() {
			debtList = new ArrayList<DebtMediator>();
			claimList = new ArrayList<DebtMediator>();
		}

	/**
	 * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
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
