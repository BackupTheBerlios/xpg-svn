package ws.kazak.xpg.main;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ws.kazak.xpg.idiom.Language;
import ws.kazak.xpg.misc.input.GenericQuestionDialog;

public class MainMenu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	// Menues desplegables
	JMenu connection, dataBase, tables, query, admin, help, group, sub_user; 
	JMenuItem connectItem, disconnectItem, exitItem, newDBItem, dropDBItem, sub_permi; // items de los Menues connection y dataBase
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
	
	private MainWindow mainWindow;
	
	/**
	 * METODO public void CreateMenu() Crea Menu desplegable
	 */
	public MainMenu(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		connection = new JMenu(Language.getWord("CONNEC")); // Crea el menu de
		// conexi�n
		connection.setMnemonic(Language.getNemo("NEMO-CONNEC"));
		add(connection); // Adiciona el menu al menu desplegable

		dataBase = new JMenu(Language.getWord("DB")); // Crea el menu de Base
		// de Dato
		dataBase.setMnemonic(Language.getNemo("NEMO-DB")); // Establece un
		// atajo
		add(dataBase); // Adiciona el menu al menu desplegable

		tables = new JMenu(Language.getWord("TABLE")); // Crea un menu llamado
		// Table
		tables.setMnemonic(Language.getNemo("NEMO-TABLE")); // Establece un
		// atajo
		add(tables); // Adiciona el menu al menu desplegable

		query = new JMenu(Language.getWord("QUERY"));// Crea un menu llamado
		// Query
		query.setMnemonic(Language.getNemo("NEMO-QUERY")); // Establece un
		// atajo
		add(query); // Adiciona el menu al menu desplegable

		admin = new JMenu(Language.getWord("ADMIN"));// Crea un menu llamado
		// admin
		admin.setMnemonic(Language.getNemo("NEMO-ADMIN")); // Establece un
		// atajo
		add(admin); // Adiciona el menu al menu desplegable

		help = new JMenu(Language.getWord("HELP"));// Crea un menu llamado Help
		help.setMnemonic(Language.getNemo("NEMO-HELP")); // Establece un
		// atajo
		add(help); // Adiciona el menu al menu desplegable

		/*----------Items Menu connection----------*/
		URL imgURL = getClass().getResource("/icons/16_connect.png");
		connectItem = new JMenuItem(Language.getWord("CONNE2"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		connectItem.setActionCommand("ItemConnect");
		connectItem.addActionListener(this);
		connection.add(connectItem);
		connectItem.setMnemonic(Language.getNemo("NEMO-CONNE2"));

		imgURL = getClass().getResource("/icons/16_disconnect.png");
		disconnectItem = new JMenuItem(Language.getWord("DISCON"),
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		disconnectItem.setActionCommand("ItemDisconnect");
		disconnectItem.addActionListener(this);
		connection.add(disconnectItem);
		disconnectItem.setMnemonic(Language.getNemo("NEMO-DISCON"));
		disconnectItem.setEnabled(false);

		connection.addSeparator();

		imgURL = getClass().getResource("/icons/16_exit.png");
		exitItem = new JMenuItem(Language.getWord("EXIT"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		exitItem.setActionCommand("ItemExit");
		exitItem.addActionListener(this);
		connection.add(exitItem);
		exitItem.setMnemonic(Language.getNemo("NEMO-EXIT"));

		/*----------Items Menu Database ----------*/
		imgURL = getClass().getResource("/icons/16_NewDB.png");
		newDBItem = new JMenuItem(Language.getWord("NEWF"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newDBItem.setActionCommand("ItemCreateDB");
		newDBItem.addActionListener(this);
		dataBase.add(newDBItem);
		newDBItem.setMnemonic(Language.getNemo("NEMO-NEWF"));

		imgURL = getClass().getResource("/icons/16_DropDB.png");
		dropDBItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropDBItem.setActionCommand("ItemDropDB");
		dropDBItem.addActionListener(this);
		dataBase.add(dropDBItem);
		dropDBItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		/*----------Items Menu Table----------*/
		imgURL = getClass().getResource("/icons/16_NewTable.png");
		newTableItem = new JMenuItem(Language.getWord("NEWF"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newTableItem.setActionCommand("ItemCreateTable");
		newTableItem.addActionListener(this);
		tables.add(newTableItem);
		newTableItem.setMnemonic(Language.getNemo("NEMO-NEWF"));

		imgURL = getClass().getResource("/icons/16_DropTable.png");
		dropTableItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropTableItem.setActionCommand("ItemDropTable");
		dropTableItem.addActionListener(this);
		tables.add(dropTableItem);
		dropTableItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		imgURL = getClass().getResource("/icons/16_Dump.png");
		dumpTableItem = new JMenuItem(Language.getWord("DUMP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dumpTableItem.setActionCommand("ItemDumpTable");
		dumpTableItem.addActionListener(this);
		tables.add(dumpTableItem);
		dumpTableItem.setMnemonic(Language.getNemo("NEMO-DUMP"));

		imgURL = getClass().getResource("/icons/16_Grant.png");
		sub_permi = new JMenuItem(Language.getWord("PERMI"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		sub_permi.setActionCommand("ItemGrant");
		sub_permi.addActionListener(this);
		tables.add(sub_permi);
		sub_permi.setMnemonic(Language.getNemo("NEMO-PERMI"));

		/*----------Items Sub-Menu group----------*/
		group = new JMenu(Language.getWord("GROUP"));

		imgURL = getClass().getResource("/icons/16_NewGroup.png");
		newGroupItem = new JMenuItem(Language.getWord("CREATE"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newGroupItem.setActionCommand("ItemCreateGroup");
		newGroupItem.addActionListener(this);
		group.add(newGroupItem);
		newGroupItem.setMnemonic(Language.getNemo("NEMO-CREATE"));

		imgURL = getClass().getResource("/icons/16_AlterGroup.png");
		alterGroupItem = new JMenuItem(Language.getWord("ALTER"),
				new ImageIcon(Toolkit.getDefaultToolkit().getImage(imgURL)));
		alterGroupItem.setActionCommand("ItemAlterGroup");
		alterGroupItem.addActionListener(this);
		group.add(alterGroupItem);
		alterGroupItem.setMnemonic(Language.getNemo("NEMO-ALTER"));

		imgURL = getClass().getResource("/icons/16_DropGroup.png");
		dropGroupItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropGroupItem.setActionCommand("ItemDropGroup");
		dropGroupItem.addActionListener(this);
		group.add(dropGroupItem);
		dropGroupItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		admin.add(group);
		group.setMnemonic(Language.getNemo("NEMO-GROUP"));
		admin.addSeparator();

		/*----------Items Sub-Menu user----------*/
		sub_user = new JMenu(Language.getWord("USER"));

		imgURL = getClass().getResource("/icons/16_NewUser.png");
		newUserItem = new JMenuItem(Language.getWord("CREATE"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newUserItem.setActionCommand("ItemCreateUser");
		newUserItem.addActionListener(this);
		sub_user.add(newUserItem);
		newUserItem.setMnemonic(Language.getNemo("NEMO-CREATE"));

		imgURL = getClass().getResource("/icons/16_AlterUser.png");
		alterUserItem = new JMenuItem(Language.getWord("ALTER"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		alterUserItem.setActionCommand("ItemAlterUser");
		alterUserItem.addActionListener(this);
		sub_user.add(alterUserItem);
		alterUserItem.setMnemonic(Language.getNemo("NEMO-ALTER"));

		imgURL = getClass().getResource("/icons/16_DropUser.png");
		dropUserItem = new JMenuItem(Language.getWord("DROP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		dropUserItem.setActionCommand("ItemDropUser");
		dropUserItem.addActionListener(this);
		sub_user.add(dropUserItem);
		dropUserItem.setMnemonic(Language.getNemo("NEMO-DROP"));

		admin.add(sub_user);
		sub_user.setMnemonic(Language.getNemo("NEMO-USER"));

		/*----------Items Menu query----------*/
		imgURL = getClass().getResource("/icons/16_new.png");
		newQryItem = new JMenuItem(Language.getWord("NEWF"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		newQryItem.setActionCommand("ItemCreateQry");
		newQryItem.addActionListener(this);
		query.add(newQryItem);
		newQryItem.setMnemonic(Language.getNemo("NEMO-NEWF"));

		imgURL = getClass().getResource("/icons/16_Load.png");
		openQryItem = new JMenuItem(Language.getWord("OPEN"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		openQryItem.setActionCommand("ItemOpenQry");
		openQryItem.addActionListener(this);
		query.add(openQryItem);
		openQryItem.setMnemonic(Language.getNemo("NEMO-OPEN"));

		imgURL = getClass().getResource("/icons/16_HQ.png");
		hqItem = new JMenuItem(Language.getWord("HQ"), new ImageIcon(Toolkit
				.getDefaultToolkit().getImage(imgURL)));
		hqItem.setActionCommand("ItemHQ");
		hqItem.addActionListener(this);
		query.add(hqItem);
		hqItem.setMnemonic(Language.getNemo("NEMO-HQ"));

		/*----------Items Menu help----------*/
		imgURL = getClass().getResource("/icons/16_Help.png");
		helpItem = new JMenuItem(Language.getWord("HELP"), new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(imgURL)));
		helpItem.setActionCommand("ItemContenido");
		helpItem.addActionListener(this);
		help.add(helpItem);
		helpItem.setMnemonic(Language.getNemo("NEMO-HELP2"));

		help.addSeparator();

		aboutItem = new JMenuItem(Language.getWord("ABOUT") + "...");
		aboutItem.setActionCommand("ItemAbout");
		aboutItem.addActionListener(this);
		help.add(aboutItem);
		aboutItem.setMnemonic(Language.getNemo("NEMO-ABOUT"));
		switchJMenus(false);
	}

	/**
	 * METODO switchJMenus
	 */
	public void switchJMenus(boolean state) {
		dataBase.setEnabled(state);
		tables.setEnabled(state);
		query.setEnabled(state);
		admin.setEnabled(state);
	}
	
	public void setAlreadyConnected() {
		connectItem.setEnabled(false);
		disconnectItem.setEnabled(true);
	}
	
	public void setMenuStates(boolean state) {
	newTableItem.setEnabled(state);
	dropTableItem.setEnabled(state);
	dumpTableItem.setEnabled(state);
	}
	
	public void setDatabaseItem(boolean state) {
		dataBase.setEnabled(state);
	}
	
	public void enableQueryItem() {
		if (!query.isEnabled()) {
			query.setEnabled(true);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if (action.equals("ItemConnect")
				|| e.getActionCommand().equals("ButtonConnect")) {
			mainWindow.openConnectionDialog();
			return;
		}
		
		if (action.equals("ItemExit")) {
			mainWindow.exit();
			return;
		}
	}
}
