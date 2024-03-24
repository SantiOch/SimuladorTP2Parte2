package simulator.model;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public class RegionManager implements AnimalMapView{

	private final int _cols;
	private final int _rows;
	private final int _width;
	private final int _height;
	
	private final int _region_width;
	private final int _region_height;
	
	private final Region[][] _region;
	
	private final Map<Animal, Region> _animal_region;
	
	public RegionManager(int cols, int rows, int width, int height) {
		
		this._cols = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		
		this._region_width = (width/cols);
		this._region_height = (height/rows);
		
	  	if ( _width % _cols != 0 || _height % _rows != 0) throw new IllegalArgumentException("Width/height is not divisible by cols/rows!");

		this._region = new Region[rows][cols];

		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				this._region[i][j] = new DefaultRegion();
			}
		}

		this._animal_region = new HashMap<>();
	}
	
	@Override
	public int get_cols() {
		return this._cols;
	}

	@Override
	public int get_rows() {
		return this._rows;
	}

	@Override
	public int get_width() {
		return this._width;
	}

	@Override
	public int get_height() {
		return this._height;
	}

	@Override
	public int get_region_width() {
		return this._region_width;
	}

	@Override
	public int get_region_height() {
		return this._region_height;
	}

	//Devuelve una lista con los animales dentro del campo de visión y que además cumplen con la condición
	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		
		//Nueva lista para almacenar los animales que ve y que cumplen la condición
		List<Animal> listAux = new LinkedList<>();
		
		//Legibilidad
		double range = e.get_sight_range();
		double posX = e.get_position().getX();
		double posY = e.get_position().getY();
			
		//Límites para las regiones que puede ver o no ver
		int regMinWidthIndex = (int) ((posX - range) / this._region_width);
		int regMaxWidthIndex = (int) ((posX + range) / this._region_width);
		int regMinHeightIndex = (int) ((posY - range) / this._region_height);
		int regMaxHeightIndex = (int) ((posY + range) / this._region_height);
		
		//Ver que no se salgan del mapa, si se salen, se establecen en los límites
		regMinWidthIndex = Math.max(regMinWidthIndex, 0);
		regMaxWidthIndex = Math.min(regMaxWidthIndex, this._cols);
		regMinHeightIndex = Math.max(regMinHeightIndex, 0);
		regMaxHeightIndex = Math.min(regMaxHeightIndex, this._rows);
		
		//Recorre todas las regiones dentro del campo visual y las añade a la lista auxiliar
		for(int i = regMinHeightIndex; i < regMaxHeightIndex; i++) {
			for(int j = regMinWidthIndex; j < regMaxWidthIndex; j++) {
				for(Animal a: this._region[i][j].getAnimals()) {
					if(a != e && a.get_position().distanceTo(e.get_position()) < range && filter.test(a)) {
						listAux.add(a);
					}
				}
			}
		}
		
		//Coger solo las regiones que están y hacer 2 forEach
		//Cada forEach comprobar Animal a: a.get_position().distanceTo(e.get_position) < r && filter.test(a)
		//listAux.add(a);
		
		/* Calcular que regiones toca o ve el animal con su campo de visión, pintar ejes centrados en el animal (Cruz),
		 * con esos miro en que regiones toco, ver en cada una la lista de animales, ya que son candidatos a que me interesen.
		 * Con cuáles me quedo, con los que estén lo suficientemente cerca para verlos y que además cumplan el predicate.
		 * O sea que la distancia al animal es menor que r (campo de visión) y que cumplan el test */

		return listAux;
	}
	
	//Cambia la región que está en la posición [row][col] por la región r, y cambia todos los animales que estaban en esa región a la nueva
	public void set_region(int row, int col, Region r) {
		
		//Cambia todos los animales que estaban antes en esa región a la nueva
		Region old = this._region[row][col];
		
		for(Animal a: r.animalList) {
			this._animal_region.remove(a);
			this._animal_region.put(a, r);
		}
		
		r.animalList = old.animalList;
		old.animalList = null;
		
		this._region[row][col] = r;
	}
	
	public void register_animal(Animal a) {
		
		//Encuentra la región a la que tiene que pertenecer el animal (a partir de su posición) y 
		//lo añade a esa región y actualiza _animal_region.
		a.init(this);
				
		Region r = regByPos(a.get_position());
		

		r.add_animal(a);
		
		this._animal_region.put(a, r);
	}
	
	public void unregister_animal(Animal a) {
		
		this._animal_region.get(a).remove_animal(a);
		this._animal_region.remove(a);	
	}
	
	public void update_animal_region(Animal a) {
		
		//Encuentra la región a la que tiene que pertenecer el animal (a partir de su posición actual),
		//y si es distinta de su región actual lo añade a la nueva región, 
		//lo quita de la anterior, y actualiza _animal_region.
		
		Region act = this._animal_region.get(a);
		Region newReg = regByPos(a.get_position());
		
		if(act != newReg) {
			
			act.remove_animal(a);
			newReg.add_animal(a);
			
			this._animal_region.remove(a);
			this._animal_region.put(a, newReg);
		}
	}

	//Da comida al animal que la pide
	@Override
	public double get_food(Animal a, double dt) {		
		return this._animal_region.get(a).get_food(a, dt);
	}
	
	//Update de todas las regiones
	public void update_all_regions(double dt) {
		for(Region[] ArrReg: this._region) {
			for(Region Reg: ArrReg) {
				Reg.update(dt);
			}
		}
	}
	
	//Devuelve la region a la que debería pertenecer un animal por su posición
	private Region regByPos(Vector2D vect) {
		
		int regionI = (int) (vect.getY() / this._region_height);
		int regionJ = (int) (vect.getX() / this._region_width);

		return this._region[regionI][regionJ];
	}

	@Override
	//Devuelve el JSON de todas las regiones
	public JSONObject as_JSON() {
		
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		
		for(int i = 0; i < this._rows; i++) {
			for(int j = 0; j < this._cols; j++) {
				
				JSONObject regionJSON = new JSONObject();
				
				regionJSON.put("row", i );
				regionJSON.put("col", j );
				
				regionJSON.put("data", this._region[i][j].as_JSON());
				
				ja.put(regionJSON);
			}
		}
		
		jo.put("regions", ja);
		
		return jo;
	}

	@Override
	public Iterator<RegionData> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
