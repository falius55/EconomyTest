package economy.stockmanager;

import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;
import java.util.OptionalInt;
import java.time.LocalDate;
import java.time.Period;

import economy.stockmanager.StockManager;
import economy.enumpack.Product;

/**
 * 工場を表すクラス(メーカー)
 * 定期的に生産
 */
public class Factory implements StockManager {
	private final Product product; // 製造する製品
	private int stock = 0; // 在庫
	private int totalCost = 0; // 原価総額
	private LocalDate lastManufacture; // 最終製造日
	private final Period manufactureGap;
	private final int manufactureUnit;
	private final Map<Product, Integer> materials; // 保有している原材料

	/**
	 * @param manufactureGap 製造間隔
	 * @param manufactureUnit 一度に生産する量
	 */
	public Factory(Period manufactureGap, int manufactureUnit) {
		this.manufactureGap = manufactureGap;
		this.manufactureUnit = manufactureUnit;
		materials = product.materialSet.stream()
			.collect(Collectors.toMap(Function.identity(), e -> 0, (s, t) -> s, () -> new EnumMap(Product.class)));
	}

	/*
	 * shipOut() - canShipOut()
	 * computePurchaseExpense() - manufacture() - canManufacture(), pullMaterial(), restockAll() - restock()
	 */

	/**
	 * 在庫があるかどうか
	 */
	@Override
	public boolean canShipOut(int lot) {
		return stock < lot * product.numOfLot();
	}
	/**
	 * 出荷します
	 * @return 原価。出荷に失敗すると空のOptionalInt
	 */
	@Override
	public OptionalInt shipOut(int lot) {
		if (!canShipOut(lot)) return OptionalInt.empty();
		int count = lot * product.numOfLot();
		int cost = (totalCost / stock) * count;
		totalCost -= cost;
		stock -= count;
		return OptionalInt.of(cost);
	}

	/**
	 * 仕入費用を集計します
	 * @return 仕入に要した費用
	 */
	@Override
	public int computePurchaseExpense(LocalDate date) {
		// 最終製造日から製造期間が過ぎていれば、製造期間に付き１セットの製造を行う
		int count = lastManufacture.until(date).getDays() / manufactureGap.getDays(); // 製造日が何回きたか
		if (count <= 0) return 0;
		return IntStream.range(0, count)
			.map(n -> manufacture(date))
			.sum();
	}

	/**
	 * 製造します
	 * @return 仕入に要した費用
	 */
	private int manufacture(LocalDate date) {
		int amount = restockAll();
		if (!canManufacture()) return amount; // 補充が十分にできなかった場合は、原材料の仕入のみを行って製造はしない
		lastManufacture = date;
		stock += manufactureUnit * count;
		product.materials()
			.forEach((material, materialStock) -> pullMaterial(material, materialStock));
		return amount;
	}

	/**
	 * 製造が可能なだけの原材料の在庫があるかどうかを返します
	 */
	private boolean canManufacture() {
		return product.materials().entrySet().stream()
			.allMatch(entry -> materials.get(entry.getKey()) >= entry.getValue());
	}

	/**
	 * 一度の生産に必要な原材料を取り出します。足りなければ例外を投げます
	 * @param material 必要とする原材料
	 * @param require 必要数量
	 */
	private void pullMaterial(Product material, int require) {
		if (!canManufacture) throw new IllegalStatementException();
		int stock = materials.get(material);
		materials.compute(material, (k, v) -> v - require);
	}
	/**
	 * 製造に必要な原材料をすべてそろえます
	 * @return 仕入に要した費用
	 */
	private int restockAll() {
		int ret = 0;
		for (entry : product.materials().entrySet()) {
			OptionalInt amount = restock(entry.getKey(), entry.getValue());
			if (!amount.isPresent())
				return ret;
			ret += amount.getAsInt();
		}
		return ret;
	}
	/**
	 * 指定された原材料の保有量がrequireになるよう補充します
	 * @return 仕入に要した費用。在庫が十分にあれば0。失敗すると空
	 */
	private OptionalInt restock(Product material, int require) {
		// 必要量を計算して仕入れる。在庫が十分にあれば何もしない
		int stock = materials.get(material);
		if (stock >= require) return OptionalInt.of(0);
		int shortfall = require - stock;
		int requireUnit = (int)Math.ceil((double)shortfall / material.numOfLot());
		return purchase(material, requireUnit);
	}

	/**
	 * 仕入れます
	 * @return 仕入に要した費用。仕入に失敗すると空
	 */
	private OptionalInt purchase(Product product, int lot) {
		PrivateBusiness store =
			PrivateBusiness.stream().filter(e -> e.canSale(product, lot))
			.findAny().get();
		OptionalInt amount = store.sale(product, lot);
		if (!amount.isPresent()) return amount;
		totalCost += amount.getAsInt();
		materials.compute(product, (k, v) -> v + lot * product.numOfLot());
		return amount;
	}
}
