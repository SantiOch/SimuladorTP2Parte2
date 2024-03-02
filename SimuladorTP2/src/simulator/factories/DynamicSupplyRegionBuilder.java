package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	private double food = 1000.0;
	private double factor = 2.0;
	
	public DynamicSupplyRegionBuilder() {
		super("dynamic", "dynamic supply region builder");
	}

	@Override
	protected Region create_instance(JSONObject data) {
		
		if(data.has("factor")) factor = data.getDouble("factor");

		if(data.has("food")) food = data.getDouble("food");
		
		return new DynamicSupplyRegion(food, factor);
	}
	
	@Override
	protected void fill_in_data(JSONObject data) {
		
		data.put("factor", factor);
		data.put("food",".....");
	}
}
