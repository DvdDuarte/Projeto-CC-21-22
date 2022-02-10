package Pacotes;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class MetaDados extends PDU{
    private String filename;
    private String ip;
    private long dataModificacao;
    private long fileSize;


    public MetaDados(short numSequencia, String filename, long dataModificacao, long fileSize){
        super(numSequencia, Consts.META_DADOS);
        this.filename = filename;
        this.dataModificacao = dataModificacao;
        this.fileSize = fileSize;
        this.ip = null;
    }


    public MetaDados(short numSequencia, String ip, String filename, long dataModificacao, long fileSize){
        super(numSequencia,Consts.META_DADOS);
        this.filename = filename;
        this.dataModificacao = dataModificacao;
        this.fileSize = fileSize;
        this.ip = ip;
    }


    public DatagramPacket serializePDU(InetAddress ip, int port){
        DatagramPacket p = super.serializePDU(ip,port);
        byte[] padrao = p.getData();
        byte[] filenameBytes = this.filename.getBytes(Charset.forName("UTF-8"));


        int offset = 0;

        int tamanho = padrao.length + filenameBytes.length + 8 + 8;
        byte[] pduMetaDados = new byte[tamanho];

        // Copiar PDU Padrao
        System.arraycopy(padrao,0,pduMetaDados,0,padrao.length);
        offset += padrao.length;

        // Copiar Tamanho do Filename
        byte[] tamanhoFilenameBytes = ByteBuffer.allocate(4).putInt(this.filename.length()).array();
        System.arraycopy(tamanhoFilenameBytes,0,pduMetaDados,offset,tamanhoFilenameBytes.length);
        offset += tamanhoFilenameBytes.length;

        // Copiar filename
        System.arraycopy(filenameBytes,0,pduMetaDados,offset,filenameBytes.length);
        offset += filenameBytes.length;

        // Copiar DataModificacao
        byte[] dataMod = longToBytes(dataModificacao);
        System.arraycopy(dataMod,0,pduMetaDados,offset,dataMod.length);
        offset += dataMod.length;

        // Copiar fileSize
        byte[] fs = longToBytes(fileSize);
        System.arraycopy(fs,0,pduMetaDados,offset,fs.length);

        return new DatagramPacket(pduMetaDados,1472, ip,port);
    }

    public static MetaDados deserializePDU(byte[] data,InetAddress ip, String path){
        PDU p = PDU.deserializePDU(data);
        long dataMod, fileSize;
        int offset = 3;
        int sizeFileName = ByteBuffer.wrap(data,offset,4).getInt();
        byte[] filenameBytes = new byte[sizeFileName];
        offset += 4;
        String filename = ByteBuffer.wrap(data,offset,sizeFileName).get(filenameBytes).toString();
        offset += filenameBytes.length;
        long dataM = ByteBuffer.wrap(data,offset,8).getLong();
        offset += 8;
        long fileS = ByteBuffer.wrap(data,offset,8).getLong();

        return new MetaDados(p.getNumSequencia(),ip.getHostAddress(),path + "/" + filename,dataM,fileS);
    }



    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }


    public void compare (String ip, long data, long size){
        if(this.dataModificacao == data){
            if(size > this.fileSize){
                this.ip = ip;
                this.fileSize = size;
            } // se der erro, fazer caso em que  size == this.filesize && ip == null
        }
        if(data > this.dataModificacao){
            this.ip = ip;
            this.fileSize = size;
        }
    }

    // GETTERS

    public String getIp() {return this.ip;}
    public String getFilename() { return this.filename;}

    public long getDataModificacao() { return this.dataModificacao;}
    public long getFileSize() { return this.fileSize;}
}
