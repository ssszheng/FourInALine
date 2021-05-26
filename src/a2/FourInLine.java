package a2;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import a2.FourInLine.Column;

import static a2.FourInLine.showGameState;
import static java.util.stream.Collectors.*;

public class FourInLine {

	// Declare some constants

	static int NColumns = 7;
	static int NRows = 6;

	// A player is either the red player, or the blue player

	public static Player redPlayer = Player.redPlayer;
	public static Player bluePlayer = Player.bluePlayer;

	// A piece is either a red piece or a blue piece

	public static Piece redPiece = Piece.redPiece;
	public static Piece bluePiece = Piece.bluePiece;

	// A column is a list of Pieces. The first element of the list represents the
	// top of
	// the column, e.g.
	// row 6 --
	// row 5 --
	// row 4 -- RedPiece <- first element of the list
	// row 3 -- RedPiece
	// row 2 -- BluePiece
	// row 1 -- RedPiece <- last element in the list
	// The list for this column would be [redPiece, redPiece, bluePiece, redPiece]
	// Now, to add a piece to the TOP of a column, just create a new column
	// with that piece and append the rest of the old column to it

	// a Column is a list of pieces

	public static class Column extends ArrayList<Piece> {
		public Column() {
		}

		public Column(List<Piece> l) {
			this.addAll(l);
		}
	}

	// The GameState is a list of Columns

	public static class GameState extends ArrayList<Column> {
		public GameState() {
		}

		public GameState(List<List<Piece>> g) {
			List<Column> c = g.stream().map(Column::new).collect(toList());
			this.addAll(c);
		}
		
	}

	// ColumnNums are 1-based, but list indices are 0-based.
	//indexOfColumn converts a ColumnNum to a list index.

	public static class ColumnNum {
		int index;

		public ColumnNum(int index) {
			GameState s;
			this.index = index;
		}

		public int indexOfColumn() {
			return index - 1;
		}

		public String toString() {
			return "" + index;
		}
	}

	//
	// Convert a column to a string of the form "rBrrBB", or " rrB". The string
	// must have length 6. If the column is not full, then the list should be
	// prefixed with an appropriate number of spaces
	//

	public static String showColumn(Column xs) {
		String s = Stream.concat(IntStream.range(0, NRows-xs.size()).mapToObj(i -> " "), 
				xs.stream().map(Piece::toString)).collect(joining());
		return s; 
	}

	//
	// Convert a GameState value to a string of the form:
	// " r \n
	// r r B r\n
	// B B r B r B\n
	// r B r r B r r\n
	// r B B r B B r\n
	// r B r r B r B"
	// Useful functions:
	// showColumn
	// (which you already defined)
	// and transposes a list of lists using streams,
	// so List(List(1,2,3), List(4,5,6)) becomes List(List(1,4), List(2,5),
	// List(3,6))

	public static String showGameState(GameState xs) {
	     // add each column with space,  
		 
         List<char[]> colWithSpaces = xs.stream().map(c -> showColumn(c).toCharArray()).collect(Collectors.toList());
         
         
		 List<List<Character>> transposedCol = IntStream.range(0, NRows)
		         .mapToObj(i -> colWithSpaces.stream().map(l -> l[i]).collect(Collectors.toList()))
		         .collect(Collectors.toList());
 
        // convert list of list to string, add space between characters, 
		 //remove the last space in each line to match the test sample
		 String s = transposedCol.stream()
				 .map(row -> {
					 String rowStr = row.stream().map(e -> e.toString()).reduce("", (a,b) -> a+b+" ");
				     return rowStr.substring(0, rowStr.length() - 1);
				 }).collect(Collectors.joining("\n"));
//         System.out.println(s);
		 return s;
         }

	// Which pieces belong to which players?

	public static Piece pieceOf(Player player) {

		if (player.toString() == "Red Player") {
			return redPiece;
		} else {
			return bluePiece;
		} // replace this with implementation

	}

	// Given a player, who is the opposing player?

	public static Player otherPlayer(Player player) {
		if (player.toString() == "Red Player") {
			return bluePlayer;
		} else {
			return redPlayer;
		} 

	}

	// Given a piece, what is the colour of the other player's pieces?

