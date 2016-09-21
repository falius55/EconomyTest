package economy.player;

/**
 * 民間であることを表すクラス
 */
public interface Private {

	/**
	 * 納税額を計算します
	 */
	int computeTex();

	/**
	 * 納税します
	 */
	void payTax(int amount);

}
