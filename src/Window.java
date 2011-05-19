import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Window {
	
	public NodeTable nodeTable;
	public GraphDisplay graphDisplay;
	
	private Map nodeMap;
	
	public JCheckBox treeGraphBox;
	public JCheckBox neighborGraphBox;
	
	public JCheckBox normalMessagesBox;
	public JCheckBox stateMessageBox;
	public JCheckBox tempMessagesBox;
	public JCheckBox alertMessagesBox;
	public JCheckBox neighborBiMessagesBox;	
	
	public JSlider timelineSlider;
	public JButton timelinePauseButton;
	public JButton timelinePlayButton;
	public JLabel timelineLabel;
	public JCheckBox liveCheckBox;
	public JCheckBox recordCheckBox;
	
	public DataFileReader fileReader;
	
	private HistoryManagement history;
	
	public Window (Map nodeMap, DataFileReader fileReader, HistoryManagement history) 
	{
		this.nodeMap = nodeMap;
		this.fileReader = fileReader;
		this.history = history;
		
		JFrame frame = new JFrame("Net Monitor");
		frame.setSize(1000, 900);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		BorderLayout layout = new BorderLayout();
		frame.setLayout(layout);
		
		/** 
		 * HeadPanel
		 */
//		JPanel headPanel = new JPanel(new GridLayout(1, 3));
		JPanel headPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		headPanel.setSize(1000, 200);
		
		/**
		 * Message Types
		 */
		JPanel messageTypePanel = new JPanel(new GridLayout(2,1));
		messageTypePanel.setSize(200, 200);
		messageTypePanel.setBorder(BorderFactory.createTitledBorder("Message Types"));
		
		treeGraphBox = new JCheckBox("Tree Graph");
		treeGraphBox.setSelected(true);
		messageTypePanel.add(treeGraphBox);
		
		neighborGraphBox = new JCheckBox("Neighbor Graph");
		neighborGraphBox.setSelected(false);
		messageTypePanel.add(neighborGraphBox);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 0.2;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.ipady = 69;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		headPanel.add(messageTypePanel, gridBagConstraints);
		
		/**
		 * Graph Types
		 */		
		JPanel graphTypePanel = new JPanel(new GridLayout(5,1));
		graphTypePanel.setSize(200, 200);
		graphTypePanel.setBorder(BorderFactory.createTitledBorder("Graph Types"));
		
		normalMessagesBox = new JCheckBox("Normal Messages");
		normalMessagesBox.setSelected(true);
		graphTypePanel.add(normalMessagesBox);
		
		tempMessagesBox = new JCheckBox("Temp only Messages");
		tempMessagesBox.setSelected(true);
		tempMessagesBox.setEnabled(false);
		graphTypePanel.add(tempMessagesBox);
		
		stateMessageBox = new JCheckBox("State Messages");
		stateMessageBox.setSelected(true);
		graphTypePanel.add(stateMessageBox);
		
		alertMessagesBox = new JCheckBox("Alert Messages");
		alertMessagesBox.setSelected(false);
		alertMessagesBox.setEnabled(false);
		graphTypePanel.add(alertMessagesBox);
		
		neighborBiMessagesBox = new JCheckBox("Neighbor (bi) Messages");
		neighborBiMessagesBox.setSelected(true);
		graphTypePanel.add(neighborBiMessagesBox);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.ipady = 0;
		headPanel.add(graphTypePanel, gridBagConstraints);
		
		/**
		 * Node Table
		 */
		nodeTable = new NodeTable();		
		nodeTable.setOpaque(true);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.weightx = 0.6;
		gridBagConstraints.ipady = 20;
		headPanel.add(nodeTable, gridBagConstraints);
		
		frame.add(headPanel, layout.NORTH);

		/**
		 * Graph display
		 */
		graphDisplay = new GraphDisplay(fileReader, nodeMap, this);
		frame.add(graphDisplay, layout.CENTER);
		
		/**
		 * Timeline Bar
		 */
		JPanel sliderPanel = new JPanel();
		sliderPanel.setSize(1000, 100);
		sliderPanel.setBorder(BorderFactory.createTitledBorder("Timeline Bar"));
		sliderPanel.setLayout(new GridBagLayout());
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 0.80;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		timelineSlider = new JSlider(JSlider.HORIZONTAL, 0, 1, 1);
		timelineSlider.setMinorTickSpacing(1);
		timelineSlider.setMajorTickSpacing(10);
		timelineSlider.setPaintTicks(true);
		
//		timelineSlider.setPaintLabels(true);
		timelineSlider.setEnabled(false);
		sliderPanel.add(timelineSlider, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.weightx = 0.05;
		timelineLabel = new JLabel("1");
		timelineLabel.setHorizontalTextPosition(JLabel.CENTER);
		sliderPanel.add(timelineLabel, gridBagConstraints);
		
		final Window window =(Window)this;
		ChangeListener sliderListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (timelineSlider.getValueIsAdjusting() == false) {
					int val = timelineSlider.getValue();
					
					timelineLabel.setText("" + val);
					
					if (recordCheckBox.isSelected()) {
						timelineSlider.setValue( Window.this.history.goToState(val) );
					}
				}
				
			}
		};
		timelineSlider.addChangeListener(sliderListener);
		
		gridBagConstraints.gridx = 2;
		gridBagConstraints.weightx = 0.05;
		timelinePauseButton = new JButton("Pause");
		timelinePauseButton.setEnabled(false);
		sliderPanel.add(timelinePauseButton, gridBagConstraints);
		
		ActionListener pauseButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				timelinePauseButton.setEnabled(false);
				timelinePlayButton.setEnabled(true);
				
			}
		};
		
		timelinePauseButton.addActionListener(pauseButtonListener);
		
		gridBagConstraints.gridx = 3;
		timelinePlayButton = new JButton("Play");
		timelinePlayButton.setEnabled(false);
		sliderPanel.add(timelinePlayButton, gridBagConstraints);
		
		ActionListener playButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				timelinePauseButton.setEnabled(true);
				timelinePlayButton.setEnabled(false);
				
			}
		};
		
		timelinePlayButton.addActionListener(playButtonListener);
		
		gridBagConstraints.gridx = 4;
		JPanel liveSimulationPanel = new JPanel(new GridLayout(2,1));
		liveCheckBox = new JCheckBox("Live");
		liveCheckBox.setSelected(true);
		liveCheckBox.setEnabled(false);
		recordCheckBox = new JCheckBox("Record");
		recordCheckBox.setSelected(false);		
		liveSimulationPanel.add(liveCheckBox);
		liveSimulationPanel.add(recordCheckBox);
		
		ItemListener liveCheckBoxListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				JCheckBox checkBox = (JCheckBox)itemEvent.getSource();
				int state = itemEvent.getStateChange();
	        
				if (state == ItemEvent.SELECTED) {
					recordCheckBox.setSelected(false);
					
					timelinePauseButton.setEnabled(false);
					timelinePlayButton.setEnabled(false);
					timelineSlider.setEnabled(false);
					
					graphDisplay.vis.run(NetMonitor.LIVEACTION);
					graphDisplay.vis.run(NetMonitor.LAYOUTACTION);
					
					liveCheckBox.setEnabled(false);
					recordCheckBox.setEnabled(true);
				}
	        
			}
	    };	    
	    liveCheckBox.addItemListener(liveCheckBoxListener);

	    ItemListener recordCheckBoxListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				JCheckBox checkBox = (JCheckBox)itemEvent.getSource();
				int state = itemEvent.getStateChange();
	        
				if (state == ItemEvent.SELECTED) {
					liveCheckBox.setSelected(false);
					
					timelinePauseButton.setEnabled(false);
					timelinePlayButton.setEnabled(true);
					timelineSlider.setEnabled(true);
					
					liveCheckBox.setEnabled(true);
					recordCheckBox.setEnabled(false);

					graphDisplay.vis.cancel(NetMonitor.LIVEACTION);
				}
	        
			}
	    };
	    recordCheckBox.addItemListener(recordCheckBoxListener);
		
		sliderPanel.add(liveSimulationPanel, gridBagConstraints);
		
		frame.add(sliderPanel, layout.SOUTH);
		
	
		/**
		 *  general
		 */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.setVisible(true);
//		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		
	}
	
}
