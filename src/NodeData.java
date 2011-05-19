import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.expression.Predicate;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ColorLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;

public class NodeData {
	private int nodeId;
	
	private int accelX;
	private int accelY;
	private int accelZ;
	
	private double temp;
	
	private int age;
	
	private Node thisNode;
	
	private int alertRounds;
	
	public XYSeriesCollection dataset;
	public XYSeriesCollection datasetTemperature;
	public DefaultValueDataset temperatureDataset;
	
	public JFrame dataFrame;
	
	public static int ROOT = 0;
	
	private Window gui;
	
	private HistoryManagement history;
	
	public NodeData(int nodeId, Window gui, HistoryManagement history) {
		thisNode = null;
		dataFrame = null;
		
		this.nodeId = nodeId;
		this.gui = gui;
		this.history = history;
		
		dataset = new XYSeriesCollection();
		XYSeries accelx = new XYSeries("Acceleration X");
		XYSeries accely = new XYSeries("Acceleration Y");
		XYSeries accelz = new XYSeries("Acceleration Z");
		
		dataset.addSeries(accelx);
		dataset.addSeries(accely);
		dataset.addSeries(accelz);

		datasetTemperature = new XYSeriesCollection();
		XYSeries temperature = new XYSeries("Temperature");
		datasetTemperature.addSeries(temperature);
		
		temperatureDataset = new DefaultValueDataset(10.0);
		
		alertRounds = 0;
		
		thisNode = gui.graphDisplay.graph.addNode();
		
		thisNode.set(NetMonitor.NODEID, "" + nodeId);
		thisNode.set(NetMonitor.LABEL, "" + nodeId);
		thisNode.set(NetMonitor.COLOR, getColor());
		if ( nodeId != ROOT ) {
			thisNode.set(NetMonitor.CURRENT, false);
		}
	}
	
	public void setLabel(String text)
	{
		thisNode.set(NetMonitor.LABEL, text);
	}
	
	public int getNodeId()
	{
		return nodeId;
	}
	
	public void clearAge()
	{
		this.age = 0;
	}
	
	public int getAge()
	{
		return age;
	}
	
	public void incAge()
	{
		this.age++;
	}
	
	public void setAlert(int alertRounds)
	{
		this.alertRounds = alertRounds;
	}
	
	public int getAlert()
	{
		return this.alertRounds;
	}
	
	public void addEmptyValues(int round)
	{
		dataset.getSeries(0).add(round, this.accelX);
		dataset.getSeries(1).add(round, this.accelY);
		dataset.getSeries(2).add(round, this.accelZ);
		
		datasetTemperature.getSeries(0).add(round, this.temp);
		temperatureDataset.setValue(this.temp);		
	}
	
	public void addTempValues(int round, int temp)
	{
		if (nodeId == ROOT)
			return;
		
		double temperature;
		temperature = (temp - 2732)/10.0;
		this.temp = temperature;
				
		dataset.getSeries(0).add(round, this.accelX);
		dataset.getSeries(1).add(round, this.accelY);
		dataset.getSeries(2).add(round, this.accelZ);
		
		datasetTemperature.getSeries(0).add(round, temperature);
		temperatureDataset.setValue(temperature);
	}
	
	public void addValues(int round, int accelX, int accelY, int accelZ, int temp)
	{
		if (nodeId == ROOT)
			return;
		
		double temperature;
		temperature = (temp - 2732)/10.0;
		this.temp = temperature;
		
		int prevX = this.accelX;
		int prevY = this.accelY;
		int prevZ = this.accelZ;
		
		this.accelX = (int)(( accelX * 0.5 ) + ( prevX * 0.5 ));
		this.accelY = (int)(( accelY * 0.5 ) + ( prevY * 0.5 ));
		this.accelZ = (int)(( accelZ * 0.5 ) + ( prevZ * 0.5 ));   
		
		dataset.getSeries(0).add(round, this.accelX);
		dataset.getSeries(1).add(round, this.accelY);
		dataset.getSeries(2).add(round, this.accelZ);
		
		datasetTemperature.getSeries(0).add(round, temperature);
		temperatureDataset.setValue(temperature);
	}
	
	public void addTableRow (NodeTable nodeTable)
	{
		nodeTable.addRow(nodeId);
	}
	
	public void updateTableRow (NodeTable nodeTable) 
	{		
		int tableRow = nodeTable.getTableRow(nodeId);
		
		nodeTable.tableModel.setValueAt(accelX, tableRow, 1);
		nodeTable.tableModel.setValueAt(accelY, tableRow, 2);
		nodeTable.tableModel.setValueAt(accelZ, tableRow, 3);
		nodeTable.tableModel.setValueAt(temp + "°C", tableRow, 4);
		nodeTable.tableModel.setValueAt(age, tableRow, 5);
		
//		nodeTable.tableModel.fireTableStructureChanged();
	}
	
	public void removeTableRow(NodeTable nodeTable)
	{
		int tableRow = nodeTable.getTableRow(nodeId);
		
		nodeTable.tableModel.removeRow(tableRow);
	}
	
