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
	// TODO definir atributos necesario
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
	public String getColumnName(int col) {
		return _colNames.get(col);
	}
	
	@Override
	public int getRowCount() {
		return _rowCount;
	}

	@Override
	public int getColumnCount() {
		return _colNames.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
				
		switch(columnIndex) {
		case 0:
			return _regions.get(rowIndex).row();
		case 1:
			return _regions.get(rowIndex).col();
		case 2:
			return _regions.get(rowIndex).r();
		default:
			return _regions.get(rowIndex).r().getAnimalsInfo().stream().filter(a->a.get_diet() == Diet.valueOf(_colNames.get(columnIndex))).count();
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this._rowCount = map.get_rows() * map.get_cols();
		_colNames.add("Row");
		_colNames.add("Col");
		_colNames.add("Desc.");

		for(RegionData r: map) this._regions.add(r);
		
		for(Diet d: Diet.values()) _colNames.add(d.toString());
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this._rowCount = map.get_rows() * map.get_cols();
		
		this._regions = new LinkedList<>();
		
		for(RegionData r: map) {
			this._regions.add(r);
		}

		fireTableDataChanged();

	}
	

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		
		int index = row * map.get_cols() + col;
				
		this._regions.remove(index);
		
		this._regions.add(index, new RegionData(row, col,  r));

	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		fireTableDataChanged();
	}
}