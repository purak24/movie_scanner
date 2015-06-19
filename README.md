# movie_scanner
Helps the user to organise his/her movie collection systematically

Scans a particular directory specified by the user, for movies. Then cross-references the movies with IMDB server database, to retrive the rating, genre and summary of the movie. An additional attribute for seen(a boolean field that specifies whether the user has seen/not seen the movie) is present. The data is fetched from the server only for the first time a new movie is scanned. Previously present movie data is stored locally in a database.
