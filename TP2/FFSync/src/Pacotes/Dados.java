package Pacotes;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Dados extends PDU{
    public static int SIZE_DATA_PACKET = 1500;
    private String filename;
    private byte[] dados;


    public Dados(short numSequencia, String filename, byte[] dados) {
        super(numSequencia, Consts.TRANSFERENCIA_DADOS);
        this.filename = filename;
        this.dados = dados;
    }


    /** Serialize: PDUPadrao + filename_size + filename + data_size + data
     *              (17)          (4)          (max: 45)    (4)       (1472 - (17 + 4 + 45 + 4))  BYTES
     * 1472 = 1500 - 20 - 8 (overhead IP e do UDP) */

    public DatagramPacket serializePDU(InetAddress ip, int port) {

        DatagramPacket p = super.serializePDU(ip,port);
        byte[] padrao = p.getData();
        byte[] filenameBytes = this.filename.getBytes(Charset.forName("UTF-8"));


        int offset = 0;

        int tamanho = padrao.length + filenameBytes.length + dados.length;
        byte[] pduDados = new byte[tamanho];

        // Copiar PDU Padrao
        System.arraycopy(padrao,0,pduDados,0,padrao.length);
        offset += padrao.length;

        // Copiar Tamanho do Filename
        byte[] tamanhoFilenameBytes = ByteBuffer.allocate(4).putInt(this.filename.length()).array();
        System.arraycopy(tamanhoFilenameBytes,0,pduDados,offset,tamanhoFilenameBytes.length);
        offset += tamanhoFilenameBytes.length;

        // Copiar Filename
        System.arraycopy(filenameBytes,0,pduDados,offset,filenameBytes.length);
        offset += filenameBytes.length;

        // Copiar Tamanho dos Dados
        byte[] tamanhoDadosBytes = ByteBuffer.allocate(4).putInt(this.dados.length).array();
        System.arraycopy(tamanhoDadosBytes,0,pduDados,offset,tamanhoDadosBytes.length);
        offset += tamanhoDadosBytes.length;

        // Copiar Data
        System.arraycopy(this.dados,0,pduDados,offset,this.dados.length);

        return new DatagramPacket(pduDados,1472, ip,port);
    }

    public static short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /* Deserialize */
    public static Dados deserialize (byte[] data){
        PDU pdu = PDU.deserializePDU(data);
        byte[] numSequencia = new byte[2];
        numSequencia[0] = data[1];
        numSequencia[1] = data[2];
        short nSeq = bytesToShort(numSequencia);

        int sizeFileName = ByteBuffer.wrap(data,3,4).getInt();
        int offset = 7;
        byte[] encodedFileName = Arrays.copyOfRange(data,7,7+sizeFileName);
        String filename = new String(encodedFileName, Charset.forName("UTF-8"));
        offset += sizeFileName;
        offset += 4; // saltar o tamanho dos dados
        byte[] dados = Arrays.copyOfRange(data,offset,data.length);
        return new Dados(nSeq,filename,dados);
    }

    /* Getters */
    public byte[] getDados(){ return dados;}
    public String getFilename() {return this.filename;}

    /* toString */

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("Filename: ").append(this.filename).append("\nDados: ").append(this.dados.toString()).append("\n");
        return sb.toString();
    }

}
