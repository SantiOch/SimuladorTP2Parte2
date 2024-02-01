package simulator.model;

import simulator.misc.Vector2D;

public class Wolf extends Animal{
	private static final String gcode = "Wolf";
	
	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;
	
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy,
			Vector2D pos) {
		super(gcode, Diet.CARNIVORE, 50.0, 60.0, mate_strategy, pos);
		this._hunting_strategy = hunting_strategy;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1,p2);
		this._hunt_target = null;
		this._hunting_strategy = p1._hunting_strategy;
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}
	
}

