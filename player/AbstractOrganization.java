package economy.player;

import java.util.Set;
import java.util.HashSet;

import economy.player.AbstractSubject;
import economy.player.Organization;
import economy.player.Parson;
import economy.account.PrivateBusinessAccount;

/**
 * 組織インターフェースの骨格実装クラスです
 * 職員の管理を行います
 */
public class AbstractOrganization extends AbstractSubject implements Organization {
	private Set<Parson> employers; // 社員のリスト
	private Map<LocalDate, Set<Parson>> workingRecord; // 出勤記録

	public AbstractOrganization() {
		super(PrivateBusinessAccount.newInstance());
		employer = new HashSet<Parson>();
	}

	public AbstractOrganization employ(Parson parson) {
		employers.add(parson);
		return this;
	}
	public AbstractOrganization fire(Parson parson) {
		employers.remove(parson);
		return this;
	}
}
