package gameClient;

import java.util.Comparator;

public class Comperator implements Comparator<Fruit> {
	public Comperator() {
		;
	}

	@Override
	public int compare(Fruit f1, Fruit f2) {
		int max = (int) (f2.getvValue() - f1.getvValue());
		return max;
	}
}
