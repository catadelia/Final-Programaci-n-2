import Exceptions.DuplicateBookException;
import Exceptions.IllegalBookIDException;
import Exceptions.IllegalYearException;
import Other.Genre;

public class Main {
	public static void main(String[] args) {
		try {

			Library l = new Library();

			l.removeBookID(23);

			for (Book c : l.getAllBooks())
				System.out.println(c.toString());

		
		}
		catch (Exception e) { System.out.println(e);}
	}
}