	public static Piece otherPiece(Piece piece) {
		if (piece.toString() == "r") {
			return bluePiece;
		} else {
			return redPiece;
		} // replace this with implementation

	}

	// The initial GameState, all columns are empty. Make sure to create the proper
	// number of columns

	public static GameState initGameState() {
//		Piece emptyPiece = new Piece("") {};
		
//		List<Piece> emptyCol = IntStream.range(0,NRows)
//				.mapToObj(m -> new Piece("") {}).collect(Collectors.toList());
		
		List<List<Piece>> columnList = IntStream.range(0,NColumns)
				.mapToObj(n -> new Column())
				.collect(Collectors.toList());

		
		GameState gs = new GameState(columnList); 
        return gs;
        
	}

	// Check if a column number is valid (i.e. in range)

	public static boolean isValidColumn(ColumnNum c) {
		
		Predicate<ColumnNum> pr = a -> (0 < a.index && a.index <= NColumns); // Creating predicate  
        return pr.test(c);   // Calling Predicate method 
		
	}

	// Check if a column is full (a column can hold at most nRows of pieces)

	public static boolean isColumnFull(Column column) {
		
		Predicate<Column> pr = a -> (a.size() == NRows); 
		
        return pr.test(column);  
	}

	// Return a list of all the columns which are not full (used by the AI)

	public static List<ColumnNum> allViableColumns(GameState game) {
		List<ColumnNum> cn = IntStream.range(0, game.size())
				.filter(i -> !isColumnFull(game.get(i)))
				.mapToObj(m -> new ColumnNum(m+1))
				.collect(Collectors.toList());
		return cn;

	}

	// Check if the player is able to drop a piece into a column

	public static boolean canDropPiece(GameState game, ColumnNum columnN) {
		List<ColumnNum> cn = allViableColumns(game);
		boolean result = cn.stream().anyMatch(t -> t.indexOfColumn() == columnN.indexOfColumn());
		return result;
	}

	// Drop a piece into a numbered column, resulting in a new gamestate
	// always add a piece to the TOP of a column(first of the list)
	public static GameState dropPiece(GameState game, ColumnNum columnN, Piece piece) {
		if (canDropPiece(game,columnN)){
			game.get(columnN.indexOfColumn()).add(0, piece);	
			
		}
		return game;
	}

	// Are there four pieces of the same colour in a column?

	static boolean fourInCol(Piece piece, Column col) {
//		List<Piece> pieces4List= IntStream.range(0, 4).mapToObj(i -> piece).collect(Collectors.toList());
//		if (Collections.indexOfSubList(col, pieces4List) != -1) { 
//		return true;
//		}
	
		List<Integer> idxList= IntStream.range(0,col.size())
				                    .filter(i-> col.get(i).toString().equals(piece.toString())).boxed()
				                    .collect(Collectors.toCollection(ArrayList::new));
		
		if (idxList.stream().count() <4) {
			return false;
		}
	   //check if in consecutive order
		return IntStream.range(0,idxList.size()-1).allMatch(i -> idxList.get(i)+1 == idxList.get(i+1));
		
	}
	
	public static boolean fourInColumn(Piece piece, GameState game) {
		return game.stream().filter(col -> fourInCol(piece, col)).count() > 0;
	}

	// transposes gameboard, assumes all columns are full
	static GameState transpose(GameState g) {
		return new GameState(IntStream.range(0, g.get(0).size())
				.mapToObj(i -> g.stream().map(l -> l.get(i)).collect(toList())).collect(toList()));
	}
	// A helper function that fills up a column with pieces of a certain colour. It
	// is used to fill up the columns with pieces of the colour that
	// fourInRow/fourInDiagonal is not looking for. This will make those functions
	// easier to define.

	static Column fillBlank(Piece piece, Column column) {
		if (NRows == column.size()) {return column;}
		IntStream.range(0, NRows - column.size()).forEach(i -> column.add(0, piece));
        
		return column;
	}

	// Are there four pieces of the same colour in a row? Hint: use fillBlanks and
	// transpose to reduce the problem to fourInColumn

