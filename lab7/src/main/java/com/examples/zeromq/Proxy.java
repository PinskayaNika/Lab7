package com.examples.zeromq;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Proxy {
    public static void main(String[] args) {

//    Создаем ZContext или ZMQ.Context
        ZContext context = new ZContext();
        ZMQ.Socket socket = null;

        try {
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
