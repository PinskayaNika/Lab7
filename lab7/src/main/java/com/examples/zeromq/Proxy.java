package com.examples.zeromq;

import org.zeromq.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Proxy {

    private static final int EPSILON_TIME = 5000;
    private static final String EMPTY_FRAME = "";
    private static final String DELIMITER = " ";
    private static final String GET_COMMAND = "GET";
    private static final String PUT_COMMAND = "PUT";
    private static final String BACKEND_SOCKET = "tcp://localhost:5559";
    private static final String FRONTEND_SOCKET = "tcp://localhost:5560";
    private static final String ERROR_MESSAGE = "There was an error with a proxy. Please retry.";


    /*private static void  getErrorMessages(ZMsg errorMessage, ZMsg message, ZMQ.Socket frontend) {
        //errorMessage = new ZMsg();
        errorMessage.add(message.getFirst());
        errorMessage.add("");
        errorMessage.add("ERROR MESSAGE");
        errorMessage.send(frontend);
    }*/



    public static void main(String[] args) {

//    Создаем ZContext или ZMQ.Context
        try {
            ZContext context = new ZContext();
            ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
            ZMQ.Socket backend = context.createSocket(SocketType.ROUTER);

            frontend.setHWM(0);
            backend.setHWM(0);
            frontend.bind("tcp://localhost:5555");
            backend.bind("tcp://localhost:5561");

            //Initialize poll set
            ZMQ.Poller items = context.createPoller(2);
            items.register(frontend, ZMQ.Poller.POLLIN);
            items.register(backend, ZMQ.Poller.POLLIN);

            Map<ZFrame, CacheCommutator> commutatorMap = new HashMap<>();
            long time = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()) {
                items.poll(1);
                if (!commutatorMap.isEmpty() && System.currentTimeMillis() - time > EPSILON_TIME ) {
                    for (Iterator<Map.Entry<ZFrame, CacheCommutator>> it = commutatorMap.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<ZFrame, CacheCommutator> entry = it.next();

                        if (Math.abs(entry.getValue().getTime() - time) > EPSILON_TIME * 2) {
                            System.out.println("THIS CACHE WAS DELETED -> " + entry.getKey());
                            it.remove();
                        }
                    }
                    time = System.currentTimeMillis();

                }
                if (items.pollin(0)) {        //FRONTEND_MESSAGE
                    ZMsg message = ZMsg.recvMsg(backend);
                    if (message == null) {
                        break;
                    }
                    System.out.println("GOT MSG ->" + message);

                    if (commutatorMap.isEmpty()) {

                        ZMsg errorMessage = new ZMsg();
                        //getErrorMessages(errorMessage, message, frontend);
                        errorMessage.add(message.getFirst());
                        errorMessage.add("");
                        errorMessage.add("NO CURRENT CACHE");
                        errorMessage.send(frontend);
                    } else {
                        String[] data = message.getLast().toString().split(DELIMITER);
                        if (data[0].equals(GET_COMMAND)) {
                            for (Map.Entry<ZFrame, CacheCommutator> map : commutatorMap.entrySet()) {
                                if (map.getValue().isIntersect(data[1])) {
                                    ZFrame cacheFrame = map.getKey().duplicate();
                                    message.addFirst(cacheFrame);
                                    message.send(backend);
                                }
                            }
                        } else {
                            if (data[0].equals(PUT_COMMAND)) {
                                for (Map.Entry<ZFrame, CacheCommutator> map : commutatorMap.entrySet()) {
                                    if (map.getValue().isIntersect(data[1])) {
                                        ZMsg tmp = message.duplicate();
                                        ZFrame cacheFrame = map.getKey().duplicate();
                                        tmp.addFirst(cacheFrame);
                                        System.out.println("PUT MSG ->" + tmp);
                                        tmp.send(backend);
                                    }
                                }
                            } else {
                                ZMsg errorMessage = new ZMsg();
                                //getErrorMessages(errorMessage, message, frontend);
                                errorMessage.add(message.getFirst());
                                errorMessage.add("");
                                errorMessage.add("ERROR MESSAGE");
                                errorMessage.send(frontend);
                            }
                        }
                    }
                }

                if (items.pollin(1)) {        //BACKEND_MESSAGE
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    if (msg == null) {
                        break;
                    }
                    //System.out.println("GOT MSG ->" + message);

                    if (msg.getLast().toString().contains("Heartbleed")) {
                        if (!commutatorMap.containsKey(msg.getFirst())) {
                            ZFrame data = msg.getLast();
                            String[] fields = data.toString().split(DELIMITER);
                            CacheCommutator tmp = new CacheCommutator(
                                    fields[1],
                                    fields[2],
                                    System.currentTimeMillis()
                            );
                            commutatorMap.put(msg.getFirst().duplicate(), tmp);
                            System.out.println("New cache -> " + msg.getFirst() + " " + tmp.getLeftBound() + " " + tmp.getRightBound());
                        } else {
                            commutatorMap.get(msg.getFirst().duplicate()).setTime(System.currentTimeMillis());
                        }
                    } else {
                        System.out.println("NO HEARTBEAT ->" + msg);
                        //msg.pop();
                        msg.send(frontend);
                    }
                }
            }
//            while (!Thread.currentThread().isInterrupted()) {
//                items.poll(1);
//                if(!commutatorMap.isEmpty() && System.currentTimeMillis() - time > EPSILON_TIME ){
//                    for(Iterator<Map.Entry<ZFrame, CacheCommutator>> it = commutatorMap.entrySet().iterator(); it.hasNext(); ){
//                        Map.Entry<ZFrame, CacheCommutator> entry = it.next();
//                        if(Math.abs(entry.getValue().getTime() - time) > EPSILON_TIME * 2){
//                            System.out.println("THIS CACHE WAS DELETED -> " + entry.getKey());
//                            it.remove();
//                        }
//                    }
//                    time = System.currentTimeMillis();
//                }
//                if (items.pollin(0)) {
//                    ZMsg msg = ZMsg.recvMsg(frontend);
//                    if (msg == null) {
//                        break;
//                    }
//                    System.out.println("GOT MSG ->" + msg);
//                    if (commutatorMap.isEmpty()) {
//                        ZMsg errMsg = new ZMsg();
//                        errMsg.add(msg.getFirst());
//                        errMsg.add(EMPTY_FRAME);
//                        errMsg.add("NO CURRENT CACHE");
//                        errMsg.send(frontend);
//                    } else {
//                        String[] data = msg.getLast().toString().split(DELIMITER);
//                        if (data[0].equals(GET_COMMAND)) {
//                            for (Map.Entry<ZFrame, CacheCommutator> map : commutatorMap.entrySet()) {
//                                if (map.getValue().isIntersect(data[1])) {
//                                    ZFrame cacheFrame = map.getKey().duplicate();
//                                    msg.addFirst(cacheFrame);
//                                    msg.send(backend);
//                                }
//                            }
//                        } else {
//                            if (data[0].equals(PUT_COMMAND)) {
//                                for (Map.Entry<ZFrame, CacheCommutator> map : commutatorMap.entrySet()) {
//                                    if (map.getValue().isIntersect(data[1])) {
//                                        ZMsg tmp = msg.duplicate();
//                                        ZFrame cacheFrame = map.getKey().duplicate();
//                                        tmp.addFirst(cacheFrame);
//                                        System.out.println("PUT MSG ->" + tmp);
//                                        tmp.send(backend);
//                                    }
//                                }
//                            } else {
//
//                                ZMsg errMsg = new ZMsg();
//                                errMsg.add(msg.getFirst());
//                                errMsg.add(EMPTY_FRAME);
//                                errMsg.add("ERROR MESSAGE");
//                                errMsg.send(frontend);
//                            }
//                        }
//                    }
//                }
//
//                if (items.pollin(1)) {
//                    ZMsg msg = ZMsg.recvMsg(backend);
//                    if (msg == null) {
//                        break;
//                    }
//                    if (msg.getLast().toString().contains("Heartbleed")) {
//                        if (!commutatorMap.containsKey(msg.getFirst())) {
//                            ZFrame data = msg.getLast();
//                            String[] fields = data.toString().split(DELIMITER);
//                            CacheCommutator tmp = new CacheCommutator(
//                                    fields[1],
//                                    fields[2],
//                                    System.currentTimeMillis()
//                            );
//                            commutatorMap.put(msg.getFirst().duplicate(), tmp);
//                            System.out.println("New cache -> " + msg.getFirst() + " " + tmp.getLeftBound() + " " + tmp.getRightBound());
//                        }else{
//                            commutatorMap.get(msg.getFirst().duplicate()).setTime(System.currentTimeMillis());
//                        }
//                    } else {
//                        System.out.println("NO HEARTHBEAT ->" + msg);
//                        msg.pop();
//                        msg.send(frontend);
//                    }
//                }
//            }
        } catch (ZMQException ex) {
            System.out.println("ERROR_MESSAGE");
            ex.printStackTrace();
//            context.destroySocket(socket);
//            context.destroy();
        }



// С помощью методов Context.socket(<тип сокета>) или ZContext.createSocket(<тип сокета>) создаем сокет
//    В бесконечном цикле читаем из сокета данные и отвечаем или посылаем данные в другие сокеты
//    Посылать можно набор байтов, строку и сообщение Zmsg(набор фреймов)
//    Массив и строка посылаются методами сокета sendXXX, принимаются методами сокетов recvXXX
//    ZMsg отправляется и своими методами ZMsg.send и ZMsg.recvMsg

    }
}
