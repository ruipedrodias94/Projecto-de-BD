import java.io.Serializable;
import java.util.Date;

/**
 * Created by Rui Dias on 30/11/2015.
 */
public class Projecto implements Serializable {

    private int id_Projecto;
    private String nome_Projecto;
    private String descricao_Projecto;
    private int estado;
    private Date data_Limite;
    private int dinheiro_Angariado;
    private int dinheiro_Limite;
    private int Cliente_idCliente;

    public Projecto(int id_Projecto,String nome_Projecto, String descricao_Projecto, int estado, Date data_Limite, int dinheiro_Angariado, int dinheiro_Limite, int Cliente_idCliente){
        this.setId_Projecto(id_Projecto);
        this.setNome_Projecto(nome_Projecto);
        this.setDescricao_Projecto(descricao_Projecto);
        this.setEstado(estado);
        this.setData_Limite(data_Limite);
        this.setDinheiro_Angariado(dinheiro_Angariado);
        this.setDinheiro_Limite(dinheiro_Limite);
        this.setCliente_idCliente(Cliente_idCliente);
    }

    public String getNome_Projecto() {
        return nome_Projecto;
    }

    public void setNome_Projecto(String nome_Projecto) {
        this.nome_Projecto = nome_Projecto;
    }

    public String getDescricao_Projecto() {
        return descricao_Projecto;
    }

    public void setDescricao_Projecto(String descricao_Projecto) {
        this.descricao_Projecto = descricao_Projecto;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Date getData_Limite() {
        return data_Limite;
    }

    public void setData_Limite(Date data_Limite) {
        this.data_Limite = data_Limite;
    }

    public int getDinheiro_Angariado() {
        return dinheiro_Angariado;
    }

    public void setDinheiro_Angariado(int dinheiro_Angariado) {
        this.dinheiro_Angariado = dinheiro_Angariado;
    }

    public int getDinheiro_Limite() {
        return dinheiro_Limite;
    }

    public void setDinheiro_Limite(int dinheiro_Limite) {
        this.dinheiro_Limite = dinheiro_Limite;
    }

    public int getCliente_idCliente() {
        return Cliente_idCliente;
    }

    public void setCliente_idCliente(int cliente_idCliente) {
        Cliente_idCliente = cliente_idCliente;
    }

    public int getId_Projecto() {
        return id_Projecto;
    }

    public void setId_Projecto(int id_Projecto) {
        this.id_Projecto = id_Projecto;
    }
}
