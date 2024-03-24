package simulator.model;

import java.util.List;

import simulator.model.JSONable;

public interface RegionInfo extends JSONable {
	public List<AnimalInfo> getAnimalsInfo();
}

