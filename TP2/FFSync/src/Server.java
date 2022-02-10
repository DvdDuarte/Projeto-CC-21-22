import java.io.IOException;
import java.net.*;

public class Server implements Runnable { // extends Thread
    private String pasta;
    private String ipParceiro;
    private listMetaDados listMetaDados;
    private DatagramSocket socket;


    private final static int MTU = 1500;

    public Server(DatagramSocket socket, String ipParceiro, String pasta, listMetaDados lista){
        this.socket = socket;
        this.ipParceiro = ipParceiro;
        this.pasta = pasta;
        this.listMetaDados = lista;
    }


    public void run() {
        int port = 80;
        boolean running = true;
        System.out.println("listening on port: " +port);

        while (running) {
            try{
                byte[] inBuffer = new byte[MTU];
                DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                socket.receive(inPacket);// bloqueia ate receber pacote

                /** Incrementar o valor do serverInfo do que estou a responder */
                // processar o pacote
                ClientHandler ch = new ClientHandler(new DatagramSocket(),inPacket,this.listMetaDados,this.pasta);
                Thread t = new Thread(ch);
                t.start();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}