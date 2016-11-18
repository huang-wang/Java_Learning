package BinConlector;

public class Aluminum extends Trash {
	private double value = 2.0;
	public Aluminum(double weight) {
		super(weight);
	}

	@Override
	public double getVal() {
		return value;
	}

}
