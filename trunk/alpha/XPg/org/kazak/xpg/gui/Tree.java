package org.kazak.xpg.gui;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class Tree {
	
	private JTree jtree;
	private JScrollPane jscroll;
	private DefaultMutableTreeNode treenodeRoot;
	
	public Tree() {
		treenodeRoot = new DefaultMutableTreeNode("Raiz");
		jtree = new JTree(treenodeRoot);
		jscroll = new JScrollPane(jtree);
	}

	public JTree getJtree() {
		return jtree;
	}
	
	public JScrollPane getWithScroll() {
		return jscroll;
	}

	public DefaultMutableTreeNode getTreenodeRoot() {
		return treenodeRoot;
	}
}
