/**
 * Disponible en http://www.kazak.ws
 *
 * Desarrollado por Soluciones KAZAK
 * Grupo de Investigacion y Desarrollo de Software Libre
 * Santiago de Cali/Republica de Colombia 2001
 *
 * CLASS PGConnection v 0.1
 * Descripcion:
 * Esta clase desarrolla las siguientes funciones: cargar el driver  
 * de jdbc, realizar la conexi�n con Postgres, enviar consultas SQL  
 * a trav�s del driver.                                              
 *                                                                   
 * Los objetos de este tipo se crean desde la clase XPg y ConnectWin    
 *
 * Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
 *
 * Fecha: 2001/10/01
 *
 * Autores: Beatriz Flori�n  - bettyflor@kazak.ws
 *          Gustavo Gonzalez - xtingray@kazak.ws
 */
package ws.kazak.xpg.db;

import java.sql.*;
import java.util.*;

import ws.kazak.xpg.idiom.*;

public class PGConnection {

	//Variables de la Base de Datos  
	Connection db;	 // La conexion a la base de datos              
	Statement  st;  // El estamento para ejecutar consultas           
	DatabaseMetaData dbmd; // La definicion de la estructura de la base de datos 
	String url;                                                            
	String query;                                                          
	protected ConnectionInfo DBdata;                                          
	int answer=0;                                                          
	String version = "";                                                   
	public Vector<String> tableHeader = new Vector<String>();                                     
	public String problem ="";                                                    
	String ProductVersion; 
	boolean wasFail;                                                
	public String SQL="";

	/** 
	 * METODO CONSTRUCTOR
	 * Este m�todo se encarga de cargar el driver y conectarse con Postgres 
	 */
	public PGConnection(ConnectionInfo User_Entry) {

		DBdata = User_Entry;        
		String port = "";           

		if (DBdata.getPort() != 5432)     
			port = ":" + DBdata.getPort();    

		if (DBdata.requireSSL()) 
			url = "jdbc:postgresql://" + DBdata.getHost() + port + "/" + DBdata.getDatabase() + "?ssl";
		else
			url = "jdbc:postgresql://" + DBdata.getHost() + port + "/" + DBdata.getDatabase();

		/*1. Cargar el driver*/                                                                               
		try {                                                                                                                               
			String Driver = "Driver";
			String path = "org.postgresql"; 
			if(path != null)
				Driver = path + "." + Driver; 
			Class.forName(Driver);                               
		}                                                                          
		catch (ClassNotFoundException ex) {
			problem = Language.getWord("NODRIVER");                                                         
			answer=-1;                                                           	 
			System.out.println("Error: " + ex);
			ex.printStackTrace();
		}                                          	 

		/*2. Conectar a Postgres */                                                                
		try {
			db = DriverManager.getConnection(url,DBdata.getUser(),DBdata.getPassword());
			dbmd = db.getMetaData();
			st = db.createStatement();
			answer=1;
		}
		catch(SQLException ex) {
			problem = ex.getMessage();
			answer=-2; 
		}          
	}


	/**
	 * METODO FAIL 
	 * Retorna el mensaje satisfactorio de conexi�n
	 */
	public boolean Fail() {  

		if (answer==1) { //Cuando pudo conectarse 
			try {
				ProductVersion = dbmd.getDatabaseProductVersion();
				version = Language.getWord("CONNTO") + dbmd.getDatabaseProductName()+ " " + ProductVersion; 
			}
			catch (SQLException ex) {
				problem = ex.getMessage();
			}

			return false;
		}
		else
			return true;
	}

	/**
	 * METODO getVersion 
	 * Retorna la version de Postgres utilizada
	 */
	public String getVersion() {
		return ProductVersion;
	}

	/**
	 * METODO getHostname 
	 * Retorna el nombre o ip del servidor al que el usuario accede 
	 */      
	public String getHostname() {
		return DBdata.getHost();
	}	  

	/**
	 * METODO getUsername 
	 *Retorna el login del usuario utilizado para la conexi�n
	 */
	public String getUsername() {
		return DBdata.getUser();
	}

	/**
	 * METODO getDBname 
	 * Retorna el nombre de la base de datos que el usuario utiliz�
	 * para la conexi�n
	 */
	public String getDBname() {
		return DBdata.getDatabase();
	}

	/**
	 * METODO getPort 
	 * Retorna el puerto utilizado para la conexi�n
	 */
	public int getPort() {
		return DBdata.getPort();
	}

