package Pacotes;

public class Consts {

    public static final int MAX_PACKET_SIZE = 1472; //1500 - (20+8)

    public static final byte ACK = 1;
    public static final byte TRANSFERENCIA_DADOS = 2;
    public static final byte FIM_CONEXAO= 3;
    public static final byte META_DADOS= 4;
    public static final byte PEDIDO_FICHEIRO = 5;

}
