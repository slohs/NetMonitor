import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class NodeTable extends JPanel {

	private static final long serialVersionUID = 1L;

	public DefaultTableModel tableModel;
	
	public NodeTable() {
		super(new GridLayout(1,0));

		tableModel = new DefaultTableModel();
		
		tableModel.addColumn(NetMonitor.NODEID);
		tableModel.addColumn(NetMonitor.ACCELX);
		tableModel.addColumn(NetMonitor.ACCELY);
		tableModel.addColumn(NetMonitor.ACCELZ);
		tableModel.addColumn(NetMonitor.TEMP);
		tableModel.addColumn(NetMonitor.AGE);
		
	    final JTable table = new JTable(tableModel);
	    
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
	}
	
	public void addRow(int nodeId) 
	{
		tableModel.addRow(new Object[] { nodeId });
	}
	
	public int getTableRow(int nodeId) 
	{
		int rowNr = 0;
		while ((rowNr < tableModel.getRowCount()) && ((Integer) tableModel.getValueAt(rowNr, 0) != nodeId))
			rowNr++;
		
		return rowNr;
	}
	
}
