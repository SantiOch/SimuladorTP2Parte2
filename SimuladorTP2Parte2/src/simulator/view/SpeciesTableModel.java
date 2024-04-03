package simulator.view;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;

@SuppressWarnings("serial")
class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	
	private List<String> _colNames;
	private Set<String> _rowNames;
	private List<AnimalInfo> _animals;
	
	
	SpeciesTableModel(Controller ctrl) {
		_colNames = new ArrayList<>();
		_animals = new ArrayList<>();
		_rowNames = new HashSet<>();
		_colNames.add("Species");
				
		for(State s: State.values()) _colNames.add(s.toString());
		
		ctrl.addObserver(this);
	}
	

	@Override
	public String getColumnName(int col) {
		return _colNames.get(col);
	}
	
	@Override
	// método obligatorio, probad a quitarlo, no compila
	//
	// this is for the number of columns
	public int getColumnCount() {
		return _colNames.size();
	}
	
	@Override
	public int getRowCount() {
		return _rowNames.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		switch (columnIndex) {
			case 0:
				return _rowNames.toArray()[rowIndex];
		default:
				return getAnimals(State.valueOf(_colNames.get(columnIndex)), _rowNames.toArray()[rowIndex].toString());
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_animals = animals;
		for(AnimalInfo a: animals) {
			_rowNames.add(a.get_genetic_code());
		}
		fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		_rowNames = new HashSet<>();
		_animals = animals;
		for(AnimalInfo a: animals) {
			_rowNames.add(a.get_genetic_code());
		}
		fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		this._animals.add(a);
		_rowNames.add(a.get_genetic_code());
		fireTableDataChanged();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		this._animals = animals;
		fireTableDataChanged();
	}
	
	private int getAnimals(State s, String gcode) {
		int n = 0;

		for(AnimalInfo a: _animals) {
			if(a.get_state() == s && a.get_genetic_code().equals(gcode)) {
				n++;
			}
		}
		
		return n;
	}
}