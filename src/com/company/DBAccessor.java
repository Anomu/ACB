package com.company;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DBAccessor {
	private String dbname;
	private String host;
	private String port;
	private String user;
	private String passwd;
	private String schema;
	Connection conn = null;



	/**
	 * Initializes the class loading the database properties file and assigns
	 * values to the instance variables.
	 * 
	 * @throws RuntimeException
	 *             Properties file could not be found.
	 */
	public void init() {
		Properties prop = new Properties();
		InputStream propStream = this.getClass().getClassLoader().getResourceAsStream("db.properties");

		try {
			prop.load(propStream);
			this.host = prop.getProperty("host");
			this.port = prop.getProperty("port");
			this.dbname = prop.getProperty("dbname");
			this.schema = prop.getProperty("schema");
		} catch (IOException e) {
			String message = "ERROR: db.properties file could not be found";
			System.err.println(message);
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * Obtains a {@link Connection} to the database, based on the values of the
	 * <code>db.properties</code> file.
	 * 
	 * @return DB connection or null if a problem occurred when trying to
	 *         connect.
	 */
	public Connection getConnection(Identity identity) {

		// Implement the DB connection
		String url = null;
		try {
			// Loads the driver
			Class.forName("org.postgresql.Driver");

			// Preprara connexió a la base de dades
			StringBuffer sbUrl = new StringBuffer();
			sbUrl.append("jdbc:postgresql:");
			if (host != null && !host.equals("")) {
				sbUrl.append("//").append(host);
				if (port != null && !port.equals("")) {
					sbUrl.append(":").append(port);
				}
			}
			sbUrl.append("/").append(dbname);
			url = sbUrl.toString();

			// Utilitza connexió a la base de dades
			conn = DriverManager.getConnection(url, identity.getUser(), identity.getPassword());
			conn.setAutoCommit(true);
		} catch (ClassNotFoundException e1) {
			System.err.println("ERROR: Al Carregar el driver JDBC");
			System.err.println(e1.getMessage());
		} catch (SQLException e2) {
			System.err.println("ERROR: No connectat  a la BD " + url);
			System.err.println(e2.getMessage());
		}

		// Sets the search_path
		if (conn != null) {
			Statement statement = null;
			try {
				statement = conn.createStatement();
				statement.executeUpdate("SET search_path TO " + this.schema);
				// missatge de prova: verificació
				System.out.println("OK: connectat a l'esquema " + this.schema + " de la base de dades " + url
						+ " usuari: " + user + " password:" + passwd);
				System.out.println();
				//
			} catch (SQLException e) {
				System.err.println("ERROR: Unable to set search_path");
				System.err.println(e.getMessage());
			} finally {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println("ERROR: Closing statement");
					System.err.println(e.getMessage());
				}
			}
		}

		return conn;
	}

	public void mostrarEquips() throws SQLException {
		Statement st = conn.createStatement();
		ResultSet rs = null;

		rs = st.executeQuery("SELECT * FROM team ");
		System.out.println("--Equips--");
		while(rs.next()) {
			System.out.println(rs.getString("name"));
		}
		if(rs.wasNull()) System.out.println("No hi ha equips a la base de dades");
		rs.close();

	}

	public void mostrarJugadorsEquip() throws SQLException {
		Scanner sc = new Scanner(System.in);
		Statement st = conn.createStatement();
		ResultSet rs = null;

		rs = st.executeQuery("SELECT * FROM team ");
		System.out.println("--Equips--");
		while(rs.next()) {
			System.out.println(rs.getString("name"));
		}
		if(rs.wasNull()) System.out.println("No hi ha equips a la base de dades");
		System.out.println("Indica el nom del equip del que vols veure els jugadors:");
		String equip = sc.nextLine();
		rs = st.executeQuery("SELECT * FROM player WHERE team_name = '"+equip+"'");
		if(!rs.next()) System.out.println("No esxisteix l'equip.");
		while(rs.next()) {
			System.out.println(rs.getString("first_name")+" "+rs.getString("last_name"));
		}

		rs.close();
	}

	public void crearEquip() throws SQLException {
		Scanner sc = new Scanner(System.in);
		Statement st = conn.createStatement();
		ResultSet rs = null;

		System.out.println("--Crear equip--");
		System.out.println("Nom de l'equip:");
		String nom = "null";
		nom = sc.nextLine();

		System.out.println("Equip nacional (1) o Club (2):");
		String tipus = "null";
		int tps = sc.nextInt();
		if(tps == 1){
			tipus = "National Team";
		}else if(tps == 2){
			tipus = "Club";
		}

		System.out.println("Paìs:");
		String pais = "null";
		pais = sc.nextLine();

		String ciutat = "null";
		String pavello = "null";
		if (tipus.equals("Club")){
			System.out.println("Ciutat:");
			ciutat = sc.nextLine();
			System.out.println("Pavelló:");
			pavello = sc.nextLine();
		}

		st.executeUpdate("INSERT INTO team VALUES('"+nom+"','"+tipus+"','"+pais+"','"+ciutat+"','"+pavello+"');");

		st.close();
	}

	public void crearJugador() throws SQLException {
		Scanner sc = new Scanner(System.in);
		Statement st = conn.createStatement();
		ResultSet rs = null;

		System.out.println("--Crear jugador--");
		String equip = "null";
		while(true) {
			System.out.println("De quin equip es?");
			ResultSet resultSet = st.executeQuery("SELECT * FROM team ");
			while (resultSet.next()) {
				System.out.println(resultSet.getString("name"));
			}
			resultSet.close();
			equip = sc.nextLine();
			rs = st.executeQuery("SELECT * FROM team WHERE name = '" + equip + "'");
			if (!rs.next()) {
				System.out.println("No existeix l'equip a la base de dades");
			} else {
				break;
			}
		}

		System.out.println("Número de la federació:");
		String numFederacio = "null";
		numFederacio = sc.nextLine();

		System.out.println("Nom:");
		String nom = "null";
		nom = sc.nextLine();

		System.out.println("Cognoms:");
		String cognoms = "null";
		cognoms = sc.nextLine();

		System.out.println("Data de naixement (YYYY-MM-DD):");
		String data = "null";
		data = sc.nextLine();

		String genere = "null";
		while(true) {
			System.out.println("Génere (M/F):");
			genere = sc.nextLine();
			if(genere.equals("M") || genere.equals("F")){
				break;
			} else {
				System.out.println("No válid (en aquesta base de dades, no a la vida real).");
			}
		}

		System.out.println("Alçada (cm):");
		int altura = 0;
		altura = sc.nextInt();

		System.out.println("Quantitat d'MVPs:");
		int mvp = 0;
		mvp = sc.nextInt();

		st.executeUpdate("INSERT INTO player VALUES('"+numFederacio+"','"+nom+"','"+cognoms+"','"+data+"','"+genere+"','"+altura+"','"+equip+"','"+mvp+"')");

		st.close();
	}

	public void crearPartit() throws SQLException {
		Scanner sc = new Scanner(System.in);
		Statement st = conn.createStatement();
		ResultSet rs = null;

		String equipLocal = "null";
		String equipVisitant = "null";
		while(true) {
			System.out.println("Equip local:");
			rs = st.executeQuery("SELECT * FROM team ");
			while(rs.next()) {
				System.out.println(rs.getString("name"));
			}
			equipLocal = sc.nextLine();

			System.out.println("Equip visitant");
			rs = st.executeQuery("SELECT * FROM team ");
			while(rs.next()) {
				System.out.println(rs.getString("name"));
			}
			equipVisitant = sc.nextLine();

			if (equipLocal.equals(equipVisitant)) {
				System.out.println("No poden ser el mateix equip.");
			} else {
				break;
			}
		}

		System.out.println("Quin dia va ser el partit (YYYY-MM-DD)?");
		String data = "null";
		data = sc.nextLine();

		System.out.println("Quanta gent hi va assistir?");
		int espectadors = 0;
		espectadors = sc.nextInt();

		System.out.println("Qui va ser l'MVP (número de federat)?");
		rs = st.executeQuery("SELECT * FROM player");
		while(rs.next()) {
			System.out.println(rs.getString("first_name")+" "+rs.getString("last_name")+" "+"Número de federat: " + rs.getString("federation_license_code"));
		}
		int mvp = 0;
		mvp = sc.nextInt();

		st.executeUpdate("INSERT INTO match VALUES('"+equipLocal+"','"+equipVisitant+"','"+data+"','"+espectadors+"','"+mvp+"')");
	}

	public void jugadorsSenseEquip() throws SQLException {
		Statement st = conn.createStatement();
		ResultSet rs = null;

		rs = st.executeQuery("SELECT * FROM player WHERE team_name = 'null'");
		System.out.println("--Jugadors sense equip--");
		if(!rs.next()){
			System.out.println("No hi ha jugadors sense equip");
		}
		while(rs.next()) {
			System.out.println(rs.getString("first_name"));
		}
		rs.close();

	}

	public void assignarJugadorEquip() throws SQLException {
		Scanner sc = new Scanner(System.in);
		Statement st = conn.createStatement();
		ResultSet rs = null;

		rs = st.executeQuery("SELECT * FROM player WHERE team_name = 'null'");
		System.out.println("--Jugadors sense equip--");
		if(!rs.next()){
			System.out.println("No hi ha jugadors sense equip");
		}else {
			System.out.println("Quin jugador vols assignar?");
			rs = st.executeQuery("SELECT * FROM player ");
			while (rs.next()) {
				System.out.println(rs.getString("last_name"));
			}
			String jugador = sc.nextLine();
			System.out.println("A quin equip el vols assignar?");
			rs = st.executeQuery("SELECT * FROM team");
			while(rs.next()){
				System.out.println(rs.getString("name"));
			}
			String equip = sc.nextLine();

			st.executeUpdate("UPDATE player SET team_name ("+equip+") WHERE last_name = '"+jugador+"'");
		}

	}

	public void desvicularJugadorDequip() throws SQLException {
		Scanner sc = new Scanner(System.in);
		Statement st = conn.createStatement();
		ResultSet rs = null;

		rs = st.executeQuery("SELECT * FROM player ");
		System.out.println("--Jugadors--");
		if(!rs.next()){
			System.out.println("No hi ha jugadors");
		}else {
			System.out.println("Quin jugador vols desvincular del seu equip?");
			rs = st.executeQuery("SELECT * FROM player ");
			while (rs.next()) {
				System.out.println(rs.getString("last_name"+" "+rs.getString("tesm_name")));
			}
			String jugador = sc.nextLine();

			st.executeUpdate("UPDATE player SET team_name ('null') WHERE last_name = '"+jugador+"'");
		}

	}



	public void sortir() throws SQLException {
		System.out.println("ADÉU!");
		conn.close();
	}
}
