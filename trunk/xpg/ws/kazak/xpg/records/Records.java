/**
 * Disponible en http://www.kazak.ws
 *
 * Desarrollado por Soluciones KAZAK 
 * Grupo de Investigacion y Desarrollo de Software Libre
 * Santiago de Cali/Republica de Colombia 2001
 *
 * CLASS Records v 0.1                                                   
 * Descripcion:
 * Esta clase se encarga de manejar el panel de Registros
 * en la interfaz principal. A traves de este panel, se
 * pueden realizar operaciones como ingresar, modificar y
 * eliminar registros de una tabla.
 *
 * Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
 *                                                                   
 * Fecha: 2001/10/01                                                 
 *
 * Autores: Beatriz Flori�n  - bettyflor@kazak.ws                    
 *          Gustavo Gonzalez - xtingray@kazak.ws                     
 *          Angela Sandobal  - angesand@libertad.univalle.edu.co     
 */
package ws.kazak.xpg.records;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import ws.kazak.xpg.db.PGConnection;
import ws.kazak.xpg.db.Table;
import ws.kazak.xpg.db.TableHeader;
import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.input.ErrorDialog;
import ws.kazak.xpg.misc.input.ExportSeparatorField;
import ws.kazak.xpg.misc.input.ExportToFile;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;
import ws.kazak.xpg.misc.input.ImportSeparatorField;
import ws.kazak.xpg.report.ExportToReport;
import ws.kazak.xpg.report.ReportDesigner;

public class Records extends JPanel implements ActionListener,SwingConstants,KeyListener,FocusListener {

	private static final long serialVersionUID = 1L;
	JToolBar toolBar;
	JScrollPane tableScroll;
	JPanel generalPanel;
	JTextField title;
	JScrollPane mainScrollpane;
	JPanel firstPanel;
	JPanel base;
	JButton insertButton,deleteButton,updateButton,exportButton,reportButton;
	JCheckBox checkBox1,checkBox2;
	JComboBox combo1,combo2;
	JTextField combo3,numRegTextField,limitText; 
	JLabel msg1,msg2;
	JButton button,advanced;
	JTable table;
	JPanel topPanel;
	JFrame frame;

	final JButton backButton;
	final JButton queryLeft;
	final JButton queryRight;
	final JButton forwardButton;

	JTextField onScreen = new JTextField();
	JTextField onTable = new JTextField();
	JTextField onMem = new JTextField();
	JTextField currentStatistic = new JTextField();

	boolean firstBool = true;
	String currentTable = "";
	String oldTable = "";
	String operator = "";
	String field = "";
	Table tableStruct;
	PGConnection pgconnection;
	final JPopupMenu popup = new JPopupMenu();
	String sentence = "";
	String whereClausule = "";
	boolean refreshOn = false;

	int recordsTotal = 0;
	int totalReg = 0;
	int recordsCount = 0; 
	int nPages = 0;
	int indexMin = 1;
	int indexMax = 50;
	int currentPage = 1;
	int oldPage = 0;
	int oldMem = 0;
	int start = 0;
	int limit = 50;

	String firstField;
	String recordFilter = "*";
	String orderBy = "";
	Hashtable<String,String> hashRecordFilter = new Hashtable<String,String>();
	Hashtable<String,Hashtable> dbHash = new Hashtable<String,Hashtable>();
	RecordsTableModel myModel;

	Vector columnNamesVector = new Vector();

	Object[] columnNames;
	Object[][] data;

	private JTextArea logArea;

	boolean firstTime = true;
	boolean connected = true;

	String realTableName;

