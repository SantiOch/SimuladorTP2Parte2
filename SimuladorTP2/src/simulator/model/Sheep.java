package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{

	private static final String _gcode = "Sheep";
	private static final double _sight_range = 40.0;
	private static final double _init_speed = 35.0;
	private static final double _max_age = 8.0;
	private static final double _min_energy = 0.0;
	private static final double _max_energy = 100.0;
	private static final double _min_desire = 0.0;
	private static final double _max_desire = 100.0;
	private static final double _min_distance = 8.0;
	private static final double _speed_variance = 0.007;
	private static final double _energy_multiplier = 20.0;
	private static final double _desire_multiplier = 40.0;
	private static final double _mate_minimum = 65.0;
	private static final double _running_multiplier = 2.0;
	private static final double _running_tiredeness_multiplier = 1.2;
	private static final double _baby_probability = 0.9;

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;
	
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

		//Si está muerto
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
			if(this._energy < _min_energy) this._energy = _max_energy;
			if(this._energy > _max_energy) this._energy = _max_energy;		
		}

	}

	private void updateNormal(double dt) {

		//Avanzar
		this.advance(dt);

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
			this._danger_source = null;
		}
	}

	private void updateDanger(double dt) {

		//Danger source ha muerto
		if(this._danger_source != null && this._danger_source.get_state() == State.DEAD) {
			this._danger_source = null;
		} 

		//Avanzar ya que danger source es nulo
		if(this._danger_source == null) {

			this.advance(dt);

		}else {

			this._dest = this._pos.plus(this._pos.minus(_danger_source.get_position()).direction());
			this.advanceRunning(dt);

			if(this._danger_source == null || this._danger_source.get_position().distanceTo(this._pos) > this.get_sight_range()) {

				//Busca un nuevo danger source
				this._danger_source = 
						this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
								(Animal other) -> other.get_diet() == Diet.CARNIVORE));
			}

			//Comprobar otra vez ya que puede haver escogido un nuevo danger source
			if(this._danger_source == null) {
				if(this._desire > _mate_minimum) {
					this._state = State.MATE;
					this._danger_source = null;

				}else {
					this._state = State.NORMAL;
					this._mate_target = null;
				}
			}
		}
	}

	private void updateMate(double dt) {

		//Está muerto o no está en el campo de visión
		if((this._mate_target != null && this._mate_target.get_state() == State.DEAD)
				|| (this._mate_target != null && this._mate_target.get_position().distanceTo(this._pos) > this.get_sight_range())){

			this._mate_target = null;
		}

		if(this._mate_target == null) {
			//Busca a otro animal para emparejarse con la estrategia que tenga
			this._mate_target = this._mate_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
					(Animal other) -> other.get_genetic_code() == this.get_genetic_code()));
		}

		//No ha conseguido encontrar otro animal para emparejarse
		if(this._mate_target == null) {

			this.advance(dt);

		}else {
			this._dest = this._mate_target.get_position();
			this.advanceRunning(dt);

			if(this._pos.distanceTo(this._mate_target.get_position()) < _min_distance) {

				super.resetDesire();
				this._mate_target.resetDesire();

				if(this._baby == null && Utils._rand.nextDouble() < _baby_probability) {

					//Crea un nuevo bebé con probabilidad de 0.9
					this._baby = new Sheep(this, this._mate_target);

				}

				this._mate_target = null;
			}
		}

		if(this._danger_source == null) {

			this._danger_source = 
					this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
							(Animal other) -> other.get_diet() == Diet.CARNIVORE));		
		}

		if(this._danger_source != null) {

			this._state = State.DANGER;
			this._mate_target = null;

		}else if (this._desire < _mate_minimum) {

			this._state = State.NORMAL;
			this._danger_source = null;
		}
	}

	void advanceRunning(double dt) {
		this.move(_running_multiplier * super.get_speed() * dt * Math.exp((super.get_energy() - _max_energy) * _speed_variance));
		this._age += dt;

		this._energy -= _energy_multiplier * _running_tiredeness_multiplier * dt;

		if(this._energy < _min_energy) this._energy = _min_energy;
		if(this._energy > _max_energy) this._energy = _max_energy;

		this._desire += _desire_multiplier * dt;

		if(this._desire < _min_desire) this._desire = _min_desire;
		if(this._desire > _max_desire) this._desire = _max_desire;
	}

	void advance(double dt) {

		if(super.get_position().distanceTo(super.get_destination()) < _min_distance) {
			this._dest = super.randomPos();
		}

		this.move(super.get_speed() * dt * Math.exp((super.get_energy() - _max_energy) * _speed_variance));
		this._age += dt;

		this._energy -= _energy_multiplier * dt;

		if(this._energy < _min_energy) this._energy = _min_energy;
		if(this._energy > _max_energy) this._energy = _max_energy;

		this._desire += _desire_multiplier * dt;

		if(this._desire < _min_desire) this._desire = _min_desire;
		if(this._desire > _max_desire) this._desire = _max_desire;

	}
}
