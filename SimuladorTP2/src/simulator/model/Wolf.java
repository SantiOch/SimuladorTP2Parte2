package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal{

	private static final String gcode = "Wolf";
	private static final double sight_range = 50.0;
	private static final double init_speed = 60.0;

	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;


	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy,
			Vector2D pos) {
		super(gcode, Diet.CARNIVORE, sight_range, init_speed, mate_strategy, pos);
		this._hunting_strategy = hunting_strategy;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1,p2);
		this._hunt_target = null;
		this._hunting_strategy = p1._hunting_strategy;
	}

	@Override
	public void update(double dt) {

		if(this._state == State.DEAD) {
			return;
		}

		switch(super.get_state()) {

		case NORMAL:

			this.updateNormal(dt);

			break;
		case HUNGER:

			this.updateHunger(dt);

			break;
		case MATE:

			this.updateMate(dt);

			break;
		default:

			break;
		}

		//TODO Ajustar la posicion si está fuera del mapa

		if(this._energy == 0.0 || this._age > 14.0) {
			this._state = State.DEAD;
		}

		if(this._state != State.DEAD) {
			this._energy += this.get_region_mngr().get_food(this, dt);
			if(this._energy < 0.0) this._energy = 0.0;
			if(this._energy > 100.0) this._energy = 100.0;		
		}
	}

	private void updateMate(double dt) {

		if(this._mate_target != null && (this._mate_target.get_state() == State.DEAD /* TODO || Está fuera del campo visual*/)) {
			this._mate_target = null;
		}

		if(this._mate_target == null) {
			// TODO Buscar nuevo mate target
		}

		if(this._mate_target == null) {
			this.advance(dt);
		}else {

			this._dest = this._mate_target.get_position();

			this.advanceRunning(dt);

			if(this._pos.distanceTo(this._mate_target.get_position()) < 8.0) {
				super.resetDesire();
				this._mate_target.resetDesire();
				
				if(this._baby == null && Utils._rand.nextDouble() < 0.9) {
					
					this._baby = new Wolf(this, this._mate_target);
					//TODO Crear nuevo bebé con probabilidad de 0.9, usando new Wolf(this, this._mate_target)
				}
				
				this._energy -= 10;
				this._mate_target = null;
			}

			//Actualiza estado si está con más de 50 de energía
			if(this._energy < 50.0) {
				this._state = State.HUNGER;
				this._hunt_target = null;
				this._mate_target = null;
			}
			if(this._desire < 65.0) {
				this._state = State.NORMAL;
				this._hunt_target = null;
			}
		}
	}


	private void updateHunger(double dt) {

		if(this._hunt_target == null || this._hunt_target._state == State.DEAD /* TODO || está fuera del campo visual*/) {
			//TODO Buscar nuevo animal para cazarlo
		}

		if(this._hunt_target == null) {
			this.advance(dt);
		}else {

			this._dest = this._hunt_target.get_position();

			this.advanceRunning(dt);

			//Ver si mata o no al hunt target
			if(this._pos.distanceTo(this._hunt_target.get_position()) < 8.0) {

				this._hunt_target._state = State.DEAD;
				this._hunt_target = null;

				this._energy += 50;

				if(this._energy < 0.0) this._energy = 0.0;
				if(this._energy > 100.0) this._energy = 100.0;
			}

			//Actualiza estado si está con mas de 50 de energía
			if(this._energy > 50.0) {
				if(this._desire > 65.0) {
					this._state = State.MATE;
					this._hunt_target = null;
				}else {
					this._state = State.NORMAL;
					this._hunt_target = null;
					this._mate_target = null;
				}
			}
		}
	}

	private void updateNormal(double dt) {

		//Avanzar
		this.advance(dt);

		if(this._energy < 50.0) {

			//Cambio de estado a HUNGER y cambio de objetivo a null
			this._state = State.HUNGER;
			this._mate_target = null; 

		}else if( this._desire > 65.0) {

			//Cambio de estado a MATE y cambio de objetivo a null
			this._state = State.MATE;
			this._hunt_target = null;
		}
	}

	void advance(double dt) {

		//Si está cerca escoge otra
		if(super.get_position().distanceTo(super.get_destination()) < 8.0) {
			this._dest = super.randomPos();
		}

		this.move(super.get_speed() * dt * Math.exp((super.get_energy() - 100.0) * 0.007));
		this._age += dt;

		this._energy -= 18.0 * dt;

		if(this._energy < 0.0) this._energy = 0.0;
		if(this._energy > 100.0) this._energy = 100.0;

		this._desire += 30.0 * dt;

		if(this._desire < 0.0) this._desire = 0.0;
		if(this._desire > 100.0) this._desire = 100.0;
	}

	void advanceRunning(double dt) {

		this.move(3 * super.get_speed() * dt * Math.exp((super.get_energy() - 100.0) * 0.007));
		this._age += dt;

		this._energy -= 18.0 * 1.2 * dt;

		if(this._energy < 0.0) this._energy = 0.0;
		if(this._energy > 100.0) this._energy = 100.0;

		this._desire += 30.0 * dt;

		if(this._desire < 0.0) this._desire = 0.0;
		if(this._desire > 100.0) this._desire = 100.0;
	}
}

