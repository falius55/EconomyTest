import java.util.Map;
import java.util.EnumMap;

/*
 * 継承の際に、型引数にiItemインターフェースを実装した勘定科目enumを指定する
 * T : 勘定科目enum
 */
public abstract class AbstractAccount<T extends Enum<T> & AbstractAccount.iItem> implements Account<T> {
	// 科目の細目を定義するenumの型を定義する
	public interface iItem {
		Type type();
	}

	// 科目種別(資産、費用、収益、負債、資産)
	enum Type {
		ASSETS(RL.LEFT),EXPENSE(RL.LEFT),REVENUE(RL.RIGHT),LIABILITIES(RL.RIGHT),EQUITY(RL.RIGHT);
		private final RL rl; // 貸借対照表、損益計算書で左右どちらに表記されるか(借方科目か貸方科目か)
		Type(RL rl) {
			this.rl = rl;
		}
		public RL rl() {
			return this.rl;
		}

		enum RL {
			RIGHT,LEFT;
			public RL inverse() {
				switch (this) {
					case RIGHT: return RL.LEFT;
					case LEFT: return RL.RIGHT;
					default: throw new AssertionError("Unknown rl: "+ this);
				}
			}
		}
	}

	private final Map<Type, Map<T, Integer>> accountsBook; // 帳簿(EnumMap) 科目種別のマップ
	// private final Map<AbstractAdministration, Integer> bondMap; // 公債の発行先から、保有公債総額へのマップ

	// 型変数から直接.classをしたりメソッドを実行したりはできないため、enumのClassインスタンスを引数に取る
	protected AbstractAccount(Class<T> clazz) {
		this.accountsBook = initBook(clazz);
		// bondMap = new WeakHashMap<AbstractAdministration, Integer>();
	}

	// 帳簿を初期化する
	private Map<Type, Map<T, Integer>> initBook(Class<T> clazz) {
		Map<Type, Map<T, Integer>> result = new EnumMap<Type, Map<T, Integer>>(Type.class);
		for (Type type : Type.values()) {
			Map<T, Integer> map = new EnumMap<T, Integer>(clazz);
			for (T item : clazz.getEnumConstants()) {
				if (item.type().equals(type))
					map.put(item, 0);
			}
			result.put(type, map);
		}
		return result;
	}

	// 記入する
	// rl: 左右どちらに記入するか
	public void add(Type.RL rl, T item, int amount) {
		if (item.type().rl().equals(rl))
			increace(item, amount);
		else
			decreace(item, amount);
	}
	// 指定がないときに増減させる科目を返す。普通は現金
	abstract public T defaultItem();
	// 標準資産科目を相手科目として、指定された科目を増加させる
	// 標準科目が資産科目でなければIllegalaugumentexception
	public void add(T item, int amount) {
		T defaultItem = defaultItem();
		if (!defaultItem.type().equals(Type.ASSETS)) throw new IllegalArgumentException("defaultItem is not Assets");
		add(item.type().rl(), item, amount);
		add(item.type().rl().inverse(), defaultItem, amount);
	}
	// 借方に記入する
	public void addLeft(T item, int amount) {
		add(Type.RL.LEFT, item, amount);
	}
	public void addRIGHT(T item, int amount) {
		add(Type.RL.RIGHT, item, amount);
	}

	// 特定科目の金額を増加する
	private void increace(T item, int amount) {
		Map<T, Integer> itemMap = accountsBook.get(item.type());
		itemMap.put(item, itemMap.get(item).intValue() + amount);
	}
	private void decreace(T item, int amount) {
		Map<T, Integer> itemMap = accountsBook.get(item.type());
		itemMap.put(item, itemMap.get(item).intValue() - amount);
	}

	@Override
	public String toString() {
		return accountsBook.toString();
	}

	// ２つの会計を合わせ、新しい会計を返す。結婚、合併
	// 同等のインスタンス同士で統合するのかまではチェックできないため、サブタイプでオーバーライドしてチェックすること
	@Override
	public AbstractAccount<T> merge(Account<T> another) {
		if (!(another instanceof AbstractAccount)) throw new IllegalArgumentException();
		AbstractAccount<T> newInstance = (AbstractAccount<T>)newInstance();
		for (T item : items()) {
			newInstance.add(item.type().rl(), item, another.get(item));
		}
		return newInstance;
	}

	@Override
	abstract public Account<T> newInstance();

	@Override
	abstract public T[] items();

	// 資産合計
	@Override
	public int assets() {
		return get(Type.ASSETS);
	}

	// 費用合計
	@Override
	public int expense() {
		return get(Type.EXPENSE);
	}

	// 収益合計
	@Override
	public int revenue() {
		return get(Type.REVENUE);
	}

	// 負債合計
	@Override
	public int liabilities() {
		return get(Type.LIABILITIES);
	}

	// 種類別に集計する
	public int get(Type type) {
		Map<T, Integer> itemMap = accountsBook.get(type);
		int result = 0;

		for (Map.Entry<T, Integer> entry : itemMap.entrySet()) {
			result += entry.getValue().intValue();
		}
		return result;
	}
	// 科目の金額を取得する
	@Override
	public int get(T item) {
		Map<T, Integer> itemMap = accountsBook.get(item.type());
		return itemMap.get(item).intValue();
	}

}
