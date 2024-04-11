package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

import javax.swing.JLabel;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.misc.Utils;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolBar;
	private JFileChooser _fc;
//	private FileDialog fd;

	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton;
	private JButton _fileButton;
	private JButton _viewerButton;
	private JButton _regionsButton;
	private JButton _playButton;
	private JButton _pauseButton;
	private JSpinner _steps;
	private JTextField _dt;

	public ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI(); 
	}

	private void initGUI() {

		setLayout(new BorderLayout());
		_toolBar = new JToolBar(); 
		add(_toolBar, BorderLayout.PAGE_START);

		_fc = new JFileChooser();
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		JFrame f = new JFrame();

		//FileChooser Button
		_fileButton = new JButton();
		_fileButton.setToolTipText("Choose a file");
		_fileButton.setIcon(new ImageIcon("resources/icons/open.png"));
		_fileButton.addActionListener((e) -> {

			
//			fd = new FileDialog(f, "Choose a file", FileDialog.LOAD);
//			fd.setDirectory(System.getProperty("user.dir") + "/resources/examples");
//			fd.setVisible(true);
//
//			if(fd.getFile() == null) {
//				ViewUtils.showErrorMsg("Error cargando el archivo de entrada");
//
//			}else {
//				try {
//					InputStream in = new FileInputStream(new File(fd.getDirectory() + fd.getFile()));
//					JSONObject jo = new JSONObject(new JSONTokener(in));
//				_ctrl.reset(jo.getInt("cols"), jo.getInt("rows"), jo.getInt("width"), jo.getInt("height"));
//				_ctrl.load_data(jo);
//				} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
			
			if(_fc.showOpenDialog(ViewUtils.getWindow(this)) == JFileChooser.APPROVE_OPTION) {

				try {
						
					InputStream in = new FileInputStream(_fc.getSelectedFile());
					JSONObject jo = new JSONObject(new JSONTokener(in));
					_ctrl.reset(jo.getInt("cols"), jo.getInt("rows"), jo.getInt("width"), jo.getInt("height"));
					_ctrl.load_data(jo);
					
				}catch(Exception exc) {
					exc.printStackTrace();
					ViewUtils.showErrorMsg("Error cargando el archivo de entrada" + exc.getMessage());
				}
			}
		});

		_toolBar.add(_fileButton);
		_toolBar.addSeparator();

		//Viewer Button
		_viewerButton = new JButton();
		_viewerButton.setToolTipText("Show map viewer");
		_viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_viewerButton.addActionListener((e)-> {
			SwingUtilities.invokeLater(() -> new MapViewer());
		});

		_toolBar.add(_viewerButton);

		//RegionsButton
		_regionsButton = new JButton();
		//TODO
		_regionsButton.setToolTipText("Show region");
		_regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		_regionsButton.addActionListener((e) ->  _changeRegionsDialog.open(ViewUtils.getWindow(this)) );
		_toolBar.add(_regionsButton);
		_toolBar.addSeparator();

		//Play Button
		_playButton = new JButton();
		_playButton.setToolTipText("Resume simluation");
		_playButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_playButton.addActionListener((e)-> {

			disableButtons();
			_stopped = false;
			Double dt = Double.parseDouble(_dt.getText());
			Integer time = (Integer) _steps.getValue();
			run_sim(time, dt);

		});
		_toolBar.add(_playButton);

		//Pause Button
		_pauseButton = new JButton();
		_pauseButton.setToolTipText("Pause the simulation");
		_pauseButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_pauseButton.addActionListener((e) -> {
			_stopped = true;
			enableButtons();
		});
		_toolBar.add(_pauseButton);

		//Steps
		_steps = new JSpinner(new SpinnerNumberModel(5000, 1, 10000, 100));
		_steps.setMaximumSize(new Dimension(80, 40));
		_steps.setMinimumSize(new Dimension(80, 40));
		_steps.setPreferredSize(new Dimension(80, 40));		
		_toolBar.add(new JLabel("Steps: "));
		_toolBar.add(_steps);

		//Delta-Time
		_dt = new JTextField(Main._default_delta_time.toString());
		_toolBar.add(new JLabel("Delta-Time: "));
		_toolBar.add(_dt);

		//Quit Button
		_toolBar.add(Box.createGlue()); // this aligns the button to the right
		_toolBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		_toolBar.add(_quitButton);

		// TODO Inicializar _changeRegionsDialog con instancias del diálogo de cambio
		// de regiones
		_changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
	}

	private void run_sim(int n, double dt) {
		if (n > 0 && !_stopped) {
			try {
				_ctrl.advance(dt);
				Thread.sleep(1);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt)); 

			} catch (Exception e) {

				ViewUtils.showErrorMsg(e.getMessage());
				e.printStackTrace();
				enableButtons();
				_stopped = true;
			}
		} else {

			enableButtons();
			_stopped = true;
		}
	}

	private void enableButtons() {
		_quitButton.setEnabled(true);
		_fileButton.setEnabled(true);
		_viewerButton.setEnabled(true);
		_regionsButton.setEnabled(true);
		_playButton.setEnabled(true);		
	}

	private void disableButtons() {
		_quitButton.setEnabled(false);
		_fileButton.setEnabled(false);
		_viewerButton.setEnabled(false);
		_regionsButton.setEnabled(false);
		_playButton.setEnabled(false);
	}
}
