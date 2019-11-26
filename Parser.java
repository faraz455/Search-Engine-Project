import java.util.*;


public class Parser {

	// contains every single word used in every movie overview or title
	static ArrayList<String> lexicon = new ArrayList<String>();

	// parses string and returns a hashmap containing all words with their corresponding hits
	public static HashMap<String, ArrayList<Integer>> parseWords(String parsingString) {
		
		// if there are m words for each movie and each word has n number of hits:
		// format is {word1:[nhits, hit1, hit2....hitn], word2:[...], word3:[...],...wordm:[...]}
		HashMap<String, ArrayList<Integer>> hitList = new HashMap<>();
		// element at index 0 is number of hits n
		// elements at indices 1-n are positions of a word
		ArrayList<Integer> hitInfo;

		// splits string by delimiting with
		// "'s " at the end of words, commas, fullstops and non-alphabetical or non-numerical characters
		String[] arr = parsingString.split("('s )|(, )|(\\.[ ])|(\\W+)");
		
		// int i will be the position of a word
		for (int i = 0; i < arr.length; i++) {
			// if the hitlist already contains word that we found
			if(hitList.containsKey(arr[i])) {
				hitInfo = hitList.get(arr[i]);		// get the corresponding ArrayList from the hitList
				hitInfo.set(0, hitInfo.get(0) + 1);	// increment frequency of word
				hitInfo.add(i);						// add position of word to hitInfo
			}
			// if the word is not found
			else {
				hitInfo = new ArrayList<Integer>();	// create new hitInfo ArrayList
				hitInfo.add(1);						// add frequency = 1
				hitInfo.add(i);						// add position of word to hitInfo
				hitList.put(arr[i], hitInfo);		// add new entry to hitList for the new word
			}
			
			if (!lexicon.contains(arr[i]))			// in case we find a new unique word
				lexicon.add(arr[i]);				// add it to our lexicon
		}
		
		return hitList;
	}

}
