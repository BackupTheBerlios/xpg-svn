package org.kazak.xpg.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.kazak.xpg.Run;
import org.kazak.xpg.settings.Language;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private Tree tree;
    private static int MAX_WIN_SIZE_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static int MAX_WIN_SIZE_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	public MainWindow() {
		super(Language.getWord("WINDOW-TITLE"));
		setSize(800,600);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				Run.exit();
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
		
		setLayout(new BorderLayout());
		setJMenuBar(new Menu());
		add(new ToolBar(),NORTH);
		add(jsplitCentral,CENTER);
		
		setLookAndFeel("com.nilo.plaf.nimrod.NimRODLookAndFeel");
        setLocation(
                (MAX_WIN_SIZE_WIDTH / 2) - this.getWidth() / 2,
                (MAX_WIN_SIZE_HEIGHT / 2) - this.getHeight() / 2);
		
		setVisible(true);
	}
	
	private void setLookAndFeel(String look) {
		
		if (look!=null && !"".equals(look)) {
			System.out.println("* Loading lookAndFeel : " + look);
			try {
				UIManager.setLookAndFeel(look); 
			} catch (ClassNotFoundException e) {
				System.out.println("Error!!!");
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
        Font f = new Font("Tahoma", Font.PLAIN, 12);
        Font f2 = new Font("Tahoma",Font.BOLD,14);
        UIManager.put("Menu.font",                      f);
        UIManager.put("MenuItem.font",          f);
        UIManager.put("Button.font",            f);
        UIManager.put("Label.font",                     f);
        UIManager.put("TextField.font",         f);
        UIManager.put("ComboBox.font",          f);
        UIManager.put("CheckBox.font",          f);
        UIManager.put("TextPane.font",          f);
        UIManager.put("TextArea.font",          f);
        UIManager.put("List.font",                      f);
        UIManager.put("Slider.font",            f);
        UIManager.put("TitledBorder.font",      f2);
        UIManager.put("RadioButton.font",       f);
        UIManager.put("InternalFrame.font",     f2);
        UIManager.put("Table.font",                     f);
        UIManager.put("TabbedPane.font",        f);
        UIManager.put("DesktopColor.color",     Color.GRAY);
	}

	public static JFrame getFrame() {
		return new JFrame();
	}
}
