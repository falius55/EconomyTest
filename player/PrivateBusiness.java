package economy.player;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.EnumMap;
import java.util.OptionalInt;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.function.Function;

import economy.player.AbstractSubject;
import economy.player.HumanResourcesDepartment;
import economy.account.PrivateBusinessAccount;
import economy.stockmanager.StockManager;
import economy.enumpack.Product;
import economy.enumpack.Industry;
import economy.enumpack.PrivateBusinessAccountTitle;

/**
 * 売買
 */
public class PrivateBusiness extends AbstractSubject {
	private static final List<PrivateBusiness> owns = new ArrayList<PrivateBusiness>();
	private final Set<Product> products;
	private final Industry industry;
	private final HumanResourcesDepartment stuffManager = new HumanResourcesDepartment();
	private final Map<Product, StockManager> stockManagers; // 製品ごとに在庫管理
	private static final double MARGIN = 0.2; // 原価に上乗せするマージン

	public PrivateBusiness(Industry industry, Set<Product> products) {
		super(PrivateBusinessAccount.newInstance());
		this.industry = industry;
		this.products = products;
		stockManagers = products.stream()
			.collect(Collectors.toMap(Function.identity(), industry.type()::newManager, (p1, p2) -> p1, () -> new EnumMap<Product, StockManager>(Product.class)));

		owns.add(this);
	}

	/**
	 * 製品が売っているのかどうかを判断します
	 * @param product 製品
	 * @param lot 必要となるロット数
	 */
	public boolean canSale(Product product, int lot) {
		// 製品を取り扱っており、在庫があればtrue
		return products.contains(product) && stockManagers.get(product).canShipOut(lot);
	}
	public boolean canSale(String product, int lot) {
		return canSale(Product.fromString(product), lot);
	}
	/**
	 * 指定された製品を指定されたロット分売ります
	 * @return 売値。販売できなければ空のOptionalInt
	 */
	public OptionalInt sale(String strProduct, int requireLot) {
		Product product = Product.fromString(strProduct);
		// 倉庫、工場から製品を持ってくる
		OptionalInt cost = stockManagers.get(product).shipOut(requireLot);
		if (!cost.isPresent()) return OptionalInt.empty();

		// 原価にマージンを上乗せして売値を決める
		int price = (int)(cost.getAsInt() * MARGIN);

		// 帳簿に記帳する
		account.<PrivateBusinessAccountTitle>saleBy(saleAccount(), price);
		return OptionalInt.of(price);
	}
	/**
	 * @return 売値
	 */
	public OptionalInt sale(String strProduct) {
		return sale(strProduct, 1);
	}
	public OptionalInt sale(Product product) {
		return sale(product.toString());
	}
	public OptionalInt sale(Product product, int requireLot) {
		return sale(product.toString(), requireLot);
	}
	/**
	 * 業種が同じかどうかを判定します
	 */
	public boolean is(Industry industry) {
		return industry == this.industry;
	}
	/**
	 * 業態が同じかどうかを判定します
	 */
	public boolean is(Industry.Type type) {
		return type == industry.type();
	}
	/**
	 * 売り上げた時、通常使用する勘定科目を返します
	 * 当座預金でない場合は継承してオーバーライドしてください
	 */
	PrivateBusinessAccountTitle saleAccount() {
		return PrivateBusinessAccountTitle.CHECKING_ACCOUNTS;
	}

	public static Stream<PrivateBusiness> stream() {
		return owns.stream();
	}
}
