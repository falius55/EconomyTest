package economy.enumpack;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.EnumSet;

import economy.enumpack.Product;

/**
 * 業種
 */
public enum Industry {
	// このコンストラクタ実行時点でProductが初期化されているとは限らない
	LIBLIO("書店") { public Set<Product> products() { return EnumSet.of(Product.NOVEL); } },
	REALTOR("不動産屋") { public Set<Product> products() { return EnumSet.of(Product.LAND, Product.BUILDINGS); } },
	FARMER("農家") { public Set<Product> products() { return EnumSet.of(Product.RICE); } },
	SUPER_MARKET("スーパー") { public Set<Product> products() { return EnumSet.of(Product.NOVEL, Product.RICE_BALL); } };

	private final String name; // 日本語名

	private static final Map<String, Industry> stringToEnum = new HashMap<String, Industry>(); // 日本語名から業種enumへのマップ
	static {
		for (Industry industry : values())
			stringToEnum.put(industry.toString(), industry);
	}

	/**
	 * @param name 日本語名
	 */
	Industry(String name) {
		this.name = name;
	}
	/**
	 * 日本語名から対象のenumインスタンスを取得する
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
	/**
	 * 取扱商品の集合を返す
	 */
	abstract public Set<Product> products();
	/**
	 * 商品取り扱いの有無
	 */
	public boolean hasProduct(Product product) {
		return products().contains(product);
	}
	public void print() {
		System.out.printf("%s%n", this);
		System.out.printf("取扱商品:%s%n", products());
	}
}
