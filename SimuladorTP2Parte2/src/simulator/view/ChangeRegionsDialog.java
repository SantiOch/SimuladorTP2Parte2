package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

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

	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	private String[] _headers = { "Key", "Value", "Description" };

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame)null, true);
		_ctrl = ctrl;
		initGUI();
		// TODO registrar this como observer;
	}
	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); setContentPane(mainPanel);
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
		// _regionsModel es un modelo de combobox que incluye los tipos de regiones
		_regionsModel = new DefaultComboBoxModel<>();
		// TODO añadir la descripción de todas las regiones a _regionsModel, para eso
		//       usa la clave “desc” o “type” de los JSONObject en _regionsInfo,

		//      ya que estos nos dan información sobre lo que puede crear la factoría.
		// TODO crear un combobox que use _regionsModel y añadirlo al diálogo.
		// TODO crear 4 modelos de combobox para _fromRowModel, _toRowModel,
		//       _fromColModel y _toColModel.
		// TODO crear 4 combobox que usen estos modelos y añadirlos al diálogo.
		// TODO crear los botones OK y Cancel y añadirlos al diálogo.
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
		// TODO Auto-generated method stub

	}
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub

	}
	//TODO el resto de métodos van aquí... 
}
