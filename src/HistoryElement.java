import java.util.ArrayList;

import prefuse.visual.VisualItem;


public class HistoryElement {

	private int round;
	
	private ArrayList<VisualItem> setItems;
	private ArrayList<VisualItem> unsetItems;
	
	private HistoryElement next;
	private HistoryElement before;
	
	public HistoryElement(int round, HistoryElement before) 
	{
		this.round = round;
		this.before = before;
		this.next = null;
		
		setItems = new ArrayList<VisualItem>();
		unsetItems = new ArrayList<VisualItem>();
	}
	
	public void addSet(VisualItem item)
	{
		setItems.add(item);
	}
	
	public void addUnset(VisualItem item)
	{
		unsetItems.add(item);
	}
		
	public HistoryElement undo()
	{
		for (int i = 0; i < setItems.size(); i++) {
			((VisualItem)setItems.get(i)).setBoolean(NetMonitor.CURRENT, false);
		}
		
		for (int i = 0; i < unsetItems.size(); i++) {
			((VisualItem)unsetItems.get(i)).setBoolean(NetMonitor.CURRENT, true);
		}
		
		return before;
	}
	
	public HistoryElement redo()
	{
		for (int i = 0; i < setItems.size(); i++) {
			((VisualItem)setItems.get(i)).setBoolean(NetMonitor.CURRENT, true);
		}
		
		for (int i = 0; i < unsetItems.size(); i++) {
			((VisualItem)unsetItems.get(i)).setBoolean(NetMonitor.CURRENT, false);
		}
	
		return next;		
	}
	
	public HistoryElement next()
	{
		return this.next;
	}
	
	public void setNext(HistoryElement next)
	{
		this.next = next;
	}
	
	public int getRound()
	{
		return round;
	}
	
}