	public void addNode(int round) 
	{
		if ( nodeId == ROOT ) {
			return;
		}
		
		if ( ( thisNode != null ) && ( thisNode.isValid() ) ) {	
			TupleSet ts = gui.graphDisplay.vis.getGroup(NetMonitor.NODES);
		
			for (Iterator it = ts.tuples(); it.hasNext();) {
				NodeItem node = (NodeItem) it.next();
				
				if ( ( node.getInt(NetMonitor.NODEID) == nodeId ) && ( node.getBoolean(NetMonitor.CURRENT) == false )) {
					node.setBoolean(NetMonitor.CURRENT, true);
					
					history.addSet(node, round);
					
					return;
				}		
				
			}	
			
		}
	}

	public void removeNode(int round) 
	{
		TupleSet ts = gui.graphDisplay.vis.getGroup(NetMonitor.NODES);
		
		for (Iterator it = ts.tuples(); it.hasNext();) {
			NodeItem node = (NodeItem) it.next();
			
			if ( ( node.getInt(NetMonitor.NODEID) == nodeId ) && ( node.getBoolean(NetMonitor.CURRENT) == true)) {
				node.setBoolean(NetMonitor.CURRENT, false);
				removeEdges(round);
				
				history.addUnset(node, round);
				
				return;
			}		
			
		}		
	}
	
	public int getParent(String type, Predicate predicate)
	{
		TupleSet ts = gui.graphDisplay.vis.getGroup(NetMonitor.EDGES);
		
		for (Iterator it = ts.tuples(predicate); it.hasNext();) {
			EdgeItem edge = (EdgeItem) it.next();
			
			if ( ( edge.isVisible() ) && ( edge.getSourceNode().getInt(NetMonitor.NODEID) == nodeId )) {
				return edge.getTargetNode().getInt(NetMonitor.NODEID);
			}
			
		}
		
		return -1;
	}
	
	public void addEdge(int targetId, String type, Predicate predicate, int round)
	{
		TupleSet ts = gui.graphDisplay.vis.getGroup(NetMonitor.EDGES);
		
		for (Iterator it = ts.tuples(predicate); it.hasNext();) {
			EdgeItem edge = (EdgeItem) it.next();
			
			if ((edge.getSourceNode().getInt(NetMonitor.NODEID) == nodeId) &&
					(edge.getTargetNode().getInt(NetMonitor.NODEID) == targetId)) {
				edge.setBoolean(NetMonitor.CURRENT, true);
				
				history.addSet(edge, round);
				
				return;
			}
		}
		
		Node targetNode = gui.graphDisplay.graph.getNodeFromKey(targetId);
		
		if ( ( targetNode != null ) && ( targetNode.isValid() ) ) {
			Edge edge = gui.graphDisplay.graph.addEdge(thisNode, targetNode);
			edge.set(NetMonitor.EDGETYPE, type);
			
			VisualItem visualItem = gui.graphDisplay.vis.getVisualItem(NetMonitor.EDGES, edge);
			
			history.addSet(visualItem, round);
			
			
//			for (Iterator it = gui.graphDisplay.vis.getGroup(NetMonitor.EDGES).tuples(predicate); it.hasNext(); ) {
//				VisualItem edge = (VisualItem) it.next();
//				if (edge.i)
//				history.addSet(edge, round);
//			}
		}
	}
	
	public void removeEdge(int targetId, String type, Predicate predicate, int round)
	{
		TupleSet ts = gui.graphDisplay.vis.getGroup(NetMonitor.EDGES);
		
		for (Iterator it = ts.tuples(predicate); it.hasNext();) {
			EdgeItem edge = (EdgeItem) it.next();
			
			if (edge.getTargetNode().getInt(NetMonitor.NODEID) == targetId) {
				edge.setVisible(false);
				edge.set(NetMonitor.CURRENT, false);
				
				history.addUnset(edge, round);				
			}
		}				
	}
	
	public void removeEdges(String type, Predicate predicate, int round)
	{
		TupleSet ts = gui.graphDisplay.vis.getGroup(NetMonitor.EDGES);
		
		for (Iterator it = ts.tuples(predicate); it.hasNext();) {
			EdgeItem edge = (EdgeItem) it.next();
			
			if (edge.getSourceNode().getInt(NetMonitor.NODEID) == nodeId) {
				edge.setVisible(false);	
				edge.set(NetMonitor.CURRENT, false);
				
				history.addUnset(edge, round);
			}
		}				
	}
	
	public void removeEdges(int round)
	{
		TupleSet ts = gui.graphDisplay.vis.getGroup(NetMonitor.EDGES);
		
		for (Iterator it = ts.tuples(); it.hasNext();) {
			EdgeItem edge = (EdgeItem) it.next();
			
			if ( ( edge.getSourceNode().getInt(NetMonitor.NODEID) == nodeId ) || ( edge.getTargetNode().getInt(NetMonitor.NODEID) == nodeId ) ) {
				edge.setVisible(false);
				edge.set(NetMonitor.CURRENT, false);
				
				history.addUnset(edge, round);
			}
		}				
	}
	
	public int getColor() 
	{
//normalen knoten
		if (nodeId == ROOT)
			return ColorLib.rgb(200, 0, 0);

		else if (( nodeId > 1 ) && ( nodeId <= 50 ))
			return ColorLib.rgb(0, 150, 0);

		return ColorLib.rgb(100, 100, 100);
	}
	
	
	
}
