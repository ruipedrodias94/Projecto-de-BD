import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        //Esta vai ser a nossa interface!
        DataBase dataBase = new DataBase();

        //Registar Cliente
        dataBase.registarConta("Rui Pedro", "ruipedrodias", "12345", 10000);
    }
}
