import java.io.Serializable;
import java.util.Date;

/**
 * Created by Rui Dias on 05/12/2015.
 */
public class Mensagem implements Serializable {

    private int id_Mensagem;
    private String assunto_Mensagem;
    private String descricao_Mensagem;
    private Date data_Mensagem;
    private int tipo;
    private int Projecto_idProjecto;
    private int Cliente_idCliente;

    public Mensagem(int id_Mensagem, String assunto_Mensagem, String descricao_Mensagem, Date data_Mensagem, int tipo, int projecto_idProjecto, int cliente_idCliente) {
        this.setId_Mensagem(id_Mensagem);
        this.setAssunto_Mensagem(assunto_Mensagem);
        this.setDescricao_Mensagem(descricao_Mensagem);
        this.setData_Mensagem(data_Mensagem);
        this.setTipo(tipo);
        setProjecto_idProjecto(projecto_idProjecto);
        setCliente_idCliente(cliente_idCliente);
    }


    public int getId_Mensagem() {
        return id_Mensagem;
    }

    public void setId_Mensagem(int id_Mensagem) {
        this.id_Mensagem = id_Mensagem;
    }

    public String getAssunto_Mensagem() {
        return assunto_Mensagem;
    }

    public void setAssunto_Mensagem(String assunto_Mensagem) {
        this.assunto_Mensagem = assunto_Mensagem;
    }

    public String getDescricao_Mensagem() {
        return descricao_Mensagem;
    }

    public void setDescricao_Mensagem(String descricao_Mensagem) {
        this.descricao_Mensagem = descricao_Mensagem;
    }

    public Date getData_Mensagem() {
        return data_Mensagem;
    }

    public void setData_Mensagem(Date data_Mensagem) {
        this.data_Mensagem = data_Mensagem;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getProjecto_idProjecto() {
        return Projecto_idProjecto;
    }

    public void setProjecto_idProjecto(int projecto_idProjecto) {
        Projecto_idProjecto = projecto_idProjecto;
    }

    public int getCliente_idCliente() {
        return Cliente_idCliente;
    }

    public void setCliente_idCliente(int cliente_idCliente) {
        Cliente_idCliente = cliente_idCliente;
    }
}
