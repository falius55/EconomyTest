package economy.player;

/**
 * 組織のインターフェース
 */
public interface Organization extends Subject {

	/**
	 * 人を雇用します
	 */
	Organization employ(Parson parson);

	/**
	 * 社員を解雇します
	 */
	Organization fire(Parson parson);

	/**
	 * 給与を支払います
	 * @param parson 支払対象社員
	 * @return 計算された給与額
	 */
	int paySalary(Parson parson);
}
