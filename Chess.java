/*
 * Name: Justin Senia
 * E-Number: E00851822
 * Date: 10/20/2017
 * Class: COSC 461
 * Project: #2
 */
import java.util.LinkedList;
import java.io.*;
import java.util.*;

//This program is a chess game, utilizing alpha-beta pruning,
//depth limits and minmax heuristics to improve run time
public class Chess{
	
	private final char EMPTY = ' ';		//empty slot
	private final char COMP_KING 	= 'K';	//computer
	private final char COMP_BISHOP 	= 'B';	//computer
	private final char COMP_ROOK 	= 'R';	//computer
	private final char PLYR_KING	= 'k';	//player
	private final char PLYR_BISHOP	= 'b';	//player
	private final char PLYR_ROOK	= 'r';	//player
	private final int MIN = 0;			//min level
	private final int MAX = 1;			//max level
	private final int LIMIT = 5;		//depth limit
	
	//used for populating game board
	//public char[] compGamePieces = {'K', 'B', 'B', 'R', 'R'};
	//public char[] plyrGamePieces = {'k', 'b', 'b', 'r', 'r'};
	
	
	//Board class (inner class)
	private class Board
	{
		private char[][] array;			//board array
		
		//Constructor of Board class
		private Board(int size)
		{
			array = new char[size][size]; 	//create array
											//fill with empty slots
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
					array[i][j] = EMPTY;
		}
		
		//constructor for populating initial board of Board class
		//(uses passed boolean to overload constructor)
		private Board(int size, boolean initPop)
		{
			//used for populating game board
			char[] compGamePieces = {'K', 'B', 'B', 'R', 'R'};
			char[] plyrGamePieces = {'k', 'b', 'b', 'r', 'r'};
			
			//initializing array to all blank characters
			array = new char[size][size]; 	//create array
											//fill with empty slots
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
					array[i][j] = EMPTY;
				
			//populating array with (appropriately located) randomly placed chess pieces
			for (int i = 0; i < size; i++)
			{
				if (i == 0)	//populates computer's board side randomly with chess pieces
				{	
					while (arrayNotEmpty(compGamePieces))	//while computer still has chess pieces
					{										//to randomly place
						int randLoc   = (int)(Math.random() * 6); //random location on board
						int randPiece = (int)(Math.random() * 5); //rand location on piece array
						
						//if rand gameboard location is empty and rand gamepiece location
						//has piece available, transfer piece onto board
						if (array[i][randLoc] == ' ' && compGamePieces[randPiece] != ' ')
						{
							array[i][randLoc] = compGamePieces[randPiece];
							compGamePieces[randPiece] = ' ';
						}
					}
				}
				else if (i < 5) //populates 
				{
					; //skips all middle sections that already contain empty chars
				}
				else if (i == 5) //populates user's board side randomly with chess pieces
				{				
					while (arrayNotEmpty(plyrGamePieces)) 	//while player still has chess pieces
					{										//to randomly place
						int randLoc   = (int)(Math.random() * 6); //random location on board
						int randPiece = (int)(Math.random() * 5); //rand location on piece array
						
						//if rand gameboard location is empty and rand gamepiece location
						//has piece available, transfer piece onto board
						if (array[i][randLoc] == ' ' && plyrGamePieces[randPiece] != ' ')
						{
							array[i][randLoc] = plyrGamePieces[randPiece];
							plyrGamePieces[randPiece] = ' ';
						}
					}
				}
			}
		}
		
		//method checks if array is empty
		//used to continue/discontinue random chess piece population procedure
		private boolean arrayNotEmpty(char[] arIn)
		{
			for (int i = 0; i < arIn.length; i++)
			{
				if (arIn[i] != ' ')
					return true;
			}
			
			
				return false;
		}
	}
	
	private Board board;				//game board
	private int size = 6;					//size of board
	
	//constructor of Chess class
	public Chess()
	{
		this.board = new Board(size, true);	//create game board
	}
	
