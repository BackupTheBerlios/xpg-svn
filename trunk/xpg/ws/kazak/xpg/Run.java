package ws.kazak.xpg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.main.MainWindow;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;

import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;

public class Run {
	public static void main(String arg[]) {

		final MainWindow program = new MainWindow();
		try {
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		program.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Listener to close application when user click on the X of the frame
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				GenericQuestionDialog killX = new GenericQuestionDialog(
						program, Language.getWord("OK"), Language
								.getWord("CANCEL"), Language
								.getWord("BOOLEXIT"), Language
								.getWord("QUESTEXIT"));

				boolean sure = killX.getSelecction();

				if (sure) {
					if (program.isConnected())
						program.closePGSockets();
					System.exit(0);
				} else {
					return;
				}
			}
		};

		program.addWindowListener(l);
		program.openConnectionDialog();
	}

}
