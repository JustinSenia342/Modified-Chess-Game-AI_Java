import java.util.LinkedList;
import java.io.*;
import java.util.*;

public class testRandom
{
	public static void main(String[] args)
	{
		/*
		for (int i = 0; i < 20; i++)
		{
			int randLoc   = (int)(Math.random() * 6);
			int randPiece = (int)(Math.random() * 5);
			
			System.out.println("RandLoc: " + randLoc + " RandPiece: " + randPiece);
		}
		*/
		
		
		popBoard(6, true);
	}
	
	public static void popBoard(int size, boolean initPop)
		{
			//used for populating game board
			char[] compGamePieces = {'K', 'B', 'B', 'R', 'R'};
			char[] plyrGamePieces = {'k', 'b', 'b', 'r', 'r'};
			
			/////////////////////added type of array
			char[][] array = new char[size][size]; 	//create array
											//fill with empty slots
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
					array[i][j] = ' ';
				
			for (int i = 0; i < size; i++)
			{
				if (i == 0)
				{	
					while (arrayNotEmpty(compGamePieces))
					{
						int randLoc   = (int)(Math.random() * 6);
						int randPiece = (int)(Math.random() * 5);
						
						if (array[i][randLoc] == ' ' && compGamePieces[randPiece] != ' ')
						{
							array[i][randLoc] = compGamePieces[randPiece];
							compGamePieces[randPiece] = ' ';
						}
					}
				}
				else if (i < 5)
				{
					for (int j = 0; j < size; j++)
					{
						array[i][j] = ' ';
					}
				}
				else if (i == 5)
				{
////////////////////					popBool = true;
					while (arrayNotEmpty(plyrGamePieces))
					{
						int randLoc   = (int)(Math.random() * 6);
						int randPiece = (int)(Math.random() * 5);
						
						if (array[i][randLoc] == ' ' && plyrGamePieces[randPiece] != ' ')
						{
							array[i][randLoc] = plyrGamePieces[randPiece];
							plyrGamePieces[randPiece] = ' ';
						}
					}
				}
				

			}
			/////////////////
			for (int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
					System.out.print(" " + array[i][j] + " ");
				System.out.println("");
			}
		}
		
		//method checks if array is empty
		public static boolean arrayNotEmpty(char[] arIn)
		{
			for (int i = 0; i < arIn.length; i++)
			{
				if (arIn[i] != ' ')
					return true;
			}
			
			/////////////////////////////////////
				return false;
		}
}



/*

2,2
can go to 1,1  1,2  1,3  2,1  2,2  2,3  3,1  3,2  3,3
          -1-1 -1-0 -1+1 -0-1 -0-0 -0+1 +1-1 +1-0 +1+1
		  
		  