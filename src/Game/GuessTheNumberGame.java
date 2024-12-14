package Game;
import java.util.*;
import java.time.Duration;
import java.time.Instant;
import java.sql.*;
public class GuessTheNumberGame {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/guess_the_number_game";
			    private static final String DB_USER = "root"; 
			    private static final String DB_PASSWORD = "1234";  
			    private static String generateUniqueNumber() {
			        List<Integer> digits = new ArrayList<>();
			        for (int i = 0; i <= 9; i++) {
			            digits.add(i);
			        }
			        Collections.shuffle(digits);
			        StringBuilder number = new StringBuilder();
			        for (int i = 0; i < 4; i++) {
			            number.append(digits.get(i));
			        }
			        return number.toString();
			    }
			    private static void saveScore(String playerName, long time, int attempts) {
			        double score = time + attempts * 2.0;  // Formula to combine time and guesses
			        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/play","root","1234")) {
			            String sql = "INSERT INTO scores(name, time, guesses, score) VALUES(?, ?, ?, ?)";
			            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			                pstmt.setString(1, playerName);
			                pstmt.setLong(2, time);
			                pstmt.setInt(3, attempts);
			                pstmt.setDouble(4, score);
			                pstmt.executeUpdate();
			            }
			        } catch (SQLException e) {
			            System.out.println("Database error: " + e.getMessage());
			        }
			    }
			    private static void displayBestScore() {
			        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			            String sql = "SELECT name, time, guesses, score FROM scores ORDER BY score ASC LIMIT 1";
			            try (Statement stmt = conn.createStatement();
			                 ResultSet rs = stmt.executeQuery(sql)) {
			                if (rs.next()) {
			                    String name = rs.getString("name");
			                    long time = rs.getLong("time");
			                    int guesses = rs.getInt("guesses");
			                    System.out.println("Best Score: " + name + " | Time: " + time + " seconds | Guesses: " + guesses);
			                } else {
			                    System.out.println("No scores yet.");
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("Database error: " + e.getMessage());
			        }
			    }
			    public static void main(String[] args) {
			        Scanner scanner = new Scanner(System.in);
			        System.out.println("Welcome to the Guess the Number game!");
			        System.out.print("Enter your name to start the game: ");
			        String playerName = scanner.nextLine();
			        String secretNumber = generateUniqueNumber();
			        System.out.println("The computer has selected a unique 4-digit number. Try to guess it!");
		            Instant startTime = Instant.now();
			        int attempts = 0;
			        while (true) {
			            System.out.print("Enter your 4-digit guess: ");
			            String userGuess = scanner.nextLine();
			            if (userGuess.length() != 4 || !userGuess.matches("\\d+") || userGuess.chars().distinct().count() != 4) {
			                System.out.println("Invalid input! Enter a 4-digit number with unique digits.");
			                continue;
			            }
				    attempts++;
			            int plus = 0, minus = 0;
			            for (int i = 0; i < 4; i++) {
			                if (userGuess.charAt(i) == secretNumber.charAt(i)) {
			                    plus++;
			                } else if (secretNumber.indexOf(userGuess.charAt(i)) != -1) {
			                    minus++;
			                }
			            }
			            System.out.println("Feedback: " + "+".repeat(plus) + "-".repeat(minus));
			            if (plus == 4) {
			                Instant endTime = Instant.now();
			                long timeTaken = Duration.between(startTime, endTime).toSeconds();
			                System.out.println("Congratulations " + playerName + "! You guessed the number " + secretNumber + ".");
			                System.out.println("It took you " + timeTaken + " seconds and " + attempts + " attempts.");
			                saveScore(playerName, timeTaken, attempts);
			                displayBestScore();
			                
			                break;
			            }
			        }
                              scanner.close();
			    }
			}









