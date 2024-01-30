package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal{

	private static final String gcode = "Sheep";
	
	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;
	
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy,
			Vector2D pos) {
		super(gcode, Diet.HERBIVORE, 40.0, 35.0, mate_strategy, pos);
		this._danger_strategy = danger_strategy;
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1,p2);
		this._danger_source = null;
		this._danger_strategy = p1._danger_strategy;
	}
	
}
