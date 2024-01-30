package simulator.model;

public class DynamicSupplyRegion extends Region{
	
	private double _food;
	private double _factor;
	
	public DynamicSupplyRegion(double food, double factor) {
		this._food = food;
		this._factor = factor;
	}
	
	@Override
	public double get_food(Animal a, double dt) {
		
		if(a.get_diet() == Diet.CARNIVORE) {
		
			return 0.0;
		}
		
		//TODO cambiar para que solo sean los hervívoros
		int n = this.animalList.size();
		
		double food_returned = Math.min(_food,60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt);
		
		this._food -= food_returned;
		
		return food_returned;
		
	}
	
	@Override
	public void update(double dt) {
		// TODO incrementar con probabilidad de 0,5 la comida por dt*_factor
	
		
	}
}
