/**
 * Interfaz Gráfica para Autómatas
 * @author Marta García Pérez
 */

import javax.swing.*;
import java.awt.*;

public class GUI_automata
{
	JFrame frame = new JFrame();
	JTabbedPane pantalla = new JTabbedPane();
	ca2DGraphicSimulation Graf_2D = new ca2DGraphicSimulation();
	ca1DGraphicSimulation Graf_1D = new ca1DGraphicSimulation();
	belZabGraphic Graf_belZab = new belZabGraphic();

	GUI_automata()
	{
		frame.setLayout(new BorderLayout());
		frame.setSize(640,480);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);

		pantalla.addTab("Automata 1D", Graf_1D);
		pantalla.addTab("Automata 2D", Graf_2D);
		pantalla.addTab("Reaccion Quimica", Graf_belZab);
		frame.add(pantalla);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new GUI_automata();
	}
}
