package simulator.model;

import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

import java.util.LinkedList;
import java.util.Collections;

public class Simulator implements JSONable{
	
	private Factory<Animal> _animals_factory;
	private Factory<Region> _regions_factory;
	
	private RegionManager _region_manager;
	private List<Animal> _animal_list;
	private double _time;
	
	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;		
		
		this._region_manager = new RegionManager(cols, rows, width, height);
		this._animal_list = new LinkedList<>();
		this._time = 0.0;
	}
	
	private void set_region(int row, int col, Region r) {
		this._region_manager.set_region(row, col, r);
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
		
			a.update(dt);
			this._region_manager.update_animal_region(a);
			
			if(a._state == State.DEAD) {
				deadAnimals.add(a);
				this._region_manager.unregister_animal(a);
			}
			
			if(a.is_pregnant()) {
				newBornAnimals.add(a.deliver_baby());
			}
		}
		
		for(Animal a: newBornAnimals) {
			this.add_animal(a);
		}
		
//		this._animal_list.addAll(newBornAnimals);
	
		
		this._region_manager.update_all_regions(dt);
		this._animal_list.removeAll(deadAnimals);
		
	}
	
	@Override
	public JSONObject as_JSON() {
		JSONObject jo = new JSONObject();
		
		jo.put("time", this._time);
		jo.put("state", this._region_manager.as_JSON());
		
		return jo;
	}
}
