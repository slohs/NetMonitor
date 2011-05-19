import java.util.Iterator;
import java.util.Map;

import prefuse.action.GroupAction;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ColorLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;


public class AgingAction extends GroupAction 
{

	private Map nodeMap;
	private Window gui;
	
	public AgingAction(String group, Map nodeMap, Window gui) 
	{
		super(group);
		this.gui = gui;
		this.nodeMap = nodeMap;
	}
	
	@Override
	public void run(double frac) 
	{
		/**
		 * updates the color-alpha value of nodes
		 * remove nodes and edges if age value increases above a value
		 */
		TupleSet ts = getVisualization().getGroup(this.m_group);
		
		for (Iterator iterator = ts.tuples(); iterator.hasNext();) {
			NodeItem node = (NodeItem) iterator.next();
			
			int nodeId = node.getInt(NetMonitor.NODEID);
			NodeData nodeData = (NodeData) nodeMap.get(nodeId);
			
			/* do some updates */
			nodeData.updateTableRow(gui.nodeTable);
			nodeData.incAge();
			
			/* check if this node is the root, set red color */
			if ( nodeId == NodeData.ROOT) {
				node.setFillColor(ColorLib.setAlpha(node.getInt(NetMonitor.COLOR), 255));
				node.setStrokeColor(ColorLib.setAlpha(0, 255));
				node.setTextColor(ColorLib.setAlpha(ColorLib.rgb(255, 255, 255), 255));				
			}
			
			/* if this node is the root we only remove edges to invisible nodes */
			if (( nodeData == null ) || ( nodeId == NodeData.ROOT ) || (nodeData.getAlert() > 0)) {
				// FIXME: remove edges to invisible nodes
				continue;
			}
			
			/* if we have a non root node, compute new alpha value from age */
			int originalColor = node.getInt(NetMonitor.COLOR); 
			int alpha = (int)((240.0 / NetMonitor.MAXAGE) * (NetMonitor.MAXAGE - nodeData.getAge())) + 15;
			
			/* set the new color */
			node.setFillColor(ColorLib.setAlpha(originalColor, alpha));
			node.setStrokeColor(ColorLib.setAlpha(0, alpha));
			node.setTextColor(ColorLib.setAlpha(0, alpha));

			/* if this node is older than maxage but younger than the deletion age, set him invisible
			 * remove all edge from and to him */
			if ( nodeData.getAge() > NetMonitor.MAXAGE ) {
				nodeData.removeNode(-1);				
			}
			
		}

	}

}
