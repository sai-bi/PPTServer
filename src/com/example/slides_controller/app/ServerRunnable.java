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

    private byte[] get_screenshot() throws IOException {
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
            switch (message.getOperation()) {
                case Command.START_DISPLAY:
                    robot.keyPress(KeyEvent.VK_F5);
                    robot.keyRelease(KeyEvent.VK_F5);
                    tellAllClients(message, Command.IMAGE, -1);
                    break;
                case Command.END_DISPLAY:
                    robot.keyPress(KeyEvent.VK_ESCAPE);
                    robot.keyRelease(KeyEvent.VK_ESCAPE);
                    tellAllClients(message, Command.IMAGE, -1);
                    break;
                case Command.BLACK_SCREEN:
                    robot.keyPress(KeyEvent.VK_B);
                    robot.keyRelease(KeyEvent.VK_B);
                    tellAllClients(message, Command.IMAGE, -1);
                    break;
                case Command.WHITE_SCREEN:
                    robot.keyPress(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_W);
                    tellAllClients(message, Command.IMAGE, -1);
                    break;
                case Command.NEXT_SLIDE:
                    robot.keyPress(KeyEvent.VK_DOWN);
                    robot.keyRelease(KeyEvent.VK_DOWN);
                    tellAllClients(message, Command.IMAGE, -1);
                    break;
                case Command.PREVIOUS_SLIDE:
                    robot.keyPress(KeyEvent.VK_UP);
                    robot.keyRelease(KeyEvent.VK_UP);
                    tellAllClients(message, Command.IMAGE, -1);
                    break;
                case Command.LASER:
                    // ignore temporarily
                    break;
                case Command.HIGHLIGHT:
                    // ignore temporarily
                    break;
                case Command.PEN:
                    robot.keyPress(KeyEvent.VK_WINDOWS);
                    robot.keyPress(KeyEvent.VK_P);
                    robot.keyRelease(KeyEvent.VK_P);
                    robot.keyRelease(KeyEvent.VK_WINDOWS);
                    break;
                case Command.LINE:
                    drawLines(message.getLine_x(), message.getLine_y(), message.getScreenWidth(), message.getScreenHeight());
                    tellAllClients(message, Command.LINE, id);
                    break;
                case Command.EXIT_BLACK_SCREEN:
                    robot.keyPress(KeyEvent.VK_B);
                    robot.keyRelease(KeyEvent.VK_B);
                    tellAllClients(message, Command.IMAGE, -1);
                    break;
                case Command.EXIT_WHITE_SCREEN:
                    robot.keyPress(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_W);
                    tellAllClients(message, Command.IMAGE, -1);
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


        private synchronized void tellAllClients(Message input_message, int message_type, int neglect_client_id) {
            Message message = new Message();
            message.setScreenHeight(input_message.getScreenHeight());
            message.setScreenWidth(input_message.getScreenWidth());
            if (message_type == Command.IMAGE) {
                try {
                    Thread.sleep(500);
                    byte[] screen_byte = get_screenshot();
                    message.setImageByteArray(screen_byte);
                    message.setOperation(Command.IMAGE);

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else if (message_type == Command.LINE) {
                message.setLine_x(input_message.getLine_x());
                message.setLine_y(input_message.getLine_y());
                message.setOperation(Command.LINE);
            }
            for (int x : object_output_list.keySet()) {
                if (x == neglect_client_id)
                    continue;
                try {
                    ObjectOutputStream out = object_output_list.get(x);
                    out.writeObject(message);
                    out.flush();
                    System.out.println("send to client!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
