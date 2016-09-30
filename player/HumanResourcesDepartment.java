package economy.player;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;

import economy.player.AbstractSubject;
import economy.player.Organization;
import economy.player.Parson;
import economy.account.PrivateBusinessAccount;

/**
 * 人事部を表すクラスです
 * 職員の管理を行います
 */
public class HumanResourcesDepartment {
	private Set<Parson> employers; // 社員のリスト
	private Map<LocalDate, Set<Parson>> workingRecord; // 出勤記録

	public HumanResourcesDepartment() {
		employers = new HashSet<Parson>();
		workingRecord = new HashMap<LocalDate, Set<Parson>>();
	}

	/**
	 * 職員として採用します
	 */
	public HumanResourcesDepartment employ(Parson parson) {
		employers.add(parson);
		return this;
	}
	/**
	 * 職員を解雇します
	 */
	public HumanResourcesDepartment fire(Parson parson) {
		employers.remove(parson);
		return this;
	}

	/**
	 * 勤務記録をつけます
	 */
	public HumanResourcesDepartment add(LocalDate date, Parson parson) {
		if (!workingRecord.containsKey(date)) workingRecord.put(date, new HashSet<Parson>());

		workingRecord.get(date).add(parson);
		return this;
	}
}
