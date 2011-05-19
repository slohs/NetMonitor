import java.util.Iterator;
import java.util.Map;

import javax.swing.JCheckBox;

import prefuse.action.GroupAction;
import prefuse.data.expression.Predicate;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;


public class EdgeAction extends GroupAction 
{

	private Map nodeMap;
	private Window gui;
	
	public EdgeAction(String group, Map nodeMap, Window gui)
	{
		super(group);
		this.nodeMap = nodeMap;
		this.gui = gui;
	}
	
	@Override
	public void run(double frac) 
	{
		checkTreeEdgeType(NetMonitor.treeEdgePred, gui.normalMessagesBox);
		checkTreeEdgeType(NetMonitor.treeEdgeTempPred, gui.tempMessagesBox);
		checkTreeEdgeType(NetMonitor.treeEdgeAlarmPred, gui.alertMessagesBox);
		checkTreeEdgeType(NetMonitor.treeEdgeStatePred, gui.stateMessageBox);
		checkTreeEdgeType(NetMonitor.treeEdgeNeighborPred, gui.neighborBiMessagesBox);
		
		TupleSet ts = getVisualization().getVisualGroup(this.m_group);
		/**
		 * Neighbor edges from Neighbor-bi Messages
		 */		
		for (Iterator neighEdgeIterator = ts.tuples(NetMonitor.neighborEdgeBiPred); neighEdgeIterator.hasNext(); ) {
			EdgeItem edge = (EdgeItem) neighEdgeIterator.next();
			
			if ( gui.neighborGraphBox.isSelected() && gui.neighborBiMessagesBox.isSelected() ) {
				
				if ( ((NodeItem)edge.getSourceItem()).isVisible() && ((NodeItem)edge.getTargetItem()).isVisible() && edge.getBoolean(NetMonitor.CURRENT)) { 
					edge.setVisible(true);
				}
				
				else {
					edge.setVisible(false);
				}
				
			}
			
			else {
				edge.setVisible(false);
			}			
		}
		

	}

	private void checkTreeEdgeType(Predicate predicate, JCheckBox checkBox) 
	{
		TupleSet ts = getVisualization().getVisualGroup(this.m_group);
		
		for (Iterator treeEdgeIterator = ts.tuples(predicate); treeEdgeIterator.hasNext(); ) {
			EdgeItem edge = (EdgeItem) treeEdgeIterator.next();
			
			if ( gui.treeGraphBox.isSelected() && checkBox.isSelected() ) {
				
				if ( ((NodeItem)edge.getSourceItem()).isVisible() && ((NodeItem)edge.getTargetItem()).isVisible() && edge.getBoolean(NetMonitor.CURRENT) ) {
					edge.setVisible(true);
				}
				
				else {
					edge.setVisible(false);
				}
				
			}
			
			else {
				edge.setVisible(false);
			}			
		}
	}
	
}


