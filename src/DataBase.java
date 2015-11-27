import java.sql.*;

/**
 * Created by Rui Dias on 27/11/2015.
 */
public class DataBase {


    private static final long serialVersionUID = 1L;

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;

    private int FALSE = 0;
    private int TRUE = 1;


    public DataBase(){
        try {
            ConnectDataBase();
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    public synchronized void ConnectDataBase() throws SQLException{

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("[DATABASE] Nao tem a Oracle JDBC Driver instalada!");
            return;
        }

        System.out.println("[DATABASE] Oracle JDBC driver instalada");

        try{
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?user=root","root", "root");
        }catch (SQLException e){
            System.out.println("Falhou a fazer a connexao a base de dados!");
            System.out.println(e.getLocalizedMessage());
        }

        if (connection != null){
            System.out.println("[DATABASE] Ligada com sucesso!");
        }else{
            System.out.println("[DATABASE] Nao conseguiu ligar!");
        }
    }

    //Funcoes de base de dados

    public synchronized void registarConta(String nome_Cliente, String user_Name, String password, int saldo) throws SQLException{

        try{

            preparedStatement = connection.prepareStatement("INSERT INTO proj_bd.cliente(nome_Cliente, user_Name, password, saldo)" +
                    "VALUES (?,?,?,?);");

            preparedStatement.setString(1, nome_Cliente);
            preparedStatement.setString(2, user_Name);
            preparedStatement.setString(3, password);
            preparedStatement.setInt(4,saldo);

            preparedStatement.executeUpdate();

            System.out.println("QUELIENTE ADICIONADO! PARABÉNS");
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
