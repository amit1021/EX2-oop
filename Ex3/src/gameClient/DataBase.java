package gameClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DataBase {
	public static final String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser = "student";
	public static final String jdbcUserPassword = "OOP2020student";
	private final int MYID = 205699952;
	private Statement st = null;
	private Connection connection = null;
	private HashMap<Integer, Integer> myBestScore = new HashMap<Integer, Integer>();

	// Constructor
	public DataBase() {
		// connect to data base
		connect();
		// initiate the HashMap
		for (int i = 0; i < 24; i++) {
			myBestScore.put(i, 0);
		}
		getMaxLevel(MYID);
	}

	// create connect to the data base
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// close the connection
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printLog(int id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where userID=" + id;

			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			int ind = 0;
			while (resultSet.next()) {
				System.out.println(ind + ") Id: " + resultSet.getInt("UserID") + ", level: "
						+ resultSet.getInt("levelID") + ", score: " + resultSet.getInt("score") + ", moves: "
						+ resultSet.getInt("moves") + ", time: " + resultSet.getDate("time"));
				ind++;
			}
			resultSet.close();
			statement.close();
			connection.close();
		}

		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// print my max level and score
	public void getMaxLevel(int id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where userID=" + id;

			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			int numOfGames = 0;
			int maxLevel = Utils.MINUS_INFINITE;
			while (resultSet.next()) {
				int level = resultSet.getInt("levelID");
				int score = resultSet.getInt("score");
				int moves = resultSet.getInt("moves");
				if (maxLevel < level) {
					maxLevel = level;
				}
				bestScorePerLevel(level, score, 200);
				numOfGames++;
			}
			resultSet.close();
			statement.close();
			connection.close();
			System.out.println("My Level: " + maxLevel + "   Number Of Games: " + numOfGames);
			printMyBest();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// check if the current score is more then the best score, And the moves under
	// the conditions
	public void bestScorePerLevel(int level, int score, int moves) {
		int level_case = level;
		switch (level_case) {
		case 0:
			if (moves < 290 && score > myBestScore.get(0)) {
				myBestScore.put(0, score);
			}
			break;
		case 1:
			if (moves < 580 && score > myBestScore.get(1)) {
				myBestScore.put(1, score);
			}
			break;
		case 3:
			if (moves < 580 && score > myBestScore.get(3)) {
				myBestScore.put(3, score);
			}
			break;
		case 5:
			if (moves < 500 && score > myBestScore.get(5)) {
				myBestScore.put(5, score);
			}
			break;
		case 9:
			if (moves < 580 && score > myBestScore.get(9)) {
				myBestScore.put(9, score);
			}
			break;
		case 11:
			if (moves < 580 && score > myBestScore.get(11)) {
				myBestScore.put(11, score);
			}
			break;
		case 13:
			if (moves < 580 && score > myBestScore.get(13)) {
				myBestScore.put(13, score);
			}
			break;
		case 16:
			if (moves < 290 && score > myBestScore.get(16)) {
				myBestScore.put(16, score);
			}
			break;
		case 19:
			if (moves < 580 && score > myBestScore.get(19)) {
				myBestScore.put(19, score);
			}
			break;
		case 20:
			if (moves < 290 && score > myBestScore.get(20)) {
				myBestScore.put(20, score);
			}
			break;
		case 23:
			if (moves < 1140 && score > myBestScore.get(23)) {
				myBestScore.put(23, score);
			}
			break;
		default:
			break;
		}
	}

	// print the best scores of all the levels that i pass
	public void printMyBest() {
		for (int i = 0; i < myBestScore.size(); i++) {
			if (myBestScore.get(i) != 0) {
				System.out.println("Level: " + i + "   Best Score: " + myBestScore.get(i));
			}
		}
	}

	public static void main(String[] args) {
		DataBase db = new DataBase();
		// db.printLog(205699952);
	}

}
