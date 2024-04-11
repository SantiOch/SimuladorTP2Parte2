package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JTextArea;
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
	//IMPORTANTE: Si añadimos más códigos genéticos y/o estados al simulador, la tabla tiene que seguir
	//funcionando igual sin la necesidad de modificar nada de su código, y por eso (1) está prohibido hacer
	//referencia explícita a códigos genéticos como “sheep” y “wolf”, esta información hay que sacarla de la
	//lista de animales; (2) está prohibido hacer referencia a estados concretos como NORMAL, DEAD, etc. Hay que
	//usar State.values() para saber cuáles son los posibles estados.
	//
	//IMPORTANTE: Si añadimos más tipos de dietas al simulador, la tabla tiene que seguir funcionando igual sin
	//la necesidad de modificar nada de su código, y por eso está prohibido hacer referencia explícita a tipos de
	//dietas como CARNIVORE y HERBIVORE. Hay que usar Diet.values() para saber cuales son las posibles
	//dietas.


	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	private String[] _headers = { "Key", "Value", "Description" };

	int _rows;
	int _cols;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame)null, true);
		_ctrl = ctrl;
		initGUI();
		ctrl.addObserver(this);
	}
	
	private void initGUI() {
		
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); 
		setContentPane(mainPanel);
		
		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new BorderLayout());
		JLabel help = new JLabel();
		help.setText("<html><p>Select a region type, the rows/cols interval,"
				+ " and provide values for the parametes in the <b>Value column</b> "
				+ "(default values are used for parametes with no value).</p></html>");
		
		help.setLayout(new FlowLayout(FlowLayout.LEFT));
		help.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		helpPanel.add(help);
		mainPanel.add(helpPanel);
		
		// TODO crea varios paneles para organizar los componentes visuales en el
		// dialogo, y añadelos al mainpanel. P.ej., uno para el texto de ayuda,
		// uno para la tabla, uno para los combobox, y uno para los botones.
		// TODO crear el texto de ayuda que aparece en la parte superior del diálogo y
		// añadirlo al panel correspondiente diálogo (Ver el apartado Figuras)
		// _regionsInfo se usará para establecer la información en la tabla
		_regionsInfo = Main._regions_factory.get_info();
		// _dataTableModel es un modelo de tabla que incluye todos los parámetros de
		// la region
		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1; // TODO hacer editable solo la columna 1
			} 
		};
		
		_dataTableModel.setColumnIdentifiers(_headers);

		// TODO crear un JTable que use _dataTableModel, y añadirlo al diálogo
		JTable t = new JTable(_dataTableModel);
		mainPanel.add(new JScrollPane(t));
		
		// _regionsModel es un modelo de combobox que incluye los tipos de regiones
		
		_regionsModel = new DefaultComboBoxModel<>();

		for(JSONObject jo: this._regionsInfo) {
			_regionsModel.addElement(jo.getString("type"));
		}
		
		// TODO añadir la descripción de todas las regiones a _regionsModel, para eso
		// usa la clave “desc” o “type” de los JSONObject en _regionsInfo,
		// ya que estos nos dan información sobre lo que puede crear la factoría.
		// TODO crear un combobox que use _regionsModel y añadirlo al diálogo.
		
		JPanel panelInferior = new JPanel();
		JComboBox<String> regions = new JComboBox<>(_regionsModel);	
		JLabel j1 = new JLabel("Region type: ");
		
		//Añadir primer jlabel y combobox al panel
		panelInferior.add(j1);
		panelInferior.add(regions);
	
		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		
		//Creamos los combobox a partir del modelo
		JComboBox<String> fromRow = new JComboBox<>(_fromRowModel);	
		JComboBox<String> toRow = new JComboBox<>(_toRowModel);	
		JLabel j2 = new JLabel("Row from/to: ");
		
		//Añadir segundo jlabel y combobox al panel
		panelInferior.add(j2);
		panelInferior.add(fromRow);
		panelInferior.add(toRow);

		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();
		
		//Creamos los combobox a partir del modelo
		JComboBox<String> fromCol = new JComboBox<>(_fromColModel);	
		JComboBox<String> toCol = new JComboBox<>(_toColModel);	
		JLabel j3 = new JLabel("Col from/to: ");
		
		//Añadir tercer jlabel y combobox al panel
		panelInferior.add(j3);
		panelInferior.add(fromCol);
		panelInferior.add(toCol);
		
		// TODO crear 4 modelos de combobox para _fromRowModel, _toRowModel,
		//       _fromColModel y _toColModel.
		// TODO crear 4 combobox que usen estos modelos y añadirlos al diálogo.
		
		
		// TODO crear los botones OK y Cancel y añadirlos al diálogo.
		JButton cancel = new JButton("Cancel");
		
		cancel.addActionListener((e) -> this.setVisible(false));
		
		JButton ok = new JButton("Ok");
		
		ok.addActionListener((e) -> {
			
			/*"regions" : [ {
                "row" : [ row_from, row_to ],
                "col" : [ col_from, col_to ],
                "spec" : {
                  "type" : region_type,
                  "data" : region_data
                }
]*/
			
			JSONObject jo = new JSONObject();
			JSONObject region_data = new JSONObject();
			JSONObject spec = new JSONObject();
			String region_type = regions.getSelectedItem().toString();
			
			JSONArray row = new JSONArray();
						
			row.put(fromRow.getSelectedItem());
			row.put(toRow.getSelectedItem());

			JSONArray col = new JSONArray();

			col.put(fromCol.getSelectedItem());
			col.put(toCol.getSelectedItem());

			jo.put("row",row);
			jo.put("col",col);
			
			spec.put("data", region_data);
			spec.put("type", region_type);
			
			jo.put("spec", spec);
			
			JSONObject JSONRegiones = new JSONObject();
			JSONArray arrayRegiones = new JSONArray();
			
			arrayRegiones.put(jo);
			
			JSONRegiones.put("regions", arrayRegiones);
			
			_ctrl.set_regions(JSONRegiones);
			
			this.setVisible(false);
		});

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
		setLocation(//
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		
		this._cols = map.get_cols();
		this._rows = map.get_rows();
		
		_fromRowModel.removeAllElements();
		_fromColModel.removeAllElements();
		_toRowModel.removeAllElements();
		_toColModel.removeAllElements();
		
		for(int i = 0; i < _rows; i++) {
			_fromRowModel.addElement(i + "");
			_toRowModel.addElement(i + "");
		}
		
		for(int i = 0; i < _cols; i++) {
			_fromColModel.addElement(i + "");
			_toColModel.addElement(i + "");
		}
		
	}
	
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		
		this._cols = map.get_cols();
		this._rows = map.get_rows();
		
		_fromRowModel.removeAllElements();
		_fromColModel.removeAllElements();
		_toRowModel.removeAllElements();
		_toColModel.removeAllElements();
		
		for(int i = 0; i < _rows; i++) {
			_fromRowModel.addElement(i + "");
			_toRowModel.addElement(i + "");
		}
		
		for(int i = 0; i < _cols; i++) {
			_fromColModel.addElement(i + "");
			_toColModel.addElement(i + "");
		}
	}
	
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}
	
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}
	
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {}
	
	//TODO el resto de métodos van aquí... 
}
