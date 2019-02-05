package lesson_4.server.core;

public class ChatServer {
    public void start(int port) {
        System.out.println("Подключен сервер по порту: " + port);
    }

    public void stop() {
        System.out.println("Остановлен сервер!");
    }
}
