package economy.account;

import java.util.Map;
import java.util.EnumMap;

import economy.account.AbstractAccount;

public class PrivateBusinessAccount extends AbstractAccount<PrivateBusinessAccount.Item> {
	// 会計細目
	public enum Item implements AbstractAccount.iItem {
		MISCELLANEOUS_EXPENSE(Type.EXPENSE), // 雑費
		DEPRECIATION(Type.EXPENSE), // 減価償却費
		PURCHESES(Type.EXPENSE), // 仕入費用
		RENT_EXPENSE(Type.EXPENSE), // 支払家賃
		SALARIES_EXPENSE(Type.EXPENSE), // 給料費用
		SALES(Type.REVENUE), // 売上高
		ACCRUED_REVENUE(Type.REVENUE), // 未収収益

		CASH(Type.ASSETS), // 現金
		RECEIVABLE(Type.ASSETS), // 売掛金
		ACCUMULATED_DEPRECIATION(Type.ASSETS), // 減価償却累計額(資産の控除) 貸方にaddする
		LAND(Type.ASSETS), // 土地
		LOANS_RECEIVABLE(Type.ASSETS), // 貸付金
		MERCHANDISE(Type.ASSETS), // 商品
		PREEPAID_EXPENSE(Type.ASSETS), // 前払費用
		SUPPLIES_EXPENSE(Type.ASSETS), // 消耗品費
		TANGIBLE_ASSETS(Type.ASSETS), // 有形固定資産
		CHECKING_ACCOUNTS(Type.ASSETS), // 当座預金
		BUILDINGS(Type.ASSETS), // 建物

		PAYABLE(Type.LIABILITIES), // 買掛金
		ACCRUED_EXPENSE(Type.LIABILITIES), // 未払費用
		LOANS_PAYABLE(Type.LIABILITIES), // 借入金

		CAPITAL_STOCK(Type.EQUITY); // 資本金

		private final Type type;
		private static final Item defaultItem = CHECKING_ACCOUNTS;
		Item(Type type) {
			this.type = type;
		}
		public Type type() {
			return this.type;
		}
		public static Item defaultItem() {
			return defaultItem;
		}
	}

	protected PrivateBusinessAccount() {
		super(Item.class);
	}

	@Override
	public PrivateBusinessAccount merge(Account<Item> account) {
		if (!(account instanceof PrivateBusinessAccount)) throw new IllegalArgumentException();
		return (PrivateBusinessAccount)super.merge(account);
	}
	@Override
	public Item defaultItem() {
		return Item.defaultItem();
	}
	@Override
	public Item[] items() {
		return Item.values();
	}

	@Override
	public PrivateBusinessAccount newInstance() {
		return new PrivateBusinessAccount();
	}

	public static void main(String[] args) {
		Account<PrivateBusinessAccount.Item> account = new PrivateBusinessAccount();
		account.add(PrivateBusinessAccount.Item.SALES, 2000);
		System.out.println(account);
		PrivateBusinessAccount castAccount = (PrivateBusinessAccount)account;
		castAccount.test_fixedAssets();
	}
}
