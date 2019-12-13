package com.examples;

import org.zeromq.*;

import java.util.Scanner;

//- Клиент. Подключается к центральному прокси.
// Читает команды из консоли и отправляет их в прокси.
public class ClientReq {

    private static final String ERROR_MESSAGE = "There was an error with the client";
    
    public static void main(String[] args) {

        ZContext context = new ZContext();
        //Socket to talk to server
        //ZMQ.Socket socket = context.createSocket(SocketType.REQ);
        //socket.setHWM(0);
        //socket.connect(FRONTEND_SOCKET); //"tcp://localhost:5559"
        try {
            System.out.println("connect");
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.setHWM(0);
            socket.connect("tcp://localhost:5555");

            Scanner in = new Scanner(System.in);

            while (true) {
                String message = in.nextLine();
                if (message.equals("EXIT")) {
                    break;
            }
            if (!message.contains(GET_COMMAND) && !message.contains(PUT_COMMAND)) {
                    System.out.println("INCORRECT INPUT, you can use only get and put comands");
            } else {
                ZMsg res  = new ZMsg();
                res.addString(message);
                res.send(socket);

                ZMsg req = ZMsg.recvMsg(socket);
                if(req == null) {
                    break;
                }

                String s = req.popString();
                System.out.println(s);
                req.destroy();
            }


            }
            for (int i = 0; i < 10; i++) {
                socket.send("request" + i, 0);
                String reply = socket.recvStr();
                System.out.println("reply" + i + " result=" + reply);
            }
        } catch (ZMQException ex){
            System.out.println(ERROR_MESSAGE);
            ex.printStackTrace();
            //context.destroySocket(socket);
            //context.destroy();
        }
    }
}
