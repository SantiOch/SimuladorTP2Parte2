package simulator.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Collections;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{

	protected List<Animal> animalList;
	
	public Region() {
		this.animalList = new LinkedList<Animal>();
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
	
	@Override
	public JSONObject as_JSON() {
		
		//Creo que está bien así, preguntar a Pablo , está bien (He preguntado)
		
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		
		for(Animal a: animalList) {
			ja.put(a.as_JSON());
		}
		
		jo.put("animals", ja);
	
		return jo;
	}
	
	protected int getHervivores() {
		
		int n = 0;

		for(Animal a: this.animalList) {
			if(a.get_diet() == Diet.HERBIVORE) {
				n++;
			}
		}
		
		return n;
	}
}


