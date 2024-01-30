package simulator.model;

public class DefaultRegion extends Region{
	
	
	@Override
	public double get_food(Animal a, double dt) {
		if(a.get_diet() == Diet.CARNIVORE) {
		
			return 0.0;
		}
		
		//TODO cambiar para que solo sean los hervívoros
		int n = this.animalList.size();
		
		return 60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt;
	}
}
