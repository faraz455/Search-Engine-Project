import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.csv.*;

public class Indexer {
	public static void main(String[] args) throws IOException {
		// map key is imdb_id and map value is overview
		HashMap<String, String> map = new HashMap<>();
		
		File csvData = new File("data/movies_metadata.csv");
		
		// this parser will go through record-by-record in the csv
		CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.EXCEL);
		
		
		// this should add all records to the hashmap
		for (CSVRecord currentRecord : parser) {
			map.put(currentRecord.get(0), currentRecord.get(1));
			System.out.println(currentRecord.get(0) + " " + currentRecord.get(1));
		}
		
		System.out.println("\nSize: " + map.size());

	}
}