package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import Server.PropertiesClass;

public class Client implements ActionListener {
    private final JFrame mainFrame = new JFrame("Quizkampen");
    ImageIcon imgIcon = new ImageIcon("src/Pics/Banner.jpg");
    private final JPanel titlePanel = new JPanel();
    private final JLabel gameTitle = new JLabel();
    private final JPanel howManyPanel = new JPanel();
    private final JPanel amountOfRoundPanel = new JPanel();
    private final JPanel colorPanel = new JPanel();
    private final JPanel colorInsideColorPanel = new JPanel();
    private final JLabel bgColor = new JLabel("Välj bakgrundsfärg:");
    private final JLabel fColor = new JLabel("Välj font färg:");
    private Color backgroundColor = Color.white;
    private Color fontColor = Color.BLACK;
    private final JPanel examplePanel = new JPanel();
    private final JLabel exampleText = new JLabel("Exempel");

    private final JLabel howManyRounds = new JLabel();
    private final JLabel howManyQuestions = new JLabel();
    private final JPanel bottomPanel = new JPanel();
    private final JButton newGame = new JButton("Nytt Spel");
    private final JButton quitGame = new JButton("Avsluta");

    private final JComboBox<String> colorChoose = new JComboBox<>();
    private final JComboBox<String> fontColorChoose = new JComboBox<>();
    private String currentRound;
    private PrintWriter out;
    private BufferedReader in;
    private String player;

    private final PropertiesClass propertiesClass = new PropertiesClass();

    public Client() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(640, 480);
            mainFrame.setVisible(true);
            mainFrame.setResizable(true);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setLayout(new BorderLayout());
            mainFrame.add(titlePanel, BorderLayout.NORTH);
            mainFrame.add(howManyPanel, BorderLayout.CENTER);
            mainFrame.add(bottomPanel, BorderLayout.SOUTH);
            howManyPanel.setLayout(new GridLayout(1, 2));
            titlePanel.add(gameTitle);
            gameTitle.setIcon(imgIcon);
            titlePanel.setBackground(Color.BLACK);

            propertiesClass.loadProperties();
            int amountOfRounds = propertiesClass.getAmountOfRounds();
            int amountOfQuestions = propertiesClass.getAmountOfQuestions();
            howManyRounds.setText("Antal ronder: " + amountOfRounds);
            howManyQuestions.setText("Antal frågor/rond: " + amountOfQuestions);

            howManyPanel.add(amountOfRoundPanel);
            howManyPanel.add(colorPanel);
            amountOfRoundPanel.setLayout(new GridLayout(2, 1));
            amountOfRoundPanel.add(howManyRounds);
            amountOfRoundPanel.add(howManyQuestions);

            colorPanel.setLayout(new GridLayout(2, 1));
            colorPanel.add(colorInsideColorPanel);
            colorPanel.add(examplePanel);
            colorInsideColorPanel.setLayout(new GridLayout(4, 1));
            colorInsideColorPanel.add(bgColor);
            ColorBox(colorChoose);
            colorInsideColorPanel.add(fColor);
            FontColorBox(fontColorChoose);

            examplePanel.setLayout(new FlowLayout());
            examplePanel.setBackground(Color.WHITE);
            examplePanel.add(exampleText);
            exampleText.setForeground(Color.BLACK);

            bottomPanel.add(newGame);
            bottomPanel.add(quitGame);

