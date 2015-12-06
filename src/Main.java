import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws SQLException, ParseException {

        //Variaveis
        boolean login = false;
        int id_Cliente = 0;
        ArrayList<String> projectos = new ArrayList<>();
        int saldo = 0;
        String detalhes = "";
        int numero_thread = 0;

        //TODO Carregar configuracoes de ficheiro
        int port = 6000;
        try{
            //Esta vai ser a nossa interface!
            DataBase dataBase = new DataBase();
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Escuta no porto 6000");
            //Fica a escuta e cria uma nova thread para cada cliente que se ligue
            while(true) {
                Socket s = ss.accept();
                System.out.println("ServerSocket Created: " + ss);
                new Connection(s,numero_thread,dataBase);
                numero_thread++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Registar Cliente
        //dataBase.registarConta("Jorjao", "jorgearauj", "12345", 10000);

        //Login
        //login = dataBase.login("ruipedrodias", "12345");

        //Get id_Cliente
        //id_Cliente = dataBase.getIdCliente("jorgearauj");
        //System.out.println(id_Cliente);

        //Listar os projectos, temos de ter uma variavel para o estado  activo --- 1 inactivo -- 0;
        //projectos = dataBase.listarProjectos(0);
        /*for (int i = 0; i< projectos.size();i++){
            System.out.println(projectos.get(i));

        }*/

        //Consultar saldo
        //saldo = dataBase.consultarSaldoProjecto(5);
        //System.out.print(saldo);

        //Criar projecto
        //dataBase.criarProjecto("Segundo","Isto é um segundo teste", "2015-11-27", id_Cliente, 10000);

        /*String a = "2015-11-27";
        String b = "2015-11-28";

        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date date1 = format.parse(a);
        Date date2 = format.parse(b);

        if (date2.before(date1)){
            System.out.println("A data dois e mais velha que a um");
        }*/

        //detalhes = dataBase.listarDetalhes_Projecto(4);
        //System.out.println(detalhes);

        //Fazer doacao
        //dataBase.fazerDoacao(5, 10, id_Cliente);

        //Cancelar projecto
        //dataBase.cancelarProjecto(4);

        //Finalizar projectos
        //dataBase.finalizarProjectos();
    }
}
 class Connection extends Thread
 {
     int thread_number;
     InputStream is;
     ObjectInputStream ois;
     Socket ClientSocket;
     DataBase bd;
     Shared_Clients sc = new Shared_Clients();

     public Connection (Socket aClientSocket, int numero, DataBase BD) throws IOException {
         bd = BD;
         thread_number = numero;
         ClientSocket = aClientSocket;
         sc.addClient(ClientSocket);
         this.start();
     }

     public void run()
     {
         try {
             while(true) {
                 //Aqui que se vai fazer o tratamento dos pedidos
                 ois = new ObjectInputStream(ClientSocket.getInputStream());
                 //Chegada de mensagem ---> Tratamento

                 Pedido pedido= (Pedido) ois.readObject();

                if(pedido.type.equals("LOGIN"))
                {
                    if(bd.login(pedido.username,pedido.password)== true)
                    {
                        Resposta respostaLog = new Resposta("SUCCESS LOGIN");
                        sc.send_clients(respostaLog,thread_number);
                    }
                    else{
                        Resposta respostaLog = new Resposta("INSUCCESS LOGIN");
                        sc.send_clients(respostaLog,thread_number);
                    }
                }
                 else if(pedido.type.equals("REGISTRY"))
                 {
                     int saldo_inicial = 100;
                     if(bd.registarConta(pedido.name,pedido.username,pedido.password,saldo_inicial)==0)
                     {
                         Resposta respostaReg = new Resposta("REGISTRY SUCCESS");
                         sc.send_clients(respostaReg,thread_number);
                     }
                     else if(bd.registarConta(pedido.name,pedido.username,pedido.password,saldo_inicial)==1)
                     {
                         Resposta respostaReg = new Resposta("REGISTRY INSUCCESS");
                         sc.send_clients(respostaReg,thread_number);

                     }
                 }
                 else if(pedido.type.equals("LIST ALL PROJECTS"))
                {
                    Resposta respostaListaProj = new Resposta("SUCCESS LIST");
                            respostaListaProj.Projects = bd.listarProjectos(1);
                    sc.send_clients(respostaListaProj,thread_number);
                }
                else if(pedido.type.equals("LIST ALL PAST PROJECTS"))
                {
                    Resposta respostaListaProj = new Resposta("SUCCESS LIST");
                    respostaListaProj.Projects = bd.listarProjectos(0);
                    sc.send_clients(respostaListaProj,thread_number);
                }
                else if(pedido.type.equals("CHECK BALANCE"))
                {
                    int id_cliente = bd.getIdCliente(pedido.username);
                    int saldo = bd.consultarSaldo(id_cliente);
                    Resposta rSaldo = new Resposta("BALANCE SUCESS");
                    rSaldo.setSaldo(saldo);
                    sc.send_clients(rSaldo,thread_number);
                }
                else if(pedido.type.equals("CHECK PROJECT EXISTS"))
                {
                    if(bd.projectExists(pedido.getProjectName())==false)
                    {
                        Resposta RPrjName = new Resposta("PROJECT NAME OK");
                        sc.send_clients(RPrjName,thread_number);
                    }
                    else
                    {
                        Resposta RPrjName = new Resposta("PROJECT NAME ALREADY TAKEN");
                        sc.send_clients(RPrjName,thread_number);
                    }
                }
                 else if(pedido.type.equals("NEW PROJECT"))
                {

                    String data = Integer.toString(pedido.getYear())+"-"+Integer.toString(pedido.getMonth())+"-"+Integer.toString(pedido.getDay());
                    String CriaPrj = bd.criarProjecto(pedido.getProjectName(), pedido.getDescriptionProject(), data, bd.getIdCliente(pedido.username), pedido.limit_cash, pedido.getArrayRecompensas());
                    Resposta rspCriaProj = new Resposta(CriaPrj);
                    sc.send_clients(rspCriaProj,thread_number);
                }
                else if(pedido.type.equals("LIST REWARDS PROJECT"))
                {
                    Resposta RwrdPrj = new Resposta("PRJ REWARDS LIST");
                    RwrdPrj.setRecompensas(bd.getRecompensasIDProj(pedido.getId_prj()));
                    sc.send_clients(RwrdPrj,thread_number);
                }
                 else if(pedido.type.equals("LIST ALTERNATIVES"))
                {
                    Resposta RspAlt = new Resposta("ALTERNATIVES LIST");
                    RspAlt.setArrayAlter(bd.getAltIdRecompensa(pedido.getId_Recompensa()));
                    sc.send_clients(RspAlt,thread_number);

                }
                 else if(pedido.type.equals("MAKE DONATION"))
                {
                    Resposta rspDoa;
                    int id_cliente = bd.getIdCliente(pedido.getUsername());
                    if(bd.fazerDoacao(pedido.getId_prj(),pedido.getMontanteDoar(),id_cliente,pedido.getId_Recompensa(),pedido.getId_Voto())==0)
                    {
                     rspDoa = new Resposta("DONATION SUCCESS");
                        sc.send_clients(rspDoa,thread_number);
                    }
                    else
                    {
                      rspDoa = new Resposta("DONATION INSUCCESS");
                        sc.send_clients(rspDoa,thread_number);
                    }
                }
                else if(pedido.type.equals("GET REWARDS"))
                {
                    Resposta rspRew;
                    rspRew = new Resposta("REWARDS GETTED");
                    int id_cliente = bd.getIdCliente(pedido.getUsername());
                    rspRew.setRecompensas(bd.getRecompensasIDCliente(id_cliente));
                    sc.send_clients(rspRew,thread_number);
                }


             }
         } catch (IOException e) {
             System.out.println("Cliente Desconectado!");
             try {
                 ois.close();
                 ClientSocket.close();
             } catch (IOException e1) {
                 e1.printStackTrace();
             }
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         } catch (SQLException e) {
             e.printStackTrace();
         }

     }

 }

 class Shared_Clients {
public static ArrayList<Socket> clientes = new ArrayList<Socket>();
        ObjectOutputStream out;

synchronized void addClient(Socket c)
        {
        clientes.add(c);
        System.out.println("Cliente adicionado");
        }

synchronized void send_clients(Resposta answer, int clintNmr) throws IOException {

        int i;
        for(i=0;i<clientes.size();i++)
        {
        try{
        if(clintNmr == i){
        out = new ObjectOutputStream(clientes.get(i).getOutputStream());
        out.writeObject(answer);
        System.out.println("Enviado para " + i);
        }
        }catch(IOException e){System.out.println("IO:" + e);}

        }
        }
        }

class Resposta implements Serializable
{
 String resposta;
    int saldo;
    ArrayList <Voto> arrayAlter;

    public ArrayList<Voto> getArrayAlter() {
        return arrayAlter;
    }

    public void setArrayAlter(ArrayList<Voto> arrayAlter) {
        this.arrayAlter = arrayAlter;
    }

    public ArrayList<Recompensa> getRecompensas() {
        return Recompensas;
    }

    public void setRecompensas(ArrayList<Recompensa> recompensas) {
        Recompensas = recompensas;
    }

    ArrayList <Recompensa> Recompensas;

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public ArrayList<String> getProjects() {
        return Projects;
    }

    public void setProjects(ArrayList<String> projects) {
        Projects = projects;
    }

    ArrayList <String> Projects;

    public Resposta(String resposta) {
        this.resposta = resposta;
    }
}

class Voto implements Serializable
{
    int idVoto;
    String descricao;

    public Voto(int idVoto, String desc) {
        this.idVoto = idVoto;
        this.descricao = desc;
    }
}