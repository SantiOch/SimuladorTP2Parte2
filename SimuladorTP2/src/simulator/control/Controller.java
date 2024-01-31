package simulator.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.model.Simulator;

public class Controller {
	private Simulator _sim;
	
	public Controller(Simulator sim) {
		this._sim = sim;
	}
	
	public void load_data(JSONObject data) {
		//TODO
		//JSONObject joFromFile1 = new JSONObject(
		//		new JSONTokener(new FileInputStream(new File("resources/other/json-example-1.json"))));
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		//TODO
	}
}
