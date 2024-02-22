package simulator.factories;

import org.json.JSONObject;

public class SelectFirstBuilder<T> extends Builder<T> {

	public SelectFirstBuilder() {
		super("first", "select first builder");
	}

	@Override
	protected T create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}

}
