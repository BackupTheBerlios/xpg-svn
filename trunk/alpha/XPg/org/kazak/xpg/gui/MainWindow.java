package org.kazak.xpg.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.kazak.xpg.MainClass;
import org.kazak.xpg.settings.Language;

public class MainWindow {
	
	private static JFrame frame;
	private Tree tree;
	
	public MainWindow() {
		
		frame = new JFrame(Language.getWord("WINDOW-TITLE"));
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				MainClass.exit();
			}
		});
		
		tree = new Tree();
		JSplitPane jsplitCentral = new JSplitPane();
		jsplitCentral.setOneTouchExpandable(true);
		jsplitCentral.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jsplitCentral.setRightComponent(new JPanel());
		
		JSplitPane jsplitSup = new JSplitPane();
		jsplitSup.setDividerLocation(170);
		jsplitSup.setOneTouchExpandable(true);
		jsplitSup.setLeftComponent(tree.getWithScroll());
		jsplitSup.setRightComponent(new JPanel());
		
		jsplitCentral.setLeftComponent(jsplitSup);
		
		frame.setLayout(new BorderLayout());
		frame.setJMenuBar(new Menu());
		frame.add(new ToolBar(),NORTH);
		frame.add(jsplitCentral,CENTER);
		frame.setVisible(true);
		
		/*try {
			Thread.sleep(5*1000);
			System.out.println("Sali del sleep");
		} catch (InterruptedException IEe) {
			IEe.printStackTrace();
		}
		tree.getTreenodeRoot().add(new DefaultMutableTreeNode("Test"));
		tree.getJtree().updateUI();*/
	}

	public static JFrame getFrame() {
		return frame;
	}
}