	public static boolean fourInRow(Piece piece, GameState game) {
		Piece blank = new Piece("") {};
		
		//deep copy.not alter original game state
		List<Column> copy = game.stream()
				.map(col -> new Column(col.stream().map(p -> new Piece(p.toString()) {}).collect(Collectors.toList())))
				.collect(Collectors.toList());
		
		List<Column> clist = copy.stream().map(c -> fillBlank(blank, c)).collect(Collectors.toList());		
		
		GameState gs = new GameState();
		gs.addAll(clist);
		GameState gt = transpose(gs);

		return fourInColumn(piece, gt);
	}

	// Another helper function for fourInDiagonal. Remove n pieces from the top of
	// a full column and add blanks (of the colour we're not looking for) to the
	// bottom to make up the difference. This makes fourDiagonal easier to define.

	static Column shift(int n, Piece piece, Column column) {
		if(n == 0) {return column;}
		IntStream.range(0, n).forEach(i-> column.remove(0));
//		Piece blank = new Piece("") {};
		IntStream.range(0, n).forEach(i-> column.add(piece));
		return column;
	}

	// Are there four pieces of the same colour diagonally? Hint: define a helper
	// function using structural recursion over the gamestate, and using shift and
	// fourInRow.

	static boolean fourDiagonalHelper(GameState g, Piece piece) {
		//base case : if remaining columns < 4 
		boolean result = false;
		if (g.size() < 4) {
			return result;
		}else {
			//deep copy
			List<Column> copy1 = g.stream()
					.map(col -> new Column(col.stream().map(p -> new Piece(p.toString()) {}).collect(Collectors.toList())))
					.collect(Collectors.toList());
			
			List<Column> copy2 =  g.stream()
					.map(col -> new Column(col.stream().map(p -> new Piece(p.toString()) {}).collect(Collectors.toList())))
					.collect(Collectors.toList());
			
		   //else: anti-diagonal- for the first four columns, first col up shifts  3, second up 2, third up 1
			List<Column> c1 = IntStream.range(0, 4).mapToObj(i -> shift(3-i, new Piece("") {}, copy1.get(i))).collect(Collectors.toList());
			
			
			GameState gs = new GameState();
			gs.addAll(c1);
			boolean case1 = fourInRow(piece, gs);
			
			//else: main diagonal -for the first four columns, first col no move, second up 1, third up 2, forth up 3
			List<Column> c2 = IntStream.range(0, 4).mapToObj(i -> shift(i, new Piece("") {}, copy2.get(i))).collect(Collectors.toList());
		
			GameState gs2 = new GameState();
			gs2.addAll(c2);
			boolean case2 = fourInRow(piece, gs2);
			
			if (case1 == false && case2 == false) {
				g.remove(0);
				result = fourDiagonalHelper(g,piece);
				
			}else if (case1 == true || case2 == true){
				result = true;
			}
			}
		return result;
		}

	public static boolean fourDiagonal(Piece piece, GameState game) {
		//make gamestate to full columns 
		Piece blank = new Piece("") {};
		//deep copy.not alter original game state
		List<Column> copy = game.stream()
				.map(col -> new Column(col.stream().map(p -> new Piece(p.toString()) {}).collect(Collectors.toList())))
				.collect(Collectors.toList());
		
		List<Column> clist = copy.stream().map(c -> fillBlank(blank, c)).collect(Collectors.toList());		
		GameState gs = new GameState();
		gs.addAll(clist);
		return fourDiagonalHelper(gs, piece);
	}

	// Are there four pieces of the same colour in a line (in any direction)

	public static boolean fourInALine(Piece piece, GameState game) {
		
	
		boolean col = fourInColumn(piece,game);
		
		boolean row = fourInRow(piece,game);
		
		boolean diagonal = fourDiagonal(piece,game);
		

		
	
		if(diagonal== true||row == true ||col ==true) {
			return true;
		}return false;
		
	}

	// Who won the game. Returns an Optional since it could be that no one has won
	// the game yet.

	public static Optional<Player> winner(GameState game) {
		Optional<Player> winner = Optional.empty();
		if (fourInALine(redPiece,game) == true) {
			winner = Optional.of(redPlayer);
		} else if (fourInALine(bluePiece,game) == true) {
			winner = Optional.of(bluePlayer);
		}
		
		
		return winner;
	}

}
