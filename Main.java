import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {

		// generates forward index and lexicon
		Indexer.genforwardIndex("data/movies_metadata.csv");
		// generates inverted index
		Indexer.invertIndex("forward-index.json", "lexicon.json");

	}

}
