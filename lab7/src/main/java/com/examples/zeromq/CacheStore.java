package com.examples.zeromq;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.util.Scanner;

//- Хранилище части распределенного кэша.
// Открывает сокет DEALER, подключается к центральному прокси.
// После подключения с определнным интервалом времени высылает сообщение NOTIFY в котором сообщает интервал хранимых значений.
// Также принимает из сокета два вида команд — на изменение ячейки кэша и на извлечение ячейки.
public class CacheStore {

    private static final String ERROR_MESSAGE = "There was an error with the client. Please retry.";
    
    public static void  main (String[] arg) {

        Scanner in = new Scanner(System.in);
        try {
            ZMQ.Context context = ZMQ.context(1);

            //Socket to talk to server
            ZMQ.Socket requester = context.createSocket(SocketType.DEALER);
            requester.connect("tcp://localhost:5559");

        } catch (ZMQException ex) {
            System.out.println(ERROR_MESSAGE);
        }
    }
}
