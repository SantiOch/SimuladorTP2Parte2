package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy{

	@Override
	public Animal select(Animal a, List<Animal> as) {
		
		if(as.isEmpty()) return null;
		
		Animal closest = as.get(0);
		
		double closestDistance = closest.get_position().distanceTo(a.get_position());
		double actualDistance;
		
		for(Animal ani: as) {
			
			actualDistance = ani.get_position().distanceTo(a.get_position());
			
			if(actualDistance < closestDistance) closest = ani;
		}
		
		return closest;
	}

}
