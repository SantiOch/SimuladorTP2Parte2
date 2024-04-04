 package simulator.launcher;

 import org.apache.commons.cli.*;
 import org.json.JSONObject;
 import org.json.JSONTokener;
 import simulator.control.Controller;
 import simulator.factories.*;
 import simulator.misc.Utils;
 import simulator.model.Animal;
 import simulator.model.Region;
 import simulator.model.SelectionStrategy;
 import simulator.model.Simulator;
import simulator.view.ControlPanel;
import simulator.view.MainWindow;

import java.io.FileOutputStream;
 import java.io.FileInputStream;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
//import simulator.view.SimpleObjectViewer;

public class Main {

	public static Factory<Region> _regions_factory;
	public static Factory<Animal> _animals_factory;

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private final String _tag;
		private final String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	public final static Double _default_time = 10.0; // in seconds
	public final static Double _default_delta_time = 0.03; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _time = null;
	private static String _in_file = null;
	private static String _out_file = null;
	private static boolean _sv = false;
	private static Double _dt = null;
	private static ExecMode _mode = ExecMode.BATCH;

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			
			
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_out_file_option(line);
			parse_delta_time_option(line);
			parse_simple_viewer_option(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				StringBuilder error = new StringBuilder("Illegal arguments:");
				for (String o : remaining)
					error.append(" ").append(o);
				throw new ParseException(error.toString());
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// delta time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: " + _default_delta_time + ".").build());

		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());

		// simple viewer
		cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}
	
	private static void parse_simple_viewer_option(CommandLine line) {		
		if(line.hasOption("sv")) {
			_sv = true;
		}
	}

	private static void parse_delta_time_option(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _default_delta_time.toString());
		try {
			_dt = Double.parseDouble(dt);
			assert (_dt >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + dt);
		}		
	}

	private static void parse_out_file_option(CommandLine line) throws ParseException {
		_out_file = line.getOptionValue("o");
		if (_mode == ExecMode.BATCH && _out_file == null) {
			throw new ParseException("In batch mode an output configuration file is required");
		}
		
	}

	private static void init_factories() {

		//Completar el método init_factories para inicializar las factorías y almacenarlas en los atributos correspondientes.
 
		Factory<SelectionStrategy> selection_strategy_factory;
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder()); 
		selection_strategy_builders.add(new SelectYoungestBuilder()); 

		selection_strategy_factory = new BuilderBasedFactory<>(selection_strategy_builders);
		
		List<Builder<Animal>> animal_builders = new ArrayList<>();

		animal_builders.add(new WolfBuilder(selection_strategy_factory));
		animal_builders.add(new SheepBuilder(selection_strategy_factory));
		
		_animals_factory = new BuilderBasedFactory<>(animal_builders);
		
		List<Builder<Region>> region_builders = new ArrayList<>();

		region_builders.add(new DefaultRegionBuilder());
		region_builders.add(new DynamicSupplyRegionBuilder());
		
		_regions_factory = new BuilderBasedFactory<>(region_builders);

	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}


	private static void start_batch_mode() throws Exception {
		
		InputStream is = new FileInputStream(_in_file);

		OutputStream os = new FileOutputStream(_out_file);
		
		JSONObject jo = load_JSON_file(is);

		Simulator sim = new Simulator(jo.getInt("cols"), jo.getInt("rows"), jo.getInt("width"), jo.getInt("height"), _animals_factory, _regions_factory);
		
		Controller con = new Controller(sim);

		con.load_data(jo);
		
		SwingUtilities.invokeLater(() -> new MainWindow(con));
		
//
//		con.run(_time, _dt, _sv, os);
//		
//		os.close();
		
		/* Completar el método start_batch_mode para que haga lo siguiente 
		 * (1) cargar el archivo de entrada en un JSONObject; 
		 * (2) crear el archivo de salida; 
		 * (3) crear una instancia de Simulator pasando a su constructora la información que necesita; 
		 * (4) crear una instancia de Controller pasandole el simulador; 
		 * (5) llamar a load_data pasandole el JSONObject de la entrada; y 
		 * (6) llamar al método run con los parámetros correspondents; y 
		 * (7) cerrar el archivo de salida. */
	}

	private static void start_GUI_mode() throws Exception {
		throw new UnsupportedOperationException("GUI mode is not ready yet ...");
	}

	private static void start(String[] args) throws Exception {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		init_factories(); 
		parse_args(args); 
		switch (_mode) { 
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode(); 
			break; 
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647L);

		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
