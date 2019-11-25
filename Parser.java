import java.util.*;



public class Parser {

	static ArrayList<String> lexicon = new ArrayList<String>();
	
	public static String parseWords(String parsingString) {
		
		HashMap<String, Integer> countMap = new HashMap<>();
		int count;
		
		// words delimited by space
		String[] arr = parsingString.split("('s )|(, )|(\\. )|(\\.)|(\\s)");
		
		for (String s : arr)
			if(countMap.containsKey(s)) {
				count = countMap.get(s);
				countMap.put(s, ++count);
			}
			else {
				countMap.put(s, 1);
			}
		
		parsingString = "";
		
		for(Map.Entry<String, Integer> entry : countMap.entrySet()) {
			parsingString += entry.getKey() + "-" + entry.getValue() + ";";
		}
		
		//System.out.println(parsingString);
		
		return parsingString;
	}

}
