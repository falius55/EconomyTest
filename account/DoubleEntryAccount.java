package economy.account;

import economy.account.Account;
import economy.enumpack.AccountTitle;

/**
 * 複式簿記会計のインターフェース
 */
public interface DoubleEntryAccount<T extends Enum<T> & AccountTitle> extends Account<T> {
	// 資産合計
	int assets();

	// 費用合計
	int expense();

	// 収益合計
	int revenue();

	// 負債合計
	int liabilities();
}
