package simulator.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Predicate;

import org.json.JSONObject;

public class RegionManager implements AnimalMapView{

	private int _cols;
	private int	_rows;
	private int	_width;
	private int	_height;
	
	private int	_region_width;
	private int _region_height;
	
	private Region[][] _region;
	
	private Map<Animal, Region> _animal_region;
	
	public RegionManager(int cols, int rows, int width, int height) {
		
		this._cols = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		
		this._region_width = (width/cols);
		this._region_height = (height/rows);
		
		this._region = new Region[rows][cols];

		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				this._region[i][j] = new DefaultRegion();
			}
		}
		
		//TODO inicializar _animal_region , creo que debería ser HashMap
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

	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		
		List<Animal> listAux = new ArrayList<>();
		
		/* Calcular que regiones toca o ve el animal con su campo de visión, pintar ejes centrados en el animal (Cruz),
		 * con esos miro en que regiones toco, ver en cada una la lista de animales, ya que son candidatos a que me interesen
		 * con cuales me quedo, con los que estén lo suficientemente cerca para verlos y que además cumplan el predicate,
		 * y que la distancia a el animal es menor que r ( campo de visión), y que además cumpla el test
		 * TODO Predicate con función lambda codigo genético del animal = sheep o wolf */

		return listAux;
	}
	
	void set_region(int row, int col, Region r) {
		//TODO cambiar todos los animales que estaban antes en esa región a la nueva
		
		this._region[row][col] = r;
	}
	
	void register_animal(Animal a) {
		
		//TODO encuentra la región a la que tiene que pertenecer el animal (a partir de su posición) y 
		//lo añade a esa región y actualiza _animal_region.
		
		Region r = regByPos(a);
		
		a.init(this);

		r.add_animal(a);
	}
	
	void unregister_animal(Animal a) {
		
		//TODO Creo que está bien así
		
		this._animal_region.get(a).remove_animal(a);
		this._animal_region.remove(a);	
	}
	
	void update_animal_region(Animal a) {
		
		//TODO Encuentra la región a la que tiene que pertenecer el animal (a partir de su posición actual),
		//y si es distinta de su región actual lo añade a la nueva región, 
		//lo quita de la anterior, y actualiza _animal_region.
		
		Region act = this._animal_region.get(a);
	
		Region newReg = regByPos(a);
		
		if(act != newReg) {
			
			act.remove_animal(a);
			newReg.add_animal(a);
			
			this._animal_region.remove(a);
			this._animal_region.put(a, newReg);
		}
	}

	@Override
	public double get_food(Animal a, double dt) {
		
		//TODO Preguntar a Pablo
		
		return this._animal_region.get(a).get_food(a, dt);
	}
	
	void update_all_regions(double dt) {
		for(Region[] ArrReg: this._region) {
			for(Region Reg: ArrReg) {
				Reg.update(dt);
			}
		}
	}
	
	private Region regByPos(Animal a) {
		
		int regionI = (int) Math.floor((a.get_position().getX() / this._region_width)) ;
		int regionJ = (int) Math.floor((a.get_position().getY() / this._region_height)) ;
		
		return this._region[regionI][regionJ];
	}

	@Override
	public JSONObject as_JSON() {
		//TODO devolver json de todas las regiones
		
		return null;
	}
	
}