	/******************** METODO CONSTRUCTOR ********************/
	public Records (JFrame frame,JTextArea logArea) {

		this.frame = frame;
		this.logArea = logArea;
		setLayout(new BorderLayout());
		toolBar = new JToolBar(SwingConstants.VERTICAL);
		toolBar.setFloatable(false);
		createToolBar();

		Border etched1 = BorderFactory.createEtchedBorder();

		title = new JTextField("");
		title.setHorizontalAlignment(JTextField.CENTER);
		title.setEditable(false);

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
		top.add(title);

		JPanel recordsControl = new JPanel();
		recordsControl.setLayout(new FlowLayout());

		onTable.setText(" " + Language.getWord("TOTAL") + " : 0 ");
		onTable.setEditable(false);

		onMem.setText(" " + Language.getWord("ONMEM") + " : 0 ");
		onMem.setEditable(false);

		onScreen.setText(" " + Language.getWord("ONSCR") + " : 0 ");
		onScreen.setEditable(false);

		recordsControl.add(onScreen);
		recordsControl.add(onMem);
		recordsControl.add(onTable);

		JPanel recordsButtons = new JPanel();
		recordsButtons.setLayout(new FlowLayout());

		URL imgURB = getClass().getResource("/icons/backup.png");
		backButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURB)));
		backButton.setEnabled(false);
		backButton.setToolTipText(Language.getWord("FSET"));

		URL imgURLeft = getClass().getResource("/icons/queryLeft.png");
		queryLeft = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURLeft)));
		queryLeft.setEnabled(false);
		queryLeft.setToolTipText(Language.getWord("PSET"));

		URL imgURRight = getClass().getResource("/icons/queryRight.png");
		queryRight = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURRight)));
		queryRight.setEnabled(false);
		queryRight.setToolTipText(Language.getWord("NSET"));

		URL imgURF = getClass().getResource("/icons/forward.png");
		forwardButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURF)));
		forwardButton.setEnabled(false);
		forwardButton.setToolTipText(Language.getWord("LSET"));

		MouseListener listener = new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				if(backButton.isEnabled()) {
					start = 0; 
					limit = 50;
					currentPage = 1;
					backButton.setEnabled(false);
					queryLeft.setEnabled(false);

					if (!queryRight.isEnabled()) {
						queryRight.setEnabled(true);
						forwardButton.setEnabled(true);
					}

					indexMin = 1;
					indexMax = 50;

					String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + " LIMIT 50 OFFSET 0";

					if (whereClausule.length()!=0) {
						sql = "SELECT * FROM ("
							+ "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + whereClausule + ") " 
							+ "AS foo LIMIT 50 OFFSET 0";
					}

					Vector result = pgconnection.getResultSet(sql);
					Vector columns = pgconnection.getTableHeader();

					addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

					if (!pgconnection.queryFail()) {
						addTextLogMonitor(Language.getWord("RES") + "OK");
						showQueryResult(result,columns);
						updateUI();
					}
				}
			}
		};

		backButton.addMouseListener(listener);

		MouseListener mouseLQL = new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				if (queryLeft.isEnabled()) {

					currentPage--;

					if (currentPage == 1) {

						queryLeft.setEnabled(false);
						backButton.setEnabled(false);

						indexMin = 1;
						indexMax = 50;
					}
					else {
						if (currentPage == nPages - 1)
							indexMax = indexMin - 1;
						else
							indexMax -= 50;

						indexMin -= 50;
					}

					if (!queryRight.isEnabled()) {

						queryRight.setEnabled(true);
						forwardButton.setEnabled(true);
					}


					start = indexMin - 1;
					limit = 50;

					String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + " LIMIT 50 OFFSET " + start;

					if (whereClausule.length()!=0)

						sql = "SELECT * FROM ("
								+ "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + whereClausule + ") AS foo LIMIT 50"
								+ " OFFSET " + start;

					Vector res = pgconnection.getResultSet(sql);
					Vector col = pgconnection.getTableHeader();

					addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

					if (!pgconnection.queryFail()) {

						addTextLogMonitor(Language.getWord("RES") + "OK");
						showQueryResult(res,col);
						updateUI();
					}
				}
			}
		};

		queryLeft.addMouseListener(mouseLQL);

		MouseListener mouseLQR = new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				if (queryRight.isEnabled()) {

					currentPage++;
					start = indexMax;

					if (currentPage > 1) {

						if (!queryLeft.isEnabled()) {

							queryLeft.setEnabled(true);
							backButton.setEnabled(true);
						}
					}

					int downLimit = 1;

					if (currentPage == nPages) {
						indexMax = recordsCount;
						downLimit = (nPages-1) * 50 + 1;
						queryRight.setEnabled(false);
						forwardButton.setEnabled(false);
					}

					int diff = (indexMax - downLimit) + 1;

					if (diff > 50)
						diff = 50;

					limit = diff;

					String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + " LIMIT " + diff + " OFFSET " + start;

					if (whereClausule.length()!=0)

						sql = "SELECT * FROM (" 
								+ "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + whereClausule + ") AS foo LIMIT " 
								+ diff + " OFFSET " + start; 

					Vector res = pgconnection.getResultSet(sql);
					Vector col = pgconnection.getTableHeader();

					addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

					indexMin += 50;
					indexMax += 50;

					if (!pgconnection.queryFail()) {

						addTextLogMonitor(Language.getWord("RES") + "OK");
						showQueryResult(res,col);
						updateUI();
					}
				}
			}
		};

		queryRight.addMouseListener(mouseLQR);

		MouseListener mouseLF = new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				if (forwardButton.isEnabled()) {

					currentPage = nPages;
					queryRight.setEnabled(false);
					forwardButton.setEnabled(false);

					if (!queryLeft.isEnabled()) {

						queryLeft.setEnabled(true);
						backButton.setEnabled(true);
					}

					indexMin = ((nPages-1) * 50); 
					start = indexMin;
					limit = 50;

					indexMin++;

					String sql = "SELECT " + recordFilter + " FROM \"" + currentTable + " LIMIT 50 OFFSET " + start;

					if (whereClausule.length()!=0)

						sql = "SELECT * FROM ("
							+ "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + whereClausule + ") AS foo " + " LIMIT 50"
							+ " OFFSET " + start;

					Vector res = pgconnection.getResultSet(sql);
					Vector col = pgconnection.getTableHeader();

					addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

					if (!pgconnection.queryFail()) {

						addTextLogMonitor(Language.getWord("RES") + "OK");
						showQueryResult(res,col);
						updateUI();
					}
				}
			}
		};

		forwardButton.addMouseListener(mouseLF);

		recordsButtons.add(backButton);
		recordsButtons.add(new JPanel());
		recordsButtons.add(queryLeft);
		recordsButtons.add(new JPanel());
		recordsButtons.add(queryRight);
		recordsButtons.add(new JPanel());
		recordsButtons.add(forwardButton);
		recordsButtons.add(new JPanel());

		currentStatistic.setHorizontalAlignment(JTextField.CENTER);
		currentStatistic.setEditable(false);

		JPanel dataStat = new JPanel();
		dataStat.setLayout(new BorderLayout());

		dataStat.add(recordsControl,BorderLayout.NORTH);
		dataStat.add(currentStatistic,BorderLayout.CENTER);
		dataStat.add(recordsButtons,BorderLayout.SOUTH);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched1);
		dataStat.setBorder(title1);

		top.add(new JPanel(),BorderLayout.CENTER); 
		top.add(dataStat);
		top.setBorder(title1);

		add(top,BorderLayout.NORTH); 

		setLabel("","","");
		showQueryResult(new Vector(),new Vector());
		add(toolBar,BorderLayout.WEST);
		footer();
		add(firstPanel,BorderLayout.SOUTH);
		setSize(500,500);
	}

	/******************** METODO Filter() : Panel Filtro de Inf. ********************/

	public void Filter() {

		base = new JPanel();
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
		JPanel row1 = new JPanel();
		row1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel row2 = new JPanel();
		row2.setLayout(new FlowLayout(FlowLayout.LEFT));
		CheckBoxListener myListener = new CheckBoxListener();
		String[] datmp = {""};

		checkBox1 = new JCheckBox(Language.getWord("FILTER")+":");
		checkBox1.setMnemonic('F'); 
		checkBox1.addItemListener(myListener);

		checkBox2 = new JCheckBox(Language.getWord("LIMIT")+":");
		checkBox2.setMnemonic('L'); 
		checkBox2.addItemListener(myListener);

		combo1 = new JComboBox(datmp);
		combo2 = new JComboBox(datmp);
		combo3 = new JTextField(10);

		combo1.setActionCommand("COMBO1");
		combo1.addActionListener(this);
		combo2.setActionCommand("COMBO2");
		combo2.addActionListener(this);

		JPanel space = new JPanel();

		advanced = new JButton(Language.getWord("OPC"));
		advanced.setActionCommand("Options");
		advanced.addActionListener(this);

		JMenuItem Item = new JMenuItem(Language.getWord("DSPLY"));
		Item.setActionCommand("DISPLAY");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("ADF")); 
		Item.setActionCommand("ADVANCED");
		Item.addActionListener(this);
		popup.add(Item);

		Item = new JMenuItem(Language.getWord("CUF")); 
		Item.setActionCommand("CUSTOMIZE");
		Item.addActionListener(this);
		popup.add(Item);

		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!popup.isVisible() && advanced.isEnabled())
					popup.show(advanced,90,0);
			}
		};
		advanced.addMouseListener(mouseListener);

		button = new JButton(Language.getWord("UPDT"));
		button.setActionCommand("REFRESH");
		button.addActionListener(this);

		JPanel row0 = new JPanel();
		row0.setLayout(new FlowLayout(FlowLayout.CENTER));
		row0.add(button);
		row0.add(advanced);

		row1.add(checkBox1); 
		row1.add(combo1);
		row1.add(combo2);
		row1.add(combo3);
		row1.add(space);

		numRegTextField = new JTextField(7);
		msg1 = new JLabel(Language.getWord("STARTR")+":"); 
		limitText = new JTextField(7);
		msg2 = new JLabel(Language.getWord("LRW"));

		row2.add(checkBox2);
		row2.add(msg1);
		row2.add(numRegTextField);
		row2.add(msg2);
		row2.add(limitText);

		setRow1(false);
		setRow2(false);

		JPanel right = new JPanel();
		right.setLayout(new BorderLayout());
		right.add(row2,BorderLayout.WEST);

		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
		groupPanel.add(row1);
		groupPanel.add(right);

		Border etched1 = BorderFactory.createEtchedBorder();
		TitledBorder title1 = BorderFactory.createTitledBorder(etched1);

		groupPanel.setBorder(title1);

		base.add(row0);
		base.add(groupPanel); 
	}

	/******************** METODO pieDatos() : Texto de Operacion ********************/
	public void footer() {
		firstPanel = new JPanel();
		firstPanel.setLayout(new BorderLayout());
		Filter();
		firstPanel.add(base,BorderLayout.CENTER);
	}

	/******************** METODO CreateToolBar() : Crea Barra de Iconos ********************/
	public void createToolBar() {
		URL imgURL = getClass().getResource("/icons/16_InsertRecord.png"); 
		insertButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		insertButton.setActionCommand("INSERT-RECORD");
		insertButton.addActionListener(this);
		insertButton.setToolTipText(Language.getWord("INSREC"));
		toolBar.add(insertButton);

		imgURL = getClass().getResource("/icons/16_DelRecord.png");
		deleteButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		deleteButton.setActionCommand("DELETE-RECORD");
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText(Language.getWord("DELREC"));
		deleteButton.setEnabled(false);
		toolBar.add(deleteButton);

		imgURL = getClass().getResource("/icons/16_UpdateRecord.png");
		updateButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		updateButton.setActionCommand("UPDATE-RECORD");
		updateButton.addActionListener(this);
		updateButton.setToolTipText(Language.getWord("UPDREC"));
		updateButton.setEnabled(false);
		toolBar.add(updateButton);

		imgURL = getClass().getResource("/icons/16_ExportFile.png");
		exportButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		exportButton.setActionCommand("EXPORT-TO-FILE");
		exportButton.addActionListener(this);
		exportButton.setToolTipText(Language.getWord("EXPORTAB") + "/" + Language.getWord("ITT"));
		exportButton.setEnabled(false);
		toolBar.add(exportButton);

		imgURL = getClass().getResource("/icons/16_NewTable.png");
		reportButton = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		reportButton.setActionCommand("EXPORT-REPORT");
		reportButton.addActionListener(this);
		reportButton.setToolTipText(Language.getWord("EXPORREP"));
		reportButton.setEnabled(false);
		toolBar.add(reportButton);
	}

	/******************** METODO actionPerformed() : Manejador de Eventos ********************/
	public void actionPerformed(java.awt.event.ActionEvent e) {

		if (e.getActionCommand().equals("UPDATE-POP")) {
			updateRecords();
			return;
		}

		if (e.getActionCommand().equals("DELETE-POP")) {
			dropRecords();
			return;
		}

		if (e.getActionCommand().equals("INSERT-RECORD")) {
			insertRecords();
			return;
		}

		if (e.getActionCommand().equals("DELETE-RECORD") ) {

			table.clearSelection();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			DropTableRecord eraser = new DropTableRecord(realTableName,tableStruct,frame);
			eraser.setLocationRelativeTo(frame);
			eraser.setVisible(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			if (eraser.isWellDone()) {

				addTextLogMonitor(Language.getWord("EXEC")+ eraser.getSQL() + "\"");
				String result = pgconnection.executeSQL(eraser.getSQL());

				if (result.equals("OK")) {
					result = refreshAfterDrop(result);
				}
				else {
					result = result.substring(0,result.length()-1);
					ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(frame);
					showError.setVisible(true);
				}

				addTextLogMonitor(Language.getWord("RES") + result);
			}

			return;
		}

		if (e.getActionCommand().equals("UPDATE-RECORD") ) {

			table.clearSelection();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			UpdateTable upper = new UpdateTable(realTableName,tableStruct,frame);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			if (upper.getResult()) {

				addTextLogMonitor(Language.getWord("EXEC")+ upper.getUpdate() + "\"");
				String result = pgconnection.executeSQL(upper.getUpdate());

				if (result.equals("OK")) {

					String sql = "SELECT " + recordFilter + " FROM " + realTableName;

					if (whereClausule.length()!=0) {
						sql = "SELECT " + recordFilter + " FROM " + realTableName + whereClausule;
					}

					recordsCount = countRecords(realTableName,pgconnection,sql,true);

					if (recordsCount > 50) {
						sql = "SELECT * FROM (" + sql + ") AS foo LIMIT " + limit + " OFFSET " + start;
					}

					Vector res = pgconnection.getResultSet(sql);
					Vector col = pgconnection.getTableHeader();

					if (!pgconnection.queryFail()) {
						showQueryResult(res,col);
						updateUI(); 
					}    
				}
				else {
					result = result.substring(0,result.length()-1);
					ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(frame);

					showError.setVisible(true);
				}

				addTextLogMonitor(Language.getWord("RES") + result);
			}

			return;
		}

		if (e.getActionCommand().equals("EXPORT-TO-FILE")) {

			ExportToFile dialog = new ExportToFile(frame,recordsTotal);

			int option = 0;

			if (dialog.isWellDone()) {

				option = dialog.getOption();
				String s = "file:" + System.getProperty("user.dir");
				File file;
				boolean Rewrite = true;
				String FileName = "";
				int returnVal;
				JFileChooser fc;

				switch(option) {

				case 1: 
					fc = new JFileChooser(s);

					returnVal = fc.showDialog(frame,Language.getWord("EXPORTAB"));

					if (returnVal == JFileChooser.APPROVE_OPTION) {

						file = fc.getSelectedFile();
						FileName = file.getAbsolutePath(); // Camino Absoluto

						if (file.exists()) {

							GenericQuestionDialog win = new GenericQuestionDialog(frame,Language.getWord("YES"),
									Language.getWord("NO"),Language.getWord("ADV"),
									Language.getWord("FILE") + " \"" + FileName 
									+ "\" " + Language.getWord("SEQEXIS2") + " " 
									+ Language.getWord("OVWR"));
							Rewrite = win.getSelecction();
						}

						if (Rewrite) {

							try {
								ExportSeparatorField little = new ExportSeparatorField(frame);

								if (little.isDone()) {

									String limiter = little.getLimiter();
									PrintStream saveFile = new PrintStream(new FileOutputStream(FileName));
									String sentence = "SELECT * FROM " + realTableName;
									Vector resultGlobal = pgconnection.getResultSet(sentence);
									Vector columnNamesG = pgconnection.getTableHeader();
									String val = "OK";

									if (pgconnection.queryFail()) {
										val = pgconnection.problem;
										val = val.substring(0,val.length()-1);
									}

									addTextLogMonitor(Language.getWord("EXEC") + sentence + "\"");
									addTextLogMonitor(Language.getWord("RES") + val);
									printFile(saveFile,resultGlobal,columnNamesG,limiter);
								} // fin if

							} // fin try
							catch (Exception ex) { System.out.println("Error: " + ex); }
						} // fin if
					} // fin if 
					return;

				case 2:
					fc = new JFileChooser(s);

					returnVal = fc.showDialog(frame,Language.getWord("LFILE"));

					if (returnVal == JFileChooser.APPROVE_OPTION) {

						file = fc.getSelectedFile();
						FileName = file.getAbsolutePath(); // Camino Absoluto

						ImportSeparatorField little = new ImportSeparatorField(frame);

						if (little.isDone()) {

							String limiter = little.getLimiter();

							try {
								BufferedReader buffer = new BufferedReader(new FileReader(file));
								String firstReg = buffer.readLine(); 
								Vector<Vector> data = new Vector<Vector>();
								int index = firstReg.indexOf(limiter);

								if (index != -1) {

									StringTokenizer filter = new StringTokenizer(firstReg,limiter);
									int i = 0;
									Vector<String> tuple = new Vector<String>();

									while (filter.hasMoreTokens()) {
										i++;
										String tmp = filter.nextToken();
										tuple.addElement(tmp);
									} 

									int k = tableStruct.getTableHeader().getNumFields();

									if (i == k) {
										data.addElement(tuple); 

										while (true) {

											String line = buffer.readLine();

											if (line == null) 
												break;

											StringTokenizer filterFile = new StringTokenizer(line,limiter);
											int counter = 0;
											tuple = new Vector<String>();

											while (filterFile.hasMoreTokens()) {

												counter += 1;
												String tmp = filterFile.nextToken();
												tuple.addElement(tmp);
											}

											data.addElement(tuple);
										}

										buildSQLRecords(currentTable,tableStruct.getTableHeader(),data);
									}
									else {

										JOptionPane.showMessageDialog(new JDialog(),
												Language.getWord("NCNNA"),
												Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
										return;
									}
								} 
								else {
									JOptionPane.showMessageDialog(new JDialog(),
											Language.getWord("SEPNF"),
											Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
									return;
								}
							} 
							catch (Exception ex) {
								System.out.println("Error" + ex);
							}
						}
					}
					return;
				default:
				}
			}

			return;
		}

		if (e.getActionCommand().equals("EXPORT-REPORT")) {

			Vector dataRecords = new Vector();
			Vector columns = new Vector();

			if (totalReg <= 50) {

				addTextLogMonitor(Language.getWord("EXEC") + "SELECT " + recordFilter + " FROM " + realTableName + ";\"");
				dataRecords = pgconnection.getResultSet("SELECT " + recordFilter + " FROM " + realTableName);
				columns = pgconnection.getTableHeader();
				String str = "OK";

				if (!pgconnection.queryFail()) {
					addTextLogMonitor(Language.getWord("RES") + str); 
					new ReportDesigner(frame,columns,dataRecords,logArea,currentTable,pgconnection);
				} else {
					str = pgconnection.getProblemString().substring(0,pgconnection.getProblemString().length()-1);
					addTextLogMonitor(Language.getWord("RES") + str);
				}

				return;
			}

			ExportToReport dialog = new ExportToReport(frame);
			int option = 0;

			if (dialog.isWellDone()) {

				option = dialog.getOption();

				String sql = "SELECT " + recordFilter + " FROM " + realTableName;

				switch (option) {

				case 1: if (whereClausule.length()>0) 
					sql += whereClausule;  
				sql = "SELECT * FROM (" + sql + ") AS foo ORDER BY " + orderBy + " LIMIT " + limit + " OFFSET " + start; 
				break;

				case 2: sql += whereClausule;
				break;

				case 3: sql += " ORDER BY " + orderBy;
				}

				addTextLogMonitor(Language.getWord("EXEC") + sql + ";\"");

				dataRecords = pgconnection.getResultSet(sql);
				columns = pgconnection.getTableHeader();

				String str = "OK";

				if (!pgconnection.queryFail()) {

					addTextLogMonitor(Language.getWord("RES") + str);
					new ReportDesigner(frame,columns,dataRecords,logArea,currentTable,pgconnection);
				}
				else {
					str = pgconnection.getProblemString().substring(0,pgconnection.getProblemString().length()-1);
					addTextLogMonitor(Language.getWord("RES") + str);

					ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(frame);
					showError.setVisible(true);
				}
			}

			return;
		}

		if (e.getActionCommand().equals("COMBO1")) {

			JComboBox cb = (JComboBox)e.getSource();
			int pos = cb.getSelectedIndex();

			if (pos < 0)
				pos = 0;

			field = (String) columnNamesVector.elementAt(pos);

			if (field == null || field.length() == 0) {

				field = firstField; 
				pos = columnNamesVector.indexOf(firstField);
				cb.setSelectedIndex(pos);
			}

			if (!field.equals("oid")) {

				String type = tableStruct.getTableHeader().getType(field);

				String[] varcharOpc = {"=","!=","<",">","<=",">=","like","not like","~","~*","!~","!~*"};
				String[] boolOpc = {"=","!="};
				String[] intOpc = {"=","!=","<",">","<=",">="};

				combo2.removeAllItems();

				if (type.startsWith("bool")) {

					for (int i=0;i<boolOpc.length;i++)
						combo2.addItem(boolOpc[i]);

					return;
				}

				if (type.startsWith("int") || type.startsWith("serial") || type.startsWith("smallint")
						|| type.startsWith("real") || type.startsWith("double") || type.startsWith("float")) {

					for (int i=0;i<intOpc.length;i++)
						combo2.addItem(intOpc[i]);

					return;
				}

				for (int i=0;i<varcharOpc.length;i++)
					combo2.addItem(varcharOpc[i]);

			}
			else {
				combo2.removeAllItems();
				String[] ig = {"=","!=","<",">","<=",">="};

				for (int i=0;i<ig.length;i++)
					combo2.addItem(ig[i]);
			}

			return;
		} 

		if (e.getActionCommand().equals("COMBO2")) {

			JComboBox cb = (JComboBox)e.getSource();
			operator = (String) cb.getSelectedItem();

			return;
		}

		if (e.getActionCommand().equals("DISPLAY")) {

			boolean keepOn = false;
			Hashtable<String,String> hashTable = new Hashtable<String,String>();

			if (dbHash.containsKey(pgconnection.getDBname())) {
				hashTable = (Hashtable<String,String>) dbHash.get(pgconnection.getDBname());
				if (hashTable.containsKey(currentTable)) { 
					keepOn = true;
					recordFilter = (String) hashTable.get(currentTable); 
				}
				else 
					recordFilter = "*";
			}
			else 
				recordFilter = "*";

			if (checkBox2.isSelected())
				checkBox2.setSelected(false);

			if (checkBox1.isSelected())
				checkBox1.setSelected(false);

			DisplayControl regListPanel = new DisplayControl(tableStruct,frame,recordFilter,keepOn);

			if (regListPanel.isWellDone()) {

				recordFilter = regListPanel.getFilter();

				hashTable.remove(currentTable);
				dbHash.remove(pgconnection.getDBname());

				if (recordFilter.length() == 0) {
					recordFilter = "*";
					dbHash.put(pgconnection.getDBname(),hashTable);
				}
				else {
					if (regListPanel.isKeepIt()) { 

						hashTable.put(currentTable,recordFilter);
						dbHash.put(pgconnection.getDBname(),hashTable);
					}
					else
						dbHash.put(pgconnection.getDBname(),hashTable);

					refreshTable();
				}

			}

			return;
		}

		if (e.getActionCommand().equals("ADVANCED")) {

			if (checkBox2.isSelected())
				checkBox2.setSelected(false);

			if (checkBox1.isSelected())
				checkBox1.setSelected(false);

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			AdvancedFilter button = new AdvancedFilter(tableStruct,frame);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			if (button.isWellDone()) {

				sentence = button.getSelect();

				if (button.isThereOrder())
					orderBy = button.getOrder();

				recordsCount = countRecords(currentTable,pgconnection,sentence,true);

				if (recordsCount > 50)
					sentence = "SELECT * FROM (" + sentence + ") AS foo LIMIT 50 OFFSET " + start;

				Vector res = pgconnection.getResultSet(sentence);
				Vector col = pgconnection.getTableHeader();

				addTextLogMonitor(Language.getWord("EXEC") + sentence + ";\"");
				String str = "OK";

				if (!pgconnection.queryFail()) {

					showQueryResult(res,col);
					updateUI(); 
				}
				else {
					str = pgconnection.getProblemString().substring(0,pgconnection.getProblemString().length()-1);
					ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(frame);

					showError.setVisible(false);
				}     

				addTextLogMonitor(Language.getWord("RES") + str);
			}

			return;
		} 

		if (e.getActionCommand().equals("CUSTOMIZE")) {

			if (checkBox2.isSelected())
				checkBox2.setSelected(false);

			if (checkBox1.isSelected())
				checkBox1.setSelected(false);

			CustomizeFilter custim = new CustomizeFilter(tableStruct,frame);

			if (custim.isWellDone()) {

				sentence = custim.getSelect();
				String order = custim.getOrder();

				if (order.length() > 0)
					orderBy = order;

				recordsCount = countRecords(currentTable,pgconnection,sentence,true);

				if (recordsCount > 50)
					sentence = "SELECT * FROM (" + sentence + ") AS foo LIMIT 50 OFFSET " + start;

				Vector res = pgconnection.getResultSet(sentence);
				Vector col = pgconnection.getTableHeader();
				String str = "OK";
				addTextLogMonitor(Language.getWord("EXEC") + sentence + ";\"");

				if (!pgconnection.queryFail())  {

					showQueryResult(res,col);
					updateUI(); 
				}    
				else {
					str = pgconnection.problem.substring(0,pgconnection.problem.length()-1);
					ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(Records.this);
					showError.setVisible(true);
				}

				addTextLogMonitor(Language.getWord("RES") + str);
			}

			return;
		}

		if (e.getActionCommand().equals("REFRESH")) {

			refreshOn = true;
			sentence = "SELECT " + recordFilter + " FROM \"" + currentTable + "\"";
			whereClausule = "";

			if (checkBox1.isSelected()) {

				String var = combo3.getText();

				if (var.length()==0) {

					JOptionPane.showMessageDialog(frame,                               
							Language.getWord("ERRFIL"),                       
							Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

					return;
				}
				else 
				{
					String type = "int";

					if (!field.equals("oid"))
						type = tableStruct.getTableHeader().getType(field);

					int code = getTypeCode(type);

					switch (code) {
					case 1:
						if (!var.startsWith("'"))
							var = "'" + var;

						if (!var.endsWith("'"))
							var = var + "'";

						combo3.setText(var);
						break;
					case 2:
						if (!isANumber(var)) {
							JOptionPane.showMessageDialog(frame,
									Language.getWord("FINTIV"),
									Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

							combo3.setText("");
							combo3.requestFocus();

							return;
						}
						break;
					case 3:
						var = var.toLowerCase();
						if (!var.equals("true") && !var.equals("false")) {
							JOptionPane.showMessageDialog(frame,
									Language.getWord("IBT"),
									Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);

							combo3.setText("");
							combo3.requestFocus();

							return;
						}
					} // fin switch

					sentence += " WHERE \"" + field + "\" " + operator + " " + var;
					whereClausule += " WHERE \"" + field + "\" " + operator + " " + var;

				} // fin else
			}

			//sentence += " ORDER BY " + orderBy;
			//where += " ORDER BY " + orderBy;

			if (checkBox2.isSelected()) {

				int fail = 0;
				boolean firstvalid = false;
				boolean secondvalid = false;
				String num = numRegTextField.getText();
				String limit = limitText.getText();

				if (num.length()>0) {

					if (isANumber(num))
						firstvalid = true;
					else {
						JOptionPane.showMessageDialog(frame,                               
								Language.getWord("ERRLIM") + " 1 " + Language.getWord("ERRLIM2"),                       
								Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				else
					fail += 1;

				if (limit.length()>0) {

					if (isANumber(limit))
						secondvalid = true;
					else {
						JOptionPane.showMessageDialog(frame,                               
								Language.getWord("ERRLIM") + " 2 " + Language.getWord("ERRLIM2"),                       
								Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				else
					fail += 1;

				if (fail==2) {

					JOptionPane.showMessageDialog(frame,                               
							Language.getWord("LIMUS"),                       
							Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
					return;
				}
				else {

					if (fail==1) {

						JOptionPane.showMessageDialog(frame,                               
								Language.getWord("LIM1US"),                       
								Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
						if (firstvalid && secondvalid) {

							int a = Integer.parseInt(num);
							int b = Integer.parseInt(limit);

							if (a <= b) {
								int numrows = (b - a) + 1;
								sentence += " LIMIT " + numrows + " OFFSET " + num;
								whereClausule += " LIMIT " + numrows + " OFFSET " + num;
							}
							else {
								JOptionPane.showMessageDialog(frame,                               
										Language.getWord("MORELIM"),                       
										Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
								return;
							} // fin else
						} // fin if
				} // fin else

			}

			recordsCount = countRecords(currentTable,pgconnection,sentence,true);

			if (recordsCount > 50) 
				sentence = "SELECT * FROM (" + sentence + ") AS foo LIMIT 50";

			sentence += ";";

			Vector res = pgconnection.getResultSet(sentence);
			addTextLogMonitor(Language.getWord("EXEC") + sentence + "\"");
			Vector col = pgconnection.getTableHeader();

			if (!pgconnection.queryFail()) {

				showQueryResult(res,col);
				updateUI(); 
			}
			else {
				String resStr = pgconnection.getProblemString();
				addTextLogMonitor(Language.getWord("ERRONRUN") + resStr.substring(0,resStr.length()-1));
			}
		}
	}

	/**
	 * METODO activeInterface()  
	 * Activa o desactiva los Botones
	 */ 
	public void activeInterface(boolean value) {

		insertButton.setEnabled(value);
		updateButton.setEnabled(value);
		checkBox1.setEnabled(value);
		checkBox2.setEnabled(value);
		button.setEnabled(value);
		exportButton.setEnabled(value);

		if (!value) {

			connected = false;
			title.setText(Language.getWord("DSCNNTD"));
			showQueryResult(new Vector(),new Vector());
			dbHash.clear();
			advanced.setEnabled(false);

			onMem.setText(" " + Language.getWord("ONMEM") + " : 0 "); 
			onScreen.setText(" " + Language.getWord("ONSCR") + " : 0 ");
			onTable.setText(" " + Language.getWord("TOTAL") + " : 0 ");
			currentStatistic.setText(" " + Language.getWord("DSCNNTD") + " "); 

			if (backButton.isEnabled()) {

				backButton.setEnabled(false);
				queryLeft.setEnabled(false);
			}

			if (forwardButton.isEnabled()) {

				queryRight.setEnabled(false);
				forwardButton.setEnabled(false);
			}
		}
		else
			connected = true;
	}                                                                              

	public void setLabel(String dbName,String table,String owner) {

		currentTable = table;
		String mesg = "";

		if (dbName.length()>0) {
			//String regTitle = Language.getWord("RECS");
			mesg = Language.getWord("TABLE") + ": '" + table + "' [Owner: " + owner + " / DB:" + dbName + "]";  
		}
		else
			mesg = Language.getWord("NOSELECT");

		title.setText(mesg);
	}

	public void activeBox(boolean state) {

		checkBox1.setEnabled(state);
		checkBox2.setEnabled(state);
		advanced.setEnabled(state);
	}

	public void setRecordFilter(String TableN,String DBName) {
		if (dbHash.containsKey(DBName)) {
			hashRecordFilter = (Hashtable) dbHash.get(DBName);
			recordFilter = (String) hashRecordFilter.get(TableN);
			if (recordFilter == null) {
				recordFilter = "*";
			}
		}
		else
			recordFilter = "*";
	}

	public boolean updateTable(PGConnection connection, String tableName, Table table) {

		realTableName = "\"" + tableName + "\"";

		if (connection.gotUserSchema(tableName)) { 
			realTableName = connection.getSchemaName(tableName) + "." + realTableName;
		}

		//orderBy = "oid";

		if (backButton.isEnabled()) {

			backButton.setEnabled(false);
			queryLeft.setEnabled(false);
		}

		if (dbHash.containsKey(connection.getDBname())) {

			hashRecordFilter = (Hashtable<String,String>) dbHash.get(connection.getDBname());
			recordFilter = (String) hashRecordFilter.get(tableName);

			if (recordFilter == null)
				recordFilter = "*";
		}
		else
			recordFilter = "*";

		whereClausule = "";

		String sentence = "SELECT " + recordFilter + " FROM " + realTableName + ";";

		recordsCount = countRecords(realTableName,connection,"",false);

		if (recordsCount == -1)
			return false;

		if (recordsCount > 50) {

			sentence = "SELECT " + recordFilter + " FROM " + realTableName + " LIMIT 50;";
			indexMax = 50;
			indexMin = 1;
		}

		currentPage = 1;

		pgconnection = connection;
		tableStruct = table;
		//currentTable = TableN;
		currentTable = realTableName;
		checkBox1.setSelected(false);
		checkBox2.setSelected(false);
		combo3.setText("");
		numRegTextField.setText("");
		limitText.setText("");

		String answer = "OK";
		Vector result = pgconnection.getResultSet(sentence);
		columnNamesVector = pgconnection.getTableHeader();

		if (!pgconnection.queryFail()) {

			String owner = pgconnection.getOwner(tableName);
			setLabel(pgconnection.getDBname(),tableName,owner);

			if (result.size()==0) 
				activeBox(false);
			else { 
				activeBox(true);
				button.setEnabled(true);
			}

			combo1.removeAllItems();
			firstField = "";

			for (int t=0;t<columnNamesVector.size();t++) {

				String element = (String) columnNamesVector.elementAt(t);

				if (element.length() > 25)
					element = element.substring(0,25) + "...";

				combo1.insertItemAt(element, t); 

				if (t == 0 && element != null && element.length()>0)
					firstField = element; 

				if ((firstField == null) || (element.length()==0))
					firstField = element;
			}

			combo1.setSelectedIndex(0);

			showQueryResult(result,columnNamesVector);
			insertButton.setEnabled(true);
			exportButton.setEnabled(true);
		}
		else {
			checkBox1.setEnabled(false);
			checkBox2.setEnabled(false);
			insertButton.setEnabled(false);
			combo1.removeAllItems();
			combo1.insertItemAt("",0);
			showQueryResult(new Vector(),new Vector());
			title.setText(Language.getWord("NRE"));

			answer = pgconnection.getProblemString().substring(0,pgconnection.getProblemString().length()-1);

			ErrorDialog showError = new ErrorDialog(new JDialog(), connection.getErrorMessage());
			showError.pack();
			showError.setLocationRelativeTo(frame);
			showError.setVisible(true);
		}

		addTextLogMonitor(Language.getWord("EXEC")+ sentence + "\"");
		addTextLogMonitor(Language.getWord("RES") + answer);
		updateUI();

		return true;
	}


	public void showQueryResult(Vector rowData,Vector columnsVector) {

		int resultSize = rowData.size();
		boolean flag = false;
		if (oldMem != recordsCount) {
			onMem.setText(" " + Language.getWord("ONMEM") + " : " + recordsCount + " ");
			oldMem = recordsCount;
			flag = true;
		}

		if (recordsCount == 0) { 
			nPages = 1;
			indexMin = 0;
			indexMax = 0;

			if (queryRight.isEnabled()) {
				queryRight.setEnabled(false);
				forwardButton.setEnabled(false);
			}

			if (queryLeft.isEnabled()) {
				queryLeft.setEnabled(false);
				backButton.setEnabled(false);
			}
		}
		else {
			if (recordsCount <= 50) {
				nPages = 1;
				indexMin = 1; 
				indexMax = recordsCount;

				if (queryRight.isEnabled()) {
					queryRight.setEnabled(false);
					forwardButton.setEnabled(false);
				}

				if (queryLeft.isEnabled()) {
					queryLeft.setEnabled(false);
					backButton.setEnabled(false);
				}
			} else {
				nPages = getPagesNumber(recordsCount);
				if (nPages == 1 && recordsCount != 0) {
					indexMax = recordsCount;
				}
				if (nPages == currentPage) {
					indexMax = recordsCount;
					if (queryRight.isEnabled()) {
						queryRight.setEnabled(false);
						forwardButton.setEnabled(false);
					}
				} else {
					if (nPages > currentPage) {
						if (nPages > 1) {
							if (!queryRight.isEnabled()) {
								queryRight.setEnabled(true);
								forwardButton.setEnabled(true);
							}
							if (indexMax < 50) {
								indexMax = 50;
							}
						}
						else {
							if (queryRight.isEnabled()) {
								queryRight.setEnabled(false);
								forwardButton.setEnabled(false);
							}
						}
					}
				}
			}
		}

		if (oldPage != currentPage || !oldTable.equals(currentTable) || flag || refreshOn ) {
			if (refreshOn) { 
				refreshOn = false;
				currentPage = 1;
				if (recordsCount > 50) {
					indexMax = 50;
					indexMin = 1;
					if (!queryRight.isEnabled()) {
						queryRight.setEnabled(true);
						forwardButton.setEnabled(true);
					}
				}
				else {
					if (queryRight.isEnabled()) {
						queryRight.setEnabled(false);
						forwardButton.setEnabled(false);
					}
					if (recordsCount == 0) {
						indexMax = indexMin = 0;
					}
					else {
						indexMin = 1;
						indexMax = recordsCount;
					}
				}
				if (queryLeft.isEnabled()) {
					queryLeft.setEnabled(false);
					backButton.setEnabled(false);
				}
			}

			flag = false;

			if (!oldTable.equals(currentTable)) {
				oldTable = currentTable;
				whereClausule = "";

				if (recordsCount > 0) {
					indexMin = 1;
					if (recordsCount <= 50) {
						indexMax = recordsCount;
						nPages = 1;
					}
					else {
						indexMax = 50;
					}
				}
				else {
					indexMin = 0;
					indexMax = 0;
					nPages = 1;
				}

				currentPage = 1;
			}

			currentStatistic.setText(" " + Language.getWord("PAGE") + " " + currentPage + " " + Language.getWord("OF")
					+ " " + nPages + " [ " + Language.getWord("RECS")
					+ " " + Language.getWord("FROM") + " " + indexMin + " " + Language.getWord("TO")
					+ " " + indexMax + " " + Language.getWord("ONMEM") + " ] ");
			oldPage = currentPage;
		}

		if (!firstTime && connected) {
			int tuples = countRecords(currentTable,pgconnection,"",true);
			if (totalReg != tuples) {
				onTable.setText(" " + Language.getWord("TOTAL") + " : " + tuples + " ");
				totalReg = tuples;
			}
		}
		else  {
			firstTime = false;
		}

		if (resultSize != recordsTotal) {
			onScreen.setText(" " + Language.getWord("ONSCR") + " : " + resultSize + " ");
			recordsTotal = resultSize;
		}

		String[] columnsArray = new String[columnsVector.size()];
		Object[][] objectsMatrix = new Object[rowData.size()][columnsVector.size()];

		if (columnsVector.size()>0) {
			for (int p=0;p<columnsVector.size();p++) {
				Object o = columnsVector.elementAt(p);
				columnsArray[p] = o.toString();
			}

			for (int p=0;p<rowData.size();p++) {
				Vector tempo = (Vector) rowData.elementAt(p);
				for (int j=0;j<columnsVector.size();j++) {
					Object o = tempo.elementAt(j);
					objectsMatrix[p][j] = o;
				}
			}
		}

		if (resultSize > 0) {
			int tableWidth = table.getWidth();
			myModel = new RecordsTableModel(objectsMatrix,columnsArray);
			table = new JTable(myModel);
			table.setPreferredScrollableViewportSize(new Dimension(tableWidth, 70));
			table.addFocusListener(this);
			addFocusListener(this);

			String value = objectsMatrix[0][0].toString();
			int dataLength = value.length();
			dataLength = dataLength*10;

			DefaultTableCellRenderer renderer = new ColoredTableCellRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);

			//Personalizar ancho de columnas
			TableColumn column = table.getColumnModel().getColumn(0);
			column.setPreferredWidth(dataLength);
			//column.setMaxWidth(dataLength);
			//column.setCellRenderer(renderer);

			int width = (tableWidth - dataLength) / columnsVector.size() - 1; 
			for (int p=1;p<columnsVector.size();p++) {
				column = table.getColumnModel().getColumn(p);
				column.setPreferredWidth(width);

				String type = tableStruct.getTableHeader().getType((String) columnsVector.elementAt(p));
				int code = getTypeCode(type);
				DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();

				switch(code) {
				case 2:  
					cellRenderer.setHorizontalAlignment(JLabel.RIGHT);
					break;
				case 3:  
					cellRenderer.setHorizontalAlignment(JLabel.CENTER); 
					break;
				default: 
					cellRenderer.setHorizontalAlignment(JLabel.LEFT);
				}
				column.setCellRenderer(cellRenderer);
			}

			final JPopupMenu popup = new JPopupMenu();

			JMenuItem item = new JMenuItem("Update");
			item.setFont(new Font("Helvetica", Font.PLAIN, 10));
			item.setActionCommand("UPDATE-POP");
			item.addActionListener(this);
			popup.add(item);

			item = new JMenuItem("Delete");
			item.setFont(new Font("Helvetica", Font.PLAIN, 10));
			item.setActionCommand("DELETE-POP");
			item.addActionListener(this);
			popup.add(item);

			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					int[] row = table.getSelectedRows();

					if (row.length > 0) {
						if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)
							&& row[0] != -1) {
							if (!popup.isVisible()) {
								popup.show(table,e.getX(),e.getY());
							}
						}
					}

					if (e.getClickCount() == 2) { 
						updateRecords();
					}
				}
			});

			deleteButton.setEnabled(true);
			updateButton.setEnabled(true);
			exportButton.setEnabled(true); 
			reportButton.setEnabled(true);
		}
		else {
			deleteButton.setEnabled(false);
			updateButton.setEnabled(false);
			reportButton.setEnabled(false);
			table = new JTable(rowData,columnsVector);
		}

		if (!firstBool) {
			remove(mainScrollpane);
		} else {
			firstBool = false;
		}

		mainScrollpane = new JScrollPane(table);
		add(mainScrollpane,BorderLayout.CENTER);
	}

	class RecordsTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		public RecordsTableModel(Object[][] xdata,String[] colN) {
			data = xdata;
			columnNames = colN;
		}

		public String getColumnName(int col) {
			return columnNames[col].toString();
		}

		public int getRowCount() {
			return data.length; 
		}

		public int getColumnCount() {
			return columnNames.length; 
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col); 
		}

	}

	public void setRow1(boolean state) {
		combo1.setEnabled(state);  
		combo2.setEnabled(state);
		combo3.setEnabled(state);
	}

	public void setRow2(boolean state) {
		numRegTextField.setEnabled(state);
		msg1.setEnabled(state);
		limitText.setEnabled(state);
		msg2.setEnabled(state);
	}

	class CheckBoxListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			if (source == checkBox1) {
				if (checkBox1.isSelected()) {
					setRow1(true);
					combo1.setSelectedIndex(1);
				}
				else {
					setRow1(false);
				}
				combo2.setSelectedIndex(0);
			} 

			if (source == checkBox2) {
				if (checkBox2.isSelected()) {
					setRow2(true);
				}
				else {
					setRow2(false);
					numRegTextField.setText("");
					limitText.setText("");
				}
			}
		}
	}

	public boolean isANumber(String word) {
		for (int i=0;i<word.length();i++) {
			char c = word.charAt(i);
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	public int countRecords(String tableName,PGConnection connection,String sql, boolean log) {
		int value = -1;
		if (tableName.indexOf("\"")==-1 && connection.gotUserSchema(tableName)) {
			String schema = connection.getSchemaName(tableName); 
			tableName = schema + ".\"" + tableName + "\"";
		}

		String counting = "SELECT count(*) FROM " + tableName + ";";

		if (sql.length()>0) {
			if (sql.endsWith(";")) {
				sql = sql.substring(0,sql.length()-1);
			}
			counting = "SELECT count(*) FROM (" + sql + ") AS foo;";
		}

		String answer = "OK";
		if (log) {
			addTextLogMonitor(Language.getWord("EXEC")+ counting + "\"");
		}
		
		Vector result = new Vector();
		result = connection.getResultSet(counting);
		
		if (connection.queryFail()) {
			if (log) {
				answer = connection.getProblemString().substring(0,connection.getProblemString().length()-1);
				addTextLogMonitor(Language.getWord("RES") + answer);
			}
			ErrorDialog showError = new ErrorDialog(new JDialog(),connection.getErrorMessage());
			showError.pack();
			showError.setLocationRelativeTo(frame);
			showError.setVisible(true);
		}
		else {
			Vector recordsVector = (Vector) result.elementAt(0);
			try {
				Long integer = (Long) recordsVector.elementAt(0);   
				value = integer.intValue();
			}
			catch(Exception ex){
				Integer integer = (Integer) recordsVector.elementAt(0);
				value = integer.intValue();
			}
			if (log) {
				addTextLogMonitor(Language.getWord("RES") + value + " " + Language.getWord("RECS"));
			}
		}

		return value;
	}

	public int getTypeCode(String typeStr) {

		if (typeStr.startsWith("varchar") || typeStr.startsWith("char") || typeStr.startsWith("text") 
				|| typeStr.startsWith("name") || typeStr.startsWith("date") || typeStr.startsWith("time"))

			return 1;

		if (typeStr.startsWith("int") || typeStr.equals("serial") || typeStr.equals("smallint") 
				|| typeStr.equals("real") || typeStr.equals("double"))

			return 2;

		if (typeStr.startsWith("bool"))

			return 3;
		else
			return 4;
	}

	/**
	 * Metodo addTextLogMonitor
	 * Imprime mensajes en el Monitor de Eventos
	 */
	public void addTextLogMonitor(String msg) {
		logArea.append(msg + "\n");
		int length = logArea.getDocument().getLength();
		if(length > 0)
			logArea.setCaretPosition(length - 1);
	}

	/**
	 * Metodo getNumRegs
	 * Retorna el numero de registros de la tabla
	 */
	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void printFile(PrintStream file,Vector registers,Vector fieldsVector,String separator) {
		String limit = "";
		boolean useFormatCSV = false;

		try {
			int tableWidth = fieldsVector.size();
			if (separator.equals("csv")) {
				limit = ",";
				useFormatCSV = true;
			}
			else {
				limit = separator;
			}

			for (int p=0;p<registers.size();p++) {
				Vector rData = (Vector) registers.elementAt(p);
				for (int i=0;i<tableWidth;i++) {
					Object o = rData.elementAt(i);
					String field = "NULL";
					if (o != null) {
						field = o.toString();
					}
					if (useFormatCSV) {
						file.print("\"" + field + "\"");
					} else {
						file.print(field);
					}
					if (i<tableWidth-1) {
						file.print(limit);
					}
				}
				file.print("\n");
			}

			try {
				file.close();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	void buildSQLRecords(String table,TableHeader tableHeader,Vector dataVector) {
		Vector columnsVector  = tableHeader.getNameFields(); 
		String sql  = "";
		int columnsSize = columnsVector.size();
		try {
			for (int p=0;p<dataVector.size();p++) {
				sql = "INSERT INTO \"" + table + "\" VALUES(";
				Vector tempo = (Vector) dataVector.elementAt(p);
				if (tempo.size() != columnsSize) {
					if (p > 0) {
						refreshTable();
					}
					int k = p + 1;
					JOptionPane.showMessageDialog(Records.this,
							Language.getWord("TFIC") + k,
							Language.getWord("ERROR!"),JOptionPane.ERROR_MESSAGE);
					return;
				}          
				for (int j=0;j<columnsSize;j++) {
					String columnName = (String) columnsVector.elementAt(j);
					String type = tableHeader.getType(columnName);
					Object o = tempo.elementAt(j);
					if (type.startsWith("varchar") || type.startsWith("char") || type.startsWith("text") 
							|| type.startsWith("name") || type.startsWith("date") || type.startsWith("time")) {
						sql += "'" + o.toString() + "'";
					} else {
						sql += o.toString();
					}
					if (j < (columnsSize - 1)) {
						sql += ",";
					}
				}

				sql += ");";
				addTextLogMonitor(Language.getWord("EXEC")+ sql + "\"");
				String result = pgconnection.executeSQL(sql);

				if (result.equals("OK")) {
					addTextLogMonitor(Language.getWord("RES") + result);
				}
				else {
					result = result.substring(0,result.length()-1);
					addTextLogMonitor(Language.getWord("RES") + result);

					ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
					showError.pack();
					showError.setLocationRelativeTo(frame);
					showError.setVisible(true);
					return;
				}

			}
			refreshTable();
		}
		catch (Exception ex) {
			System.out.println("Error: " + ex);
			ex.printStackTrace(); 
		}

	}

	public void refreshTable() {

		String sql = "SELECT " + recordFilter + " FROM \"" + currentTable;

		if (whereClausule.length()!=0) { 
			sql = "SELECT " + recordFilter + " FROM \"" + currentTable + "\"" + whereClausule;
		}

		recordsCount = countRecords(currentTable,pgconnection,sql,true);
		currentPage = 1;

		if (recordsCount > 50) {
			sql = "SELECT * FROM (" + sql + ") AS foo LIMIT 50";
			indexMin = 1;
			indexMax = 50;
		} else {
			nPages = 1;
			if (recordsCount == 0) {
				indexMin = indexMax = 0;
			} 
			else {
				indexMin = 1;
				indexMax = recordsCount;
			}
		}

		Vector result = pgconnection.getResultSet(sql);
		Vector columnsVector = pgconnection.getTableHeader();
		String owner = pgconnection.getOwner(currentTable);
		setLabel(pgconnection.getDBname(),currentTable,owner);
		showQueryResult(result,columnsVector);
		updateUI();
	}

	/** METODO keyTyped */

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		String selectedKey = KeyEvent.getKeyText(keyCode); 
		if (selectedKey.equals("Delete")) {
			dropRecords();
			return;
		}
		if (selectedKey.equals("Insert")) {
			updateRecords();
			return;
		} 
	}

	/*
	 * METODO keyReleased
	 * Handle the key released event from the text field.
	 */

	public void keyReleased(KeyEvent e) {
	}

	/**
	 * METODO focusGained
	 * Es un foco para los eventos del teclado
	 */

	public void focusGained(FocusEvent e) {
		Component jTable = e.getComponent();
		jTable.addKeyListener(this);
	}

	/**
	 * METODO focusLost
	 */

	public void focusLost(FocusEvent e) {
		Component jTable = e.getComponent();
		jTable.removeKeyListener(this);
	}

	/**
	 * METODO insertRecords 
	 */

	public void insertRecords() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		InsertData insert = new InsertData(realTableName,tableStruct,frame);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		if (insert.isSuccessful()) {
			addTextLogMonitor(Language.getWord("EXEC")+ insert.getSQLString() + "\"");
			String result = pgconnection.executeSQL(insert.getSQLString());

			if (result.equals("OK")) {
				totalReg++;
				String sql = "SELECT " + recordFilter + " FROM " + realTableName;
				if (whereClausule.length()>0) {
					sql = "SELECT " + recordFilter + " FROM " + realTableName + " " + whereClausule;
				}
				oldMem = recordsCount;
				recordsCount = countRecords(realTableName,pgconnection,sql,true);
				int oldNPages = nPages;
				nPages = getPagesNumber(recordsCount);
				onTable.setText(" " + Language.getWord("TOTAL") + " : " + totalReg + " ");
				if (recordsCount > 50) {
					if ((oldNPages == nPages - 1) && (currentPage == nPages - 1)) {
						if (!queryLeft.isEnabled()) {
							queryLeft.setEnabled(true);
							backButton.setEnabled(true);
						}
						currentPage++;
						limit = 1;
						indexMin = indexMax = recordsCount;
						start = indexMin - 1;
						sql = "SELECT " + recordFilter + " FROM " + realTableName + "\" LIMIT " + limit + " OFFSET " + start;
						if (whereClausule.length()!=0) {
							sql = "SELECT * FROM ("
								+ "SELECT " + recordFilter + " FROM " + realTableName + " " + whereClausule + ") AS foo LIMIT " + limit
								+ " OFFSET " + start;
						}

						Vector res = pgconnection.getResultSet(sql);
						Vector col = pgconnection.getTableHeader();
						addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

						if (!pgconnection.queryFail()) {
							addTextLogMonitor(Language.getWord("RES") + "OK");
							showQueryResult(res,col);
							updateUI();
						}
					}
					else {
						if (currentPage == nPages) {
							if (limit < 50) {
								limit++;
							}

							sql = "SELECT " + recordFilter + " FROM " + realTableName + " LIMIT " + limit + " OFFSET " + start;

							if (whereClausule.length()!=0) {
								sql = "SELECT * FROM ("
									+ "SELECT " + recordFilter + " FROM " + realTableName + " " + whereClausule + ") AS foo LIMIT " + limit 
									+ " OFFSET " + start;
							}

							Vector resultSet = pgconnection.getResultSet(sql);
							Vector columnsVector = pgconnection.getTableHeader();

							addTextLogMonitor(Language.getWord("EXEC")+ sql + ";\"");

							if (!pgconnection.queryFail()) {
								addTextLogMonitor(Language.getWord("RES") + "OK");
								showQueryResult(resultSet,columnsVector);
								updateUI();
							}
						}

						if (currentPage < nPages) {
							setStatistics(currentPage,nPages,indexMin,indexMax);
							if (oldMem != recordsCount) {
								onMem.setText(" " + Language.getWord("ONMEM") + " : " + recordsCount + " ");
								oldMem = recordsCount;
							}
							if (!queryRight.isEnabled()) {
								queryRight.setEnabled(true);
								forwardButton.setEnabled(true);
							} 
						} 
					} 
				}
				else {
					nPages = 1;
					Vector res = pgconnection.getResultSet(sql);
					Vector col = pgconnection.getTableHeader();
					int oldNum = recordsTotal;

					if (!pgconnection.queryFail()) {
						String owner = pgconnection.getOwner(currentTable);
						setLabel(pgconnection.getDBname(),currentTable,owner);
						showQueryResult(res,col);
						if (oldNum==0) {
							checkBox1.setEnabled(true);
							checkBox2.setEnabled(true);
							advanced.setEnabled(true);
						}
						updateUI();
					}

				} 
			}
			else {
				result = result.substring(0,result.length()-1);
				ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
				showError.pack();
				showError.setLocationRelativeTo(frame);
				showError.setVisible(true);
			}

			addTextLogMonitor(Language.getWord("RES") + result);
		}
	}

	/**
	 * METODO dropRecords 
	 */

	public void dropRecords() {
		GenericQuestionDialog genericDialog = new GenericQuestionDialog(frame,Language.getWord("YES"),Language.getWord ("NO"),
				Language.getWord("CONFRM"),Language.getWord("DRCONF"));

		boolean sure = genericDialog.getSelecction();
		if (sure) {
			String[] oid = getRecordOid();
			String result = "";

			for (int i=0;i<oid.length;i++) {
				String sqlStr = "DELETE FROM " + realTableName + " WHERE oid=" + oid[i];
				addTextLogMonitor(Language.getWord("EXEC")+ sqlStr + ";\"");
				result = pgconnection.executeSQL(sqlStr);
				if (!result.equals("OK")) {
					result = result.substring(0,result.length()-1);
				}
				addTextLogMonitor(Language.getWord("RES") + result);
			}

			result = refreshAfterDrop(result);
			addTextLogMonitor("Deletion Report: " + result);
		}
	}

	/**
	 * METODO getRecordOid 
	 */
	public String[] getRecordOid() {
		int[] rows = table.getSelectedRows();
		String[] oid = new String[rows.length];
		for (int i=0;i<rows.length;i++) {
			Object proof = myModel.getValueAt(rows[i],0);
			oid[i] = proof.toString();
		}
		return oid;
	} 

	/**
	 * METODO updatingRecords 
	 */

	public void updateRecords() {
		String[] oid = getRecordOid();
		Vector<Object> oldData = new Vector<Object>();
		for (int i=1;i<table.getColumnCount();i++) {
			oldData.addElement(table.getValueAt(table.getSelectedRow(), i));
		}
		UpdateRecord recordUpdate = new UpdateRecord(realTableName,tableStruct,oldData,frame);
		if (recordUpdate.getResult()) {
			String SQL = recordUpdate.getUpdate() + " WHERE ";
			for (int i=0;i<oid.length;i++) {
				SQL += "oid=" + oid[i];
				if (i<oid.length-1) {
					SQL += " OR ";
				}
			}

			addTextLogMonitor(Language.getWord("EXEC")+ SQL + ";\"");
			String result = pgconnection.executeSQL(SQL);

			if (result.equals("OK")) {

				String sql = "SELECT " + recordFilter + " FROM " + realTableName;
				if (whereClausule.length()!=0) {
					sql = "SELECT " + recordFilter + " FROM " + realTableName + whereClausule;
				}

				recordsCount = countRecords(currentTable,pgconnection,sql,true);
				if (recordsCount > 50) {
					sql = "SELECT * FROM (" + sql + ") AS foo LIMIT " + limit + " OFFSET " + start;
				}

				Vector resultSet = pgconnection.getResultSet(sql);
				Vector columns = pgconnection.getTableHeader();

				if (!pgconnection.queryFail()) {
					showQueryResult(resultSet,columns);
					updateUI();
				}
			}
			else {
				result = result.substring(0,result.length()-1);
				ErrorDialog showError = new ErrorDialog(new JDialog(),pgconnection.getErrorMessage());
				showError.pack();
				showError.setLocationRelativeTo(frame);
				showError.setVisible(true);
			}

			addTextLogMonitor(Language.getWord("RES") + result);
		}
	}

	/**
	 * METODO getPagesNumber
	 */

	public int getPagesNumber(int recordsTotal) {
		double number = ((double)recordsTotal)/50;
		String value = "" + number;
		int pagesTotal = recordsTotal/50;
		if (value.indexOf(".") != -1) {
			String str = value.substring(value.indexOf(".")+1,value.length());
			if (!str.equals("0")) {
				pagesTotal++;
			}
		}
		
		return pagesTotal;
	}

	/**
	 * METODO setStatistics
	 */

	public void setStatistics(int currentPage, int pagesTotal, int minIndex, int maxIndex) {
		currentStatistic.setText(" " + Language.getWord("PAGE") + " " + currentPage + " " + Language.getWord("OF")
				+ " " + pagesTotal + " [ " + Language.getWord("RECS")
				+ " " + Language.getWord("FROM") + " " + minIndex + " " + Language.getWord("TO")
				+ " " + maxIndex + " " + Language.getWord("ONMEM") + " ] ");
	}

	/**
	 * METODO refreshAfterDrop 
	 */

	public String refreshAfterDrop(String result) {
		String sql = "SELECT " + recordFilter + " FROM " + realTableName;
		if (whereClausule.length()!=0) {
			sql = "SELECT " + recordFilter + " FROM " + realTableName + " " + whereClausule;
		}

		int oldCount = recordsCount;
		recordsCount = countRecords(realTableName,pgconnection,sql,true);
		int newTotal = countRecords(realTableName,pgconnection,"",true);
		int diff = totalReg - newTotal;
		String mesg = Language.getWord("DELOKS");

		if (diff == recordsTotal) {
			if (diff == 1) {
				mesg = Language.getWord("DELOK");
			}
			if (currentPage == nPages && currentPage > 1) {
				currentPage--;
				start -= 50;
				limit = 50;
				indexMin = start + 1;
			}
		}

		result += " [ " + diff + " " + mesg + " ]";
		if (oldCount > recordsCount) {
			if (recordsCount > 50) {
				sql = "SELECT * FROM (" + sql + ") AS foo LIMIT " + limit + " OFFSET " + start;
			}
			else {
				currentPage = 1;
				if (recordsCount < 0) {
					indexMin = 1;
					indexMax = recordsCount;
				}
			}
			Vector resultSet = pgconnection.getResultSet(sql);
			Vector columnsVector = pgconnection.getTableHeader();
			if (!pgconnection.queryFail()) {
				String owner = pgconnection.getOwner(currentTable);
				setLabel(pgconnection.getDBname(),currentTable,owner);
				showQueryResult(resultSet,columnsVector);
				if (recordsTotal==0) {
					checkBox1.setEnabled(false);
					checkBox2.setEnabled(false);
					advanced.setEnabled(false);
				}
				updateUI();
			}
		}
		return result;
	}

	public void setOrder() {
		orderBy = "oid";
	}

} // Fin de la Clase

class ColoredTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	public void setValue(Object value) {
		setForeground(Color.red);
		setText(value.toString());
	}
}
