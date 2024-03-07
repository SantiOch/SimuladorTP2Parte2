package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal{

	private final static String GCODE = "Wolf";
	private final static double SIGHT_RANGE = 50.0;
	private final static double INIT_SPEED = 60.0;
	private final static double MAX_AGE = 14.0;
	private final static double MIN_ENERGY = 0.0;
	private final static double MAX_ENERGY = 100.0;
	private final static double MIN_DESIRE = 0.0;
	private final static double MAX_DESIRE = 100.0;
	private final static double MIN_DISTANCE = 8.0;
	private final static double SPEED_VARIANCE = 0.007;
	private final static double ENERGY_MULTIPLIER = 18.0;
	private final static double DESIRE_MULTIPLIER = 30.0;
	private final static double MATE_MINIMUM = 65.0;
	private final static double RUNNING_MULTIPLIER = 3.0;
	private final static double PREY_ENERGY = 50.0;
	private final static double CHANGE_STATE_ENERGY = 50.0;
	private final static double RUNNING_TIREDNESS_MULTIPLIER = 1.2;
	private final static double BABY_PROBABILITY = 0.9;
	private final static double BABY_ENERGY = 10;

	private Animal _hunt_target;
	private final SelectionStrategy _hunting_strategy;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy,
			Vector2D pos) {
		super(GCODE, Diet.CARNIVORE, SIGHT_RANGE, INIT_SPEED, mate_strategy, pos);
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

		//Ajustar posición si está fuera del mapa
		if(super.isOut()) {
			this._pos = adjustPosition(this.get_position().getX(), this.get_position().getY());
		}

		if(this._energy <= MIN_ENERGY || this._age > MAX_AGE) {
			this._state = State.DEAD;
		}

		if(this._state != State.DEAD) {
			this._energy += this.get_region_mngr().get_food(this, dt);
			if(this._energy < MIN_ENERGY) this._energy = MIN_ENERGY;
			if(this._energy > MAX_ENERGY) this._energy = MAX_ENERGY;
		}
	}

	private void updateMate(double dt) {

		super.updateMateTarget();

		if(this._mate_target == null) {
			super.advance(dt, MIN_DISTANCE,
					MAX_ENERGY, MIN_ENERGY, ENERGY_MULTIPLIER,
					DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE, SPEED_VARIANCE);
		}else {

			this._dest = this._mate_target.get_position();

			super.advanceRunning(dt, RUNNING_MULTIPLIER, SPEED_VARIANCE,
					ENERGY_MULTIPLIER, RUNNING_TIREDNESS_MULTIPLIER, MIN_ENERGY,
					MAX_ENERGY, DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE);

			if(this._pos.distanceTo(this._mate_target.get_position()) < MIN_DISTANCE) {
				super.resetDesire();
				this._mate_target.resetDesire();

				if(this._baby == null && Utils._rand.nextDouble() < BABY_PROBABILITY) {

					//Crea un nuevo bebé con probabilidad de 0.9
					this._baby = new Wolf(this, this._mate_target);

					this._energy -= BABY_ENERGY;
					
				}
				this._mate_target.resetTarget();
				this._mate_target = null;
				//Actualiza estado si está con más de 50 de energía
				if(this._energy < CHANGE_STATE_ENERGY) {
					this._state = State.HUNGER;
					this._hunt_target = null;
                }
				if(this._desire < MATE_MINIMUM) {
					this._state = State.NORMAL;
					this._hunt_target = null;
                }
			}
		}
	}

	private void updateHunger(double dt) {

		if(this._hunt_target == null //No tiene hunt target
				|| this._hunt_target._state == State.DEAD //Ha muerto
				|| this._hunt_target.get_position().distanceTo(this._pos) > this.get_sight_range()/*Está fuera del campo visual*/) {
			
			//Busca a un nuevo animal para cazarlo
			this._hunt_target = this._hunting_strategy.select(this, this.get_region_mngr().get_animals_in_range(this,
					(Animal other)-> other.get_diet() == Diet.HERBIVORE));
		}

		if(this._hunt_target == null) {

			super.advance(dt, MIN_DISTANCE,
					MAX_ENERGY, MIN_ENERGY, ENERGY_MULTIPLIER,
					DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE, SPEED_VARIANCE);

		}else {

			this._dest = this._hunt_target.get_position();

			super.advanceRunning(dt, RUNNING_MULTIPLIER, SPEED_VARIANCE,
					ENERGY_MULTIPLIER, RUNNING_TIREDNESS_MULTIPLIER, MIN_ENERGY,
					MAX_ENERGY, DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE);
			//Ver si mata o no al hunt target
			if(this._pos.distanceTo(this._hunt_target.get_position()) < MIN_DISTANCE) {

				this._hunt_target._state = State.DEAD;
				this._hunt_target = null;

				this._energy += PREY_ENERGY;

				if(this._energy < MIN_ENERGY) this._energy = MIN_ENERGY;
				if(this._energy > MAX_ENERGY) this._energy = MAX_ENERGY;
			}

			//Actualiza estado si está con más de 50 de energía
			if(this._energy > CHANGE_STATE_ENERGY) {
				if(this._desire > MATE_MINIMUM) {
					this._state = State.MATE;

                }else {
					this._state = State.NORMAL;
                }
                this._hunt_target = null;
                this._mate_target = null;
            }
		}
	}

	private void updateNormal(double dt) {

		//Avanzar
		super.advance(dt, MIN_DISTANCE,
				MAX_ENERGY, MIN_ENERGY, ENERGY_MULTIPLIER,
				DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE, SPEED_VARIANCE);

		if(this._energy < CHANGE_STATE_ENERGY) {

			//Cambio de estado a HUNGER y cambio de objetivo a null
			this._state = State.HUNGER;
			this._mate_target = null; 

		}else if( this._desire > MATE_MINIMUM) {

			//Cambio de estado a MATE y cambio de objetivo a null
			this._state = State.MATE;
			this._hunt_target = null;
			this._mate_target = null;
		}
	}
}

