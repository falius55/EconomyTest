package economy.stockmanager;

import java.util.Optional;
import java.util.OptionalInt;
import java.time.LocalDate;

import economy.stockmanager.StockManager;
import economy.enumpack.Product;
import economy.enumpack.Industry;

/**
 * 倉庫
 */
public class Repository implements StockManager {
	private final Product product; // 製造する製品
	private int stock = 0; // 在庫
	private int puchaseExpense = 0; // 未計上の仕入費用総額
	private int totalCost = 0; // 原価総額
	private Industry.Type productSource; // 仕入先の業態(小売なら流通業者、流通業者ならメーカー)

	public Repository(Industry.Type productSource) {
		this.productSource = productSource;
	}

	/**
	 * 在庫があるかどうか。あるいは、在庫補充のめどが立つかどうか
	 */
	@Override
	public boolean canShipOut(int lot) {
		int require = lot * product.numOfLot();
		int shortfall = require - stock;
		if (shortfall <= 0) return true;
		return PrivateBusiness.stream()
			.anyMatch(store -> store.isCategory(productSource) && store.canSale(product, lot));
	}
	/**
	 * 出荷します
	 * @return 原価。出荷に失敗すると空のOptionalInt
	 */
	@Override
	public OptionalInt shipOut(int lot) {
		// 足りなくなれば即座に仕入
		int require = lot * product.numOfLot();
		if (require > stock)
			puchase(product, (int)Math.ceil((double)(require - stock) / product.numOfLot()));
		if (!canShipOut(lot)) return OptionalInt.empty();
		int cost = (totalCost / stock) * require;
		totalCost -= cost;
		stock -= require;
		return OptionalInt.of(cost);
	}
	/**
	 * 仕入費用を集計します
	 * @return 仕入に要した費用
	 */
	@Override
	public int computePurchaseExpense(LocalDate date) {
		int ret = puchaseExpense;
		puchaseExpense = 0;
		return ret;
	}

	/**
	 * 仕入れます
	 * @return 仕入に要した費用。仕入に失敗すると空
	 */
	private OptionalInt purchase(Product product, int lot) {
		// 仕入先の店を探す
		Optional<PrivateBusiness> store =
			PrivateBusiness.stream().filter(e -> e.isCategory(productSource) && e.canSale(product, lot))
			.findAny().get();
		if (!store.isPresent()) return Optional.empty();
		// 購入する
		OptionalInt cost = store.get().sale(product, lot);
		if (!cost.isPresent()) return cost;
		// 集計
		totalCost += cost.getAsInt();
		purchaseExpense += cost.getAsInt();
		stock += lot * product.numOfLot();
		return cost;
	}
}
