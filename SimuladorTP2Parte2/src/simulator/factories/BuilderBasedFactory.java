package simulator.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private final Map<String, Builder<T>> _builders;
	private final List<JSONObject> _builders_info;

	public BuilderBasedFactory() {

		this._builders = new HashMap<>();
		this._builders_info = new LinkedList<>();
		//Create a HashMap for _builders, and a LinkedList _builders_info // ...
		
	}
	public BuilderBasedFactory(List<Builder<T>> builders) {

		this();
 
		for(Builder<T> b: builders) {
			this.add_builder(b);
		}
		// call add_builder(b) for each builder b in builder
	}
	public void add_builder(Builder<T> b) {

		this._builders.put(b.get_type_tag(), b);
		this._builders_info.add(b.get_info());

	}

	@Override
	public T create_instance(JSONObject info) {
		
		if (info == null) throw new IllegalArgumentException("’info’ cannot be null");

		Builder<T> b = this._builders.get(info.getString("type"));

		T instance = null;

		if(b != null) instance = b.create_instance(info.has("data") ? info.getJSONObject("data") : new JSONObject());

		if(b == null || instance == null) throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString()); 

		return instance;
	}
	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	} 
}