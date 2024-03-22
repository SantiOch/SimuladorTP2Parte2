package simulator.model;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

import java.util.Objects;

public abstract class Animal implements Entity, AnimalInfo {

	private final static double SPEED_VARIANCE = 0.2;
	private final static double INIT_SPEED_VARIANCE = 0.1;
	private final static double INIT_ENERGY = 100.0;
	private final static double RANDOM_POS_NEWBORN = 60.0;
	private final static double SIGHT_RANGE_VARIANCE = 0.2;


	private final String _genetic_code;

	protected State _state;
	private final Diet _diet;

	protected Vector2D _dest;
	protected Vector2D _pos;

	protected double _energy;
	protected double _age;
	protected double _desire;
	private final double _speed;
	private final double _sight_range;

	protected Animal _mate_target;
	protected Animal _baby;

	private AnimalMapView _region_mngr;


	protected SelectionStrategy _mate_strategy;

	protected Animal(String genetic_code, Diet diet, double sight_range,
					 double init_speed, SelectionStrategy mate_strategy, Vector2D pos) {

		if ((!Objects.equals(genetic_code, "Sheep") && !Objects.equals(genetic_code, "Wolf")) || diet == null || mate_strategy == null) {
			throw new IllegalArgumentException("Invalid genetic_code/diet/mate_strategy");
		}

		this._genetic_code = genetic_code;
		this._diet = diet;
		this._sight_range = sight_range;
		this._mate_strategy = mate_strategy;

		if (pos != null) this._pos = pos;

		this._speed = Utils.get_randomized_parameter(init_speed, INIT_SPEED_VARIANCE);

		this._state = State.NORMAL;
		this._energy = INIT_ENERGY;
		this._desire = 0.0;

		this._region_mngr = null;
		this._mate_target = null;
		this._dest = null;
		this._baby = null;
	}

	protected Animal(Animal p1, Animal p2) {

		if (p1 == null || p2 == null) {
			throw new IllegalArgumentException("Invalid animal");
		}

		this._region_mngr = null;
		this._mate_target = null;
		this._dest = null;
		this._baby = null;

		this._state = State.NORMAL;
		this._desire = 0.0;

		this._mate_strategy = p2._mate_strategy;

		this._genetic_code = p1._genetic_code;
		this._diet = p1._diet;
		this._energy = (p1._energy + p2._energy) / 2;

		this._pos = p1.get_position().plus(Vector2D.get_random_vector(-1, 1).scale(RANDOM_POS_NEWBORN * (Utils._rand.nextGaussian() + 1)));
		this._sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2, SIGHT_RANGE_VARIANCE);
		this._speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, SPEED_VARIANCE);

	}

	public AnimalMapView get_region_mngr() {
		return _region_mngr;
	}

	@Override
	public State get_state() {
		return this._state;
	}

	@Override
	public Vector2D get_position() {
		return this._pos;
	}

	@Override
	public String get_genetic_code() {
		return this._genetic_code;
	}

	@Override
	public Diet get_diet() {
		return this._diet;
	}

	@Override
	public double get_speed() {
		return this._speed;
	}

	@Override
	public double get_sight_range() {
		return this._sight_range;
	}

	@Override
	public double get_energy() {
		return this._energy;
	}

	@Override
	public double get_age() {
		return this._age;
	}

	@Override
	public Vector2D get_destination() {
		return this._dest;
	}

	@Override
	public boolean is_pregnant() {
		return this._baby != null;
	}

	public void resetDesire() {
		this._desire = 0.0;
	}

	public void init(AnimalMapView reg_mngr) {

		this._region_mngr = reg_mngr;

		//Elegir posicion aleatoria si la posicion es null y si no ajustarla para que esté dentro
		if (this._pos == null) {

			this._pos = randomPos();
		} else this._pos = adjustPosition(this._pos.getX(), this._pos.getY());

		//Elegir destino aleatorio dentro del mapa
		this._dest = randomPos();
	}

	//Función para ajustar la posición si está fuera del mapa
	protected Vector2D adjustPosition(double y, double x) {

		int width = this._region_mngr.get_width();
		int height = this._region_mngr.get_height();

		while (x >= width) x = (x - width);
		while (x < 0) x = (x + width);
		while (y >= height) y = (y - height);
		while (y < 0) y = (y + height);

		return new Vector2D(x, y);
	}

	public Animal deliver_baby() {

		Animal baby = this._baby;

		this._baby = null;

		return baby;

	}

	protected Vector2D randomPos() {

		double x = Utils._rand.nextDouble(this._region_mngr.get_width());
		double y = Utils._rand.nextDouble(this._region_mngr.get_height());

		return 	new Vector2D(x, y);

	}

	protected void move(double speed) {

		this._pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}

	@Override
	public JSONObject as_JSON() {

		JSONObject jo = new JSONObject();

		jo.put("pos", this._pos.toString());
		jo.put("gcode", this._genetic_code);
		jo.put("diet", this._diet.toString());
		jo.put("state", this._state.toString());

		return jo;
	}

	public void resetTarget() {
		this._mate_target = null;
	}

	public boolean isOut() {
		return this._pos.getX() >= this._region_mngr.get_width()
				|| this._pos.getX() < 0 || this._pos.getY() < 0
				|| this._pos.getY() >= this._region_mngr.get_height();
	}
	protected void updateMateTarget() {
		if ((this._mate_target != null
				&& this._mate_target.get_state() == State.DEAD)
				|| (this._mate_target != null && this._mate_target.get_position().distanceTo(this._pos) > this.get_sight_range())) {
			this._mate_target = null;
		}

		if (this._mate_target == null) {
			//Busca a otro animal para emparejarse con la estrategia que tenga
			this._mate_target = this._mate_strategy.select(this, get_region_mngr().get_animals_in_range(this,
					(Animal other) -> Objects.equals(other.get_genetic_code(), this.get_genetic_code())));
		}
	}

	protected void advance(double dt, double _min_distance, double _max_energy,
						   double _min_energy, double _energy_multiplier,
						   double _desire_multiplier, double _min_desire, double _max_desire, double _speed_change) {

		if(get_position().distanceTo(get_destination()) < _min_distance) {
			this._dest = randomPos();
		}

		this.move(get_speed() * dt * Math.exp((get_energy() - _max_energy) * _speed_change));

		this.adjustEnergy(dt, _energy_multiplier, 1,
				_min_energy,  _max_energy,  _desire_multiplier,
				_min_desire,  _max_desire);
	}

	private void adjustEnergy(double dt, double _energy_multiplier, double _running_tiredness_multiplier,
							  double _min_energy, double _max_energy, double _desire_multiplier,
							  double _min_desire, double _max_desire){
		this._age += dt;

		this._energy -= _energy_multiplier * _running_tiredness_multiplier * dt;

		if(this._energy < _min_energy) this._energy = _min_energy;
		if(this._energy > _max_energy) this._energy = _max_energy;

		this._desire += _desire_multiplier * dt;

		if(this._desire < _min_desire) this._desire = _min_desire;
		if(this._desire > _max_desire) this._desire = _max_desire;
	}

	protected void advanceRunning(double dt, double _running_multiplier, double _speed_change, double _energy_multiplier, double _running_tiredness_multiplier,
	double _min_energy, double _max_energy, double _desire_multiplier, double _min_desire, double _max_desire) {

		this.move(_running_multiplier * get_speed() * dt * Math.exp((get_energy() - _max_energy) * _speed_change));

		this.adjustEnergy(dt, _energy_multiplier, _running_tiredness_multiplier,
				_min_energy,  _max_energy,  _desire_multiplier,
				_min_desire,  _max_desire);
	}

}