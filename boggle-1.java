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
      final int[] indices;
      List<Cell> neighbours;
      
      Cell(char letter, int[] indices) {
        this.letter = letter;
        this.indices = indices;
      }
      
      public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Cell obj = (Cell) other;
        return this.letter == obj.letter && Arrays.equals(this.indices, obj.indices);
      }
    }
    
    private final Map<Character, List<Cell>> cells = new HashMap<>();
    private final Cell[][] board;
    private final int[] size;
    private final String word;
    
    public Boggle(final char[][] board, final String word) {
        final long start = System.nanoTime();
        this.word = word;
        this.size = new int[]{board.length, board.length > 0 ? board[0].length: 0};
        this.board = new Cell[this.size[0]][this.size[1]];
        for (AtomicInteger i=new AtomicInteger(0); i.get()<this.size[0]; i.getAndIncrement()) {
          for (AtomicInteger j=new AtomicInteger(0); j.get()<this.size[1]; j.getAndIncrement()) {
            this.board[i.get()][j.get()] = new Cell(board[i.get()][j.get()], new int[]{i.get(), j.get()});
            this.board[i.get()][j.get()].neighbours = getNeighbours(i.get(), j.get(), board);
            cells.merge(board[i.get()][j.get()], Arrays.asList(this.board[i.get()][j.get()]), (cells, newCells) -> {
              List<Cell> result = new ArrayList<>(cells);
              result.addAll(newCells);
              return result;
            });
          }
        }
        System.out.print((System.nanoTime()-start)/1e9);
    }
    
    private List<Cell> getNeighbours(int i, int j, char[][] board) {
      final List<Cell> neighbours = new ArrayList<>();
      for (int i0=(i-1 <= 0 ? 0 : i-1); i0<=(i+1>=this.size[0]-1 ? this.size[0] - 1 : i+1); i0++) {
        for (int j0=j-1; j0<=j+1; j0++) {
          if (j0<0 || j0>this.size[1]-1 || (i0==i && j0==j)) continue;
          neighbours.add(new Cell(board[i0][j0], new int[]{i0, j0}));
        }
      }
      return neighbours;
    }
    
    private List<Cell> findNeighbours(final char letterToFind, final int[] indices) {
      final int i = indices[0], j = indices[1];
      if (!this.cells.containsKey(letterToFind) || i<0 || j<0) return Collections.emptyList();
      return this.board[i][j].neighbours.stream()
        .filter(neighbour -> neighbour.letter == letterToFind)
        .collect(Collectors.toList());
    }
    
    public boolean check() {
        final long start = System.nanoTime();
        final char[] letters = this.word.toCharArray();
        List<List<Cell>> chains = new ArrayList<>();
        System.out.print(" "+word+" "+size[0]+" ");
        for (int i=0; i<letters.length; i++) {
          final char letterToFind = letters[i];
          if (i == 0) {
            final List<Cell> lettersCells = this.cells.get(letterToFind);
            if (lettersCells == null || lettersCells.isEmpty()) return false;
            chains = lettersCells.parallelStream()
                .map(cell -> Arrays.asList(new Cell(letterToFind, cell.indices)))
                .collect(Collectors.toList());
          } else {
            final char previousLetter = letters[i-1];
            chains = chains.parallelStream()
              .map(chain -> {
                if (chain.size()==0) return Collections.<List<Cell>>emptyList();
                final Cell lastCell = chain.get(chain.size()-1);
                if (lastCell.letter != previousLetter) return Collections.<List<Cell>>emptyList();
                final List<Cell> neighbours = findNeighbours(letterToFind, lastCell.indices);
                if (neighbours.isEmpty()) return Collections.<List<Cell>>emptyList();
                return neighbours.parallelStream()
                  .filter(neighbour -> !chain.contains(neighbour))
                  .map(neighbour -> {
                    List<Cell> newChain = new ArrayList<>(chain);
                    newChain.add(neighbour);
                    return newChain;
                  })
                  .collect(Collectors.toList());
              })
              .flatMap(List::parallelStream)
              .collect(Collectors.toList()); 
          }
          if (chains.size() == 0) return false;
        }
        System.out.println((System.nanoTime()-start)/1e9);
        return chains.size() > 0;
    }
}