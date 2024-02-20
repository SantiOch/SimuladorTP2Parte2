package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{

	private static final String gcode = "Sheep";
	private static final double sight_range = 40.0;
	private static final double init_speed = 35.0;


	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;


	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy,
			Vector2D pos) {
		super(gcode, Diet.HERBIVORE, sight_range, init_speed, mate_strategy, pos);
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

		if(this._energy == 0.0 || this._age == 8.0) {
			this._state = State.DEAD;
		}

		if(this._state != State.DEAD) {
			this._energy += this.get_region_mngr().get_food(this, dt);
			if(this._energy < 0.0) this._energy = 0.0;
			if(this._energy > 100.0) this._energy = 100.0;		
		}

	}

	private void updateNormal(double dt) {

		//Avanzar
		this.advance(dt);

		//Cambio de estado
		if(_danger_source == null) {

			this._danger_source = 
					this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
							(Animal other) -> other.get_genetic_code() != this.get_genetic_code()));
		}

		if(this._danger_source != null) {
			this._state = State.DANGER;
			this._mate_target = null;
		}else if(this._desire > 65.0) {
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

			if(this._danger_source == null || !(super.get_region_mngr().get_animals_in_range(this,
					(Animal other) -> other == this._danger_source).contains(this._danger_source))) {

				//Busca un nuevo danger source
				this._danger_source = 
						this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
								(Animal other) -> other.get_genetic_code() != this.get_genetic_code()));
			}

			//Comprobar otra vez ya que puede haver escogido un nuevo danger source
			if(this._danger_source == null) {
				if(this._desire > 65.0) {
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
		if((this._mate_target != null 
				&& this._mate_target.get_state() == State.DEAD)
				|| !(super.get_region_mngr().get_animals_in_range(this,
						(Animal other) ->other == this._mate_target).contains(this._mate_target))){

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

			if(this._pos.distanceTo(this._mate_target.get_position()) < 8.0) {

				super.resetDesire();
				this._mate_target.resetDesire();

				if(this._baby == null && Utils._rand.nextDouble() < 0.9) {

					//Crea un nuevo bebé con probabilidad de 0.9
					this._baby = new Sheep(this, this._mate_target);

				}

				this._mate_target = null;
			}
		}

		if(this._danger_source == null) {

			this._danger_source = 
					this._danger_strategy.select(this, super.get_region_mngr().get_animals_in_range(this,
							(Animal other) -> other.get_genetic_code() != this.get_genetic_code()));		
		}

		if(this._danger_source != null) {

			this._state = State.DANGER;
			this._mate_target = null;

		}else if (this._desire < 65.0) {

			this._state = State.NORMAL;
			this._danger_source = null;
		}
	}

	void advanceRunning(double dt) {
		this.move(2 * super.get_speed() * dt * Math.exp((super.get_energy() - 100.0) * 0.007));
		this._age += dt;

		this._energy -= 20.0 * 1.2 * dt;

		if(this._energy < 0.0) this._energy = 0.0;
		if(this._energy > 100.0) this._energy = 100.0;

		this._desire += 40.0 * dt;

		if(this._desire < 0.0) this._desire = 0.0;
		if(this._desire > 100.0) this._desire = 100.0;
	}

	void advance(double dt) {

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

	}
}
