package Other;

import java.text.Collator;

public enum Genre {

    FICTION ("Ficción"),
    NONFICTION ("No Ficción"),
    SCIFI ("Ciencia Ficción"),
    FANTASY ("Fantasía"),
    THRILLER ("Thriller"),
    YOUNGADULT ("Adulto Joven"),
    MYSTERY ("Misterio"),
    ACTION ("Acción"),
    HUMOR ("Humor"),
    DRAMA ("Drama"),
    COMEDY ("Comedia"),
    CLASSIC ("Clásico"),
    GRAPHICNOVEL ("Novela Gráfica"),
    CHILDRENS ("Niños"),
    CRIMINAL ("Policial"),
    ROMANCE ("Romance"),
    BIOGRAPHY ("Biografía"),
    AUTOBIOGRAPHY ("Autobiografía"),
    HISTORY ("Historia"),
    POETRY ("Poesía"),
    EXPLICIT ("Explícito"),
    ADULT ("Adulto"),
    OTHER ("Otro"),
    NONE ("Ninguno");

    //El nombre del género escrito en español.
    public final String name;

    Genre (String s) { this.name = s; }

    //Devuelve la constante correspondiente sin considerar tildes ni mayúsculas.
    //Si no coincide con ninguno de los géneros simplemente le pone "Otro".
    public static Genre toGenre (String s) {

        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.PRIMARY);
        Genre finalGenre = OTHER;

        for (Genre g : Genre.values()) {
            if(instance.compare(s, g.name) == 0) {
                finalGenre = g;
                break;
            }
        }

        return finalGenre;
    }

}

