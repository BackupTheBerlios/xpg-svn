package org.kazak.xpg.settings;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.JDOMException;

public class Language {
	
	private static Hashtable<String,String> words;
	
	public Language(String locale) {
		words = new Hashtable<String,String>();
		try {
			Element element = XmlLoader.load("/resources/language.xml");
			Iterator it = element.getChildren().iterator();
			
			while (it.hasNext()) {
				Element word = (Element) it.next();
				words.put(word.getChildText("key"),word.getChildText(locale));
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getWord(String word) {
		String text = words.get(word);
		if (text!=null && !"".equals(text)) {
			return text;
		}
		return null;
	}
}
