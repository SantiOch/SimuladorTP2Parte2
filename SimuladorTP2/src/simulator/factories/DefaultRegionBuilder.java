package simulator.factories;

import org.json.JSONObject;

public class DefaultRegionBuilder<T> extends Builder<T> {

	public DefaultRegionBuilder() {
		super("default", "default region builder");
	}

	@Override
	protected T create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}

}
