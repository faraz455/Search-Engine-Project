import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.csv.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class Indexer {

	// ArrayList will contain the forward index
	static ArrayList<HashMap<String, HashMap<String, ArrayList<Integer>>>> forwardIndex = new ArrayList<>();

	// generates a forward index and a lexicon for a given csv file
	public static void genforwardIndex(String filename) throws IOException {

		// string will contain the text to be parsed
		String parsingString;
		// gson will be used to convert our ArrayList into a JSON file
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		File csvData = new File(filename);

		// this parser will go through record-by-record in the csv
		CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.EXCEL);

		// loop parses and adds each record into the index
		for (CSVRecord currentRecord : parser) {

			// if there is no imdb_id for a particular record
			if (currentRecord.get(0).equals(""))
				continue;	// go to the next record

			// format is {imdb_id:{words:[nhits, hit1, hit2, hit3.....hitn]}}
			HashMap<String, HashMap<String, ArrayList<Integer>>> map = new HashMap<>();

			// column 7 is movie title, column 1 is the movie overview
			// string is all lowercase in order to make queries easier to implement
			parsingString = (currentRecord.get(7) + " " + currentRecord.get(1)).toLowerCase();

			map.put(currentRecord.get(0), Parser.parseWords(parsingString));

			forwardIndex.add(map);

			// for testing: prints imdb_id and movie name
			System.out.println(currentRecord.get(0) + " " + currentRecord.get(7));
		}

		// index ArrayList converted to JSON
		String json = gson.toJson(forwardIndex);
		// puts a new-line after every movie record
		json = json.replaceAll("\\},", "},\n");

		// writes out forward index JSON to new file in Unicode
		try (FileOutputStream fos = new FileOutputStream("forward-index.json")) {
			OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter out = new PrintWriter(writer, true);
			out.println(json);
		}

		// pretty-printing puts new-lines after every word in the lexicon JSON
		builder.setPrettyPrinting();
		gson = builder.create();
		// lexicon ArrayList converted to JSON
		json = gson.toJson(Parser.lexicon);

		// writes out lexicon JSON to new file in Unicode
		try (FileOutputStream fos = new FileOutputStream("lexicon.json")) {
			OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter out = new PrintWriter(writer, true);
			out.println(json);
		}

	}

	// converts a given forward index into an inverted index
	public static void invertIndex(String index_filename, String lexicon_filename) throws IOException {

		loadJSONFiles(index_filename, lexicon_filename);

		// this hashmap will become our inverted index
		HashMap<String, HashMap<String, ArrayList<Integer>>> invMap = new HashMap<>();

		// put all words from the lexicon into the hashmap but without any values
		for (String word : Parser.lexicon) {
			invMap.put(word, null);
		}

		// iterate over the forward index
		for(int i = 0; i < forwardIndex.size(); i++)
			// iterate through every movie record
			for(Entry<String, HashMap<String, ArrayList<Integer>>> fwd_imdb : forwardIndex.get(i).entrySet())
				// iterate over every word
				for (Entry<String, ArrayList<Integer>> fwd_words : fwd_imdb.getValue().entrySet()) {

					// check if word in forward index has an entry in the inverted index
					if (invMap.get(fwd_words.getKey()) == null) {
						// if not, then add a new entry containing the imdb_id and corresponding hitInfo
						HashMap<String, ArrayList<Integer>> inv_imdb = new HashMap<>();
						inv_imdb.put(fwd_imdb.getKey(), fwd_words.getValue());
						invMap.replace(fwd_words.getKey(), inv_imdb);
					}
					else {
						// if entry exists, add another imdb_id with its corresponding hitInfo
						HashMap<String, ArrayList<Integer>> inv_imdb = invMap.get(fwd_words.getKey());
						inv_imdb.put(fwd_imdb.getKey(), fwd_words.getValue());
						invMap.replace(fwd_words.getKey(), inv_imdb);
					}
				}

		// converting inverted index hashmap to string
		Gson gson = new Gson();
		String invertedJSON = gson.toJson(invMap);
		// puts a new-line after every movie record
		invertedJSON = invertedJSON.replaceAll("\\},", "},\n");

		// writes out inverted index JSON to new file in Unicode
		try (FileOutputStream fos = new FileOutputStream("inverted-index.json")) {
			OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter out = new PrintWriter(writer, true);
			out.println(invertedJSON);
		}
	}

	// loads forward index and lexicon into memory
	private static void loadJSONFiles(String index_filename, String lexicon_filename) throws IOException {
		Gson gson = new Gson();

		String forward_index = Files.readString(Paths.get(index_filename));
		// removes all newlines from the string for deserialization
		forward_index = forward_index.replaceAll("$", "");

		String lexicon_string = Files.readString(Paths.get(lexicon_filename));

		// since the types of both HashMaps and ArrayLists are generic,
		// we need to specify the types of HashMaps and ArrayLists we need
		// in order to properly deserialze the data into our data structures in memory
		Type entryType = new TypeToken<ArrayList<HashMap<String, HashMap<String, ArrayList<Integer>>>>>() {}.getType();
		Type lexiconType = new TypeToken<ArrayList<String>>() {}.getType();

		// load JSON files into memory using the specified types
		forwardIndex = gson.fromJson(forward_index, entryType);
		Parser.lexicon = gson.fromJson(lexicon_string, lexiconType);

	}
}