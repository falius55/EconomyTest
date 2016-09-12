import java.util.Map;
import java.util.EnumMap;

public class PrivateBusinessAccount extends AbstractAccount<PrivateBusinessAccount.Item> {
	// 会計細目
	public enum Item implements AbstractAccount.iItem {
		CASH(Type.ASSETS), // 現金
		SALES(Type.REVENUE); // 売上高

		private final Type type;
		private static final Item defaultItem = CASH;
		Item(Type type) {
			this.type = type;
		}
		public Type type() {
			return this.type;
		}
		public static Item defaultItem() {
			return defaultItem;
		}
	}

	protected PrivateBusinessAccount() {
		super(Item.class);
	}

	@Override
	public PrivateBusinessAccount merge(Account<Item> account) {
		if (!(account instanceof PrivateBusinessAccount)) throw new IllegalArgumentException();
		return (PrivateBusinessAccount)super.merge(account);
	}
	@Override
	public Item defaultItem() {
		return Item.defaultItem();
	}
	@Override
	public Item[] items() {
		return Item.values();
	}

	@Override
	public PrivateBusinessAccount newInstance() {
		return new PrivateBusinessAccount();
	}

	public static void main(String[] args) {
		Account account = new PrivateBusinessAccount();
		System.out.println(account);
	}
}
