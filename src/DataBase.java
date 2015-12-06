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

    /**
     * Metodo responsavel pela conexão à base de dados. Verifica se temos a driver JDBC instalada e se as credenciais estão
     * correctas
     * @throws SQLException
     */
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

    /**
     * Metodo para meter o autocommit a true
     */
    private void restoreAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Metodo para obter o id do cliente através do seu username. Cada username tem de ser diferente. É unico, logo podemos
     * usa lo como forma de identificação.
     * @param username
     * @return
     * @throws SQLException
     */
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
    public synchronized int getNVotosAlt(int id_voto) throws SQLException{
        int n = 0;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM proj_bd.voto WHERE idVoto = '" + id_voto + "';");
            while (resultSet.next()){
                n = resultSet.getInt(4);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return n;
    }

    /**
     * Metodo para verificar o id de um projecto através do nome do projecto
     * @param nomeProjecto
     * @return
     * @throws SQLException
     */
    //Get o id do projecto
    public synchronized int getIdProjeto(String nomeProjecto) throws SQLException{
        int id = 0;
        ArrayList<Projecto> projectosAux = getProjectos();
        for (int i = 0; i < projectosAux.size(); i++){
            if(projectosAux.get(i).getNome_Projecto().equals(nomeProjecto)){
                id = projectosAux.get(i).getId_Projecto();
            }
        }
        return id;
    }

    public synchronized int getIdRecompensa(int IdProjecto,String descrRecompensa) throws SQLException{
        int id = 0;
        try{
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM proj_bd.recompensa WHERE Projecto_idProjecto = '" + IdProjecto + "' AND descricao_Recompensa='"+descrRecompensa+"';");
            while (resultSet.next()){
                id = resultSet.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Metodo para registar um cliente
     * @param nome_Cliente
     * @param user_Name
     * @param password
     * @param saldo
     * @return
     * @throws SQLException
     */
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

            System.out.println("CLIENTE ADICIONADO! PARABÉNS");
            return 0;
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Metodo que verifica se uma conta já está registada através do username e da password
     * @param user_Name
     * @param password
     * @return
     * @throws SQLException
     */
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

    /**
     * Metodo que verifica se um dado projecto ja existe. Nós tratamos cada projecto pelo nome, ou seja, todos os projectos
     * tem de ter nomes diferentes
     * @param nome_Projecto
     * @return
     * @throws SQLException
     */
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

    /**
     * Metodo que retorna um arraylist dos projectos todos
     * @return
     */
    public synchronized ArrayList<Projecto> getProjectos(){
        ArrayList<Projecto> aux = new ArrayList<>();
        Projecto projectoAux;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT idProjecto, nome_Projecto, descricao_Projecto, estado, data_Limite, dinheiro_Angariado, dinheiro_Limite, Cliente_idCliente" +
                    " FROM proj_bd.projecto;");

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

    /**
     * Metodo que retornar um arraylist de projectos de cada utilizador
     * @param userId
     * @return
     */
    public synchronized ArrayList<Projecto> getProjectosIDUser(int userId){
        ArrayList<Projecto> projectosAux = getProjectos();
        ArrayList<Projecto> projectosUser = new ArrayList<>();
        for (int i = 0; i < projectosAux.size(); i++){
            if(projectosAux.get(i).getCliente_idCliente() == userId){
                projectosUser.add(projectosAux.get(i));
            }
        }
        return projectosUser;
    }

    /**
     * Metodo que lista um arraylist de strings que lista os projectos de acordo com o seu estado. state = 0, projecto acabado
     * ou cancelado, state = 1, projecto activo
     * @param state
     * @return
     * @throws SQLException
     */
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

    /**
     * Metodo que retorna um inteiro que é o saldo do cliente.
     * @param id_Cliente
     * @return
     * @throws SQLException
     */
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

    /**
     * Metodo que retorna um inteiro que é o saldo do projecto
     * @param id_Projecto
     * @return
     * @throws SQLException
     */
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

    /**
     * Metodo que cria um projecto. Aquando da criação do projecto, damos a possibilidade ao cliente de criar também as
     * suas recompensas. Cada projecto tem que ter obrigatoriamente uma recompensa. Neste caso para o valor doado ser igual a 0.
     * @param nome_Projecto
     * @param desricao_Projecto
     * @param data
     * @param id_Cliente
     * @param dinheiro_Limite
     * @throws SQLException
     */
    //Criar um projecto
    public synchronized String criarProjecto(String nome_Projecto, String desricao_Projecto, String data,
                                             int id_Cliente, int dinheiro_Limite,ArrayList<Recompensa_proj> ARP ) throws  SQLException{
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
        String r = "PROJECTO CRIADO COM SUCESSO";
        if(projectExists(nome_Projecto)){
            System.out.println("JA EXISTE UM PROJECTO COM ESSE NOME, POR FAVOR ESCOLHA OUTRO!");
        }
        try{
            preparedStatement = connection.prepareStatement("INSERT INTO proj_bd.projecto (nome_Projecto, descricao_Projecto, estado, data_Limite," +
                    " dinheiro_Angariado, dinheiro_Limite, Cliente_idCliente) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);");

            preparedStatement.setString(1, nome_Projecto);
            preparedStatement.setString(2, desricao_Projecto);
            preparedStatement.setInt(3, 1);
            preparedStatement.setDate(4, java.sql.Date.valueOf(data));
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, dinheiro_Limite);
            preparedStatement.setInt(7, id_Cliente);

            preparedStatement.executeUpdate();



        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            return "ERROR CREATING PROJECT";
        }

        id_Projecto = getIdProjeto(nome_Projecto);
        System.out.println("ID PROJECT:"+id_Projecto);


        for(int i=0;i<ARP.size();i++){
            System.out.println("DESCRICAO DA RECOMPENSA: ");
            descricao = ARP.get(i).description;
            System.out.println("MONTANTE A PARTIR DO QUAL O CLIENTE RECEBER A RECOMPENSA: ");
            montante = ARP.get(i).montante;
            criarRecompensa(descricao, montante, id_Projecto);
            for(int j=0;j<ARP.get(i).alt.size();j++){
                System.out.println(ARP.get(i).alt.get(j).getTipoAlt());
                criarVoto(getIdRecompensa(id_Projecto,ARP.get(i).description),id_Projecto,ARP.get(i).alt.get(j).getTipoAlt());
            }

        }

        // System.out.println("POR FIM UMA RECOMPENSA DEFAULT ----> MONTANTE = 0 \nDESCRICAO DA RECOMPENSA: ");

        //criarRecompensa(descricao, 0, id_Projecto);
        return r;

    }

    /**
     * Metodo que cria recompensas. É chamado na criação de um projecto, visto que optamos por cada projecto ter pelo menos
     * uma recompensa.
     * @param descricao
     * @param montante
     * @param id_Projecto
     * @throws SQLException
     */
    //Criar uma recompensa
    public synchronized int criarRecompensa(String descricao, int montante, int id_Projecto) throws SQLException{
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
            return 1;
        }
        return 0;
    }

    /**
     * Metodo que apaga recompensas atraves do seu id
     * Antes deste metodo ser chamado devemos recorrer ao uso do metodo getProjectosUser() para ele ver qual os projectos que lhe pertencem
     * E depois disso chamar o metodo getRecompensas e usar o id do projecto escolhido anteriormente
     * @param id_Recompensa
     */
    public synchronized int deleteRecompensa(int id_Recompensa){

        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM proj_bd.recompensa WHERE idRecompensa = " + id_Recompensa + ";");
            connection.commit();
        }catch (SQLException e){

            System.out.println(e.getLocalizedMessage());
            try{
                connection.rollback();
            }catch (SQLException e1){
                //Ignore
            }
            return 1;
        }
        return 0;
    }

    /**
     * Metodo para listar os detalhes de um projecto. Nada de especial a acrescentar
     * @param id_Projecto
     * @return
     * @throws SQLException
     */
    //Listar detalhes do projecto
    public synchronized ArrayList<Projecto>  listarDetalhes_Projecto(int id_Projecto) throws SQLException{
        String string_Final = "";
        ArrayList<Projecto> projectosAux = getProjectos();

        return projectosAux;
    }


    /**
     * Metodo que faz uma doação. De acordo com o dinheiro que doa, o cliente pode criar um voto, é feita a doação
     * e é feito também um update no saldo do cliente e no saldo do projecto
     * @param id_Projecto
     * @param valor
     * @param id_Cliente
     * @throws SQLException
     */
    //Inacabada
    //Fazer doação ao projecto
    public synchronized int fazerDoacao(int id_Projecto, int valor, int id_Cliente,int id_Recompensa,int id_voto) throws SQLException {
        int valor_Cliente, valor_Projecto;
        ArrayList<Recompensa> recompensas = getRecompensas();



        valor_Cliente = consultarSaldo(id_Cliente);
        valor_Projecto = consultarSaldoProjecto(id_Projecto);

        try {
            connection.setAutoCommit(false);
            if (valor_Cliente > valor) {
                //Continua
                System.out.println("DE ACORDO COM O VALOR QUE QUER DOAR, ESTAS SAO AS RECOMPENSAS DISPONIVEIS PARA VOTAR. " +
                        "ATENCAO QUE A RECOMPENSA EM QUE VOTAR VAI SER A QUE LHE VAI FICAR ATRIBUIDA");

                criarDoacao(valor, id_Recompensa, id_voto, id_Cliente, id_Projecto);

                //Cria o voto
                //criarVoto(id_Recompensa, id_Projecto);
                VotoAlternativa(id_voto);

                //Recalcula o valor do cliente e faz o update
                valor_Cliente = valor_Cliente - valor;
                updateSaldoCliente(id_Cliente, valor_Cliente);

                //Recalcula o valor do Projecto e faz o update
                valor_Projecto = valor_Projecto + valor;
                updateSaldoProjecto(id_Projecto, valor_Projecto);
                connection.commit();

                //Cria a doaçaoreturn 0;

            } else {
                System.out.println("NAO TEM DINHEIRO SUFICIENTE");
                return 1;
            }

        }catch (SQLException e){
            connection.rollback();
            return 1;
        }
        return 0;
    }

    public synchronized void VotoAlternativa(int id_Voto) throws SQLException{

        int n_votos = getNVotosAlt(id_Voto);
        n_votos++;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(" UPDATE proj_bd.voto SET num_Votos = "+n_votos+" WHERE idVoto = "+id_Voto+";");
            preparedStatement.executeUpdate();

            connection.commit();
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            try{
                connection.rollback();
                connection.setAutoCommit(true);
            }catch (SQLException e1){
                System.out.println(e1.getLocalizedMessage());
            }
        }
        restoreAutoCommit();
    }

    /**
     * Metodo que faz o update do saldo do cliente aquando é feita ou retirada uma doação
     * @param id_Cliente
     * @param novoSaldo
     * @throws SQLException
     */
    //Faz update do saldo cliente
    public synchronized void updateSaldoCliente(int id_Cliente, int novoSaldo) throws SQLException{

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(" UPDATE proj_bd.cliente SET saldo = "+novoSaldo+" WHERE idCliente = "+id_Cliente+";");
            preparedStatement.executeUpdate();

            connection.commit();
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            try{
                connection.rollback();
                connection.setAutoCommit(true);
            }catch (SQLException e1){
                System.out.println(e1.getLocalizedMessage());
            }
        }
        restoreAutoCommit();
    }

    /**
     * Metodo que faz o update do saldo do projecto aquando é feita ou retirada uma doação
     * @param id_Projecto
     * @param novoSaldo
     * @throws SQLException
     */
    //Faz update do saldo projecto
    public synchronized void updateSaldoProjecto(int id_Projecto, int novoSaldo) throws SQLException{

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(" UPDATE proj_bd.projecto SET dinheiro_Angariado = "+novoSaldo+" WHERE idProjecto = "+id_Projecto+";");
            preparedStatement.executeUpdate();
            connection.commit();

        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            try {
                connection.rollback();
            }catch (SQLException e1) {
                System.out.println(e1.getLocalizedMessage());
            }
        }
    }


    public synchronized ArrayList<Recompensa> getRecompensasIDProj(int IDProjecto) throws SQLException {
        ArrayList<Recompensa> recompensasAux = new ArrayList<>();
        Recompensa recompensa;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT idRecompensa ,descricao_Recompensa, montante_Recompensa, Projecto_idProjecto" +
                    " FROM proj_bd.recompensa WHERE Projecto_idProjecto='"+IDProjecto+"';");

            while (resultSet.next()){
                recompensa = new Recompensa(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4));
                recompensasAux.add(recompensa);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return recompensasAux;

    }

    public synchronized ArrayList<Recompensa> getRecompensasIDCliente(int IDCliente) throws SQLException {
        ArrayList<Doacao> DoacoesAux = new ArrayList<>();
        ArrayList<Recompensa> recompensasAux = new ArrayList<>();
        Recompensa recompensa;
        Doacao d;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT idDoacao ,montante, Recompensa_idRecompensa, Voto_idVoto,Cliente_idCliente, Projecto_idProjecto" +
                    " FROM proj_bd.doacao WHERE Cliente_idCliente='"+IDCliente+"';");

            while (resultSet.next()){
                d = new Doacao(resultSet.getInt(1), resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4),resultSet.getInt(5),resultSet.getInt(6));
                DoacoesAux.add(d);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }

        for(int i =0;i<DoacoesAux.size();i++){
            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(" SELECT idRecompensa ,descricao_Recompensa, montante_Recompensa, Projecto_idProjecto" +
                        " FROM proj_bd.recompensa WHERE idRecompensa='"+DoacoesAux.get(i).getRecompensa_idRecompensa()+"';");

                while (resultSet.next()){
                    recompensa = new Recompensa(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4));
                    recompensasAux.add(recompensa);
                }
            }catch (SQLException e){
                System.out.println(e.getLocalizedMessage());
            }
        }
        return recompensasAux;

    }

    /**
     * Metodo que retorna um arraylist das recompensas de cada utilizador
     * Atenção que depois esta parte vai ser feita no menu. Listar as recompensas de utilizador, ou listar as recompensas
     * de cada projecto
     * @return
     * @throws SQLException
     */
    public synchronized ArrayList<Recompensa> getRecompensas() throws SQLException{
        ArrayList<Recompensa> recompensasAux = new ArrayList<>();
        Recompensa recompensa;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT idRecompensa, descricao_Recompensa, montante_Recompensa, Projecto_idProjecto" +
                    " FROM proj_bd.recompensa");

            while (resultSet.next()){
                recompensa = new Recompensa(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4));
                recompensasAux.add(recompensa);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return recompensasAux;
    }

    /**
     * Metodo que cria um voto baseado na recompensa e no id do projecto
     * @param id_Recompensa
     * @param id_Projecto
     * @throws SQLException
     */
    //Criar voto
    public synchronized void criarVoto(int id_Recompensa, int id_Projecto, String descricao) throws SQLException{
        try {

            preparedStatement = connection.prepareStatement(" INSERT INTO proj_bd.voto (id_Recompensa,Projecto_idProjecto,num_Votos,descricao)" +
                    "VALUES (?,?,?,'"+descricao+"');");
            preparedStatement.setInt(1, id_Recompensa);
            preparedStatement.setInt(2, id_Projecto);
            preparedStatement.setInt(3, 0);


            preparedStatement.executeUpdate();

            System.out.println("VOTO EFECTUADO COM SUCESSO");

        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    //Criar doacao
    //Exemplo 1
    public synchronized void criarDoacao(int montante, int id_Recompensa,int id_Voto, int id_Cliente,  int id_Projecto) throws SQLException{
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(" INSERT INTO proj_bd.doacao (montante,Recompensa_idRecompensa,Voto_idVoto , Cliente_idCliente,Projecto_idProjecto)" +
                    "VALUES (?,?,?,?,?);");
            preparedStatement.setInt(1, montante);
            preparedStatement.setInt(2, id_Recompensa);
            preparedStatement.setInt(3, id_Voto);
            preparedStatement.setInt(4, id_Cliente);
            preparedStatement.setInt(5, id_Projecto);

            preparedStatement.executeUpdate();
            boolean ok = true;
            if(ok){
                connection.commit();
                connection.setAutoCommit(true);
                System.out.println("DOACAO EFECTUADA COM SUCESSO");
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            try{
                connection.rollback();
            }catch (SQLException e1){
                System.out.println(e1.getLocalizedMessage());
            }
        }
    }

    /**
     * Metodo simples que apenas confirma se queremos continuar a transação
     * @return
     */
    public boolean askUserTransaction(){
        System.out.println("QUER FINALIZAR ESTA TRANSAÇÃO?  -----<> S/N");
        String decisao = sc.nextLine();
        if(decisao.matches("S")){
            return true;
        }
        return false;
    }

    //Função para ver o id do voto

    /**
     * Metodo para retornar o id do voto
     * @return
     */
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
    /**
     * Metodo para cancelar um projecto atravez do seu id. Muda o estado do projecto para 0 (acabado) e retorna o dinheiro
     * a quem doou.
     * @param id_Projecto
     * @throws SQLException
     */
    public synchronized int cancelarProjecto(int id_Projecto) throws SQLException {
        ArrayList<Doacao> doacoes = getDoacoes(id_Projecto);
        int saldo_Cliente = 0;
        try {
            preparedStatement = connection.prepareStatement(" UPDATE proj_bd.projecto SET estado = 0 WHERE idProjecto = "+id_Projecto+";");
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            return 1;
        }

        System.out.println("TAMNHO: "+doacoes.size());
        for (int i = 0; i<doacoes.size(); i++){
            saldo_Cliente = consultarSaldo(doacoes.get(i).getCliente_idCliente());
            saldo_Cliente += doacoes.get(i).getMontante();
            updateSaldoCliente(doacoes.get(i).getCliente_idCliente() , saldo_Cliente);
        }

        System.out.println("PROJECTO FOI CANCELADO\nSALDO DO CLIENTE FOI RESTAURADO, OBRIGADO");
        return 0;
    }

    /**
     * Metodo que retorna um arraylist das doações de um certo projecto
     * @param id_Projecto
     * @return
     */
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

    //Get doacoes
    public synchronized ArrayList<Voto> getAltIdRecompensa(int id_Recompensa){
        ArrayList<Voto> altAux = new ArrayList<>();
        Voto AlterAux;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(" SELECT  idVoto, descricao" +
                    " FROM proj_bd.Voto WHERE id_Recompensa = "+id_Recompensa + ";");
            while (resultSet.next()){
                AlterAux = new Voto(resultSet.getInt(1),resultSet.getString(2));
                altAux.add(AlterAux);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }
        return altAux;
    }

    /**
     * Metodo para finalizar um projecto
     * Verifica a data e o dinheiro limite, sempre que a data é ultrapassada e o dinheiro limite não satisfaz os requisitos
     * o dinheiro é restituído aos clientes que doaram para esse projecto
     * @throws SQLException
     */
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

    /**
     * Metodo para enviar mensagens para um projecto ou para um cliente
     * 1 - Pergunta
     * 2 - Resposta
     * @param id_Cliente
     * @param id_Projecto
     * @param assunto
     * @param descricao
     */
    public synchronized int sendMessage(int id_Cliente, int id_Projecto, String assunto, String descricao, int tipo){
        Date data = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dataString = df.format(data);
        try{
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO proj_bd.mensagem (assunto_Mensagem, conteudo_Mensagem, data_Mensagem, tipo_Mensagem, Projecto_idProjecto, Cliente_idCliente) " +
                    "VALUES ('" + assunto + "','"+ descricao +"','"+ java.sql.Date.valueOf(dataString) +"', "+ tipo +", "+ id_Projecto + "," + id_Cliente +");");
            connection.commit();
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
            try{
                connection.rollback();
                return 0;
            }catch (SQLException e1){
                //Ignore
            }
        }
        return 1;
    }

    /**
     * Metodo para termos todas as mensagens, o resto e feito no menu
     * @return
     */
    public synchronized ArrayList<Mensagem> getMessages(int id_User){
        ArrayList<Mensagem> mensagens = new ArrayList<>();
        Mensagem mensagemAux;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT idMensagem, assunto_Mensagem, conteudo_Mensagem, data_Mensagem, tipo_Mensagem, mensagem.Projecto_idProjecto, mensagem.Cliente_idCliente" +
                    " FROM proj_bd.mensagem, proj_bd.cliente, proj_bd.projecto WHERE cliente.idCliente = mensagem.Cliente_idCliente \n" +
                    "AND projecto.idProjecto = mensagem.Projecto_idProjecto AND cliente.idCliente = "+ id_User + ";");
            while (resultSet.next()){
                mensagemAux = new Mensagem(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getDate(4), resultSet.getInt(5),resultSet.getInt(6), resultSet.getInt(7));
                mensagens.add(mensagemAux);
            }
        }catch (SQLException e){
            System.out.println(e.getLocalizedMessage());
        }

        return mensagens;
    }


}
