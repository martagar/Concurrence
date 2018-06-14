/**
 * Automaton Cellular 1D's Graphics
 * @author Marta García Pérez
 */

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.String;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

public class ca1DGraphicSimulation extends JPanel {
	private static int nGen;
	private static int n_cells;

	static Automaton_Cel_1D a_cel;
	static Menu_1D menu = new Menu_1D();
	static JPanel panel_AM; //frame of the menu and the automaton
	static JFrame frame_graphics;

	public ca1DGraphicSimulation() {
		ini_AM();
		setSize(640,480);
		setLayout(new BorderLayout());
		add(panel_AM);
	}

  private static XYDataset make_data_Hamming() {
  	XYSeriesCollection set_data = new XYSeriesCollection();
  	XYSeries hamming = new XYSeries("Hamming");

  	for(int i = 0; i < ca1DSimulator.iter; ++i)
  		hamming.add(i,ca1DSimulator.curv_Hamming[i]);

  	set_data.addSeries(hamming);

  	return set_data;
  }

  private static JPanel make_Graphic_Hamming() {
  	XYDataset data = make_data_Hamming();

  	JFreeChart graph = ChartFactory.createXYLineChart("Hamming Curve", "Generations", "Cells", data);

  	return new ChartPanel(graph);
  }

  private static XYDataset make_data_Entropy() {
  	XYSeriesCollection set_data = new XYSeriesCollection();
  	XYSeries entropy = new XYSeries("Entropy");

  	for(int i = 0; i < ca1DSimulator.iter+1; ++i)
  		entropy.add(i,ca1DSimulator.space_entropy[i]);

  	set_data.addSeries(entropy);

  	return set_data;
  }

  private static JPanel make_Graphic_Entropy() {
  	XYDataset data = make_data_Entropy();

  	JFreeChart graph = ChartFactory.createXYLineChart("Space Entropy Curve", "Generations", "Entropy", data);

  	return new ChartPanel(graph);
  }

  public static void drawGraphics() {
  	frame_graphics = new JFrame();
  	frame_graphics.setSize(640,480);
  	frame_graphics.setLayout(new GridLayout(2,1));
  	JPanel graph_curvHamming = make_Graphic_Hamming();
  	JPanel graph_entropy = make_Graphic_Entropy();

  	frame_graphics.add(graph_curvHamming);
  	frame_graphics.add(graph_entropy);
  	frame_graphics.setVisible(true);
  }

	public void ini_AM() {
		panel_AM = new JPanel();
		panel_AM.setSize(640,480);
		panel_AM.setLayout(new BorderLayout());

		panel_AM.add(menu,BorderLayout.EAST);
		panel_AM.setVisible(true);
	}

	public static void addA_Cel(int n_cel, int gen) {
		n_cells = n_cel;
		nGen = gen;
		a_cel = new Automaton_Cel_1D(n_cel, gen+1);
		panel_AM.add(a_cel, BorderLayout.CENTER);
	}

	public static void restart() {
		ca1DSimulator.gen = 0;
		a_cel.setVisible(false);
	}

	public static void update(int gene[], int row) {
      for (int i = 0; i < n_cells; ++i) {
          Color c = new Color((gene[i]*25)%255, (gene[i]*50)%255, (gene[i]*75)%255);
          Automaton_Cel_1D.img.setRGB(i, row, c.getRGB());
      }
      a_cel.repaint();
  }

  public static int[] intr_manual() {
  	if(menu.txtCelMan.getText().equals(""))
  		return new int[0];
  	else {
  		int[] res = new int[menu.txtCelMan.getText().length()];
  		int i = 0;
  		int j = 0;
  		while(i < menu.txtCelMan.getText().length()) {
  			if(menu.txtCelMan.getText().charAt(i) != ',')	{
  				res[j] = Character.getNumericValue(menu.txtCelMan.getText().charAt(i));
  				j++;
  			}
  			i++;
  		}
  		return res;
  	}
  }

  public static int[] calc_rule() {
		int[] res = new int[8];
		if(menu.txtRule.getText().equals(""))
			return res;
		else {
			int r = Integer.parseInt(menu.txtRule.getText());
			for(int i = 0; i < 8; ++i)	{
				if(r == 0)
					res[i] = 0;
				else	{
					res[i] = r%2;
					r /= 2;
				}
			}
			return res;
		}
	}
}

class Automaton_Cel_1D extends JPanel {
	static BufferedImage img;

	public Automaton_Cel_1D(int n_cel, int nGen) {
		img = new BufferedImage(n_cel, nGen, BufferedImage.TYPE_INT_RGB);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img,0,0,getWidth(),getHeight(),this);
	}
}

class Menu_1D extends JPanel {
	private JLabel nCel = new JLabel("Number of Cells");
	JTextField txtCel = new JTextField();

	private JLabel num_gen = new JLabel("Number of Generations");
	JTextField txtGen = new JTextField();

	private JLabel nRule = new JLabel("Number of Rule");
	JTextField txtRule = new JTextField();

	private JLabel dim = new JLabel("Size");
	JTextField txtDim = new JTextField();

	private JLabel cel_man = new JLabel("Manual Introduction");
	JTextField txtCelMan = new JTextField();

	private JButton button = new JButton("Start");

	Menu_1D()	{
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(nCel);
		add(txtCel);
		add(num_gen);
		add(txtGen);
		add(nRule);
		add(txtRule);
		add(cel_man);
		add(txtCelMan);

		button.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				if(button.getText().equals("Start")) {
					button.setText("Restart");

					if(txtCelMan.getText().equals(""))
						ca1DSimulator.n_c = Integer.parseInt(txtCel.getText());
					else
						ca1DSimulator.n_c = txtCelMan.getText().length();

					ca1DSimulator.iter = Integer.parseInt(txtCel.getText());
					ca1DSimulator.k = 2;

					ca1DGraphicSimulation.addA_Cel(ca1DSimulator.n_c,ca1DSimulator.iter);

					ca1DSimulator.initialize(4,ca1DSimulator.n_c/2);

					ca1DGraphicSimulation.update(ca1DSimulator.timeT,ca1DSimulator.gen++);

					ca1DSimulator.make_automaton();
				}	else if(button.getText().equals("Restart")) {
						ca1DGraphicSimulation.restart();
						button.setText("Start");
					}
			}
		});
		add(button);
	}
}
