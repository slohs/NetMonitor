import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class DataFileReader {

	private String filePath;
	private BufferedReader inputReader;
	private int readLines;
	
	private Map nodeMap;
	
	private Window gui;
	
	public int currentRound;
	private int maximumRound;
	
	private HistoryManagement history;
	
	public DataFileReader(String filePath, HistoryManagement history)
	{
		this.filePath = filePath;
		this.history = history;
		readLines = 0;
		currentRound = 0;
		maximumRound = 0;
	}
	
	public void openDataFile() {
		File file = new File(filePath);

		try {
			inputReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("cannot open file");
		}

		System.out.println("opened File: " + filePath);
	}
	
	public void setNodeMap(Map nodeMap)
	{
		this.nodeMap = nodeMap;
	}
	
	public void setGUI(Window gui)
	{
		this.gui = gui;
	}
	
	public void readline() 
	{
		while (true) {
			String[] result = {};
			
			try {
				String line = inputReader.readLine();
				result = line.split(";");
			}
			catch (IOException e) {
				System.out.println("cannot read new line");
			}

			catch (NullPointerException e) {
//				 e.printStackTrace();
				return;
			}
	
			processLine(result);
		}
	}
	
	public void processLine(String[] result)
	{
		String packetType = result[0];
		
		if (packetType.equals("CompleteData")) {

			System.out.println("==== Complete =======");
			
			int nodeId = Integer.parseInt(result[1]);
			int parentId = Integer.parseInt(result[2]);
			int level = Integer.parseInt(result[3]);
			
			int accelX = Integer.parseInt(result[4]);
			int accelY = Integer.parseInt(result[5]);
			int accelZ = Integer.parseInt(result[6]);
			
			int temp = Integer.parseInt(result[7]);
	
			float RSSI_dbm = Float.parseFloat( result[8] );
			int LQI = Integer.parseInt( result[9] );
			
			currentRound = Integer.parseInt( result[10] );
			
			NodeData nodeData = (NodeData)nodeMap.get(nodeId);
	
			if (nodeData == null) {
				nodeData = new NodeData(nodeId, gui, history);
				nodeData.addTableRow(gui.nodeTable);
				nodeMap.put(nodeId, nodeData);
			}
			
			int oldParent = nodeData.getParent(NetMonitor.TREEEDGE, NetMonitor.treeEdgePred);
			
			if (oldParent != parentId) {
				nodeData.removeEdge(oldParent, NetMonitor.TREEEDGE, NetMonitor.treeEdgePred, currentRound);
				nodeData.addEdge(parentId, NetMonitor.TREEEDGE, NetMonitor.treeEdgePred, currentRound);
			}
			
			nodeData.addValues(currentRound, accelX, accelY, accelZ, temp);
			
			if (nodeId != 0) {
				nodeData.clearAge();
			}
			nodeData.addNode(currentRound);
			
			setTimeline(currentRound);				

		}
		
//		else if ( packetType.equals("ShortData") ) {
//			System.out.println("==== Temp Only ======");
//			
//			int nodeId = Integer.parseInt(result[1]);
//			int parentId = Integer.parseInt(result[2]);
//			int level = Integer.parseInt(result[3]);
//			
//			int temp = Integer.parseInt(result[4]);
//	
//			NodeData nodeData = (NodeData)nodeMap.get(nodeId);
//	
//			if (nodeData == null) {
//				nodeData = new NodeData(nodeId, gui);
//				nodeData.addTableRow(gui.nodeTable);
//				nodeMap.put(nodeId, nodeData);
//			}
//			
//			int oldParent = nodeData.getParent(NetMonitor.TREEEDGETEMP, NetMonitor.treeEdgeTempPred); 
//			
//			if (oldParent != parentId) {
//				nodeData.removeEdge(oldParent, NetMonitor.TREEEDGETEMP, NetMonitor.treeEdgeTempPred);
//				nodeData.addEdge(parentId, NetMonitor.TREEEDGETEMP, NetMonitor.treeEdgeTempPred);
//			}
//			
//			nodeData.addTempValues(currentRound, temp);
//			
//			if (nodeId != 0) {
//				nodeData.clearAge();
//			}
//		}
		
//		else if ( packetType.equals("AlertData") ) {
//			System.out.println("==== !! ALERT !! ====");
//			
//			int nodeId = Integer.parseInt(result[1]);
//			int parentId = Integer.parseInt(result[2]);
//			int level = Integer.parseInt(result[3]);
//			
//			int temp = Integer.parseInt(result[4]);
//	
//			NodeData nodeData = (NodeData)nodeMap.get(nodeId);
//	
//			if (nodeData == null) {
//				nodeData = new NodeData(nodeId, gui);
//				nodeData.addTableRow(gui.nodeTable);
//				nodeMap.put(nodeId, nodeData);
//			}
//			
//			int oldParent = nodeData.getParent(NetMonitor.TREEEDGEALARM, NetMonitor.treeEdgeAlarmPred); 
//			
//			if (oldParent != parentId) {
//				nodeData.removeEdge(oldParent, NetMonitor.TREEEDGEALARM, NetMonitor.treeEdgeAlarmPred);
//				nodeData.addEdge(parentId, NetMonitor.TREEEDGEALARM, NetMonitor.treeEdgeAlarmPred);
//			}
//			
//			nodeData.addTempValues(currentRound, temp);
//			
//			nodeData.setAlert(5);
//			
//			if (nodeId != 0) {
//				nodeData.clearAge();
//			}
//			
//			
//		}
		
		else if (packetType.equals("StateData") ) {
			System.out.println("==== State ==========");
			
			int nodeId = Integer.parseInt(result[1]);
			int parentId = Integer.parseInt(result[2]);
			int level = Integer.parseInt(result[3]);
			
			float RSSI_dbm = Float.parseFloat( result[4] );
			int LQI = Integer.parseInt( result[5] );
			
			currentRound = Integer.parseInt( result[6] );
			setTimeline(currentRound);
			
			if (nodeId == 0) {
				return;
			}
				
			NodeData nodeData = (NodeData)nodeMap.get(nodeId);
	
			if (nodeData == null) {
				nodeData = new NodeData(nodeId, gui, history);
				nodeData.addTableRow(gui.nodeTable);
				nodeMap.put(nodeId, nodeData);
			}
			
			int oldParent = nodeData.getParent(NetMonitor.TREEEDGESTATE, NetMonitor.treeEdgeStatePred); 
			
			if (oldParent != parentId) {
				nodeData.removeEdge(oldParent, NetMonitor.TREEEDGESTATE, NetMonitor.treeEdgeStatePred, currentRound);
				nodeData.addEdge(parentId, NetMonitor.TREEEDGESTATE, NetMonitor.treeEdgeStatePred, currentRound);
			}
			
			nodeData.addEmptyValues(currentRound);//, 0, 0, 0, -400);
			nodeData.clearAge();
			nodeData.addNode(currentRound);
		}
		
		else if (packetType.equals("NeighborData")) {
			System.out.println("==== Neighbor =======");
			
			int nodeId = Integer.parseInt(result[1]);
			int parentId = Integer.parseInt(result[2]);
			int level = Integer.parseInt(result[3]);
			
			float RSSI_dbm = Float.parseFloat( result[4] );
			int LQI = Integer.parseInt( result[5] );
			
			currentRound = Integer.parseInt( result[6] );
			
			// find node adjazient to this data
			NodeData nodeData = (NodeData)nodeMap.get(nodeId);
			
			if (nodeData == null) {
				nodeData = new NodeData(nodeId, gui, history);
				nodeData.addTableRow(gui.nodeTable);
				nodeMap.put(nodeId, nodeData);
			}
			
			int oldParent = nodeData.getParent(NetMonitor.TREEEDGENEIGHBOR, NetMonitor.treeEdgeNeighborPred); 
			
			if (oldParent != parentId) {
				nodeData.removeEdge(oldParent, NetMonitor.TREEEDGENEIGHBOR, NetMonitor.treeEdgeNeighborPred, currentRound);
				nodeData.addEdge(parentId, NetMonitor.TREEEDGENEIGHBOR, NetMonitor.treeEdgeNeighborPred, currentRound);
			}
			
			nodeData.clearAge();
			nodeData.addNode(currentRound);				
			nodeData.removeEdges(NetMonitor.NEIGHBOREDGEBI, NetMonitor.neighborEdgeBiPred, currentRound);
			
			setTimeline(currentRound);

			int pos = 7;
			
			while (pos < result.length) {
				int neighborId = Integer.parseInt(result[pos]);
				pos++;
				
				nodeData.addEdge(neighborId, NetMonitor.NEIGHBOREDGEBI, NetMonitor.neighborEdgeBiPred, currentRound);
			}
		
		}
		
		if ( maximumRound < currentRound ) {
			maximumRound = currentRound;
		}
	}
	
	private void setTimeline(int roundTimestamp)
	{
		currentRound = roundTimestamp;
		
		if (roundTimestamp > maximumRound) {
			maximumRound = roundTimestamp;
		}
		
		gui.timelineSlider.setValue(roundTimestamp);
		gui.timelineSlider.setMaximum(roundTimestamp);
		
		gui.timelineLabel.setText("" + gui.timelineSlider.getValue());
		
		roundTimestamp = (roundTimestamp / 100) * 100;		
		gui.timelineSlider.setMinorTickSpacing((int)roundTimestamp / 100);
		gui.timelineSlider.setMajorTickSpacing((int)roundTimestamp / 10);
	}
}
