import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.commons.csv.*;

public class GUIMain {

	public static Searcher searcher;
	public static String[] results;
	private static JList list;
	private static DefaultListModel listModel;
	private static boolean isResetting = false;

	public static void main(String[] args) throws IOException {

		// generates forward index and lexicon
//		Indexer indexer = new Indexer();
//		
//		indexer.genforwardIndex("data/movies_metadata.csv");
//		indexer.invertIndex("forward-index.json", "lexicon.json");

		// making JFrame window
		JFrame frame = new JFrame("Search Engine");

		// making panels to add to JFrame
		JPanel south = new JPanel();
		JPanel north = new JPanel();
		JPanel west = new JPanel();
		JPanel east = new JPanel();
		JPanel center = new JPanel();

		// Main panels added to JFrame
		frame.setLayout(new BorderLayout());
		frame.add(north, BorderLayout.NORTH);
		frame.add(south, BorderLayout.SOUTH);
		frame.add(east, BorderLayout.EAST);
		frame.add(west, BorderLayout.WEST);
		frame.add(center, BorderLayout.CENTER);

		// for spacing at top
		JLabel space = new JLabel(" ");
		space.setFont(new Font("Calbiri", Font.PLAIN, 69));
		north.add(space); // adding space to top

		// Further panels for center panel
		center.setLayout(new BorderLayout());
		JPanel ccenter = new JPanel();
		JPanel cnorth = new JPanel();
		JPanel csouth = new JPanel();

		// adding further panel to center panel
		center.add(ccenter, BorderLayout.CENTER);
		center.add(cnorth, BorderLayout.NORTH);
		center.add(csouth, BorderLayout.SOUTH);

		// for title
		JLabel title = new JLabel("  IMDb   ");
		title.setFont(new Font("Calbiri", Font.BOLD, 90));
		title.setOpaque(true);// adding background colour to title
		title.setBackground(Color.yellow);

		// adding title to center panel north
		cnorth.add(title);

		// Text field to search
		JTextField search = new JTextField("\t\t\t\t");
		search.setFont(new Font("Calbiri", Font.PLAIN, 20));

		// adding Text field to center panel of center
		ccenter.add(search);

		// Creating button
		JButton button = new JButton();
		button.setText("Search");

		JButton newrec = new JButton();
		newrec.setText("Add new Records");

		// adding search button to center panel of center
		ccenter.add(button);
		ccenter.add(newrec);

		// list for results
		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.setFont(new Font("Calbiri", Font.PLAIN, 12));

		JScrollPane scrollableList = new JScrollPane(list);

		// adding scroll to text area

		scrollableList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollableList.setPreferredSize(new Dimension(640, 200));

		ListSelectionModel listSelectionModel = list.getSelectionModel();
		listSelectionModel.addListSelectionListener(new ListSelectionHandler());

		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					searcher = new Searcher("inverted-index.json", "titles.json");

					String query = search.getText();
					results = searcher.executeQuery(query);

					isResetting = true;
					listModel.removeAllElements();
					
					
					for (String a : results) {
						String title = searcher.titles.get(a);
						listModel.addElement(title);
					}
					
					isResetting = false;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

//adding action listener to "Add new records" button		
		newrec.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFrame newframe = new JFrame();
				newframe.setLayout(new BorderLayout());
				JPanel top = new JPanel();
				JPanel cen = new JPanel();
				JPanel bot = new JPanel();

				newframe.add(top, BorderLayout.NORTH);
				newframe.add(cen, BorderLayout.CENTER);
				newframe.add(bot, BorderLayout.SOUTH);

				JLabel idlabel = new JLabel("IMDB ID: ");
				JTextField idjtf = new JTextField("Enter IMDB ID Here");
				top.add(idlabel);
				top.add(idjtf);

				JLabel overviewlabel = new JLabel("Overview: ");
				JTextField overviewjtf = new JTextField("Enter Overview Here");
				cen.add(overviewlabel);
				cen.add(overviewjtf);

				JLabel titlelable = new JLabel("Title: ");
				JTextField titlejtf = new JTextField("Enter Title Here");
				cen.add(titlelable);
				cen.add(titlejtf);

				JButton jbsave = new JButton("SAVE");
				bot.add(jbsave, BorderLayout.SOUTH);

				jbsave.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						String imdb_id = idjtf.getText();
						String title = titlejtf.getText();
						String overview = overviewjtf.getText();
						try (CSVPrinter printer = new CSVPrinter(new FileWriter("movies_metadata.csv", true),
								CSVFormat.EXCEL)) {

							printer.printRecord(imdb_id, overview, null, null, "", "", "", title, "");

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
		frame.getContentPane().add(scrollableList, BorderLayout.SOUTH);

		// Setting the size of JFrame
		frame.setSize(600, 550);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	static class ListSelectionHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			
			if (!isResetting && !e.getValueIsAdjusting()) {
				int selectedIndex = e.getFirstIndex();

				String url = "https://www.imdb.com/title/" + results[selectedIndex] + "/";
				URI uri;
				try {
					uri = new URI(url);
					java.awt.Desktop.getDesktop().browse(uri);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}
}
