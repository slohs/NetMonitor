import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;


public class GraphDisplay extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	public Graph graph;
	public Visualization vis;
	
	private DataFileReader fileReader;
	private final Map nodeMap;
	
	private Window gui;
	
	public ActionList liveAction;
	private ActionList layout;
	
	public GraphDisplay (DataFileReader fileReader, Map nodeMap, Window gui) 
	{
		super(new GridLayout());
		
		this.nodeMap = nodeMap;
		this.fileReader = fileReader;
		this.gui = gui;
		
		genDisplay();
	}
	
	private void genDisplay()
	{
		vis = new Visualization();
		
		Table graphNodeTable = new Table();
		graphNodeTable.addColumn(NetMonitor.NODEID, int.class, -1);
		graphNodeTable.addColumn(NetMonitor.LABEL, String.class, "--");
		graphNodeTable.addColumn(NetMonitor.COLOR, int.class, ColorLib.rgb(255, 255, 255));
		graphNodeTable.addColumn(NetMonitor.CURRENT, boolean.class, true);
		
		Table graphEdgeTable = new Table();
		graphEdgeTable.addColumn(NetMonitor.EDGE_SOURCE, int.class, -1);
		graphEdgeTable.addColumn(NetMonitor.EDGE_DESTINATION, int.class, -1);
		graphEdgeTable.addColumn(NetMonitor.EDGETYPE, String.class, NetMonitor.TREEEDGE);
		graphEdgeTable.addColumn(NetMonitor.CURRENT, boolean.class, true);
		
		//graph = new Graph(graphNodeTable, true, NetMonitor.NODEID, NetMonitor.EDGE_SOURCE, NetMonitor.EDGE_DESTINATION);
		graph = new Graph(graphNodeTable, graphEdgeTable, true, NetMonitor.NODEID, NetMonitor.EDGE_SOURCE, NetMonitor.EDGE_DESTINATION);
		
		vis.addGraph(NetMonitor.GRAPH, graph);
		
		/* renderer */
		LabelRenderer labelRend = new LabelRenderer(NetMonitor.LABEL);
		labelRend.setRoundedCorner(8, 8); // round the corners
		labelRend.setVerticalPadding(3);
		labelRend.setHorizontalPadding(3);
		labelRend.setHorizontalTextAlignment(Constants.CENTER);

		EdgeRenderer edgeRend = new EdgeRenderer(Constants.EDGE_TYPE_LINE);
		edgeRend.setArrowType(Constants.EDGE_ARROW_FORWARD);
		edgeRend.setArrowHeadSize(5, 3);
		edgeRend.setDefaultLineWidth(1);
		edgeRend.setRenderType(prefuse.render.AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);

		DefaultRendererFactory factory = new DefaultRendererFactory(labelRend);
		factory.setDefaultEdgeRenderer(edgeRend);
		
		vis.setRendererFactory(factory);
		
		/* colors */
		ActionList color = new ActionList();
		ColorAction text = new ColorAction(NetMonitor.NODES, VisualItem.TEXTCOLOR,	ColorLib.rgba(0, 0, 0, 255));
		ColorAction node = new ColorAction(NetMonitor.NODES, VisualItem.STROKECOLOR, ColorLib.rgba(0, 0, 0, 200));
		color.add(text);
		color.add(node);
		
		ColorAction treeEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgePred, VisualItem.STROKECOLOR, ColorLib.rgba(0, 50, 0, 255));
		color.add(treeEdges);
		ColorAction arrowTreeEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgePred, VisualItem.FILLCOLOR, ColorLib.rgba(0, 50, 0, 255));
		color.add(arrowTreeEdges);
		
		ColorAction treeEdgesState = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeStatePred, VisualItem.STROKECOLOR, ColorLib.rgba(0, 100, 0, 255));
		color.add(treeEdgesState);
		ColorAction arrowTreeStateEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeStatePred, VisualItem.FILLCOLOR, ColorLib.rgba(0, 100, 0, 255));
		color.add(arrowTreeStateEdges);
		
		ColorAction treeEdgesAlarm = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeAlarmPred, VisualItem.STROKECOLOR, ColorLib.rgba(0, 150, 0, 255));
		color.add(treeEdgesAlarm);
		ColorAction arrowTreeAlarmEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeAlarmPred, VisualItem.FILLCOLOR, ColorLib.rgba(0, 150, 0, 255));
		color.add(arrowTreeAlarmEdges);
		
		ColorAction treeEdgesTemp = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeTempPred, VisualItem.STROKECOLOR, ColorLib.rgba(0, 200, 0, 255));
		color.add(treeEdgesTemp);
		ColorAction arrowTreeTempEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeTempPred, VisualItem.FILLCOLOR, ColorLib.rgba(0, 2000, 0, 255));
		color.add(arrowTreeTempEdges);
		
		ColorAction treeEdgesNeighbor = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeNeighborPred, VisualItem.STROKECOLOR, ColorLib.rgba(0, 250, 0, 255));
		color.add(treeEdgesNeighbor);
		ColorAction arrowTreeNeighborEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.treeEdgeNeighborPred, VisualItem.FILLCOLOR, ColorLib.rgba(0, 250, 0, 255));
		color.add(arrowTreeNeighborEdges);
		
		ColorAction neighborEdgesBi = new ColorAction(NetMonitor.EDGES, NetMonitor.neighborEdgeBiPred, VisualItem.STROKECOLOR, ColorLib.rgba(150, 0, 0, 150));
		color.add(neighborEdgesBi);
		ColorAction arrowNeighborBiEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.neighborEdgeBiPred, VisualItem.FILLCOLOR, ColorLib.rgba(150, 0, 0, 150));
		color.add(arrowNeighborBiEdges);
		
		ColorAction neighborEdgesUni = new ColorAction(NetMonitor.EDGES, NetMonitor.neighborEdgeUniPred, VisualItem.STROKECOLOR, ColorLib.rgba(150, 0, 0, 150));
		color.add(neighborEdgesUni);
		ColorAction arrowNeighborUniEdges = new ColorAction(NetMonitor.EDGES, NetMonitor.neighborEdgeUniPred, VisualItem.FILLCOLOR, ColorLib.rgba(150, 0, 0, 150));
		color.add(arrowNeighborUniEdges);

		vis.putAction("color", color);
		/*
		 * Layout and animations
		 */
		layout = new ActionList(Action.INFINITY);
