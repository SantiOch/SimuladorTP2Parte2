package simulator.model;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;

import simulator.factories.Factory;

import java.util.LinkedList;
import java.util.Collections;

public class Simulator implements JSONable, Observable<EcoSysObserver>{
	
	private final Factory<Animal> _animals_factory;
	private final Factory<Region> _regions_factory;
	
	private List<EcoSysObserver> _observers;
	private RegionManager _region_manager;
	private List<Animal> _animal_list;
	private double _time;
	
	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		
		if(animals_factory == null || regions_factory == null) throw new IllegalArgumentException("Factories cannot be null!");
		
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;		
		
		this._observers = new LinkedList<>();
		this._region_manager = new RegionManager(cols, rows, width, height);
		this._animal_list = new LinkedList<>();
		this._time = 0.0;
	}
	
	public void reset(int cols, int rows, int width, int height) {
		this._region_manager = new RegionManager(cols, rows, width, height);
		this._animal_list = new LinkedList<>();
		this._time = 0.0;
		for(EcoSysObserver o: this._observers) {
			o.onReset(_time, _region_manager, new ArrayList<>(this._animal_list));
		}
	}
	
	private void set_region(int row, int col, Region r) {
		this._region_manager.set_region(row, col, r);
		for(EcoSysObserver o: this._observers) {
			o.onRegionSet(row, col, _region_manager, r);
		}
	}
	
	public void set_region(int row, int col, JSONObject r_json) {
		
		//Crear region y llamar a add_region
		Region r = this._regions_factory.create_instance(r_json);
	
		this.set_region(row, col, r);
	}
	
	//Añade el animal a y lo registra en el region manager
	private void add_animal(Animal a) {
		this._animal_list.add(a);
		this._region_manager.register_animal(a);
		for(EcoSysObserver o: this._observers) {
			o.onAnimalAdded(_time, _region_manager, new ArrayList<>(this._animal_list), a);
		}
	}
	
	public void add_animal(JSONObject a_json) {
		
		Animal a = this._animals_factory.create_instance(a_json);

		this.add_animal(a);
	}
	
	public MapInfo get_map_info() {
		return this._region_manager;
	}
	
	public List<? extends AnimalInfo> get_animals(){
		return Collections.unmodifiableList(this._animal_list);
	}
	
	public double get_time() {
		return this._time;
	}
	
	public void advance(double dt) {
		
		this._time += dt;
		
		List<Animal> deadAnimals = new LinkedList<>();
		List<Animal> newBornAnimals = new LinkedList<>();
		
		for(Animal a: this._animal_list) {
			if(a.get_state() == State.DEAD) {
				deadAnimals.add(a);
				this._region_manager.unregister_animal(a);
			}
		}
		
		this._animal_list.removeAll(deadAnimals);

		for(Animal a: this._animal_list) {
			a.update(dt);
			this._region_manager.update_animal_region(a);
		}	
		
		this._region_manager.update_all_regions(dt);
		
		for(Animal a: this._animal_list) {
			if(a.is_pregnant()) {
				Animal baby = a.deliver_baby();
				newBornAnimals.add(baby);
				this._region_manager.register_animal(baby);
			}
		}
		
		this._animal_list.addAll(newBornAnimals);
		
		this.notify_on_advanced(dt);
	}
	
	 private void notify_on_advanced(double dt) {
     List<AnimalInfo> animals = new ArrayList<>(this._animal_list);
     // para cada observador o, invocar o.onAvanced(_time, _region_mngr, animals, dt)
     for(EcoSysObserver o: this._observers) {
    	 o.onAvanced(_time, _region_manager, animals, dt);
     }
	 }
	
	@Override
	public JSONObject as_JSON() {
		JSONObject jo = new JSONObject();
		
		jo.put("time", this._time);
		jo.put("state", this._region_manager.as_JSON());
		
		return jo;
	}

	@Override
	public void addObserver(EcoSysObserver o) {
		this._observers.add(o);
		o.onRegister(_time, _region_manager, new ArrayList<>(this._animal_list));
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		this._observers.remove(o);		
	}
}
