package simulator.control;

import java.io.OutputStream;

import org.json.JSONObject;

import simulator.model.Simulator;

public class Controller {
	private Simulator _sim;
	
	public Controller(Simulator sim) {
		this._sim = sim;
	}
	
	public void load_data(JSONObject data) {
		//TODO
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		//TODO
	}
}
