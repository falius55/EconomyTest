package economy.enumpack;

/**
 * 科目種別(費用、収益、資産、負債、資本)
 */
public enum AccountType {
	/** 費用 */ EXPENSE(RL.LEFT),
	/** 収益 */ REVENUE(RL.RIGHT),
	/** 資産 */ ASSETS(RL.LEFT),
	/** 負債 */ LIABILITIES(RL.RIGHT),
	/** 資本 */ EQUITY(RL.RIGHT);
	private final RL rl; // 貸借対照表、損益計算書で左右どちらに表記されるか(借方科目か貸方科目か)
	AccountType(RL rl) {
		this.rl = rl;
	}
	public RL rl() {
		return this.rl;
	}

	/**
	 * 貸借を表す列挙型
	 */
	public enum RL {
		RIGHT,LEFT;
		public RL inverse() {
			switch (this) {
				case RIGHT: return RL.LEFT;
				case LEFT: return RL.RIGHT;
				default: throw new AssertionError("Unknown rl: "+ this);
			}
		}
	}
}
