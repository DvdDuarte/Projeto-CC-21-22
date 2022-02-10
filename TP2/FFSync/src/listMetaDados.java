import Pacotes.MetaDados;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// guardar os metadados
public class listMetaDados {
    private Map<String,MetaDados> mapMetaDados;  // nomeFicheiro -> Ficheiro de MetaDados
    private String nomePasta;

    public listMetaDados(String nomePasta){
        this.nomePasta = nomePasta;
        this.mapMetaDados = new HashMap<>();
    }


    public void adicionaMetaDados(String filename, String ip, long dataModificacao, long fileSize){
        if(this.mapMetaDados.containsKey(filename)){
            this.mapMetaDados.get(filename).compare(ip,dataModificacao,fileSize);
        } else {
            this.mapMetaDados.put(filename,new MetaDados((short)11,ip,filename,dataModificacao,fileSize)); // FIXME REMOVER numSEQUENCIA
        }
    }


    public MetaDados getMetaDados(String file){ return this.mapMetaDados.get(file);}

    public List<MetaDados> listaMetaDados(){
        List<MetaDados> l = new ArrayList<>();

        for(MetaDados m : this.mapMetaDados.values()){
            l.add(m);
        }
        return l;
    }

    public List<MetaDados> ficheirosEmFalta(){
        List<MetaDados> files = new ArrayList<>();
        for(MetaDados m : this.mapMetaDados.values()){
            if(m.getIp() != null){
                files.add(m);
            }
        }
        return files;
    }

    public void verificaApagados(){
        // verifica ficheiros apagados a meio da execucao
        for(MetaDados m : this.mapMetaDados.values()){
            if(m.getIp() == null){
                File f = new File(this.nomePasta + "/" + m.getFilename());
                if(!(f.exists())) this.mapMetaDados.remove(m.getFilename());
            }
        }
    }

}