/**The main program for the Pipe Panic game.
 * @author Tom Huang and Victor Cong
 * @version June 16, 2012
 */
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PipeMain extends JFrame {


private PipePanel pipeArea;
		
		public PipeMain() {
			super("Pipe Panic");
			setResizable(false);
			
			// Position in the middle of the window
			setLocation(100, 100);

			// Add in an Icon
			setIconImage(new ImageIcon("Pipe.png").getImage());
			
			// Right Panel
			JPanel rightPanel = new JPanel();
			rightPanel.setBackground(new Color(32,178,170));
			rightPanel.setLayout(new BorderLayout());
			
			// Add the PipePanel to the centre of the Frame
			setLayout(new BorderLayout());
			pipeArea = new PipePanel(this);

			add(pipeArea, BorderLayout.CENTER);

		}		     

		public static void main(String[] args) {
			PipeMain frame = new PipeMain();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			}


	}

