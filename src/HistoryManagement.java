import prefuse.visual.VisualItem;


public class HistoryManagement {

	private HistoryElement current;
	private int maxRounds;
	
	public HistoryManagement() {
		current = new HistoryElement(0, null);
	}
	
	
	public void addSet(VisualItem item, int round) {
		if ( round == -1 ) {
			round = current.getRound();
		}			
		
		if (current.getRound() == round) {
			current.addSet(item);
		}
		
		else if (current.getRound() < round) {
			HistoryElement newElem = new HistoryElement(round, current);
			current.setNext(newElem);
			current = newElem;
			
			current.addSet(item);
		}
	}
	
	public void addUnset(VisualItem item, int round) {
		if ( round == -1 ) {
			round = current.getRound();
		}
		
		if (current.getRound() == round) {
			current.addUnset(item);
		}
		
		else if (current.getRound() > round) {
			HistoryElement newElem = new HistoryElement(round, current);
			current = newElem;
			
			current.addUnset(item);
		}
	}
	
	public int goToState(int targetRound)
	{
		int roundsChanged = 0;
		
		System.out.println("history: " + current.getRound() + " -> " + targetRound);
		
		// go to future
		if (targetRound > current.getRound()) {
			
			while (( targetRound > current.getRound() ) && ( current.next() != null )) {
				HistoryElement next = current.next();
				next.redo();
				current = next;
				
				roundsChanged++;
			}			
			
			System.out.println("redo " + roundsChanged + " rounds");
			return current.getRound();
		}
		
		// go to past
		else if (( targetRound < current.getRound() ) && ( targetRound > 0 )) {
			
			while ( targetRound < current.getRound() ) {
				HistoryElement before = current.undo();
				
				if (before == null) {
					System.out.println("undo " + roundsChanged + " rounds");
					return current.getRound();
				}
				
				current = before;
				roundsChanged++;
			}
		
			System.out.println("undo " + roundsChanged + " rounds");
			return current.getRound();
		}
		
		return current.getRound();
		
	}
	
	
}
