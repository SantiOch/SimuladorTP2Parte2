package simulator.model;

import java.util.function.Predicate;

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
	
	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		if(super.get_state() == State.DEAD) return;
		
		switch(super.get_state()) {
			
			case NORMAL:
				
				this.updateNormal(dt);
				
				break;
			case DANGER:
				
				this.updateDanger(dt);
				
				break;
			case MATE:
				
				this.updateMate(dt);
				
				break;
			default:
				
				break;
		}
		
	}
	
	private void updateNormal(double dt) {
		
		if(super.get_position().distanceTo(super.get_destination()) < 8.0) {
			this._dest = super.randomPos();
		}
		
		this.move(super.get_speed() * dt * Math.exp((super.get_energy() - 100.0) * 0.007));
		this._age += dt;
		
		this._energy -= 20.0 * dt;
		
		if(this._energy < 0.0) this._energy = 0.0;
		if(this._energy > 100.0) this._energy = 100.0;

		this._desire += 40.0 * dt;
		
		if(this._desire < 0.0) this._desire = 0.0;
		if(this._desire > 100.0) this._desire = 100.0;
		
		if(_danger_source == null) this._danger_source = 
				this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,/*TODO cambiar con el filtro*/ null));
	}
	
	private void updateDanger(double dt) {
		
	}
	
	private void updateMate(double dt) {
		
	}
}
