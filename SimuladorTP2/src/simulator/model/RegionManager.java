package simulator.model;

import java.util.List;
import java.util.Map;
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
		
		//TODO inicializar _animal_region 
		
		
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
		// TODO Auto-generated method stub
		return null;
	}
	
	void set_region(int row, int col, Region r) {
		//TODO cambiar rodoos los animales que estaban antes en esa región a la nueva
		this._region[row][col] = r;
	}
	
	void register_animal(Animal a) {
		//TODO 
	}
	
	void unregister_animal(Animal a) {
		//TODO
	}
	
	void update_animal_region(Animal a) {
		//TODO
	}

	public double get_food(Animal a, double dt) {
		//TODO
		return 0.0;
	}
	
	void update_all_regions(double dt) {
		for(Region[] ArrReg: this._region) {
			for(Region Reg: ArrReg) {
				Reg.update(dt);
			}
		}
	}

	
	public JSONObject as_JSON() {
		//TODO devolver json de todas las regiones
		
		return null;
	}
	
}
