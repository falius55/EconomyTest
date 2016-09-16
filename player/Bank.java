package economy.player;

import economy.player.Organization;

/**
 * 銀行のインターフェース
 */
public interface Bank extends Organization {

	/**
	 * お金を預かる
	 */
	keep(int money);

	/**
	 * お金を払い出す
	 */
	paidOut(int money);
}
