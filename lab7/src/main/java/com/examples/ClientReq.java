package com.examples;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ClientReq {
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

            while (true) {
                
            }

            for (int i = 0; i < 10; i++) {
                socket.send("request" + i, 0);
                String reply = socket.recvStr();
                System.out.println("reply" + i + " result=" + reply);
            }
        } finally {
            context.destroySocket(socket);
            context.destroy();
        }
    }
}
