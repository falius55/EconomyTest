package economy.account;

import java.util.Map;
import java.util.EnumMap;
import java.util.Set;
import java.util.EnumSet;
import java.time.LocalDate;

import economy.account.AbstractAccount;
import economy.enumpack.Product;
import economy.enumpack.Industry;
import economy.enumpack.PrivateBusinessAccountTitle;

public class PrivateBusinessAccount extends AbstractDoubleEntryAccount<PrivateBusinessAccountTitle> {

	private Set<Product> products; // 取扱商品の集合
	private Set<Product> materials; // 使用する原材料の集合(購入するときに仕入になるのかどうかを判断する)

	private PrivateBusinessAccount(Set<Product> products, Set<Product> materials) {
		super(PrivateBusinessAccountTitle.class);
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
			materials.addAll(pd.materialSet());
		}
		return newInstance(products, materials);
	}
	public static PrivateBusinessAccount newInstance(Industry industry) {
		return newInstance(industry.products());
	}

	@Override
	public PrivateBusinessAccount merge(Account<PrivateBusinessAccountTitle> account) {
		if (!(account instanceof PrivateBusinessAccount)) throw new IllegalArgumentException();
		return (PrivateBusinessAccount)super.merge(account);
	}
	@Override
	public PrivateBusinessAccountTitle defaultItem() {
		return PrivateBusinessAccountTitle.defaultItem();
	}
	@Override
	public PrivateBusinessAccountTitle[] items() {
		return PrivateBusinessAccountTitle.values();
	}

	/**
	 * 売り上げる。売上金は当座預金への振込で受け取り
	 */
	public PrivateBusinessAccount saleToAccount(int mount) {
		addLeft(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, mount);
		addRight(PrivateBusinessAccountTitle.SALES, mount);
		return this;
	}
	// 現金受け取り
	public PrivateBusinessAccount saleByCash(int mount) {
		addLeft(PrivateBusinessAccountTitle.CASH, mount);
		addRight(PrivateBusinessAccountTitle.SALES, mount);
		return this;
	}
	// 売掛金
	public PrivateBusinessAccount saleByReceivable(int mount) {
		addLeft(PrivateBusinessAccountTitle.RECEIVABLE, mount);
		addRight(PrivateBusinessAccountTitle.SALES, mount);
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
			case FIXED_ASSET:
				return buyFixedAsset(date, product);
			case LAND:
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
		addLeft(PrivateBusinessAccountTitle.PURCHESES, amount);
		addRight(PrivateBusinessAccountTitle.PAYABLE, amount);
		return this;
	}
	/**
	 * 固定資産の購入
	 */
	private PrivateBusinessAccount buyFixedAsset(LocalDate date, Product asset) {
		if (asset.type() != Product.Type.FIXED_ASSET) throw new IllegalArgumentException();
		addFixedAsset(date,asset.price(), asset.serviceLife());

		addLeft(PrivateBusinessAccountTitle.TANGIBLE_ASSETS,asset.price());
		addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS,asset.price());
		return this;
	}
	/**
	 * 間接法で減価償却する
	 */
	private PrivateBusinessAccount depreciationByIndirect(LocalDate date) {
		int amount = recordFixedAssets(date);
		addLeft(PrivateBusinessAccountTitle.DEPRECIATION,amount);
		addRight(PrivateBusinessAccountTitle.ACCUMULATED_DEPRECIATION, amount);
		return this;
	}
	/**
	 * 直接法で減価償却する
	 */
	private PrivateBusinessAccount depreciationByDirect(LocalDate date) {
		int amount = recordFixedAssets(date);
		addLeft(PrivateBusinessAccountTitle.DEPRECIATION, amount);
		addRight(PrivateBusinessAccountTitle.TANGIBLE_ASSETS, amount);
		return this;
	}
	/**
	 * 土地の購入
	 */
	private PrivateBusinessAccount buyLand(LocalDate date, Product asset) {
		addLeft(PrivateBusinessAccountTitle.TANGIBLE_ASSETS, asset.price());
		addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, asset.price());
		return this;
	}

	/**
	 * 借金する
	 */
	private PrivateBusinessAccount borrow(int amount) {
		addLeft(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
		addRight(PrivateBusinessAccountTitle.LOANS_PAYABLE, amount);
		return this;
	}

	public static void main(String[] args) {
		Account<PrivateBusinessAccountTitle> account = PrivateBusinessAccount.newInstance(Industry.FARMER);
		account.add(PrivateBusinessAccountTitle.SALES, 2000);
		System.out.println(account);
		PrivateBusinessAccount castAccount = (PrivateBusinessAccount)account;
		castAccount.test_fixedAssets();
		Product.printAll();
	}
}
