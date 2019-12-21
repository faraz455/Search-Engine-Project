import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Searcher {

	public HashMap<String, HashMap<String, ArrayList<Integer>>> invertedIndex;
	public HashMap<String, String> titles;
	
	// loads inverted index and titles from given file paths
	public Searcher(String invIndexFilepath, String titlesFilepath) throws IOException {
		Gson gson = new Gson();
		
		String invIndexString = Files.readString(Paths.get(invIndexFilepath));
		String titlesString = Files.readString(Paths.get(titlesFilepath));
		
		Type indexType = new TypeToken<HashMap<String, HashMap<String, ArrayList<Integer>>>>() {}.getType();
		Type titlesType = new TypeToken<HashMap<String, String>>() {}.getType();
		
		invertedIndex = gson.fromJson(invIndexString, indexType);
		titles = gson.fromJson(titlesString, titlesType);
	}

	public String[] executeQuery(String query) {
		
		String[] keywords = query.split("\\s+");
		ArrayList<Movie> movies = new ArrayList<>();
		
		// if it is a single-word query
		if (keywords.length == 1) {
			HashMap<String, ArrayList<Integer>> idHashMap = invertedIndex.get(keywords[0]);
			
			for (Map.Entry<String, ArrayList<Integer>> entry : idHashMap.entrySet()) {
					movies.add(new Movie(entry.getKey(), entry.getValue()));
			}
			
			Collections.sort(movies);
			
			String[] results = new String[movies.size()];
			
			for (int i = 0; i < results.length; i++)
				results[i] = movies.get(i).getID();
			
			return results;
		}
		// if it is a multi-word query
		else if (keywords.length > 1){
			// first retrieve all imdb_ids for each word and extract common ids
			// then sort them according to their proximity to each other
			// and sort them once more according to frequency
			
			return null;
		}
		else return null;
	}

	public static class Movie implements Comparable<Movie>{
		String imdb_id;
		ArrayList<Integer> movieHitInfo;
		
		public Movie(String movie_id, ArrayList<Integer> movieData) {
			imdb_id = movie_id;
			movieHitInfo = movieData;
		}

		public String getID() {
			return imdb_id;
		}
		
		// comparing function for sorting according to frequency in descending order
		@Override
		public int compareTo(Movie movie) {
			int result = movie.movieHitInfo.get(0) - this.movieHitInfo.get(0);
			return result;
		}
	}

}
