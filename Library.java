import java.util.*;
import Other.*;
import java.text.Collator;
import Exceptions.IllegalBookIDException;

public class Library {
    private ArrayList<Book> allBooks = new ArrayList<Book>();

    public Library () {
        updateLibrary();   
    }

    public void removeBookID (int id) {
        Queries.removeBook(id);
    }

    public int getBookIndex (int id) {
        int index = -1;
        for (Book b : this.allBooks) {
            if (id == b.getBookID()) index = b.getBookID();
        }
        return index;
    }

    public int getBookID (int index) {
        return allBooks.get(index).getBookID();
    }

    public ArrayList<Book> searchByTitle (String s) {
        
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.PRIMARY);

        ArrayList<Book> bookByTitle = new ArrayList<Book>();
        for (Book b : allBooks) {
            if (instance.compare(b.getTitle(), s) != 0) {
                for (String w : b.getTitle().split(" ")) {
                    if (instance.compare(w, s) == 0) {
                        bookByTitle.add(b);
                        break;
                    }
                }
            
            } else bookByTitle.add(b);
        }

        return bookByTitle;
    } 

    public ArrayList<Book> getAllBooks() {
        updateLibrary();
        return allBooks;
    }

    public ArrayList<String> getAllAuthors () {
        updateLibrary();
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.PRIMARY);

        ArrayList<String> allAuthors = new ArrayList<String>();
        boolean repeated = false;

        for (Book b : allBooks) {
            for (String a : allAuthors) {
                if (instance.compare(a, b.getAuthor()) == 0) {
                    repeated = true;
                    break;
                }
            }
            if (!repeated) allAuthors.add(b.getTitle());
            else repeated = false;
        }

        return allAuthors;
    }
    
    public ArrayList<Book> searchByAuthor (String s) {
        updateLibrary();
        if (s.length() == 1) return this.searchByAuthorInitial(s);
        else if (s.length() == 0) return null;

        ArrayList<Book> authorBooks = new ArrayList<Book>();
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.PRIMARY);

        for (Book b : allBooks) {
            if (instance.equals(s, b.getTitle()))
                authorBooks.add(b);
        }

        return authorBooks;
    }
    
    public ArrayList<Book> searchByGenre (Genre g) {
        updateLibrary();
        ArrayList<Book> genreBooks = new ArrayList<Book>();
        for (Book b : allBooks) {
            for (Genre gen : b.getGenresGenre())
                if (gen == g) {
                    genreBooks.add(b);
                    break;
                }
        }

        return genreBooks;
    }

    public ArrayList<Book> searchByEditor (String s) {
        updateLibrary();
        ArrayList<Book> editorBooks = new ArrayList<Book>();
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.PRIMARY);
        
        for (Book b : allBooks) {
            if (instance.equals(b.getEditor(), s))
                editorBooks.add(b);
        }

        return editorBooks;
    }

    public ArrayList<Book> searchByYear (int y) {
        updateLibrary();
        ArrayList<Book> yearBooks = new ArrayList<Book>();

        for (Book b : allBooks) {
            if (b.getYear() == y) 
                yearBooks.add(b);
        }

        return yearBooks;
    }

    public ArrayList<Book> searchByYear (int startingYear, int endingYear) {
        updateLibrary();
        ArrayList<Book> yearBooks = new ArrayList<Book>();

        for (Book b : allBooks) {
            if (b.getYear() >= startingYear && b.getYear() <= endingYear)
                yearBooks.add(b);
        }
        return yearBooks;

    }

    private ArrayList<Book> searchByAuthorInitial (String s) {
        updateLibrary();
        ArrayList<Book> authorBooks = new ArrayList<Book>();
        for (Book b : allBooks) {
            if (b.getAuthor().substring(0, 1).equalsIgnoreCase(s))
                authorBooks.add(b);
        }
        return authorBooks;
    }
    
    private void updateLibrary () {
        allBooks.clear();
        ArrayList<Integer> allBookIDs = new ArrayList<Integer>();
        allBookIDs.addAll(Queries.getAllBookID());
        try {
            for (Integer i : allBookIDs)
                allBooks.add(new Book(i));

        } catch (IllegalBookIDException ibe) {
            System.out.println("Ha ocurrido un problema recuperardo los libros almacenados.\nEn teoría esto nunca debería pasar con este programa pero ni Dios sabe cómo llegamos acá.");
        }     
    }

}
