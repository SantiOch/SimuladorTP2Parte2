package simulator.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Region implements Entity, FoodSupplier, RegionInfo{

	protected List<Animal> animalList;
	
	public Region() {
		this.animalList = new ArrayList<Animal>();
	}
	
	final void add_animal(Animal a) {
		this.animalList.add(a);
	}
	final void remove_animal(Animal a) {
		this.animalList.remove(a);
	}
	
	final List<Animal> getAnimals(){
		return Collections.unmodifiableList(this.animalList);
	}
	
	public JSONObject as_JSON() {
		//TODO 
		
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		
		for(Animal a: animalList) {
			ja.put(a.as_JSON());
		}
		
		jo.put("animals", ja);
	
		
		return jo;
	}
	
	@Override
	public double get_food(Animal a, double dt) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

}