	//method plays game
	public void play()
	{	
		gameBoardDisplay(board);		//initially display board 
		
		//gives user instructions on how to move
		System.out.println("Please enter y x coordinates of piece to be moved,");
		System.out.println("then enter y x coordinates of where piece will move to.");
		System.out.println("Please separate all coordinates by spaces");
		System.out.println("Example:         Player Move: 4 0  3 0");
		
		while (true)					//computer and player take turns
		{
			board = playerMove(board);	//player move
			
			if (playerWin(board))		//if player wins then game over
			{
				System.out.println("Player Wins");
				break;
			}
			
			board = computerMove(board);	//computer move
			
			if (computerWin(board))			//if computer wins then game over
			{
				System.out.println("Computer Wins");
				break;
			}
			
		}
	}
	
	//method performs player move
	private Board playerMove(Board board)
	{
		System.out.print("Player move: ");			//prompt player
		Scanner scanner = new Scanner(System.in);	//read move
		
		int i = scanner.nextInt();
		int j = scanner.nextInt();
		
		int y = scanner.nextInt();
		int x = scanner.nextInt();
		
		while (isMoveValid(board, i, j, y, x, board.array[i][j], 'P', true) == false)
		{
			i = scanner.nextInt();
			j = scanner.nextInt();
			
			y = scanner.nextInt();
			x = scanner.nextInt();
		}
		
		board.array[y][x] = board.array[i][j];		//place player symbol
		board.array[i][j] = ' ';
		
		//displayBoard(board);						//display board
		gameBoardDisplay(board);
		
		return board;								//return updated board
	}
	
