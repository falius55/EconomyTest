package economy.account;

import java.util.Map;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;

import economy.account.AbstractAccount;
import economy.account.DoubleEntryAccount;;
import economy.enumpack.AccountTitle;
import economy.enumpack.AccountType;

/**
 * 複式簿記会計の骨格実装クラス
 * 帳簿の操作と固定資産の処理を主に担当
 */
public abstract class AbstractDoubleEntryAccount<T extends Enum<T> & AccountTitle> extends AbstractAccount<T> implements DoubleEntryAccount<T> {
	private final Map<AccountType, Map<T, Integer>> accountsBook; // 帳簿(EnumMap) 科目種別のマップ
	private final Set<FixedAsset> fixedAssets;

	protected AbstractDoubleEntryAccount(Class<T> clazz) {
		this.accountsBook = initBook(clazz);
		fixedAssets = new HashSet<FixedAsset>();
		// bondMap = new WeakHashMap<AbstractAdministration, Integer>();
	}

	// 帳簿を初期化する
	private Map<AccountType, Map<T, Integer>> initBook(Class<T> clazz) {
		Map<AccountType, Map<T, Integer>> result = new EnumMap<AccountType, Map<T, Integer>>(AccountType.class);
		for (AccountType type : AccountType.values()) {
			Map<T, Integer> map = new EnumMap<T, Integer>(clazz);
			for (T item : clazz.getEnumConstants()) {
				if (item.type().equals(type))
					map.put(item, 0);
			}
			result.put(type, map);
		}
		return result;
	}

	/**
	 * 帳簿に記入する
	 * @param rl 記入箇所。借方(LEFT)か貸し方(RIGHT)か
	 * @param item 勘定科目
	 * @param amount 金額
	 */
	protected void add(AccountType.RL rl, T item, int amount) {
		if (item.type().rl().equals(rl))
			increase(item, amount);
		else
			decrease(item, amount);
	}
	/**
	 * 標準資産科目(defaultItem()によって定義)を相手科目として、指定された科目を増加させる
	 * @param item 勘定科目
	 * @param amount 金額
	 * @throws IllegalArgumentException サブタイプで定義した標準科目が資産科目でない場合
	 */
	public void add(T item, int amount) {
		T defaultItem = defaultItem();
		if (!defaultItem.type().equals(AccountType.ASSETS)) throw new IllegalArgumentException("defaultItem is not Assets");
		add(item.type().rl(), item, amount);
		add(item.type().rl().inverse(), defaultItem, amount);
	}
	/**
	 * 借方に記入する
	 * @param item 勘定科目
	 * @param amount 金額
	 */
	protected void addLeft(T item, int amount) {
		add(AccountType.RL.LEFT, item, amount);
	}
	/**
	 * 貸方に記入する
	 * @param item 勘定科目
	 * @param amount 金額
	 */
	protected void addRight(T item, int amount) {
		add(AccountType.RL.RIGHT, item, amount);
	}

	// 特定科目の金額を増加する
	@Override
	protected void increase(T item, int amount) {
		Map<T, Integer> itemMap = accountsBook.get(item.type());
		itemMap.put(item, itemMap.get(item).intValue() + amount);
	}
	protected void decrease(T item, int amount) {
		Map<T, Integer> itemMap = accountsBook.get(item.type());
		itemMap.put(item, itemMap.get(item).intValue() - amount);
	}

	/**
	 * 資産合計
	 */
	@Override
	public int assets() {
		return get(AccountType.ASSETS);
	}

	/**
	 * 費用合計
	 */
	@Override
	public int expense() {
		return get(AccountType.EXPENSE);
	}

	/**
	 * 収益合計
	 */
	@Override
	public int revenue() {
		return get(AccountType.REVENUE);
	}

	/**
	 * 負債合計
	 */
	@Override
	public int liabilities() {
		return get(AccountType.LIABILITIES);
	}

	/**
	 * 指定した科目種別の総額を計算する
	 * @param type 科目種別
	 * @return 集計結果
	 */
	@Override
	public int get(AccountType type) {
		Map<T, Integer> itemMap = accountsBook.get(type);
		int result = 0;

		for (Map.Entry<T, Integer> entry : itemMap.entrySet()) {
			result += entry.getValue().intValue();
		}
		return result;
	}
	/**
	 * 指定した勘定科目の金額を返す
	 * @param item 勘定科目
	 * @return 指定した勘定科目の金額
	 */
	@Override
	public int get(T item) {
		Map<T, Integer> itemMap = accountsBook.get(item.type());
		return itemMap.get(item).intValue();
	}

	/**
	 * 帳簿内容の文字列表現を返す
	 */
	@Override
	public String toString() {
		return accountsBook.toString();
	}


