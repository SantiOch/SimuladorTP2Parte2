package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

@SuppressWarnings("serial")
class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	private final String[] _headers = { "Key", "Value", "Description" };

	int _rows;
	int _cols;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame)null, true);
		_ctrl = ctrl;
		initGUI();
		ctrl.addObserver(this);
	}
	
	private void initGUI() {
		
		// Creamos el panel principal y ponemos el layout en el eje y para que los componentes 
		// se pongan de arriba a abajo
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); 
		setContentPane(mainPanel);
		
		// Panel para el texto superior
		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new BorderLayout());
		
		// Texto superior de ayuda
		JLabel help = new JLabel();
		help.setText("<html><p>Select a region type, the rows/cols interval,"
				+ " and provide values for the parametes in the <b>Value column</b> "
				+ "(default values are used for parametes with no value).</p></html>");
		help.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// Algo de márgen
		help.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Añadimos el texto al panel de ayuda y el panel de ayuda al panel principal
		helpPanel.add(help);
		mainPanel.add(helpPanel);
		
		// Cogemos el JSON de las factorías del main, que sobreescriben el método 
		// fill_in_data dependiendo de el tipo de región
		_regionsInfo = Main._regions_factory.get_info();
		
		// _dataTableModel es un modelo de tabla que incluye todos los parámetros de
		// la region
		_dataTableModel = new DefaultTableModel() {
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1; // Hacer editable solo la columna 1
			} 
		};
		
		// Colocamos los nombres de las columnas
		_dataTableModel.setColumnIdentifiers(_headers);
	
		// Crear un JTable que use _dataTableModel, y añadirlo al diálogo
		JTable t = new JTable(_dataTableModel);
		t.getColumnModel().getColumn(2).setPreferredWidth(200);
		mainPanel.add(new JScrollPane(t, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		// _regionsModel es un modelo de combobox que incluye los tipos de regiones
		_regionsModel = new DefaultComboBoxModel<>();

		// Añadir cada tipo de región
		for(JSONObject jo: _regionsInfo) { _regionsModel.addElement(jo.getString("type")); }

		// Creamos el panel inferior, que contiene los combobox
		JComboBox<String> regions = new JComboBox<>(_regionsModel);	
		JLabel regionTypeLabel = new JLabel("Region type: ");
		JPanel panelInferior = new JPanel();
		
		// Actualizar la tabla dependiendo de la región escogida
		regions.addActionListener((e)-> {
			
			JSONObject info =	_regionsInfo.get(regions.getSelectedIndex());
			JSONObject data = info.getJSONObject("data");

			_dataTableModel.setRowCount(0);

			// Iteramos sobre las keys y añadimos dependiendo de el tipo de región
			for(String s: data.keySet()) { _dataTableModel.addRow(new Object[] {s, "", data.get(s)}); }
			
		});
		
		// Añadir primer JLabel y combobox al panel
		panelInferior.add(regionTypeLabel);
		panelInferior.add(regions);
	
		// Creamos los modelos
		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		
		// Creamos los combobox a partir del modelo
		JComboBox<String> fromRow = new JComboBox<>(_fromRowModel);	
		JComboBox<String> toRow = new JComboBox<>(_toRowModel);	
		JLabel rowFromToText = new JLabel("Row from/to: ");
		
		// Añadir segundo jlabel y combobox al panel
		panelInferior.add(rowFromToText);
		panelInferior.add(fromRow);
		panelInferior.add(toRow);

		// Creamos los modelos
		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();
		
		// Creamos los combobox a partir del modelo
		JComboBox<String> fromCol = new JComboBox<>(_fromColModel);	
		JComboBox<String> toCol = new JComboBox<>(_toColModel);	
		JLabel colFromToText = new JLabel("Col from/to: ");
		
		// Añadir tercer jlabel y combobox al panel
		panelInferior.add(colFromToText);
		panelInferior.add(fromCol);
		panelInferior.add(toCol);
		
		// Crear los botones ok y cancel y añadirlos al diálogo.
		JButton cancel = new JButton("Cancel");
		
		// Si se pulsa hacemos la ventana invisible
		cancel.addActionListener((e) -> setVisible(false));
		
		JButton ok = new JButton("Ok");
		
		// Funcionalidad del botón de ok, creando JSON de la región con los valores  
		ok.addActionListener((e) -> {
			
			JSONObject JSONRegion = new JSONObject();
			JSONObject spec = new JSONObject();
			JSONObject region_data = new JSONObject();
			
			String region_type = regions.getSelectedItem().toString();
			
			JSONArray row = new JSONArray();
					
			// Array con las filas seleccionadas
			row.put(fromRow.getSelectedItem());
			row.put(toRow.getSelectedItem());

			JSONArray col = new JSONArray();

			// Array con las columnas seleccionadas
			col.put(fromCol.getSelectedItem());
			col.put(toCol.getSelectedItem());

			// Colocamos los arrays
			JSONRegion.put("row",row);
			JSONRegion.put("col",col);
			
			// Para iterar sobre las diferentes keys
			JSONObject info =	_regionsInfo.get(regions.getSelectedIndex());
			JSONObject data = info.getJSONObject("data");
			
			// Contador para seguir la fila en la que estamos
			int cont = 0;
			
			// Iteramos sobre las diferentes keys
			for(String s: data.keySet()) {
				
				// Cogemos el valor de la celda
				Object val = _dataTableModel.getValueAt(cont, 1);
				
				// Si la celda no está vacía metemos el valor de dicha celda en el JSON
				if(!val.equals("") && val != null) { region_data.put(s, val);	}
			
				cont++;
			}
			
			// Colocamos los json
			spec.put("data", region_data);
			spec.put("type", region_type);
			
			// Colocamos las especificaciones
			JSONRegion.put("spec", spec);
			
			JSONObject JSONRegionesGrande = new JSONObject();
			JSONArray arrayRegiones = new JSONArray();
			
			arrayRegiones.put(JSONRegion);
			
			JSONRegionesGrande.put("regions", arrayRegiones);
			
			_ctrl.set_regions(JSONRegionesGrande);
			
			setVisible(false);
		});

		// Panel el botón de cancelar o aceptar
		JPanel acciones = new JPanel();
		
		acciones.add(cancel);
		acciones.add(ok);
		
		mainPanel.add(panelInferior);
		mainPanel.add(acciones);
		
		setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
		pack();
		setResizable(false);
		setVisible(false);
	}
	
	public void open(Frame parent) {
		setLocation(
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, 
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
	
	// Establece los combobox dependiendo del mapa (código duplicado en el onReset y onRegister)
	private void setCombobox(MapInfo map) {
		
		// Establecemos las nuevas filas y columnas 
		_cols = map.get_cols();
		_rows = map.get_rows();
		
		// Quitamos todos los elementos de los commobox que ya tenemos
		_fromRowModel.removeAllElements();
		_fromColModel.removeAllElements();
		_toRowModel.removeAllElements();
		_toColModel.removeAllElements();
		
		// Colocamos los elementos de las filas
		for(int i = 0; i < _rows; i++) { _fromRowModel.addElement(i + ""); _toRowModel.addElement(i + ""); }
		
		// Colocamos los elementos de las columnas
		for(int i = 0; i < _cols; i++) { _fromColModel.addElement(i + ""); _toColModel.addElement(i + ""); }
	}
	
	// Establece las filas y columnas para los combobox
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) { setCombobox(map); }
	
	// Reinicia las filas y columnas para los combobox por si el nuevo mapa tiene diferentes dimensiones
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) { setCombobox(map); }
	
	// No hace nada
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) { }
	
	// No hace nada
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) { }

	// No hace nada
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) { }
	
}
