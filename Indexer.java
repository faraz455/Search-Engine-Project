import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import org.apache.commons.csv.*;
import com.google.gson.*;

public class Indexer {
	
	// generates a forward index and a lexicon for a given csv file
	public static void genforwardIndex(String filename) throws IOException {
		
		// ArrayList will contain the forward index
		ArrayList<Object> index = new ArrayList<>();
		// string will contain the text to be parsed
		String parsingString;
		// gson will be used to convert our ArrayList into a JSON file
		Gson gson = new Gson();

		File csvData = new File(filename);
		
		// this parser will go through record-by-record in the csv
		CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.EXCEL);


		// loop parses and adds each record into the index
		for (CSVRecord currentRecord : parser) {
			
			// format is {imdb_id:{words:[nhits, hit1, hit2, hit3.....hitn]}}
			HashMap<String, HashMap<String, ArrayList<Integer>>> map = new HashMap<>();
			
			// column 7 is movie title, column 1 is the movie overview
			// string is all lowercase in order to make queries easier to implement
			parsingString = (currentRecord.get(7) + " " + currentRecord.get(1)).toLowerCase();

			map.put(currentRecord.get(0), Parser.parseWords(parsingString));
			
			index.add(map);
			
			System.out.println(currentRecord.get(0));
		
		}
		
		// index ArrayList converted to JSON
		String json = gson.toJson(index);
		// puts a new-line after every movie record
		json = json.replaceAll("\\},", "},\n");
		
		// writes out index json to new file
		try (PrintWriter out = new PrintWriter("forward-index.json")) {
		    out.println(json);
		}
		
		// lexicon ArrayList converted to JSON
		json = gson.toJson(Parser.lexicon);
		
		// writes out lexicon json to new file
		try (PrintWriter out = new PrintWriter("lexicon.json")) {
			out.println(json);
		}

	}
}