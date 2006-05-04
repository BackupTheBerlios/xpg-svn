package org.kazak.xpg.settings;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XmlLoader {
	
	public static Element load(String url) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder(false);
		Document document = builder.build(url.getClass().getResource(url));
		return document.getRootElement();
	}
}
