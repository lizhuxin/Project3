

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Board extends JPanel {
    // Constants
    private final int CELL_SIZE  = 15;
    private final int NUM_IMAGES = 13;

    private final int IMAGE_MINE       = 9;
    private final int IMAGE_COVER      = 10;
    private final int IMAGE_MARK       = 11;
    private final int IMAGE_WRONG_MARK = 12;

    private JLabel statusBar;

    private int totalMines = 40;
    private int remainderMines;

    private int rows, columns;

    private Cell[][] cells;
    private Image[] img;

    private boolean inGame;

    public Board(JLabel statusBar) {
    	
    	DimensionSelectorPanel j = new DimensionSelectorPanel();
    	int dimensions = j.getDimensions();
    	
    	rows = dimensions;
    	columns = dimensions;
    	
        this.statusBar = statusBar;

        this.img = new Image[NUM_IMAGES];
        for (int i = 0; i < NUM_IMAGES; i++) {
            String path = "bin/img/j" + i + ".gif";
            img[i] = new ImageIcon(path).getImage();
        }

        this.setDoubleBuffered(true);
        this.addMouseListener(new MinesAdapter());
        this.newGame();
    }

    private void initCells () {
        this.cells = new Cell[rows][columns];

        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.columns; ++j) {
                this.cells[i][j] = new Cell();
            }
        }
    }

    public void newGame () {
        Random random;

        random = new Random();

        this.inGame = true;
        this.remainderMines = totalMines;

        this.initCells();
        this.statusBar.setText(Integer.toString(this.remainderMines));

        int remainder = totalMines;
        while (remainder >= 0) {
            int randX = random.nextInt(this.rows);
            int randY = random.nextInt(this.columns);

            Cell cell = this.cells[randX][randY];
            if (!cell.isMine()) {
                cell.setMine(true);
                remainder--;
            }
        }

        this.setMineCounts();
    }

    private void setMineCounts() {

        for (int i = 0; i < this.columns; ++i) {
            for (int j = 0; j < this.rows; ++j) {
                Cell cell = this.cells[i][j];

                if (!cell.isMine()) {
                    int count = countMinesAround(i, j);
                    cell.setAroundMines(count);
                }
            }
        }
    }

    /***
     * 
     * @param x x coordinate in the grid
     * @param y y coordinate in the grid
     * @return number of mines around this position
     * 
     * You should explore nearby cells and return an accurate count of
     * nearby mines.
     */
    private int countMinesAround(int x, int y) { //check 8 cells around, check for out of bounds
        int count = 0;
        
        if(inRange(x+1, y+1)) {
        	if(cells[x+1][y+1].isMine()) {
        		count++;
        	}
        }
        if(inRange(x+1, y)) {
        	if(cells[x+1][y].isMine()) {
        		count++;
        	}
        }
        if(inRange(x+1, y-1)) {
        	if(cells[x+1][y-1].isMine()) {
        		count++;
        	}
        }
        if(inRange(x, y+1)) {
        	if(cells[x][y+1].isMine()) {
        		count++;
        	}
        }
        if(inRange(x, y-1)) {
        	if(cells[x][y-1].isMine()) {
        		count++;
        	}
        }
        if(inRange(x-1, y + 1)) {
        	if(cells[x-1][y+1].isMine()) {
        		count++;
        	}
        }
        
        if(inRange(x-1, y)) {
        	if(cells[x-1][y].isMine()) {
        		count++;
     
        	}
        }
        
        if(inRange(x-1, y-1)) {
        	if(cells[x-1][y-1].isMine()) {
        		count++;
        	}
        }
        
        return count;
    }
    
    public boolean inRange(int xCoord, int yCoord) {
    	if(xCoord < 0 || xCoord > columns - 1 || yCoord < 0 || yCoord > rows - 1) {
    		return false;
    	} else {
    		return true;
    	}
    }

    public void paint(Graphics g) {
        int coveredCells = 0;

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                Cell cell = this.cells[i][j];
                int imageType;
                int xPosition, yPosition;

                if (cell.isCovered()) {
                    coveredCells++;
                }

                if (inGame) {
                    if (cell.isMine() && !cell.isCovered()) {
                        inGame = false;
                    }
                }

                imageType = this.decideImageType(cell);

                xPosition = (j * CELL_SIZE);
                yPosition = (i * CELL_SIZE);

                g.drawImage(img[imageType], xPosition, yPosition, this);
            }
        }

        if (coveredCells == 0 && inGame) {
            inGame = false;
            statusBar.setText("Game Won");
        } else if (!inGame) {
            statusBar.setText("Game Lost");
        }
    }

    /***
     * 
     * @param cell
     * @return IMAGE_MINE, IMAGE_COVER, IMAGE_MARK, or IMAGE_WRONG_MARK (integer).
     * You should make decision on which image type to display while the game is in
     * active (see the "inGame" variable), and if we are not in a game, you should
     * uncover the cell and reveal its contents based on whether the cell is a mine,
     * is marked, or is marked incorrectly.
     */
    private int decideImageType(Cell cell) {
        if (inGame == false) {
        	if (cell.isMine() == true) {
        		return IMAGE_MINE;
        	} else if (cell.isMarked() == true) {
        		return IMAGE_WRONG_MARK;
        	} else if (cell.isCovered() == false){
        		return cell.getAroundMines();
        	} else {
        		return IMAGE_COVER;
        	}
        } else {
        if (cell.isCovered() == true) {
        	if (cell.isMarked() == true) {
        		return IMAGE_MARK;
        	} else {
        	return IMAGE_COVER;
        	}
        } else if (cell.isMine() == true) {
        	return IMAGE_MINE;
        } else {
        	return cell.getValue();
       	}
       }
    }

    public void findEmptyCells(int x, int y, int depth) {

        for (int i = -1; i <= 1; ++i) {
            int xIndex = x + i;

            if (xIndex < 0 || xIndex >= this.rows) {
                continue;
            }

            for (int j = -1; j <= 1; ++j) {
                int yIndex = y + j;

                if (yIndex < 0 || yIndex >= this.columns) {
                    continue;
                }

                if (!(i == 0 || j == 0)) {
                    continue;
                }

                Cell cell = this.cells[xIndex][yIndex];
                if (checkEmpty(cell)) {
                    cell.uncover();
                    cell.checked();

                    uncoverAroundCell(xIndex, yIndex);
                    findEmptyCells(xIndex, yIndex, depth + 1);
                }
            }
        }

        if (depth == 0) {
            this.clearAllCells();
        }
    }

    /***
     * 
     * @param x x coordinate in the grid
     * @param y y coordinate in the grid
     * 
     * This method is used to uncover nearby non-empty cells.
     */
    private void uncoverAroundCell(int x, int y) {
    int y2 = y;
    int bound = 0;
    int bound2 = rows;
    while (y2>0 && (bound2-bound)>1) {
    	int count = 0;
    	for (int left = x;left>bound;left--) {
    		cells[left][y2].uncover();
    		if (cells[left][y2].getAroundMines() > 0) {
    			bound = left;
    			break;
    		}
    	}
    	for (int right = x;right<bound2;right++) {
    		cells[right][y2].uncover();
    		if (cells[right][y2].getAroundMines() > 0) {
    			bound2 = right;
    			break;
    		} 
    	}
    	y2--;
    }
    y2 = y;
    bound = 0;
    bound2 = rows;
    while (y2 < columns && (bound2-bound)>1) {
    	int count = 0;
    	for (int left = x;left>bound;left--) {
    		cells[left][y2].uncover();
    		if (cells[left][y2].getAroundMines() > 0) {
    			bound = left;
    			break;
    		}
    	}
    	for (int right = x;right<bound2;right++) {
    		cells[right][y2].uncover();
    		if (cells[right][y2].getAroundMines() > 0) {
    			bound2 = right;
    			break;
    		} 
    	}
    	y2++;
    }
    }

    private boolean checkEmpty(Cell cell) {
        if (!cell.isChecked()) {
            if (cell.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private void clearAllCells() {
        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < this.columns; ++j) {
                this.cells[i][j].clearChecked();
            }
        }
    }

    class MinesAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int pressedCol = e.getX() / CELL_SIZE;
            int pressedRow = e.getY() / CELL_SIZE;

            boolean doRepaint = false;
            Cell pressedCell;

            if (!inGame) {
                newGame();
                repaint();
            }

            if ((pressedCol < 0 || pressedCol >= columns)
                || (pressedRow < 0 || pressedRow >= rows)) {
                return;
            }

            pressedCell = cells[pressedRow][pressedCol];

            if (e.getButton() == MouseEvent.BUTTON3) {
                doRepaint = true;

                if (!pressedCell.isCovered()) {
                    return;
                }

                String str;
                if (!pressedCell.isMarked()) {
                    pressedCell.setMark(true);
                    remainderMines--;
                } else {
                    pressedCell.setMark(false);
                    remainderMines++;
                }

                statusBar.setText(Integer.toString(remainderMines));
            } else {
                if (pressedCell.isMarked() || !pressedCell.isCovered()) {
                    return;
                }

                doRepaint = true;

                pressedCell.uncover();
                if (pressedCell.isMine()) {
                    inGame = false;
                } else if (pressedCell.isEmpty()) {
                    findEmptyCells(pressedRow, pressedCol, 0);
                }
            }

            if (doRepaint) {
                repaint();
            }
        }
    }
}
