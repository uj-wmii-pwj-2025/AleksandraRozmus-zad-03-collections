package uj.wmii.pwj.collections;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public interface BattleshipGenerator {
    String generateMap();

    static BattleshipGenerator defaultInstance() {
        return new BattleshipGeneratorImpl();
    }
}

class BattleshipGeneratorImpl implements BattleshipGenerator {
    final int ROWS = 10;
    final int COLUMNS = 10;
    final int MAX_ATTEMPTS = 1000;
    final int[] SHIP_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    char[][] board = new char[ROWS][COLUMNS];

    @Override
    public String generateMap() {
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLUMNS; j++)
                board[i][j] = '.';

        int attempts;
        Boolean placed;
        
        for (int i = 0; i < SHIP_SIZES.length; i++) {
            attempts = 0;
            placed = false;

            while (attempts < MAX_ATTEMPTS) {
                if (addShip(SHIP_SIZES[i])) {
                    placed = true;
                    break;
                }
                attempts++;
            }
            if (!placed)
                throw new RuntimeException("Map generation error");
        }
        return convertToString(board);
    }

    Boolean addShip(int size) {
        Random random = new Random();
        int x, y;
        List<int[]> tempCoordinates = new ArrayList<>();

        while (true) {
            x = random.nextInt(ROWS);
            y = random.nextInt(COLUMNS);

            if (isFieldValid(x, y)) {
                tempCoordinates.add(new int[]{x, y});
                break;
            }
        }

        int[] newCoordinate;

        for (int i = 1; i < size; i++) {
            newCoordinate = getNewCoords(tempCoordinates, random);
            if (newCoordinate == null) 
                return false;
            tempCoordinates.add(newCoordinate);
        }

        for (int i = 0; i < tempCoordinates.size(); i++) {
            int[] field = tempCoordinates.get(i);
            board[field[0]][field[1]] = '#';
        }
        return true;
    }

    int[] getNewCoords(List<int[]> temp, Random random) {
        List<int[]> directions = new ArrayList<>(List.of(
        new int[]{-1, 0}, new int[]{1, 0}, new int[]{0, -1}, new int[]{0, 1}));

        java.util.Collections.shuffle(temp, random);
        java.util.Collections.shuffle(directions, random);

        int newX, newY;

        for (int i = 0; i < temp.size(); i++) {
            int[] start = temp.get(i);
            for (int j = 0; j < 4; j++) {
                int[] move = directions.get(j);
                newX = start[0] + move[0];
                newY = start[1] + move[1];

                if (newX >= 0 && newX < ROWS && newY >= 0 && newY < COLUMNS) {
                    if (isFieldValid(newX, newY) && !isAlreadyInTemp(temp, newX, newY))
                        return new int[]{newX, newY};
                }
            }
        }
        return null;
    }

    boolean isAlreadyInTemp(List<int[]> temp, int x, int y) {
        for (int i = 0; i < temp.size(); i++) {
            int[] seg = temp.get(i);
            if (seg[0] == x && seg[1] == y)
                return true;
        }
        return false;
    }

    Boolean isFieldValid(int x, int y) {
        if (board[x][y] == '#' || hasNeighbor(x,y) == true)
            return false;
        else
            return true;
    }

    Boolean hasNeighbor(int x, int y) {
        int newX, newY;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                newX = x + dx;
                newY = y + dy;
                if (newX >= 0 && newX < ROWS && newY >= 0 && newY < COLUMNS) {
                    if (board[newX][newY] == '#')
                        return true;
                }
            }
        }
        return false;
    }

    String convertToString (char[][] board){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLUMNS; j++) 
                sb.append(board[i][j]);
    
        return sb.toString();
    }
}
