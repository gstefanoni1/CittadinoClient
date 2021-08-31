import client.ClientHandler;

public class MainApp {
    public static void main(String[] args) throws Exception {
        if(args.length > 0) ClientHandler.getInstance().setIpAddress(args[0]);
        GUIStarter.exec(args);
    }
}
