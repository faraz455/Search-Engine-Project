package com.searchEngine;

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
	private static JList<String> list;
	private static DefaultListModel<String> listModel;
	private static boolean isResetting = false;

	public static void main(String[] args) throws IOException, URISyntaxException {

		// generates forward index and lexicon
		Indexer indexer = new Indexer();

/*		Uncomment the following two lines to regenerate forward and backward indexes	*/
//		indexer.genforwardIndex("data/movies_metadata.csv");
//		indexer.invertIndex("forward-index.json", "lexicon.json");

		searcher = new Searcher(getURI("inverted-index.json"), getURI("titles.json"));

		Window wnd = new Window(indexer, searcher);
		wnd.setVisible(true);
	}
	
	// Utility function for fetching resources
	public static URI getURI(String filename) throws URISyntaxException {
		return GUIMain.class.getClassLoader().getResource( filename ).toURI();
	}
	
	// Window class dealing with all GUI components
	@SuppressWarnings("serial")
	public static class Window extends JFrame {
		
		public Window (Indexer indexer, Searcher searcher) {
			// making JFrame window
			super("Search Engine");
			Window wndRef = this;
			
			// making panels to add to JFrame
			JPanel south = new JPanel();
			JPanel north = new JPanel();
			JPanel west = new JPanel();
			JPanel east = new JPanel();
			JPanel center = new JPanel();

			// Main panels added to JFrame
			setLayout(new BorderLayout());
			add(north, BorderLayout.NORTH);
			add(south, BorderLayout.SOUTH);
			add(east, BorderLayout.EAST);
			add(west, BorderLayout.WEST);
			add(center, BorderLayout.CENTER);

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
			listModel = new DefaultListModel<String>();
			list = new JList<String>(listModel);
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
			
			//adding action listener to "Search" button
			button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					String query = search.getText();
					try {
					results = searcher.executeQuery(query);
					}
					catch (NullPointerException exc) {
						JOptionPane.showMessageDialog(wndRef, "Query could not be executed. Please revise your query.","Error!", JOptionPane.WARNING_MESSAGE);
					}
					isResetting = true;
					listModel.removeAllElements();

					for (String a : results) {
						String title = searcher.titles.get(a);
						listModel.addElement(title);
					}

					isResetting = false;

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

							int confirmation = JOptionPane.showConfirmDialog(null, "Do you wish to recreate the whole index?\nProgram must restart after this.");
							if (confirmation != 0) return;
							
							try (CSVPrinter printer = new CSVPrinter(new FileWriter("data/movies_metadata.csv", true),
									CSVFormat.EXCEL)) {

								printer.printRecord(imdb_id, overview, null, null, "", "", "", title, "");
								
								// 0 = yes, 1 = no, 2 = cancel
								if (confirmation == 0) {
									indexer.genforwardIndex(getURI("data/movies_metadata.csv"));
									indexer.invertIndex(getURI("forward-index.json"), getURI("lexicon.json"));
									System.exit(0);
								}
							} catch (IOException ex) {
								System.err.println("Error! Could not add record to CSV file!");
							} catch (URISyntaxException e1) {
								System.err.println("Error! Invalid filename!");
								e1.printStackTrace();
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
			getContentPane().add(scrollableList, BorderLayout.SOUTH);

			// Setting the size of JFrame
			setSize(600, 550);
			setResizable(true);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	
	}
	// Class implenting listener for list
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
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}
}
