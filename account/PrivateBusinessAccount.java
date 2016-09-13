package economy.account;

import java.util.Map;
import java.util.EnumMap;
import java.util.Set;
import java.util.EnumSet;
import java.time.LocalDate;

import economy.account.AbstractAccount;
import economy.Product;

public class PrivateBusinessAccount extends AbstractAccount<PrivateBusinessAccount.Item> {
	// 会計細目
	public enum Item implements AbstractAccount.iItem {
		/** 雑費(費用) */ MISCELLANEOUS_EXPENSE(Type.EXPENSE), // 雑費
		/** 減価償却費(費用) */ DEPRECIATION(Type.EXPENSE),
		/** 仕入費用(費用) */ PURCHESES(Type.EXPENSE), 
		/** 支払家賃(費用) */ RENT_EXPENSE(Type.EXPENSE), 
		/**  給料費用(費用) */ SALARIES_EXPENSE(Type.EXPENSE),
		/** 消耗品費(資産) */ SUPPLIES_EXPENSE(Type.EXPENSE),

		/** 売上高(収益) */ SALES(Type.REVENUE),
		/** 未収収益(収益) */ ACCRUED_REVENUE(Type.REVENUE),

		/** 現金(資産) */ CASH(Type.ASSETS),
		/** 売掛金(資産) */ RECEIVABLE(Type.ASSETS),
		/** 減価償却累計額(資産) 貸方にaddする */ ACCUMULATED_DEPRECIATION(Type.ASSETS),
		/** 土地(資産) */ LAND(Type.ASSETS),
		/** 貸付金(資産) */ LOANS_RECEIVABLE(Type.ASSETS),
		/** 商品(資産) */ MERCHANDISE(Type.ASSETS),
		/** 前払費用(資産) */ PREEPAID_EXPENSE(Type.ASSETS),
		/** 有形固定資産(資産) */ TANGIBLE_ASSETS(Type.ASSETS),
		/** 当座預金(資産) */ CHECKING_ACCOUNTS(Type.ASSETS),
		/** 建物(資産) */ BUILDINGS(Type.ASSETS),

		/** 買掛金(負債) */ PAYABLE(Type.LIABILITIES),
		/** 未払費用(負債) */ ACCRUED_EXPENSE(Type.LIABILITIES),
		/** 借入金(負債) */ LOANS_PAYABLE(Type.LIABILITIES),

		/** 資本金(資本) */ CAPITAL_STOCK(Type.EQUITY);

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

	private Set<Product> products; // 取扱商品の集合
	private Set<Product> materials; // 使用する原材料の集合

	private PrivateBusinessAccount(Set<Product> products, Set<Product> materials) {
		super(Item.class);
		this.products = products;
		this.materials = materials;
	}
	/**
	 * @param products 取扱商品の集合
	 * @param materials 利用する原材料の種類
	 */
	public static PrivateBusinessAccount newInstance(Set<Product> products, Set<Product> materials) {
		return new PrivateBusinessAccount(products, materials);
	}
	public static PrivateBusinessAccount newInstance(Set<Product> products) {
		Set<Product> materials = EnumSet.noneOf(Product.class);
		for (Product pd : products) {
			materials.addAll(pd.materials());
		}
		return newInstance(products, materials);
	}
	public static PrivateBusinessAccount newInstance(Industry industry) {
		return newInstance(industry.products());
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

	/**
	 * 売り上げる。売上金は当座預金への振込で受け取り
	 */
	public PrivateBusinessAccount saleToAccount(int mount) {
		addLeft(Item.CHECKING_ACCOUNTS, mount);
		addRight(Item.SALES, mount);
		return this;
	}
	// 現金受け取り
	public PrivateBusinessAccount saleByCash(int mount) {
		addLeft(Item.CASH, mount);
		addRight(Item.SALES, mount);
		return this;
	}
	// 売掛金
	public PrivateBusinessAccount saleByReceivable(int mount) {
		addLeft(Item.RECEIVABLE, mount);
		addRight(Item.SALES, mount);
		return this;
	}

	/**
	 * 購入処理を行う
	 * @param date 購入日
	 * @param product 購入品
	 */
	public PrivateBusinessAccount buy(LocalDate date, Product product) {
		if (materials.contains(product)) return stock(product, 1);
		switch (product.type()) {
			case Product.Type.FIXED_ASSET:
				return buyFixedAsset(date, product);
			case Product.Type.LAND:
				return buyLand(date, product);
		}
		return this;
	}
	/**
	 * 仕入れる(買掛金)
	 * @param product 仕入対象の製品
	 * @param units 単位数
	 */
	public PrivateBusinessAccount stock(Product product, int units) {
		int amount = product.price() * units;
		addLeft(Item.PURCHESES, amount);
		addRight(Item.PAYABLE, amount);
		return this;
	}
	/**
	 * 固定資産の購入
	 */
	private PrivateBusinessAccount buyFixedAsset(LocalDate date, Product asset) {
		if (asset.type() != Product.FIXED_ASSET) throw new IllegalArgumentException();
		addFixedAsset(data,asset.price(), asset.serviceLife());

		addLeft(Item.TANGIBLE_ASSETS,asset.price());
		addRight(Item.CHECKING_ACCOUNTS,asset.price());
		return this;
	}
	/**
	 * 間接法で減価償却する
	 */
	private PrivateBusinessAccount depreciationByIndirect(LocalDate date) {
		int amount = recordFixedAssets(date);
		addLeft(Item.DEPRECIATION,amount);
		addRight(Item.ACCUMULATED_DEPRECIATION, amount);
		return this;
	}
	/**
	 * 直接法で減価償却する
	 */
	private PrivateBusinessAccount depreciationByDirect(LocalDate date) {
		int amount = recordFixedAssets(date);
		addLeft(Item.DEPRECIATION, amount);
		addRight(Item.TANGIBLE_ASSETS, amount);
		return this;
	}
	/**
	 * 土地の購入
	 */
	private PrivateBusinessAccount buyLand(LocalDate date, Prodacut asset) {
		addLeft(Item.TANGIBLE_ASSETS, asset.price());
		addRight(Item.CHECKING_ACCOUNTS, asset.price());
		return this;
	}

	/**
	 * 借金する
	 */
	private PrivateBusinessAccount borrow(int amount) {
		addLeft(Item.CHECKING_ACCOUNTS, amount);
		addRight(Item.LOANS_PAYABLE, amount);
		return this;
	}

	public static void main(String[] args) {
		Account<PrivateBusinessAccount.Item> account = new PrivateBusinessAccount();
		account.add(PrivateBusinessAccount.Item.SALES, 2000);
		System.out.println(account);
		PrivateBusinessAccount castAccount = (PrivateBusinessAccount)account;
		castAccount.test_fixedAssets();
	}
}
