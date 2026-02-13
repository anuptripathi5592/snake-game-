import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JFrame {
    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}

class GamePanel extends JPanel {
    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int GRID_WIDTH = WIDTH / TILE_SIZE;
    private static final int GRID_HEIGHT = HEIGHT / TILE_SIZE;
    private static final int GAME_SPEED = 100;

    private LinkedList<Point> snake;
    private Point food;
    private int score = 0;
    private boolean gameOver = false;
    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private Random random = new Random();
    private Timer gameTimer;

    enum Direction {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        int dx, dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Initialize snake
        snake = new LinkedList<>();
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        snake.add(new Point(GRID_WIDTH / 2 - 1, GRID_HEIGHT / 2));
        snake.add(new Point(GRID_WIDTH / 2 - 2, GRID_HEIGHT / 2));

        // Generate first food
        generateFood();

        // Add keyboard listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        // Start game loop
        gameTimer = new Timer(GAME_SPEED, e -> update());
        gameTimer.start();
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                if (direction != Direction.DOWN) {
                    nextDirection = Direction.UP;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != Direction.UP) {
                    nextDirection = Direction.DOWN;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != Direction.RIGHT) {
                    nextDirection = Direction.LEFT;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != Direction.LEFT) {
                    nextDirection = Direction.RIGHT;
                }
                break;
            case KeyEvent.VK_SPACE:
                if (gameOver) {
                    resetGame();
                }
                break;
        }
    }

    private void update() {
        if (gameOver) {
            return;
        }

        direction = nextDirection;

        // Calculate new head position
        Point head = snake.getFirst();
        int newX = (head.x + direction.dx + GRID_WIDTH) % GRID_WIDTH;
        int newY = (head.y + direction.dy + GRID_HEIGHT) % GRID_HEIGHT;
        Point newHead = new Point(newX, newY);

        // Check self collision
        if (snake.contains(newHead)) {
            gameOver = true;
            return;
        }

        snake.addFirst(newHead);

        // Check food collision
        if (newHead.equals(food)) {
            score += 10;
            generateFood();
        } else {
            snake.removeLast();
        }

        repaint();
    }

    private void generateFood() {
        Point newFood;
        do {
            int x = random.nextInt(GRID_WIDTH);
            int y = random.nextInt(GRID_HEIGHT);
            newFood = new Point(x, y);
        } while (snake.contains(newFood));

        food = newFood;
    }

    private void resetGame() {
        snake.clear();
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        snake.add(new Point(GRID_WIDTH / 2 - 1, GRID_HEIGHT / 2));
        snake.add(new Point(GRID_WIDTH / 2 - 2, GRID_HEIGHT / 2));
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        score = 0;
        gameOver = false;
        generateFood();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw snake
        g2d.setColor(Color.GREEN);
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == 0) {
                g2d.setColor(new Color(144, 238, 144));
            } else {
                g2d.setColor(Color.GREEN);
            }
            g2d.fillRect(p.x * TILE_SIZE + 1, p.y * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2);
        }

        // Draw food
        g2d.setColor(Color.RED);
        g2d.fillRect(food.x * TILE_SIZE + 1, food.y * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2);

        // Draw score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 10, 20);

        // Draw game over message
        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            String gameOverText = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (WIDTH - fm.stringWidth(gameOverText)) / 2;
            g2d.drawString(gameOverText, x, HEIGHT / 2 - 50);

            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            String finalScoreText = "Final Score: " + score;
            fm = g2d.getFontMetrics();
            x = (WIDTH - fm.stringWidth(finalScoreText)) / 2;
            g2d.drawString(finalScoreText, x, HEIGHT / 2 + 20);

            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            String restartText = "Press SPACE to restart";
            fm = g2d.getFontMetrics();
            x = (WIDTH - fm.stringWidth(restartText)) / 2;
            g2d.drawString(restartText, x, HEIGHT / 2 + 80);
        }
    }
}
