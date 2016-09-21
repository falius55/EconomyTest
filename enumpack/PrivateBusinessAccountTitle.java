package economy.enumpack;

import economy.enumpack.AccountType;
import economy.enumpack.AccountTitle;

/**
 * 民間企業の勘定科目
 */
public enum PrivateBusinessAccountTitle implements AccountTitle {
	/** 雑費(費用) */ MISCELLANEOUS_EXPENSE(AccountType.EXPENSE),
	/** 減価償却費(費用) */ DEPRECIATION(AccountType.EXPENSE),
	/** 仕入費用(費用) */ PURCHESES(AccountType.EXPENSE), 
	/** 支払家賃(費用) */ RENT_EXPENSE(AccountType.EXPENSE), 
	/**  給料費用(費用) */ SALARIES_EXPENSE(AccountType.EXPENSE),
	/** 消耗品費(費用) */ SUPPLIES_EXPENSE(AccountType.EXPENSE),
	/** 支払利息(費用) */ INTEREST_EXPENSE(AccountType.EXPENSE),

	/** 売上高(収益) */ SALES(AccountType.REVENUE),
	/** 未収収益(収益) */ ACCRUED_REVENUE(AccountType.REVENUE),
	/** 受取利息(収益) */ RECEIVE_INTEREST(AccountType.REVENUE),

	/** 小口現金(資産) */ CASH(AccountType.ASSETS),
	/** 売掛金(資産) */ RECEIVABLE(AccountType.ASSETS),
	/** 減価償却累計額(資産) 貸方にaddする */ ACCUMULATED_DEPRECIATION(AccountType.ASSETS),
	/** 土地(資産) */ LAND(AccountType.ASSETS),
	/** 貸付金(資産) */ LOANS_RECEIVABLE(AccountType.ASSETS),
	/** 商品(資産) */ MERCHANDISE(AccountType.ASSETS),
	/** 前払費用(資産) */ PREEPAID_EXPENSE(AccountType.ASSETS),
	/** 有形固定資産(資産) */ TANGIBLE_ASSETS(AccountType.ASSETS),
	/** 当座預金(資産) */ CHECKING_ACCOUNTS(AccountType.ASSETS),
	/** 建物(資産) */ BUILDINGS(AccountType.ASSETS),

	/** 買掛金(負債) */ PAYABLE(AccountType.LIABILITIES),
	/** 未払費用(負債) */ ACCRUED_EXPENSE(AccountType.LIABILITIES),
	/** 借入金(負債) */ LOANS_PAYABLE(AccountType.LIABILITIES),

	/** 資本金(資本) */ CAPITAL_STOCK(AccountType.EQUITY);

	private final AccountType type;
	private static final PrivateBusinessAccountTitle defaultItem = CHECKING_ACCOUNTS;

	PrivateBusinessAccountTitle(AccountType type) {
		this.type = type;
	}

	public AccountType type() {
		return this.type;
	}
	public static PrivateBusinessAccountTitle defaultItem() {
		return defaultItem;
	}
}
