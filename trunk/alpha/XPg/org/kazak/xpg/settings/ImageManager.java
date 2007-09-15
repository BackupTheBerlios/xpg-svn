package org.kazak.xpg.settings;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.jdom.Element;
import org.jdom.JDOMException;

public class ImageManager {
	
	private static Hashtable<String, String> images;
	
	public ImageManager() {
		images = new Hashtable<String,String>();
		try {
			Element root = XmlLoader.load("/resources/icons.xml");
			Iterator it = root.getChildren().iterator();
			while( it.hasNext() ) {
				Element element = (Element) it.next();
				String key = element.getAttributeValue("key");
				String path = element.getAttributeValue("path");
				images.put(key,path);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ImageIcon getImage (String key) {
		ImageIcon imageIcon;
		String path = images.get(key);
		java.awt.Image image = Toolkit.getDefaultToolkit().createImage(path.getClass().getResource(path));
		imageIcon = new ImageIcon(image);
		return imageIcon;
	}
}
