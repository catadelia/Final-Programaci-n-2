import java.sql.*;
import java.util.*;
import Other.*;

//La clase se encarga de TODAS las interacciones con la base de datos para que ninguna otra clase tenga que tocarla.
public abstract class Queries {

	private static String USER = "root";
 	private static String PASS = "root";
	private static String DBNAME = "Library";
	private static String URL = "jdbc:mysql://localhost:3306/" + DBNAME;
	private static Connection con = null;
	
	public static int insertBook (Book b) {
		
		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase Book.java puede acceder a éste método.");
		
		int bookID = -1;
		try {
			
			connectToDatabase();
			String query = "INSERT INTO Books (title, author, year, editor, is_available) VALUES (?, ?, ?, ?, ?);";

			String isAvailable = "F";
			if (b.getIsAvailable()) isAvailable = "T";
			PreparedStatement stat = con.prepareStatement(query);

			stat.setString(1, b.getTitle());
			stat.setString(2, b.getAuthor());
			stat.setInt(3, b.getYear());
			stat.setString(4, b.getEditor());
			stat.setString(5, isAvailable);

			stat.executeUpdate();

			query = "SELECT LAST_INSERT_ID()";
			PreparedStatement stat2 = con.prepareStatement(query);
			ResultSet rs = stat2.executeQuery();
			rs.next();
			bookID = rs.getInt(1);

			rs.close();
			stat.close();
			
		} catch (SQLException se) { System.out.println(se + "en insertBook"); } 
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
		
		return bookID;
	}

