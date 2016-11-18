package BinConlector;

public class Paper extends Trash {
	private double value = 1.5;
	public Paper(double weight) {
		super(weight);
	}

	@Override
	public double getVal() {
		return value;
	}

}
