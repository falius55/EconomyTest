package economy.market;

import java.time.LocalDate;

/**
 * 市場の情報を保持するためのシングルトンクラス
 */
public class MarketInfomation {
	public static MarketInfomation INSTANCE;
	
	private LocalDate date;

	static {
		INSTANCE = new MarketInfomation(LocalDate.now());
	}

	private MarketInfomation(LocalDate date) {
		this.date = date;
	}

	public MarketInfomation nextDay() {
		this.date = date.plusDays(1);
		return this;
	}

	public LocalDate nowDate() {
		return date;
	}
}
