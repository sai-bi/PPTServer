/**
 * Created by saibi on 3/25/14.
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunnable {
    private ServerSocket socket;
    static private final int START_DISPLAY = 0;
    static private final int END_DISPLAY = 1;
    static private final int PREVIOUS_SLIDE = 2;
    static private final int NEXT_SLIDE = 3;
    static private final int BLACK_SCREEN = 4;
    static private final int EXIT_BLACK_SCREEN = 5;
    static private final int WHITE_SCREEN = 6;
    static private final int EXIT_WHITE_SCREEN = 7;

    public ServerRunnable(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("cannot listen on port " + Integer.toString(port));
            System.exit(0);
        }
        run();
    }

    public void run() {
        // TODO Auto-generated method stub
        try {
            while (true) {
                System.out.println("Listenning...");
                Socket client = socket.accept();
                System.out.println("Got a connection.");
                Thread t = new Thread(new ClientHandler(client));
                t.start();
            }
        } catch (Exception e) {
            System.out.println("bugs...");
        }
    }

    class ClientHandler implements Runnable {
        private Socket client;
        private ObjectInputStream object_input;

        public ClientHandler(Socket client) {
            this.client = client;
        }


        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message input_message;
            try {
                Robot robot = new Robot();
                InputStreamReader isr = new InputStreamReader(client.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String input_string;
                while (true) {
                    input_string = br.readLine();
                    System.out.println("Got a message...");
                    input_message = parse_input_message(input_string);
                    switch (input_message.getOperation()) {
                        case START_DISPLAY:
                            robot.keyPress(KeyEvent.VK_F5);
                            break;
                        case END_DISPLAY:
                            robot.keyPress(KeyEvent.VK_ESCAPE);
                            break;
                        case PREVIOUS_SLIDE:
                            robot.keyPress(KeyEvent.VK_UP);
                            break;
                        case NEXT_SLIDE:
                            robot.keyPress(KeyEvent.VK_DOWN);
                            break;
                        case BLACK_SCREEN:
                            robot.keyPress(KeyEvent.VK_B);
                            break;
                        case EXIT_BLACK_SCREEN:
                            robot.keyPress(KeyEvent.VK_ESCAPE);
                            break;
                        case WHITE_SCREEN:
                            robot.keyPress(KeyEvent.VK_W);
                            break;
                        case EXIT_WHITE_SCREEN:
                            robot.keyPress(KeyEvent.VK_ESCAPE);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error in handling client");
            }
        }

    }

    private Message parse_input_message(String input_message){
        System.out.println(input_message);
        Message message = new Message();
        if(input_message == null) {
            System.out.println("message is null");
            return message;
        }
        String[] result = input_message.split(";");
        if(result.length == 0){
            return message;
        }
        try {
            // first part is operation
            message.setOperation(Integer.parseInt(result[0]));
        } catch (Exception e){
            //e.printStackTrace();
            return message;
        }
        return message;
    }

}
