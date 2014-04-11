package com.example.slides_controller.app;
/**
 * Created by saibi on 3/25/14.
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunnable {
    private ServerSocket socket;

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
        private ObjectOutputStream object_output;

        public ClientHandler(Socket client) {
            this.client = client;
        }


        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message input_message;
            try {
                Robot robot = new Robot();
                ObjectInputStream objs=new ObjectInputStream(client.getInputStream());
                object_output=new ObjectOutputStream(client.getOutputStream());
                while (true) {
                    if(client == null)
                        break;

                    input_message=(Message)objs.readObject();
                    System.out.println("Got a message...");
                    System.out.println(input_message.getOperation());

                    handleMessage(input_message);

                    Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
                    BufferedImage buff=robot.createScreenCapture(new Rectangle(0,0,d.width,d.height));
                    BufferedImage scaled=ImageSolver.resizeImageWithHint(buff,640,d.height*640/d.width,BufferedImage.TYPE_INT_RGB);

                    /*
                    byte[] image_byte_array=ImageSolver.imgbyte(scaled);
                    Message m=new Message();
                    m.setOperation(11);
                    m.setImageByteArray(image_byte_array);
                    object_output.writeObject(m);
                    object_output.flush();
                    input_message = parse_input_message(input_string);
                    */
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error in handling client");
            }
        }

    }


    private void handleMessage(Message message){
        switch (message.getOperation()){
            case Command.START_DISPLAY:
                break;
            case Command.END_DISPLAY:
                break;
            case Command.BLACK_SCREEN:
                break;
            case Command.WHITE_SCREEN:
                break;
            case Command.NEXT_SLIDE:
                break;
            case Command.PREVIOUS_SLIDE:
                break;
            case Command.LASER:
                break;
            case Command.HIGHLIGHT:
                break;
            case Command.PEN:
                break;
            case Command.EXIT_BLACK_SCREEN:
                break;
            case Command.EXIT_WHITE_SCREEN:
                break;
            default:
                break;
        }

    }

































}
