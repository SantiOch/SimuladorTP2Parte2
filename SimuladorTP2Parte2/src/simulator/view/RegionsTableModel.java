package simulator.view;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.RegionInfo;

@SuppressWarnings("serial")
class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

	private List<String> _colNames;
	private List<RegionData> _regions;
	private int _rowCount;

	RegionsTableModel(Controller ctrl) {
		super();
		_colNames = new LinkedList<>();
		_regions = new LinkedList<>();
		ctrl.addObserver(this);
	}

	@Override
	public String getColumnName(int col) { return _colNames.get(col); }
	
	@Override
	public int getRowCount() { return _rowCount; }

	@Override
	public int getColumnCount() { return _colNames.size(); }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		return switch (columnIndex) {
		case 0 -> _regions.get(rowIndex).row();
		case 1 -> _regions.get(rowIndex).col();
		case 2 -> _regions.get(rowIndex).r();
		default -> _regions.get(rowIndex).r().getAnimalsInfo().stream().filter((a) -> a.get_diet() == Diet.valueOf(_colNames.get(columnIndex))).count();
		};
	}

	// Añade los titulos de las columnas y las regiones
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		
		_rowCount = map.get_rows() * map.get_cols();
		
		_colNames.add("Row");
		_colNames.add("Col");
		_colNames.add("Desc.");

		for(RegionData r: map) _regions.add(r);
		
		for(Diet d: Diet.values()) _colNames.add(d.toString());
	}

	// Cambia el numero de columnas y mete la información de las regiones
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_rowCount = map.get_rows() * map.get_cols();
		
		_regions = new LinkedList<>();
		
		for(RegionData r: map) {
			_regions.add(r);
		}

		fireTableDataChanged();
	}
	
	// No hace nada
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) { }

	// Cambia la región antigua con la nueva
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		
		int index = row * map.get_cols() + col;

		_regions.set(index, new RegionData(row, col,  r));
	}

	// Vuelve a pintar la tabla
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		fireTableDataChanged();
	}
}