package simulator.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

@SuppressWarnings("serial")
class MapWindow extends JFrame implements EcoSysObserver {
	
	private Controller _ctrl;
	private AbstractMapViewer _viewer;
	private Frame _parent;
	
	MapWindow(Frame parent, Controller ctrl) {
		super("[MAP VIEWER]");
		_ctrl = ctrl;
		_parent = parent;
		intiGUI();
		ctrl.addObserver(this);
	}

	private void intiGUI() {
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		// Poner contentPane como mainPanel
		this.setContentPane(mainPanel);
		
		// Cear el viewer y añadirlo a mainPanel (en el centro)
		_viewer = new MapViewer();
		mainPanel.add(_viewer, BorderLayout.CENTER);
		
		// En el método windowClosing, eliminar ‘MapWindow.this’ de los
		// observadores
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) { _ctrl.removeObserver(MapWindow.this); }

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}
			
		});

		pack();
		
		if (_parent != null) {
			setLocation(
					_parent.getLocation().x + _parent.getWidth()/4,
					_parent.getLocation().y + _parent.getHeight()/6);
		}
			
		setResizable(false);
		setVisible(true);
	}
	
	// Llama al reset del viewer
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(()-> {_viewer.reset(time, map, animals); pack();});
	}
	
	// Llama al reset del viewer
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(()-> {_viewer.reset(time, map, animals); pack();});
	}
	
	// No hace nada
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {	}
	
	// No hace nada
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) { }
	
	// No hace nada
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater( () ->	this._viewer.update(animals, time));
	}
}
