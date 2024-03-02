package simulator.model;

public class DefaultRegion extends Region{
	
	private final static double _exp_multiplier = 60.0;
	private final static double _herbivores_subtraction = 5.0;
	private final static double _food_multiplier = 2.0;
	
	@Override
	public double get_food(Animal a, double dt) {
		
		if(a.get_diet() == Diet.CARNIVORE) {
		
			return 0.0;
		}
		
		int n = super.getHerbivores();
		
		return _exp_multiplier * Math.exp(-Math.max(0, n - _herbivores_subtraction) * _food_multiplier) * dt;
	}
	
	@Override
	public void update(double dt) {
		//No hace nada el update
	}
}
