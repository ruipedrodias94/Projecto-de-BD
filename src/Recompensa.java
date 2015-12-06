import java.io.Serializable;

/**
 * Created by Rui Dias on 30/11/2015.
 */
public class Recompensa implements Serializable{
    private int id_Recompensa;
    private String descricao_Recompensa;
    private int montante_Recompensa;
    private int Projecto_idProjecto;

    public Recompensa(int id_Recompensa, String descricao_Recompensa, int montante_Recompensa, int projecto_idProjecto) {
        this.id_Recompensa = id_Recompensa;
        this.descricao_Recompensa = descricao_Recompensa;
        this.montante_Recompensa = montante_Recompensa;
        this.Projecto_idProjecto = projecto_idProjecto;
    }

    public int getId_Recompensa() {
        return id_Recompensa;
    }

    public void setId_Recompensa(int id_Recompensa) {
        this.id_Recompensa = id_Recompensa;
    }

    public String getDescricao_Recompensa() {
        return descricao_Recompensa;
    }

    public void setDescricao_Recompensa(String descricao_Recompensa) {
        this.descricao_Recompensa = descricao_Recompensa;
    }

    public int getMontante_Recompensa() {
        return montante_Recompensa;
    }

    public void setMontante_Recompensa(int montante_Recompensa) {
        this.montante_Recompensa = montante_Recompensa;
    }

    public int getProjecto_idProjecto() {
        return Projecto_idProjecto;
    }

    public void setProjecto_idProjecto(int projecto_idProjecto) {
        Projecto_idProjecto = projecto_idProjecto;
    }
}
