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
 * 借方貸方への記帳
 * 集計
 * 減価償却
 */
public abstract class AbstractDoubleEntryAccount<T extends Enum<T> & AccountTitle> extends AbstractAccount<T> implements DoubleEntryAccount<T> {
	private final Map<AccountType, Map<T, Integer>> accountsBook; // 帳簿(EnumMap) 科目種別のマップ
	private final Set<FixedAsset> fixedAssets; // TODO:建物は科目が別なので、別に保持する

	protected AbstractDoubleEntryAccount(Class<T> clazz) {
		this.accountsBook = initBook(clazz);
		fixedAssets = new HashSet<FixedAsset>();
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
	protected void add(T item, int amount) {
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
	 * 指定した科目種別の総額を計算する
	 * @param type 科目種別
	 * @return 集計結果
	 */
	@Override
	public int get(AccountType type) {
		Map<T, Integer> itemMap = accountsBook.get(type);
		int result = 0;

		for (Integer amount : itemMap.values()) {
			result += amount.intValue();
		}
		return result;
	}
	/**
	 * 指定した勘定科目の金額を返します
	 * @param item 勘定科目
	 * @return 指定した勘定科目の金額
	 */
	@Override
	public int get(T item) {
		Map<T, Integer> itemMap = accountsBook.get(item.type());
		return itemMap.get(item).intValue();
	}

	/**
	 * 帳簿内容の文字列表現を返します
	 */
	@Override
	public String toString() {
		return accountsBook.toString();
	}


	// 以下は固定資産

	/**
	 * 固定資産を追加します
	 * @param dateOfAcquisition 取得日
	 * @param acquisitionCost 取得原価
	 * @param serviceLife 耐用年数
	 */
	protected void addFixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
		fixedAssets.add(new FixedAsset(dateOfAcquisition, acquisitionCost, serviceLife));
	}
	/**
	 * 所有している固定資産全てにおいて、減価償却の処理を行います
	 * より具体的には、dateが償却日である固定資産のみ減価償却し、その償却費の総額を返します。
	 * ただし、帳簿への記帳処理は行いません
	 * @param date 記入日
	 * @return その日の償却額
	 */
	protected int recordFixedAssets(LocalDate date) {
		int amount = 0;
		for (FixedAsset asset : fixedAssets)
			amount += asset.record(date);
		return amount;
	}
	/**
	 * 保有している固定資産の現在価値の総額を計算します
	 */
	protected int fixedAssetsValue() {
		int amount = 0;
		for (FixedAsset asset : fixedAssets)
			amount += asset.presentValue();
		return amount;
	}


	/**
	 * 固定資産の減価償却の計算を行うクラス
	 * 土地は減価償却しないので土地以外
	 */
	private static class FixedAsset {
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

			this.recordMap = new TreeMap<LocalDate, Integer>(); // 償却日でソートされる
		}
		/**
		 * この固定資産の現在の価値を返します
		 */
		private int presentValue() {
			return residualValue + undepreciatedBalance;
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
		 * 減価償却を計上します。引数で渡された日付が計上日でない場合、あるいは未償却残高がすでにない場合は何もせず０を返します
		 * @param date 計上日
		 * @return 計上月額
		 */
		private int record(LocalDate date) {
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
		 * 状態を表形式で表示します
		 */
		private void print() {
			System.out.printf("get:%s, all-amount:%d円, per-amount:%d, life:%d年%n", dateOfAcquisition, acquisitionCost, fixedAmountOfMonths, serviceLife);
			economy.util.TableBuilder tb = new economy.util.TableBuilder("償却回", "日付", "金額");
			int cnt = 1;
			for (LocalDate date : recordMap.keySet())
				tb.insert(cnt++)
					.add(1, date)
					.add(2, recordMap.get(date));
			tb.print();
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
