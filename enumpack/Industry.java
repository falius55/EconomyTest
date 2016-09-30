package economy.enumpack;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.EnumSet;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.time.Period;

import economy.enumpack.Product;
import economy.player.PrivateBusiness;
import economy.player.Retail;
import economy.stockmanager.StockManager;
import economy.stockmanager.Repository;
import economy.stockmanager.Factory;

/**
 * 業種
 */
public enum Industry {
	// このコンストラクタ実行時点でProductが初期化されているとは限らない
	LIBLIO("書店", Type.RETAIL) { public Set<Product> products() { return EnumSet.of(Product.NOVEL); } },
	REALTOR("不動産屋", Type.BIGMOUTHED_RETAIL) { public Set<Product> products() { return EnumSet.of(Product.LAND, Product.BUILDINGS); } },
	FARMER("農家", Type.FIRST) { public Set<Product> products() { return EnumSet.of(Product.RICE); } },
	SUPER_MARKET("スーパー", Type.RETAIL) { public Set<Product> products() { return EnumSet.of(Product.NOVEL, Product.RICE_BALL); } };

	private final String name; // 日本語名
	private final Type type;

	private static final Map<String, Industry> stringToEnum = new HashMap<String, Industry>(); // 日本語名から業種enumへのマップ
	static {
		for (Industry industry : values())
			stringToEnum.put(industry.toString(), industry);
	}

	/**
	 * @param name 日本語名
	 */
	Industry(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	/**
	 * 日本語名から対象のenumインスタンスを取得します
	 * @param name 日本語名
	 * @return 対象のenum
	 */
	public static Industry fromString(String name) {
		return stringToEnum.get(name);
	}
	/**
	 * @return 日本語名
	 */
	@Override public String toString() { return name; }
	public Type type() {
		return type;
	}
	/**
	 * 取扱商品の集合を返します
	 */
	abstract public Set<Product> products();
	/**
	 * 商品取り扱いの有無を返します
	 */
	public boolean hasProduct(Product product) {
		return products().contains(product);
	}
	public PrivateBusiness createInstance() {
		return type().createInstance(this, products());
	}
	public void print() {
		System.out.printf("%s%n", this);
		System.out.printf("取扱商品:%s%n", products());
	}

	/**
	 * filterによってtrueと判定された要素の集合を返します
	 * @param filter 要素の判定に使用する関数型インタフェース
	 * @return 判定がtrueである要素の集合
	 */
	static public Set<Industry> selectSet(Predicate<Industry> filter) {
		return Arrays.stream(values())
			.filter(filter)
			.collect(Collectors.toCollection(() -> EnumSet.noneOf(Industry.class)));
	}

	public enum Type {
		RETAIL("小売") {
			@Override public PrivateBusiness createInstance(Industry industry, Set<Product> products) {
				return new Retail(industry, products);
			}
			@Override public StockManager newManager(Product product) {
				return new Repository(product, DISTRIBUTOR);
			}
		}, MAKER("メーカー") {
			@Override public StockManager newManager(Product product) {
				return new Factory(product);
			}
		}, FIRST("第一次産業"),
		DISTRIBUTOR("流通業") {
			@Override public StockManager newManager(Product product) {
				return new Repository(product, MAKER);
			}
		},
		BIGMOUTHED_RETAIL("大口小売");

		private final String name;

		Type(String name) {
			this.name = name;
		}

		public PrivateBusiness createInstance(Industry industry, Set<Product> products) {
			return new PrivateBusiness(industry, products);
		}
		public StockManager newManager(Product product) {
			return new Repository(product, MAKER);
		}
	}
}
