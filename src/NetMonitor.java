import java.util.HashMap;
import java.util.Map;

import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;


public class NetMonitor {

	public static String ACCELX = "accelX";
	public static String ACCELY = "accelY";
	public static String ACCELZ = "accelZ";
	public static String NODEID = "NodeID";
	public static String LABEL = "Label";
	public static String PARENT = "ParentID";
	public static String COLOR = "MyColor";
	public static String LEVEL = "Level";
	public static String DATATABLE = "Datatable";
	public static String TEMP = "Temperature";
	public static String CURRENT = "Current";
	
	public static String EDGETYPE = "EdgeType";
	
	public static String TREEEDGE = "TreeEdge";
	public static Predicate treeEdgePred = (Predicate)ExpressionParser.parse("ISEDGE() AND (" + NetMonitor.EDGETYPE + " = \"" + NetMonitor.TREEEDGE + "\")");
	public static String TREEEDGESTATE = "TreeEdgeState";
	public static Predicate treeEdgeStatePred = (Predicate)ExpressionParser.parse("ISEDGE() AND (" + NetMonitor.EDGETYPE + " = \"" + NetMonitor.TREEEDGESTATE + "\")");
	public static String TREEEDGEALARM = "TreeEdgeAlarm";
	public static Predicate treeEdgeAlarmPred = (Predicate)ExpressionParser.parse("ISEDGE() AND (" + NetMonitor.EDGETYPE + " = \"" + NetMonitor.TREEEDGEALARM + "\")");
	public static String TREEEDGETEMP = "TreeEdgeTemp";
	public static Predicate treeEdgeTempPred = (Predicate)ExpressionParser.parse("ISEDGE() AND (" + NetMonitor.EDGETYPE + " = \"" + NetMonitor.TREEEDGETEMP + "\")");
	public static String TREEEDGENEIGHBOR = "TreeEdgeNeighbor";
	public static Predicate treeEdgeNeighborPred = (Predicate)ExpressionParser.parse("ISEDGE() AND (" + NetMonitor.EDGETYPE + " = \"" + NetMonitor.TREEEDGENEIGHBOR + "\")");

	public static String NEIGHBOREDGEBI = "NeighborEdgeBi";
	public static Predicate neighborEdgeBiPred = (Predicate)ExpressionParser.parse("ISEDGE() AND (" + NetMonitor.EDGETYPE + " = \"" + NetMonitor.NEIGHBOREDGEBI + "\")");
	public static String NEIGHBOREDGEUNI = "NeighborEdgeUni";
	public static Predicate neighborEdgeUniPred = (Predicate)ExpressionParser.parse("ISEDGE() AND (" + NetMonitor.EDGETYPE + " = \"" + NetMonitor.NEIGHBOREDGEUNI + "\")");
	
	
	
	public static String AGE = "Age";

	public static String PARENTNODE = "Parent_Node";
	public static String PARENTEDGE = "Parent_Edge";

	public static String EDGE_SOURCE = "Edge_Source";
	public static String EDGE_DESTINATION = "Edge_Destination";

	public static final String GRAPH = "graph";
	public static final String NODES = "graph.nodes";
	public static final String EDGES = "graph.edges";
	
	public static final String LIVEACTION = "readlineActionList";
	public static final String LAYOUTACTION = "layoutActionList";
	
	public static int MAXAGE = 50;
	public static int MAXDELETEAGE = 100;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HistoryManagement history = new HistoryManagement();
		
		Map nodeMap = new HashMap();
		
		DataFileReader fileReader = new DataFileReader(args[0], history);
		
		Window window = new Window(nodeMap, fileReader, history);
		
		fileReader.setGUI(window);
		fileReader.openDataFile();		
		fileReader.setNodeMap(nodeMap);
		
		NodeData root = new NodeData(NodeData.ROOT, window, history);
		nodeMap.put(NodeData.ROOT, root);
		
		root.addTableRow(window.nodeTable);
		root.addValues(0, 0, 0, 0, 0);
		root.setLabel("root");
		root.addNode(0);
		
		window.graphDisplay.vis.run("color"); // assign the colors
		window.graphDisplay.vis.run(LAYOUTACTION); // start up the animated layout
		window.graphDisplay.vis.run("timesteps");
		
		window.graphDisplay.vis.run(LIVEACTION);
		
	}

}
