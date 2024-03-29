package com.examples.zeromq;

import org.zeromq.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//- Хранилище части распределенного кэша.
// Открывает сокет DEALER, подключается к центральному прокси.
// После подключения с определнным интервалом времени высылает сообщение NOTIFY в котором сообщает интервал хранимых значений.
// Также принимает из сокета два вида команд — на изменение ячейки кэша и на извлечение ячейки.
public class CacheStore {

    private static int leftBound;
    private static int rightBound;
    private static final int EPSILON_TIME = 5000;
    private static final String DELIMITER = " ";
    private static final String EMPTY_FRAME = "";
    private static final String GET_COMMAND = "GET";
    private static final String PUT_COMMAND = "PUT";
    private static final String ERROR_MESSAGE = "There was an error with the cache. Please retry.";


    public static void  main (String[] arg) {

        Scanner in = new Scanner(System.in);
        leftBound = in.nextInt();
        rightBound = in.nextInt();

        Map<Integer, String> cache = new HashMap<>();
        for (int i = leftBound; i <= rightBound; i++) {
            cache.put(i, Integer.toString(i));
        }

        try {
            ZContext context = new ZContext();

            //Socket to talk to server
            ZMQ.Socket backendSocket = context.createSocket(SocketType.DEALER);
            backendSocket.setHWM(0);
            backendSocket.connect("tcp://localhost:5561");
            //System.out.println("launch and connect broker");

            //Initialize poll set
            ZMQ.Poller items = context.createPoller(1);
            items.register(backendSocket, ZMQ.Poller.POLLIN);
            long time = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {

                //apply state updates from main thread
                items.poll(1);
                if (System.currentTimeMillis() - time > EPSILON_TIME) {
                    ZMsg messageTime = new ZMsg();
                    messageTime.addLast(
                            "Heartbleed" + DELIMITER
                                    + Integer.toString(leftBound) + DELIMITER
                                    + Integer.toString(rightBound)
                    );
                    messageTime.send(backendSocket);
                }

                if (items.pollin(0)) {
                    ZMsg message = ZMsg.recvMsg(backendSocket);
                    System.out.println("GOT MESSAGE ->" + message.toString());
                    ZFrame content = message.getLast();
                    String[] contentArr = content.toString().split(DELIMITER);

                    if (contentArr[0].equals(GET_COMMAND)) {
                        int pos = Integer.parseInt(contentArr[1]);
                        String value = cache.get(pos);
                        message.pollLast();
                        message.addLast(value);
                        message.send(backendSocket);
                    }

                    if (contentArr[0].equals(PUT_COMMAND)) {
                        int pos = Integer.parseInt(contentArr[1]);
                        String swapString = contentArr[2];
                        cache.put(pos, swapString);
                        message.send(backendSocket);
                    }
                }
            }

        } catch (ZMQException ex) {
            System.out.println(ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
