package simulator.model;

import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

import java.util.LinkedList;
import java.util.Collections;

public class Simulator implements JSONable{
	
	private Factory<Animal> _animals_factory;
	private Factory<Region> _regions_factory;
	
//	private int _cols;
//	private int _rows;
//	private int _width;
//	private int _height;
	
	private RegionManager _region_manager;
	private List<Animal> _animal_list;
	private double _time;
	
	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory, Factory<Region> regions_factory) {
//		this._cols = cols;
//		this._rows = rows;
//		this._width = width;
//		this._height = height;
		
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;		
		
		this._region_manager = new RegionManager(cols, rows, width, height);
		this._animal_list = new LinkedList<>();
		this._time = 0.0;
	}
	
	//TODO Cambiar a private luego
	public void set_region(int row, int col, Region r) {
		//TODO
	}
	
	public void set_region(int row, int col, JSONObject r_json) {
		//TODO crear region y llamar a add_region

	}
	
	//TODO Cambiar a private luego
	public void add_animal(Animal a) {
		//TODO
	}
	
	public void add_animal(JSONObject a_json) {
		//TODO crear animal y llamar a add_animal
		Animal a;
		if(a_json.get("type") == "wolf") {
			//No se que estrategia coger
		}
		
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
		//TODO llamar al update de todo, incrementar tiempo etc. 
	}
	
	public JSONObject as_JSON() {
		return null;
	}
}
