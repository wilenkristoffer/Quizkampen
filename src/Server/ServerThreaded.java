package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThreaded implements Runnable {

    Socket socket1;
    Socket socket2;
    private final PrintWriter out1;
    private final PrintWriter out2;
    private final BufferedReader in1;
    private final BufferedReader in2;
    DAO dao = new DAO();
    private final List<String> listOfSentQuestions = new ArrayList<>();
    private String listWithPlayer1Points = "";
    private String listWithPlayer2Points = "";
    private int currentRound = 1;

    public ServerThreaded(Socket socket1, Socket socket2) {
        this.socket1 = socket1;
        this.socket2 = socket2;

        try {
            in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            out1 = new PrintWriter(socket1.getOutputStream(), true);

            in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            out2 = new PrintWriter(socket2.getOutputStream(), true);

            out1.println("PLAYER1");
            out2.println("PLAYER2");

            out1.println("START"); //I Client anropas categoryUI.
            out2.println("WAIT"); //I Client hamnar man i "väntrum"

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public void run() {

            loop:
            while (true) {
                try {
                    String player1Message;
                    while ((player1Message = in1.readLine()) != null) {
                        if (player1Message.contains("CATEGORY")) {
                            out1.println("QUESTIONS" + "|" + currentRound);
                            sendNextQuestion(player1Message, out1);

                        } else if (player1Message.startsWith("GIVE_UP"))  {
                            handleGiveUp(out2);
                            break;

                        }  else if (player1Message.contains("NEXT_QUESTION")) {
                            sendNextQuestion(null, out1);

                        }else if (player1Message.contains("OPEN_RESULT")){
                            String score = getScore(player1Message);
                            listWithPlayer1Points = listWithPlayer1Points  +  score + ",";
                            sendResponse( listWithPlayer1Points + "|" + listWithPlayer2Points + "|" + "OPEN_RESULT", out1);

                        } else if (player1Message.contains("ALL_Q_ANSWERED")) { //Skickas från ResultGUI när spelaren har kollat klart på resultatet
                            sendResponse(player1Message, out1);

                            out2.println("OPPONENT_DONE" + "|" + currentRound);
                            sendPreviousQuestions();
                            break;
                        } else if (player1Message.contains("GAME_FINISHED")) {

                            String score = getScore(player1Message);
                            listWithPlayer1Points = listWithPlayer1Points + score + ",";
                            sendResponse( listWithPlayer1Points + "|" + listWithPlayer2Points + "|" + "OPEN_LAST_RESULT", out1);

                            sendResponse("GAME_PLAYERONE_FINISHED" +"|" + currentRound, out2);
                            sendPreviousQuestions();
                            break;
                        }

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    String player2Message;
                    while ((player2Message = in2.readLine()) != null) {

                        if (player2Message.contains("NEXT_QUESTION")) {
                            sendPreviousQuestions();

                        } else if (player2Message.contains("CATEGORY")) {
                            out2.println("CATEGORY");
                            sendNextQuestion(player2Message, out2);

                        } else if (player2Message.startsWith("GIVE_UP")) {
                            handleGiveUp(out1);
                            break;

                        }else if (player2Message.contains("OPEN_RESULT")) {
                            String score = getScore(player2Message);
                            listWithPlayer2Points = listWithPlayer2Points  +  score + ",";
                            sendResponse( listWithPlayer1Points + "|" + listWithPlayer2Points + "|" + "OPEN_RESULT", out2);

                        } else if (player2Message.contains("GAME_FINISHED")) { //Här kan det bli fel.
                            String score = getScore(player2Message);
                            listWithPlayer2Points = listWithPlayer2Points + score + ",";
                            sendResponse(listWithPlayer1Points + "|" + listWithPlayer2Points + "|" + "GAME_FINISHED", out1);
                            sendResponse(listWithPlayer1Points + "|" + listWithPlayer2Points + "|" + "GAME_FINISHED", out2);

                        } else if (player2Message.contains("ALL_Q_ANSWERED")) {
                            String pointsThisRoundPlayer1 = getScoreForCurrentRound(listWithPlayer1Points);
                            String pointsThisRoundPlayer2 = getScoreForCurrentRound(listWithPlayer2Points);
                            currentRound++;
                            out1.println(pointsThisRoundPlayer1 +"|"+ pointsThisRoundPlayer2 +"|" + "START");
                            continue loop;

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    private String getScoreForCurrentRound(String s) {
        String[] values = s.split(",\\s*");
        if (values.length > 0) {
            return values[values.length -1];
        } else {
            return "";
        }
    }

    public String getScore(String message) {
        String[] parts = message.split("#");
        return parts[0];
    }

    private void sendNextQuestion(String category, PrintWriter out) {
        String response = dao.getRandomQuestionAndAnswers(category);
        out.println(response);
        addSentQuestionToList(response);

    }
    private void sendResponse(String message, PrintWriter out) {
        out.println(message);
        out.flush();
    }
    private void addSentQuestionToList(String message){
        listOfSentQuestions.add(message);

    }
    private void sendPreviousQuestions(){
        out2.println(listOfSentQuestions.get(0));
        listOfSentQuestions.remove(0);

    }
    private void handleGiveUp(PrintWriter opponentOut) {
        opponentOut.println("OPPONENT_GAVE_UP");
    }
}