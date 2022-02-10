import Pacotes.MetaDados;
import Pacotes.PedidoFicheiro;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ServerHandler extends Thread {
    private DatagramSocket datagramSocket;
    private String ipParceiro;
    private List<MetaDados> listaFicheirosFalta;
    // Envia os ficheiros em Falta
    public ServerHandler(DatagramSocket datagramSocket, String ipParceiro, List<MetaDados> lista){
        this.datagramSocket = datagramSocket;
        this.ipParceiro = ipParceiro;
        this.listaFicheirosFalta = lista;
    }

    @Override
    public void run(){
        try{
            for(MetaDados m : this.listaFicheirosFalta){
                PedidoFicheiro p = new PedidoFicheiro((short) 11, m.getFilename());
                DatagramPacket packet = p.serializePDU(InetAddress.getByName(ipParceiro),80);
                datagramSocket.send(packet);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException i){
            i.printStackTrace();
        }
        datagramSocket.close();
    }
}
