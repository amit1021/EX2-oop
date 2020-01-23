package gameClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
	public static final String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser = "student";
	public static final String jdbcUserPassword = "OOP2020student";
	private static final int MYID = 209046879;
	private static Connection connection = null;
	private static HashMap<Integer, Integer> scoreCompOthers = new HashMap<Integer, Integer>();
	private static String[] allScoresPerLevel = new String[10];
	private static int[] scores = { 125, 436, 0, 713, 0, 570, 0, 0, 0, 480, 0, 1050, 0, 310, 0, 0, 235, 0, 0, 250, 200,
			0, 0, 1000 };
	private static int[] moves = { 290, 580, 0, 580, 0, 500, 0, 0, 0, 580, 0, 580, 0, 580, 0, 0, 290, 0, 0, 580, 290, 0,
			0, 1140 };

	// Constructor
	public DataBase() {
		;
	}

	// return my max level
	public static int getMyLevel(int id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT levelNum FROM Users where userID=" + id + ";";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			int myLevel = Utils.MINUS_INFINITE;
			if (resultSet.next()) {
				myLevel = resultSet.getInt("levelNum");
			}
			resultSet.close();
			statement.close();
			connection.close();
			return myLevel;
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return id;
	}

	// insert the best result of other to the HashMap
	public static void allUsers(int level) {
		String allCustomersQuery = "SELECT * FROM Logs WHERE  levelID = '" + level
				+ "' AND userID > '200000000' AND moves <= '" + moves[level] + "' ORDER BY score DESC;";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while (resultSet.next()) {
				int userID = resultSet.getInt("UserID");
				int score = resultSet.getInt("score");
				// if the result is greater than the required result
				if (score > scores[level]) {
					// if this id exists and this score is bigger then he has then update his score
					if (scoreCompOthers.containsKey(userID)) {
						if (scoreCompOthers.get(userID) < score) {
							scoreCompOthers.put(userID, score);
						}
						// if this id doesnt exists put him in the hashmap
					} else {
						scoreCompOthers.put(userID, score);
					}
				}
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// return the top 10 to specific level
	public String[] scoreTable(int level) {
		allUsers(level);
		int i = 0;
		// sort the hashmap by value
		Map<Integer, Integer> sortedMapByValues = HashMapComperator.getSortedMapByValues(scoreCompOthers);
		for (Map.Entry<Integer, Integer> entry : sortedMapByValues.entrySet()) {
			if (i == 9) {
				allScoresPerLevel[i++] = "   " + entry.getKey() + "    " + entry.getValue();
				break;
			}
			allScoresPerLevel[i++] = "    " + entry.getKey() + "    " + entry.getValue();
		}
		return allScoresPerLevel;
	}

	public static int getMyPlace(int level) {
		int myPlace = 1;
		int myBest = bestScore(level);
		if (myBest == 0) {
			return 0;
		}
		allUsers(level);
		Map<Integer, Integer> sortedMapByValues = HashMapComperator.getSortedMapByValues(scoreCompOthers);
		for (Map.Entry<Integer, Integer> entry : sortedMapByValues.entrySet()) {
			if (entry.getValue() > myBest) {
				myPlace++;
			}
		}
		return myPlace;
	}

	// return my best score to specific level
	public static int bestScore(int level) {
		int score = 0;
		String allCustomersQuery = "SELECT * FROM Logs WHERE userID='" + MYID + "' AND levelID = '" + level
				+ "' AND moves <= '" + moves[level] + "' ORDER BY score DESC;";
		try {
			connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			if (resultSet.next()) {
				score = resultSet.getInt("score");
			}
			return score;
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}
		return 0;
	}

	// return getAllScoresPerLevel array
	public String[] getAllScoresPerLevel() {
		return this.allScoresPerLevel;
	}

	// return moves array
	public int[] getMoves() {
		return this.moves;
	}

}