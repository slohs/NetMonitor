import java.util.Map;

import prefuse.Visualization;
import prefuse.action.Action;


public class ReadlineAction extends Action {

	private DataFileReader fileReader;
	private Visualization vis;
	
	public ReadlineAction(DataFileReader fileReader, Visualization vis) 
	{
		this.fileReader = fileReader;
		this.vis = vis;
	}
	
	@Override
	public void run(double frac) {
		fileReader.readline();
		vis.run("layout");
	}

}
