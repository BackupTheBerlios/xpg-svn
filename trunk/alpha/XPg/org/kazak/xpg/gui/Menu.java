package org.kazak.xpg.gui;

import java.io.IOException;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.kazak.xpg.settings.Language;
import org.kazak.xpg.settings.XmlLoader;

public class Menu extends JMenuBar {
	
	private static final long serialVersionUID = -234811641655609540L;
	private JMenuBar menubar;
	
	public Menu() {
		
		menubar = this;
		Thread thread = new Thread () {
			public void run() {
		
				try {
					Element root = XmlLoader.load("/resources/menu.xml");
					Iterator i = root.getChildren().iterator();
						
					while( i.hasNext() ) {
						Element element = (Element) i.next();
						String name = element.getName();
						
						if ("jmenu".equals(name)) {
							String menutext = Language.getWord(element.getAttributeValue("name"));
							JMenu jmenu = new JMenu(menutext);
							menubar.add(jmenu);
							Iterator j = element.getChildren().iterator();
							while(j.hasNext()) {
								Element item = (Element) j.next();
								if ("item".equals(item.getName())) {
									
									String text = Language.getWord(item.getAttributeValue("name"));
									String _class = item.getAttributeValue("class");
									String _method = item.getAttributeValue("method");
									
									JMenuItem mitem = new JMenuItem(text);
									mitem.addActionListener(new Action(_class,_method));
									jmenu.add(mitem);
								}
								else if ("separator".equals(item.getName())) {
									jmenu.add(new JSeparator());
								}
							}
							
						}
					}
					updateUI();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
}