	// 以下は固定資産

	/**
	 * 固定資産を追加する
	 * @param dateOfAcquisition 取得日
	 * @param acquisitionCost 取得原価
	 * @param serviceLife 耐用年数
	 */
	protected void addFixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
		fixedAssets.add(new FixedAsset(dateOfAcquisition, acquisitionCost, serviceLife));
	}
	/**
	 * 所有している固定資産全てにおいて、減価償却の処理を行う
	 * @param date 記入日
	 * @return その日の償却額
	 */
	protected int recordFixedAssets(LocalDate date) {
		int amount = 0;
		for (FixedAsset asset : fixedAssets) {
			amount += asset.record(date);
		}
		return amount;
	}


	/**
	 * 固定資産を表すクラス
	 */
	public static class FixedAsset {
		private static final int RESIDUAL_PERCENT = 10; // 取得原価に対する残存価額の割合(%)
		private final LocalDate dateOfAcquisition; // 取得日
		private final int acquisitionCost; // 取得原価
		private final int serviceLife; // 耐用年数
		private final int residualValue; // 残存価額
		private final int fixedAmountOfMonths; // 定額法における、償却月額
		private final LocalDate lastRecordedDate; // 最終計上日 TODO: 営業日を考慮する
		private int undepreciatedBalance; // 未償却残高
		private Map<LocalDate, Integer> recordMap; // 償却日から未償却額へのマップ

		/**
		 * @param dateOfAcquisition 取得日
		 * @param acquisitionCost 取得原価
		 * @param serviceLife 耐用年数
		 */
		private FixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
			this.dateOfAcquisition = dateOfAcquisition;
			this.acquisitionCost = acquisitionCost;
			this.serviceLife = serviceLife;
			this.lastRecordedDate = dateOfAcquisition.plusYears(serviceLife).minusMonths(1);
			this.residualValue = acquisitionCost * RESIDUAL_PERCENT / 100; // 切り捨て
			this.fixedAmountOfMonths = (int)Math.ceil((double)(acquisitionCost - residualValue) / (serviceLife * 12));
			this.undepreciatedBalance = acquisitionCost - residualValue;

			this.recordMap = new TreeMap<LocalDate, Integer>(); // 償却日でソート
		}
		/**
		 * その日が計上日であるか(毎月。営業日無視) TODO: 営業日を考慮する
		 */
		private boolean isRecordedDate(LocalDate date) {
			// 償却が終わっている
			if (date.isAfter(lastRecordedDate)) return false;
			// 対応する日がない
			if (date.lengthOfMonth() < dateOfAcquisition.getDayOfMonth())
				return date.getDayOfMonth() == date.lengthOfMonth();
			// 対応する日がある
			return date.getDayOfMonth() == dateOfAcquisition.getDayOfMonth();
		}
		/**
		 * 計上する
		 * @param date 計上日
		 * @return 計上月額
		 */
		public int record(LocalDate date) {
			if (!isRecordedDate(date)) return 0;
			if (recordMap.containsKey(date)) return 0;
			if (undepreciatedBalance <= 0) return 0;
			int amount = fixedAmountOfMonths;
			amount = undepreciatedBalance < amount ? undepreciatedBalance : amount;
			undepreciatedBalance -= amount;
			recordMap.put(date, undepreciatedBalance); // 記録
			return amount;
		}
		/**
		 * 状態を表形式で表示する
		 */
		public void print() {
			System.out.printf("get:%s, all-amount:%d円, per-amount:%d, life:%d年%n", dateOfAcquisition, acquisitionCost, fixedAmountOfMonths, serviceLife);
			System.out.printf("日付 	金額%n");
			int cnt = 1;
			for (LocalDate date : recordMap.keySet()) {
				System.out.printf("%d回目 %s %s%n", cnt++, date, recordMap.get(date));
			}
			System.out.printf("最終償却日の合致:%b%n", ((SortedMap<LocalDate, Integer>)recordMap).lastKey().equals(lastRecordedDate));
		}
	}

	/**
	 * テスト用メソッド
	 */
	public void test_fixedAssets() {
		LocalDate date = LocalDate.now();
		int depreciatedBalance = 0;
		for (int i = 0; i < 100; i++) {
			addFixedAsset(date, 100000*(i+1), i%10+1);
			depreciatedBalance += recordFixedAssets(date);
			date = date.plusDays(1);
		}
		for (int i=0; i<3000; i++) {
			depreciatedBalance += recordFixedAssets(date);
			date = date.plusDays(1);
		}

		for (FixedAsset asset : fixedAssets) {
			asset.print();
		}
		System.out.printf("現在日: %s, 減価償却累計額:%d%n", date, depreciatedBalance);
	}
}
