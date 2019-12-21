import java.io.*;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {

		// generates forward index and lexicon
//		Indexer indexer = new Indexer();
//		
//		indexer.genforwardIndex("data/movies_metadata.csv");
//		indexer.invertIndex("forward-index.json", "lexicon.json");

		// initialize Searcher object with inverted index and titles
		Searcher searcher = new Searcher("inverted-index.json", "titles.json");

		// Temporarily use Scanner for input (GUI to be implemented)
		Scanner s = new Scanner(System.in);
		System.out.println("Enter your query: ");
		String query = s.nextLine();
		s.close();

		// gets query, ranks results and then returns a list of matching ids
		String[] results = searcher.executeQuery(query);

		for (String a : results) {
			String title = searcher.titles.get(a);

			System.out.println(title);
		}
	}

}
