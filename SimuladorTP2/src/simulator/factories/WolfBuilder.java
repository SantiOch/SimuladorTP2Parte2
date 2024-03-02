package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal>{

	private final Factory<SelectionStrategy> selectionFactory;

	public WolfBuilder(Factory<SelectionStrategy> f) {
		super("wolf", "wolf builder");
		this.selectionFactory = f;
	}

	@Override
	protected Animal create_instance(JSONObject data) {

		SelectionStrategy mate_strategy = null;
		SelectionStrategy hunt_strategy = null;

		//La posición es opcional
		Vector2D pos = null;

		if(data.has("mate_strategy")) mate_strategy = this.selectionFactory.create_instance(data.getJSONObject("mate_strategy"));

		if(mate_strategy == null) mate_strategy = new SelectFirst();

		if(data.has("hunt_strategy")) hunt_strategy = this.selectionFactory.create_instance(data.getJSONObject("hunt_strategy"));

		if(hunt_strategy == null) hunt_strategy = new SelectFirst();

		if(data.has("pos")) {

			JSONObject jo = data.getJSONObject("pos");

			JSONArray x = jo.getJSONArray("x_range");
			JSONArray y = jo.getJSONArray("y_range");

			if(x.length() != 2 || y.length() != 2) {
				throw new IllegalArgumentException("Wrong number of parameters in x/y range");
			}

			double minX = x.getDouble(0);
			double maxX = x.getDouble(1);
			double minY = y.getDouble(0);
			double maxY= y.getDouble(1);

			double posX, posY;

			posX = Utils._rand.nextDouble(minX, maxX);
			posY = Utils._rand.nextDouble(minY, maxY);

			pos = new Vector2D(posX, posY);
		}

		return new Wolf(mate_strategy, hunt_strategy, pos);
	}


	@Override
	protected void fill_in_data(JSONObject data) {

		JSONArray x = new JSONArray();
		JSONArray y = new JSONArray();	

		x.put(100.0);
		x.put(200.0);

		y.put(100.0);
		y.put(200.0);

		JSONObject mateStrat = new JSONObject();
		JSONObject huntStrat = new JSONObject();

		mateStrat.put("type", "first");
		huntStrat.put("type",	"closest");

		JSONObject pos = new JSONObject();

		pos.put("x_range", x);
		pos.put("y_range", y);

		data.put("mate_strategy", mateStrat);
		data.put("hunt_strategy", huntStrat);
		data.put("pos", pos);

	}
}



