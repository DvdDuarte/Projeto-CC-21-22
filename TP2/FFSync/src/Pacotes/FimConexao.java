package Pacotes;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class FimConexao extends PDU {
    // fim de conexao

    public FimConexao(short numSequencia){
        super(numSequencia,Consts.FIM_CONEXAO);
    }

    public FimConexao(PDU p){
        super(p,Consts.FIM_CONEXAO);
    }

    public DatagramPacket serializePDU(InetAddress ip, int port) { return super.serializePDU(ip,port);}

    public static FimConexao deserializePDU(byte[] data){
        return new FimConexao(PDU.deserializePDU(data));
    }
}
