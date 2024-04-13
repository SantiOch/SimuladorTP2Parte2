package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region{
	
	private final static double _exp_multiplier = 60.0;
	private final static double _herbivores_subtraction = 5.0;
	private final static double _food_multiplier = 2.0;

	private double _food;
	private double _factor;
	
	public DynamicSupplyRegion(double food, double factor) {
		super();
		this._food = food;
		this._factor = factor;
	}

	@Override
	public double get_food(Animal a, double dt) {
		
		if(a.get_diet() == Diet.CARNIVORE) {
		
			return 0.0;
		}
		
		int n = super.getHerbivores();
		
		double food_returned = Math.min(_food, _exp_multiplier * Math.exp(-Math.max(0, n - _herbivores_subtraction) * _food_multiplier) * dt);
		
		this._food -= food_returned;
		
		return food_returned;
	}
	
	@Override
	public void update(double dt) {
		
		// Incrementar con probabilidad de 0,5 la comida por dt *  this._factor
		// Creo que está bien, preguntar a Pablo, está bien (he preguntado)
		
		if(Utils._rand.nextBoolean()) this._food += dt * this._factor;
	}
	
	@Override
	public String toString() {
		
		return "dynamic";
	}
}
