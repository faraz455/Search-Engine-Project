import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.csv.*;
import com.google.gson.*;

public class Indexer {
	public static void main(String[] args) throws IOException {
		// map key is imdb_id and map value is overview
		ArrayList<HashMap<String, HashMap<String, Integer>>> al = new ArrayList<HashMap<String, HashMap<String, Integer>>>();
		
		String parsingString;
		File csvData = new File("data/movies_metadata.csv");
		
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();

		// this parser will go through record-by-record in the csv
		CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.EXCEL);


		// this should parse and add all records to the hashmap
		for (CSVRecord currentRecord : parser) {

			HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
			
			parsingString = (currentRecord.get(7) + " " + currentRecord.get(1)).toLowerCase();

			map.put(currentRecord.get(0), Parser.parseWords(parsingString));
			
			al.add(map);
			
			System.out.println(currentRecord.get(0));
		}

		String json = gson.toJson(al);
		
		try (PrintWriter out = new PrintWriter("forward-index.json")) {
		    out.println(json);
		}
		
		json = gson.toJson(Parser.lexicon);
		
		try (PrintWriter out = new PrintWriter("lexicon.json")) {
			out.println(json);
		}

	}
}