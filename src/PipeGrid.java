/**The grid that has all of the logic behind the game Pipe Panic.
 * @author Tom Huang and Victor Cong
 * @version June 16, 2012
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PipeGrid
{

	// The grid representing the type of pipes.
	// 0 = conveyer belt
	// 1 = normal pipe, toggles in a plus sign shape
	// 2 = diagonal pipe, toggles in a x shape
	// 3 = moving normal pipe, moves along conveyer
	// 4 = moving diagonal pipe
	// Negative numbers indicate a pipe that is off, positive one that is on.
	private int[][] grid;

	// The list of moves that should be clicked to solve the game, stored as integers in base 10.
	//The number/10 is the row, the number%10 is the column.
	private ArrayList<Integer> solveMoves;
	// The type of pipe corresponding to each of the moves, mostly just to track
	// the positions of moving pipes
	private ArrayList<Integer> solveType;
	// The size of the grid.
	private int gridSize;

	/**
	 * Constructor given a file.
	 * 
	 * @param str
	 *            the name of the file
	 */
	public PipeGrid(String str)
	{
		try
		{

			// Creates a scanner the read the file.
			Scanner fileIn = new Scanner(new File(str));

			// Takes the first line to determine the grid size
			String firstLine = fileIn.nextLine();
			gridSize = firstLine.length();

			solveMoves = new ArrayList<Integer>();
			solveType = new ArrayList<Integer>();

			// Creates the grid based on the found size.
			grid = new int[gridSize][gridSize];

			// Goes through each character of the first line and converts it
			// into an integer.
			for (int index = 0; index < gridSize; index++)
			{
				grid[0][index] = -Character.digit(firstLine.charAt(index), 10);
			}

			// Does the same for each remaining line.
			for (int row = 1; row < gridSize; row++)
			{
				String nextLine = fileIn.nextLine();
				for (int index = 0; index < gridSize; index++)
				{
					grid[row][index] = -Character.digit(nextLine.charAt(index),
							10);
				}
			}

			// Creates a random number to see if a pipe should move.
			double shouldMove = Math.random();
			double luck = 0;
			// Generates a random puzzle based on the read file, and continues
			// to do so until
			// an unsolved combination is reached.
			do
			{
				// Moves the movable pipes randomly and naturally along the
				// conveyer
				// system. Each pipe will be toggled after, and then the movable
				// pipes
				// will be moved again for a more interesting layout.
				do
				{
					// Goes through each pipe on the grid.
					for (int row = 0; row < gridSize; row++)
						for (int col = 0; col < gridSize; col++)
						{
							// Checks if the pipe is movable.
							if (Math.abs(grid[row][col]) == 3
									|| Math.abs(grid[row][col]) == 4)
							{
								// Remembers the value of the current pipe.
								int previousPipe = grid[row][col];

								// A constant to give a high chance of the pipe
								// moving.
								luck = 0.99;
								// A random number to determine whether the pipe
								// should move.
								shouldMove = Math.random();
								if (shouldMove < luck)
								{
									// Chooses a random direction for the pipe
									// to move in. Also checks for boundaries.
									int direction = (int) (4 * Math.random());

									// Moves left
									if (direction == 0 && col - 1 >= 0
											&& grid[row][col - 1] == 0)
									{
										grid[row][col] = 0;
										grid[row][col - 1] = previousPipe;
									}
									// Moves up
									else if (direction == 1 && row - 1 >= 0
											&& grid[row - 1][col] == 0)
									{
										grid[row][col] = 0;
										grid[row - 1][col] = previousPipe;
									}
									// Moves down
									else if (direction == 2
											&& row + 1 < gridSize
											&& grid[row + 1][col] == 0)
									{
										grid[row][col] = 0;
										grid[row + 1][col] = previousPipe;
									}
									// Moves right
									else if (direction == 3
											&& col + 1 < gridSize
											&& grid[row][col + 1] == 0)
									{
										grid[row][col] = 0;
										grid[row][col + 1] = previousPipe;
									}
									// Stops the search for more pipes to move.
									// Since the pipe is now in a new place,
									// when the while loop repeats, a different
									// pipe will move.
									break;

								}
							}

						}
				} while (shouldMove < luck);

				// Randomly generate a solvable pattern
				// A chance between 0 and 29% for each pipe to be toggled.
				double chance = Math.random() * 0.29;

				for (int row = 0; row < gridSize; row++)
					for (int col = 0; col < gridSize; col++)
					{
						// Toggles the pipe at a chance%, and makes sure that
						// the
						// number of moves needed to solve the grid is less than
						// or equal to 6, making it possible for a human to
						// solve.
						if (chance > Math.random() && solveMoves.size() < 6)
						{

							// Toggles the grid if it's not a conveyer.
							if (grid[row][col] != 0)
							{

								toggle(row, col);
								// Adds the move to the solution.
								if (!solveMoves.contains(10 * row + col))
								{
									solveMoves.add(10 * row + col);
									solveType.add(grid[row][col]);
								}
								// Removes the move from the solution if it is
								// already there
								// (2 toggles in the same spot is equivalent to
								// none)
								else
								{
									int index = solveMoves.indexOf(10 * row
											+ col);
									solveMoves.remove(index);
									solveType.remove(index);
								}
							}
						}
					}// end of for loop

				// Repeat of earlier code, to randomly move the movable pipes
				// again.
				do
				{
					for (int row = 0; row < gridSize; row++)
						for (int col = 0; col < gridSize; col++)
						{
							if (Math.abs(grid[row][col]) == 3
									|| Math.abs(grid[row][col]) == 4)
							{
								int previousPipe = grid[row][col];

								shouldMove = Math.random();
								if (shouldMove < luck)
								{
									int direction = (int) (4 * Math.random());
									if (direction == 0 && col - 1 >= 0
											&& grid[row][col - 1] == 0)
									{
										grid[row][col] = 0;
										grid[row][col - 1] = previousPipe;
									} else if (direction == 1 && row - 1 >= 0
											&& grid[row - 1][col] == 0)
									{
										grid[row][col] = 0;
										grid[row - 1][col] = previousPipe;
									} else if (direction == 2
											&& row + 1 < gridSize
											&& grid[row + 1][col] == 0)
									{
										grid[row][col] = 0;
										grid[row + 1][col] = previousPipe;
									} else if (direction == 3
											&& col + 1 < gridSize
											&& grid[row][col + 1] == 0)
									{
										grid[row][col] = 0;
										grid[row][col + 1] = previousPipe;
									}
									break;

								}
							}

						}
				} while (shouldMove < luck);
			} while (isSolved());

			// Randomly generates a wildcard at the end. These replace normal
			// pipes.
			// 10% chance of a grid to be considered to have a wildcard.
			if (Math.random() < 0.1)
			{
				// Chooses a random location on the grid
				int index = (int) (gridSize * gridSize * Math.random());
				// If the pipe at that location is normal, than it will become a
				// wildcard.
				if (Math.abs(grid[index / gridSize][index % gridSize]) == 1)
				{
					grid[index / gridSize][index % gridSize] = 10;

				}
			}

		} catch (FileNotFoundException exp)
		{
			System.out.println("Your file is invalid.");
		}
	}

	/**
	 * Constructor for a default 5x5 grid with all normal pipes.
	 * 
	 */
	public PipeGrid()
	{
		solveMoves = new ArrayList<Integer>();
		solveType = new ArrayList<Integer>();
		gridSize = 5;
		grid = new int[gridSize][gridSize];

		// Creates a basic grid of normal pipes.
		for (int row = 0; row < gridSize; row++)
			for (int col = 0; col < gridSize; col++)
			{

				grid[row][col] = -1;

			}

		// Toggles 2 to 7 different locations.
		int noOfMoves = (int) (5 * Math.random() + 2);
		for (int move = 0; move < noOfMoves; move++)
		{
			// Toggles a spot a ranodm
			int row = (int) (gridSize * Math.random());
			int col = (int) (gridSize * Math.random());
			if (grid[row][col] < 10)
				toggle(row, col);
			// Adds or removes the spot from the solution as required.
			if (!solveMoves.contains(10 * row + col))
			{
				solveMoves.add(10 * row + col);
				solveType.add(grid[row][col]);
			}

			else
			{
				int index = solveMoves.indexOf(10 * row + col);
				solveMoves.remove(index);
				solveType.remove(index);
			}

		}
	}

	/**
	 * Checks whether a specific pipe is on, which is if it is positive.
	 * 
	 * @param row
	 *            the row of the pipe being checked
	 * @param col
	 *            the column of the pipe being checked
	 * @return true if the pipe is on, false otherwise.
	 */
	public boolean isOn(int row, int col)
	{
		return (grid[row][col] > 0);
	}

	/**
	 * Checks if the grid has been solved, which is when all pipes are off.
	 * 
	 * @return true if all the pipes are off, false otherwise.
	 */
	public boolean isSolved()
	{

		for (int row = 0; row < gridSize; row++)
			for (int col = 0; col < gridSize; col++)
			{
				if (isOn(row, col))
					return false;
			}
		return true;
	}

	/**
	 * Toggles a pipe, which affects the on/off situation of pipes surrounding
	 * it depending on its type.
	 * 
	 * @param row
	 *            the row of the pipe being toggled
	 * @param col
	 *            the column of the pipe being toggled
	 */
	public void toggle(int row, int col)
	{
		// Toggles in a plus sign shape for normal pipes, when the pipe value is
		// 1 or 3 (for moving pipes)
		if (Math.abs(grid[row][col]) == 1 || Math.abs(grid[row][col]) == 3)
		{
			grid[row][col] *= -1;
			if (row + 1 < gridSize)
				grid[row + 1][col] *= -1;
			if (row - 1 >= 0)
				grid[row - 1][col] *= -1;
			if (col - 1 >= 0)
				grid[row][col - 1] *= -1;
			if (col + 1 < gridSize)
				grid[row][col + 1] *= -1;
		}
		// Toggles in an x shape for diagonal pipes, when the pipe value is 2 or
		// 4 (for moving pipes)
		else if (Math.abs(grid[row][col]) == 2 || Math.abs(grid[row][col]) == 4)
		{
			grid[row][col] *= -1;
			if (row + 1 < gridSize && col + 1 < gridSize)
				grid[row + 1][col + 1] *= -1;
			if (row - 1 >= 0 && col - 1 >= 0)
				grid[row - 1][col - 1] *= -1;
			if (col - 1 >= 0 && row + 1 < gridSize)
				grid[row + 1][col - 1] *= -1;
			if (row - 1 >= 0 && col + 1 < gridSize)
				grid[row - 1][col + 1] *= -1;
		}

		// Wildcards turn everything in a 3x3 square around them off.
		else if (Math.abs(grid[row][col]) == 10)
		{
			grid[row][col] = -1;
			if (row + 1 < gridSize && col + 1 < gridSize)
				grid[row + 1][col + 1] = -1 * Math.abs(grid[row + 1][col + 1]);
			if (row - 1 >= 0 && col - 1 >= 0)
				grid[row - 1][col - 1] = -1 * Math.abs(grid[row - 1][col - 1]);
			if (col - 1 >= 0 && row + 1 < gridSize)
				grid[row + 1][col - 1] = -1 * Math.abs(grid[row + 1][col - 1]);
			if (row - 1 >= 0 && col + 1 < gridSize)
				grid[row - 1][col + 1] = -1 * Math.abs(grid[row - 1][col + 1]);
			if (row + 1 < gridSize)
				grid[row + 1][col] = -1 * Math.abs(grid[row + 1][col]);
			if (row - 1 >= 0)
				grid[row - 1][col] = -1 * Math.abs(grid[row - 1][col]);
			if (col - 1 >= 0)
				grid[row][col - 1] = -1 * Math.abs(grid[row][col - 1]);
			if (col + 1 < gridSize)
				grid[row][col + 1] = -1 * Math.abs(grid[row][col + 1]);
		}

	}

	/**
	 * Goes through all the rows of the grid except the last,
	 * "bringing the lights down". It does this by toggling directly under every
	 * pipe. The end result is compressing the entire grid into a single pattern
	 * at the bottom row.
	 */
	public void bringDown()
	{

		for (int row = 0; row < gridSize - 1; row++)
		{
			for (int col = 0; col < gridSize; col++)
			{
				// If the pipe is positive, it is on. Toggling the pipe directly
				// underneath will turn it on in this row.
				if (grid[row][col] > 0)
				{
					toggle(row + 1, col);

					// Adds or removes the toggling from the solution as needed.
					if (!solveMoves.contains(10 * (row + 1) + col))
						solveMoves.add(10 * (row + 1) + col);
					else
						solveMoves.remove(solveMoves.indexOf(10 * (row + 1)
								+ col));
				}
			}
		}
	}

	/**
	 * The algorithm to solve a 5x5 board of normal pipes (the classic lights
	 * out game).
	 * 
	 */
	public void solve()
	{
		solveMoves.clear();
		solveType.clear();

		bringDown();

		// The next part of the algorithm involves toggling 1 spot at the top of
		// the grid,
		// and "bringing it down". If the result is not solved, it proceeds to
		// the spot beside it
		// and does the same thing.
		for (int row2 = 0; row2 < gridSize; row2++)
		{
			// Makes sure that the grid isn't already solved.
			if (isSolved() == false)
			{
				// Toggles the spot at the top.
				toggle(0, row2);

				// Does the same "bringing down" algorithm as above.
				bringDown();

			}

		}

		// The final part of the algorithm involves toggling each spot at the
		// top of the grid,
		// and "bringing it down" once again. However, this time,if the result
		// is not solved, it undoes this by repeating
		// this process for that same spot. It does this for each spot in the
		// top row.
		for (int row2 = 0; row2 < gridSize; row2++)
		{
			// Brings down the first time
			if (isSolved() == false)
			{
				toggle(0, row2);
				bringDown();

			}
			// If it was unsuccessful, brings it up, which is just bringing it
			// down twice.
			if (isSolved() == false)
			{
				toggle(0, row2);
				bringDown();

			}

		}

		// Finally, once the grid has been solved, the solveMoves will be
		// recorded,
		// and the grid will be composed entirely of -1s (normal pipes that are
		// off).
		// To return back to the original grid, it goes through each move of the
		// solve moves
		// and toggles them, to undo the solving.
		for (int index : solveMoves)
		{
			toggle(index / 10, index % 10);
		}

	}

	/**
	 * Getter for the size of the grid
	 * 
	 * @return the size of the grid.
	 */
	public int getSize()
	{
		return gridSize;
	}

	/**
	 * Gives the solution to the grid.
	 * 
	 * @return the integer coordinates of where to click.
	 */
	public ArrayList<Integer> showSolution()
	{
		return solveMoves;
	}

	/**
	 * Getter for the value of a particular pipe
	 * 
	 * @param row
	 *            the row of the pipe
	 * @param col
	 *            the column of the pipe
	 * @return the value of the pipe
	 */
	public int getPipe(int row, int col)
	{
		return grid[row][col];
	}

	/**
	 * Sets the value of a pipe
	 * 
	 * @param row
	 *            the row of the pipe
	 * @param col
	 *            the column of the pipe
	 * @param setValue
	 *            the value that the pipe is being set to
	 */
	public void setPipe(int row, int col, int setValue)
	{
		grid[row][col] = setValue;
	}
	

	/**Restarts the grid by resetting the toggle pattern to the original without changing the grid.
	 * 
	 */
	public void restart()
	{
		//Turns everything off
		for (int row = 0; row < gridSize; row++)
			for (int col = 0; col < gridSize; col++)
			{
				grid[row][col] = -Math.abs(grid[row][col]);
			}

		//If it was not a read in file, then there were no conveyers.
		//Thus, the solveMoves can be relied on to restart the grid.
		if (solveType.size() == 0)
			for (int place : solveMoves)
			{
				toggle(place / 10, place % 10);
			}

		else
			for (int place = 0; place < solveMoves.size(); place++)
			{
				//If the solution originally had a conveyer, creates a conveyer there, 
				//toggles, and returns it to the original, to simulate a conveyer without
				//actually moving one there.
				if (Math.abs(solveType.get(place)) == 4
						|| Math.abs(solveType.get(place)) == 3)
				{
					int original = grid[solveMoves.get(place) / 10][solveMoves
							.get(place) % 10];
					grid[solveMoves.get(place) / 10][solveMoves.get(place) % 10] = Math
							.abs(solveType.get(place));
					toggle(solveMoves.get(place) / 10,
							solveMoves.get(place) % 10);
					grid[solveMoves.get(place) / 10][solveMoves.get(place) % 10] = original;

				}
				//If there is no conveyer, proceed as normal.
				else
					toggle(solveMoves.get(place) / 10,
							solveMoves.get(place) % 10);
			}
	}

	/**A brute force to check the legitimacy of the solve() method.
	 * Goes through each of the 2^25 = 33554432 ways that the board could be toggled,
	 * and solves each one. For each case, it confirms that all the pipes are off before
	 * proceeding to the next pipe. Total run time is about 1 hour 30 minutes.
	 * 
	 */
	public void checkSolution()
	{
		//Creates a linear array which will represent a pattern of the grid.
		int[] solveCheck = new int[gridSize * gridSize];
		
		//Each integer in binary will create a distinct solveCheck array.
		for (int check = 0; check < 33554432; check++)
		{
			//Stores the value of check
			int store = check;
			
			//Turns everything off.
			for (int row = 0; row < gridSize; row++)
				for (int col = 0; col < gridSize; col++)
				{
					grid[row][col] = -1;
				}
			
			//Turns the integer check into an array of 1s and 0s for solveCheck
			//by converting it to binary.
			for (int i = 0; i < solveCheck.length; i++)
			{
				solveCheck[i] = store % 2;
				store = store / 2;

			}

			//Converts solveCheck into a unique toggling pattern on the grid.
			for (int pipe = 0; pipe < solveCheck.length; pipe++)
			{
				if (solveCheck[pipe] > 0)
					toggle(pipe / 5, pipe % 5);
			}

			//Uses the solve.
			solve();
			
			//If anything is not solved at this point, breaks the loop and prints out the
			//toggling pattern that doesn't work.
			if (!isSolved())
			{
				System.out.println("crap, " + check + " doesn't work");
				break;
			}
		}

	}

}
