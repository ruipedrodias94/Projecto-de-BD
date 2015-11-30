/**
 * Created by Rui Dias on 30/11/2015.
 */
public class Doacao {

    private int id_Doacao;
    private int montante;
    private int Recompensa_idRecompensa;
    private int Voto_idVoto;
    private int Cliente_idCliente;
    private int Projecto_idProjecto;

    public Doacao(int id_Doacao, int montante, int recompensa_idRecompensa, int voto_idVoto, int cliente_idCliente, int projecto_idProjecto) {
        this.setId_Doacao(id_Doacao);
        this.setMontante(montante);
        this.setRecompensa_idRecompensa(recompensa_idRecompensa);
        this.setVoto_idVoto(voto_idVoto);
        this.setCliente_idCliente(cliente_idCliente);
        this.setProjecto_idProjecto(projecto_idProjecto);
    }

    public int getId_Doacao() {
        return id_Doacao;
    }

    public void setId_Doacao(int id_Doacao) {
        this.id_Doacao = id_Doacao;
    }

    public int getMontante() {
        return montante;
    }

    public void setMontante(int montante) {
        this.montante = montante;
    }

    public int getRecompensa_idRecompensa() {
        return Recompensa_idRecompensa;
    }

    public void setRecompensa_idRecompensa(int recompensa_idRecompensa) {
        Recompensa_idRecompensa = recompensa_idRecompensa;
    }

    public int getVoto_idVoto() {
        return Voto_idVoto;
    }

    public void setVoto_idVoto(int voto_idVoto) {
        Voto_idVoto = voto_idVoto;
    }

    public int getCliente_idCliente() {
        return Cliente_idCliente;
    }

    public void setCliente_idCliente(int cliente_idCliente) {
        Cliente_idCliente = cliente_idCliente;
    }

    public int getProjecto_idProjecto() {
        return Projecto_idProjecto;
    }

    public void setProjecto_idProjecto(int projecto_idProjecto) {
        Projecto_idProjecto = projecto_idProjecto;
    }
}
