package Pacotes;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class PDU {
    public static int SIZE_PDU_BASE = 3;
    private byte tipo;
    private short numSequencia; // 2 bytes

    public PDU(short numSequencia, byte tipo){
        this.numSequencia = numSequencia;
        this.tipo = tipo;
    }

    public PDU(PDU pacote, byte tipo){
        this.numSequencia = pacote.getNumSequencia();
        this.tipo = tipo;
    }

    /* Gerar um Pacote PDU (Array de Bytes) */
    public DatagramPacket serializePDU(InetAddress ip, int port){
        byte tipo = this.getTipo();
        byte[] seqByte = ByteBuffer.allocate(2).putShort(this.getNumSequencia()).array();
        byte[] pdu = new byte[3];

        pdu[0] = tipo;
        for(int i = 0,pos = 1; i < seqByte.length; i++,pos++){
            pdu[pos] = seqByte[i];
        }
        
        return new DatagramPacket(pdu,1472,ip,port);
    }
    
    public static PDU deserializePDU(byte[] data){
        byte tipo = data[0];
        short numSequencia = ByteBuffer.wrap(data,1,2).getShort();

        PDU p = new PDU(numSequencia,tipo);
        return p;
    }
    
    
    /* Getters */

    public byte getTipo(){return tipo;}

    public short getNumSequencia(){return numSequencia;}

    /* Setters */

    public void setTipo(byte tipo){ this.tipo = tipo;}

    public void setNumSequencia(short numSequencia){ this.numSequencia = numSequencia;}


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo: ").append(tipo).append("\n");
        sb.append("numSequencia: ").append(numSequencia).append("\n");
        return sb.toString();
    }

}