	/**
	 * METODO requireSSL 
	 * Retorna verdadero si la conexion requiere soporte SSL
	 */
	public boolean requireSSL() {
		return DBdata.requireSSL();
	}

	/**
	 * METODO getConnectionInfo
	 * Retorna toda la estructura de datos de la conexi�n
	 */
	public ConnectionInfo getConnectionInfo() {
		return DBdata;
	}

	/**
	 * METODO getSQL
	 * Retorna toda la estructura de datos de la conexi�n
	 */
	public String getSQL() {
		return SQL;
	}

	/**
	 * METODO getTableHeader
	 * Retorna el encabezado de las columnas de una tabla 
	 */

	public Vector getTableHeader() {
		return tableHeader;
	}

	/**
	 * METODO getTableHeader
	 * Retorna el encabezado de las columnas de una tabla 
	 */

	public String getFirstFieldName() {
		return "dato";
	}



	/**
	 * METODO getProblemString
	 * Retorna el texto especifico del error 
	 */

	public String getProblemString() {
		return problem;
	}

	/**
	 * METODO getErrorMessage
	 * Retorna un vector con el nro. t�tulo y detalle 
	 * de cada error de conexi�n
	 */  
	public String[] getErrorMessage() {

		String[] ErrorData = new String[3];
		ErrorData[0]="1";
		ErrorData[1]="";	
		ErrorData[2]=problem;

		int k;
		k = problem.indexOf("ClassNotFoundException");

		if (k != -1) {

			ErrorData[0]="1";	
			ErrorData[1]=problem;
			ErrorData[2] = "CLASSPATH variable doesn't contain the path of the PostgreSQL JDBC Driver.";

			return ErrorData;
		}  
		//recordar -> indexOf retorna -1 si no se encuentra el �ndice donde empieza la cadena dada
		k = problem.indexOf(Language.getWord("NODRIVER"));

		if (k != -1) {

			ErrorData[1] = problem;
			return ErrorData;
		}

		k = problem.indexOf("No pg_hba.conf entry");	 

		if (k != -1) {
			ErrorData[0]="2";
			ErrorData[1] = Language.getWord("NOPGHBA");
			return ErrorData;
		}

		k = problem.indexOf("Password authentication failed");

		if (k != -1) {
			ErrorData[0]="3";
			ErrorData[1] = Language.getWord("BADPASS");
			return ErrorData;
		}  

		k = problem.indexOf("UnknownHostException");

		if (k != -1) {
			ErrorData[0]="4";	
			ErrorData[1] = Language.getWord("BADHOST");
			return ErrorData;
		} 

		k = problem.indexOf("Permission denied");

		if (k != -1) {
			ErrorData[0]="5";	
			ErrorData[1] = "You don't have some permissions to access this table.";
			ErrorData[2] = ErrorData[2] + "\nContact your PostreSQL Server Admin to avoid this message.";
			return ErrorData;
		}

		k = problem.indexOf("referential integrity violation");	 

		if (k != -1) {
			ErrorData[0]="6";
			ErrorData[1] = "Referential Integrity Violation";
			return ErrorData;
		}

		k = problem.indexOf("Connection refused");

		if (k != -1) {
			ErrorData[0]="7";
			ErrorData[1] = "Connection refused from PostgreSQL Server";
			return ErrorData;
		}

		k = problem.indexOf(".");
		ErrorData[0]= Language.getWord("STRANGE");	

		if (k != -1)
			ErrorData[1]=  problem.substring (0,k); //captura titulo inicial antes de excepciones del mensaje
		else
			ErrorData[1] = problem.substring(0,problem.length()); //captura excepciones del mensaje

		return ErrorData;
	}

	/**
	 * METODO close 
	 * Se encarga de cerrar la conexi�n
	 */  
	public void close() {

		try { 
			st.close();
			db.close();    
		}
		catch (SQLException ex) {
			System.out.println("Error: " + ex);
			ex.printStackTrace();
			problem = ex.getMessage();
		}	     
	}

	/**
	 * METODO Void_Instruction 
	 * Metodo para ejecutar consultas de tipo INSERT, UPDATE, 
	 * DELETE, DROP y CREATE
	 */  
	public String executeSQL(String instruction) {

		String result = "OK";

		try {
			st.executeUpdate(instruction);
		}
		catch(SQLException ex) {
			problem = ex.getMessage();
			result = problem;
		}

		return result;  
	}