            newGame.addActionListener(this);
            quitGame.addActionListener(this);


        });

    }

    public void connectToServer() {
        try {
            Socket sock = new Socket("127.0.0.1", 12345);
            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setBgColor(Color color) {
        backgroundColor = color;
    }

    private void setFontColor(Color color) {
        fontColor = color;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == newGame) {
            connectToServer();
            startGame();
            mainFrame.setTitle("Väntar på spelare...");
            newGame.setEnabled(false);
        } else if (e.getSource() == quitGame) {
            System.exit(0);
        } else if (colorChoose.getSelectedIndex() == 0) {
            examplePanel.setBackground(Color.white);
            setBgColor(Color.white);
        } else if (colorChoose.getSelectedIndex() == 1) {
            examplePanel.setBackground(Color.BLACK);
            setBgColor(Color.BLACK);
        } else if (colorChoose.getSelectedIndex() == 2) {
            examplePanel.setBackground(Color.BLUE);
            setBgColor(Color.BLUE);
        } else if (colorChoose.getSelectedIndex() == 3) {
            examplePanel.setBackground(Color.PINK);
            setBgColor(Color.PINK);
        } else if (colorChoose.getSelectedIndex() == 4) {
            examplePanel.setBackground(Color.CYAN);
            setBgColor(Color.CYAN);

        }  if (fontColorChoose.getSelectedIndex() == 0) {
            exampleText.setForeground(Color.BLACK);
            setFontColor(Color.BLACK);
        } else if (fontColorChoose.getSelectedIndex() == 1) {
            exampleText.setForeground(Color.WHITE);
            setFontColor(Color.WHITE);
        } else if (fontColorChoose.getSelectedIndex() == 2) {
            exampleText.setForeground(Color.BLUE);
            setFontColor(Color.BLUE);
        } else if (fontColorChoose.getSelectedIndex() == 3) {
            exampleText.setForeground(Color.PINK);
            setFontColor(Color.PINK);
        } else if (fontColorChoose.getSelectedIndex() == 4) {
            exampleText.setForeground(Color.CYAN);
            setFontColor(Color.CYAN);
        }
    }

    private void ColorBox(JComboBox<String> colorChoose) {
        colorInsideColorPanel.add(colorChoose);
        colorChoose.addItem("Vit(default)");
        colorChoose.addItem("Svart");
        colorChoose.addItem("Blå");
        colorChoose.addItem("Rosa");
        colorChoose.addItem("Cyan");

        colorChoose.setPreferredSize(new Dimension(100, 30));
        colorChoose.addActionListener(this);
    }

    private void FontColorBox(JComboBox<String> colorChoose) {
        colorInsideColorPanel.add(colorChoose);
        colorChoose.addItem("Svart(default)");
        colorChoose.addItem("Vit");
        colorChoose.addItem("Blå");
        colorChoose.addItem("Rosa");
        colorChoose.addItem("Cyan");

        colorChoose.setPreferredSize(new Dimension(100, 30));
        colorChoose.addActionListener(this);
    }

    public static void main(String[] args) {
        Client client = new Client();

    }

    public String getScoreStr1(String message) {
        String[] parts = message.split("\\|");
        return parts[0];
    }

    public String getScoreStr2(String message) {
        String[] parts = message.split("\\|");
        return parts[1];
    }

    public String getCurrentRound(String message) {
        String[] parts = message.split("\\|");
        return parts[1];
    }

    public void startGame() {
        new Thread(() -> {
            try {
                String fromServer;
                String scoreStr1;
                String scoreStr2;


                while ((fromServer = in.readLine()) != null) {

                    if (fromServer.contains("START")) {

                        if (fromServer.equals("START")) {
                            mainFrame.dispose();
                            CategoryGUI categoryGUI = new CategoryGUI(out, player, "", "");
                        } else {
                            scoreStr1 = getScoreStr1(fromServer);
                            scoreStr2 = getScoreStr2(fromServer);
                            mainFrame.dispose();
                            CategoryGUI categoryGUI = new CategoryGUI(out, player, scoreStr1, scoreStr2);
                        }

                    } else if (fromServer.startsWith("WAIT")) {
                        mainFrame.setTitle("PLAYER 2\tVäntar på att motståndare ska slutföra runda...");

                    } else if (fromServer.startsWith("PLAYER")) {
                        player = fromServer;

                    } else if (fromServer.startsWith("QUESTIONS")) {
                        currentRound = getCurrentRound(fromServer);

                        QuestionGUI questionGUI = new QuestionGUI(in, out, player, currentRound, backgroundColor, fontColor);


                    } else if (fromServer.contains("OPEN_RESULT")) {


                        scoreStr1 = getScoreStr1(fromServer);
                        scoreStr2 = getScoreStr2(fromServer);

                        ResultGUI resultGUI = new ResultGUI(out, player, scoreStr1, scoreStr2);
                    } else if (fromServer.contains("OPEN_LAST_RESULT")) {
                        scoreStr1 = getScoreStr1(fromServer);
                        scoreStr2 = getScoreStr2(fromServer);

                        ResultGUI resultGUI = new ResultGUI(out, player, scoreStr1, scoreStr2);
                        resultGUI.disablePlayButton();
                    } else if (fromServer.startsWith("OPPONENT_GAVE_UP")) {
                        SwingUtilities.invokeLater(() -> {
                            mainFrame.dispose();
                            JOptionPane.showMessageDialog(mainFrame, "Din motståndare gav upp, du vann!");
                        });
                        break;
                    } else if (fromServer.startsWith("OPPONENT_DONE")) {
                        mainFrame.dispose();
                        currentRound = getCurrentRound(fromServer);
                        QuestionGUI questionGUI = new QuestionGUI(in, out, player, currentRound, backgroundColor, fontColor);

                    } else if (fromServer.startsWith("GAME_PLAYERONE_FINISHED")) {
                        mainFrame.dispose();
                        currentRound = getCurrentRound(fromServer);
                        QuestionGUI questionGUI = new QuestionGUI(in, out, player, currentRound, backgroundColor, fontColor);

                    } else if (fromServer.contains("GAME_FINISHED")) {
                        scoreStr1 = getScoreStr1(fromServer);
                        scoreStr2 = getScoreStr2(fromServer);

                        ResultGUI resultGUI = new ResultGUI(out, player, scoreStr1, scoreStr2);
                        resultGUI.showFinalResult();
                        resultGUI.disablePlayButton();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
