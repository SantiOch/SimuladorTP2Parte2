package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.*;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;


public class Controller{
	private final Simulator _sim;

	public Controller(Simulator sim) {
		this._sim = sim;
	}

	public void load_data(JSONObject data) {
		//Load region data
		if(data.has("regions")) {
			this.set_regions(data);
		}
		if(data.has("animals")) {

			JSONArray animales = data.getJSONArray("animals");

			for(int i = 0; i < animales.length(); i++) {

				JSONObject jo = animales.getJSONObject(i);
				int N = jo.getInt("amount");
				JSONObject O = jo.getJSONObject("spec");
				for(int j = 0; j < N; j++) {
					this._sim.add_animal(O);
				}
			}
		}
		else {
			throw new IllegalArgumentException("Missing animals in JSON");
		}
	}


	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {

		List<ObjInfo> ol = new ArrayList<>(animals.size());

		for (AnimalInfo a : animals) {

			ol.add(new ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(), (int) a.get_position().getY(),(int) Math.round(a.get_age())+2));
		}

		return ol; 
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		 
		if(t < 0 || dt < 0) throw new IllegalArgumentException("Time/delta-time cannot be a negative number");
		
		PrintStream p = new PrintStream(out);
		
		SimpleObjectViewer view = null;
		
		JSONObject info = new JSONObject(); 
		
		info.put("in", this._sim.as_JSON());
		
		if (sv) {
			
			MapInfo m = _sim.get_map_info();
			
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		while(this._sim.get_time() <= t) {
			
			this._sim.advance(dt);
			
			if (sv) view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}
		
//		if(sv) view.close();
		
		info.put("out", this._sim.as_JSON());
		
		p.println(info);
	}
	
	public void reset(int cols, int rows, int width, int height) {
		this._sim.reset(cols, rows, width, height);
	}
	
	public void set_regions(JSONObject rs) {
		JSONArray regiones = rs.getJSONArray("regions");

		for(int i = 0; i < regiones.length(); i++) {

			int minRow, maxRow, minCol, maxCol;
			
			JSONArray rows;
			JSONArray cols;

			JSONObject jo = regiones.getJSONObject(i);

			JSONObject O = jo.getJSONObject("spec");

			rows = jo.getJSONArray("row");
			minRow = rows.getInt(0);
			maxRow = rows.getInt(1);

			cols = jo.getJSONArray("col");
			minCol = cols.getInt(0);
			maxCol = cols.getInt(1);

			for(int R = minRow; R < maxRow; R++ ) {
				for(int C = minCol; C < maxCol; C++) {
					this._sim.set_region(R, C, O);
				}
			}
		}
	}
	
	public void advance(double dt) {
		this._sim.advance(dt);
	}
	
	public void addObserver(EcoSysObserver o) {
		this._sim.addObserver(o);
	}
	
	public void removeObserver(EcoSysObserver o) {
		this._sim.removeObserver(o);
	}
	
	
}