	/**
	 * METODO getUserPerm 
	 * Metodo para capturar los permisos de un usuario 
	 */ 
	public String[] getUserPerm(String login) {

		String[] values = new String[2];
		String permissions = "SELECT usecreatedb,usesuper FROM pg_user WHERE usename='" + login + "';";

		try {
			Vector resultx = getResultSet(permissions);
			Vector row = (Vector) resultx.elementAt(0);
			Object o = row.elementAt(0);
			values[0] = o.toString();
			o = row.elementAt(1);
			values[1] = o.toString();
		}
		catch(Exception ex) {
			System.out.println("Error: " + ex);
			ex.printStackTrace();
		}

		return values;
	}  

	/**
	 * METODO TableQuery
	 * M�todo para ejecutar consultas de tipo SELECT
	 */  
	public Vector<Vector> getResultSet(String sentence) {

		SQL = sentence;
		Vector<Vector> queryResult= new Vector<Vector>(); //Vector de vectores
		Vector<Object> oneRow; // = new Vector<String>();
		tableHeader = new Vector<String>();
		ResultSet resultSet;

		try {
			resultSet = st.executeQuery(sentence);	       
			ResultSetMetaData metaData = resultSet.getMetaData();
			// Extraer Nombres de Columnas
			int cols = metaData.getColumnCount();

			String columnName = new String();
			for(int i=1;i<=cols;i++) {
				tableHeader.addElement(metaData.getColumnLabel(i));
				columnName = metaData.getColumnLabel(i);
			}

			while (resultSet.next()) {

				//System.out.println("contenido de getFetchSize despues: "+rs.getFetchSize());

				oneRow = new Vector<Object>();
				Object o = new Object();  
				for  (int i=1;i<=cols;i++) {

					if (columnName.equals("tgargs")) {
						byte dt[] = resultSet.getBytes(1);
						StringBuffer record = new StringBuffer();
						for (int j=0;j<dt.length;j++) {
							int k = dt[j];
							if (k != 0)
								record.append(k + "-");
							else
								record.append(" ");
						}	
						String data = record.toString();
						//return getASCII(data);
						oneRow = getASCII(data);
					}	
					else {	
						o = resultSet.getObject(i);
					}                       

					if (resultSet.wasNull()) {
						o = null;
					}
					oneRow.addElement(o);
				}
				queryResult.addElement(oneRow); 
			}
			wasFail = false;
		}
		catch(SQLException ex) { 
			wasFail = true;
			problem = ex.getMessage();
			System.out.println("Error: " + ex);
			ex.printStackTrace();
		}

		return queryResult;
	}

	public Vector<Object> getASCII(String data) {
		Vector<Object> foreignKeys = new Vector<Object>();
		StringTokenizer filter = new StringTokenizer(data," ");

		while (filter.hasMoreTokens()) {
			StringTokenizer translator = new StringTokenizer(filter.nextToken(),"-");
			StringBuffer word = new StringBuffer();
			while (translator.hasMoreTokens()) {
				int p = new Integer(translator.nextToken()).intValue();
				word.append((char) p);
			}
			String key = word.toString();
			foreignKeys.addElement(key);
		}

		return foreignKeys;	
	}

	public boolean queryFail() {
		return wasFail;  
	}

	public String getOwnerDB() {

		Vector user = getResultSet("SELECT usename FROM pg_user,pg_database WHERE datdba = usesysid AND datname = '" + DBdata.getDatabase() + "';");
		Vector nameU = (Vector) user.elementAt(0);
		String owner = (String) nameU.elementAt(0);

		return owner;
	}

	public String getOwner(String tableName) {
		String sentence = "SELECT usename FROM pg_class,pg_user " +
		"WHERE usesysid=relowner AND relname='" + tableName + "';";
		Vector propertiesVector = getResultSet(sentence);
		System.out.println("SQL: " + sentence);
		System.out.println("Size: " + propertiesVector.size());

		Vector usersVector = (Vector) propertiesVector.elementAt(0);
		String owner = (String) usersVector.elementAt(0);

		return owner;
	}

	public void getTablePermissions(String TableName) {
		/*
   Table x = new Table(TableName);
   Vector Permissions = TableQuery("SELECT * FROM pg_class WHERE relname='" + TableName + "';");

   for (int j=0;j<Permissions.size();j++) {

        Vector user = (Vector) Permissions.elementAt(j);

        for (int i=0;i<user.size();i++) {
             String perm = (String) user.elementAt(i);
         }
    } */
	}

