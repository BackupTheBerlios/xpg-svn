/**
 * Disponible en http://www.kazak.ws
 *
 * Desarrollado por Soluciones KAZAK 
 * Grupo de Investigacion y Desarrollo de Software Libre
 * Santiago de Cali/Republica de Colombia 2001
 *
 * CLASS XPg v 0.1                                                   
 * Descripcion:
 * Esta clase es la principal de la aplicacion. Se encarga de iniciar 
 * la Interfaz Grafica, manejar los eventos de rat�n y teclado para la interfaz 
 * e instanciar las clases necesarias para realizar los diversas funciones 
 * relacionadas con bases de datos.
 *
 * Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
 *                                                                   
 * Fecha: 2001/10/01                                                 
 *
 * Autores: Beatriz Flori�n  - bettyflor@kazak.ws                    
 *          Gustavo Gonzalez - xtingray@kazak.ws                     
 *          Angela Sandobal  - angesand@libertad.univalle.edu.co     
 */
package ws.kazak.xpg.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
/*import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener; */
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Vector;

//import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
//import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
//import javax.swing.JTextField;
import javax.swing.JToolBar;
//import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
//import javax.swing.tree.TreeSelectionModel;

import ws.kazak.xpg.db.ConnectionInfo;
import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.db.TableFieldRecord;
import ws.kazak.xpg.db.TableHeader;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.menu.AlterGroup;
import ws.kazak.xpg.menu.AlterUser;
import ws.kazak.xpg.menu.CreateDB;
import ws.kazak.xpg.menu.CreateGroup;
import ws.kazak.xpg.menu.CreateTable;
import ws.kazak.xpg.menu.CreateUser;
import ws.kazak.xpg.menu.DropDB;
import ws.kazak.xpg.menu.DropGroup;
import ws.kazak.xpg.menu.DropTable;
import ws.kazak.xpg.menu.DropUser;
import ws.kazak.xpg.menu.DumpDb;
import ws.kazak.xpg.menu.DumpTable;
import ws.kazak.xpg.menu.TablesGrant;
import ws.kazak.xpg.misc.file.BuildConfigFile;
import ws.kazak.xpg.misc.file.ConfigFileReader;
import ws.kazak.xpg.misc.file.ExtensionFilter;
import ws.kazak.xpg.misc.help.About;
import ws.kazak.xpg.misc.input.LanguageChooser;
import ws.kazak.xpg.misc.input.ChooseIdiomButton;
import ws.kazak.xpg.misc.input.ErrorDialog;
import ws.kazak.xpg.misc.input.ExportSeparatorField;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;
import ws.kazak.xpg.misc.input.UpdateDBTree;
import ws.kazak.xpg.queries.HotQueries;
import ws.kazak.xpg.queries.Queries;
import ws.kazak.xpg.queries.SQLFuncBasic;
import ws.kazak.xpg.queries.SQLFunctionDataStruc;
import ws.kazak.xpg.records.Records;
import ws.kazak.xpg.report.ReportDesigner;
import ws.kazak.xpg.structure.Structures;
import ws.kazak.xpg.utilities.Path;

//import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
//import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;

