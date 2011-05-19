import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JFrame;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister.Pack;

import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;


public class control extends ControlAdapter {
	
	private Map nodeMap;
	
	public control(Map nodeMap) 
	{	
		this.nodeMap = nodeMap;
	}
	
	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		int nodeId = item.getInt(NetMonitor.NODEID);
        NodeData nodeData = (NodeData)nodeMap.get(nodeId);
        
        if (nodeData.dataFrame == null) {
	        JFrame newDataFrame = new JFrame("Sample Window, Node " + nodeData.getNodeId());
	        newDataFrame.setSize(1500,350);
	        
	        DiagrammPanel diagram = new DiagrammPanel(nodeData.dataset, nodeData.getNodeId());	        
	        newDataFrame.add(diagram, BorderLayout.WEST);
	        diagram.draw(nodeData.dataset);
	        
	        TemperatureChart temperatureChart = new TemperatureChart(nodeData.datasetTemperature, nodeData.getNodeId());
	        newDataFrame.add(temperatureChart, BorderLayout.CENTER);
	        temperatureChart.draw(nodeData.datasetTemperature);
	        
	        TemperaturePanel temperature = new TemperaturePanel(nodeData.temperatureDataset, nodeData.getNodeId());
//	        temperature.pack();
//	        temperature.setVisible(true);
	        newDataFrame.add(temperature, new BorderLayout().EAST);
	        
	        newDataFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	        newDataFrame.setVisible(true);
	        nodeData.dataFrame = newDataFrame;
        }
        
        else {
        	nodeData.dataFrame.setVisible(true);
        }
	}
}
