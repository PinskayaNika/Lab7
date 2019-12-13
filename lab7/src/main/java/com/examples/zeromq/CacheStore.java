package com.examples.zeromq;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

//- Хранилище части распределенного кэша.
// Открывает сокет DEALER, подключается к центральному прокси.
// После подключения с определнным интервалом времени высылает сообщение NOTIFY в котором сообщает интервал хранимых значений.
// Также принимает из сокета два вида команд — на изменение ячейки кэша и на извлечение ячейки.
public class CacheStore {
    ZMQ.Context context = ZMQ.context(1);

    //Socket to talk to server
    ZMQ.Socket requester = context.socket(SocketType.DEALER);

}
