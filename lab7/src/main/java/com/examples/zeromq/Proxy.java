package com.examples.zeromq;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Proxy {

    private static final int EPSILON_TIME = 5000;
    private static final String  BACKEND_SOCKET = "tcp://localhost:5559";
    private static final String  FRONTEND_SOCKET = "tcp://localhost:5560";
    public static void main(String[] args) {

//    Создаем ZContext или ZMQ.Context
        try {
        ZContext context = new ZContext();
        ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
        ZMQ.Socket backend = context.createSocket(SocketType.ROUTER);

        frontend.setHWM(0);
        backend.setHWM(0);
        frontend.bind("tcp://localhost:5559");
        backend.bind("tcp://localhost:5560");

        //Initialize poll set
        ZMQ.Poller items = context.createPoller(2);
        items.register(frontend, ZMQ.Poller.POLLIN);
        items.register(backend, ZMQ.Poller.POLLIN);

        Map<ZFrame, CacheCommutator> commutatorMap = new HashMap<>();
        long time = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted()) {
            items.poll(1);
            if (!commutatorMap.isEmpty() && System.currentTimeMillis() - time > 5000 * 2) {
                for (Iterator <Map.Entry <ZFrame, CacheCommutator>> it = commutatorMap.entrySet().iterator(); it.hasNext(); ) {
                    
                }
            }
        }






            socket.bind("tcp://localhost:5555");
            System.out.println("bind!");
            while (!Thread.currentThread().isInterrupted()) {
                String req = socket.recvStr();
                socket.send("reply! + req);");
            }
        } finally {
            context.destroySocket(socket);
            context.destroy();
        }
// С помощью методов Context.socket(<тип сокета>) или ZContext.createSocket(<тип сокета>) создаем сокет
//    В бесконечном цикле читаем из сокета данные и отвечаем или посылаем данные в другие сокеты
//    Посылать можно набор байтов, строку и сообщение Zmsg(набор фреймов)
//    Массив и строка посылаются методами сокета sendXXX, принимаются методами сокетов recvXXX
//    ZMsg отправляется и своими методами ZMsg.send и ZMsg.recvMsg

    }
}