//		layout.add(new NodeLinkTreeLayout(NetMonitor.GRAPH, Constants.ORIENT_TOP_BOTTOM, 50.0, 25.0, 5.0));
		ForceDirectedLayout forceLayout = new ForceDirectedLayout(NetMonitor.GRAPH, true);
				
		layout.add(forceLayout);
		layout.add(color);
		layout.add(new RepaintAction());
		layout.add(new EdgeAction(NetMonitor.EDGES, nodeMap, gui));
		layout.add(new NodeAction(NetMonitor.NODES, gui));
		
		vis.putAction(NetMonitor.LAYOUTACTION, layout);
		
		/*
		 * Round actions
		 */
		ActionList timeSteps = new ActionList(Action.INFINITY, 500);
		
		timeSteps.add(new AlertAction(NetMonitor.GRAPH, nodeMap));
		
		vis.putAction("timesteps", timeSteps);
		
		liveAction = new ActionList(Action.INFINITY, 500);
		liveAction.add(new ReadlineAction(fileReader, vis));
		liveAction.add(new AgingAction(NetMonitor.NODES, nodeMap, gui));		
		
		
		vis.putAction(NetMonitor.LIVEACTION, liveAction);
		
		Display display = new Display(vis);
		display.setSize(720, 500); // set display size
		display.addControlListener(new DragControl()); // drag items around
		display.addControlListener(new PanControl()); // pan with background
		display.addControlListener(new ZoomControl()); // zoom with vertical
		
		display.addControlListener(new control(nodeMap));
		
		display.setHighQuality(true);
		display.pan(300.0, 300.0);
		
		add(display);			
	}	
	
}
