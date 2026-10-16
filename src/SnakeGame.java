import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    private class Tile {
        int x;
        int y;

         Tile(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;

    ArrayList<Tile> snakeBody;

    Tile food;

    int velocityX;
    int velocityY;

    Random random;

    Timer gameLoop;

    boolean gameOver;

    SnakeGame(int boardWidth, int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        food = new Tile(10, 10);
        random = new Random();

        placeFood();

        velocityX = 0;
        velocityY = 1;

        gameLoop = new Timer(150, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        // Grid
        g.setColor(Color.DARK_GRAY);
        for(int i = 0; i < boardWidth/tileSize; i++){
            // (x1, y1, x2, y2)
            g.drawLine(i*tileSize , 0, i*tileSize, boardHeight);
            g.drawLine(0, i*tileSize , boardWidth, i*tileSize);
        }

        // Food
        g.setColor(Color.red);
        g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        // Snake Head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Snake Body
        for(Tile tile : snakeBody ){
            g.fill3DRect(tile.x * tileSize, tile.y * tileSize, tileSize, tileSize, true);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if(gameOver){
            g.setColor(Color.red);
            g.drawString("Game Over: "+ String.valueOf(snakeBody.size()), tileSize , tileSize);
        }else{
            g.setColor(Color.green);
            g.drawString("Score: "+ String.valueOf(snakeBody.size()), tileSize , tileSize);
        }
    }

    public void placeFood(){
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public void move() {
        if(collision(snakeHead, food)){
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Snake Body
        for(int i = snakeBody.size() - 1;  i >= 0; i--){
            Tile snakePart = snakeBody.get(i);
            if(i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }else{
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        isGameOver();
    }

    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void isGameOver(){
        if(snakeHead.x <= 0 || snakeHead.x > (boardWidth / tileSize) || snakeHead.y <= 0 || snakeHead.y > (boardWidth / tileSize) ){
            gameOver = true;
        }
        for(Tile bodyTile : snakeBody){
            if(collision(snakeHead, bodyTile)){
                gameOver =  true;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (key == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1; // Fixed typo (was -1)
        } else if (key == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (key == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
    }


    // No need
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
      move();
      repaint();
      if(gameOver) gameLoop.stop();
    }

}
