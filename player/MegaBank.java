package economy.player;

import java.util.Set;
import java.util.HashSet;

import economy.player.Subject;
import economy.player.AbstractSubject;
import economy.player.PrivateBank;

public class MegaBank extends AbstractSubject implements PrivateBank {
	private Set<Subject> clients; // 顧客リスト(企業なども含む)

	public MegaBank() {
		clients = new HashSet<Subject>();
	}
}
