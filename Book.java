import java.util.*;
import Other.*;
import Exceptions.*;

public class Book {
    
    private int bookID = 0;
    private String title = null;
    private String author = null;
    private int year = 0;
    private boolean isAvailable = false;
    private String editor = null;
    private ArrayList<Genre> bookGenres = new ArrayList<Genre>();
    
    //Libro creado desde un registro de la base de datos.
    public Book (int bookID) throws IllegalBookIDException {

        ArrayList<String> book = new ArrayList<String>
        (7);
        
        if (Queries.getBookByID(bookID) == null) 
            throw new IllegalBookIDException("No existen libros con el ID '" + bookID + "'.");
        else book.addAll(Queries.getBookByID(bookID));

        this.bookID = bookID;
        this.title = book.get(1);
        this.author = book.get(2);

        if (book.get(3) != null) this.year = Integer.parseInt(book.get(3));

        this.editor = book.get(4);
        this.isAvailable = Val.get(book.get(5));
        this.bookGenres.addAll(Queries.getGenres(this));
    }

    public Book (String title, String author, int year, boolean isAvailable, String editor) throws DuplicateBookException, IllegalYearException, MissingTitleException {

        Calendar cal = Calendar.getInstance();
        //El año debe ser menor al año actual para ser válido
        if (year > cal.get(Calendar.YEAR)) throw new IllegalYearException("El año ingresado para el año de lanzamiento del libro es inválido.");
       
        if (author == null | author == "" | author == "_") author = "Anónimo";

        //Checkea que el libro con parámetros similares no exista ya.
        if (Queries.isDuplicate(title, author, year, editor)) throw new DuplicateBookException("El libro ya está registrado.");

        if (title == null | title == "" | title == " ") throw new MissingTitleException("El título ingresado no es válido.");

        this.title = title;
        this.author = author;
        this.year = year;
        this.isAvailable = isAvailable;
        this.editor = editor;

        this.bookID = Queries.insertBook(this);
        
    }

    public int getBookID() { 
        return this.bookID; 
    }

    public void addGenre (Genre gen) {
        boolean repeated = false;
        for (Genre g : bookGenres) {
            if (g == gen) {
                repeated = true;
                break;
            }
        }

        if (!repeated) {
            bookGenres.add(gen);
            Queries.addGenre(gen, this);
        }
    }
    public void removeGenre (Genre gen) {
        Queries.removeGenre(gen, this);
        this.bookGenres.clear();
        this.bookGenres.addAll(Queries.getGenres(this));
    }
    public void clearGenres() {

        Queries.clearGenres(this);
        this.bookGenres.clear();
    }
    public ArrayList<Genre> getGenresGenre() {
        if (Thread.currentThread().getStackTrace()[2].getClassName() != "Queries") {
            this.bookGenres.clear();
            this.bookGenres.addAll(Queries.getGenres(this));
        }
    
        return this.bookGenres;
    }
    public ArrayList<String> getGenresString() {
        ArrayList<String> bookGenreString = new ArrayList<String>();

        //Para que siempre devuelva una lista actualizada.
        ArrayList<Genre> auxBookGenre = new ArrayList<Genre>();
        auxBookGenre.addAll(this.getGenresGenre());

        for (Genre g : auxBookGenre) 
            bookGenreString.add(g.name);

        return bookGenreString;

    }

    public String getTitle() { 
        if (Thread.currentThread().getStackTrace()[2].getClassName() != "Queries")
            this.title = Queries.getAttribute(Field.TITLE, this);
        return this.title; 
    }
    public void setTitle (String title) throws DuplicateBookException {

        String oldTitle = this.title;
        this.title = title;
        //Si el cambio de título generase un duplicado, se evita el cambio.
        if (Queries.isDuplicate(this)) {
            this.title = oldTitle;
            throw new DuplicateBookException("El cambio de título genera un libro duplicado.");
        } else Queries.updateAttribute(Field.TITLE, this);
    }
    
    public String getAuthor() {
        if (Thread.currentThread().getStackTrace()[2].getClassName() != "Queries" && Thread.currentThread().getStackTrace()[2].getClassName() != "Book" && Thread.currentThread().getStackTrace()[2].getMethodName() != "isDuplicate") 
            this.author = Queries.getAttribute(Field.AUTHOR, this);
        return this.author; 
    }
    public void setAuthor (String author) throws DuplicateBookException {

        String oldAuthor = this.author;
        if (author == null | author == "" | author == " ") this.author = "Anónimo";
        else this.author = author;

        //Si el cambio de autor generase un duplicado, se evita el cambio.
        if (Queries.isDuplicate(this)) {
            this.author = oldAuthor;
            throw new DuplicateBookException("El cambio de autor genera un libro duplicado.");
        } else Queries.updateAttribute(Field.AUTHOR, this);
    }

    public int getYear() { 
        if (Thread.currentThread().getStackTrace()[2].getClassName() != "Queries") 
            this.year = Integer.parseInt(Queries.getAttribute(Field.YEAR, this));
        return this.year; 
    }  
    public void setYear (int year) throws DuplicateBookException, IllegalYearException {
        
        Calendar cal = Calendar.getInstance();
        //El año debe ser menor al año actual para ser válido

        int oldYear = this.year;
        this.year = year;
        //Si el cambio de año generase un duplicado, se evita el cambio.
        if (Queries.isDuplicate(this)) {
            this.year = oldYear;
            throw new DuplicateBookException("El cambio de año genera un libro duplicado.");
        } else if (year > cal.get(Calendar.YEAR)) {
            this.year = oldYear;
            throw new IllegalYearException("El año ingresado para el año de lanzamiento del libro es inválido.");
        }
        else 
            Queries.updateAttribute(Field.YEAR, this);
    }

    public String getEditor() {
        if (Thread.currentThread().getStackTrace()[2].getClassName() != "Queries")  
            this.editor = Queries.getAttribute(Field.EDITOR, this);
        return editor;
    }
    public void setEditor (String editor) throws DuplicateBookException {
        
        String oldEditor = this.editor;
        this.editor = editor;
        if(Queries.isDuplicate(this)) {
            this.editor = oldEditor;
            throw new DuplicateBookException("El cambio de edición genera un libro duplicado.");
        } else Queries.updateAttribute(Field.EDITOR, this);
    }

    public boolean getIsAvailable() {
        if (Thread.currentThread().getStackTrace()[2].getClassName() != "Queries") 
            this.isAvailable = Val.get(Queries.getAttribute(Field.ISAVAILABLE, this));
        
        return this.isAvailable; 
    }
    public void setIsAvailable (boolean isAvailable) {
        
        if (this.isAvailable != isAvailable) {
            this.isAvailable = isAvailable;
            Queries.updateAttribute(Field.ISAVAILABLE, this);
        }
    } 

    @Override
    public String toString() {
        String allGenres = "";
        String available = "No";
        if (this.isAvailable) available = "Sí";

        for (int i = 0; i < this.getGenresString().size() - 1; i++)
            allGenres += this.getGenresString().get(i) + ", ";
        allGenres += this.getGenresString().get(this.getGenresGenre().size() - 1);

        return "Título: '" + this.title + "' | Autor(a): " + this.getAuthor() + " | Año: " + this.year + " | Edición: " + this.editor + " | ¿Disponible? " + available + "\nGéneros: " + allGenres;
    }

    private enum Val {
        TRUE,
        FALSE;

        public static boolean get(String s) {
            return s == "T";
        }


    }
    
}
