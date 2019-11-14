import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Main {

    public static void main(String[] args) {

        HashMap<String,String> h=new HashMap<String,String>();
        String csvFile = "C:\\Users\\AMMAR\\Documents\\NetBeansProjects\\JavaApplication2\\src\\test.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] c = line.split(cvsSplitBy);
                h.put("1",c[0]);
                h.put("2",c[1]);
                h.put("3", c[2]);
                System.out.println("1. " + h.get("1")+ " , 2. " + h.get("2") + " 3."+ h.get("3"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}