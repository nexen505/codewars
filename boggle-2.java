import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.concurrent.atomic.AtomicInteger;

public class Boggle {

    private class Cell {
      final char letter;
      final int index;
      final List<Integer> neighbours;
      
      Cell(char letter, int index, List<Integer> neighbours) {
        this.letter = letter;
        this.index = index;
        this.neighbours = neighbours;
      }
      
      public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Cell obj = (Cell) other;
        return this.letter == obj.letter && this.index == obj.index;
      }
    }
    
    private final Cell[][] board;
    private final int[] size;
    private final String word;
    
    public Boggle(final char[][] board, final String word) {
      this.word = word;
      this.size = new int[]{board.length, board.length};
      this.board = new Cell[this.size[0]][this.size[1]];
      for (int i=0; i<this.size[0]; i++) {
        for (int j=0; j<this.size[1]; j++) {
          this.board[i][j] = new Cell(board[i][j], i*this.size[0]+j, getNeighbours(i, j, board));
        }
      }  
    }
    
    private List<Integer> getNeighbours(final int row, final int column, final char[][] board) {
      final List<Integer> neighbours = new ArrayList<>();
      for (int i=(row-1 <= 0 ? 0 : row-1); i<=(row+1>=this.size[0]-1 ? this.size[0] - 1 : row+1); i++) {
        for (int j=column-1; j<=column+1; j++) {
          if (j<0 || j>this.size[1]-1 || (i==row && j==column)) continue;
          neighbours.add(i*this.size[0]+j);
        }
      } 
      return neighbours;
    }
    
    private List<Cell> checkNeighbours(final Cell point, final char letter, final List<Integer> visited) {
      return point.neighbours.stream()
        .filter(globalIndex -> {
          int row = globalIndex / this.board.length,
            index = globalIndex % this.board.length;
          return !visited.contains(globalIndex) && letter == this.board[row][index].letter;
        })
        .map(globalIndex -> {
          int row = globalIndex / this.board.length,
            index = globalIndex % this.board.length;
          return this.board[row][index];
        })
        .collect(Collectors.toList());
    }
    
    private boolean searchNext(final int index, final Cell point, final List<Integer> visited) {
      if (index<0 || index>=this.word.length()) return true;
      final List<Cell> candidates = checkNeighbours(point, this.word.charAt(index), visited);
      for (Cell candidate: candidates) {
        if (index == this.word.length() - 1) return true;
        List<Integer> v = new ArrayList<>(visited);
        v.add(candidate.index);
        if (searchNext(index+1, candidate, v)) return true;
      }
      return false;
    }
    
    public boolean check() {
       for (int i=0; i<this.size[0]; i++) {
          for (int j=0; j<this.size[1]; j++) {
            Cell point = this.board[i][j];
            if (point.letter != this.word.charAt(0)) continue;
            if (searchNext(1, point, Collections.emptyList())) return true;
          }
        }  
        return false;
    }
}