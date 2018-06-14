/**
 * Automaton Cellular 2D's Graphics
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
import java.util.concurrent.*;

public class ca2DGraphicSimulation extends JPanel {
	private static int n_cells;

	static Automaton_Cel_2D a_cel;
	static Menu_2D menu = new Menu_2D();
	static Cells cells;
	static JPanel panel_AM; //panel for the menu and the automaton
	static JFrame frame_CV; //frame for the living cells graphic

	public ca2DGraphicSimulation() {
		ini_AM();
		setSize(640,480);
		setLayout(new BorderLayout());
		add(panel_AM);
	}

  private static XYDataset make_data() {
  	XYSeriesCollection data_set = new XYSeriesCollection();
  	XYSeries alive = new XYSeries("Alive");
  	XYSeries dead = new XYSeries("Dead");

  	for(int i = 0; i < ca2DSimulator.iter; ++i)	{
  		alive.add(i,ca2DSimulator.living_cells[i]);
  		dead.add(i,ca2DSimulator.n_c*ca2DSimulator.n_c - ca2DSimulator.living_cells[i]);
  	}

  	data_set.addSeries(alive);
  	data_set.addSeries(dead);

  	return data_set;
  }

  private static JPanel make_Graphics() {
  	XYDataset data = make_data();

  	JFreeChart graph = ChartFactory.createXYLineChart("Medicion cells", "Generaciones", "Numero de Cells", data);

  	return new ChartPanel(graph);
  }

  public static void drawGraphic() {
  	frame_CV = new JFrame();
  	frame_CV.setSize(640,480);
  	frame_CV.setLayout(new BorderLayout());
  	JPanel graphics = make_Graphics();

  	frame_CV.add(graphics);
  	frame_CV.setVisible(true);
  }

	public void ini_AM() {
		panel_AM = new JPanel();
		panel_AM.setSize(640,480);
		panel_AM.setLayout(new BorderLayout());

		panel_AM.add(menu,BorderLayout.EAST);
		panel_AM.setVisible(true);
	}

	public static void addA_Cel(int n_cel) {
		n_cells = n_cel;
		a_cel = new Automaton_Cel_2D(n_cel);
		panel_AM.add(a_cel, BorderLayout.CENTER);
		cells = new Cells();
		panel_AM.add(cells, BorderLayout.SOUTH);
	}

	public static void restart() {
		a_cel.setVisible(false);
		cells.setVisible(false);
		frame_CV.setVisible(false);
	}

	public static void update(int gene[][]) {
  	for(int i = 0; i < gene.length; ++i)
      for (int j = 0; j < n_cells; ++j)
      {
          Color c = new Color((gene[i][j]*25)%255, (gene[i][j]*50)%255, (gene[i][j]*75)%255);
          Automaton_Cel_2D.img.setRGB(j, i, c.getRGB());
      }
    a_cel.repaint();
  }
}

class Cells extends JPanel {
	static JLabel cells = new JLabel("Living Cells: \nDead Cells: ");

	Cells()	{
		add(cells);
	}

	public static void update()	{
		cells.setText("Living Cells: " + ca2DSimulator.living_cel.get() + " Dead Cells: " + (ca2DSimulator.n_c*ca2DSimulator.n_c-ca2DSimulator.living_cel.get()));
	}
}

class Automaton_Cel_2D extends JPanel {
	static BufferedImage img;

	public Automaton_Cel_2D(int n_cel) {
		img = new BufferedImage(n_cel, n_cel, BufferedImage.TYPE_INT_RGB);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img,0,0,getWidth(),getHeight(),this);
	}
}

class Menu_2D extends JPanel {
	private JLabel nCel = new JLabel("Number of Cells");
	JTextField txtCel = new JTextField();

	private JLabel num_gen = new JLabel("Number of Generations");
	JTextField txtGen = new JTextField();

	private JLabel Neighbourhood = new JLabel("Neighbourhood (Newmann/Moore)");
	JButton butNeigh = new JButton("Newmann");

	private JButton button = new JButton("Start");

	Menu_2D() {
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(Neighbourhood);

		butNeigh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				if(butNeigh.getText().equals("Newmann"))
					butNeigh.setText("Moore");
				else
					butNeigh.setText("Newmann");
			}
		});

		add(butNeigh);
		add(nCel);
		add(txtCel);
		add(num_gen);
		add(txtGen);

		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				if(button.getText().equals("Start")) {
					button.setText("Restart");

					ca2DSimulator.n_c = Integer.parseInt(txtCel.getText());

					if(ca2DSimulator.n_c >= 200) {
						ca2DSimulator.iter = Integer.parseInt(txtCel.getText());
						ca2DSimulator.neighbourhood = (butNeigh.getText().equals("Newmann")? 1 : 0);

						ca2DGraphicSimulation.addA_Cel(ca2DSimulator.n_c);

						ca2DSimulator.initialize();

						ca2DGraphicSimulation.update(ca2DSimulator.timeT);

						ca2DSimulator.make_automaton();
					}
				}	else if(button.getText().equals("Restart"))	{
						ca2DGraphicSimulation.restart();
						button.setText("Start");
					}
			}
		});
		add(button);
	}
}
