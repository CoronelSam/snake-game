import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeTG extends JFrame {
    private final int WIDTH = 700;
    private final int HEIGHT = 500;
    private final int POINT_SIZE = 10;
    private  long SPEED = 50;
    private int level = 1;
    private int speedDec = 10;
    private int animalsEaten=0;


    private ArrayList<Point> snake;
    private Point comida;
    private String direccion = "RIGHT";
    
    private Font ccOverbyteFont;

    private enum ESTADO{
        MENU,
        GAME,
        GAME_OVER
    };

    private ESTADO estado =  ESTADO.MENU;

    public SnakeTG() throws FontFormatException, IOException {
        setTitle("Snake");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        ccOverbyteFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/ccoverbyteoffregular.otf"));

        ImagenSnake imagenSnake = new ImagenSnake();
        getContentPane().add(imagenSnake);

        addKeyListener(new Teclas());
        setFocusable(true);

        Momento momento = new Momento();
        Thread trid = new Thread(momento);
        trid.start();

        estado = ESTADO.MENU;
        setVisible(true);
    }


    public void startGame() {
        estado = ESTADO.GAME;
        snake = new ArrayList<>();
        snake.add(new Point(4 * POINT_SIZE, 4 * POINT_SIZE));
        generarComida();
    }

    public void generarComida() {
        Random rnd = new Random();
        int x = (rnd.nextInt(WIDTH / POINT_SIZE)) * POINT_SIZE;
        int y = (rnd.nextInt(HEIGHT / POINT_SIZE)) * POINT_SIZE;
        comida = new Point(x, y);
    }

    public void aumentarVelocidad() {
        if (animalsEaten % 5 == 0) {
            level++;
            SPEED -= speedDec;
            System.out.println("New level: " + level);
        }
    }
    
    public void resetSettings(){
        SPEED = 50;
        level = 1;
        animalsEaten = 0;
    }

    public void actualizar() {
        Point newHead = new Point(snake.get(0).x, snake.get(0).y);

        if (direccion.equals("RIGHT")) {
            newHead.x += POINT_SIZE;
        } else if (direccion.equals("LEFT")) {
            newHead.x -= POINT_SIZE;
        } else if (direccion.equals("UP")) {
            newHead.y -= POINT_SIZE;
        } else if (direccion.equals("DOWN")) {
            newHead.y += POINT_SIZE;
        }

        if (newHead.equals(comida)) {
            snake.add(0, newHead);
            generarComida();
            animalsEaten++; // Increase the count of animals eaten
            System.out.println("Animals eaten: " + animalsEaten);
            aumentarVelocidad(); 
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }
        // Check if the snake head goes out of bounds.
        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            estado = ESTADO.GAME_OVER;
        }
        
        // Check if the snake collides with itself.
        for (int i = 1; i < snake.size(); i++) {
            if (newHead.equals(snake.get(i))) {
                estado = ESTADO.GAME_OVER;
                return;
            }
        }
        repaint();
    }

    private class ImagenSnake extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.GREEN);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            if (estado == ESTADO.MENU) {
                g.setColor(Color.blue);
                g.setFont(ccOverbyteFont.deriveFont(Font.BOLD, 80));
                g.drawString("SNAKE GAME", 115, 200);

                g.setFont(ccOverbyteFont.deriveFont(Font.PLAIN, 30));
                g.drawString("Press 'N' to Start Game", 195, 300);
                g.drawString("Press 'ESC' to Quit", 195, 350);
            } else if (estado == ESTADO.GAME) {
                g.setColor(Color.BLUE);
                for (Point point : snake) {
                    g.fillRect(point.x, point.y, POINT_SIZE, POINT_SIZE);
                }
                g.setColor(Color.RED);
                g.fillRect(comida.x, comida.y, POINT_SIZE, POINT_SIZE);

                g.setColor(Color.BLACK);
                g.setFont(ccOverbyteFont.deriveFont(Font.BOLD, 24));
                g.drawString("Puntos: " + animalsEaten, 20, 40);
                g.drawString("Nivel: " + level, 20, 70);
            } else {
                g.setColor(Color.red);
                g.setFont(ccOverbyteFont.deriveFont(Font.BOLD, 110));
                g.drawString("GAME OVER", 55, 250);
                g.setColor(Color.BLUE);
                g.setFont(ccOverbyteFont.deriveFont(Font.BOLD, 30));
                g.drawString("Press 'N' to Start Game", 195, 350);
                g.drawString("Press 'ESC' to Quit", 195, 400);
                g.setColor(Color.BLUE);
                g.setFont(ccOverbyteFont.deriveFont(Font.BOLD, 30));
                g.drawString("LEVEL REACHED " + level, 20, 45);
                g.drawString("PUNTOS: " + animalsEaten, 20, 85);
            }
        }
    }

   private class Teclas extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (estado == ESTADO.MENU) {
            if (key == KeyEvent.VK_N) {
                resetSettings();
                startGame();
            } else if (key == KeyEvent.VK_ESCAPE) {
                resetSettings();
                System.exit(0);
            }
        } else if (estado == ESTADO.GAME) {
            if (key == KeyEvent.VK_RIGHT && !direccion.equals("LEFT")) {
                direccion = "RIGHT";
            } else if (key == KeyEvent.VK_LEFT && !direccion.equals("RIGHT")) {
                direccion = "LEFT";
            } else if (key == KeyEvent.VK_UP && !direccion.equals("DOWN")) {
                direccion = "UP";
            } else if (key == KeyEvent.VK_DOWN && !direccion.equals("UP")) {
                direccion = "DOWN";
            } else if (key == KeyEvent.VK_ESCAPE) {
                System.exit(0); 
            }
        } else if (estado == ESTADO.GAME_OVER) {
            if (key == KeyEvent.VK_N) {
                resetSettings();
                startGame();
                estado= ESTADO.GAME;
            }else if (key == KeyEvent.VK_ESCAPE) {
                resetSettings();
                System.exit(0);
            }
        }
    }
}

private class Momento extends Thread {
        @Override
        public void run() {
            while (true) {
                if (estado == ESTADO.GAME || estado == ESTADO.GAME_OVER) {
                    actualizar();
                }
                try {
                    Thread.sleep(Math.abs(SPEED));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new SnakeTG();
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

