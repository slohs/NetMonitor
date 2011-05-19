import java.util.Iterator;
import java.util.Map;

import prefuse.action.GroupAction;
import prefuse.data.Graph;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;


public class AlertAction extends GroupAction 
{

	private Map nodeMap;
	
	public AlertAction(String group, Map nodeMap) 
	{
		super(group);
		this.nodeMap = nodeMap;
	}
	
	@Override
	public void run(double frac) 
	{

		TupleSet ts = getVisualization().getGroup(this.m_group);
		
		for (Iterator iterator = ts.tuples(); iterator.hasNext();) {
			VisualItem	item = (VisualItem) iterator.next();
			
			if (!item.canGet(NetMonitor.NODEID, Integer.class))
				continue;
			
			int nodeId = item.getInt(NetMonitor.NODEID);
			NodeData nodeData = (NodeData) nodeMap.get(nodeId);
			
			if (( nodeData == null ) || ( nodeId == NodeData.ROOT )) {
				continue;
			}
			
			int originalColor = item.getInt(NetMonitor.COLOR); 
			
			if (nodeData.getAlert() > 0) {
					
				if ( (nodeData.getAlert() % 2) == 1 ) {
					item.setFillColor(originalColor);
				}
					
				else {
					item.setFillColor(ColorLib.rgb(255, 255, 255));
				}
				
				nodeData.setAlert(nodeData.getAlert() - 1);				
			}			
					
		}

	}

}
