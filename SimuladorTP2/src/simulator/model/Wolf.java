package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal{

	private final static String _gcode = "Wolf";
	private final static double _sight_range = 50.0;
	private final static double _init_speed = 60.0;
	private final static double _max_age = 14.0;
	private final static double _min_energy = 0.0;
	private final static double _max_energy = 100.0;
	private final static double _min_desire = 0.0;
	private final static double _max_desire = 100.0;
	private final static double _min_distance = 8.0;
	private final static double _speed_variance = 0.007;
	private final static double _energy_multiplier = 18.0;
	private final static double _desire_multiplier = 30.0;
	private final static double _mate_minimum = 65.0;
	private final static double _running_multiplier = 3.0;
	private final static double _prey_energy = 50.0;
	private final static double _change_state_energy = 50.0;
	private final static double _running_tiredness_multiplier = 1.2;
	private final static double _baby_probability = 0.9;
	private final static double _baby_energy = 10;

	private Animal _hunt_target;
	private final SelectionStrategy _hunting_strategy;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy,
			Vector2D pos) {
		super(_gcode, Diet.CARNIVORE, _sight_range, _init_speed, mate_strategy, pos);
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

		if(this._energy <= _min_energy || this._age > _max_age) {
			this._state = State.DEAD;
		}

		if(this._state != State.DEAD) {
			this._energy += this.get_region_mngr().get_food(this, dt);
			if(this._energy < _min_energy ) this._energy = _min_energy ;
			if(this._energy > _max_energy ) this._energy = _max_energy;		
		}
	}

	private void updateMate(double dt) {

		super.updateMateTarget();

		if(this._mate_target == null) {
			super.advance(dt, _min_distance,
					_max_energy, _min_energy, _energy_multiplier,
					_desire_multiplier, _min_desire, _max_desire, _speed_variance);
		}else {

			this._dest = this._mate_target.get_position();

			super.advanceRunning(dt, _running_multiplier, _speed_variance,
					_energy_multiplier, _running_tiredness_multiplier, _min_energy,
					_max_energy, _desire_multiplier, _min_desire, _max_desire );

			if(this._pos.distanceTo(this._mate_target.get_position()) < _min_distance) {
				super.resetDesire();
				this._mate_target.resetDesire();

				if(this._baby == null && Utils._rand.nextDouble() < _baby_probability) {

					//Crea un nuevo bebé con probabilidad de 0.9
					this._baby = new Wolf(this, this._mate_target);

					this._energy -= _baby_energy;
					
				}
				this._mate_target.resetTarget();
				this._mate_target = null;
				//Actualiza estado si está con más de 50 de energía
				if(this._energy < _change_state_energy) {
					this._state = State.HUNGER;
					this._hunt_target = null;
                }
				if(this._desire < _mate_minimum) {
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

			super.advance(dt, _min_distance,
					_max_energy, _min_energy, _energy_multiplier,
					_desire_multiplier, _min_desire, _max_desire, _speed_variance);

		}else {

			this._dest = this._hunt_target.get_position();

			super.advanceRunning(dt, _running_multiplier, _speed_variance,
					_energy_multiplier, _running_tiredness_multiplier, _min_energy,
					_max_energy, _desire_multiplier, _min_desire, _max_desire );
			//Ver si mata o no al hunt target
			if(this._pos.distanceTo(this._hunt_target.get_position()) < _min_distance) {

				this._hunt_target._state = State.DEAD;
				this._hunt_target = null;

				this._energy += _prey_energy;

				if(this._energy < _min_energy) this._energy = _min_energy;
				if(this._energy > _max_energy) this._energy = _max_energy;
			}

			//Actualiza estado si está con más de 50 de energía
			if(this._energy > _change_state_energy) {
				if(this._desire > _mate_minimum) {
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
		super.advance(dt, _min_distance,
				_max_energy, _min_energy, _energy_multiplier,
				_desire_multiplier, _min_desire, _max_desire, _speed_variance);

		if(this._energy < _change_state_energy) {

			//Cambio de estado a HUNGER y cambio de objetivo a null
			this._state = State.HUNGER;
			this._mate_target = null; 

		}else if( this._desire > _mate_minimum) {

			//Cambio de estado a MATE y cambio de objetivo a null
			this._state = State.MATE;
			this._hunt_target = null;
			this._mate_target = null;
		}
	}
}

