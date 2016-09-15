package economy.account;

import economy.account.Account;
import economy.enumpack.AccountTitle;

/**
 * 複式簿記会計のインターフェース
 */
public interface DoubleEntryAccount<T extends Enum<T> & AccountTitle> extends Account<T> {
}
