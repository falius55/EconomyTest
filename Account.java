// T: 科目を表すenum
public interface Account<T extends Enum<T>> {

	// ２つの会計を合わせ、新しい会計を返す。結婚、合併
	Account<T> merge(Account<T> account);

	void add(T item, int amount);

	// 資産合計
	int assets();

	// 費用合計
	int expense();

	// 収益合計
	int revenue();

	// 負債合計
	int liabilities();

	Account<T> newInstance();

	int get(T item);

	// 扱っている科目一覧を返す
	T[] items();
}
