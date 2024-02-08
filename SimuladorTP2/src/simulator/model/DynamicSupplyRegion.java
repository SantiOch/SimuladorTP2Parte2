package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region{
	
	private double _food;
	private double _factor;
//	private int _hervivores; No se si tener numero de hervívoros o no
	
	public DynamicSupplyRegion(double food, double factor) {
		this._food = food;
		this._factor = factor;
	}
	
	@Override
	public double get_food(Animal a, double dt) {
		
		if(a.get_diet() == Diet.CARNIVORE) {
		
			return 0.0;
		}
		
		//FIXME cambiar para que solo sean los hervívoros
		int n = this.animalList.size();
		
		double food_returned = Math.min(_food, 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt);
		
		this._food -= food_returned;
		
		return food_returned;
	}
	
	@Override
	public void update(double dt) {
		
		// TODO incrementar con probabilidad de 0,5 la comida por dt *  this._factor
		// Creo que está bien, preguntar a Pablo, está bien (he preguntado)
		
		if(Utils._rand.nextBoolean()) this._food += dt * this._factor;
	}
}
