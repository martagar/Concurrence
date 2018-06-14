/**
 * Graphic Automaton
 * @author Marta García Pérez
 */

import javax.swing.*;
import java.awt.*;

public class GUI_automaton {
	JFrame frame = new JFrame();
	JTabbedPane windows = new JTabbedPane();
	ca2DGraphicSimulation Graph_2D = new ca2DGraphicSimulation();
	ca1DGraphicSimulation Graph_1D = new ca1DGraphicSimulation();
	belZabGraphic Graph_belZab = new belZabGraphic();

	GUI_automaton()	{
		frame.setLayout(new BorderLayout());
		frame.setSize(640,480);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);

		windows.addTab("Automaton 1D", Graph_1D);
		windows.addTab("Automaton 2D", Graph_2D);
		windows.addTab("Chemical Reaction", Graph_belZab);
		frame.add(windows);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new GUI_automaton();
	}
}
