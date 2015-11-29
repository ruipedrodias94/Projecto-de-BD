import jdk.internal.util.xml.impl.Input;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

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

    //Obter o id do cliente logado
    public synchronized int getIdCliente(String username) throws SQLException{
        int id = 0;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM proj_bd.cliente WHERE user_Name = '" + username + "';");
            while (resultSet.next()){
                id = resultSet.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }

    //Get o id do projecto
    public synchronized int getIdProjeto(String nomeProjecto) throws SQLException{
        int id = 0;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM proj_bd.projecto WHERE nome_Projecto = '" + nomeProjecto + "';");
            while (resultSet.next()){
                id = resultSet.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }

    //Registar conta
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

    //Login
    public synchronized boolean login(String user_Name, String password) throws SQLException{
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM proj_bd.cliente" +
                    " WHERE user_Name = '"+user_Name +"' AND password = '"+ password +"';");

            //Se obtiver mais que um resultado
            while (resultSet.next()){
                return true;
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }

        //Caso contrario retorna falso
        return false;
    }

    //Listar projectos actuais
    public synchronized ArrayList<String> listarProjectos(int state) throws SQLException{
        ArrayList<String> projectos_Actuais = new ArrayList<>();
        String nome_Projecto, data_Final, estado = "";
        String string_Final = "";
        int id_Projecto = 0;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT nome_Projecto, data_Limite, idProjecto FROM proj_bd.projecto" +
                    " WHERE projecto.estado ="+ state +";");
            while (resultSet.next()){
                nome_Projecto = resultSet.getString(1);
                data_Final = resultSet.getString(2);
                id_Projecto = resultSet.getInt(3);
                if(state == 1) {
                    estado = "ACTIVO";
                } else {
                    estado = "INACTIVO";
                }
                string_Final = "NOME DO PROJECTO: " + nome_Projecto +"\nID DO PROJECTO: "+ String.valueOf(id_Projecto) +"\nDATA LIMITE: " + data_Final +
                        "\nESTADO: " + estado + "\n====================================================";

                projectos_Actuais.add(string_Final);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }

        return projectos_Actuais;
    }

    //Consultar o saldo
    public synchronized int consultarSaldo(int id_Cliente) throws SQLException{
        int saldo_Cliente = 0;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT saldo FROM proj_bd.cliente WHERE idCliente =" + id_Cliente+";");

            while (resultSet.next()){
                saldo_Cliente = resultSet.getInt(1);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return saldo_Cliente;
    }

    //Criar um projecto
    public synchronized void criarProjecto(String nome_Projecto, String desricao_Projecto, String data,
                                           int id_Cliente, int dinheiro_Limite ) throws  SQLException{
        //Saldo = 0
        //Estado = 1 ---> Activo
        //Data limite ---> Ainda nao sei
        //Dinheiro angariado = 0
        //id_Cliente ---> Tambem ainda nao sei como meter
        //A data vai ter que ser do tipo 20151030  ---> 30-10-2015 Passamos como string, e no menu pede-se o dia o mes e o ano, tornando dempois numa string

        //Variaveis de pixota
        int quantidade = 0;
        int montante = 0;
        String descricao = "";
        int id_Projecto = 0;

        Scanner sc = new Scanner(System.in);

        try{
            preparedStatement = connection.prepareStatement("INSERT INTO proj_bd.projecto (nome_Projecto, descricao_Projecto, estado, data_Limite," +
                    " dinheiro_Angariado, dinheiro_Limite, Cliente_idCliente) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);");

            preparedStatement.setString(1,nome_Projecto);
            preparedStatement.setString(2,desricao_Projecto);
            preparedStatement.setInt(3, 1);
            preparedStatement.setDate(4, Date.valueOf(data));
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6,dinheiro_Limite);
            preparedStatement.setInt(7, id_Cliente);

            preparedStatement.executeUpdate();

            System.out.println("PROJECTO CRIADO COM SUCESSO");

        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }

        id_Projecto = getIdProjeto(nome_Projecto);

        System.out.println("QUANTAS RECOMPENSAS DESEJA CRIAR?");
        quantidade = sc.nextInt();
        sc.nextLine();
        while (quantidade > 0){
            System.out.println("DESCRICAO DA RECOMPENSA: ");
            descricao = sc.nextLine();
            System.out.println("MONTANTE A PARTIR DO QUAL O CLIENTE RECEBER A RECOMPENSA: ");
            montante = sc.nextInt();
            sc.nextLine();
            criarRecompensa(descricao, montante, id_Projecto);
            quantidade--;
        }

    }

    //Criar uma recompensa
    public synchronized void criarRecompensa(String descricao, int montante, int id_Projecto) throws SQLException{
        try {

            preparedStatement = connection.prepareStatement(" INSERT INTO proj_bd.recompensa (descricao_Recompensa,montante_Recompensa, Projecto_idProjecto)" +
            "VALUES (?,?,?);");

            preparedStatement.setString(1, descricao);
            preparedStatement.setInt(2,montante);
            preparedStatement.setInt(3, id_Projecto);

            preparedStatement.executeUpdate();

            System.out.println("RECOMPENSA CRIADA COM SUCESSO");

        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    //Listar detalhes do projecto
    public synchronized String listarDetalhes_Projecto(int id_Projecto) throws SQLException{
        String string_Final = "", nome_Projecto = "", descricao_Projecto= "";
        int dinheiro_Angariado = 0, dinheiro_Limite = 0;
        Date data_Limite = null;

        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT nome_Projecto, descricao_Projecto, data_Limite, dinheiro_Angariado, dinheiro_Limite" +
                    " FROM proj_bd.projecto WHERE idProjecto= " + id_Projecto + ";");
            if(resultSet.next()){
                nome_Projecto = resultSet.getString(1);
                descricao_Projecto = resultSet.getString(2);
                data_Limite = resultSet.getDate(3);
                dinheiro_Angariado = resultSet.getInt(4);
                dinheiro_Limite = resultSet.getInt(5);

                string_Final = "DETALHES DO PROJECTO: \n"
                        + "NOME: " + nome_Projecto + "\n"
                        + "DESCRICAO: " + descricao_Projecto + "\n"
                        + "DATA LIMITE: " + String.valueOf(data_Limite) + "\n"
                        + "DINHEIRO ANGARIADO: " + String.valueOf(dinheiro_Angariado) + "\n"
                        + "DINHEIRO NECESSARIO: " + String.valueOf(dinheiro_Limite)
                        + "\n====================================================";

            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }

        return string_Final;
    }

    //Inacabada
    //Fazer doação ao projecto
    public synchronized void fazerDoacao(int id_Projecto, int valor, int id_Cliente) throws SQLException {
        int valor_Cliente, valor_Projecto;

        valor_Cliente = consultarSaldo(id_Cliente);
        if(valor_Cliente > valor){
            //Continua
        }else{
            System.out.println("NAO TEM DINHEIRO SUFICIENTE");
        }

    }

    //Faz update do saldo cliente
    public synchronized void updateSaldoCliente(int id_Cliente, int novoSaldo) throws SQLException{

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" UPDATE proj_bd.cliente SET saldo = "+novoSaldo+" WHERE idCliente = "+id_Cliente+";");


        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
    }

}