	/**
	 * METODO getSpecStrucTable
	 * Retorna una la estructura de una tabla espec�fica dado su nombre
	 */
	public Table getSpecStrucTable(String Tname) {

		//Se construye un vector donde cada elemento es la definici�n completa de un campo
		Vector tableStructure = getResultSet( "SELECT a.attname, t.typname, a.attlen, a.attnotnull, a.atttypmod, a.atthasdef,  a.attnum "
				+ "FROM pg_class c, pg_attribute a, pg_type t WHERE c.relname='" + Tname 
				+ "' AND a.attnum > 0 AND a.attrelid = c.oid AND a. atttypid = t.oid ORDER BY a.attnum"); //se hace la consulta

		//Se crea un vector que lee y formatea los datos leidos de cada campo
		Vector<TableFieldRecord> fields = new Vector<TableFieldRecord>();

		for (int j=0;j<tableStructure.size();j++) {

			String AtriName="";
			String Atritype="";
			String AtriDef ="";
			int intLong  = -1;
			int charLong = -1;
			boolean isNull  = true;
			boolean haveDef = false;
			boolean pK = false;
			boolean uK = false;
			boolean fK = false;
			Vector row = (Vector) tableStructure.elementAt(j);

			//capturar nombre del campo
			Object t = row.elementAt(0);
			AtriName = t.toString();

			//capturar tipo del campo
			t = row.elementAt(1);
			Atritype = t.toString(); //Tipo del campo

			//caturar longitud del campo
			t = row.elementAt(2);
			intLong = Integer.parseInt(t.toString());

			//capturar si activa not null
			t = row.elementAt(3);
			Boolean boolvalue = new Boolean(t.toString());
			isNull = boolvalue.booleanValue();

			//capturar logitud de char y varchar
			t = row.elementAt(4);
			charLong = Integer.parseInt(t.toString());

			if (charLong != -1)
				charLong -= 4;

			//Si existe valor por defecto capturarlo
			t = row.elementAt(5);
			boolvalue = new Boolean(t.toString());
			haveDef = boolvalue.booleanValue();

			if (haveDef) {

				t = row.elementAt(6);
				Vector vecAtriDef  = getResultSet("SELECT d.adsrc FROM pg_attrdef d, pg_class c WHERE c.relname='"+ Tname +"' AND c.oid = d.adrelid AND d.adnum="+ Integer.parseInt(t.toString()));
				Object d = vecAtriDef.elementAt(0);
				AtriDef = d.toString();
				int k = AtriDef.indexOf("]");
				AtriDef = AtriDef.substring (1,k);
			}

			if (Atritype.equals("bpchar"))
				Atritype= "char";

			OptionField opcAtrib = new OptionField(Atritype,charLong,intLong,isNull,pK,uK,fK,AtriDef);
			TableFieldRecord oneAtrib = new TableFieldRecord(AtriName,Atritype,opcAtrib);
			fields.addElement(oneAtrib);
		}

		TableHeader header = new TableHeader(fields);
		Table table = new Table(Tname,header);

		return table;
	}

	/**
	 * METODO getHeader 
	 * Retorna el vector con el nombre de las cabeceras de la tabla de resultados
	 */  
	public Vector getHeader() {
		return tableHeader;
	}