public class MainWindow extends JFrame implements ActionListener, SwingConstants,
		FocusListener, KeyListener {

	private static final long serialVersionUID = 1L;
	JFrame program;
	private static MainMenu menuBar; // Barra de menus desplegables
	JToolBar iconBar; // Barra de iconos
	JPanel Global; // Panel de contenido
	JSplitPane splitPane; // Panel redimensionable grande entre el monitor de logs
						// y la interfaz

	private static JTextArea LogWin; // Monitor de Eventos
	DefaultTreeModel treeModel;
	DefaultMutableTreeNode top, category1, globalLeaf; // Nodos del Arbol de
														// conexi�n
	//JTree tree; // HostTree de la estructura de la conexi�n a PostgreSQL
	private static MainTree mainTree;
	private static JTabbedPane tabbedPane; // Carpetas donde se desplegar� la informaci�n
							// de tables, datos y queries

	private static PGConnection mainConnection; // Objeto de la clase PGConnection que maneja la
							// conexion
	ConnectionInfo online; // Objeto de la clase ConnectionInfo que define la
							// estructura de datos de la conexion
	LanguageChooser language;
	boolean connected = false;
	JSplitPane topPanel; // Panel redimensionable entre el arbol de bases de
							// datos y las carpetas
	JScrollPane treeView;
	DefaultTreeCellRenderer renderer;
	JButton connect, disconnect; // iconos de conexion
	JButton newDB, dropDB; // icons de bases de datos
	JButton newTable, dropTable, dumpTable, changeIdiom; // iconos de tablas
	JMenu connection, dataBase, tables, query, admin, help, group, sub_user; // Menues
																			// desplegables
	JMenuItem connectItem, disconnectItem, exitItem, newDBItem, dropDBItem,
			sub_permi; // items de los Menues connection y dataBase

	JMenuItem newTableItem, dropTableItem, dumpTableItem, addFieldItem; // items
																		// del
																		// JMenu
																		// tables

	JMenuItem editFieldItem, dropFieldItem, insertRecordItem, updateRecordItem,
			delRecordItem;// items del JMenu tables

	JMenuItem newGroupItem, alterGroupItem, dropGroupItem;

	JMenuItem newUserItem, alterUserItem, dropUserItem;

	JMenuItem grantItem, revokeItem;

	JMenuItem newQryItem, saveQryItem, openQryItem, hqItem, runQryItem,
			helpItem, aboutItem; // items de los JMenu query y help

	Language idiom = new Language();

	ConnectionWatcher guard;

	private static String currentDataBase = "";

	String[] permissions;

	String xlanguage = "";

	private static String dbComponentName = "";

	private static Vector<PGConnection> connectionsVector = new Vector<PGConnection>();

	private static Vector<String> dbNamesVector = new Vector<String>();

	Vector<String> evaluatedDB = new Vector<String>();

	private static Structures structuresPanel;

	private static Records recordsPanel;

	private static Queries queriesPanel;

	int activatedTab = 2;

	private static int OldCompType = -1;

	int numOldTables = 0;

	private static int DBComponentType = -1;

	Table currentTable;

	Vector<String> indexesVector;

	JPopupMenu popup, popupDB;

	String startDate = "";

	String OS = "";

	String configPath = "xpg.cfg";

	String xpgHome = "";

	boolean networkLink = false;	
    private static int MAX_WIN_SIZE_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static int MAX_WIN_SIZE_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();


	/**
	 * METODO CONSTRUCTOR public XPg() Arma y ensambla los elementos de la
	 * interfaz gr�fica
	 */
	public MainWindow() {

		super("XPg - PostgreSQL GUI"); // Llama al m�todo de la clase padre
										// (Frame) que espera como par�metro
										// una cadena

		JPanel pe = new JPanel();
		pe.setLayout(new BorderLayout());
		URL gURL = getClass().getResource("/icons/xpg.png");
		JLabel labelx = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(gURL)), JLabel.CENTER);
		pe.add(labelx, BorderLayout.CENTER);

		JWindow first = new JWindow(MainWindow.this);
		first.getContentPane().add(pe);
		first.setSize(216, 237);
		//first.setLocation(232, 220);
		first.setLocation(
                (MAX_WIN_SIZE_WIDTH / 2) - first.getWidth() / 2,
                (MAX_WIN_SIZE_HEIGHT / 2) - first.getHeight() / 2);
		first.setVisible(true);

		ConfigFileReader readLang = new ConfigFileReader();
		OS = System.getProperty("os.name");

		if (OS.equals("Linux") || OS.equals("Solaris") || OS.equals("FreeBSD")) {

			String UHome = System.getProperty("user.home")
					+ System.getProperty("file.separator") + ".xpg";
			File file = new File(UHome);
			configPath = UHome + System.getProperty("file.separator")
					+ "xpg.cfg";

			if (!file.exists()) {

				file.mkdir();
				file = new File(UHome + System.getProperty("file.separator")
						+ "logs");
				file.mkdir();
				file = new File(UHome + System.getProperty("file.separator")
						+ "queries");
				file.mkdir();
				file = new File(UHome + System.getProperty("file.separator")
						+ "reports");
				file.mkdir();
				readLang.Create_File(configPath);
			} else
				readLang = new ConfigFileReader(configPath, 1);
		}

		if (OS.startsWith("Windows")) {
			System.setProperty("xpgHome", Path.getPathxpg());
			xpgHome = System.getProperty("xpgHome");
			String home = xpgHome + System.getProperty("file.separator");
			configPath = home + "xpg.cfg";
			File file = new File(configPath);

			if (!file.exists())
				readLang.Create_File(configPath);
			else
				readLang = new ConfigFileReader(configPath, 1);

			file = new File(home + System.getProperty("file.separator")
					+ "logs");

			if (!file.exists())
				file.mkdir();

			file = new File(home + System.getProperty("file.separator")
					+ "queries");

			if (!file.exists())
				file.mkdir();

			file = new File(home + System.getProperty("file.separator")
					+ "reports");

			if (!file.exists())
				file.mkdir();
		}

		// Leer del archivo de configuraci�n el idioma actual
		xlanguage = readLang.getIdiom();

		// Si actualmente no se guarda ning�n idioma
		// mostrar ventana inicial para escoger idioma
		if (xlanguage.equals("none")) {

			language = new LanguageChooser(MainWindow.this);
			language.pack();
			language.setLocationRelativeTo(MainWindow.this);
			language.setVisible(true);
			xlanguage = language.getIdiom();
			idiom.CargarLenguaje(xlanguage);
			writeFile(xlanguage);
		} else
			idiom.CargarLenguaje(xlanguage);

		getContentPane().setLayout(new BorderLayout()); // Parte el FRAME
		menuBar = new MainMenu(this);
		setJMenuBar(menuBar); // A�adiendo el menu desplegable al Frame
		
		iconBar = new JToolBar(SwingConstants.HORIZONTAL); // Se crea un nuevo
															// objeto barra de
															// iconos
		iconBar.setFloatable(false); // Se hace la barra de iconos fija
		Global = new JPanel();
		CreateToolBar(); // Se definen los componentes de los iconos de la
							// ventana principal
		Folders(); // Crea los Folders a desplegar
				
		// Crear un scroll pane y a�ade el arbol a este
		mainTree = new MainTree(this);
		treeView = new JScrollPane(mainTree.getTree());

		// Crear el split pane a�adiendo a la izq. el arbol y a la derecha las
		// pesta�as
		topPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topPanel.setLeftComponent(treeView);
		topPanel.setRightComponent(tabbedPane);
		topPanel.setOneTouchExpandable(true); // el SplitPane muestra
												// controles que permiten al
		// usuario ocultar uno de los componentes y asignar todo el espacio al
		// otro
		// Dar el tama�o m�nimo para los dos componentes del split pane
		treeView.setMinimumSize(new Dimension(100, 400));
		treeView.setPreferredSize(new Dimension(200, 400));
		tabbedPane.setMinimumSize(new Dimension(400, 640));
		tabbedPane.setEnabled(false); // Deshabilitar las carpetas

		topPanel.setDividerLocation(135); // Selecciona u obtiene la
											// posici�n actual del divisor.
		topPanel.setPreferredSize(new Dimension(200, 400)); // Tama�o
																// preferido
																// para el split
																// pane

		// Armar el monitor de eventos
		JButton upTitle = new JButton(Language.getWord("LOGMON"));
		upTitle.setToolTipText(Language.getWord("PRESSCL"));

		upTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int pi = splitPane.getDividerLocation();

				if (pi == 430)
					splitPane.setDividerLocation(0);
				else
					splitPane.setDividerLocation(430);
			}
		});

		JPanel downP = new JPanel();
		downP.setLayout(new BorderLayout());

		LogWin.setEditable(false);
		JScrollPane winCover = new JScrollPane(LogWin);
		downP.add(upTitle, BorderLayout.NORTH);
		downP.add(winCover, BorderLayout.CENTER);

		// A�ade al panel Global al norte la barra de iconos y en el centro el
		// split
		Global.setLayout(new BorderLayout());
		Global.add(iconBar, BorderLayout.NORTH);

		topPanel.setMinimumSize(new Dimension(0, 0));
		downP.setMinimumSize(new Dimension(0, 25));

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true); // el SplitPane muestra controles
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(downP);
		splitPane.setDividerLocation(430);
		Global.add(splitPane, BorderLayout.CENTER);

		setBackground(Color.lightGray);// define el color de fondo para el
										// Frame
		getContentPane().add("Center", Global);// A�ade al Frame ppal el
												// panel Global
		pack();
		setSize(680, 600); // Tama�o inicial del Frame
        setLocation(
                (MAX_WIN_SIZE_WIDTH / 2) - this.getWidth() / 2,
                (MAX_WIN_SIZE_HEIGHT / 2) - this.getHeight() / 2);
		setVisible(true); // mostrar el Frame
	    
		first.setVisible(false);

	} // Fin constructor


	/**
	 * METODO CreateToolBar Crea Barra de Iconos
	 */
	public void CreateToolBar() {

		JToolBar.Separator line1 = new JToolBar.Separator();
		JToolBar.Separator line2 = new JToolBar.Separator();
		JToolBar.Separator line3 = new JToolBar.Separator();
		/*------------ Botones Conexion ------------*/
		URL imgURL = getClass().getResource("/icons/16_connect.png");
		connect = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		connect.setActionCommand("ButtonConnect");
		connect.addActionListener(this);
		connect.setToolTipText(Language.getWord("CONNE2"));
		iconBar.add(connect);

		imgURL = getClass().getResource("/icons/16_disconnect.png");
		disconnect = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		disconnect.setActionCommand("ButtonDisconnect");
		disconnect.addActionListener(this);
		disconnect.setToolTipText(Language.getWord("DISCON"));
		iconBar.add(disconnect);
		disconnect.setEnabled(false);

		iconBar.add(line1);
		/*------------ Botones Base de Datos ------------*/
		imgURL = getClass().getResource("/icons/16_NewDB.png");
		newDB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				imgURL)));
		newDB.setActionCommand("ButtonNewDB");
		newDB.addActionListener(this);
		newDB.setToolTipText(Language.getWord("NEWDB"));
		iconBar.add(newDB);
		newDB.setEnabled(false);

		imgURL = getClass().getResource("/icons/16_DropDB.png");
		dropDB = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		dropDB.setActionCommand("ButtonDropDB");
		dropDB.addActionListener(this);
		dropDB.setToolTipText(Language.getWord("DROPDB"));
		iconBar.add(dropDB);
		dropDB.setEnabled(false);

		iconBar.add(line2);
		/*------------ Botones Tabla ------------*/
		imgURL = getClass().getResource("/icons/16_NewTable.png");
		newTable = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		newTable.setActionCommand("ButtonNewTable");
		newTable.addActionListener(this);
		newTable.setToolTipText(Language.getWord("NEWT"));
		iconBar.add(newTable);
		newTable.setEnabled(false);

		imgURL = getClass().getResource("/icons/16_DropTable.png");
		dropTable = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		dropTable.setActionCommand("ButtonDropTable");
		dropTable.addActionListener(this);
		dropTable.setToolTipText(Language.getWord("DROPT"));
		iconBar.add(dropTable);
		dropTable.setEnabled(false);

		imgURL = getClass().getResource("/icons/16_Dump.png");
		dumpTable = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		dumpTable.setActionCommand("ButtonDumpTable");
		dumpTable.addActionListener(this);
		dumpTable.setToolTipText(Language.getWord("DUMPT"));
		iconBar.add(dumpTable);
		dumpTable.setEnabled(false);

		iconBar.add(line3);

		/*------------ Botones Lenguaje ------------*/
		imgURL = getClass().getResource("/icons/16_Language.png");
		changeIdiom = new JButton(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL)));
		changeIdiom.setActionCommand("ButtonChangeLanguage");
		changeIdiom.addActionListener(this);
		changeIdiom.setToolTipText(Language.getWord("CHANGE_L"));
		iconBar.add(changeIdiom);

	}

	/**
	 * METODO actionPerformed Manejador de Eventos para la barra de botones y el
	 * menu desplegable
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
/*
		if (e.getActionCommand().equals("ItemRename")) {

			TreePath selPath = tree.getSelectionPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
					.getLastPathComponent();
			final String oldName = node.toString();
			tree.setEditable(true);
			final JTextField rename = new JTextField();
			tree.setCellEditor(new DefaultCellEditor(rename));
			tree.startEditingAtPath(selPath);
			rename.requestFocus();

			rename.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String newName = rename.getText();

					if (newName.indexOf(" ") != -1) {

						rename.setText(oldName);
						JOptionPane.showMessageDialog(MainWindow.this, Language
								.getWord("NOCHART"),
								Language.getWord("ERROR!"),
								JOptionPane.ERROR_MESSAGE);

						return;
					}

					rename.setText(newName);
					String result = "";

					if (newName.length() == 0) {

						TreePath selPath = tree.getSelectionPath();
						tree.startEditingAtPath(selPath);
					} else {
						int index = dbNamesVector.indexOf(currentDataBase);
						PGConnection konn = (PGConnection) connectionsVector
								.elementAt(index);
						result = konn.SQL_Instruction("ALTER TABLE \""
								+ oldName + "\" RENAME TO \"" + newName + "\"");

						if (result.equals("OK")) {

							dbComponentName = newName;
							String owner = konn.getOwner(newName);
							structuresPanel.setLabel(currentDataBase, newName,
									owner);
							recordsPanel.setLabel(currentDataBase, newName, owner);
						} else {
							result = result.substring(0, result.length() - 1);
							rename.setText(oldName);
						}
					}
					addTextLogMonitor(Language.getWord("EXEC")
							+ "ALTER TABLE \"" + oldName + "\" RENAME TO \""
							+ newName + "\"\"");
					addTextLogMonitor(Language.getWord("RES") + result);
				}
			});

			return;
		}

		if (e.getActionCommand().equals("ItemDelete")) {

			GenericQuestionDialog killtb = new GenericQuestionDialog(MainWindow.this,
					Language.getWord("YES"), Language.getWord("NO"), Language
							.getWord("BOOLDELTB"), Language
							.getWord("MESGDELTB")
							+ dbComponentName + "?");

			boolean sure = killtb.getSelecction();
			String result = "";

			if (sure) {

				int index = dbNamesVector.indexOf(currentDataBase);
				PGConnection konn = (PGConnection) connectionsVector.elementAt(index);
				result = konn.SQL_Instruction("DROP TABLE \"" + dbComponentName
						+ "\"");

				if (result.equals("OK")) {

					TreePath selPath = tree.getSelectionPath();
					DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
							.getLastPathComponent());
					DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) currentNode
							.getParent();
					treeModel.removeNodeFromParent(currentNode);

					if (NodeDB.getChildCount() == 0) {
						DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(
								Language.getWord("NOTABLES"));
						NodeDB.add(nLeaf);
					}

				} else {
					result = result.substring(0, result.length() - 1);
				}

				addTextLogMonitor(Language.getWord("EXEC") + "DROP TABLE \""
						+ dbComponentName + "\";\"");
				addTextLogMonitor(Language.getWord("RES") + result);
				tabbedPane.setSelectedIndex(2);
				tabbedPane.setEnabledAt(0, false);
				tabbedPane.setEnabledAt(1, false);
			}

			return;
		} */

		if (e.getActionCommand().equals("ItemDump")) {

			String s = "file:" + System.getProperty("user.home");
			File file;
			boolean Rewrite = true;
			String FileName = "";
			JFileChooser fc = new JFileChooser(s);
			ExtensionFilter filter = new ExtensionFilter("sql", Language
					.getWord("SQLF"));
			fc.addChoosableFileFilter(filter);

			int returnVal = fc.showDialog(MainWindow.this, Language.getWord("SAVE"));

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				file = fc.getSelectedFile();
				FileName = file.getAbsolutePath();

				if (file.exists()) {

					GenericQuestionDialog win = new GenericQuestionDialog(
							MainWindow.this, Language.getWord("YES"), Language
									.getWord("NO"), Language.getWord("ADV"),
							Language.getWord("FILE") + " '" + FileName + "'"
									+ Language.getWord("SEQEXIS2") + " "
									+ Language.getWord("OVWR"));
					Rewrite = win.getSelecction();
				}

				if (Rewrite) {

					try {
						int index = dbNamesVector.indexOf(currentDataBase);
						PGConnection konn = (PGConnection) connectionsVector
								.elementAt(index);
						String dataStr = createTableSQL(konn
								.getSpecStrucTable(dbComponentName));

						if (!FileName.endsWith(".sql"))
							FileName += ".sql";

						PrintStream sqlFile = new PrintStream(
								new FileOutputStream(FileName));
						sqlFile.print(dataStr);
						sqlFile.close();
					} catch (Exception ex) {
						System.out.println("Error: " + ex);
						ex.printStackTrace();
					}
				}
			}
			return;
		}

		if (e.getActionCommand().equals("ItemExport")) {

			int index = dbNamesVector.indexOf(currentDataBase);
			PGConnection konn = (PGConnection) connectionsVector.elementAt(index);
			int regs = Count(dbComponentName, konn);
			addTextLogMonitor(Language.getWord("EXEC")
					+ "SELECT count(*) FROM " + dbComponentName + "\"");
			addTextLogMonitor(Language.getWord("NUMR") + dbComponentName
					+ "' : " + regs);

			if (regs > 100) {

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("LOTREG")
						+ dbComponentName + Language.getWord("LOTREG2"),
						Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);

				return;
			}

			Vector result = konn.getResultSet("SELECT * FROM " + dbComponentName);
			Vector colnames = konn.getTableHeader();
			String res = "";

			if (!konn.queryFail())
				res = "OK";
			else {
				res = konn.problem;
				res = res.substring(0, res.length() - 1);
			}

			addTextLogMonitor(Language.getWord("EXEC") + "SELECT * FROM "
					+ dbComponentName + "\"");
			addTextLogMonitor(Language.getWord("RES") + res);
			//ReportDesigner format = 
			new ReportDesigner(MainWindow.this, colnames,
					result, LogWin, dbComponentName, konn);

			return;
		}

		if (e.getActionCommand().equals("ItemExToFile")) {

			ExportSeparatorField little = new ExportSeparatorField(MainWindow.this);

			if (little.isDone()) {

				String limiter = little.getLimiter();
				String s = "file:" + System.getProperty("user.home");
				File file;
				boolean Rewrite = true;
				String FileName = "";
				JFileChooser fc = new JFileChooser(s);
				int returnVal = fc.showDialog(MainWindow.this, Language
						.getWord("EXPTO"));

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					file = fc.getSelectedFile();
					FileName = file.getAbsolutePath();

					if (file.exists()) {

						GenericQuestionDialog win = new GenericQuestionDialog(
								MainWindow.this, Language.getWord("YES"), Language
										.getWord("NO"),
								Language.getWord("ADV"), Language
										.getWord("FILE")
										+ " '"
										+ FileName
										+ "'"
										+ Language.getWord("SEQEXIS2")
										+ " "
										+ Language.getWord("OVWR"));
						Rewrite = win.getSelecction();
					}

					if (Rewrite) {

						try {
							int index = dbNamesVector.indexOf(currentDataBase);
							PGConnection connection = (PGConnection) connectionsVector
									.elementAt(index);

							Vector result = connection
									.getResultSet("SELECT * FROM "
											+ dbComponentName);
							Vector colnames = connection.getTableHeader();
							String resultString = "OK";

							if (connection.queryFail()) {
								resultString = connection.getProblemString();
								resultString = resultString.substring(0,
										resultString.length() - 1);
							}

							addTextLogMonitor(Language.getWord("EXEC")
									+ "SELECT * FROM " + dbComponentName + "\"");
							addTextLogMonitor(Language.getWord("RES")
									+ resultString);
							Table structT = connection
									.getSpecStrucTable(dbComponentName);
							TableHeader tableHeader = structT.getTableHeader();
							PrintStream exportFile = new PrintStream(
									new FileOutputStream(FileName));
							printFile(exportFile, result, colnames, limiter,
									tableHeader);
						} catch (Exception ex) {
							System.out.println("Error: " + ex);
							ex.printStackTrace();
						}
					}

				}
			}

			return;
		}

		if (e.getActionCommand().equals("ItemPopCloseDB")) {

			if (dbComponentName.equals(mainConnection.getDBname())) {

				GenericQuestionDialog killtb = new GenericQuestionDialog(
						MainWindow.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLDISC"),
						Language.getWord("WDIS"));

				boolean sure = killtb.getSelecction();

				if (sure)
					Disconnect();

				return;
			}

			int pos = dbNamesVector.indexOf(dbComponentName);
			PGConnection pgTmp = (PGConnection) connectionsVector.remove(pos);
			pgTmp.close();
			dbNamesVector.remove(pos);
			addTextLogMonitor(Language.getWord("CLSDB") + dbComponentName + "'");
			Object raiz = treeModel.getRoot();
			int k = treeModel.getChildCount(raiz);

			for (int i = 0; i < k; i++) {

				Object o = treeModel.getChild(raiz, i);

				if (dbComponentName.equals(o.toString())) {
					treeModel.removeNodeFromParent((MutableTreeNode) o);
					break;
				}
			}

			return;
		}

		/*
		if (e.getActionCommand().equals("ItemPopDeleteDB")) {

			if (dbComponentName.equals(mainConnection.getDBname())) {

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("INVOP"), Language.getWord("INFO"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			GenericQuestionDialog killtb = new GenericQuestionDialog(MainWindow.this,
					Language.getWord("YES"), Language.getWord("NO"), Language
							.getWord("BOOLDELTB"), Language
							.getWord("MESGDELDB")
							+ dbComponentName + "?");

			boolean sure = killtb.getSelecction();

			if (sure) {

				int pos = dbNamesVector.indexOf(dbComponentName);
				PGConnection tempo = (PGConnection) connectionsVector.remove(pos);
				tempo.close();
				dbNamesVector.remove(pos);
				// Eliminando BD
				String result = mainConnection.SQL_Instruction("DROP DATABASE \""
						+ dbComponentName + "\"");
				addTextLogMonitor(Language.getWord("EXEC") + "DROP DATABASE \""
						+ dbComponentName + "\";\"");

				if (result.equals("OK")) {

					TreePath selPath = tree.getSelectionPath();
					DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
							.getLastPathComponent());
					//DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) currentNode
					//		.getParent();
					treeModel.removeNodeFromParent(currentNode);
					addTextLogMonitor(Language.getWord("RES") + result);
				} else {
					String tmp = result.substring(0, result.length() - 1);
					addTextLogMonitor(Language.getWord("RES") + tmp);
					JOptionPane.showMessageDialog(MainWindow.this, Language
							.getWord("ERRORPOS")
							+ tmp, Language.getWord("ERROR!"),
							JOptionPane.ERROR_MESSAGE);
				}

			}

			return;
		}
		*/

		if (e.getActionCommand().equals("ItemPopDumpDB")) {

			int index = dbNamesVector.indexOf(currentDataBase);
			PGConnection dbDump = (PGConnection) connectionsVector.elementAt(index);

			int numTables = dbDump.getNumTables();

			if (numTables > 0) {

				DumpDb proto = new DumpDb(MainWindow.this, currentDataBase, dbDump);
				/*
				 * proto.pack(); proto.setLocationRelativeTo(XPg.this);
				 * proto.setVisible(true);
				 */

				if (proto.wellDone)
					addTextLogMonitor(Language.getWord("DUMPT1")
							+ proto.getTables() + Language.getWord("DUMPT2")
							+ proto.getDBName() + Language.getWord("DUMPT3")
							+ proto.getFile() + "'");

			} else {

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("TNTAB")
						+ currentDataBase + "'", Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			}

			return;
		}

		/*------------ Evento Conexion --------------*/
		if (e.getActionCommand().equals("ItemConnect")
				|| e.getActionCommand().equals("ButtonConnect")) {
			openConnectionDialog();
			return;
		}

		/*------------ Evento Desconectar --------------*/
		if (e.getActionCommand().equals("ItemDisconnect")
				|| e.getActionCommand().equals("ButtonDisconnect")) {

			Disconnect();
			return;
		}

		/*------------ Evento Salir --------------*/
		if (e.getActionCommand().equals("ItemExit")) {

			if (connected) {

				GenericQuestionDialog exitWin = new GenericQuestionDialog(
						MainWindow.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLEXIT"),
						Language.getWord("MESGEXIT"));

				boolean sure = exitWin.getSelecction();

				if (sure) {
					SaveLog();
					closePGSockets();
					System.exit(0);
				}
				return;
			} else
				System.exit(0);

		}

		/*------------ Evento Base de Datos ------------*/
		if (e.getActionCommand().equals("ItemCreateDB")
				|| e.getActionCommand().equals("ButtonNewDB")) {

			CreateDB newDB = new CreateDB(MainWindow.this, mainConnection, LogWin);

			if (newDB.isDone()) {

				ConnectionInfo tmp = new ConnectionInfo(online.getHost(), newDB
						.getDBname(), online.getUser(), online.getPassword(),
						online.getPort(), online.requireSSL());
				PGConnection proofConn = new PGConnection(tmp);

				if (!proofConn.Fail()) {

					dbNamesVector.add(newDB.getDBname());
					connectionsVector.add(proofConn);

					// Insertando nueva base de datos en el arbol
					DefaultMutableTreeNode dbLeaf = new DefaultMutableTreeNode(
							tmp.getDatabase());
					DefaultMutableTreeNode noTables = new DefaultMutableTreeNode(
							Language.getWord("NOTABLES"));
					dbLeaf.add(noTables);
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeModel
							.getRoot();
					treeModel.insertNodeInto(dbLeaf, parent, parent
							.getChildCount());

					JOptionPane.showMessageDialog(MainWindow.this, Language
							.getWord("OKCREATEDB1")
							+ tmp.getDatabase()
							+ "\" \n"
							+ Language.getWord("OKCREATEDB2"), Language
							.getWord("OK"), JOptionPane.INFORMATION_MESSAGE);
				} else {
					String msg = Language.getWord("OKCREATEDB1")
							+ tmp.getDatabase() + "\" "
							+ Language.getWord("OKCREATEDB2") + "\n"
							+ Language.getWord("NNACESS") + "\n"
							+ Language.getWord("NNCONTACT");
					JOptionPane.showMessageDialog(MainWindow.this, msg, Language
							.getWord("INFO"), JOptionPane.INFORMATION_MESSAGE);
				}
			}

			return;
		}

		if (e.getActionCommand().equals("ItemDropDB")
				|| e.getActionCommand().equals("ButtonDropDB")) {

			// Formando el vector de las bases de datos de las cuales el usuario
			// es propietario
			String sqlCmmd = "SELECT datname FROM pg_database WHERE datname != 'template1' AND datname != 'template0' AND datname != '"
					+ online.getDatabase() + " ORDER by datname';";
			Vector DBNames = mainConnection.getResultSet(sqlCmmd);
			addTextLogMonitor(Language.getWord("EXEC") + sqlCmmd);

			if (DBNames.size() == 0) {

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("NDBS"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);

				return;
			}

			if (DBNames.size() == 1) {

				Vector o = (Vector) DBNames.elementAt(0);
				String db = (String) o.elementAt(0);

				if (db.equals(online.getDatabase())) {

					JOptionPane.showMessageDialog(MainWindow.this, Language
							.getWord("OIDBC"), Language.getWord("INFO"),
							JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			}

			DropDB dropDB = new DropDB(MainWindow.this, DBNames);

			// Si el usuario presiono el boton DROP

			if (dropDB.confirmDropDB()) {

				int pos = 0;
				boolean inTree = false;

				if (dbNamesVector.contains(dropDB.comboText)) {

					inTree = true;
					pos = dbNamesVector.indexOf(dropDB.comboText);
				}

				// Cerrar la conexion de la base de datos a borrar si existe
				if (inTree) {

					PGConnection pgTmp = (PGConnection) connectionsVector.remove(pos);
					pgTmp.close();
					dbNamesVector.remove(pos);
				}

				// Eliminando BD
				String result = mainConnection.executeSQL("DROP DATABASE \""
						+ dropDB.comboText + "\"");
				addTextLogMonitor(Language.getWord("EXEC") + "DROP DATABASE "
						+ dropDB.comboText + "\";\"");

				if (result.equals("OK")) {

					if (inTree) {

						Object raiz = treeModel.getRoot();
						int k = treeModel.getChildCount(raiz);

						for (int i = 0; i < k; i++) {

							Object o = treeModel.getChild(raiz, i);

							if (dropDB.comboText.equals(o.toString())) {
								treeModel
										.removeNodeFromParent((MutableTreeNode) o);
								break;
							}
						}
					}

					addTextLogMonitor(Language.getWord("RES") + result);

					JOptionPane.showMessageDialog(MainWindow.this, Language
							.getWord("OKDROPDB1")
							+ dropDB.comboText + Language.getWord("OKDROPDB2"),
							Language.getWord("OK"),
							JOptionPane.INFORMATION_MESSAGE);

				} else {
					String tmp = result.substring(0, result.length() - 1);
					addTextLogMonitor(Language.getWord("RES") + tmp);
					JOptionPane.showMessageDialog(MainWindow.this, Language.getWord("ERRORPOS")
							+ tmp, Language.getWord("ERROR!"), JOptionPane.ERROR_MESSAGE);
				}
			}

			return;
		}

		/*------------ Evento Tabla ------------*/
		if (e.getActionCommand().equals("ItemCreateTable")
				|| e.getActionCommand().equals("ButtonNewTable")) {

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			CreateTable winTable = new CreateTable(MainWindow.this, dbNamesVector, connectionsVector,
					currentDataBase, LogWin);

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			if (winTable.getWellDone()) {

				Object raiz = treeModel.getRoot();
				int k = treeModel.getChildCount(raiz);

				for (int i = 0; i < k; i++) {

					Object o = treeModel.getChild(raiz, i);

					if (winTable.dbn.equals(o.toString())) {

						Object[] list = { raiz, o };
						TreePath path = new TreePath(list);

						if (mainTree.isTreeExpanded(path)) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
							DefaultMutableTreeNode leaf = (DefaultMutableTreeNode) node.getFirstChild();

							if (leaf.toString().startsWith(
								Language.getWord("NOTABLES"))) {
								treeModel.removeNodeFromParent((MutableTreeNode) leaf);
							}

							DefaultMutableTreeNode TLeaf = new DefaultMutableTreeNode(winTable.CurrentTable);
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) o;
							treeModel.insertNodeInto(TLeaf, parent, parent.getChildCount());
							//tree.expandPath(path);
							MainTree.expandTree(path);
						}
					}
				}
			}

			return;
		}

		if (e.getActionCommand().equals("ItemDropTable")
				|| e.getActionCommand().equals("ButtonDropTable")) {

			DropTable tableDropper = new DropTable(MainWindow.this, dbNamesVector, connectionsVector, LogWin);

			Vector deletedTablesVector = tableDropper.getDeletedTables();
			int size = deletedTablesVector.size();
			int count = 0;

			while (!deletedTablesVector.isEmpty()) {

				count++;
				String tableTarget = (String) deletedTablesVector.remove(0);
				Object root = treeModel.getRoot();
				int k = treeModel.getChildCount(root);

				for (int i = 0; i < k; i++) {

					Object o = treeModel.getChild(root, i);

					if (tableDropper.dbx.equals(o.toString())) {

						Object[] nodeList = { root, o };
						TreePath branch = new TreePath(nodeList);

						if (mainTree.isTreeExpanded(branch)) {

							int p = treeModel.getChildCount(o);

							for (int j = 0; j < p; j++) {

								Object t = treeModel.getChild(o, j);

								if (tableTarget.equals(t.toString())) {

									//TreePath selPath = tree.getSelectionPath();
									//DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
										//	.getLastPathComponent();

									if (count == size) {
										structuresPanel.setNullTable();
										structuresPanel.setLabel("", "", "");
										recordsPanel.setLabel("", "", "");
										structuresPanel.activeToolBar(false);
										structuresPanel.activeIndexPanel(false);
										tabbedPane.setEnabledAt(0, false);
										tabbedPane.setEnabledAt(1, false);
										tabbedPane.setSelectedIndex(2);
										mainTree.setSelectionPath(branch);
									}

									treeModel
											.removeNodeFromParent((MutableTreeNode) t);
									DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) (branch
											.getLastPathComponent());

									if (NodeDB.getChildCount() == 0) {

										DefaultMutableTreeNode nLeaf = new DefaultMutableTreeNode(
												Language.getWord("NOTABLES"));
										NodeDB.add(nLeaf);
									}
									break;
								}
							}
						}
					}
				}// fin for
			} // fin while delTables.isEmpty()

			return;
		}

		if (e.getActionCommand().equals("ItemDumpTable")
				|| e.getActionCommand().equals("ButtonDumpTable")) {

			DumpTable proto = new DumpTable(MainWindow.this, dbNamesVector, connectionsVector);
			/*
			 * proto.pack(); proto.setLocationRelativeTo(XPg.this);
			 * proto.setVisible(true);
			 */

			if (proto.isDone())
				addTextLogMonitor(Language.getWord("DUMPT1")
						+ proto.getTables() + Language.getWord("DUMPT2")
						+ proto.getDBName() + Language.getWord("DUMPT3")
						+ proto.getFile() + "'");

			return;

		}

		/*---------- Evento Consulta --------*/
		if (e.getActionCommand().equals("ItemCreateQry")) {

			int carpeta = tabbedPane.getSelectedIndex();

			if (carpeta != 2) {

				tabbedPane.setSelectedIndex(2);
				queriesPanel.NewQuery();
			}

			return;
		}

		if (e.getActionCommand().equals("ItemOpenQry")) {

			int carpeta = tabbedPane.getSelectedIndex();

			if (carpeta != 2)
				tabbedPane.setSelectedIndex(2);

			queriesPanel.LoadQuery();

			return;
		}

		if (e.getActionCommand().equals("ItemHQ")) {

			HotQueries hotQ = new HotQueries(MainWindow.this);

			if (hotQ.isWellDone())
				queriesPanel.loadSQL(hotQ.getSQL(), hotQ.isReady());

			return;
		}

		/* ---------------Evento Admin -------------- */
		if (e.getActionCommand().equals("ItemCreateUser")) {

			//CreateUser cUser = 
			new CreateUser(MainWindow.this, mainConnection, LogWin);
			/*
			 * cUser.pack(); cUser.setLocationRelativeTo(XPg.this);
			 * cUser.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemAlterUser")) {

			//AlterUser aUser = 
			new AlterUser(MainWindow.this, mainConnection, LogWin);
			/*
			 * aUser.pack(); aUser.setLocationRelativeTo(XPg.this);
			 * aUser.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemDropUser")) {

			//DropUser dUser = 
			new DropUser(MainWindow.this, mainConnection, LogWin);
			/*
			 * dUser.pack(); dUser.setLocationRelativeTo(XPg.this);
			 * dUser.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemCreateGroup")) {

			//CreateGroup cGroup = 
			new CreateGroup(MainWindow.this, mainConnection, LogWin);
			/*
			 * cGroup.pack(); cGroup.setLocationRelativeTo(XPg.this);
			 * cGroup.show();
			 */

			return;
		}

		if (e.getActionCommand().equals("ItemAlterGroup")) {

			String as[] = mainConnection.getGroups();

			if (as.length == 0) {

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("NGRPS"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			} else {

				//AlterGroup aGroup = 
				new AlterGroup(MainWindow.this, mainConnection, LogWin);
				/*
				 * aGroup.pack(); aGroup.setLocationRelativeTo(XPg.this);
				 * aGroup.show();
				 */
			}

			return;
		}

		if (e.getActionCommand().equals("ItemDropGroup")) {

			String as[] = mainConnection.getGroups();

			if (as.length == 0) {

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("NGRPS"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				//DropGroup dGroup = 
				new DropGroup(MainWindow.this, mainConnection, LogWin);
			}

			return;
		}

		if (e.getActionCommand().equals("ItemGrant")) {

			int index = dbNamesVector.indexOf(currentDataBase);

			if (index == -1) {

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("PCDBF"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);

				return;
			}

			PGConnection konn = (PGConnection) connectionsVector.elementAt(index);

			String[] tb;

			if (permissions[1].equals("false"))
				tb = konn.getTablesNames(true);
			else
				tb = konn.getTablesNames(false);

			if ((tb.length < 1) && (!permissions[1].equals("true")))

				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("NOTOW")
						+ currentDataBase + "'", Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
			else {
				if (tb.length > 0) {
					//TablesGrant perm = 
					new TablesGrant(MainWindow.this, konn, LogWin,tb);
				} else {
					JOptionPane.showMessageDialog(MainWindow.this, Language
							.getWord("NODBC")
							+ currentDataBase + "\"!", Language.getWord("INFO"),
							JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			}

			return;
		}

		/*------------ Evento Ayuda ------------*/
		if (e.getActionCommand().equals("ItemContenido")) {

			/*
			 * xpgHelp hlp = new xpgHelp(xpgHome); hlp.pack();
			 * hlp.setLocationRelativeTo(XPg.this); hlp.setVisible(true);
			 */

			JOptionPane.showMessageDialog(MainWindow.this, Language.getWord("UIMO"),
					Language.getWord("INFO"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (e.getActionCommand().equals("ItemAbout")) {

			About dialog = new About(MainWindow.this);
			dialog.setVisible(true);

			return;
		}

		/*------------ Evento Lenguaje ------------*/
		if (e.getActionCommand().equals("ButtonChangeLanguage")) {

			ChooseIdiomButton language = new ChooseIdiomButton(MainWindow.this, LogWin);
			/*
			 * language.pack(); language.setLocationRelativeTo(XPg.this);
			 * language.show();
			 */

			if (language.getSave()) {

				xlanguage = language.getIdiom();
				JOptionPane.showMessageDialog(MainWindow.this, Language
						.getWord("NEXT_TIME"), Language.getWord("INFO"),
						JOptionPane.INFORMATION_MESSAGE);
				writeFile(xlanguage);
			}

			return;
		}

	}

	/**
	 * METODO HostTree Crea Arbol del Servidor
	 *
	public void HostTree() {

		top = new DefaultMutableTreeNode(Language.getWord("DSCNNTD"));
		category1 = new DefaultMutableTreeNode(Language.getWord("NODB"));
		top.add(category1);

		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(renderer);
		tree.collapseRow(0);

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

	} */

	/**
	 * METODO Folders Crea Pesta�as
	 */
	public void Folders() {

		URL imgURL = getClass().getResource("/icons/16_table.png");
		ImageIcon iconTable = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL));
		imgURL = getClass().getResource("/icons/16_Datas.png");
		ImageIcon iconRecord = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL));
		imgURL = getClass().getResource("/icons/16_SQL.png");
		ImageIcon iconQuery = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(imgURL));
		tabbedPane = new JTabbedPane();

		LogWin = new JTextArea(5, 0);

		structuresPanel = new Structures(MainWindow.this, LogWin);
		tabbedPane.addTab(Language.getWord("TABLE"), iconTable,
				structuresPanel, Language.getWord("TABLE"));

		recordsPanel = new Records(MainWindow.this, LogWin);
		tabbedPane.addTab(Language.getWord("RECS"), iconRecord, recordsPanel,
				Language.getWord("RECS"));

		SQLFunctionDataStruc[] fList = SQLFuncBasic.setSQLFunctionsArray();
		SQLFuncBasic[] functions = SQLFuncBasic.setSQLHelpArray();
		queriesPanel = new Queries(MainWindow.this, LogWin, fList, functions);

		tabbedPane.addTab(Language.getWord("QUERYS"), iconQuery, queriesPanel,
				Language.getWord("QUERYS"));
	}

	/**
	 * METODO ReConfigFile Re-escribe el archivo de configuracion
	 */
	public void ReConfigFile(ConnectionInfo online, Vector ListRegs,
			int lastUsed) {

		ConnectionInfo tmp = (ConnectionInfo) ListRegs.elementAt(lastUsed);
		boolean noNew = true;
		int pos = lastUsed;

		if (!tmp.getHost().equals(online.getHost())
				|| !tmp.getUser().equals(online.getUser())
				|| !tmp.getDatabase().equals(online.getDatabase())) {

			noNew = false;

			for (int i = 0; i < ListRegs.size(); i++) {

				if (i != lastUsed) {

					ConnectionInfo element = (ConnectionInfo) ListRegs
							.elementAt(i);

					if (element.getHost().equals(online.getHost())
							&& element.getUser().equals(online.getUser())
							&& element.getDatabase().equals(
									online.getDatabase())) {

						noNew = true;
						pos = i;
						break;
					}
				}
			}
		}

		if (noNew) {
			new BuildConfigFile(ListRegs, pos, xlanguage);
		} else {
			new BuildConfigFile(ListRegs, online, xlanguage);
		}
	}

	/**
	 * METODO activeToolBar
	 */
	public void activeToolBar(boolean state) {
		disconnect.setEnabled(state);

		if (permissions[0].equals("true") && state) {
			newDB.setEnabled(true);
			dropDB.setEnabled(true);
			menuBar.setDatabaseItem(true);
		} else {
			newDB.setEnabled(false);
			dropDB.setEnabled(false);
			menuBar.setDatabaseItem(false);
		}
		newTable.setEnabled(state);
		dropTable.setEnabled(state);
		dumpTable.setEnabled(state);

		menuBar.setMenuStates(state);		
	}

	/**
	 * Metodo addTextLogMonitor Imprime mensajes en el Monitor de Eventos
	 */
	public static void addTextLogMonitor(String msg) {

		LogWin.append(msg + "\n");
		int longiT = LogWin.getDocument().getLength();

		if (longiT > 0)
			LogWin.setCaretPosition(longiT - 1);
	}

	/**
	 * METODO delDBRegs Borra una base de datos
	 * 
	 * public void delDBReg(String deathDb){
	 *  }
	 * 
	 */

	/**
	 * METODO connectionLost Informa que la conexion se ha perdido
	 */
	public void connectionLost(String PSQLserver) {

		InterfaceOffLine();
		JOptionPane.showMessageDialog(MainWindow.this, Language.getWord("DOWSO")
				+ PSQLserver + Language.getWord("DOWSO2"), Language
				.getWord("ERROR!"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * METODO InterfaceOffLine
	 * 
	 */
	public void InterfaceOffLine() {

		if (networkLink) {
			guard.goOut();
		}

		//HostTree();
		
		mainTree = new MainTree(this);
		treeView = new JScrollPane(mainTree.getTree());
		treeView.setMinimumSize(new Dimension(100, 400));
		treeView.setPreferredSize(new Dimension(200, 400));

		topPanel.setLeftComponent(treeView);
		setTitle("XPg - PostgreSQL GUI");
		addTextLogMonitor(Language.getWord("DISSOF") + mainConnection.getHostname()
				+ "\n");

		connected = false;
		menuBar.switchJMenus(false);
		connect.setEnabled(true);
		connectItem.setEnabled(true);
		disconnectItem.setEnabled(false);
		activeToolBar(false);
		tabbedPane.setEnabled(false);
	}

	/** Maneja el evento de tecla presionada * */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * METODO keyPressed Maneja los eventos de teclas presionadas en el teclado
	 */
	public void keyPressed(KeyEvent e) {

		int keyCode = e.getKeyCode();
		String keySelected = KeyEvent.getKeyText(keyCode); // cadena que
															// describe la tecla
															// f�sica
															// presionada

		if (keySelected.equals("Delete")) { // si la tecla presionada es delete

			if (DBComponentType == 0) { // Presion� sobre la raiz del arbol de
										// conexi�n, el Servidor

				GenericQuestionDialog killcon = new GenericQuestionDialog(
						MainWindow.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLDISC"),
						Language.getWord("MESGDISC") + dbComponentName + "?");

				boolean sure = killcon.getSelecction();

				if (sure) {

					InterfaceOffLine();
					JOptionPane.showMessageDialog(MainWindow.this, Language
							.getWord("DISSOF")
							+ mainConnection.getHostname(), Language.getWord("INFO"),
							JOptionPane.INFORMATION_MESSAGE);
					mainConnection.close();
				}

				return;
			}

			if (DBComponentType == 1) { // DB

				if (dbNamesVector.size() == 1) {

					JOptionPane.showMessageDialog(MainWindow.this, Language
							.getWord("INVOP"), Language.getWord("ERROR!"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				GenericQuestionDialog killdb = new GenericQuestionDialog(
						MainWindow.this, Language.getWord("YES"), Language
								.getWord("NO"), Language.getWord("BOOLDELDB"),
						Language.getWord("MESGDELDB") + dbComponentName + "?");

				boolean sure = killdb.getSelecction();

				if (sure) {

					int pos = dbNamesVector.indexOf(dbComponentName);
					PGConnection tempo = (PGConnection) connectionsVector.remove(pos);
					tempo.close();
					dbNamesVector.remove(pos);
					// Eliminando BD

					String result = mainConnection.executeSQL("DROP DATABASE "
							+ dbComponentName);
					addTextLogMonitor(Language.getWord("EXEC")
							+ "DROP DATABASE " + dbComponentName + "\"");

					if (result.equals("OK")) {

						TreePath selPath = mainTree.getSelectionPath();
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selPath
								.getLastPathComponent());
						//DefaultMutableTreeNode NodeDB = (DefaultMutableTreeNode) currentNode
						//		.getParent();
						treeModel.removeNodeFromParent(currentNode);
						addTextLogMonitor(Language.getWord("RES") + result);
					} else {
						String tmp = result.substring(0, result.length() - 1);
						addTextLogMonitor(Language.getWord("RES") + tmp);
						JOptionPane.showMessageDialog(MainWindow.this, Language
								.getWord("ERRORPOS")
								+ tmp, Language.getWord("ERROR!"),
								JOptionPane.ERROR_MESSAGE);
					}

				}

				return;
			}

			if (DBComponentType == 2 && !dbComponentName.startsWith(Language.getWord("NOTABLES"))) { // Table

				GenericQuestionDialog killtb = new GenericQuestionDialog(
						MainWindow.this, Language.getWord("YES"), Language.getWord("NO"), 
						Language.getWord("BOOLDELTB"),Language.getWord("MESGDELTB") 
						+ dbComponentName + "?");

				boolean sure = killtb.getSelecction();

				if (sure) {

					int poss = dbNamesVector.indexOf(currentDataBase);
					PGConnection konn = (PGConnection) connectionsVector.elementAt(poss);

					String result = konn.executeSQL("DROP TABLE \""
							+ dbComponentName + "\"");
					//String value = "";

					if (result.equals("OK")) {

						TreePath selectedPath = mainTree.getSelectionPath();
						TreePath lastPath = selectedPath.getParentPath();
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selectedPath.getLastPathComponent());
						treeModel.removeNodeFromParent(currentNode);
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) (lastPath.getLastPathComponent());

						if (treeNode.getChildCount() == 0) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(Language.getWord("NOTABLES"));
							treeNode.add(node);
						}

						structuresPanel.setNullTable();
						structuresPanel.setLabel("", "", "");
						recordsPanel.setLabel("", "", "");
						structuresPanel.activeToolBar(false);
						structuresPanel.activeIndexPanel(false);
						tabbedPane.setEnabledAt(0, false);
						tabbedPane.setEnabledAt(1, false);
						tabbedPane.setSelectedIndex(2);
						mainTree.setSelectionPath(lastPath);
					} else {
						result = result.substring(0, result.length() - 1);
					}
					
					addTextLogMonitor(Language.getWord("EXEC") + "DROP TABLE \"" + dbComponentName + "\";\"");
					addTextLogMonitor(Language.getWord("RES") + result);
				}

				return;
			}
		}
	}

	/*
	 * METODO keyReleased Maneja el evento de tecla liberada.
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * METODO focusGained Maneja un foco para los eventos del teclado
	 */
	public void focusGained(FocusEvent e) {
		Component tmp = e.getComponent();
		tmp.addKeyListener(this);
	}

	/**
	 * METODO focusLost
	 */
	public void focusLost(FocusEvent e) {
		Component tmp = e.getComponent();
		tmp.removeKeyListener(this);
	}

	/*
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/*
	 * METODO mousePressed Maneja los eventos cuando se hace click con el mouse
	 *
	public void mousePressed(MouseEvent e) {
		/*
		treeView.requestFocus();

		if (tree.isEditable())
			tree.setEditable(false);

		// Eventos de un Click con Boton Derecho
		if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {

			TreePath selPath = tree.getClosestPathForLocation(e.getX(), e
					.getY());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
					.getLastPathComponent();

			if (node.isLeaf()
					&& !node.toString()
							.startsWith(Language.getWord("NOTABLES"))) {

				if (!popup.isVisible()) {

					popup.show(tree, e.getX(), e.getY());

					if (!dbComponentName.equals(node.toString())) {

						dbComponentName = node.toString();
						mainTree.setSelectionPath(selPath);
						activeFoldersTables(node);
					}
				}
			}

			if (!node.isRoot() && !node.isLeaf()) {

				if (!popupDB.isVisible()) {

					popupDB.show(tree, e.getX(), e.getY());

					if (!dbComponentName.equals(node.toString())) {

						dbComponentName = node.toString();
						tree.setSelectionPath(selPath);
						activeFoldersDB(selPath);
					}
				}

			}
		}

		// Eventos de un Click con Boton Izquierdo

		if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {

			TreePath selectedPath = mainTree.getPathForLocation(e.getX(), e.getY());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
			//DefaultMutableTreeNode dbnode;
			dbComponentName = node.toString();
			mainTree.setSelectionPath(selectedPath);

			// Si se selecciono una Tabla
			if (node.isLeaf()) {
				activeFoldersTables(node);
				int index = dbNamesVector.indexOf(currentDataBase);
				PGConnection connection = (PGConnection) connectionsVector.elementAt(index);
				queriesPanel.setConnection(connection);
				recordsPanel.setOrder();
			} else {
				// Si se selecciono una Base de Datos
				if (!node.isRoot()) {
					activeFoldersDB(selectedPath);
				} else {
					// Si se selecciono el Servidor de Base de Datos
					DBComponentType = 0;

					if (OldCompType != DBComponentType) {
						structuresPanel.setNullTable();
						structuresPanel.setLabel("", "", "");
						recordsPanel.setLabel("", "", "");
						structuresPanel.activeToolBar(false);
						structuresPanel.activeIndexPanel(false);
						queriesPanel.setNullPanel();
						queriesPanel.setTextLabel(Language.getWord("NODBSEL"));
						tabbedPane.setEnabledAt(0, false);
						tabbedPane.setEnabledAt(1, false);
						tabbedPane.setEnabledAt(2, false);
						currentDataBase = "";
					}

					GenericQuestionDialog genericDialog = new GenericQuestionDialog(
							MainWindow.this, Language.getWord("YES"), Language.getWord("NO"), 
							Language.getWord("DBSCAN"),	Language.getWord("DYWLOOK"));

					boolean sure = genericDialog.getSelecction();

					if (!sure)
						return;

					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					query.setEnabled(false);

					// Consultando nombres de BD en el servidor
					Vector listDB = mainConnection.getResultSet("SELECT datname FROM pg_database WHERE datname != 'template1' AND datname != 'template0' ORDER BY datname");
					addTextLogMonitor(Language.getWord("EXEC") + " SELECT datname FROM pg_database " +
							"WHERE datname != 'template1' AND datname != 'template0' " +
							"ORDER BY datname;\"");

					Vector<String> newsDB = new Vector<String>();

					for (int p = 0; p < listDB.size(); p++) {

						Vector o = (Vector) listDB.elementAt(p);
						String db = (String) o.elementAt(0);

						if (!dbNamesVector.contains(db))
							newsDB.addElement(db);
					}

					if (newsDB.size() > 0) {

						UpdateDBTree updateDBs = new UpdateDBTree(LogWin,
								mainConnection, newsDB);
						Vector dbases = updateDBs.getDatabases();
						// Vector dbases = updateDBs.validDB;

						if (dbases.size() > 0) {

							Vector tmpConn = updateDBs.vecConn;

							for (int p = 0; p < dbases.size(); p++) {

								Object o = dbases.elementAt(p);
								String db = o.toString();

								PGConnection tmpDB = (PGConnection) tmpConn.elementAt(p);
								connectionsVector.addElement(tmpDB);
								dbNamesVector.addElement(db);

								DefaultMutableTreeNode dbLeaf = new DefaultMutableTreeNode(db);
								DefaultMutableTreeNode noTables = new DefaultMutableTreeNode(Language.getWord("NOTABLES"));
								dbLeaf.add(noTables);

								DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeModel.getRoot();
								treeModel.insertNodeInto(dbLeaf, parent, parent.getChildCount());
							} // fin for
						} // fin if dbases.size
					} // fin if newsDB.size

					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					MainTree.expandTree(selectedPath);
				}
			}
			OldCompType = DBComponentType;
		}
		
	} */

	/**
	 * M�todo writeFile Sobre escribe el archivo de configuraci�n usado
	 * cuando el usuario quiere guardar un nuevo idioma
	 */
	public void writeFile(String idiomName) {

		try {
			ConfigFileReader overWrite = new ConfigFileReader(configPath, 2);
			Vector LoginRegisters = overWrite.CompleteList();

			PrintStream configFile = new PrintStream(new FileOutputStream(configPath));
			configFile.println("language=" + idiomName);

			for (int i = 0; i < LoginRegisters.size(); i++) {

				ConnectionInfo tmp = (ConnectionInfo) LoginRegisters
						.elementAt(i);
				configFile.println("server=" + tmp.getHost());
				configFile.println("database=" + tmp.getDatabase());
				configFile.println("username=" + tmp.getUser());
				configFile.println("port=" + tmp.getPort());
				configFile.println("ssl=" + tmp.requireSSL());
				configFile.println("last=" + tmp.getDBChoosed());
			}

			configFile.close();
		} catch (Exception ex) {
		}
	}

	/**
	 * M�todo NConnect Se encarga de realizar las operaciones de conexion
	 */
	public void openConnectionDialog() {

		LogWin.setText("");
		boolean fail = true;
		Vector Xtables = new Vector();
		boolean lookForOthers = false;

		while (fail) {
			ConnectionDialog connectionForm = new ConnectionDialog(LogWin,
					MainWindow.this);
			connectionForm.setLanguage(xlanguage);
			connectionForm.pack();
			connectionForm.setLocationRelativeTo(MainWindow.this);
			connectionForm.setVisible(true);

			if (connectionForm.Connected()) {

				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				lookForOthers = connectionForm.lookForOthers();
				networkLink = connectionForm.checkLink();
				online = connectionForm.getDataReg();
				mainConnection = new PGConnection(online);

				if (!mainConnection.Fail()) {

					fail = false;
					Xtables = mainConnection
							.getResultSet("SELECT tablename FROM pg_tables WHERE tablename !~ '^pg_' AND tablename !~ '^pga_' AND tablename !~ '^sql_' ORDER BY tablename");
					permissions = mainConnection.getUserPerm(online.getUser());
					ReConfigFile(connectionForm.getDataReg(), connectionForm
							.getConfigRegisters(), connectionForm
							.getRegisterSelected());
				} else {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					ErrorDialog showError = new ErrorDialog(new JDialog(),
							mainConnection.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(MainWindow.this);
					showError.setVisible(true);
				}
			} else {
				return;
			}
		} // fin while

		connected = true;
		String strQuery = "SELECT datname FROM pg_database WHERE";

		if (!lookForOthers)
			strQuery += " datname='" + online.getDatabase() + "'";
		else
			strQuery += " datname != 'template1' AND datname != 'template0'";

		strQuery += " ORDER BY datname";

		Vector listDB = mainConnection.getResultSet(strQuery);
		int numDbases = listDB.size();

		addTextLogMonitor(Language.getWord("LOOKDBS") + online.getHost() + "'");
		addTextLogMonitor(Language.getWord("EXEC") + " " + strQuery + ";\"");
		addTextLogMonitor(numDbases + " " + Language.getWord("DBON")
				+ online.getHost());

		if (numDbases > 0) {

			for (int i = 0; i < numDbases; i++) {

				Vector o = (Vector) listDB.elementAt(i);
				String dbname = (String) o.elementAt(0);
				addTextLogMonitor(Language.getWord("TRYCONN") + ": \"" + dbname
						+ "\"... ");
				ConnectionInfo tmp = new ConnectionInfo(online.getHost(),
						dbname, online.getUser(), online.getPassword(), online
								.getPort(), online.requireSSL());
				PGConnection proofConn = new PGConnection(tmp);

				if (!proofConn.Fail()) {

					addTextLogMonitor(Language.getWord("OKACCESS"));
					dbNamesVector.addElement(dbname);
					connectionsVector.addElement(proofConn);
				} else {
					addTextLogMonitor(Language.getWord("NOACCESS"));
				}
			} // fin for
		} // fin if

		top = new DefaultMutableTreeNode(online.getHost());
		numDbases = dbNamesVector.size();
		addTextLogMonitor(Language.getWord("REPORT")
				+ Language.getWord("USER ") + mainConnection.getUsername()
				+ Language.getWord("VALID") + numDbases
				+ Language.getWord("NUMDB"));
		int index = -1;

		for (int m = 0; m < numDbases; m++) {

			Object o = dbNamesVector.elementAt(m);
			String dbname = o.toString();
			addTextLogMonitor(Language.getWord("DB: ") + dbname);
			category1 = new DefaultMutableTreeNode(dbname);
			top.add(category1);

			if (dbname.equals(online.getDatabase())) {

				index = m;
				Vector OneTable = Xtables;
				int numTables = OneTable.size();

				if (numTables == 0) {
					globalLeaf = new DefaultMutableTreeNode(Language
							.getWord("NOTABLES"));
					category1.add(globalLeaf);
				} else {
					for (int i = 0; i < numTables; i++) {

						Vector t = (Vector) OneTable.elementAt(i);
						String tablename = (String) t.elementAt(0);
						globalLeaf = new DefaultMutableTreeNode(tablename);
						category1.add(globalLeaf);
					}
				}// fin else hay tablas
			} else {
				globalLeaf = new DefaultMutableTreeNode(Language
						.getWord("NOTABLES"));
				category1.add(globalLeaf);
			}
		}// fin ciclo for

		/*treeModel = new DefaultTreeModel(top);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(renderer);
		tree.expandRow(0);
		tree.expandRow(index + 1);
		tree.setSelectionRow(index + 1);*/
		
		mainTree = new MainTree(this,top,index);
		
		treeView = new JScrollPane(mainTree.getTree());
		treeView.addFocusListener(this);
		treeView.setMinimumSize(new Dimension(100, 400));
		treeView.setPreferredSize(new Dimension(200, 400));

		topPanel.setLeftComponent(treeView);
		topPanel.setDividerLocation(135);

		//tree.addMouseListener(this);

		connect.setEnabled(false);
		
		//connectItem.setEnabled(false);
		//disconnectItem.setEnabled(true);
		
		menuBar.setAlreadyConnected();
		menuBar.switchJMenus(true); // encender los botones requeridos cuando la
							// conexi�n es exitosa
		
		activeToolBar(true);

		if (permissions[0].equals("false"))
			dataBase.setEnabled(false);

		if (permissions[1].equals("false"))
			admin.setEnabled(false);

		tabbedPane.setEnabled(true);
		tabbedPane.setEnabledAt(0, false);
		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, true);
		tabbedPane.setSelectedIndex(2);

		queriesPanel.setButtons();

		dbComponentName = online.getDatabase();
		DBComponentType = 1;
		currentDataBase = dbComponentName;
		int position = dbNamesVector.indexOf(currentDataBase);
		queriesPanel.setConnection((PGConnection) connectionsVector.elementAt(position));
		queriesPanel.setLabel(currentDataBase, queriesPanel.getDBOwner());
		OldCompType = 1;
		String sslEnabled = Language.getWord("DISABLE");
		if (online.requireSSL())
			sslEnabled = Language.getWord("ENABLE");

		setTitle(Language.getWord("UONLINE") + mainConnection.getUsername());
		String mesg0 = Language.getWord("INFOSERVER") + mainConnection.getHostname();
		String mesg1 = Language.getWord("VERSION") + mainConnection.getVersion();
		String mesg2 = Language.getWord("WACCESS") + numDbases
				+ Language.getWord("NUMDB");
		String mesg3 = Language.getWord("CHKSSL") + ": " + sslEnabled;

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		JOptionPane.showMessageDialog(MainWindow.this, mesg0 + "\n" + mesg1 + "\n"
				+ mesg2 + "\n" + mesg3, Language.getWord("INFO"),
				JOptionPane.INFORMATION_MESSAGE);

		queriesPanel.queryX.requestFocus();

		String hostname = mainConnection.getHostname();
		int port = mainConnection.getPort();

		if (networkLink) {
			guard = new ConnectionWatcher(hostname, port, MainWindow.this);
			guard.start();
		}

		ChangeListener l = new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				int carpeta = tabbedPane.getSelectedIndex();
				int pox = dbNamesVector.indexOf(currentDataBase);
				PGConnection connection;

				if (pox != -1)
					connection = (PGConnection) connectionsVector.elementAt(pox);
				else
					connection = (PGConnection) connectionsVector.elementAt(0);

				currentTable = connection.getSpecStrucTable(dbComponentName);

				switch (carpeta) {

				case 0:
					activatedTab = 0;
					indexesVector = connection.getIndexTable(dbComponentName);
					addTextLogMonitor(Language.getWord("EXEC")
							+ connection.getSQL() + "\"");
					structuresPanel.activeToolBar(true);
					structuresPanel.activeIndexPanel(true);

					System.out.println("DBComponent: " + dbComponentName);
					String result = connection.getOwner(dbComponentName);
					structuresPanel.setLabel(currentDataBase, dbComponentName,result);
					structuresPanel.setTableStruct(currentTable);
					structuresPanel.setIndexTable(indexesVector, connection);
					break;

				case 1:
					activatedTab = 1;
					recordsPanel.setRecordFilter(dbComponentName, currentDataBase);

					/*
					 * if (!evaluatedDB.contains(DBComponentName)) {
					 * currentTable.setSchema(connection.getSchemaName(DBComponentName));
					 * currentTable.setSchemaType(connection.gotUserSchema(DBComponentName));
					 * evaluatedDB.addElement(DBComponentName); }
					 */

					if (!recordsPanel.updateTable(connection, dbComponentName, currentTable)) {
						tabbedPane.setSelectedIndex(0);
						activatedTab = 0;

						indexesVector = connection.getIndexTable(dbComponentName);
						addTextLogMonitor(Language.getWord("EXEC")
								+ connection.getSQL() + "\"");
						structuresPanel.activeToolBar(true);
						structuresPanel.activeIndexPanel(true);
						String ownerName = connection.getOwner(dbComponentName);
						structuresPanel.setLabel(currentDataBase, dbComponentName,ownerName);
						structuresPanel.setTableStruct(currentTable);
						structuresPanel.setIndexTable(indexesVector, connection);
					}
					break;

				case 2:
					activatedTab = 2;
					updateQueriesPanel(connection.getOwnerDB());
					break;
				}
			}
		};

		tabbedPane.addChangeListener(l);
	}

	/**
	 * METODO getTime Retorna la hora
	 */
	public String[] getTime() {

		Calendar today = Calendar.getInstance();
		String[] val = new String[5];
		int monthInt = today.get(Calendar.MONTH) + 1;
		int minuteInt = today.get(Calendar.MINUTE);
		String zero = "";
		String min = "";

		if (monthInt < 10)
			zero = "0";

		if (minuteInt < 10)
			min = "0";

		val[0] = "" + today.get(Calendar.DAY_OF_MONTH);
		val[1] = zero + monthInt;
		val[2] = "" + today.get(Calendar.YEAR);
		val[3] = "" + today.get(Calendar.HOUR_OF_DAY);
		val[4] = min + today.get(Calendar.MINUTE);

		return val;
	}

	/**
	 * METODO DateLogName Crea el nombre del archivo de logs segun la fecha
	 */
	public String DateLogName(String[] val) {

		String dformat = val[0] + "-" + val[1] + "-" + val[2] + "_" + val[3]
				+ "-" + val[4];
		return dformat;
	}

	public String DateClassic(String[] val) {

		String dformat = val[3] + ":" + val[4] + " " + val[0] + "/" + val[1]
				+ "/" + val[2];
		return dformat;
	}

	public void printFile(PrintStream xfile, Vector registers,
			Vector FieldNames, String Separator, TableHeader theader) {

		Vector types = new Vector();

		try {
			int TableWidth = FieldNames.size();

			for (int p = 0; p < TableWidth; p++) {

				String column = (String) FieldNames.elementAt(p);
				types.addElement(theader.getType(column));
				xfile.print(column);

				if (p < TableWidth - 1)
					xfile.print(Separator);
			}

			xfile.print("\n");

			for (int p = 0; p < registers.size(); p++) {

				Vector rData = (Vector) registers.elementAt(p);

				for (int i = 0; i < TableWidth; i++) {

					Object o = rData.elementAt(i);
					String Stype = (String) types.elementAt(i);
					String field = "null";

					if (o != null) {

						if (Stype.startsWith("varchar")
								|| Stype.startsWith("name")
								|| Stype.startsWith("text"))
							field = o.toString();

						if (Stype.startsWith("int")) {
							Integer ipr = (Integer) o;
							field = "" + ipr;
						}

						if (Stype.startsWith("float")
								|| Stype.startsWith("decimal")) {
							Integer ipr = (Integer) o;
							field = "" + ipr;
						}

						if (Stype.startsWith("bool")) {
							Boolean bx = (Boolean) o;
							field = "" + bx;
						}

					}

					xfile.print(field);

					if (i < TableWidth - 1)
						xfile.print(Separator);

				} // fin for

				xfile.print("\n");
			} // fin for

			xfile.close();
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * METODO Count Retorna el numero de registros de una tabla
	 */
	public int Count(String TableN, PGConnection konn) {

		int val;
		String counting = "SELECT count(*) FROM " + TableN + ";";
		Vector result = konn.getResultSet(counting);
		Vector value = (Vector) result.elementAt(0);

		try {
			Integer entero = (Integer) value.elementAt(0);
			val = entero.intValue();
		} catch (Exception ex) {
			Long entero = (Long) value.elementAt(0);
			val = entero.intValue();
		}

		return val;
	}

	/**
	 * METODO closePGConnections Cierra todas las conexiones a un servidor
	 */
	public void closePGSockets() {

		/* ciclo for para cerrar todas la pg_konn activas */
		for (int p = 0; p < connectionsVector.size(); p++) {
			PGConnection tempo = (PGConnection) connectionsVector.remove(p);
			tempo.close();
		}

		mainConnection.close();
		dbNamesVector = new Vector();
		connectionsVector = new Vector();
	}

	/**
	 * METODO Disconnect Desactiva la conexion entre el cliente y el SMBD
	 */
	public void Disconnect() {

		closePGSockets();

		structuresPanel.activeToolBar(false);
		structuresPanel.activeIndexPanel(false);
		structuresPanel.setNullTable();

		recordsPanel.activeInterface(false);
		queriesPanel.setNullPanel();
		InterfaceOffLine();
		SaveLog();

		JOptionPane.showMessageDialog(MainWindow.this, Language.getWord("DISSOF")
				+ mainConnection.getHostname(), Language.getWord("INFO"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * METODO main : SaveLog Guarda a un archivo la informacion contenida en el
	 * Monitor de Eventos
	 */
	public void SaveLog() {

		try {
			String LogName = DateLogName(getTime()) + ".log";
			String UHome = "logs" + System.getProperty("file.separator");

			if (OS.equals("Linux") || OS.equals("Solaris")
					|| OS.equals("FreeBSD"))

				UHome = System.getProperty("user.home")
						+ System.getProperty("file.separator") + ".xpg"
						+ System.getProperty("file.separator") + "logs"
						+ System.getProperty("file.separator");

			PrintStream fileLog = new PrintStream(new FileOutputStream(UHome
					+ LogName));
			fileLog.print("Begin: " + startDate + "\n" + "\n");
			fileLog.print(LogWin.getText());
			fileLog.print("End: " + DateClassic(getTime()));
			fileLog.close();
		} catch (Exception ex) {

			System.out.println("Error: " + ex);
			ex.printStackTrace();
		}
	}

	public static void activeFoldersDB(TreePath selectedPath) {

		tabbedPane.setSelectedIndex(2);

		queriesPanel.setTextAreaEditable();
		
		menuBar.enableQueryItem();

		if (OldCompType == 0 || !queriesPanel.functions.isEnabled()) {
			tabbedPane.setEnabledAt(2, true);
			queriesPanel.queryX.setEditable(true);
			queriesPanel.functions.setEnabled(true);
			queriesPanel.loadQuery.setEnabled(true);
			queriesPanel.hqQuery.setEnabled(true);
		}

		currentDataBase = dbComponentName;

		int index = dbNamesVector.indexOf(currentDataBase);
		PGConnection connection = (PGConnection) connectionsVector.elementAt(index);
		queriesPanel.setConnection(connection);

		queriesPanel.setLabel(currentDataBase, connection.getOwnerDB());
		DBComponentType = 1;

		// Consultando nombres de tablas de una BD, sin incluir tablas del
		// sistema
		Vector RefreshTableList = connection
				.getResultSet("SELECT tablename FROM pg_tables WHERE tablename !~ '^pg_' AND tablename  !~ '^pga_' AND tablename !~ '^sql_' ORDER BY tablename");
		addTextLogMonitor(Language.getWord("EXEC")
				+ " SELECT tablename FROM pg_tables WHERE tablename  !~ '^pg_' AND tablename  !~ '^pga_' AND tablename !~ '^sql_' ORDER BY tablename\"");

		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (selectedPath.getLastPathComponent());
		currentNode.removeAllChildren();

		if (RefreshTableList.size() != 0) {

			for (int i = 0; i < RefreshTableList.size(); i++) {

				Vector o = (Vector) RefreshTableList.elementAt(i);
				String tableName = (String) o.elementAt(0);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(tableName);
				currentNode.add(node);
			}
		} else {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(Language
					.getWord("NOTABLES"));
			currentNode.add(node);
		}

		//treeModel.nodeStructureChanged(currentNode);
		mainTree.setTreeModel(currentNode);
		
		MainTree.expandTree(selectedPath);
		
		if (OldCompType != 1) {

			structuresPanel.setNullTable();
			structuresPanel.setLabel("", "", "");
			recordsPanel.setLabel("", "", "");
			structuresPanel.activeToolBar(false);
			structuresPanel.activeIndexPanel(false);
			tabbedPane.setEnabledAt(0, false);
			tabbedPane.setEnabledAt(1, false);
		}
	}

	public void activeFoldersTables(DefaultMutableTreeNode node) {

		DefaultMutableTreeNode dbnode;
		DBComponentType = 2;
		dbnode = (DefaultMutableTreeNode) node.getParent();
		currentDataBase = dbnode.toString();

		if (OldCompType == 0) {

			tabbedPane.setEnabledAt(2, true);
			queriesPanel.functions.setEnabled(true);
			queriesPanel.loadQuery.setEnabled(true);
		}

		int index = dbNamesVector.indexOf(currentDataBase);
		PGConnection connection = (PGConnection) connectionsVector.elementAt(index);

		if (!dbComponentName.startsWith(Language.getWord("NOTABLES"))) {

			queriesPanel.setTextAreaEditable();

			/* if (!query.isEnabled())
				query.setEnabled(true); */
			
			menuBar.enableQueryItem();

			currentTable = connection.getSpecStrucTable(dbComponentName);
			indexesVector = connection.getIndexTable(dbComponentName);
			tabbedPane.setEnabledAt(0, true);
			tabbedPane.setEnabledAt(1, true);
			// Adicion temporal
			tabbedPane.setEnabledAt(2, true);
			structuresPanel.updateUI();

			if (activatedTab == 0) {

				structuresPanel.activeToolBar(true);
				structuresPanel.activeIndexPanel(true);
				String result = connection.getOwner(dbComponentName);
				structuresPanel.setLabel(currentDataBase, dbComponentName, result);
				structuresPanel.setTableStruct(currentTable);
				structuresPanel.setIndexTable(indexesVector, connection);
			}

			if (activatedTab == 1) {

				recordsPanel.setRecordFilter(dbComponentName, currentDataBase);

				/*
				 * if (!evaluatedDB.contains(DBComponentName)) {
				 * currentTable.setSchema(connection.getSchemaName(DBComponentName));
				 * currentTable.setSchemaType(connection.gotUserSchema(DBComponentName));
				 * evaluatedDB.addElement(DBComponentName); }
				 */

				if (!recordsPanel.updateTable(connection, dbComponentName,
						currentTable)) {

					tabbedPane.setSelectedIndex(0);
					activatedTab = 0;

					structuresPanel.activeToolBar(true);
					structuresPanel.activeIndexPanel(true);

					String result = connection.getOwner(dbComponentName);
					structuresPanel.setLabel(currentDataBase, dbComponentName,
							result);
					structuresPanel.setTableStruct(currentTable);
					structuresPanel.setIndexTable(indexesVector, connection);
				}
			}

			if (activatedTab == 2) {
				updateQueriesPanel(connection.getOwnerDB());
			}

		} else {
			if (activatedTab == 0) {
				tabbedPane.setSelectedIndex(2);
				activatedTab = 2;
			}

			if (activatedTab == 1) {
				tabbedPane.setSelectedIndex(2);
				activatedTab = 2;
			}

			if (activatedTab == 2) {
				queriesPanel.setLabel(currentDataBase, connection.getOwnerDB());
			}

			tabbedPane.setEnabledAt(0, false);
			tabbedPane.setEnabledAt(1, false);
		}
	}

	public String createTableSQL(Table currentTable) {

		String sql = "CREATE TABLE " + currentTable.getName() + " (\n";
		// Nuevos datos de la tabla
		TableHeader headT = currentTable.getTableHeader();
		int numFields = headT.getNumFields();

		for (int k = 0; k < numFields; k++) {

			Object o = (String) headT.getNameFields().elementAt(k);
			String field_name = o.toString();
			TableFieldRecord tmp = (TableFieldRecord) headT.hashFields
					.get(field_name);
			sql += tmp.getName() + " ";

			String typeF = tmp.getType();

			if ("char".equals(tmp.getType()) || "varchar".equals(tmp.getType())) {

				int longStr = tmp.getOptions().getCharLong();

				if (longStr > 0)
					typeF = tmp.getType() + "("
							+ tmp.getOptions().getCharLong() + ")";
				else
					typeF = tmp.getType();
			}

			sql += typeF + " ";

			Boolean tmpbool = new Boolean(tmp.getOptions().isNullField());

			if (tmpbool.booleanValue())
				sql += "NOT NULL ";

			String defaultV = tmp.getOptions().getDefaultValue();

			if (defaultV.endsWith("::bool")) {

				if (defaultV.indexOf("t") != -1)
					defaultV = "true";
				else
					defaultV = "false";
			}

			if (defaultV.length() > 0)
				sql += " DEFAULT " + defaultV;

			if (k < numFields - 1)
				sql += ",\n";
		}

		sql += "\n);";

		return sql;
	}

	public void updateQueriesPanel(String owner) {

		queriesPanel.setLabel(currentDataBase, owner);

		queriesPanel.functions.setEnabled(true);
		queriesPanel.loadQuery.setEnabled(true);
		queriesPanel.hqQuery.setEnabled(true);
		queriesPanel.saveQuery.setEnabled(false);
		queriesPanel.runQuery.setEnabled(false);
		queriesPanel.queryX.requestFocus();

		String currString = queriesPanel.getStringQuery();

		if (currString.length() > 1) {

			queriesPanel.newQuery.setEnabled(true);

			if (currString.length() > 15) {
				queriesPanel.saveQuery.setEnabled(true);
				queriesPanel.runQuery.setEnabled(true);
			}
		} else {
			queriesPanel.newQuery.setEnabled(false);
		}
	}

	/**
	 * METODO isConnected Retorna verdadero si la conexion esta activa
	 */
	public boolean isConnected() {
		return connected;
	}
	
	public static String getCurrentDB() {
		return currentDataBase;
	}
	
	public static void setDBComponent(String component) {
		dbComponentName = component;
	}
	
	public static String getDBComponent() {
		return dbComponentName;
	}

	public static int getDBPosition() {
		return dbNamesVector.indexOf(currentDataBase);
	}
	
	public static PGConnection getConnectionAt(int index) {
		PGConnection connection = (PGConnection) connectionsVector.elementAt(index);
		return connection;
	}
	
	public static void setLabels(String newName, String owner) {
		structuresPanel.setLabel(MainWindow.getCurrentDB(), newName, owner);
		recordsPanel.setLabel(MainWindow.getCurrentDB(), newName, owner);
	}

	public static void setTabs() {
		tabbedPane.setSelectedIndex(2);
		tabbedPane.setEnabledAt(0, false);
		tabbedPane.setEnabledAt(1, false);
	}
	
	public static PGConnection getMainConnection() {
		return mainConnection;
	}
	
	public static boolean isSelectedMainConnection() {
		return dbComponentName.equals(mainConnection.getDBname());
	}
	
	public static void closeConnection() {
		int pos = dbNamesVector.indexOf(dbComponentName);
		PGConnection tempo = (PGConnection) connectionsVector.remove(pos);
		tempo.close();
		dbNamesVector.remove(pos);
	}
	
	
	public void updateUI(TreePath selectedPath) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
		setDBComponent(node.toString());
		
		// Si se selecciono una Tabla
		if (node.isLeaf()) {
			activeFoldersTables(node);
			int index = dbNamesVector.indexOf(currentDataBase);
			PGConnection connection = (PGConnection) connectionsVector.elementAt(index);
			queriesPanel.setConnection(connection);
			recordsPanel.setOrder();
		} else {
			// Si se selecciono una Base de Datos
			if (!node.isRoot()) {
				activeFoldersDB(selectedPath);
			} else {
				// Si se selecciono el Servidor de Base de Datos
				DBComponentType = 0;

				if (OldCompType != DBComponentType) {

					structuresPanel.setNullTable();
					structuresPanel.setLabel("", "", "");
					recordsPanel.setLabel("", "", "");
					structuresPanel.activeToolBar(false);
					structuresPanel.activeIndexPanel(false);
					queriesPanel.setNullPanel();
					queriesPanel.setTextLabel(Language.getWord("NODBSEL"));

					tabbedPane.setEnabledAt(0, false);
					tabbedPane.setEnabledAt(1, false);
					tabbedPane.setEnabledAt(2, false);
					currentDataBase = "";
				}

				GenericQuestionDialog scanDb = new GenericQuestionDialog(
						MainWindow.this, Language.getWord("YES"), Language
						.getWord("NO"), Language.getWord("DBSCAN"),
						Language.getWord("DYWLOOK"));

				boolean sure = scanDb.getSelecction();

				if (!sure)
					return;

				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				query.setEnabled(false);

				// Consultando nombres de BD en el servidor
				Vector listDB = mainConnection.getResultSet("SELECT datname FROM pg_database WHERE datname != 'template1' AND datname != 'template0' ORDER BY datname");
				MainWindow.addTextLogMonitor(Language.getWord("EXEC")
						+ " SELECT datname FROM pg_database WHERE datname != 'template1' AND datname != 'template0' ORDER BY datname;\"");

				Vector<String> newsDB = new Vector<String>();

				for (int p = 0; p < listDB.size(); p++) {

					Vector o = (Vector) listDB.elementAt(p);
					String db = (String) o.elementAt(0);

					if (!dbNamesVector.contains(db))
						newsDB.addElement(db);
				}

				if (newsDB.size() > 0) {

					UpdateDBTree updateDBs = new UpdateDBTree(LogWin,
							mainConnection, newsDB);
					Vector dbases = updateDBs.getDatabases();
					// Vector dbases = updateDBs.validDB;

					if (dbases.size() > 0) {

						Vector tmpConn = updateDBs.vecConn;

						for (int p = 0; p < dbases.size(); p++) {

							Object o = dbases.elementAt(p);
							String db = o.toString();

							PGConnection tmpDB = (PGConnection) tmpConn
							.elementAt(p);
							connectionsVector.addElement(tmpDB);
							dbNamesVector.addElement(db);

							DefaultMutableTreeNode dbLeaf = new DefaultMutableTreeNode(
									db);
							DefaultMutableTreeNode noTables = new DefaultMutableTreeNode(
									Language.getWord("NOTABLES"));
							dbLeaf.add(noTables);

							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeModel
							.getRoot();
							treeModel.insertNodeInto(dbLeaf, parent, parent
									.getChildCount());
						} // fin for
					} // fin if dbases.size
				} // fin if newsDB.size

				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				MainTree.expandTree(selectedPath);
			}
		}
		OldCompType = DBComponentType;
	}
	
	public void exit() {
		if (connected) {
			GenericQuestionDialog exitWin = new GenericQuestionDialog(
					MainWindow.this, Language.getWord("YES"), Language.getWord("NO"), 
					Language.getWord("BOOLEXIT"),Language.getWord("MESGEXIT"));
			boolean sure = exitWin.getSelecction();
			if (sure) {
				SaveLog();
				closePGSockets();
				System.exit(0);
			}
			return;
		} else {
			System.exit(0);
		}
	}
	
}
