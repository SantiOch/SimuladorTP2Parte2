package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

@SuppressWarnings("serial")
class StatusBar extends JPanel implements EcoSysObserver {
	
	private JLabel _total_animals;
	private JLabel _time;
	private JLabel _dim;
	
	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}
	
	private void initGUI() {

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		
		// Crea el JLabel del tiempo y lo pone en la barra
		_time = new JLabel();
		this.add(_time);
		
		// Separadores
		JSeparator s = new JSeparator(JSeparator.VERTICAL); 
		s.setPreferredSize(new Dimension(10, 20));
		this.add(s);
		
		// Crea el JLabel de animales y lo pone en la barra
		_total_animals = new JLabel();
		this.add(_total_animals);
		
		// Separadores
		JSeparator s2 = new JSeparator(JSeparator.VERTICAL); 
		s2.setPreferredSize(new Dimension(10, 20));
		this.add(s2);		
		
		// Crea el JLabel de la dimensión y lo pone en la barra
		_dim = new JLabel();
		this.add(_dim);
	}
	
	// Inicia los JLabel del tiempo, animales y dimensión
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		_time.setText("Time: %,.3f".formatted(time));
		_total_animals.setText("Total animals: %d".formatted(animals.size()));
		_dim.setText("Dimension: %dx%d %dx%d".formatted(map.get_width(), map.get_height(), map.get_cols(), map.get_rows()));
		
	}
	
	// Resetea los JLabel del tiempo, animales y dimensión
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_time.setText("Time: %,.3f".formatted(time));
		_total_animals.setText("Total animals: %d".formatted(animals.size()));
		_dim.setText("Dimension: %dx%d %dx%d".formatted(map.get_width(), map.get_height(), map.get_cols(), map.get_rows()));
	}
	
	// No hace nada
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) { 
		_total_animals.setText("Total animals: %d".formatted(animals.size()));
	}
	
	// No hace nada
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) { }
	
	// Avanza los JLabel del tiempo, animales y dimensión
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		_time.setText("Time: %,.3f".formatted(time));
		_total_animals.setText("Total animals: %d".formatted(animals.size()));
	}
}
