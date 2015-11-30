/**
 * Created by Rui Dias on 30/11/2015.
 */
public class Cliente {

    private String nome;
    private String user_Name;
    private String password;
    private int saldo;

    public Cliente(String nome, String user_Name, String password, int saldo){
        this.nome = nome;
        this.user_Name = user_Name;
        this.password = password;
        this.saldo = saldo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }
}
