package economy.player;

import java.util.Set;

import economy.player.PrivateBusiness;
import economy.enumpack.PrivateBusinessAccountTitle;
import economy.enumpack.Industry;
import economy.enumpack.Product;

public class Retail extends PrivateBusiness {

	public Retail(Industry industry, Set<Product> products) {
		super(industry, products);
	}

	PrivateBusinessAccountTitle saleAccount() {
		return PrivateBusinessAccountTitle.CASH;
	}
}
