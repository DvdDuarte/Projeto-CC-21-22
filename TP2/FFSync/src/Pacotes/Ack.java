package Pacotes;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Ack extends PDU{
    private short ack; // numSequencia do pacote que pretende confirmar

    public Ack(short numSequencia, short ack){
        super(numSequencia, Consts.ACK);
        this.ack = ack;
    }

    public DatagramPacket serializePDU(InetAddress ip, int port){
        DatagramPacket p = super.serializePDU(ip,port);
        byte[] padrao = p.getData();
        byte[] ackBytes = ByteBuffer.allocate(2).putShort(this.ack).array();

        int size_pdu_ack = padrao.length + ackBytes.length;
        byte[] pduAck = new byte[size_pdu_ack];

        System.arraycopy(padrao,0,pduAck,0,padrao.length);
        System.arraycopy(ackBytes,0,pduAck,padrao.length,ackBytes.length);

        return new DatagramPacket(pduAck,1472,ip,port);
    }

    public static Ack deserializePDU(byte[] data){
        PDU p = PDU.deserializePDU(data);

        short numAck = ByteBuffer.wrap(data,3,2).getShort();

        Ack pacoteAck = new Ack(p.getNumSequencia(),numAck);
        pacoteAck.setNumSequencia(p.getNumSequencia());

        return pacoteAck;
    }

    /* Getters */
    public short getAckNumber(){ return ack;}

    /* toString */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("Ack: ").append(this.ack).append("\n");
        return sb.toString();
    }
}