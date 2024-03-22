package simulator.model;

import simulator.model.Animal;

public interface FoodSupplier {
  double get_food(Animal a, double dt);
}
