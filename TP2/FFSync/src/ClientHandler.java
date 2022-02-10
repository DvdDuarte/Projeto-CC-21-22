import Pacotes.*;
import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private String pasta;
    private listMetaDados listaMetaDados;
    private DatagramPacket inPacket;
    private DatagramSocket socket;

    public ClientHandler (DatagramSocket socket, DatagramPacket inPacket, listMetaDados lista, String pasta) throws SocketException {
        this.socket = socket;
        this.inPacket = inPacket;
        this.pasta = pasta;
        this.listaMetaDados = lista;
    }

    @Override
    public void run() {
        boolean running = true;

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(inPacket.getData()));
        int type = 0;
        try {
            type = dis.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (type == 2){ // transferencia dados
            try {
                short numSeq = dis.readShort();
                int tamanhoNomeFicheiro = dis.readInt();
                String nomeFich = dis.readNBytes(tamanhoNomeFicheiro).toString();
                int tamanhoDados = dis.readInt();
                byte[] arrayDados = dis.readAllBytes();
                File fich = new File(pasta + "/" + nomeFich);
                fich.createNewFile();
                fich.setLastModified(listaMetaDados.getMetaDados(nomeFich).getDataModificacao());
                listaMetaDados.adicionaMetaDados(nomeFich,null,fich.lastModified(),tamanhoDados);
                try{
                    FileOutputStream fos = new FileOutputStream(fich);
                    fos.write(arrayDados);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (type == 4) { // metadados
            int i = 0;
            while(i < 5){
                try{
                    DataInputStream di = new DataInputStream(new ByteArrayInputStream(inPacket.getData()));
                    int opcode = 0;
                    try {
                        opcode = di.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(opcode == 3){ // FIN
                        DatagramPacket d = new FimConexao((short) 11).serializePDU(inPacket.getAddress(),inPacket.getPort());
                        try {
                            socket.send(d);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        running = false; // terminou a tarefa (enviou "end")
                        i = 5;
                    } else if (opcode == 4){
                        MetaDados md = MetaDados.deserializePDU(inPacket.getData(),inPacket.getAddress(),this.pasta);
                        listaMetaDados.adicionaMetaDados(md.getFilename(),md.getIp(),md.getDataModificacao(),md.getFileSize());
                        DatagramPacket dp = new Ack((short) 11,(short)11).serializePDU(inPacket.getAddress(), inPacket.getPort());
                        socket.send(dp);
                    }
                    byte[] buff = new byte[Consts.MAX_PACKET_SIZE];
                    inPacket = new DatagramPacket(buff,buff.length);
                    socket.receive(inPacket);
                } catch (SocketTimeoutException e){
                    // verifica se houve timeout e apenas aceita 5 timeouts (i < 5)
                    i++;
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
           // quando acabar os metadados, enviar "end" para sinalizar que acabou de os enviar
           // quando receber "end" envia outro "end"
        } else if (type == 5){ // Processar Pedido de um Ficheiro
            try {
                dis.readShort(); // numSequencia
                String filename = new String(dis.readUTF()); // fileName
                File f = new File(pasta + "/" + filename);
                if(f.exists()){
                    FileInputStream fis = new FileInputStream(f);
                    while(running){
                        //byte[] data = fis.readNBytes(Consts.MAX_PACKET_SIZE - 6 - filename.getBytes(StandardCharsets.UTF_8).length - 4);
                        byte[] data = fis.readAllBytes();
                        Dados dados = new Dados((short)11,filename,data);
                        socket.send(dados.serializePDU(inPacket.getAddress(),inPacket.getPort()));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}