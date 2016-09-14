package economy.enumpack;

import java.util.Set;
import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;

import economy.enumpack.Industry;
import economy.util.EnumEachable;

/**
 * 製品
 */
public enum Product {
	// Enumのコンストラクタ実行時点でEnum全体の初期化が終わっているわけではないためか、コンストラクタの引数に直接EnumSet.noneOf(Product.class)を渡して
	// コンストラクタ内でメンバー変数に保存しようとするとExceptionInInitializerErrorが起こる(Product.classが不可？)
	// また、その要素より下で宣言されている要素を引数内で使用していると「前方参照が不正です」というコンパイルエラー
	// abstractメソッドで取り出すことにして、オーバーライドしたメソッドに値を直接記述することで回避。要素追加の際もコンストラクタ引数に渡す場合とあまり手間が変わらない
	LAND("土地",10000000 /* 円 */, Type.LAND, 100 /* 坪 */) {
		// 原材料からその利用数量へのマップを作成する
		protected Map<Product, Integer> createMaterialMap() {
			Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
			return ret;
		}
	},
	WOOD("木材", 60, Type.CONSUMER, 1000 /* g */){
		protected Map<Product, Integer> createMaterialMap() {
			Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
			return ret;
		}
	},
	RICE("米", 30, Type.CONSUMER, 1000 /* g */){
		protected Map<Product, Integer> createMaterialMap() {
			Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
			return ret;
		}
	},
	PAPER("紙", 100, Type.CONSUMER, 500 /* 枚 */){
		protected Map<Product, Integer> createMaterialMap() {
			Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
			ret.put(WOOD, 5 /* g */);
			return ret;
		}
	},
	BUILDINGS("建物", 5000000, Type.FIXED_ASSET, 45 /* 年 */, 1 /* 棟 */){
		protected Map<Product, Integer> createMaterialMap() {
			Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
			ret.put(WOOD, 100000 /* g */);
			return ret;
		}
	},
	NOVEL("小説",480,Type.CURRENT_ASSET, 1 /* 冊 */){
		protected Map<Product, Integer> createMaterialMap() {
			Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
			ret.put(PAPER, 500 /* 枚 */);
			return ret;
		}
	},
	RICE_BALL("おにぎり",120,Type.CONSUMER, 1 /* 個 */){
		protected Map<Product, Integer> createMaterialMap() {
			Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
			ret.put(RICE, 200 /* g */);
			return ret;
		}
	};

	private final String name; // 日本語名
	private final int price; // 値段
	private final Type type; // 資産としての種類
	private final int serviceLife; // 耐用年数
	private final int numOfLot; // 購入単位あたり数量
	private static final Map<String, Product> stringToEnum = new HashMap<String, Product>(); // 日本語名から商品enumへのマップ
	private static final Map<Product, Map<Product, Integer>> materials = new EnumMap<Product, Map<Product, Integer>>(Product.class);
	static {
		for (Product product : values())
			stringToEnum.put(product.toString(), product);

		for (Product product : values())
			materials.put(product, product.createMaterialMap());
	}

	/**
	 * @param name 日本語名
	 * @param price 値段(１単位あたり)
	 * @param type 資産としての種類(消費財、固定資産など)
	 * @param numOfLot １単位あたり数量
	 */
	Product(String name, int price,Type type, int numOfLot) {
		this(name, price, type, 0, numOfLot);
		if (type == Type.FIXED_ASSET) throw new IllegalArgumentException("arguments has no serviceLife");
	}
	/**
	 * 耐用年数が存在する場合に利用
	 * @param name 日本語名
	 * @param price 値段(１単位あたり)
	 * @param type 資産としての種類(消費財、固定資産など)
	 * @param servicelife 耐用年数
	 * @param numOfLot １単位あたり数量
	 */
	Product(String name, int price, Type type, int serviceLife, int numOfLot) {
		this.name = name;
		this.price = price;
		this.type = type;
		this.serviceLife = serviceLife;
		this.numOfLot = numOfLot;

		if (type == Type.FIXED_ASSET && serviceLife == 0) throw new IllegalArgumentException();
	}

	/**
	 * 日本語名から対象のenumインスタンスを取得する
	 * @param name 日本語名
	 * @return 対象のenum
	 */
	public static Product fromString(String name) {
		return stringToEnum.get(name);
	}
	/**
	 * @return この商品の日本語表現
	 */
	@Override public String toString() { return name; }
	/**
	 * @return この商品の値段
	 */
	public int price() {
		return price;
	}
	public int serviceLife() {
		return serviceLife;
	}
	/**
	 * この商品を取り扱っている業種の集合を返す
	 */
	public Set<Industry> industries() {
		return EnumEachable.selectSet(Industry.class, industry -> industry.products().contains(this));
	}
	/**
	 * 原材料の集合を返します
	 */
	public Set<Product> materialSet() {
		Map<Product, Integer> materials = Product.materials.get(this);
		return materials.keySet();
	}
	public Map<Product, Integer> materials() {
		// ないとは思うが、もしオーバーライドしたcreateMaterialMap()でEnumMap以外を返すようにした場合にもHashMapで対応する
		Map<Product, Integer> materials = Product.materials.get(this);
		if (materials instanceof EnumMap)
			return new EnumMap<Product, Integer>((EnumMap<Product, Integer>)materials);
		else
			return new HashMap<Product, Integer>(materials);
	}

	abstract protected Map<Product, Integer> createMaterialMap();

	public Type type() {
		return type;
	}
	public int numOfLot() {
		return numOfLot;
	}
	public void print() {
		System.out.printf("%s%n",this);
		System.out.printf("値段:%d%n",price());
		System.out.printf("単位あたり数量:%d%n", numOfLot());
		System.out.printf("種別:%s%n", type());
		System.out.printf("耐用年数:%d%n",serviceLife());
		System.out.printf("原材料:%s",materials());
		System.out.printf("取扱業者:%s%n",industries());
		System.out.println("");
	}
	public static void printAll() {
		for (Product pd : values()) {
			pd.print();
		}
	}

	/**
	 * 製品の分類
	 */
	public enum Type {
		CONSUMER("消費財"), // 消費財
		CURRENT_ASSET("流動資産"), // 流動資産
		LAND("土地"),
		FIXED_ASSET("固定資産"); // 固定資産

		private final String name;
		Type(String name) {
			this.name = name;
		}
		@Override public String toString() {
			return name;
		}
	}
}
