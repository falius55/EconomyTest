package economy.account;

import java.time.LocalDate;

import economy.player.Subject;

/**
 * 債権債務関係を管理するクラス
 */
public class DebtMediator {
	private LocalDate accrualDate; // 債権債務発生日
	private LocalDate deadLine; // 期限
	private Account creditorAccount = null; // 債権者の会計
	private Account debtorAccount; // 債務者の会計
	private int amount = 0; // 金額

	public DebtMediator(Account debtorAccount, int amount) {
		this.debtorAccount = debtorAccount;
		this.amount = amount;
	}
	/**
	 * 債務が受け入れられ、債権債務関係が発生する
	 */
	public DebtMediator accepted(Account creditorAccount, LocalDate date) {
		if (creditorAccount != null) throw new IllegalStatementException("債権債務関係はすでに発生しています");
		this.creditorAccount = creditorAccount;
		this.accrualDate = date;
		debtorAccount.borrow(amount());
		creditorAccount.lend(amount());
		return this;
	}

	/**
	 * 債権を譲渡する
	 * @param creditorAccount 新たな債権者の会計
	 */
	public DebtMediator transfer(Account creditorAccount) {
		this.creditorAccount = creditorAccount;
		return this;
	}

	/**
	 * 残高
	 */
	public int amount() {
		return amount;
	}

	/**
	 * 借金を減らす
	 * @return 借金が完済されればtrue
	 */
	public boolean repay(int amount) {
		amount = amount <= this.amount ? amount : this.amount;
		debtorAccount.repay(amount);
		creditorAccount.repaid(amount);
		return this.amount == 0;
	}
}