	//method determines computer move
	private Board computerMove(Board board)
	{												//generate children of board
		LinkedList<Board> children = generate(board, 'C');
		
		int maxIndex = 0;
		int maxValue = minmax(children.get(0), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
		
		for (int i = 1; i < children.size(); i++)	//find child with largest minmax value
		{
			int currentValue = minmax(children.get(i), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
			
			if (currentValue > maxValue)
			{
				maxIndex = i;
				maxValue = currentValue;
			}
		}
		
		Board result = children.get(maxIndex);	//choose child as next move
		
		System.out.println("Computer Move: ");
		
		//displayBoard(result);					//print next move
		gameBoardDisplay(result);
		
		return result;
	}
	
	//method computes minmax value of board
	private int minmax(Board board, int level, int depth, int alpha, int beta)
	{
		if (computerWin(board) || playerWin(board) || depth >= LIMIT)
			return testBoard(board);		//if board leaf
		
		else if (level == MAX)			//if board is at max level
		{
			int maxValue = Integer.MIN_VALUE;
			
			LinkedList<Board> children = generate(board, 'C');
								//generate children of board
			for (int i = 0; i < children.size(); i++)
			{
				int currentValue = minmax(children.get(i), MIN, depth+1, alpha, beta);
								//find minmax values of children
				if (currentValue > maxValue)
					maxValue = currentValue;
								//if maximum exceeds beta then stop
					if (maxValue >= beta)
						return maxValue;
								//if maximum exceeds alpha then update alpha
					if (maxValue > alpha)
						alpha = maxValue;
			}
			
			return maxValue;	//return maximum value
		}
		else					//if board is at min level
		{
			int minValue = Integer.MAX_VALUE;
			
			LinkedList<Board> children = generate(board, 'P');
								//generate children of board
			for (int i = 0; i < children.size(); i++)
			{
				int currentValue = minmax(children.get(i), MAX, depth+1, alpha, beta);
								//find the minimym of minmax values of children
				if (currentValue < minValue)
					minValue = currentValue;
								//if minimum is less than alpha stop
				if (minValue <= alpha)
					return minValue;
								//if minimum is less than beta, update beta
				if (minValue < beta)
					beta = minValue;
			}
			
			return minValue;	//return minimum value
		}
	}
	
	//method generates children of a board using a symbol
	private LinkedList<Board> generate(Board board, char symbol)
	{
		LinkedList<Board> children = new LinkedList<Board>();
												//empty list of children
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)		//go through board
			{
				if (board.array[i][j] == 'K')
				{								//if slot is empty
					for (int y = i-1; y < i+1; y++)
					{
						for (int x = j-1; x < j+1; x++)
						{
							if (isMoveValid(board, i, j, y, x, 'K', 'C', false) == true)
							{
								Board child = copy(board);
								child.array[y][x] = child.array[i][j];
								child.array[i][j] = ' ';
									System.out.println(i + " " + j + " | " + y + " " + x + " |" + testBoard(child));
								children.addLast(child);
							}
						}
					}
				}
				else if (board.array[i][j] == 'B')
				{								
					for (int y = i-1; y < i+1; y++)
					{
						for (int x = j-1; x < j+1; x++)
						{
							if (isMoveValid(board, i, j, y, x, 'B', 'C', false) == true)
							{
								Board child = copy(board);
								child.array[y][x] = child.array[i][j];
								child.array[i][j] = ' ';
									System.out.println(i + " " + j + " | " + y + " " + x + " |" + testBoard(child));
								children.addLast(child);
							}
						}
					}
				}
				else if (board.array[i][j] == 'R')
				{								
					for (int y = i-1; y < i+1; y++)
					{
						for (int x = j-1; x < j+1; x++)
						{
							if (isMoveValid(board, i, j, y, x, 'R', 'C', false) == true)
							{
								Board child = copy(board);
								child.array[y][x] = child.array[i][j];
								child.array[i][j] = ' ';
									System.out.println(i + " " + j + " | " + y + " " + x + " |" + testBoard(child));
								children.addLast(child);
							}
						}
					}
					
			//if slot is empty
					Board child = copy(board);	//put the symbol and
					child.array[i][j] = symbol; //create child board
					children.addLast(child);
				}
			}
		return children;						//return list of children
	}
	
	//method checks whether computer wins
	private boolean computerWin(Board board)
	{
		return check(board, 'C');	//checks if computer wins somewhere on board
	}
	
	//method checks whether player wins
	private boolean playerWin(Board board)
	{
		return check(board, 'P');	//checks if player wins somewhere on board
	}
	/*
	//Method evaluates a board
	private int evaluate(Board board)
	{
		if (computerTakeKing(board)) 			//score is 10*size if computer wins
			return 10*size;
		else if (computerTakeBishRook(board))	//score is 5*size if computer takes
			return 5*size;						//opponent's bishop or rook
		else if (playerTakeBishRook(board))		//score is -5*size if human player takes
			return -5*size;						//opponent's bishop or rook
		else if (playerTakeKing(board))			//score is -10*size if player wins
			return -10*size;
		else
			return count(board, 'C') - count(board, 'P');
			//score is diference between computer and player winnings for partial board
	}								
	
	//method determines if computer is going to capture it's opponent's king
	private boolean computerTakeKing(Board board)
	{
		
	}
	
	//method determines if computer is going to capture one of it's opponent's rooks/bishops
	private boolean computerTakeKing(Board board)
	{
		
	}
	
	//method determines if human player is going to capture it's opponent's king
	private boolean computerTakeKing(Board board)
	{
		
	}
	
	//method determines if human player is going to capture one of it's opponent's rooks/bishops
	private boolean computerTakeKing(Board board)
	{
		
	}
	
	//method counts value of each potential board
	private int count(Board board, char symbol)
	{
		int answer = 0;
		
		if (symbol == 'C')
		{
			answer = testBoard(board, symbol);
		}
		else if (symbol == 'P')
		{
			answer = testBoard(board, symbol);
		}
		return answer;
	}
	*/
	
	//method checks to see if computer or player won
	private boolean check(Board board, char compOrPlayer)
	{
		if (compOrPlayer == 'C')
		{
			for (int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
				{
					if (board.array[i][j] == 'k')
						return false;
				}
			}
		}
		else if (compOrPlayer == 'P')
		{
			for (int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
				{
					if (board.array[i][j] == 'K')
						return false;
				}
			}
		}
		
		return true;
	}
	
	
	//method checks for values of pieces left on the board
	private int testBoard(Board board)
	{
		int compBoardPoints = 0;
		int plyrBoardPoints = 0;
		
		int totalBoardVal = 0;
		
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (board.array[i][j] == 'K')
					compBoardPoints = compBoardPoints + 20;
				else if (board.array[i][j] == 'B' || board.array[i][j] == 'R')
					compBoardPoints = compBoardPoints + 2;
				
				if (board.array[i][j] == 'k')
					plyrBoardPoints = plyrBoardPoints + 20;
				else if (board.array[i][j] == 'b' || board.array[i][j] == 'r')
					plyrBoardPoints = plyrBoardPoints + 2;
			}
		}
		
