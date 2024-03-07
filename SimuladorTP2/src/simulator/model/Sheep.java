package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{

	private final static String GCODE = "Sheep";
	private final static double SIGHT_RANGE = 40.0;
	private final static double INIT_SPEED = 35.0;
	private final static double MAX_AGE = 8.0;
	private final static double MIN_ENERGY = 0.0;
	private final static double MAX_ENERGY = 100.0;
	private final static double MIN_DESIRE = 0.0;
	private final static double MAX_DESIRE = 100.0;
	private final static double MIN_DISTANCE = 8.0;
	private final static double SPEED_VARIANCE = 0.007;
	private final static double ENERGY_MULTIPLIER = 20.0;
	private final static double DESIRE_MULTIPLIER = 40.0;
	private final static double MATE_MINIMUM = 65.0;
	private final static double RUNNING_MULTIPLIER = 2.0;
	private final static double RUNNING_TIREDNESS_MULTIPLIER = 1.2;
	private final static double BABY_PROBABILITY = 0.9;

	private Animal _danger_source;
	private final SelectionStrategy _danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy,
			Vector2D pos) {
		super(GCODE, Diet.HERBIVORE, SIGHT_RANGE, INIT_SPEED, mate_strategy, pos);
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

		if(this._energy <= MIN_ENERGY || this._age >= MAX_AGE) {
			this._state = State.DEAD;
		}

		if(this._state != State.DEAD) {
			this._energy += this.get_region_mngr().get_food(this, dt);
			if(this._energy < MIN_ENERGY) this._energy = MIN_ENERGY;
			if(this._energy > MAX_ENERGY) this._energy = MAX_ENERGY;
		}

	}

	private void updateNormal(double dt) {

		//Avanzar
		super.advance(dt, MIN_DISTANCE,
				MAX_ENERGY, MIN_ENERGY, ENERGY_MULTIPLIER,
				DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE, SPEED_VARIANCE);
		//Cambio de estado
		if(_danger_source == null) {

			this._danger_source = 
					this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
							(Animal other) -> other.get_diet() == Diet.CARNIVORE));
		}

		if(this._danger_source != null) {
			this._state = State.DANGER;
			this._mate_target = null;
		}else if(this._desire > MATE_MINIMUM) {
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

			super.advance(dt, MIN_DISTANCE,
					MAX_ENERGY, MIN_ENERGY, ENERGY_MULTIPLIER,
					DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE, SPEED_VARIANCE);
		}else {

			this._dest = this._pos.plus(this._pos.minus(_danger_source.get_position()).direction());

			super.advanceRunning(dt, RUNNING_MULTIPLIER, SPEED_VARIANCE,
					ENERGY_MULTIPLIER, RUNNING_TIREDNESS_MULTIPLIER, MIN_ENERGY,
					MAX_ENERGY, DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE);

			if(this._danger_source == null || this._danger_source.get_position().distanceTo(this._pos) > this.get_sight_range()) {

				//Busca un nuevo danger source
				this._danger_source = 
						this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
								(Animal other) -> other.get_diet() == Diet.CARNIVORE));
			}

			//Comprobar otra vez ya que puede haber escogido un nuevo danger source
			if(this._danger_source == null) {
				if(this._desire > MATE_MINIMUM) {
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

			super.advance(dt, MIN_DISTANCE,
					MAX_ENERGY, MIN_ENERGY, ENERGY_MULTIPLIER,
					DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE, SPEED_VARIANCE);
		} else {
			this._dest = this._mate_target.get_position();

			super.advanceRunning(dt, RUNNING_MULTIPLIER, SPEED_VARIANCE,
					ENERGY_MULTIPLIER, RUNNING_TIREDNESS_MULTIPLIER, MIN_ENERGY,
					MAX_ENERGY, DESIRE_MULTIPLIER, MIN_DESIRE, MAX_DESIRE);

			if (this._pos.distanceTo(this._mate_target.get_position()) < MIN_DISTANCE) {

				super.resetDesire();
				this._mate_target.resetDesire();

				if (this._baby == null && Utils._rand.nextDouble() < BABY_PROBABILITY) {

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

		} else if (this._desire < MATE_MINIMUM) {

			this._state = State.NORMAL;
		}
	}
}
