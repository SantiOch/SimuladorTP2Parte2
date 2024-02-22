package simulator.factories;

import org.json.JSONObject;

public class SelectFirstBuilder<T> extends Builder<T> {

	public SelectFirstBuilder() {
		super("first", "");
	}

	@Override
	protected T create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}

}
