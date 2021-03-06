import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.experimental.chart.plot.dial.DialBackground;
import org.jfree.experimental.chart.plot.dial.DialCap;
import org.jfree.experimental.chart.plot.dial.DialPlot;
import org.jfree.experimental.chart.plot.dial.DialPointer;
import org.jfree.experimental.chart.plot.dial.DialTextAnnotation;
import org.jfree.experimental.chart.plot.dial.DialValueIndicator;
import org.jfree.experimental.chart.plot.dial.SimpleDialFrame;
import org.jfree.experimental.chart.plot.dial.StandardDialRange;
import org.jfree.experimental.chart.plot.dial.StandardDialScale;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

public class TemperaturePanel extends JPanel implements ChangeListener
{
	
	private static final long serialVersionUID = 1L;
//	private JSlider slider;
	private DefaultValueDataset dataset;
	
	public TemperaturePanel (DefaultValueDataset dataset, int nodeId) {
		super (new GridLayout());

		DialPlot plot = new DialPlot();
		plot.setView(0.0, 0.0, 1.0, 1.0);
		plot.setDataset(dataset);

        SimpleDialFrame dialFrame = new SimpleDialFrame();
        dialFrame.setBackgroundPaint(Color.lightGray);
        dialFrame.setForegroundPaint(Color.darkGray);
        plot.setDialFrame(dialFrame);

        // blauer farbverlauf
        GradientPaint gp = new GradientPaint(new Point(), 
                new Color(255, 255, 255), new Point(), 
                new Color(170, 170, 220));
        DialBackground db = new DialBackground(gp);
        db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.VERTICAL));
        plot.setBackground(db);
        
        DialTextAnnotation annotation1 = new DialTextAnnotation("Temperature");
        annotation1.setFont(new Font("Dialog", Font.BOLD, 14));
        annotation1.setRadius(0.7);
        
        plot.addLayer(annotation1);

        DialValueIndicator dvi = new DialValueIndicator(0, "c");
        plot.addLayer(dvi);
        
        // adding the scale print
        StandardDialScale scale = new StandardDialScale(-20, 40, -120, -300);
        scale.setTickRadius(0.88);
        scale.setTickLabelOffset(0.15);
        scale.setTickLabelFont(new Font("Dialog", Font.PLAIN, 14));
        plot.addScale(0, scale);
        
        
        // range coloring
        StandardDialRange range = new StandardDialRange(30.0, 40.0, Color.red);
        range.setInnerRadius(0.52);
        range.setOuterRadius(0.55);
        plot.addLayer(range);
        
        StandardDialRange range2 = new StandardDialRange(20.0, 30.0, Color.orange);
        range2.setInnerRadius(0.52);
        range2.setOuterRadius(0.55);
        plot.addLayer(range2);

        StandardDialRange range3 = new StandardDialRange(0.0, 10.0, 
                Color.green);
        range3.setInnerRadius(0.52);
        range3.setOuterRadius(0.55);
        plot.addLayer(range3);
        
        StandardDialRange range4 = new StandardDialRange(10.0, 20.0, 
                Color.yellow);
        range4.setInnerRadius(0.52);
        range4.setOuterRadius(0.55);
        plot.addLayer(range4);
        
        StandardDialRange range5 = new StandardDialRange(-20.0, 0.0, 
                Color.blue);
        range5.setInnerRadius(0.52);
        range5.setOuterRadius(0.55);
        plot.addLayer(range5);

        DialPointer needle = new DialPointer.Pointer();
        plot.addLayer(needle);
        
        DialCap cap = new DialCap();
        cap.setRadius(0.10);
        plot.setCap(cap);
        
        JFreeChart chart1 = new JFreeChart(plot);
        chart1.setTitle("Temperature");
        ChartPanel cp1 = new ChartPanel(chart1);
        cp1.setPreferredSize(new Dimension(300, 300));
//        this.slider = new JSlider(-40, 60);
//        this.slider.setMajorTickSpacing(10);
//        this.slider.setPaintLabels(true);
//        this.slider.addChangeListener(this);
        
        add(cp1);
        
	}
	

	public void stateChanged(ChangeEvent e) 
	{
//		this.dataset.setValue(new Integer(this.slider.getValue()));
	}
	
}
