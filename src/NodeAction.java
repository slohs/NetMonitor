import java.util.Iterator;
import java.util.Map;

import prefuse.action.GroupAction;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.NodeItem;


public class NodeAction extends GroupAction {

	private Window gui;
	
	public NodeAction(String group, Window gui) 
	{
		super(group);
		this.gui = gui;
	}
	
	@Override
	public void run(double frac) 
	{
		TupleSet ts = getVisualization().getGroup(this.m_group);
		
		for (Iterator iterator = ts.tuples(); iterator.hasNext();) {
			NodeItem node = (NodeItem) iterator.next();
			
			if (node.getBoolean(NetMonitor.CURRENT)) {
				node.setVisible(true);
			}
			
			else {
				node.setVisible(false);
			}
		}
	}

}
