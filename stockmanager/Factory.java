package economy.stockmanager;

import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.Function;
import java.util.Optional;
import java.util.OptionalInt;
import java.time.LocalDate;
import java.time.Period;

import economy.stockmanager.StockManager;
import economy.enumpack.Product;
import economy.player.PrivateBusiness;

/**
 * 工場を表すクラス(メーカー)
 * 定期的に生産
 */
public class Factory implements StockManager {
	private final Product product; // 製造する製品
	private int stock = 0; // 在庫
	private int totalCost = 0; // 原価総額　あくまで現在の在庫にかかった費用であり、出荷することで平均から算出された分だけ減少する
	private LocalDate lastManufacture; // 最終製造日
	private final Period manufacturePeriod; // 製造期間
	private final int productionVolume; // 一度の製造数
	private final Map<Product, Integer> materials; // 保有している原材料

	public Factory(Product product) {
		this.product = product;
		this.manufacturePeriod = product.manufacturePeriod();
		this.productionVolume = product.productionVolume();
		materials = product.materialSet().stream()
			.collect(Collectors.toMap(Function.identity(), e -> 0, (s, t) -> s, () -> new EnumMap(Product.class)));
	}

	/*
	 * shipOut() - canShipOut()
	 * computePurchaseExpense() - manufacture() - canManufacture(), pullMaterial(), restockAll() - computeRequireLot(), purchase()
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
		// 原価計算(単純に総費用を在庫で割って平均を求める)
		int cost = (totalCost / stock) * count;
		totalCost -= cost;
		// 在庫の減少
		stock -= count;
		return OptionalInt.of(cost);
	}

	/**
	 * 仕入費用を集計します
	 * このメソッドを実行しなければ在庫が補充されませんので注意してください
	 * @return 仕入に要した費用
	 */
	@Override
	public int computePurchaseExpense(LocalDate date) {
		// 最終製造日から製造期間が過ぎていれば、製造期間に付き１セットの製造を行う
		int count = lastManufacture.until(date).getDays() / manufacturePeriod.getDays(); // 製造日が何回きたか
		if (count <= 0) return 0;
		return IntStream.range(0, count)
			.map(n -> manufacture())
			.sum();
	}

	/**
	 * 製造します
	 * @return 仕入に要した費用
	 */
	private int manufacture() {
		int amount = restockAll();
		if (!canManufacture()) return amount; // 補充が十分にできなかった場合は、原材料の仕入(途中まで)のみを行って製造はしない
		lastManufacture = lastManufacture.plus(manufacturePeriod);
		product.materials()
			.forEach((material, require) -> pullMaterial(material, require * productionVolume));
		stock += productionVolume;
		return amount;
	}

	/**
	 * 製造が可能なだけの原材料の在庫があるかどうかを返します
	 */
	private boolean canManufacture() {
		return product.materials().entrySet().stream()
			.allMatch(entry -> materials.get(entry.getKey()) >= entry.getValue() * productionVolume);
	}

	/**
	 * 一度の生産に必要な原材料を取り出します
	 * 足りなければ例外を投げます
	 * 必ずrestockAll()で原材料を補充し、canManufacture()で生産できるのかどうかを検査してから使用してください
	 * @param material 必要とする原材料
	 * @param require 必要数量
	 */
	private void pullMaterial(Product material, int require) {
		// 原材料を減少させる
		if (!canManufacture()) throw new IllegalStateException();
		materials.compute(material, (k, v) -> v - require);
	}
	/**
	 * 製造に必要な原材料をすべてそろえます
	 * @return 仕入に要した費用
	 */
	private int restockAll() {
		// 全原材料が十分にある状態にする
		// 途中でループを終える必要があるため、streamで処理するのは難しい
		int ret = 0;
		for (Map.Entry<Product, Integer> entry : product.materials().entrySet()) {
			int lot = computeRequireLot(entry.getKey(), entry.getValue());
			OptionalInt amount = purchase(entry.getKey(), lot);
			if (!amount.isPresent())
				return ret;
			ret += amount.getAsInt();
		}
		return ret;
	}
	/**
	 * 指定された原材料の保有量がrequireになるのに必要なロット数を計算します
	 * @return 新たに必要なロット数。必要なければ０
	 */
	private int computeRequireLot(Product material, int require) {
		int stock = materials.get(material);
		if (stock >= require) return 0;
		int shortfall = require - stock;
		int ret = (int)Math.ceil((double)shortfall / material.numOfLot());
		return ret >= 0 ? ret : 0;
	}

	/**
	 * 仕入れます
	 * @return 仕入に要した費用。仕入に失敗すると空
	 */
	private OptionalInt purchase(Product product, int lot) {
		// 特定の原材料を指定された分の補充
		Optional<PrivateBusiness> store =
			PrivateBusiness.stream().filter(e -> e.canSale(product, lot))
			.findAny();
		if (!store.isPresent()) return OptionalInt.empty();

		OptionalInt amount = store.get().sale(product, lot);
		if (!amount.isPresent()) return OptionalInt.empty();

		totalCost += amount.getAsInt();
		materials.compute(product, (k, v) -> v + lot * product.numOfLot());
		return amount;
	}
}
