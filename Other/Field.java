package Other;

//Nombre de campo de la base de datos en vez de número para dejarlo lindo :)
	public enum Field {
    	TITLE ("title", "STRING"),
    	AUTHOR ("author", "STRING"),
    	YEAR ("year", "INT"),
    	EDITOR ("editor", "STRING"),
    	ISAVAILABLE ("is_available", "STRING");

		public final String name; //Nombre del capo correspondiente en la base de datos.
		public final String type;
		

    	Field (String n, String type) {
			this.type = type;
			this.name = n;
    	}

    } 
    
