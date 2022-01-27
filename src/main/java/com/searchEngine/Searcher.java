package com.searchEngine;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Searcher {

	public HashMap<String, HashMap<String, ArrayList<Integer>>> invertedIndex;
	public HashMap<String, String> titles;

	// loads inverted index and titles from given file paths
	public Searcher(URI invIndexFilepath, URI titlesFilepath) throws IOException {
		Gson gson = new Gson();

		String invIndexString = Files.readString(Paths.get(invIndexFilepath));
		String titlesString = Files.readString(Paths.get(titlesFilepath));

		Type indexType = new TypeToken<HashMap<String, HashMap<String, ArrayList<Integer>>>>() {
		}.getType();
		Type titlesType = new TypeToken<HashMap<String, String>>() {
		}.getType();

		invertedIndex = gson.fromJson(invIndexString, indexType);
		titles = gson.fromJson(titlesString, titlesType);
	}

	// gets records of movies, ranks them and returns their IMDb IDs
	public String[] executeQuery(String query) {

		// convert query to lowercase for ease of searching
		query = query.toLowerCase();
		
		String[] keywords = query.split("\\s+");
		ArrayList<Movie> movies = new ArrayList<>();

		// if it is a single-word query
		if (keywords.length == 1) {
			// retrieve all imdb_ids and sort them by frequency
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
		else if (keywords.length > 1) {
			// first retrieve all imdb_ids for each word and extract common ids
			// then sort them according to their proximity to each other
			// and sort them once more according to frequency

			HashMap<String, ArrayList<Integer>> idHashMap = invertedIndex.get(keywords[0]);
			HashMap<String, ArrayList<Integer>> compIdHashMap;	// variable used for comparisons
			Movie compMovieA, compMovieB;						// variables used for comparisons

			// add all IDs of first word in query to the ArrayList
			for (Map.Entry<String, ArrayList<Integer>> entry : idHashMap.entrySet()) {
				movies.add(new Movie(entry.getKey(), entry.getValue()));
			}
			
			// for every keyword after the first
			for (int i = 1; i < keywords.length; i++) {
				compIdHashMap = invertedIndex.get(keywords[i]);
				
				for (Map.Entry<String, ArrayList<Integer>> entry : compIdHashMap.entrySet()) {
					// if a movie is common between the two hashmaps,
					// find the smallest difference in proximity between the two words in that movie
					if (idHashMap.containsKey(entry.getKey())) {
						compMovieA = movies.get(getMovieIndex(movies, entry.getKey()));
						compMovieB = new Movie(entry.getKey(), entry.getValue());
						
						int[] posInfo = getPositionScore(compMovieA.movieHitInfo, compMovieB.movieHitInfo);
						
						if (compMovieA.positionScore == Integer.MAX_VALUE)
							compMovieA.positionScore = posInfo[0];
						else compMovieA.positionScore -= posInfo[0];

						ArrayList<Integer> newHitInfo = new ArrayList<>();
						newHitInfo.add(compMovieA.movieHitInfo.get(0) + compMovieB.movieHitInfo.get(0));
						newHitInfo.add(posInfo[1]);
						compMovieA.setHitInfo(newHitInfo);
					}
					// otherwise, just add the new movie to the ArrayList  
					else 
						movies.add(new Movie(entry.getKey(), entry.getValue()));
				}
			}
			
			Collections.sort(movies);

			String[] results = new String[movies.size()];

			for (int i = 0; i < results.length; i++)
				results[i] = movies.get(i).getID();

			return results;
		} else
			return null;	// if query is empty
	}

	// computes the smallest difference that can be found between two integers
	// given these two arrays
	private int[] getPositionScore(ArrayList<Integer> movieHitInfo, ArrayList<Integer> movieHitInfo2) {
		// initial difference
		int minDiff = Math.abs(movieHitInfo2.get(1) - movieHitInfo.get(1));
		// position of right-most word in the calculation of minimum difference
		int minPos = 1;
		
		for (int i = 1; i < movieHitInfo.size(); i++) {
			for (int j = 2; j < movieHitInfo2.size(); j++) {
				if (Math.abs(movieHitInfo2.get(j) - movieHitInfo.get(i)) < minDiff) {
					minDiff = Math.abs(movieHitInfo2.get(j) - movieHitInfo.get(i));
					minPos = j;
				}
			}
		}
		
		return new int[]{minDiff, minPos};
	}

	// utility method which gets a specific movie's index from an ArrayList
	private int getMovieIndex(ArrayList<Movie> movies, String key) {
		for (int i = 0; i < movies.size(); i++) {
			if (movies.get(i).imdb_id.equals(key))
				return i;
		}
		return -1;
	}

	// class which contains data about each Movie, used for ranking
	public static class Movie implements Comparable<Movie> {
		String imdb_id;
		ArrayList<Integer> movieHitInfo;
		int positionScore;

		public Movie(String movie_id, ArrayList<Integer> movieData) {
			imdb_id = movie_id;
			movieHitInfo = movieData;
			// positionScore is max by default so that hits with single words
			// are displayed last
			positionScore = Integer.MAX_VALUE;
		}

		public void setHitInfo(ArrayList<Integer> newMovieHitInfo) {
			movieHitInfo = newMovieHitInfo;
		}

		public String getID() {
			return imdb_id;
		}

		// comparing function which sorts according to positionScore in ascending order
		// if positionScore is same, sorts according to frequency in descending order
		@Override
		public int compareTo(Movie movie) {

			if (this.positionScore > movie.positionScore)
				return 1;
			else if (this.positionScore < movie.positionScore)
				return -1;

			else if (movie.movieHitInfo.get(0) > this.movieHitInfo.get(0))
				return 1;
			else if (movie.movieHitInfo.get(0) < this.movieHitInfo.get(0))
				return -1;
			else return 0;
		}
	}

}
