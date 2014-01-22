/**Pipe Panic, a variation of the Lights out puzzle game, with thematic and playstyle tweaks.
 * @author Tom Huang and Victor Cong
 * @version June 16, 2012
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PipePanel extends JPanel
{

	// Font for small text.
	static final Font font1 = new Font("Helvetica", Font.PLAIN, 14);

	// Larger font for titles.
	static final Font font2 = new Font("Helvetica", Font.BOLD, 40);

	private boolean won; // Check if the puzzle has been solved
	private int gridSize; // Size of the grid, read from the text file
	// The current level, deciding which text file will be read to generate a
	// game
	private int currentLevel;
	private int moveCount;
	// The main grid of the program. Each pipe has a static location, including
	// those that move. This determines how they interact, and where the moving
	// pipes will
	// go once they stop moving (when they are released by the mouse)
	private PipeGrid pipeGrid;
	// Rectangles that decide the placement of drawings of the pipes.
	private Rectangle[][] rectangleGrid;
	private Rectangle[] buttons;
	private Rectangle restartButton, showSolutionButton, newGameButton,
			menuButton;
	private Rectangle selectedButton;

	// The movable rectangles representing the moving pipes. This is in addition
	// to their static
	// representation. This allows for the pipes to move smoothly along with the
	// mouse.
	private ArrayList<Rectangle> movingRects;
	private Rectangle[] levels; // For the level select menu
	private ArrayList<Integer> movingValues; // Values of the moving pipes, e.g.
												// on/off, diagonal or normal
	private ArrayList<Integer> solveMoves; // Solution to the puzzle
	private ArrayList<Integer> originalMoves; // A copy of the solution so the
												// first can be modified
	private int minimumMoves; // Minimum moves needed to solve
	private int draggedPlace;
	private int selectedRect;
	private Point selectedRectPoint;
	private Rectangle clickedRect;
	private int clickedPipe;
	private boolean gameStart;
	private Image[] images;
	// Normal images
	private Image pipeOff, pipeOn1, pipeOn2, pipeOn3, diagOn1, diagOn2,
			diagOn3, diagOff, cDiagOff, cDiagOn1, cDiagOn2, cDiagOn3, cPipeOff,
			cPipeOn1, cPipeOn2, cPipeOn3, conveyorBack, wildCardPipeOff,
			wildCardPipeOn1, wildCardPipeOn2, wildCardPipeOn3, restartImage,
			menuImage, newGameImage, showSolutionImage, shuffleButton,
			solveImage, preview1, preview2, preview3, preview4, preview5,
			preview6, preview7, preview8, preview9, preview10, preview11,
			preview12, preview13, preview14, preview15;

	// Backgrounds
	private Image main, instructions1, instructions2, instructions3,
			selectedMenu, levelSelect, background;
	private Image wrench; // Mouse image
	private Point mousePoint; // Tracks where the mouse is at all time to draw a
								// wrench on it for style.
	private Timer timer;
	private int time;
	private boolean showSolution;
	private String selectedMenuName; // Tracks which menu is selected
	private Point firstPoint;
	private JFrame parentFrame;
	private int[][] oldGrid;

	// Constants regarding proportions of the grid
	private static final int GRID_X = 0;
	private static final int GRID_Y = 0;
	private static final int RECT_SIZE = 110;
	// Number of levels in the game.
	private static final int MAX_LEVEL = 15;

	public PipePanel(JFrame parentFrame)
	{
		// Timer to animate pipe
		timer = new Timer(60, new TimerEventHandler());

		setPreferredSize(new Dimension(550, 650));
		setBackground(new Color(0, 0, 0));
		setFocusable(true);
		requestFocusInWindow();

		this.parentFrame = parentFrame;

		// Set all the images!
		pipeOff = new ImageIcon("NormalPipeOff.png").getImage();
		pipeOn1 = new ImageIcon("NormalPipeOn1.png").getImage();
		pipeOn2 = new ImageIcon("NormalPipeOn2.png").getImage();
		pipeOn3 = new ImageIcon("NormalPipeOn3.png").getImage();
		diagOff = new ImageIcon("DiagonalPipeOff.png").getImage();
		diagOn1 = new ImageIcon("DiagonalPipeOn1.png").getImage();
		diagOn2 = new ImageIcon("DiagonalPipeOn2.png").getImage();
		diagOn3 = new ImageIcon("DiagonalPipeOn3.png").getImage();
		cPipeOff = new ImageIcon("ConveyorNormalOff.png").getImage();
		cPipeOn1 = new ImageIcon("ConveyorNormalOn1.png").getImage();
		cPipeOn2 = new ImageIcon("ConveyorNormalPipeOn2.png").getImage();
		cPipeOn3 = new ImageIcon("ConveyorNormalPipeOn3.png").getImage();
		cDiagOff = new ImageIcon("ConveyorDiagonalOff.png").getImage();
		cDiagOn1 = new ImageIcon("ConveyorDiagonalOn1.png").getImage();
		cDiagOn2 = new ImageIcon("ConveyorDiagonalPipeOn2.png").getImage();
		cDiagOn3 = new ImageIcon("ConveyorDiagonalPipeOn3.png").getImage();
		wildCardPipeOff = new ImageIcon("WildcardPipeOff.png").getImage();
		wildCardPipeOn1 = new ImageIcon("WildcardPipeOn1.png").getImage();
		wildCardPipeOn2 = new ImageIcon("WildcardPipeOn2.png").getImage();
		wildCardPipeOn3 = new ImageIcon("WildcardPipeOn3.png").getImage();
		conveyorBack = new ImageIcon("ConveyorBackground.png").getImage();
		background = new ImageIcon("Background.png").getImage();
		levelSelect = new ImageIcon("Background.png").getImage();
		menuImage = new ImageIcon("MenuButton.png").getImage();
		restartImage = new ImageIcon("RestartButton.png").getImage();
		newGameImage = new ImageIcon("NextLevel.png").getImage();
		showSolutionImage = new ImageIcon("PreviousLevel.png").getImage();
		shuffleButton = new ImageIcon("Shuffle.png").getImage();
		solveImage = new ImageIcon("Solve.png").getImage();
		main = new ImageIcon("MainMenu.png").getImage();
		instructions1 = new ImageIcon("Instructions1.png").getImage();
		instructions2 = new ImageIcon("Instructions2.png").getImage();
		instructions3 = new ImageIcon("Instructions3.png").getImage();
		wrench = new ImageIcon("Wrench.png").getImage();
		preview1 = new ImageIcon("Previews-01.png").getImage();
		preview2 = new ImageIcon("Previews-02.png").getImage();
		preview3 = new ImageIcon("Previews-03.png").getImage();
		preview4 = new ImageIcon("Previews-04.png").getImage();
		preview5 = new ImageIcon("Previews-05.png").getImage();
		preview6 = new ImageIcon("Previews-06.png").getImage();
		preview7 = new ImageIcon("Previews-07.png").getImage();
		preview8 = new ImageIcon("Previews-08.png").getImage();
		preview9 = new ImageIcon("Previews-09.png").getImage();
		preview10 = new ImageIcon("Previews-10.png").getImage();
		preview11 = new ImageIcon("Previews-11.png").getImage();
		preview12 = new ImageIcon("Previews-12.png").getImage();
		preview13 = new ImageIcon("Previews-13.png").getImage();
		preview14 = new ImageIcon("Previews-14.png").getImage();
		preview15 = new ImageIcon("Previews-15.png").getImage();

		// Initialize all the variables
		movingRects = new ArrayList<Rectangle>();
		movingValues = new ArrayList<Integer>();
		restartButton = new Rectangle(0, 550, 110, 100);
		showSolutionButton = new Rectangle(110, 550, 110, 100);
		newGameButton = new Rectangle(330, 550, 110, 100);
		menuButton = new Rectangle(440, 550, 110, 100);
		buttons = new Rectangle[]
		{ restartButton, showSolutionButton, newGameButton, menuButton };
		gameStart = false;
		selectedMenu = main;
		selectedMenuName = "main";
		firstPoint = new Point();
		mousePoint = new Point();

		levels = new Rectangle[MAX_LEVEL];
		images = new Image[15];
		images[0] = preview1;
		images[1] = preview2;
		images[2] = preview3;
		images[3] = preview4;
		images[4] = preview5;
		images[5] = preview6;
		images[6] = preview7;
		images[7] = preview8;
		images[8] = preview9;
		images[9] = preview10;
		images[10] = preview11;
		images[11] = preview12;
		images[12] = preview13;
		images[13] = preview14;
		images[14] = preview15;

		// Rectangles for the level select screen.
		for (int level = 0; level < MAX_LEVEL; level++)
		{
			levels[level] = new Rectangle(35 + 100 * (level % 5),
					100 + 120 * (level / 5), 80, 80);
		}

		this.addMouseListener(new MouseHandler());
		this.addMouseMotionListener(new MouseMotionHandler());

	}

	/**
	 * Restarts the level with the same original on/off configuration and the
	 * same solution
	 * 
	 */
	public void restart()
	{
		// Reset important variables
		showSolution = false;
		movingRects.clear();
		solveMoves.clear();

		// Copy over the solve moves that may have been modified to start fresh
		for (int i : originalMoves)
			solveMoves.add(i);
		for (int row = 0; row < gridSize; row++)
		{
			for (int col = 0; col < gridSize; col++)
			{
				pipeGrid.setPipe(row, col, oldGrid[row][col]);
			}
		}

		moveCount = 0;
		refresh();

		repaint();

	}

	/**
	 * Creates/recreates all the rectangles
	 * 
	 */
	public void refresh()
	{

		selectedRect = 0;

		// Make the rectangles representing the pipes.
		rectangleGrid = new Rectangle[gridSize][gridSize];
		for (int row = 0; row < gridSize; row++)
			for (int col = 0; col < gridSize; col++)
			{
				rectangleGrid[row][col] = new Rectangle(GRID_X + row
						* RECT_SIZE, GRID_Y + col * RECT_SIZE, RECT_SIZE,
						RECT_SIZE);

			}
		// add in a moving rectangle above the conveyer, 3 is a normal
		// moving pipe, 4 is a diagonal (see PipeGrid)
		for (int row = 0; row < gridSize; row++)
			for (int col = 0; col < gridSize; col++)
			{

				if (Math.abs(pipeGrid.getPipe(row, col)) == 3
						|| Math.abs(pipeGrid.getPipe(row, col)) == 4)
				{
					// Adds to the list of moving pipes
					movingRects.add(new Rectangle(GRID_X + row * RECT_SIZE,
							GRID_Y + col * RECT_SIZE, RECT_SIZE, RECT_SIZE));
					// Store their values
					movingValues.add(pipeGrid.getPipe(row, col));
				}
			}

	}

	/**
	 * Starts a new game with a different on off configuration and solution
	 * 
	 */
	public void newGame()
	{
		
		oldGrid = new int[gridSize][gridSize];
		originalMoves = new ArrayList<Integer>();
		showSolution = false;
		gameStart = true;
		won = false;
		moveCount = 0;
		selectedRect = 0;
		timer.start();
		time = 0;
		repaint();
		selectedRectPoint = new Point();
		clickedRect = new Rectangle();
		if (currentLevel == 1)
		{
			pipeGrid = new PipeGrid();

		} else
		{
			pipeGrid = new PipeGrid("grid" + currentLevel);

		}
		
		//Stores the old grid for restarting.
		for (int row = 0; row < gridSize; row++)
		{
			for (int col = 0; col < gridSize; col++)
			{

				oldGrid[row][col] = pipeGrid.getPipe(row, col);

			}

		}

		movingRects.clear();
		movingValues.clear();

		solveMoves = pipeGrid.showSolution();
		for (int i : solveMoves)
			originalMoves.add(i);
		minimumMoves = solveMoves.size();
		gridSize = pipeGrid.getSize();

		refresh();

	}

	/**
	 * Repaint the drawing panel
	 * 
	 * @param g
	 *            The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Draws a different background depending on which menu the game state
		// is currently at. This is important for the main menu and
		// instructions, which are just pictures.
		// Also draws the background for the actual game.
		g.drawImage(selectedMenu, 0, 0, this);

		// If the game has started and is not over, draw all the pipes and
		// components of the games
		if (gameStart && !won)
		{
			// Draw the non-moving pipes, going through each part of the
			// pipeGrid
			for (int row = 0; row < gridSize; row++)
				for (int col = 0; col < gridSize; col++)
				{

					// Normal pipe that's off
					if (pipeGrid.getPipe(row, col) == -1)
						g.drawImage(pipeOff, rectangleGrid[row][col].x,
								rectangleGrid[row][col].y, this);

					// Normal pipe that's on, shuffling through different images
					// to animate
					else if (pipeGrid.getPipe(row, col) == 1)
					{
						if (time % 10 <= 1 || time % 10 >= 9)
							g.drawImage(pipeOn1, rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);
						else if (time % 10 == 2 || time % 10 == 3
								|| time % 10 == 7 || time % 10 == 8)
							g.drawImage(pipeOn2, rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);
						else
							g.drawImage(pipeOn3, rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);

					}

					// Diagonal pipe that's off
					else if (pipeGrid.getPipe(row, col) == -2)
						g.drawImage(diagOff, rectangleGrid[row][col].x,
								rectangleGrid[row][col].y, this);

					// Diagonal pipe that's on, shuffling through different
					// images to animate
					else if (pipeGrid.getPipe(row, col) == 2)
					{
						if (time % 10 <= 1 || time % 10 >= 9)
							g.drawImage(diagOn1, rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);
						else if (time % 10 == 2 || time % 10 == 3
								|| time % 10 == 7 || time % 10 == 8)
							g.drawImage(diagOn2, rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);
						else
							g.drawImage(diagOn3, rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);

					}

					// Wild card pipe that's off
					else if (pipeGrid.getPipe(row, col) == -10)
						g.drawImage(wildCardPipeOff, rectangleGrid[row][col].x,
								rectangleGrid[row][col].y, this);

					// Wild card pipe that's on, shuffling through different
					// images to animate
					else if (pipeGrid.getPipe(row, col) == 10)
					{
						if (time % 10 <= 1 || time % 10 >= 9)
							g.drawImage(wildCardPipeOn1,
									rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);
						else if (time % 10 == 2 || time % 10 == 3
								|| time % 10 == 7 || time % 10 == 8)
							g.drawImage(wildCardPipeOn2,
									rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);
						else
							g.drawImage(wildCardPipeOn3,
									rectangleGrid[row][col].x,
									rectangleGrid[row][col].y, this);

					}

					// Conveyer belt base
					if (pipeGrid.getPipe(row, col) == 0
							|| Math.abs(pipeGrid.getPipe(row, col)) == 3
							|| Math.abs(pipeGrid.getPipe(row, col)) == 4)
					{
						g.drawImage(conveyorBack, rectangleGrid[row][col].x,
								rectangleGrid[row][col].y, this);
					}

					// Draws a circle around pipes that should be clicked to be
					// solved, if
					// the solution is requested
					if (showSolution)
						if (solveMoves.contains(10 * row + col))
						{
							g.setColor(Color.RED);
							g.drawOval(rectangleGrid[row][col].x,
									rectangleGrid[row][col].y,
									rectangleGrid[row][col].width,
									rectangleGrid[row][col].height);
						}

				}

			// Draws each of the moving rectangles, based on their current
			// location rather than
			// their location on the grid (i.e. in the hand of the mouse)
			for (int i = 0; i < movingRects.size(); i++)
			{
				Rectangle rect = movingRects.get(i);

				// Normal moving pipe off
				if (movingValues.get(i) == -3)
					g.drawImage(cPipeOff, rect.x, rect.y, this);

				// Normal moving pipe on
				else if (movingValues.get(i) == 3)
				{
					if (time % 10 <= 1 || time % 10 >= 9)
						g.drawImage(cPipeOn1, rect.x, rect.y, this);
					else if (time % 10 == 2 || time % 10 == 3 || time % 10 == 7
							|| time % 10 == 8)
						g.drawImage(cPipeOn2, rect.x, rect.y, this);
					else
						g.drawImage(cPipeOn3, rect.x, rect.y, this);
				}

				// Diagonal moving pipe off
				else if (movingValues.get(i) == -4)
					g.drawImage(cDiagOff, rect.x, rect.y, this);

				// Diagonal moving pipe on
				else if (movingValues.get(i) == 4)
				{
					if (time % 10 <= 1 || time % 10 >= 9)
						g.drawImage(cDiagOn1, rect.x, rect.y, this);
					else if (time % 10 == 2 || time % 10 == 3 || time % 10 == 7
							|| time % 10 == 8)
						g.drawImage(cDiagOn2, rect.x, rect.y, this);
					else
						g.drawImage(cDiagOn3, rect.x, rect.y, this);
				}
			}

			// Draws the buttons at the bottom
			g.drawImage(restartImage, 0, 550, this);
			g.drawImage(menuImage, 440, 550, this);
			g.drawImage(solveImage, 110, 553, this);
			g.drawImage(shuffleButton, 330, 553, this);

			// Draws the move count.
			g.setColor(Color.WHITE);
			g.setFont(font1);
			g.drawString("Moves: " + moveCount, 245, 590);
			g.drawString("Least Moves: " + minimumMoves, 225, 615);

			g.drawImage(wrench, mousePoint.x - wrench.getWidth(this) / 3,
					mousePoint.y - wrench.getHeight(this) / 3, this);

		}

		// If the game has been won, go to the win screen
		else if (gameStart && won)
		{
			// Congratulatory message
			g.setColor(Color.WHITE);
			g.setFont(font2);
			g.drawString("CONGRATULATIONS!", 75, 200);
			g.setFont(font1);
			g.drawString("You solved the puzzle in " + moveCount + " moves.",
					175, 250);
			g.drawString(
					"The least possible moves needed without wildcards was "
							+ minimumMoves + " moves.", 75, 300);

			// Draw buttons in different locations
			g.drawImage(restartImage, 165, 425, this);
			g.drawImage(menuImage, 275, 425, this);
			g.drawImage(solveImage, 165, 350, this);
			g.drawImage(shuffleButton, 275, 350, this);
		}

		// Draw the level select screen
		else if (selectedMenuName.equals("levelSelect"))
		{
			g.setColor(Color.WHITE);
			g.setFont(font1);

			// Draws small rectangles representing each level, and a level
			// number above.
			for (int level = 0; level < MAX_LEVEL; level++)
			{
				// Draws the previews
				g.drawImage(images[level], levels[level].x, levels[level].y,
						this);
				// Draws the rectangle
				g.drawRect(levels[level].x, levels[level].y,
						levels[level].width, levels[level].height);
				// Centres and draws the level number
				if (level > 10)
					g.drawString("" + (level + 1), levels[level].x
							+ levels[level].width / 2 - 6, levels[level].y - 1);
				else
					g.drawString("" + (level + 1), levels[level].x
							+ levels[level].width / 2 - 3, levels[level].y - 1);
			}
			// Draws the title
			g.setFont(font2);
			g.drawString("Select a Level", 150, 50);

			// Draws the menu button at the bottom right corner of the screen
			g.drawImage(menuImage, 440, 550, this);
		}

	}

	/**
	 * Decides which menu is to be drawn and how the buttons should react on
	 * click. Makes sure that mouse click and mouse release are within the
	 * correct distance of each other for a more natural button.
	 * 
	 * @param x
	 *            the horizontal location of mouse click
	 * @param y
	 *            the vertical location of mouse click
	 * @param x2
	 *            the horizontal location of mouse release
	 * @param y2
	 *            the vertical location of mouse release
	 */
	private void decideMenu(int x, int y, int x2, int y2)
	{
		// Main menu
		if (selectedMenuName.equals("main"))
		{
			// Play game button, goes to level select menu
			if (x > 350 && x < 525 && y > 370 && y < 425 && x2 > 350
					&& x2 < 525 && y2 > 370 && y2 < 425)
			{
				selectedMenu = levelSelect;
				selectedMenuName = "levelSelect";
				repaint();

			}

			// Instructions button
			if (x > 340 && x < 525 && y > 450 && y < 500 && x2 > 340
					&& x2 < 525 && y2 > 450 && y2 < 500)
			{
				selectedMenu = instructions1;
				selectedMenuName = "instructions1";
				repaint();
			}

			// Exit button
			if (x > 460 && x < 525 && y > 530 && y < 575 && x2 > 460
					&& x2 < 525 && y2 > 530 && y2 < 575)
			{
				System.exit(0);
			}
		}

		// Level select
		else if (selectedMenuName.equals("levelSelect"))
		{
			gameStart = false;
			// Menu button
			if (x > 440 && y > 565 && x2 > 440 && y2 > 565)
			{
				selectedMenu = main;
				selectedMenuName = "main";
				repaint();
			}

			// Goes through each level rectangle and treats them as a button.
			// When a rectangle is pressed, game is started at the corresponding
			// level.

			for (int level = 0; level < MAX_LEVEL; level++)
			{
				if (levels[level].contains(x, y)
						&& levels[level].contains(x2, y2))
				{

					// Starts the game
					selectedMenu = background;
					selectedMenuName = "background";
					currentLevel = level + 1;

					newGame();
					newGame();

				}

			}
		}

		// First instruction panel
		else if (selectedMenuName.equals("instructions1"))
		{
			// Next instructions
			if (x > 435 && y > 595 && x2 > 435 && y2 > 595)
			{
				selectedMenu = instructions2;
				selectedMenuName = "instructions2";
				repaint();

			}

		}
		// Second instruction panel
		else if (selectedMenuName.equals("instructions2"))
		{
			// Next instructions
			if (x > 435 && y > 595 && x2 > 435 && y2 > 595)
			{
				selectedMenu = instructions3;
				selectedMenuName = "instructions3";
				repaint();

			}

		}
		// Final instruction panel
		else if (selectedMenuName.equals("instructions3"))
		{
			// Next instructions
			if (x > 410 && y > 595 && x2 > 410 && y2 > 595)
			{
				selectedMenu = main;
				selectedMenuName = "main";
				repaint();

			}

		}
		// Won game panel - gives options of what to do next
		else if (won)
		{
			// Show solution button
			if (165 < x && 275 > x && 350 < y && 425 > y)
			{
				won = false;
				restart();
				showSolution = true;
			}

			// New game button
			else if (275 < x && 385 > x && 350 < y && 425 > y)
			{
				won = false;
				newGame();

			}

			// Menu button
			else if (275 < x && 385 > x && 425 < y && 500 > y)
			{
				won = false;
				gameStart = false;
				selectedMenu = levelSelect;
				selectedMenuName = "levelSelect";
				repaint();
			}

			// Restart button
			else if (165 < x && 275 > x && 425 < y && 500 > y)
			{
				won = false;
				restart();
			}

		}

	}

	// Inner timer class to track time to animate the pipes leaking water.
	private class TimerEventHandler implements ActionListener
	{

		// Occurs every 60 milliseconds to track the current time.
		// Animation changes in periods of 10 (regarding time)
		public void actionPerformed(ActionEvent event)
		{
			time++;
			repaint();
		}
	}

	/**
	 * Inner class to handle mouse events
	 */
	private class MouseHandler extends MouseAdapter
	{

		/**
		 * Method to determine actions if mouse is pressed
		 * 
		 * @param event
		 *            the event of a mouse press
		 */
		public void mousePressed(MouseEvent event)
		{

			Point currentPoint = event.getPoint();

			// Tracks the mouse pressed location so it can be sure that later on
			// in mouse released
			// the same button/pipe was pressed. This makes it more natural.
			firstPoint = currentPoint;

			// Takes action only if the game started and is not over.
			if (gameStart && !won)
			{

				// Goes through each movable rectangle and checks if one has
				// been clicked
				selectedRectPoint = null;
				for (int i = 0; i < movingRects.size(); i++)
				{
					if (movingRects.get(i).contains(currentPoint))
					{
						// Tracks the rectangles that have been selected and
						// their location
						selectedRect = i;
						selectedRectPoint = currentPoint;
					}

				}

				// Tracks which pipes have been clicked
				for (int row = 0; row < gridSize; row++)
					for (int col = 0; col < gridSize; col++)
					{
						if (rectangleGrid[row][col].contains(currentPoint))
						{

							clickedRect = rectangleGrid[row][col];
							clickedPipe = Math.abs(pipeGrid.getPipe(row, col));

							// Tracks the original location of a movable pipe
							if (clickedPipe == 3 || clickedPipe == 4)
							{
								draggedPlace = 10 * row + col;
							}

							repaint();
						}
					}

				// If a button has been pressed, remember which one it was.
				// If the same is released, action is performed.
				for (Rectangle button : buttons)
				{
					if (button.contains(currentPoint))
					{
						selectedButton = button;
					}
				}

			}
		}

		/**
		 * Method to determine actions if mouse is released
		 * 
		 * @param event
		 *            the event of a mouse release
		 */
		public void mouseReleased(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			// Changes menu if needed on mouse release.
			decideMenu(firstPoint.x, firstPoint.y, currentPoint.x,
					currentPoint.y);

			// Action only if the game has started and is not over.
			if (gameStart && !won)
			{
				// Clears the moving rectangles and their values and updates
				// them later.
				movingRects.clear();
				movingValues.clear();

				// Goes through each part of the grid.
				for (int row = 0; row < gridSize; row++)
					for (int col = 0; col < gridSize; col++)
					{

						if (rectangleGrid[row][col].contains(currentPoint))
						{
							// Checks if location of release is inside the same
							// rectangle that was pressed.
							// Toggles that location if it is true.
							if (rectangleGrid[row][col].equals(clickedRect))
							{
								if (pipeGrid.getPipe(row, col) != 0)
								{
									pipeGrid.toggle(row, col);

									moveCount++;
								}
							}

							// If the solution was shown, remove a toggled move
							// from the solution.
							if (showSolution)
								if (solveMoves.contains(10 * row + col))
									solveMoves.remove(solveMoves.indexOf(10
											* row + col));

						}
					}

				// Updates the moving pipes.
				for (int row = 0; row < gridSize; row++)
					for (int col = 0; col < gridSize; col++)
					{
						if (Math.abs(pipeGrid.getPipe(row, col)) == 3
								|| Math.abs(pipeGrid.getPipe(row, col)) == 4)
						{
							movingRects.add(new Rectangle(GRID_X + row
									* RECT_SIZE, GRID_Y + col * RECT_SIZE,
									RECT_SIZE, RECT_SIZE));
							movingValues.add(pipeGrid.getPipe(row, col));

						}

					}

				// Restart button
				if (restartButton.contains(currentPoint)
						&& restartButton.equals(selectedButton))
				{
					restart();
				}

				// Show solution button
				if (showSolutionButton.contains(currentPoint)
						&& showSolutionButton.equals(selectedButton))
				{
					restart();
					showSolution = true;
				}

				// New game button
				if (newGameButton.contains(currentPoint)
						&& newGameButton.equals(selectedButton))
				{
					newGame();
				}

				// Menu button
				if (menuButton.contains(currentPoint)
						&& menuButton.equals(selectedButton))
				{
					gameStart = false;
					selectedMenu = levelSelect;
					selectedMenuName = "levelSelect";
					repaint();

				}

				// Repaints everything on mouse release.
				repaint();

				// If the puzzle is solved, go to the won game menu
				// by setting won to be true.
				if (pipeGrid.isSolved())
				{
					showSolution = false;
					won = true;

				}
			}
		}
	}

	/**
	 * Inner Class to handle mouse movements
	 */
	private class MouseMotionHandler implements MouseMotionListener
	{
		/**
		 * Method to determine actions if mouse is moved
		 * 
		 * @param event
		 *            the event of a mouse movement
		 */
		public void mouseMoved(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			if (gameStart && !won)
			{
				// Tracks the current location of the mouse, to draw the wrench.
				mousePoint = currentPoint;
				repaint();
			}
		}

		/**
		 * Method to determine actions if mouse is dragged
		 * 
		 * @param event
		 *            the event of a mouse drag
		 */
		public void mouseDragged(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			// Performs action only when the game has started and is not over.
			if (gameStart && !won)
			{

				// Tracks mouse to draw the wrench.
				mousePoint = currentPoint;
				repaint();

				// Draws and changes where the movable pipes are and determines
				// where they can go.
				if (movingRects.size() > 0)
				{
					// Gets the selected rectangle (the one that was clicked)
					Rectangle next = movingRects.get(selectedRect);

					// Chooses 4 points slightly inside the rectangle to later
					// check if they collide with anything.
					// This is a pretty clumsy way of doing it, may need to
					// change it later.
					Point west = new Point(next.x + 5, currentPoint.y);
					Point east = new Point(next.x + next.width - 5,
							currentPoint.y);
					Point north = new Point(currentPoint.x, next.y + 5);
					Point south = new Point(currentPoint.x, next.y
							+ next.height - 5);

					// Checks to make sure that the moving pipes are actually
					// movable.
					if (clickedPipe == 3 || clickedPipe == 4)
					{
						// Makes sure that the rectangle is on the conveyer
						// belt.
						boolean onConveyer = true;
						for (int row = 0; row < gridSize; row++)
							for (int col = 0; col < gridSize; col++)
							{
								// If the moving pipe collides with anything
								// other than a conveyer rectangle,
								// doesn't let it move.
								if (rectangleGrid[row][col].contains(north)
										|| rectangleGrid[row][col]
												.contains(south)
										|| rectangleGrid[row][col]
												.contains(east)
										|| rectangleGrid[row][col]
												.contains(west))
								{
									// This is to check what type of rectangle
									// it collides with.
									if (!((row == draggedPlace / 10 && col == draggedPlace % 10) || pipeGrid
											.getPipe(row, col) == 0))
										onConveyer = false;
								}
							}

						// If the point is on the conveyer (and the point
						// exists), move the pipe.
						if (selectedRectPoint != null && onConveyer)
						{

							// Moves the pipe
							movingRects.get(selectedRect).translate(
									currentPoint.x - selectedRectPoint.x,
									currentPoint.y - selectedRectPoint.y);

							// Updates the new location of the pipe in the
							// pipeGrid.
							// This is for when the moving pipe crosses into a
							// new rectangle.
							// Goes through each rectangle on the grid to check.
							for (int row = 0; row < gridSize; row++)
								for (int col = 0; col < gridSize; col++)
								{
									// Finds which rectangle the pipe is in.
									if (rectangleGrid[row][col]
											.contains(currentPoint)
											&& movingRects.get(selectedRect)
													.contains(currentPoint))
									{
										// Checks to see if that rectangle is an
										// empty conveyer.
										if (pipeGrid.getPipe(row, col) == 0)
										{

											// If it is an empty conveyer, sets
											// the static location
											// of the moving pipe there
											pipeGrid.setPipe(row, col,
													movingValues
															.get(selectedRect));
											// Changes the old location of the
											// moving pipe
											// to become an empty conveyer.
											pipeGrid.setPipe(draggedPlace / 10,
													draggedPlace % 10, 0);

											// Remembers this new place as the
											// current
											// static location of the moving
											// pipe.
											draggedPlace = 10 * row + col;

										}
									}
								}
							// Updates this location as the place where the
							// moving rectangle was selected, so that it can be
							// moved in comparison to here later on (i.e. in the
							// next instance of mouseDragged)
							selectedRectPoint = currentPoint;

						}

					}
					repaint();

				}
			}
		}
	}
}