	/**
	 * METODO getTablesStructure 
	 * Retorna la estructura de todas las tablas de una lista de tablas
	 */  
	public Vector getTablesStructure(Vector tablesList) {

		Vector<Table> structuresVector = new Vector<Table>();
		int tablesTotal = tablesList.size();

		if (tablesTotal > 0) {

			for (int k=0;k<tablesTotal;k++) {

				Object o = tablesList.elementAt(k);
				String dbName = o.toString();
				String tableName = dbName;

				if (dbName.startsWith("["))
					tableName = dbName.substring(1,dbName.length()-1);

				Vector<Vector> tableStructure = getResultSet( "SELECT a.attname, t.typname, a.attlen, a.attnotnull, a.atttypmod, a.atthasdef, a.attnum " 
						+"FROM pg_class c, pg_attribute a,pg_type t WHERE c.relname='"+ tableName 
						+"' AND a.attnum > 0 AND a.attrelid = c.oid AND a. atttypid = t.oid ORDER BY a.attnum");
				Vector<TableFieldRecord> fields = new Vector<TableFieldRecord>();

				for (int j=0;j<tableStructure.size();j++) {

					String AtriName="";
					String Atritype="";
					int intLong = -1;
					int charLong = -1;
					boolean isNull = true;
					//boolean haveDef = false;
					boolean pK = false;
					boolean uK = false;
					boolean fK = false;
					Vector row = (Vector) tableStructure.elementAt(j);

					for (int m=0;m<row.size();m++) {

						Object t = row.elementAt(m);

						switch(m) {
						case 0:  AtriName = t.toString(); //Nombre del campo
						break;

						case 1:  Atritype = t.toString(); //Tipo del atributo
						break;

						case 2:  intLong = Integer.parseInt(t.toString()); //Longitud del campo
						break;

						case 3:  Boolean boolvalue = new Boolean(t.toString()); //Not Null? 
						isNull = boolvalue.booleanValue();
						break; 

						case 4:  charLong = Integer.parseInt(t.toString());//Longitudes de char y varchar 
						if (charLong != -1) 
							charLong -= 4; 
						break;

						case 5:  boolvalue = new Boolean(t.toString()); //Hay valor por defecto?
						//haveDef = boolvalue.booleanValue();
						break;
						}
					}

					OptionField opcAtrib = new OptionField(Atritype,charLong,intLong,isNull,pK,uK,fK,null);
					TableFieldRecord oneAtrib = new TableFieldRecord(AtriName,Atritype,opcAtrib); 
					fields.addElement(oneAtrib);
				}

				TableHeader header = new TableHeader(fields);
				Table oneT = new Table(tableName,header);
				structuresVector.addElement(oneT);
			}
		}

		return structuresVector;
	}

	/**
	 * METODO getIndexTable()
	 * Hace la consulta y retorna un vector con los �ndices de una tabla
	 */
	public Vector getIndexTable (String Tname) {

		Vector indexTable = getResultSet("SELECT c2.relname FROM pg_class c, pg_class c2, pg_index i WHERE c.relname='"
				+Tname+"' AND c.oid = i.indrelid AND i.indexrelid=c2.oid ORDER BY c2.relname"); //se hace la consulta

		Vector<String> indexTable2 = new Vector<String>();
		String indexName;

		for (int i=0; i<indexTable.size() ; i++) {
			Vector o = (Vector) indexTable.elementAt(i);
			indexName = (String) o.elementAt(0);
			indexTable2.addElement(indexName);
		}

		return indexTable2;
	}

	/**
	 * METODO getIndexProp
	 * Hace la consulta y retorna un vector con las propiedades de
	 * un indice determinado
	 */
	public Vector getIndexProperties(String indexName) {
		String sentence = "SELECT pg_index.indexrelid, pg_index.indisunique, pg_index.indisprimary FROM pg_index, pg_class WHERE pg_class.relname='"+ indexName + 
		"' AND pg_class.oid = pg_index.indexrelid";
		System.out.println("IndexProperties SQL: " + sentence);
		Vector indexesVector = getResultSet(sentence); //se hace la consulta
		return indexesVector;
	}

	/**
	 * METODO getForeignKeys 
	 * Hace una consulta y retorna un vector con las llaves foraneas 
	 * referentes a una tabla 
	 */

	public Vector getForeignKeys(String tname) {
		Vector indexFK = getResultSet("SELECT p.tgargs FROM pg_trigger p,pg_class k WHERE k.relname='" + tname + "' AND k.oid=p.tgrelid AND p.tgtype=21");
		//Vector result = new Vector();

		return indexFK; 
	}

	/**
	 * METODO getGroups
	 * Hace la consulta y retorna un vector con los grupos 
	 * existentes en el sistema
	 */
	public String[] getGroups() {

		Vector Groups = getResultSet("SELECT groname FROM pg_group ORDER BY groname");
		String[] Gnames = new String[Groups.size()];

		for (int i=0;i<Groups.size();i++) {
			Vector dat = (Vector) Groups.elementAt(i);
			Gnames[i] = (String) dat.elementAt(0);
		}

		return Gnames;

	}

	/**
	 * METODO getNumTables
	 * Hace la consulta y retorna el numero de tablas 
	 * existentes para esta base de datos 
	 */

