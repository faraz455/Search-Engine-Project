import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class GUIMain {  
	public static void main(String []args) throws IOException {  

		// generates forward index and lexicon
//		Indexer indexer = new Indexer();
//		
//		indexer.genforwardIndex("data/movies_metadata.csv");
//		indexer.invertIndex("forward-index.json", "lexicon.json");
		
		//making JFrame window
		JFrame frame = new JFrame("Search Engine"); 
		
		//making panels to add to JFrame
		JPanel south = new JPanel();  
		JPanel north = new JPanel();  
		JPanel west = new JPanel();  
		JPanel east = new JPanel();    
		JPanel center = new JPanel();   

		// Main panels added to JFrame
		frame.setLayout(new BorderLayout()); 
		frame.add(north,BorderLayout.NORTH); 
		frame.add(south,BorderLayout.SOUTH); 
		frame.add(east,BorderLayout.EAST); 
		frame.add(west,BorderLayout.WEST); 
		frame.add(center,BorderLayout.CENTER);
		
		//for spacing at top
		JLabel space = new JLabel(" ");  
		space.setFont(new Font("Calbiri", Font.PLAIN, 69));
		north.add(space); // adding space to top

		//Further panels for center panel
		center.setLayout(new BorderLayout());
		JPanel ccenter = new JPanel();
		JPanel cnorth = new JPanel();
		JPanel csouth = new JPanel();

		//adding further panel to center panel
		center.add(ccenter,BorderLayout.CENTER);
		center.add(cnorth,BorderLayout.NORTH);
		center.add(csouth,BorderLayout.SOUTH);

		// for title
		JLabel title = new JLabel("  IMDb   ");
		title.setFont(new Font("Calbiri", Font.BOLD, 90));
		title.setOpaque(true);//adding background colour to title
		title.setBackground(Color.yellow);
		
		//adding title to center panel north 
		cnorth.add(title);

		// Text field to search 
		JTextField search = new JTextField("\t\t\t\t");
		search.setFont(new Font("Calbiri", Font.PLAIN, 20));

		//adding Text field to center panel of center 
		ccenter.add(search);

		// Creating button
		JButton button = new JButton();  
		button.setText("Search"); 
		
		JButton newrec = new JButton();  
		newrec.setText("Add new Records"); 
		
		  
		// adding search button to center panel of center
		ccenter.add(button);
		ccenter.add(newrec);

		//text area for result
        JTextArea textArea = new JTextArea(10, 10);  
        textArea.setFont(new Font("Calbiri", Font.PLAIN, 12));
        JScrollPane scrollableTextArea = new JScrollPane(textArea); 
  
        //adding scroll to text area
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 

//adding action listener to search button		
button.addActionListener(new ActionListener(){  
			
	public void actionPerformed(ActionEvent e) {
		try {
				Searcher searcher = new Searcher("inverted-index.json", "titles.json");
			
				String query= search.getText();
				System.out.print("Query: " + query + "\n");
				String[] results = searcher.executeQuery(query);
				
				for (String a : results) {
					String title = searcher.titles.get(a);
					textArea.append(title);
					textArea.append("\n");
					System.out.println(title);
				}

				for (String a : results) {
					String title = searcher.titles.get(a);

					System.out.println(title);
				}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}  
			    });
		
//adding action listener to "Add new records" button		
newrec.addActionListener(new ActionListener(){  
	
public void actionPerformed(ActionEvent e) {
	 JFrame newframe = new JFrame();
	 newframe.setLayout(new BorderLayout());
	 JPanel top = new JPanel();
	 JPanel cen = new JPanel();
	 JPanel bot = new JPanel();
	 
	 newframe.add(top,BorderLayout.NORTH);
	 newframe.add(cen,BorderLayout.CENTER);
	 newframe.add(bot,BorderLayout.SOUTH);
	 
	 JLabel idlabel=new JLabel("IMDB ID: ");
	 JTextField idjtf = new JTextField("Enter IMDB ID Here");
	 top.add(idlabel); top.add(idjtf);
	
	 JLabel overviewlabel=new JLabel("Overview: ");
	 JTextField overviewjtf = new JTextField("Enter Overview Here");
	 cen.add(overviewlabel); cen.add(overviewjtf);
	 
	 JLabel titlelable= new JLabel("Title: "); 
	 JTextField titlejtf = new JTextField("Enter Title Here");
	 cen.add(titlelable); cen.add(titlejtf);
	 
	 JButton jbsave= new JButton("SAVE");
	 bot.add(jbsave,BorderLayout.SOUTH);
	 
	 jbsave.addActionListener(new ActionListener(){  
			
		 public void actionPerformed(ActionEvent e) {
			 String imdb_id = idjtf.getText();
			 String title = titlejtf.getText();
			 String overview = overviewjtf.getText();
			 try (CSVPrinter printer = new CSVPrinter(new FileWriter("movies_metadata.csv",true), CSVFormat.EXCEL)) {
				
				 printer.printRecord(imdb_id, overview, null, null, "","","",title,"");
			    
			     
			  		 } catch (IOException ex) {
			     ex.printStackTrace();
			 }
			 newframe.setVisible(false);
			 
		}
		 });
	 newframe.setSize(240, 200);  
	 newframe.setResizable(true);
	 newframe.setLocationRelativeTo(null);  
	 newframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  
	 newframe.setVisible(true);  
}  
});


        // adding text area to south of jframe
		frame.getContentPane().add(scrollableTextArea,BorderLayout.SOUTH); 
		
		//Setting the size of JFrame
		frame.setSize(600, 550);  
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setVisible(true);  
	}  
}
