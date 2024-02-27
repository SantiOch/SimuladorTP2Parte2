package simulator.factories;

import org.json.JSONObject;
import simulator.model.*;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() {
		super("default", "default region builder");
	}

	@Override
	protected Region create_instance(JSONObject data) {
		return new DefaultRegion();
	}
}
