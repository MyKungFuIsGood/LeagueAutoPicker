package LeagueAutoPicker;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.CancellationException;
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

public class Gui extends JFrame {

	private JPanel contentPane;
	private JComboBox comboHero;
	private JLabel lblHero;
	private JTextField txtLobbyMsg;
	private JButton btnStart;
	private JButton btnStop;
	private JLabel lblError;
	
	private String path; // path to riot assests
	private String selectedHero; // used to store the complete path to the currently selected hero 
	private boolean searching = false; // makes sure multiple searches are not executed
	private heroFind heroSearch = new heroFind(); // swingworker class

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
		setBounds(100, 100, 169, 338);
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
		
		comboHero = new JComboBox(GuiSupport.getHeroNames(path));
		comboHero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectedHero = path + (String)comboHero.getSelectedItem() + LeagueVars.heroPostfix;
				lblHero.setIcon(new ImageIcon( selectedHero ));  //path + heroName + postFix = success!
			}
		});
		comboHero.setBounds(18, 13, 130, 27);
		contentPane.add(comboHero);
		
		lblHero = new JLabel("");
		// load first champion avatar, will be the first one listed in the comboHero
		selectedHero =path + GuiSupport.getHeroFileNames(path).toArray()[0];
		lblHero.setIcon(new ImageIcon(selectedHero));
		lblHero.setBounds(23, 53, 120, 120);
		contentPane.add(lblHero);
		
		txtLobbyMsg = new JTextField();
		txtLobbyMsg.setText("Message (optional)");
		txtLobbyMsg.setBounds(16, 186, 134, 28);
		contentPane.add(txtLobbyMsg);
		txtLobbyMsg.setColumns(10);
		
		btnStart = new JButton("Start");
		btnStart.setHorizontalAlignment(SwingConstants.LEADING);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( !searching ) {
					searching = true;
					
					// use search to get hero, only if checked!
					// find hero
					heroSearch.execute();
					
					// move mouse, only if checked!
					// find send button
					// click on middle left of button
					// enter msg
					// send enter command
					
					// display 'Success' on btnStart for 5 seconds
					btnStart.setText("Start");
				}
			}
		});
		btnStart.setBounds(25, 227, 117, 29);
		contentPane.add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.setHorizontalAlignment(SwingConstants.LEADING);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// stop the swing worker that was started on btnStart action
				heroSearch.setStopFlag();
				
				heroSearch = new heroFind();
				searching = false;
				
				// display 'Stopped' on btnStart for 5 seconds
				btnStart.setText("Start");
			}
		});
		btnStop.setBounds(25, 269, 117, 29);
		contentPane.add(btnStop);
	} //Gui()
	
	class heroFind extends SwingWorker<int[], Integer> {
		private boolean stopFlag = false;
		
		void setStopFlag() { stopFlag = true; }
		
		@Override
		protected int[] doInBackground() throws Exception {
			int[] location = new int[2];
			int i = 0;
			
			while(location[0] == 0 && i < 600) {
				location = Sikuli.findHero(selectedHero);
				publish(i);
				Thread.sleep(100);
				i++;
				
				if( stopFlag ) { break; }
			}
			
			return location;
		} // doInBackground()
		
		@Override
		protected void process(List<Integer> chunks) {
			int mostRecent = chunks.get(chunks.size() - 1);
			
			String btnText = "";
			
			if( mostRecent % 4 == 0 ) { btnText = "Seaching"; }
			else if( mostRecent % 4 == 1 ) { btnText = "Seaching."; }
			else if( mostRecent % 4 == 2 ) { btnText = "Seaching.."; }
			else { btnText = "Seaching..."; }
			
			btnStart.setText(btnText);
		} // process()
		
		@Override
		protected void done() {
			int[] location = new int[2];
			
			try {
				location = get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int x = location[0];
			int y = location[1];
			
			if( x == 0 ) {
				btnStart.setText("Start");
				return;
			}
			
			Robot bot = null;
			try {
				bot = new Robot();
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
			int mask = InputEvent.BUTTON1_MASK;
			
			bot.mouseMove(x, y);
			bot.mousePress(mask);
			bot.mouseRelease(mask);	
		} // done()
	} // class heroFind
}
