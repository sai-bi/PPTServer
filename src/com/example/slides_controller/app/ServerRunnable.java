package com.example.slides_controller.app;
/**
 * Created by saibi on 3/25/14.
 */

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerRunnable {
    private static final int SCREEN_SHOT = 1;
    private static final int FORWARD = 2;
    private static int clientcount = 1;

    private static int screen_width = 1368;
    private static int screen_height = 768;

    private ServerSocket socket;
    private HashMap<Integer, ObjectOutputStream> object_output_list;
    private Robot robot;

    public ServerRunnable(int port) throws AWTException {
        object_output_list = new HashMap<Integer, ObjectOutputStream>();
        robot = new Robot();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        screen_width = d.width;
        screen_height = d.height;
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("cannot listen on port " + Integer.toString(port));
            System.exit(0);
        }
        run();
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Listenning...");
                Socket client = socket.accept();
                System.out.println("Got a connection.");
                object_output_list.put(clientcount, new ObjectOutputStream(client.getOutputStream()));


                Thread t = new Thread(new ClientHandler(client, clientcount));
                clientcount++;
                t.start();
            }
        } catch (Exception e) {
            System.out.println("bugs...");
        }
    }

    private synchronized void drawLines(ArrayList<Float> line_x, ArrayList<Float> line_y, int width, int height) {

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            for (int i = 0; i < line_x.size(); i++) {
                int x = (int) (line_x.get(i) / width * screen_width);
                int y = (int) (line_y.get(i) / height * screen_height);

                if (i == 0) {

                    robot.mouseMove(x, y);
                    robot.setAutoDelay(50);
                    robot.mousePress(InputEvent.BUTTON1_MASK);


                } else {
                    robot.mouseMove(x, y);
                }

            }
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void addClient(int clientid, ObjectOutputStream out) {
        object_output_list.put(clientid, out);
    }

    private synchronized void removeClient(int clientid) {
        object_output_list.remove(clientid);
    }

    /**
     * Send message to all clients.
     *
     * @param message Message object to be sent
     */
    private synchronized void tellAllClients(Message message, int flag, int command, int neglect) {

        message.setOperation(command);
        switch (flag) {
            case SCREEN_SHOT:

                try {
                    byte[] screen_byte = screenshot();
                    message.setImageByteArray(screen_byte);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
                break;
            case FORWARD:
                break;
            default:
                break;


        }
        for (int x : object_output_list.keySet()) {

            if (x == neglect)
                continue;
            try {

                ObjectOutputStream out = object_output_list.get(x);
                out.writeObject(message);
                out.flush();
                System.out.println("send to client!");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }


        }

    }

    private byte[] screenshot() throws IOException {

        BufferedImage buff = robot.createScreenCapture(new Rectangle(0, 0, screen_width, screen_height));
        BufferedImage scale = ImageSolver.resizeImageWithHint(buff, 640, 640 * screen_height / screen_width, BufferedImage.TYPE_INT_RGB);
        byte[] imgbyte = ImageSolver.getimgbyte(scale);
        buff = null;
        scale = null;

        return imgbyte;
    }

    class ClientHandler implements Runnable {
        private int id;
        private Socket client;
        private ObjectInputStream object_input;
        private ObjectOutputStream object_output;

        public ClientHandler(Socket client, int id) {
            this.client = client;
            this.id = id;
        }

        private void handleMessage(Message message) throws InterruptedException {
            Message out_message = new Message();
            switch (message.getOperation()) {
                case Command.START_DISPLAY:


                    tellAllClients(out_message, SCREEN_SHOT, Command.IMAGE, 0);
                    System.out.println("start display!");
                    break;
                case Command.END_DISPLAY:

                    break;
                case Command.BLACK_SCREEN:
                    break;
                case Command.WHITE_SCREEN:
                    break;
                case Command.NEXT_SLIDE:
                    try {
                        robot.keyPress(KeyEvent.VK_DOWN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(500);
                    tellAllClients(out_message, SCREEN_SHOT, Command.IMAGE, 0);
                    break;
                case Command.PREVIOUS_SLIDE:
                    try {
                        robot.keyPress(KeyEvent.VK_UP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(500);
                    tellAllClients(out_message, SCREEN_SHOT, Command.IMAGE, 0);
                    break;
                case Command.LASER:
                    break;
                case Command.HIGHLIGHT:
                    break;
                case Command.PEN:

                    tellAllClients(message, FORWARD, message.getOperation(), this.id);
                    break;
                case Command.LINE:
                    drawLines(message.getLine_x(), message.getLine_y(), message.getScreenWidth(), message.getScreenHeight());
                    //tellAllClients(message,FORWARD,message.getOperation(),this.id);
                    break;
                case Command.EXIT_BLACK_SCREEN:
                    break;
                case Command.EXIT_WHITE_SCREEN:
                    break;
                default:
                    break;
            }

        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message input_message;
            try {

                ObjectInputStream objs = new ObjectInputStream(client.getInputStream());
                // object_output = new ObjectOutputStream(client.getOutputStream());
                while (true) {
                    if (client == null)
                        break;

                    input_message = (Message) objs.readObject();
                    System.out.println("Got a message...");
                    System.out.println(input_message.getOperation());

                    handleMessage(input_message);

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error in handling client");
            }
        }

    }


}
