package a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import a2.FourInLine.Column;
import a2.FourInLine.GameState;

import static a2.FourInLine.*;
import static java.util.stream.Collectors.toList;

public class GameTree {
    static class Tree {
         GameState game;
         List<Move> moves;
         public Tree(GameState g, List<Move> m) {
             game = g;
             moves = m;
         }
    }
    static class Move {
        ColumnNum move;
        Tree tree;
        public Move(ColumnNum m, Tree t) {
            move = m;
            tree = t;
        }
    }

    static Move subGameTree(GameState game, ColumnNum c, Player player, int depth) {
    	//deep copy.not alter original game state
    	List<Column> copy = game.stream()
    					.map(col -> new Column(col.stream().map(p -> new Piece(p.toString()) {}).collect(Collectors.toList())))
    					.collect(Collectors.toList());
    	GameState gs = new GameState();
		gs.addAll(copy);
        return new Move(c, gameTree(otherPlayer(player),depth - 1, dropPiece(gs, c, pieceOf(player))));
    }

    // Recursively build the game tree using allViableColumns to get all possible
    // moves (introduce depth as the function is not lazy).  Note that the tree bottoms out once the game is won

    static Tree gameTree(Player player, int depth, GameState game)  {

        Optional<Player> w = winner(game);
        if (w.isPresent()) {
      

            return new Tree(game, new ArrayList<>());
        } else if (depth == 0) {
        	
            return new Tree(game, new ArrayList<>());
        } else {
            List<Move> moves = allViableColumns(game).stream().map(n -> {
                return subGameTree(game, n, player, depth);
            }).collect(toList());
          

            return new Tree(game, moves);

        }
      
    }

    //Estimate the value of a position for a player. This implementation only
    //assigns scores based on whether or not the player has won the game.  This is
    //the simplest possible way of doing it, but it results in an
    //overly-pessimistic AI.
    //
    //The "cleverness" of the AI is determined by the sophistication of the
    //estimate function.
    //Some ideas for making the AI smarter:
    //1) A win on the next turn should be worth more than a win multiple turns
    //later.  Conversely, a loss on the next turn is worse than a loss several
    //turns later.
    //2) Some columns have more strategic value than others.  For example, placing
    //pieces in the centre columns gives you more options.
    //3) It's a good idea to clump your pieces together so there are more ways you
    //could make four in a line.

    static int estimate(Player player, GameState game) {
         if (fourInALine(pieceOf(player), game))
            return 100;
         else if (fourInALine(pieceOf(otherPlayer(player)), game))
            return -100;
       
        
        // 3 pieces in line
//         else if (threeInALine(pieceOf(otherPlayer(player)), game))
//            return -90;
//        else if (threeInALine(pieceOf(player), game))
//           return 90;
////     
////        
////      
////        
//        //placing pieces in the center columns 
//        else if (centerCol(pieceOf(player), game)){
//        	return 50;
//        }
//        //opponent; placing pieces in the center columns 
//        else if (centerCol(pieceOf(otherPlayer(player)), game)){
//        	return -50;
//        }
        
        else
            return 0;
    }

    
    
    
    
    private static boolean threeInALine(Piece piece, GameState game) {
    	//column.
    	// no opponent on its top
    	List<Piece> piecesThree = IntStream.range(0, 3).mapToObj(i -> piece).collect(toList());
    	boolean col =  game.stream()
    			.filter(n -> n.size()>=3)
    			.map(c -> c.stream().limit(3).collect(toList()))
    			.anyMatch(i -> i.stream() == piecesThree.stream());
    	System.out.println(col);	
    					
    			
    		
    		
//    			piece: [r, r, B]
//    		    .forEach(i -> System.out.println("piece: "+i.limit(3).collect(toList())));
    	
    	//row 
    	// fill blank, transpose, check column
    	boolean row = false;
    	
    	
    	
    	//diagonal
    	boolean diagonal =false;
    	if (col == true ||row == true || diagonal == true) {
    		return true;
    	}
		
		return false;
	}

	//piece in the center column and no other on its top
    private static boolean centerCol(Piece piece, GameState game) {
		if (game.get(NColumns/2).get(0).toString() == piece.toString()) {
			return true;
		};
		return false;
	}

	static ColumnNum maxmini(Player player, Tree tree)  {
		
        if (tree.moves.isEmpty())
            throw new RuntimeException("The AI was asked to make a move, but there are no moves possible.  This cannot happen");
        else {

            return  tree.moves.stream()
                    .collect(Collectors.maxBy((Move a, Move b) -> {
                        return minimaxP(player, a.tree) - minimaxP(player, b.tree);
                    })).get().move;
        }

    }


    // Maximise the minimum utility of player making a move.  Do this when it is the
    // player's turn to find the least-bad move, assuming the opponent will play
    // perfectly.

    static int maxminiP(Player player, Tree tree) {
    	
        if (tree.moves.isEmpty()) {	
            return estimate(player, tree.game);
        }else {
            return Collections.max(tree.moves.stream().map(m -> minimaxP(player, m.tree)).collect(toList()));
        }
    }

    // Minimise the maximum utility of player making a move.  Do this when it is the
    // opponent's turn, to simulate the opponent choosing the move that results in
    // the least utility for the player.

    static int minimaxP(Player player, Tree tree) {
        if (tree.moves.isEmpty())
            return estimate(player, tree.game);
        else {
            return Collections.min(tree.moves.stream().map(m -> maxminiP(player, m.tree)).collect(toList()));
        }
    }

    // Determine the best move for computer player

    public static Function<GameState, ColumnNum> aiMove(int lookahead, Player player) {
        return x -> {
        	
            return maxmini(player,gameTree(player, lookahead, x));
        };
    }

}
