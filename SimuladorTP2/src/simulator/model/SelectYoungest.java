package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy{

	@Override
	public Animal select(Animal a, List<Animal> as) {
		
		if(as.isEmpty()) return null;
		
		Animal youngest = as.get(0);
		
		for(int i = 1; i < as.size(); i++) {
			if(youngest.get_age() > as.get(i).get_age()) youngest = as.get(i);
		}
		
		return youngest;
	}
}
