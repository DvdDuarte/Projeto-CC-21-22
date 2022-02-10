package Pacotes;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.Charset;

public class PedidoFicheiro extends PDU {
    private String filename;


    public PedidoFicheiro(short numSequencia, String filename){
        super(numSequencia,Consts.PEDIDO_FICHEIRO);
        this.filename = filename;
    }

    public DatagramPacket serializePDU(InetAddress ip, int port){
        DatagramPacket p = super.serializePDU(ip,port);
        byte[] padrao = p.getData();
        byte[] filenameBytes = this.filename.getBytes(Charset.forName("UTF-8"));
        int offset = 0;
        int tamanho= padrao.length + filenameBytes.length;
        byte[] pduPedidoFicheiro = new byte[tamanho];


        // Copiar PDU Padrao
        System.arraycopy(padrao,0,pduPedidoFicheiro,0,padrao.length);
        offset += padrao.length;

        // Copiar filename
        System.arraycopy(filenameBytes,0,pduPedidoFicheiro,offset, filenameBytes.length);

        return new DatagramPacket(pduPedidoFicheiro,1472,ip,port);
    }
}
