package org.kazak.xpg.gui;

import java.io.IOException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.kazak.xpg.settings.ImageManager;
import org.kazak.xpg.settings.XmlLoader;

public class ToolBar extends JToolBar {

	private static final long serialVersionUID = 9041820105049631655L;
	
	 public ToolBar() {
		 try {
			Element root = XmlLoader.load("/resources/toolbar.xml");
			Iterator it = root.getChildren().iterator();
			while ( it.hasNext() ) {
				Element element = (Element) it.next();
				String name = element.getName();
				if ("separator".equals(name)) {
					this.add(new JToolBar.Separator());
				}
				else {
					String icon = element.getAttributeValue("icon");
					String _class = element.getAttributeValue("class");
					String _method = element.getAttributeValue("method");
					JButton button = new JButton(ImageManager.getImage(icon));
					button.addActionListener(new Action(_class,_method));
					this.add(button);
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	 }
}
