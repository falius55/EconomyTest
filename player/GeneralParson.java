package economy.player;

import economy.player.AbstractSubject;
import economy.player.Organization
import economy.player.Parson;
import economy.player.LocalGoverment;
import economy.player.PrivateBusiness;

/**
 * 一般人を表すクラス
 */
public class GeneralParson extends AbstractSubject implements Parson {
	public enum Sex { MALE, FEMALE}

	private int age; // 年齢
	private Sex sex; // 性別
	private Set<Parson> family; // 家族
	private LocalGoverment local; // 所在地
	private Organization workPlace; // 勤務先

	public GeneralParson() {
	}
	
	public GeneralParson buy(String product) {
	}

	public Set<PrivateBusiness> findStore(String product) {
		return PrivateBusiness.stream()
			.filter(pb -> pb.isSale(product))
			.collect(Collectors.toSet());
	}
}
