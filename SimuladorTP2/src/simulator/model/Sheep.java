package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{

	private final static String _gcode = "Sheep";
	private final static double _sight_range = 40.0;
	private final static double _init_speed = 35.0;
	private final static double _max_age = 8.0;
	private final static double _min_energy = 0.0;
	private final static double _max_energy = 100.0;
	private final static double _min_desire = 0.0;
	private final static double _max_desire = 100.0;
	private final static double _min_distance = 8.0;
	private final static double _speed_variance = 0.007;
	private final static double _energy_multiplier = 20.0;
	private final static double _desire_multiplier = 40.0;
	private final static double _mate_minimum = 65.0;
	private final static double _running_multiplier = 2.0;
	private final static double _running_tiredness_multiplier = 1.2;
	private final static double _baby_probability = 0.9;

	private Animal _danger_source;
	private final SelectionStrategy _danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy,
			Vector2D pos) {
		super(_gcode, Diet.HERBIVORE, _sight_range, _init_speed, mate_strategy, pos);
		this._danger_strategy = danger_strategy;
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1,p2);
		this._danger_source = null;
		this._danger_strategy = p1._danger_strategy;
	}

	@Override
	public void update(double dt) {

		//Sí está muerto
		if(super.get_state() == State.DEAD) return;

		//Diferentes updates en función del estado
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

		//Ajustar posicion si está fuera del mapa
		if(super.isOut()) {
			this._pos = adjustPosition(this.get_position().getX(), this.get_position().getY());
		}

		if(this._energy <= _min_energy || this._age >= _max_age) {
			this._state = State.DEAD;
		}

		if(this._state != State.DEAD) {
			this._energy += this.get_region_mngr().get_food(this, dt);
			if(this._energy < _min_energy) this._energy = _min_energy;
			if(this._energy > _max_energy) this._energy = _max_energy;		
		}

	}

	private void updateNormal(double dt) {

		//Avanzar
		super.advance(dt, _min_distance,
				_max_energy, _min_energy, _energy_multiplier,
				_desire_multiplier, _min_desire, _max_desire, _speed_variance);
		//Cambio de estado
		if(_danger_source == null) {

			this._danger_source = 
					this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
							(Animal other) -> other.get_diet() == Diet.CARNIVORE));
		}

		if(this._danger_source != null) {
			this._state = State.DANGER;
			this._mate_target = null;
		}else if(this._desire > _mate_minimum) {
			this._state = State.MATE;
		}
	}

	private void updateDanger(double dt) {

		//Danger source ha muerto
		if(this._danger_source != null && this._danger_source.get_state() == State.DEAD) {
			this._danger_source = null;
		} 

		//Avanzar ya que danger source es nulo
		if(this._danger_source == null) {

			super.advance(dt, _min_distance,
					_max_energy, _min_energy, _energy_multiplier,
					_desire_multiplier, _min_desire, _max_desire, _speed_variance);
		}else {

			this._dest = this._pos.plus(this._pos.minus(_danger_source.get_position()).direction());

			super.advanceRunning(dt, _running_multiplier, _speed_variance,
					_energy_multiplier, _running_tiredness_multiplier, _min_energy,
					_max_energy, _desire_multiplier, _min_desire, _max_desire );

			if(this._danger_source == null || this._danger_source.get_position().distanceTo(this._pos) > this.get_sight_range()) {

				//Busca un nuevo danger source
				this._danger_source = 
						this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
								(Animal other) -> other.get_diet() == Diet.CARNIVORE));
			}

			//Comprobar otra vez ya que puede haber escogido un nuevo danger source
			if(this._danger_source == null) {
				if(this._desire > _mate_minimum) {
					this._state = State.MATE;

				}else {
					this._state = State.NORMAL;
					this._mate_target = null;
				}
			}
		}
	}

	private void updateMate(double dt) {

		super.updateMateTarget();

		//No ha conseguido encontrar otro animal para emparejarse
		if (this._mate_target == null) {

			super.advance(dt, _min_distance,
					_max_energy, _min_energy, _energy_multiplier,
					_desire_multiplier, _min_desire, _max_desire, _speed_variance);
		} else {
			this._dest = this._mate_target.get_position();

			super.advanceRunning(dt, _running_multiplier, _speed_variance,
					_energy_multiplier, _running_tiredness_multiplier, _min_energy,
					_max_energy, _desire_multiplier, _min_desire, _max_desire);

			if (this._pos.distanceTo(this._mate_target.get_position()) < _min_distance) {

				super.resetDesire();
				this._mate_target.resetDesire();

				if (this._baby == null && Utils._rand.nextDouble() < _baby_probability) {

					//Crea un nuevo bebé con probabilidad de 0.9
					this._baby = new Sheep(this, this._mate_target);

				}

				this._mate_target = null;
			}
		}

		if (this._danger_source == null) {

			this._danger_source =
					this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
							(Animal other) -> other.get_diet() == Diet.CARNIVORE));
		}

		if (this._danger_source != null) {

			this._state = State.DANGER;
			this._mate_target = null;

		} else if (this._desire < _mate_minimum) {

			this._state = State.NORMAL;
		}
	}
}
