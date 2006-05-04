package org.kazak.xpg.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Action implements ActionListener {
	
	private String _class;
	private String _method;
	
	
	public Action (String _class, String _method) {
		this._class = _class;
		this._method = _method;
	}
	
	public void actionPerformed(ActionEvent AEe) {
		try {
			
			System.out.println("=> " + this._class +"."+_method);
			Class cls = Class.forName(this._class);
			Constructor cons = cls.getConstructor();
            Object obj = cons.newInstance(new Object[]{});
			Method method = cls.getMethod(_method);
			method.invoke(obj);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
