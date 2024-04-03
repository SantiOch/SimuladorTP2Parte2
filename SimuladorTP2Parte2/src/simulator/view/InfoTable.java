package simulator.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
	
@SuppressWarnings("serial")
public class InfoTable extends JPanel {
	
	String _title;
	TableModel _tableModel;

	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI(); 
	}

	private void initGUI() {
		
		this.setLayout(new BorderLayout());
			
		this.setBorder(new TitledBorder(_title));

		JTable tab = new JTable(_tableModel);
		
		this.add(new JScrollPane(tab, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

	} 
}

