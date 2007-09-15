package org.kazak.xpg;

import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

import javax.swing.JOptionPane;

import org.kazak.xpg.gui.ConnectionDialog;
import org.kazak.xpg.gui.MainWindow;
import org.kazak.xpg.settings.ImageManager;
import org.kazak.xpg.settings.Language;

public class Run {
	
	private static MainWindow frame;
	
	public Run(String [] args) {
		new Language("es_CO");
		new ImageManager();
		frame = new MainWindow();
		new ConnectionDialog();
	}
	
	public Run() {}
	
	public static void main(String[] args) {
		new Run(args);
	}

	public static void exit() {		
		int OPTION = -1;	
		OPTION = JOptionPane.showConfirmDialog(
									frame,
									Language.getWord("REALLY-CLOSE"),
									Language.getWord("EXIT"),
									YES_NO_OPTION);
		
		if ( OPTION == YES_OPTION ) {
			System.exit(0);
		}
	}
}
