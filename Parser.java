import java.util.*;


public class Parser {

	static ArrayList<String> lexicon = new ArrayList<String>();

	public static HashMap<String, Integer> parseWords(String parsingString) {

		HashMap<String, Integer> countMap = new HashMap<>();
		int count;

		// words delimited by space
		String[] arr = parsingString.split("('s )|(, )|(\\.[ ])|(\\W+)");

		for (String s : arr) {
			if(countMap.containsKey(s)) {
				count = countMap.get(s);
				countMap.put(s, ++count);
			}
			else {
				countMap.put(s, 1);
			}
			
			if (!lexicon.contains(s))
				lexicon.add(s);
		}
		
		return countMap;
	}

}