	public static void removeBook (int id) {
		try {
			connectToDatabase();
			String query1 = "DELETE FROM Books WHERE book_ID = ?";
			String query2 = "DELETE FROM book_genre WHERE book_ID = ?";

			PreparedStatement stat1 = con.prepareStatement(query1);
			stat1.setInt(1, id);
			stat1.executeUpdate();
			stat1.close();

			PreparedStatement stat2 = con.prepareStatement(query2);
			stat2.setInt(1, id);
			stat2.executeUpdate();
			stat2.close();


		} catch (SQLException se) { System.out.println(se + "in RemoveBook()"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
	}

	public static ArrayList<String> getBookByID (int bookID) {

		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase 'Book' puede acceder a éste método.");

		ResultSet rs = null;
		ArrayList<String> results = new ArrayList<String>(6);

		try {
			connectToDatabase();
			String query = "SELECT * FROM Books WHERE book_ID = ?";

			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1, bookID + "");
			rs = stat.executeQuery();

			if (!rs.next()) results = null;
			else {
				results.add(rs.getInt(1) + "");
				for (int i = 2; i <= 6; i++) {
					//El enum Field contiene qué tipo de dato es cada uno de los campos, en orden.
					if (Field.values()[i-2].type == "INT") results.add(rs.getInt(i) + "");
					else results.add(rs.getString(i));
				}
			}

			rs.close();
			stat.close();

		} catch (SQLException se) { System.out.println(se + "en getBookByID"); }
		finally { try {con.close(); } catch (Exception e) {/*Ignorado*/} }

		return results;

	}

	public static void updateBook (Book b) {
		try {
			//TODO: genres
			connectToDatabase();
			String query = "UPDATE Books SET title = ?, author = ?, year = ?, is_available = ?, editor = ? WHERE book_ID = ?";

			String isAvailable = "F";
			if (b.getIsAvailable()) isAvailable = "T";
			
			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1, b.getTitle());
			stat.setString(2, b.getAuthor());
			stat.setInt(3, b.getYear());
			stat.setString(4, isAvailable);
			stat.setString(5, b.getEditor());
			stat.setInt(6, b.getBookID());

			stat.executeUpdate();
			stat.close();

		} catch (SQLException se) { System.out.println(se + "en updateBook"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
	}

	public static void updateAttribute (Field f, Book b) {
		
		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase 'Book' puede acceder a éste método.");

		try {
			connectToDatabase();
			String query = "UPDATE Books SET " + f.name + " = ? WHERE book_ID = ?;";
			PreparedStatement stat = con.prepareStatement(query);

			switch (f) {
				case TITLE:
					stat.setString(1, b.getTitle());
					break;
				case AUTHOR:
					stat.setString(1, b.getAuthor());
					break;
				case YEAR:
					stat.setInt(1, b.getYear());
					break;
				case EDITOR:
					stat.setString(1, b.getEditor());
					break;
				case ISAVAILABLE:
					stat.setString(1, Val.get(b.getIsAvailable()));
					break;
			}
				
			stat.setInt(2, b.getBookID());
			stat.executeUpdate();

			stat.close();
			
		} catch (SQLException se) { System.out.println(se + "en updateAttribute"); } 
		finally { try { con.close(); } catch (Exception e) {/*Ignorado.*/} }
	}

	public static void addGenre (Genre g, Book b) {
		
		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase 'Book' puede acceder a éste método.");
		
		if (g != Genre.NONE) {
			try {
				//Genre g identifica al campo que cambiará.
				connectToDatabase();
				String query = "INSERT INTO book_genre (book_ID, genre) VALUES (?, ?);";
				PreparedStatement stat = con.prepareStatement(query);
				stat.setInt(1, b.getBookID());
				stat.setString(2, g.name);
				stat.executeUpdate();

				stat.close();

			} catch (SQLException se) { System.out.println(se + "en addGenre"); }
			finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
		}
	}

	public static void removeGenre (Genre g, Book b) {
		try {
			connectToDatabase();
			String query = "DELETE FROM book_genre WHERE book_id = ? AND genre = ?;";
			PreparedStatement stat = con.prepareStatement(query);

			stat.setInt(1, b.getBookID());
			stat.setString(2, g.name);

			stat.executeUpdate();
			stat.close();

		} catch (SQLException se) { System.out.println(se + "en removeGenre"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
	}

	public static ArrayList<Genre> getGenres (Book b) {
		
		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase 'Book' puede acceder a éste método.");
		
		ArrayList<Genre> allGenres = new ArrayList<Genre>();
		try {
			connectToDatabase();
			String query = "SELECT * FROM book_genre WHERE book_id = ?;";

			PreparedStatement stat = con.prepareStatement(query);
			stat.setInt(1, b.getBookID());

			ResultSet rs = stat.executeQuery();
			while (rs.next()) allGenres.add(Genre.toGenre(rs.getString(2)));

			stat.close();

		} catch (SQLException se) {System.out.println(se + "en getGenres"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
		
		//Si el libro no tiene ningún género asignado se le agrega Genre.NONE.
		if (allGenres.size() == 0) allGenres.add(Genre.NONE);
		return allGenres;
	}

	public static void clearGenres (Book b) {

		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase 'Book' puede acceder a éste método.");
		
		try {
			connectToDatabase();
			String query = "DELETE FROM book_genre WHERE book_ID = ?;";
			PreparedStatement stat = con.prepareStatement(query);

			stat.setInt(1, b.getBookID());
			stat.executeUpdate();
			stat.close();
		} catch (SQLException se) { System.out.println(se + "en clearGenres"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
	}

	public static String getAttribute (Field f, Book b) {

		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase 'Book' puede acceder a éste método.");

		String attribute = null;
		try {
			connectToDatabase();
			String query = "SELECT " + f.name + " FROM Books WHERE book_ID = ?;";
			PreparedStatement stat = con.prepareStatement(query);
			stat.setInt(1, b.getBookID());
			ResultSet rs = stat.executeQuery();

			if (rs.next()) {
				if (f.type == "INT") attribute = rs.getInt(1) + "";
				else attribute = rs.getString(1);

			} else attribute = null;

			rs.close();
			stat.close();

		} catch (SQLException se) { System.out.println(se + "en getAttribute"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }

		return attribute;
	}

	public static boolean isDuplicate (String title, String author, int year, String editor) {
		
		boolean dup = false;
		try {

			connectToDatabase();
			String query = "SELECT * FROM Books WHERE title = ? AND author = ? AND year = ? AND editor = ?;";

			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1, title);
			stat.setString(2, author);
			stat.setInt(3, year);
			stat.setString(4, editor);
			ResultSet rs = stat.executeQuery();

			//Se fija si el resultado no está vacío. Si no lo está significa que el libro ingresado ya está registrado.
			if (rs.next()) dup = true;

			rs.close();
			stat.close();

		} catch (SQLException se) { System.out.println(se + "in isDuplicate()"); }
		finally { try { con.close();} catch (Exception e) {/*Ignorado*/} };


		return dup;
	}

	public static boolean isDuplicate (Book b) {
		
		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Book") 
			throw new IllegalCallerException("Sólo la clase 'Book' puede acceder a éste método.");

		boolean dup = false;
		try {

			connectToDatabase();
			String query = "SELECT * FROM Books WHERE title = ? AND author = ? AND year = ? AND editor = ? AND book_id NOT IN (?)";

			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1, b.getTitle());
			stat.setString(2, b.getAuthor());
			stat.setInt(3, b.getYear());
			stat.setString(4, b.getEditor());
			stat.setInt(5, b.getBookID());
			System.out.println(b.getEditor());

			ResultSet rs = stat.executeQuery();
			//Se fija si el resultado no está vacío. Si no lo está significa que el libro ingresado ya está registrado.
			if (rs.next()) dup = true;

			rs.close();
			stat.close();

		} catch (SQLException se) { System.out.println(se + "in isDuplicate()"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }
		return dup;
	}

	public static ArrayList<Integer> getAllBookID() {
		if (Thread.currentThread().getStackTrace()[2].getClassName() != "Library") 
			throw new IllegalCallerException("Sólo la clase 'Library' puede acceder a éste método.");
		ArrayList<Integer> allIDs = new ArrayList<Integer>(0);

		try {
			connectToDatabase();
			String query = "SELECT book_ID FROM Books;";
			PreparedStatement stat = con.prepareStatement(query);
			ResultSet rs = stat.executeQuery();

			while (rs.next()) allIDs.add(rs.getInt(1));

			rs.close();
			stat.close();

		} catch (SQLException se) { System.out.println(se + "en getAllBookId"); }
		finally { try { con.close(); } catch (Exception e) {/*Ignorado*/} }

		return allIDs;
	}

	private static void connectToDatabase() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(URL, USER, PASS);
		} catch (Exception e) { System.out.println(e + "connecting to database failed."); }	
	}

	private enum Val {
        TRUE,
        FALSE;

        public static String get(boolean t) {
            if (t) return "T";
            else return "F";
        }


    }
}