	public int getNumTables() {

		String SQL = "SELECT count(*) FROM pg_tables where tablename !~ '^pg_' AND tablename  !~ '^pga_' AND tablename  !~ '^sql_'";
		Vector count = getResultSet(SQL);
		Vector value = (Vector) count.elementAt(0);

		int n;

		try {
			Long entero = (Long) value.elementAt(0);
			n = entero.intValue();
		}
		catch(Exception ex) {
			Integer entero = (Integer) value.elementAt(0);
			n = entero.intValue();
		}

		return n;
	}

	/**
	 * METODO getTablesNames
	 * Hace la consulta y retorna un arreglo con los nombres de las tablas 
	 * existentes en el sistema
	 */

	public String[] getTablesNames(boolean justPrp) {

		String SQL = "SELECT tablename FROM pg_tables WHERE tablename !~ '^pg_' AND tablename  !~ '^pga_' AND tablename  !~ '^sql_'";

		if (justPrp)
			SQL += " AND tableowner = '" + DBdata.getUser() + "'";

		SQL += " ORDER BY tablename;";
		Vector Tnames = getResultSet(SQL);
		String[] names = new String[Tnames.size()];

		for (int i=0;i<Tnames.size();i++) {
			Vector dat = (Vector) Tnames.elementAt(i);
			names[i] = (String) dat.elementAt(0);
		}

		return names;
	}

	public Vector getGroupUser(String group) {

		Vector<String> groupsVector = new Vector<String>();
		Vector Size = getResultSet("SELECT array_dims(grolist) FROM pg_group WHERE groname = '" + group + "'");

		if (Size.size()<1)
			return new Vector();

		Vector mp = (Vector) Size.elementAt(0);
		String kmp = (String) mp.elementAt(0);
		int p = kmp.indexOf(":");
		int k = kmp.indexOf("]");
		int max = Integer.parseInt(kmp.substring(p+1,k));

		for (int z=1;z<=max;z++) {

			Vector Groups = getResultSet("SELECT grolist[" + z + "] FROM pg_group WHERE groname = '" + group + "'");

			for (int i=0;i<Groups.size();i++) {

				Vector dat = (Vector) Groups.elementAt(i);

				for (int j=0;j<dat.size();j++) {

					Vector Names = getResultSet("select usename from pg_user where usesysid='" + dat.elementAt(0) + "';");
					Vector n = (Vector) Names.elementAt(0);
					groupsVector.addElement("" + n.elementAt(0));
				}
			}
		}

		return groupsVector;
	}

	/**
	 * METODO getUsers
	 * Hace la consulta y retorna un vector con los usuarios
	 * existentes en el sistema
	 */

	public String[] getUsers() {

		Vector users = getResultSet("SELECT usename FROM pg_user ORDER BY usename");
		String[] Unames = new String[users.size()];

		for (int i=0;i<users.size();i++) {

			Vector dat = (Vector) users.elementAt(i);
			Unames[i] = (String) dat.elementAt(0);
		}

		return Unames;
	}

	/**
	 * METODO getUserInfo
	 * Hace la consulta y retorna informacion sobre un usuario
	 */

	public Vector getUserInfo(String user) {

		Vector users = getResultSet("SELECT usecreatedb,usesuper,valuntil FROM pg_user WHERE usename = '" + user + "';");
		Vector info = (Vector) users.elementAt(0);

		return info;
	}

	/**
	 * METODO getIndexFields
	 * Hace la consulta y retorna un vector con los nombres de los campos
	 * de un indice determinado
	 */
	public Vector getIndexFields (String codIndex) {

		Vector indexFields = getResultSet( "SELECT pg_attribute.attname FROM pg_attribute WHERE pg_attribute.attrelid =" + codIndex); //se hace la consulta
		return indexFields;
	}

	/**
	 * METODO gotSchema
	 * Verifica si una tabla pertenece a un schema personalizado o no
	 */
	public boolean gotUserSchema(String tablename) {
		String schemaName = getSchemaName(tablename);
		if (schemaName.equals("information_schema") || schemaName.equals("public") || schemaName.startsWith("pg_"))
			return false;
		else
			return true;
	}

	/**
	 * METODO getSchemaName
	 * Retorna el nombre del Schema asociado a una Tabla 
	 */
	public String getSchemaName(String tablename) {
		Vector schemaRow = getResultSet("SELECT schemaname FROM pg_tables wHERE tablename='" + tablename + "'");
		//int size = schemaRow.size();
		Vector schemaColumn = (Vector) schemaRow.elementAt(0);
		return (String) schemaColumn.elementAt(0);
	}

} //Fin de la Clase
