package LeagueAutoPicker;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File; /////
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Gui extends JFrame {

	private JPanel contentPane;
	private JComboBox comboHero;
	private JComboBox comboSpell;
	private JLabel lblHero;
	private JTextField txtLobbyMsg;
	private JButton btnStart;
	private JButton btnReset;
	private JLabel lblBtnResult;
	private JLabel lblError;
	
	// dir for asset paths
	private String spellPath; // path to summoner spell assets
	private String path; // path to riot assets 
	private String selectedSpell; // used to store complete path for spell currently selected
	private String selectedHero; // used to store the complete path to the currently selected hero 
	private boolean searching = false; // makes sure multiple searches are not executed
	private Search search = new Search(); // SwingWorker class that find summoner spell
	
	private int offset = 245; // y distance from summonerSpell to lobby text box
	private int xSearchOffset = 220; // x distance from summoner spell to search text box
	private int ySearchOffset = -355; // y distance from summoner spell to search text box
	private int xHeroOffset = -345; // x distance from summoner spell to hero
	private int yHeroOffset = -285; // y distance from summoner spell to hero

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 169, 408);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// below checks for non-default file paths
		path = LeagueVars.getImageAssets();
		// export the below code to GuiSupport eventually
		String hero = "Ashe" + LeagueVars.heroPostfix; // used ashe because she's an old hero and starts with A
		if( new File(path + hero).exists() ) {
			// Success heroes are present go on your way
		}
		else {
			// oh no, no hero's found start asking for a new file path
			while( !(new File(path + hero).exists()) && path != null ) {
				path = (String)JOptionPane.showInputDialog("Could not find league assets in default path.\r\n"
						+ "Please input your league asset path,\r\n\r\n"
						+ "C:\\\\Games\\\\Riot...assets\\\\images\\\\champions\\\\\r\n\r\n"
						+ "*** windows requires double slashes '\\\\' ***\r\n"
						+ "*** mac only needs a single slash '/' ***");
			}
			// if the user hits cancel path will be null, set an error label
			if( path == null ) {
				lblError = new JLabel("No valid path set!");
				lblError.setBounds(18, 13, 130, 27);
				contentPane.add(lblError);
				return; 
			}
		}
		
		// get summoner spell path
		spellPath = System.getProperty("user.dir") + File.separator + "img" + File.separator;
		
		comboSpell = new JComboBox(GuiSupport.getSummonerSpells(spellPath));
		comboSpell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedSpell = spellPath + (String)comboSpell.getSelectedItem() + "_spell.png";
			}
		});
		comboSpell.setBounds(19, 244, 130, 27);
		contentPane.add(comboSpell);
		
		comboHero = new JComboBox(GuiSupport.getHeroNames(path));
		comboHero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedHero = path + (String)comboHero.getSelectedItem() + LeagueVars.heroPostfix;
				lblHero.setIcon(new ImageIcon( selectedHero ));  //path + heroName + postFix = success!
			}
		});
		comboHero.setBounds(19, 10, 130, 27);
		contentPane.add(comboHero);
		
		lblHero = new JLabel("");
		// load first champion avatar, will be the first one listed in the comboHero
		selectedHero =path + GuiSupport.getHeroFileNames(path).toArray()[0];
		lblHero.setIcon(new ImageIcon(selectedHero));
		lblHero.setBounds(24, 47, 120, 120);
		contentPane.add(lblHero);
		
		txtLobbyMsg = new JTextField();
		txtLobbyMsg.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtLobbyMsg.selectAll();
			}
		});
		txtLobbyMsg.setText("Message (optional)");
		txtLobbyMsg.setBounds(17, 177, 134, 28);
		contentPane.add(txtLobbyMsg);
		txtLobbyMsg.setColumns(10);
		
		btnStart = new JButton("Start");
		btnStart.setHorizontalAlignment(SwingConstants.LEADING);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if a search is already running, don't run again
				if( !searching ) {
					searching = true;
					
					// if txtlobbyMsg is edited set flag to true
					String msg = txtLobbyMsg.getText();
					boolean set = !(msg.equals("Message (optional)") || msg.equals(""));
					search.setLobbyMsg(set);
					
					search.execute();
				}
				
				searching = false;
			}
		});
		btnStart.setBounds(26, 290, 117, 29);
		contentPane.add(btnStart);
		
		btnReset = new JButton("Reset");
		btnReset.setHorizontalAlignment(SwingConstants.LEADING);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// stop the swing worker that was started on btnStart action
				search.setStopFlag();
				
				// reset the objects
				search = new Search();
				
				// set search flag to false
				searching = false;
				
				// display 'Stopped' on btnStart for 5 seconds
				btnStart.setText("Start");
			}
		});
		btnReset.setBounds(26, 320, 117, 29);
		contentPane.add(btnReset);
		
		JLabel lblChooseASummoner = new JLabel("Summoner spell");
		lblChooseASummoner.setHorizontalAlignment(SwingConstants.CENTER);
		lblChooseASummoner.setBounds(6, 217, 157, 25);
		contentPane.add(lblChooseASummoner);
		
		lblBtnResult = new JLabel("");
		lblBtnResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblBtnResult.setBounds(6, 361, 157, 16);
		contentPane.add(lblBtnResult);
		
		
	} //Gui()
	
	class Search extends SwingWorker<Void, int[]> {
		private boolean stopFlag = false;
		private boolean lobbyMsg = false;
		
		void setStopFlag() { stopFlag = true; }
		void setLobbyMsg(boolean set) { lobbyMsg = set; }
		
		@Override
		protected Void doInBackground() throws Exception {
			int[] location = new int[2];
			int i = 0;
			
			while(location[0] == 0 && i < 600) {
				location = Sikuli.findImg(selectedSpell);
				if(location[0] == 0) { location[1] = i; }
				else { break; }
				publish(location);
				
				Thread.sleep(100);
				i++;
				
				if( stopFlag ) { return null; }
			}
			
			// return if search timed out
			if( i >= 600 ) {
				lblBtnResult.setText("Search Timeout!");
				return null;
			}
			
			int x = location[0];
			int y = location[1];
			
			Robot bot = null;
			try {
				bot = new Robot();
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
			int mask = InputEvent.BUTTON1_MASK;
			
			if(lobbyMsg) {
				// move to lobby text box
				bot.mouseMove(x, y + offset);
				bot.mousePress(mask);
				bot.mouseRelease(mask);
	
				// enter lobby msg
				String msg = txtLobbyMsg.getText();
				GuiSupport.writeMsg(bot, msg, true);
			}
			
			// move to search box
			bot.mouseMove(x + xSearchOffset, y + ySearchOffset);
			bot.waitForIdle();
			bot.mousePress(mask);
			bot.mouseRelease(mask);
			
			// enter hero name
			Thread.sleep(100); // need to wait a bit
			String hero = (String) comboHero.getSelectedItem();
			GuiSupport.writeMsg(bot, hero, false);
			
			Thread.sleep(400);
			
			// move to hero and click
			bot.mouseMove(x + xHeroOffset, y + yHeroOffset);
			bot.waitForIdle();
			bot.mousePress(mask);
			bot.mouseRelease(mask);
			
			// write to gui that you found and clicked on stuff
			lblBtnResult.setText("Search Success!");
			
			return null;
		} // doInBackground()
		
		@Override
		protected void process(List<int[]> chunks) {
			int[] location = chunks.get(chunks.size() - 1);
			int x = location[0];
			int y = location[1] + offset;
			
			if( stopFlag ) {
				lblBtnResult.setText("Search Stopped!");
			}
			
			int mostRecent = y;
			
			String btnText = "";
			
			if( mostRecent % 4 == 0 ) { btnText = "Seaching"; }
			else if( mostRecent % 4 == 1 ) { btnText = "Seaching."; }
			else if( mostRecent % 4 == 2 ) { btnText = "Seaching.."; }
			else { btnText = "Seaching..."; }
			
			btnStart.setText(btnText);
		}
		
		@Override
		protected void done() {
			btnStart.setText("Start");
			
			search = new Search();
		}
	} // Search() SwingWorker
} 
