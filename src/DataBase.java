import java.sql.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


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

    private  Scanner sc = new Scanner(System.in);

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
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?user=root","root", "pass");
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
    public synchronized int registarConta(String nome_Cliente, String user_Name, String password, int saldo) throws SQLException{

        if (login(user_Name, password)){
            System.out.println("JA EXISTE ESSE USERNAME, POR FAVOR ESCOLHA OUTRO");
            return 1;
        }
        try{

            preparedStatement = connection.prepareStatement("INSERT INTO proj_bd.cliente(nome_Cliente, user_Name, password, saldo)" +
                    "VALUES (?,?,?,?);");

            preparedStatement.setString(1, nome_Cliente);
            preparedStatement.setString(2, user_Name);
            preparedStatement.setString(3, password);
            preparedStatement.setInt(4, saldo);

            preparedStatement.executeUpdate();

            System.out.println("QUELIENTE ADICIONADO! PARABÉNS");
            return 0;
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return 0;
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

    //Ver se o projecto ja existe
    public synchronized boolean projectExists(String nome_Projecto) throws SQLException{
        ArrayList<Projecto> projectosAux = getProjectos();
        for (int i = 0; i < projectosAux.size(); i++){
            if(projectosAux.get(i).getNome_Projecto().equals(nome_Projecto)){
                return true;
            }
        }
        return false;
    }

    //GET PROJECTOS CARALHO
    public synchronized ArrayList<Projecto> getProjectos(){
        ArrayList<Projecto> aux = new ArrayList<>();
        Projecto projectoAux;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT idProjecto, nome_Projecto, descricao_Projecto, estado, data_Limite, dinheiro_Angariado, dinheiro_Limite, Cliente_idCliente" +
                    " FROM proj_bd.projecto WHERE estado = 1;");

            while (resultSet.next()){
                projectoAux = new Projecto(resultSet.getInt(1),resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4), resultSet.getDate(5), resultSet.getInt(6),
                        resultSet.getInt(7), resultSet.getInt(8));

                aux.add(projectoAux);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return aux;
    }

    //Listar projectos actuais ou acabados, basta mudar o atributo

    public synchronized ArrayList<String> listarProjectos(int state) throws SQLException{
        ArrayList<String> projectos_Actuais = new ArrayList<>();
        ArrayList<Projecto> projectosAux = getProjectos();
        String proj = "";
        for (int i = 0; i<projectosAux.size(); i++){
            if (projectosAux.get(i).getEstado() == state){
                proj = "ID: " + projectosAux.get(i).getId_Projecto() + "  -----<> " +" Data Limite: "+projectosAux.get(i).getData_Limite().toString()+" Dinheiro Limite: "+projectosAux.get(i).getDinheiro_Limite()+" Dinheiro Angariado: "+projectosAux.get(i).getDinheiro_Angariado();
                projectos_Actuais.add(proj);
            }
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

    //Consultar o saldo projecto
    public synchronized int consultarSaldoProjecto(int id_Projecto) throws SQLException{
        int saldo_Projecto = 0;
        ArrayList<Projecto> aux = getProjectos();
        for (int i = 0; i < aux.size(); i++){
            if(aux.get(i).getId_Projecto() == id_Projecto){
                saldo_Projecto = aux.get(i).getDinheiro_Angariado();
            }
        }
        return saldo_Projecto;
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

        if(projectExists(nome_Projecto)){
            System.out.println("JA EXISTE UM PROJECTO COM ESSE NOME, POR FAVOR ESCOLHA OUTRO!");
            return;
        }
        try{
            preparedStatement = connection.prepareStatement("INSERT INTO proj_bd.projecto (nome_Projecto, descricao_Projecto, estado, data_Limite," +
                    " dinheiro_Angariado, dinheiro_Limite, Cliente_idCliente) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);");

            preparedStatement.setString(1,nome_Projecto);
            preparedStatement.setString(2,desricao_Projecto);
            preparedStatement.setInt(3, 1);
            preparedStatement.setDate(4, java.sql.Date.valueOf(data));
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

        System.out.println("POR FIM UMA RECOMPENSA DEFAULT ----> MONTANTE = 0 \nDESCRICAO DA RECOMPENSA: ");
        descricao = sc.nextLine();
        criarRecompensa(descricao, 0, id_Projecto);

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
        String string_Final = "";
        ArrayList<Projecto> projectosAux = getProjectos();
        for (int i = 0; i < projectosAux.size(); i++){
            if(projectosAux.get(i).getId_Projecto() == id_Projecto){
                string_Final = "DETALHES DO PROJECTO: \n"
                        + "NOME: " + projectosAux.get(i).getNome_Projecto() + "\n"
                        + "DESCRICAO: " + projectosAux.get(i).getDescricao_Projecto() + "\n"
                        + "DATA LIMITE: " + String.valueOf(projectosAux.get(i).getData_Limite()) + "\n"
                        + "DINHEIRO ANGARIADO: " + String.valueOf(projectosAux.get(i).getDinheiro_Angariado()) + "\n"
                        + "DINHEIRO NECESSARIO: " + String.valueOf(projectosAux.get(i).getDinheiro_Limite())
                        + "\n====================================================";            }
        }
        return string_Final;
    }

    //Inacabada
    //Fazer doação ao projecto
    public synchronized void fazerDoacao(int id_Projecto, int valor, int id_Cliente) throws SQLException {
        int valor_Cliente, valor_Projecto;
        ArrayList<Recompensa> recompensas = getRecompensas(id_Projecto);
        int id_Recompensa;
        int id_Voto = 0;

        valor_Cliente = consultarSaldo(id_Cliente);
        valor_Projecto = consultarSaldoProjecto(id_Projecto);

        if(valor_Cliente > valor){
            //Continua
            System.out.println("DE ACORDO COM O VALOR QUE QUER DOAR, ESTAS SAO AS RECOMPENSAS DISPONIVEIS PARA VOTAR. " +
                    "ATENCAO QUE A RECOMPENSA EM QUE VOTAR VAI SER A QUE LHE VAI FICAR ATRIBUIDA");

            //Imprime as recompensas
            for (int i = 0; i<recompensas.size(); i++ ){
                if (recompensas.get(i).getMontante_Recompensa() <= valor) {
                    System.out.println("ID RECOMPENSA: " + String.valueOf(recompensas.get(i).getId_Recompensa())+ " -------<>  " + recompensas.get(i).getDescricao_Recompensa());
                }
            }

            System.out.println("EM QUE RECOMPENSA VAI VOTAR?");
            id_Recompensa = sc.nextInt();
            //Cria o voto
            criarVoto(id_Recompensa, id_Projecto);

            //Recalcula o valor do cliente e faz o update
            valor_Cliente = valor_Cliente - valor;
            updateSaldoCliente(id_Cliente, valor_Cliente);

            //Recalcula o valor do Projecto e faz o update
            valor_Projecto = valor_Projecto + valor;
            updateSaldoProjecto(id_Projecto, valor_Projecto);

            id_Voto = getIdVoto();

            //Cria a doaçao
            criarDoacao(valor, id_Recompensa, id_Voto, id_Cliente, id_Projecto);

        }else{
            System.out.println("NAO TEM DINHEIRO SUFICIENTE");
            return;
        }

        id_Voto++;
    }

    //Faz update do saldo cliente
    public synchronized void updateSaldoCliente(int id_Cliente, int novoSaldo) throws SQLException{

        try {
            preparedStatement = connection.prepareStatement(" UPDATE proj_bd.cliente SET saldo = "+novoSaldo+" WHERE idCliente = "+id_Cliente+";");
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    //Faz update do saldo projecto
    public synchronized void updateSaldoProjecto(int id_Projecto, int novoSaldo) throws SQLException{

        try {
            preparedStatement = connection.prepareStatement(" UPDATE proj_bd.projecto SET dinheiro_Angariado = "+novoSaldo+" WHERE idProjecto = "+id_Projecto+";");
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    //Listar as recompensas de acordo com o valor
    public synchronized ArrayList<Recompensa> getRecompensas(int id_Projecto) throws SQLException{
        ArrayList<Recompensa> recompensasAux = new ArrayList<>();
        Recompensa recompensa;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT idRecompensa, descricao_Recompensa, montante_Recompensa, Projecto_idProjecto" +
                    " FROM proj_bd.recompensa WHERE Projecto_idProjecto= " + id_Projecto + " ;");

            while (resultSet.next()){
                recompensa = new Recompensa(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4));
                recompensasAux.add(recompensa);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return recompensasAux;
    }

    //Criar voto
    public synchronized void criarVoto(int id_Recompensa, int id_Projecto) throws SQLException{
        try {

            preparedStatement = connection.prepareStatement(" INSERT INTO proj_bd.voto (id_Recompensa,Projecto_idProjecto)" +
                    "VALUES (?,?);");
            preparedStatement.setInt(1, id_Recompensa);
            preparedStatement.setInt(2, id_Projecto);

            preparedStatement.executeUpdate();

            System.out.println("VOTO EFECTUADO COM SUCESSO");

        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    //Criar doacao
    public synchronized void criarDoacao(int montante, int id_Recompensa,int id_Voto, int id_Cliente,  int id_Projecto) throws SQLException{
        try {

            preparedStatement = connection.prepareStatement(" INSERT INTO proj_bd.doacao (montante,Recompensa_idRecompensa,Voto_idVoto , Cliente_idCliente,Projecto_idProjecto)" +
                    "VALUES (?,?,?,?,?);");
            preparedStatement.setInt(1, montante);
            preparedStatement.setInt(2, id_Recompensa);
            preparedStatement.setInt(3, id_Voto);
            preparedStatement.setInt(4, id_Cliente);
            preparedStatement.setInt(5, id_Projecto);

            preparedStatement.executeUpdate();

            System.out.println("DOACAO EFECTUADA COM SUCESSO");

        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    //Função para ver o id do voto
    public synchronized int getIdVoto(){
        int id_Voto = 0;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT idVoto" + " FROM proj_bd.voto ;");
            while (resultSet.next()){
                id_Voto += 1;
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return id_Voto;
    }

    //Cancelar projecto
    public synchronized void cancelarProjecto(int id_Projecto) throws SQLException {
        ArrayList<Doacao> doacoes = getDoacoes(id_Projecto);
        int saldo_Cliente = 0;
        try {
            preparedStatement = connection.prepareStatement(" UPDATE proj_bd.projecto SET estado = 0 WHERE idProjecto = "+id_Projecto+";");
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }

        for (int i = 0; i< doacoes.size(); i++){
            saldo_Cliente = consultarSaldo(doacoes.get(i).getCliente_idCliente());
            saldo_Cliente += doacoes.get(i).getMontante();
            updateSaldoCliente(doacoes.get(i).getCliente_idCliente() , saldo_Cliente);
        }

        System.out.println("PROJECTO FOI CANCELADO\nSALDO DO CLIENTE FOI RESTAURADO, OBRIGADO");
    }

    //Get doacoes
    public synchronized ArrayList<Doacao> getDoacoes(int id_Projecto){
        ArrayList<Doacao> doacoesAux = new ArrayList<>();
        Doacao doacaoAux;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT  idDoacao, montante, Recompensa_idRecompensa, Voto_idVoto, Cliente_idCliente, Projecto_idProjecto" +
                    " FROM proj_bd.doacao WHERE Projecto_idProjecto = "+id_Projecto + ";");
            while (resultSet.next()){
                doacaoAux = new Doacao(resultSet.getInt(1),resultSet.getInt(2),resultSet.getInt(3),resultSet.getInt(4),resultSet.getInt(5),resultSet.getInt(6));
                doacoesAux.add(doacaoAux);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return doacoesAux;
    }

    //Finalizar projecto
    //Verificar data, e o dinheiro limite, se foi bem sucedido tudo bem, senao faz o cancelar projecto xD
    public synchronized void finalizarProjectos() throws SQLException {
        ArrayList<Projecto> projectos = getProjectos();
        ArrayList<Integer> naoCumpriram = new ArrayList<>();
        Date data_Actual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.format(data_Actual);
        int dinheiro_Actual, dinheiro_Limite;

        for (int i = 0; i<projectos.size(); i++){
            Date data_Projecto = projectos.get(i).getData_Limite();
            dinheiro_Actual = projectos.get(i).getDinheiro_Angariado();
            dinheiro_Limite = projectos.get(i).getDinheiro_Limite();
            if(data_Projecto.before(data_Actual)){
                if(dinheiro_Actual<dinheiro_Limite){
                    naoCumpriram.add(projectos.get(i).getId_Projecto());
                }
            }
        }

        for (int i = 0; i< naoCumpriram.size(); i++){
            cancelarProjecto(naoCumpriram.get(i));
        }

    }
}
