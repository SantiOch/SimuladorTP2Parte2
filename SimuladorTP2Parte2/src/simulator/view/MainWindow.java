package simulator.view;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import simulator.control.Controller;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		
		// Para coger la dimensión de la pantalla
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		// Para colocar el simulador más o menos centrado
		setLocation((int) (screen.getWidth()/3), (int) (screen.getHeight()/6));

		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		// Crear ControlPanel y añadirlo en PAGE_START de mainPanel
		ControlPanel c = new ControlPanel(_ctrl);
		this.add(c, BorderLayout.PAGE_START);;
		
		// Crear StatusBar y añadirlo en PAGE_END de mainPanel
		StatusBar status = new StatusBar(_ctrl);
		this.add(status, BorderLayout.PAGE_END);
		
		// Definición del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); 
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		
		// Crear la tabla de especies y añadirla a contentPanel.
		InfoTable species = new InfoTable("Species", new SpeciesTableModel(_ctrl));
		species.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(species);
		
		// Crear la tabla de regiones.
		InfoTable regions = new InfoTable("Regions", new RegionsTableModel(_ctrl));
		regions.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(regions);

		this.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) { ViewUtils.quit(MainWindow.this); }

			@Override
			public void windowClosed(WindowEvent e) { ViewUtils.quit(MainWindow.this); }

			@Override
			public void windowIconified(WindowEvent e) { }

			@Override
			public void windowDeiconified(WindowEvent e) { }

			@Override
			public void windowActivated(WindowEvent e) { }

			@Override
			public void windowDeactivated(WindowEvent e) { }

			@Override
			public void windowOpened(WindowEvent e) { }
			
		});
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 
		pack();
		setVisible(true);
	} 
}