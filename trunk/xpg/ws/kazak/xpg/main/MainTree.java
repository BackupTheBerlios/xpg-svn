package ws.kazak.xpg.main;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;

public class MainTree implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private DefaultMutableTreeNode rootNode, leaf;
	private static JTree tree;
	private MainWindow mainWindow;
	private DefaultTreeModel treeModel;
	private JPopupMenu popup;
	private JPopupMenu popupDB;
	
	public MainTree(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		rootNode = new DefaultMutableTreeNode(Language.getWord("DSCNNTD"));
		leaf = new DefaultMutableTreeNode(Language.getWord("NODB"));
		rootNode.add(leaf);
		tree = new JTree(rootNode);
		setTreeLookAndFeel();
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.collapseRow(0);
		setPopupMenu();
		tree.addMouseListener(this);
	}
	
	public MainTree(MainWindow mainWindow, DefaultMutableTreeNode top, int index) {
		this.mainWindow = mainWindow;
		treeModel = new DefaultTreeModel(top);
		tree = new JTree(treeModel);
		setTreeLookAndFeel();
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.expandRow(0);
		tree.expandRow(index + 1);
		tree.setSelectionRow(index + 1);
		tree.addMouseListener(this);
	}
	
	public JTree getTree() {
		return tree;
	}
		
	private void setTreeLookAndFeel() {
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		URL imgURL = getClass().getResource("/icons/16_DB_Open.png");
		renderer.setOpenIcon(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		imgURL = getClass().getResource("/icons/16_DB.png");
		renderer.setClosedIcon(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		imgURL = getClass().getResource("/icons/16_table.png");
		renderer.setLeafIcon(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		tree.setCellRenderer(renderer);			
	}
	
	/**
	 * METODO HostTree Crea Arbol del Servidor
	 */
	public void setPopupMenu() {
		popup = new JPopupMenu();

		JMenuItem Item = new JMenuItem(Language.getWord("RNAME"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemRename");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("DUMP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemDump");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("EXPORTAB"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemExToFile");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("EXPORREP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemExport");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("DROP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemDelete");
		Item.addActionListener(this);
		popup.add(Item);

		popupDB = new JPopupMenu();

		Item = new JMenuItem(Language.getWord("DUMP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemPopDumpDB");
		Item.addActionListener(this);
		popupDB.add(Item);

		Item = new JMenuItem(Language.getWord("CLOSE"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemPopCloseDB");
		Item.addActionListener(this);
		popupDB.add(Item);

		Item = new JMenuItem(Language.getWord("DROP"));
		Item.setFont(new Font("Helvetica", Font.PLAIN, 10));
		Item.setActionCommand("ItemPopDeleteDB");
		Item.addActionListener(this);
		popupDB.add(Item);

	}
	
	private void renameItem() {
		TreePath selectedPath = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
		final String oldName = node.toString();
		tree.setEditable(true);
		final JTextField rename = new JTextField();
		tree.setCellEditor(new DefaultCellEditor(rename));
		tree.startEditingAtPath(selectedPath);
		rename.requestFocus();

		rename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String newName = rename.getText();

				if (newName.indexOf(" ") != -1) {

					rename.setText(oldName);
					JOptionPane.showMessageDialog(mainWindow, Language
							.getWord("NOCHART"),
							Language.getWord("ERROR!"),
							JOptionPane.ERROR_MESSAGE);

					return;
				}

				rename.setText(newName);
				String result = "";

				if (newName.length() == 0) {
					TreePath selectedPath = tree.getSelectionPath();
					tree.startEditingAtPath(selectedPath);
				} else {
					int index = MainWindow.getDBPosition(); 
					PGConnection connection = MainWindow.getConnectionAt(index);
					result = connection.executeSQL("ALTER TABLE \""
							+ oldName + "\" RENAME TO \"" + newName + "\"");

					if (result.equals("OK")) {
						MainWindow.setDBComponent(newName);
						String owner = connection.getOwner(newName);
						MainWindow.setLabels(newName,owner);
					} else {
						result = result.substring(0, result.length() - 1);
						rename.setText(oldName);
					}
				}
				MainWindow.addTextLogMonitor(Language.getWord("EXEC")
						+ "ALTER TABLE \"" + oldName + "\" RENAME TO \""
						+ newName + "\"\"");
				MainWindow.addTextLogMonitor(Language.getWord("RES") + result);
			}
		});
	}
	
	private void deleteItem() {
		GenericQuestionDialog killtb = new GenericQuestionDialog(mainWindow,
				Language.getWord("YES"), Language.getWord("NO"), Language
				.getWord("BOOLDELTB"), Language
				.getWord("MESGDELTB")
				+ MainWindow.getDBComponent() + "?");

		boolean sure = killtb.getSelecction();
		String result = "";

		if (sure) {
			int index = MainWindow.getDBPosition();
			PGConnection connection = MainWindow.getConnectionAt(index);
			result = connection.executeSQL("DROP TABLE \"" + MainWindow.getDBComponent() + "\"");

			if (result.equals("OK")) {

				TreePath selPath = tree.getSelectionPath();
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
						.getLastPathComponent());
				DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) currentNode.getParent();
				treeModel.removeNodeFromParent(currentNode);

				if (NodeDB.getChildCount() == 0) {
					DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(
							Language.getWord("NOTABLES"));
					NodeDB.add(nLeaf);
				}

			} else {
				result = result.substring(0, result.length() - 1);
			}

			MainWindow.addTextLogMonitor(Language.getWord("EXEC") + "DROP TABLE \"" 
					+ MainWindow.getDBComponent() + "\";\"");
			MainWindow.addTextLogMonitor(Language.getWord("RES") + result);
			MainWindow.setTabs();
		}
	}
	
	private void deleteFromPopUp() {

		String dbComponentName = MainWindow.getDBComponent();
		
		if (MainWindow.isSelectedMainConnection()) {

			JOptionPane.showMessageDialog(mainWindow, Language
					.getWord("INVOP"), Language.getWord("INFO"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		GenericQuestionDialog genericDialog = new GenericQuestionDialog(mainWindow,
				Language.getWord("YES"), Language.getWord("NO"), Language
				.getWord("BOOLDELTB"), Language
				.getWord("MESGDELDB")
				+ dbComponentName + "?");

		boolean sure = genericDialog.getSelecction();

		if (sure) {
			MainWindow.closeConnection();

			// Eliminando BD
			String result = MainWindow.getMainConnection().executeSQL("DROP DATABASE \""
					+ dbComponentName + "\"");
			MainWindow.addTextLogMonitor(Language.getWord("EXEC") + "DROP DATABASE \""
					+ dbComponentName + "\";\"");

			if (result.equals("OK")) {
				TreePath selPath = tree.getSelectionPath();
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
						.getLastPathComponent());
				treeModel.removeNodeFromParent(currentNode);
				MainWindow.addTextLogMonitor(Language.getWord("RES") + result);
			} else {
				String tmp = result.substring(0, result.length() - 1);
				MainWindow.addTextLogMonitor(Language.getWord("RES") + tmp);
				JOptionPane.showMessageDialog(mainWindow, Language.getWord("ERRORPOS")
						+ tmp, Language.getWord("ERROR!"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}


	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		
		if (action.equals("ItemRename")) {
			renameItem();
			return;
		}
		if (action.equals("ItemDelete")) {
			deleteItem();
			return;
		}
		if (action.equals("ItemPopDeleteDB")) {
			deleteFromPopUp();
			return;
		}
	}
	
	public static void expandTree(TreePath selectedPath) {
		if (!tree.isExpanded(selectedPath))
			tree.expandPath(selectedPath);
		else
			tree.collapsePath(selectedPath);
	}
	
	public boolean isTreeExpanded(TreePath path) {
		return tree.isExpanded(path);
	}
	
	public void setSelectionPath(TreePath path) {
		tree.setSelectionPath(path);
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		tree.requestFocus();
		String dbComponentName = MainWindow.getDBComponent();

		if (tree.isEditable())
			tree.setEditable(false);

		// Eventos de un Click con Boton Derecho
		if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {			
			TreePath selectedPath = tree.getClosestPathForLocation(e.getX(), e.getY());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

			if (node.isLeaf()
					&& !node.toString().startsWith(Language.getWord("NOTABLES"))) {

				if (!popup.isVisible()) {

					popup.show(tree, e.getX(), e.getY());

					if (!dbComponentName.equals(node.toString())) {
						//dbComponentName = node.toString();
						MainWindow.setDBComponent(node.toString());
						tree.setSelectionPath(selectedPath);
						mainWindow.activeFoldersTables(node);
					}
				}
			}

			if (!node.isRoot() && !node.isLeaf()) {

				if (!popupDB.isVisible()) {

					popupDB.show(tree, e.getX(), e.getY());

					if (!dbComponentName.equals(node.toString())) {

						//dbComponentName = node.toString();
						MainWindow.setDBComponent(node.toString());
						tree.setSelectionPath(selectedPath);
						MainWindow.activeFoldersDB(selectedPath);
					}
				}

			}
		}

		// Eventos de un Click con Boton Izquierdo

		if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
			TreePath selectedPath = tree.getClosestPathForLocation(e.getX(), e.getY());
			//DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
			//MainWindow.setDBComponent(node.toString());
			tree.setSelectionPath(selectedPath);
			mainWindow.updateUI(selectedPath);
		}
	}

	public void mouseReleased(MouseEvent e) {
		
	}
	
	public void setTreeModel(DefaultMutableTreeNode currentNode) {
		treeModel.nodeStructureChanged(currentNode);
	}
	
	public TreePath getSelectionPath() {
		return tree.getSelectionPath();
	}
	
	public TreePath getPathForLocation(int x, int y) {
		return tree.getClosestPathForLocation(x,y);
	}
}
