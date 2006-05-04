package org.kazak.xpg;

import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

import javax.swing.JOptionPane;

import org.kazak.xpg.gui.MainWindow;
import org.kazak.xpg.settings.ImageManager;
import org.kazak.xpg.settings.Language;

public class MainClass {
	

	public MainClass(String [] args) {
		new Language("es_CO");
		new ImageManager();
		new MainWindow();
	}
	public MainClass() {}
	
	public static void main(String[] args) {
		new MainClass(args);
	}

	public static void exit() {
		
		int OPTION = -1;
		
		OPTION = JOptionPane.showConfirmDialog(
									MainWindow.getFrame(),
									Language.getWord("REALLY-CLOSE"),
									Language.getWord("EXIT"),
									YES_NO_OPTION);
		
		if ( OPTION == YES_OPTION ) {
			System.exit(0);
		}
	}
}