		totalBoardVal = compBoardPoints - plyrBoardPoints;
		
		return totalBoardVal;
	}
	
	
	
	//method makes copy of board
	private Board copy(Board board)
	{
		Board result = new Board(size);
		
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				result.array[i][j] = board.array[i][j];
			
		return result;
	}
	
	//method determines if move is valid based on piece to be moved
	private boolean isMoveValid(Board board, int y1, int x1, int y2, 
	int x2, char chssPiece, char turn, boolean plyrMsg)
	{
		//checks if player is trying to move out of bounds
		if (y2 < 0 || y2 > 5 || x2 < 0 || x2 > 5)
		{
			if (plyrMsg == true)
				System.out.println("Destination is out of bounds!");
			//else 
				//System.out.println("dest OOB : " + y1 + " " + x1 + " " + y2 + " " + x2);
			return false;
		}
		
		//checks if player has initially selected a space out of bounds
		else if (y1 < 0 || y1 > 5 || x1 < 0 || x1 > 5)
		{
			if (plyrMsg == true)
				System.out.println("Initial target is out of bounds!");
			//else 
				//System.out.println("init OOB : " + y1 + " " + x1 + " " + y2 + " " + x2);
			return false;
		}
		
		//checks if player has initially selected an empty space
		else if (chssPiece == ' ')
		{
			if (plyrMsg == true)
				System.out.println("Initial target is empty");
			//else 
				//System.out.println("init empty : " + y1 + " " + x1 + " " + y2 + " " + x2);
			return false;
		}
		
		//checks if human player is trying to move an opponent's chess piece
		else if (turn == 'P')
		{
			if (chssPiece == 'K' || chssPiece == 'B' || chssPiece == 'R')
			{
				if (plyrMsg == true)
					System.out.println("You cannot move an opponent's pieces!");
				//else 
					//System.out.println("Op Chss Piece : " + y1 + " " + x1 + " " + y2 + " " + x2);
				return false;
			}
		}
		
		//checks if human player tries to set their target and destination to same coords
		else if (y1 == y2 && x1 == x2)
		{
			if (plyrMsg == true)
				System.out.println("You cannot move to same location as original position!");
			//else 
				//System.out.println("init = dest : " + y1 + " " + x1 + " " + y2 + " " + x2);
			return false;
		}
		
		//if player tries to move chesspiece any number of spaces other than 1
		else if (Math.abs(y1 - y2) > 1 || Math.abs(x1 - x2) > 1)
		{
			if (plyrMsg == true)
				System.out.println("You cannot move any piece farther than 1 tile!");
			//else 
				//System.out.println("Move > 1 : " + y1 + " " + x1 + " " + y2 + " " + x2);
			return false;
		}
		
		//checks if player's move is compliant with the chess piece itself 
		else if (chssPiece == 'K' || chssPiece == 'k') 		//deals with king piece
		{
			if (chssPiece == 'K')
			{
				//invalid move if trying to capture your own chess piece
				if (board.array[y2][x2] == 'B' || board.array[y2][x2] == 'R')
				{
					//System.out.println("K capt own : " + y1 + " " + x1 + " " + y2 + " " + x2);
					return false;
				}
			}
			else if (chssPiece == 'k')
			{
				//invalid move if trying to capture your own chess piece
				if (board.array[y2][x2] == 'b' || board.array[y2][x2] == 'r')
				{
					//System.out.println("k capt own : " + y1 + " " + x1 + " " + y2 + " " + x2);
					return false;
				}
			}
		}
		else if (chssPiece == 'B' || chssPiece == 'b')		//deals with bishop piece
		{
			if (Math.abs(y1 - y2) != Math.abs(x1 - x2))
			{
				if (plyrMsg == true)
				{
					System.out.println("You must move a bishop diagonally!");
				}
				//else
					//System.out.println("bish diag : " + y1 + " " + x1 + " " + y2 + " " + x2);
				return false;
				
			}
			
			if (chssPiece == 'B')
			{
				//invalid move if trying to capture your own chess piece
				if (board.array[y2][x2] == 'K' || board.array[y2][x2] == 'R')
				{
					//System.out.println("B capt own : " + y1 + " " + x1 + " " + y2 + " " + x2);
					return false;
				}
			}
			else if (chssPiece == 'b')
			{
				//invalid move if trying to capture your own chess piece
				if (board.array[y2][x2] == 'k' || board.array[y2][x2] == 'r')
				{
					//System.out.println("b capt own : " + y1 + " " + x1 + " " + y2 + " " + x2);
					return false;
				}
			}
		}
		else if (chssPiece == 'R' || chssPiece == 'r')		//deals with rook piece
		{
			if (Math.abs(y1 - y2) == Math.abs(x1 - x2))
			{
				if (plyrMsg == true)
				{
					System.out.println("You must move a rook horizontally!");
				}
				//else
					//System.out.println("rook horiz : " + y1 + " " + x1 + " " + y2 + " " + x2);
				return false;
				
			}				
			
			if (chssPiece == 'R')
			{
				//invalid move if trying to capture your own chess piece
				if (board.array[y2][x2] == 'K' || board.array[y2][x2] == 'B')
				{
					//System.out.println("R capt own : " + y1 + " " + x1 + " " + y2 + " " + x2);
					return false;
				}
			}
			else if (chssPiece == 'r')
			{
				//invalid move if trying to capture your own chess piece
				if (board.array[y2][x2] == 'k' || board.array[y2][x2] == 'b')
				{
					//System.out.println("r capt own : " + y1 + " " + x1 + " " + y2 + " " + x2);
					return false;
				}
			}
		}
		
		System.out.println("****RETURN TRUE : " + y1 + " " + x1 + " " + y2 + " " + x2 + " ****");
		return true;
	}
	
	//method displays a board
	private void displayBoard(Board board)
	{
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
				System.out.print(board.array[i][j]);
			System.out.println();
		}
	}
	
		//This method displays the gameboard
		public void gameBoardDisplay(Board board)
		{
			System.out.print("      *");			//top row space buffer
			for (int b = 0; b < size; b++)
			{
				System.out.print("****");			//fills top row with star border
			}
			System.out.println("");					//goes to next line
			
			System.out.print("      *");			//top grid guide space buffer
			for (int b = 0; b < size; b++)
			{
				System.out.print(" " + b + " *");	//prints top grid guide coordinate helper
			}
			System.out.println("");					//goes to next line
			
			System.out.print("      *");			//bottom of top row space buffer
			for (int b = 0; b < size; b++)
			{
				System.out.print("****");			//fills bottom of top row with star border
			}
			System.out.println("");					//goes to next line
			
			System.out.print("***** -");			//first game grid line buffer
			for (int b = 0; b < size; b++)
			{
				System.out.print("----");			//draws first horiz line of game grid
			}
			System.out.println("");					//goes to next line
			
			for (int i = 0; i < size; i++)
			{
				System.out.print("* " + i + " * |");	//prints side grid coordinate helper
				for (int b = 0; b < size; b++)
				{
					//fills all spaces with array values
					System.out.print(" " + board.array[i][b] + " |");	
				}
				System.out.println("");					//goes to next line
				
				System.out.print("***** -");			//bottom line buffer
				for (int b = 0; b < size; b++)
				{
					System.out.print("----");			//draws bottom horiz line of game grid
				}
				System.out.println("");
			}
			System.out.println("");
		}
		
}