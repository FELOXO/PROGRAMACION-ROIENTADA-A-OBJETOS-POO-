package coincollector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class CoinCollector extends JFrame {
    private static final int ANCHO = 800;
    private static final int ALTO = 600;
    private static final int TAMAÑO_JUGADOR = 30;
    private static final int TAMAÑO_MONEDA = 20;
    private static final int TAMAÑO_OBSTACULO = 30;

    private Jugador jugador;
    private ArrayList<Moneda> monedas;
    private ArrayList<Obstaculo> obstaculos;
    private int puntuacion;
    private Random aleatorio;
    private Timer temporizador;

    public CoinCollector() {
        setTitle("Coin Collector");
        setSize(ANCHO, ALTO);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        inicializarJuego();
    }

    private void inicializarJuego() {
        jugador = new Jugador(ANCHO / 2 - TAMAÑO_JUGADOR / 2,
                              ALTO / 2 - TAMAÑO_JUGADOR / 2,
                              TAMAÑO_JUGADOR);

        monedas = new ArrayList<>();
        obstaculos = new ArrayList<>();
        aleatorio = new Random();
        puntuacion = 0;

        generarMonedas(5);
        generarObstaculos(3);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                jugador.mover(e.getKeyCode(), ANCHO, ALTO);
            }
        });

        temporizador = new Timer(30, e -> {
            bucleJuego();
        });

        temporizador.start();
    }

    private void generarMonedas(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            int x = aleatorio.nextInt(ANCHO - TAMAÑO_MONEDA);
            int y = aleatorio.nextInt(ALTO - TAMAÑO_MONEDA);
            monedas.add(new Moneda(x, y, TAMAÑO_MONEDA));
        }
    }

    private void generarObstaculos(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            int x = aleatorio.nextInt(ANCHO - TAMAÑO_OBSTACULO);
            int y = aleatorio.nextInt(ALTO - TAMAÑO_OBSTACULO);
            obstaculos.add(new Obstaculo(x, y, TAMAÑO_OBSTACULO));
        }
    }

    private void bucleJuego() {
        verificarColisiones();
        actualizarObjetos();
        repaint();
    }

    private void actualizarObjetos() {
        jugador.actualizar();
        for (Moneda moneda : monedas) moneda.actualizar();
        for (Obstaculo obstaculo : obstaculos) obstaculo.actualizar();
    }

    private void verificarColisiones() {
        ArrayList<Moneda> recogidas = new ArrayList<>();
        for (Moneda moneda : monedas) {
            if (jugador.getLimites().intersects(moneda.getLimites())) {
                recogidas.add(moneda);
                puntuacion += 10;
            }
        }
        monedas.removeAll(recogidas);

        if (monedas.isEmpty()) {
            generarMonedas(5);
            generarObstaculos(1);
        }

        for (Obstaculo obstaculo : obstaculos) {
            if (jugador.getLimites().intersects(obstaculo.getLimites())) {
                finDelJuego();
                return;
            }
        }
    }

    private void finDelJuego() {
        temporizador.stop();
        JOptionPane.showMessageDialog(this, "¡Game Over! Puntuación final: " + puntuacion);
        System.exit(0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, ANCHO, ALTO);

        jugador.dibujar(g2d);
        for (Moneda moneda : monedas) moneda.dibujar(g2d);
        for (Obstaculo obstaculo : obstaculos) obstaculo.dibujar(g2d);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Puntuación: " + puntuacion, 20, 30);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CoinCollector().setVisible(true));
    }
}
