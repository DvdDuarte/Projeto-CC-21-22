import Pacotes.MetaDados;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FFSync {
    private String pasta;
    private String ipParceiro;
    private listMetaDados listMetaDados;

    public static void main (String[] args) throws SocketException {
        FFSync ffsync = new FFSync();
        ffsync.pasta = args[0];
        ffsync.ipParceiro = args[1];
        ffsync.listMetaDados = new listMetaDados(ffsync.pasta);

        System.out.println("You're trying to sync " + ffsync.pasta + " with " + ffsync.ipParceiro);

        // Cria os Metadados que tenho na pasta.
        ffsync.updateFicheiroMetaDados();

        // Criar ServidorManager (listening à espera de pacotes)
        try {
            DatagramSocket socket = new DatagramSocket(80);
            Server s = new Server(socket, ffsync.ipParceiro, ffsync.pasta, ffsync.listMetaDados);
            Thread t = new Thread(s);
        } catch (SocketException e){
            System.out.println("Server already on line.\n");
        }

        while (true){ // pede ficheiros em falta de 6 em 6 segundos e envia metadados
            try{
                // calcula lista dos ficheiros em falta, se len > 0 => criar thread para pedir esses ficheiros
                List<MetaDados> listaFicheirosFalta = ffsync.listMetaDados.ficheirosEmFalta();

                if(listaFicheirosFalta.size() > 0){
                    // thread para pedir os ficheiros
                    Thread tr = new Thread(new ServerHandler(new DatagramSocket(),ffsync.ipParceiro,listaFicheirosFalta));
                    tr.start();
                }

                ffsync.sendMetaDados(); // enviar os Metadados
                Thread.sleep(6000); // esperar 6 segundos

                ffsync.updateFicheiroMetaDados(); // atualizar ficheiro de metadados
                // se ja tiver o ficheiro sincronizado, meter ip a null para nao voltar a pedir

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMetaDados(){
        try {
            DatagramSocket socket = new DatagramSocket();

            List<MetaDados> listaMetaDados = this.listMetaDados.listaMetaDados();

            for(MetaDados m : listaMetaDados){
                DatagramPacket packet = m.serializePDU(InetAddress.getByName(this.ipParceiro),80);
                socket.send(packet);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateFicheiroMetaDados() {
        File f = new File(this.pasta);
        try {
            f.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }

        List<String> listaFicheirosPasta = getAllFiles(f);

        for(String s : listaFicheirosPasta){
            File fp = new File(s);
            listMetaDados.adicionaMetaDados(s,null,fp.lastModified(),fp.length());
        }
        // verifica se tem ficheiros apgados
        listMetaDados.verificaApagados();
    }


    public List<String> getAllFiles(File f){
        List<String> files = new ArrayList<>();

        for(String s : f.list()){
            File fp = new File(f.getPath() + "/" + s);
            if(fp.isDirectory()){
                files.addAll(getAllFiles(fp));
            } else {
                files.add(f.getPath() + "/" + s);
            }
        }
        return files;
    }
}
