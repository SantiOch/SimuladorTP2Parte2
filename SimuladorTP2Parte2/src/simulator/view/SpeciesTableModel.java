package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
	
	private List<AnimalInfo> _animals;
	private List<String> _colNames;
	private Set<String> _rowNames;

	SpeciesTableModel(Controller ctrl) {
		
		_colNames = new ArrayList<>();
		_animals = new ArrayList<>();
		_rowNames = new HashSet<>();

		_colNames.add("Species");
				
		for(State s: State.values()) _colNames.add(s.toString());
		
		ctrl.addObserver(this);
	}

	@Override
	public String getColumnName(int col) { return _colNames.get(col); }
	
	@Override
	public int getColumnCount() { return _colNames.size(); }
	
	@Override
	public int getRowCount() { return _rowNames.size(); }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		return switch (columnIndex) {
		case 0 -> _rowNames.toArray()[rowIndex];
		default -> getAnimals(State.valueOf(_colNames.get(columnIndex)), _rowNames.toArray()[rowIndex].toString());
		};
	}

	// Crea el set de los nombres y añade los animales
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {

		_animals = animals;
		
		for(AnimalInfo a: animals) {
			_rowNames.add(a.get_genetic_code());
		}
		
		fireTableDataChanged();
	}

	// Crea el set de los nombres y añade los animales
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		
		_rowNames.clear();
		_animals = animals;
		
		fireTableDataChanged();
	}

	// Añade el animal a la lista de animales y mete el código genético en las filas (si no estaba antes)
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		
		_animals.add(a);
		_rowNames.add(a.get_genetic_code());
		
		fireTableDataChanged();
	}

	// No hace nada
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) { }
	
	// Coge la lista de animales nueva y repinta la tabla
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		
		_animals = animals;
		_rowNames.clear();
		
		for(AnimalInfo a: animals) {
			_rowNames.add(a.get_genetic_code());
		}
		
		fireTableDataChanged();
	}
	
	// Devuelve los animales con el estado y código genético en la lista de animales
	private long getAnimals(State s, String gcode) {
		return _animals.stream().filter(a-> a.get_state() == s && a.get_genetic_code().equals(gcode)).count();
	}
}