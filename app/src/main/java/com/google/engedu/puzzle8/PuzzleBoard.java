package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;

    int stepNumber;

    private PuzzleBoard previousBoard;

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

    public void setPreviousBoard(PuzzleBoard previousBoard) {
        this.previousBoard = previousBoard;
    }

    PuzzleBoard(Bitmap bitmap, int parentWidth) {

        stepNumber = 0;

        tiles = new ArrayList<>();

        int tileWidth = parentWidth / NUM_TILES;

        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);

        for (int y = 0 ; y<NUM_TILES; y++){
            for (int x = 0 ; x<NUM_TILES ; x++){
                int tileNumber = y*NUM_TILES + x;

                if (tileNumber != NUM_TILES*NUM_TILES -1){

                    Bitmap tileBitmap = Bitmap.createBitmap(
                            scaleBitmap,
                            x*tileWidth,
                            y*tileWidth,
                            tileWidth,
                            tileWidth
                    );

                    PuzzleTile currentTile = new PuzzleTile(tileBitmap,tileNumber);
                    tiles.add(currentTile);

                }else {
                    tiles.add(null);
                }
            }
        }

    }

    PuzzleBoard(PuzzleBoard otherBoard, int stepNumber) {
        previousBoard = otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        this.stepNumber = stepNumber + 1;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {

        ArrayList<PuzzleBoard>neighbourBoards  = new ArrayList<>();

        int emptyTileX = 0;
        int emptyTileY = 0;

        for (int i = 0 ; i<NUM_TILES*NUM_TILES ; i++){
            if (tiles.get(i) == null){
                emptyTileX = i % NUM_TILES;
                emptyTileY = i / NUM_TILES;
                break;
            }
        }

        for (int[] cordinates : NEIGHBOUR_COORDS) {
            int neighbourX = emptyTileX + cordinates[0];
            int neighbourY = emptyTileY + cordinates[1];

            if (neighbourX >= 0 && neighbourX < NUM_TILES && neighbourY >= 0 && neighbourY < NUM_TILES) {

                PuzzleBoard currentBoard = new PuzzleBoard(this, stepNumber);

                currentBoard.swapTiles(
                        XYtoIndex(neighbourX, neighbourY),
                        XYtoIndex(emptyTileX, emptyTileY)
                );
                neighbourBoards.add(currentBoard);
            }
        }
        return neighbourBoards;
    }

    public int priority() {

        int manhattanDistance = 0;

        for (int i = 0 ; i<NUM_TILES*NUM_TILES ; i++){
            PuzzleTile currentTile = tiles.get(i);
            if (currentTile != null){
                int correctPosition = currentTile.getNumber();

                int correctX = correctPosition % NUM_TILES;
                int correctY = correctPosition / NUM_TILES;

                int currentX = i % NUM_TILES;
                int currentY = i / NUM_TILES;

                manhattanDistance = manhattanDistance
                        + Math.abs(currentX - correctX)
                        + Math.abs(currentY - correctY);
            }

        }


        return manhattanDistance + stepNumber;
    }

}
