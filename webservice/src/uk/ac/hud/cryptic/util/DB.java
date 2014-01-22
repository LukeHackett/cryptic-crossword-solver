package uk.ac.hud.cryptic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.solver.Solver.Type;

/**
 * A set of database-related functions that interface with the project's MySQL
 * database on Helios.
 * 
 * @author Stuart Leader
 * @version 0.1
 */
public class DB {

	// Settings class holds connection information
	private static final Settings SETTINGS = Settings.getInstance();
	// Default number of records to retrieve from the DB
	private static final int DEFAULT_RECORDS = 10;

	/**
	 * Entry point to the class used for testing purposes
	 */
	public static void main(String[] args) {
		// Get a default number of Hidden-type clue/solutions.
		Collection<Clue> clues = getTestClues(Type.HIDDEN, true);
		// Print the results
		for (Clue c : clues) {
			System.out.println(c.getClue() + ", " + c.getPattern());
		}
	}

	/**
	 * Obtain a number of test clues from the database that are of a of a
	 * particular type (hidden, for example). These records need to have been
	 * manually marked as being this particular type of clue in the database, so
	 * if you aren't receiving any (or very few) results, you need to mark some
	 * more clues!
	 * 
	 * @param type
	 *            - the type of clue to obtain. e.g. <code>Type.HIDDEN</code> as
	 *            defined by <code>uk.ac.hud.cryptic.solver.Solver.Type</code>
	 * @param unknownCharacters
	 *            - <code>true</code> if characters of the solution should be
	 *            marked with the unknown character symbol, or
	 *            <code>false</code> if the actual character should be written
	 *            to the solution pattern (useful for fast solving)
	 * @param maxRecords
	 *            - an option parameter defining the maximum number of records
	 *            to obtain. This is limited by the marked records of this clue
	 *            type in the database. If this parameter is not supplied, a
	 *            default number (10) of clues is retrieved
	 * @return a collection of text records of the length specified (or not)
	 */
	public static Collection<Clue> getTestClues(Type type,
			boolean unknownCharacters, int... maxRecords) {
		// Will hold the example (test) clues
		Collection<Clue> clues = new ArrayList<>();
		// How many records to retrieve? Default: 10
		int records = maxRecords.length > 0 ? maxRecords[0] : DEFAULT_RECORDS;
		// Initialise the Connection and Statement
		try (Connection conn = DriverManager.getConnection(SETTINGS.getDBURL(),
				SETTINGS.getDBUsername(), SETTINGS.getDBPassword());
				Statement stmt = conn.createStatement()) {

			// Construct the query string
			String query;
			if (type == Type.UNCATEGORISED) {
				query = "SELECT `clue`, `solution` FROM `cryptic_clues` WHERE `type` IS NULL "
						+ "ORDER BY RAND() LIMIT " + records + ";";
			} else {
				query = "SELECT `clue`, `solution` FROM `cryptic_clues` WHERE `type` = '"
						+ type.getDBName()
						+ "' ORDER BY RAND() LIMIT "
						+ records + ";";
			}

			// Retrieve and process the results
			try (ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					String clue = rs.getString(1);
					String solution = rs.getString(2);
					String pattern = SolutionPattern.toPattern(solution,
							unknownCharacters);

					clues.add(new Clue(clue, pattern, solution, type));
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return clues;
	}

	/**
	 * Obtain a specified number of records for each of the clue types that have
	 * been specified as parameters. These are bundled up in a single
	 * <code>Collection</code> to be returned.
	 * 
	 * @param n
	 *            - the number of records to acquire for each clue type
	 * @param unknownCharacters
	 *            - <code>true</code> if characters of the solution should be
	 *            marked with the unknown character symbol, or
	 *            <code>false</code> if the actual character should be written
	 *            to the solution pattern (useful for fast solving)
	 * @param types
	 *            - the types of clue to obtain test data for
	 * @return a collection containing the requested data, if it is present in
	 *         the database
	 */
	public static Collection<Clue> getTestClues(int n,
			boolean unknownCharacters, Type... types) {
		// Will hold the example (test) clues
		Collection<Clue> clues = new ArrayList<>();
		// Get n number of records for each declared type
		for (Type t : types) {
			clues.addAll(getTestClues(t, unknownCharacters, n));
		}
		return clues;
	}

	static {
		// Load the MySQL driver, or try to.
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

} // End of class DB
