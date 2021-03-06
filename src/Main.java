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
        
        Informations informations = new Informations();
        int port = informations.getSocket_port();
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

    public void run() {
        try {
            while (true) {
                //Aqui que se vai fazer o tratamento dos pedidos
                ois = new ObjectInputStream(ClientSocket.getInputStream());
                //Chegada de mensagem ---> Tratamento

                Pedido pedido = (Pedido) ois.readObject();

                if (pedido.type.equals("LOGIN")) {
                    if (bd.login(pedido.username, pedido.password) == true) {
                        Resposta respostaLog = new Resposta("SUCCESS LOGIN");
                        sc.send_clients(respostaLog, thread_number);
                    } else {
                        Resposta respostaLog = new Resposta("INSUCCESS LOGIN");
                        sc.send_clients(respostaLog, thread_number);
                    }
                } else if (pedido.type.equals("REGISTRY")) {
                    int saldo_inicial = 100;
                    if (bd.registarConta(pedido.name, pedido.username, pedido.password, saldo_inicial) == 0) {
                        Resposta respostaReg = new Resposta("REGISTRY SUCCESS");
                        sc.send_clients(respostaReg, thread_number);
                    } else if (bd.registarConta(pedido.name, pedido.username, pedido.password, saldo_inicial) == 1) {
                        Resposta respostaReg = new Resposta("REGISTRY INSUCCESS");
                        sc.send_clients(respostaReg, thread_number);

                    }
                } else if (pedido.type.equals("LIST ALL PROJECTS")) {
                    Resposta respostaListaProj = new Resposta("SUCCESS LIST");
                    respostaListaProj.Projects = bd.listarProjectos(1);
                    sc.send_clients(respostaListaProj, thread_number);
                } else if (pedido.type.equals("LIST ALL PAST PROJECTS")) {
                    Resposta respostaListaProj = new Resposta("SUCCESS LIST");
                    respostaListaProj.Projects = bd.listarProjectos(0);
                    sc.send_clients(respostaListaProj, thread_number);
                } else if (pedido.type.equals("CHECK BALANCE")) {
                    int id_cliente = bd.getIdCliente(pedido.username);
                    int saldo = bd.consultarSaldo(id_cliente);
                    Resposta rSaldo = new Resposta("BALANCE SUCESS");
                    rSaldo.setSaldo(saldo);
                    sc.send_clients(rSaldo, thread_number);
                } else if (pedido.type.equals("CHECK PROJECT EXISTS")) {
                    if (bd.projectExists(pedido.getProjectName()) == false) {
                        Resposta RPrjName = new Resposta("PROJECT NAME OK");
                        sc.send_clients(RPrjName, thread_number);
                    } else {
                        Resposta RPrjName = new Resposta("PROJECT NAME ALREADY TAKEN");
                        sc.send_clients(RPrjName, thread_number);
                    }
                } else if (pedido.type.equals("NEW PROJECT")) {

                    String data = Integer.toString(pedido.getYear()) + "-" + Integer.toString(pedido.getMonth()) + "-" + Integer.toString(pedido.getDay());
                    String CriaPrj = bd.criarProjecto(pedido.getProjectName(), pedido.getDescriptionProject(), data, bd.getIdCliente(pedido.username), pedido.limit_cash, pedido.getArrayRecompensas());
                    Resposta rspCriaProj = new Resposta(CriaPrj);
                    sc.send_clients(rspCriaProj, thread_number);
                } else if (pedido.type.equals("LIST REWARDS PROJECT")) {
                    Resposta RwrdPrj = new Resposta("PRJ REWARDS LIST");
                    RwrdPrj.setRecompensas(bd.getRecompensasIDProj(pedido.getId_prj()));
                    sc.send_clients(RwrdPrj, thread_number);
                } else if (pedido.type.equals("LIST ALTERNATIVES")) {
                    Resposta RspAlt = new Resposta("ALTERNATIVES LIST");
                    RspAlt.setArrayAlter(bd.getAltIdRecompensa(pedido.getId_Recompensa()));
                    sc.send_clients(RspAlt, thread_number);

                } else if (pedido.type.equals("MAKE DONATION")) {
                    Resposta rspDoa;
                    int id_cliente = bd.getIdCliente(pedido.getUsername());
                    if (bd.fazerDoacao(pedido.getId_prj(), pedido.getMontanteDoar(), id_cliente, pedido.getId_Recompensa(), pedido.getId_Voto(), pedido.getnVotos()) == 0) {
                        System.out.println(" N VOTOS: " + pedido.getnVotos());
                        rspDoa = new Resposta("DONATION SUCCESS");
                        sc.send_clients(rspDoa, thread_number);
                    } else {
                        rspDoa = new Resposta("DONATION INSUCCESS");
                        sc.send_clients(rspDoa, thread_number);
                    }
                } else if (pedido.type.equals("GET REWARDS")) {
                    Resposta rspRew;
                    rspRew = new Resposta("REWARDS GETTED");
                    int id_cliente = bd.getIdCliente(pedido.getUsername());
                    rspRew.setRecompensas(bd.getRecompensasIDCliente(id_cliente));
                    sc.send_clients(rspRew, thread_number);
                } else if (pedido.type.equals("LIST PROJECT DETAILS")) {
                    Resposta r = new Resposta("PROJECT DETAILS");
                    r.setArrProject(bd.listarDetalhes_Projecto(pedido.getId_prj()));
                    sc.send_clients(r, thread_number);
                } else if (pedido.type.equals("GET PROJECTS ID USER")) {
                    Resposta r = new Resposta("USER PROJECTS");
                    r.setArrProject(bd.getProjectosIDUser(bd.getIdCliente(pedido.getUsername())));
                    sc.send_clients(r, thread_number);
                } else if (pedido.type.equals("GET PROJECTS")) {
                    Resposta r = new Resposta("GET ALL PROJECTS");
                    r.setArrProject(bd.getProjectos());
                    r.setIdCliente(bd.getIdCliente(pedido.username));
                    sc.send_clients(r, thread_number);
                } else if (pedido.type.equals("SEND MESSAGE")) {
                    int r = bd.sendMessage(bd.getIdCliente(pedido.username), pedido.getId_prj(), bd.getIdCliente_Proj(pedido.getId_prj()), pedido.getAssuntoMessage(), pedido.getCorpoMessage(), 1);
                    if (r == 1) {
                        Resposta resp = new Resposta("MESSAGE SUCESS");
                        sc.send_clients(resp, thread_number);
                    } else {
                        Resposta resp = new Resposta("MESSAGE INSUCESS");
                        sc.send_clients(resp, thread_number);
                    }
                } else if (pedido.type.equals("ANSWER MESSAGE")) {
                    int r = bd.sendMessage(bd.getIdCliente(pedido.username), bd.getIdProjByMessage(pedido.getId_mensagem()), bd.getIdRecebeByMessage(pedido.getId_mensagem()),
                            pedido.getAssuntoMessage(), pedido.getCorpoMessage(), pedido.getTipo_mensagem());
                    if (r == 1) {
                        Resposta resp = new Resposta("MESSAGE SUCESS");
                        sc.send_clients(resp, thread_number);
                    } else {
                        Resposta resp = new Resposta("MESSAGE INSUCESS");
                        sc.send_clients(resp, thread_number);
                    }

                } else if (pedido.type.equals("SEE PROJECT MESSAGES BY USER")) {
                    Resposta resposta = new Resposta("SEE MESSAGES");
                    ArrayList<Mensagem> mensagemsProj = bd.getMessages(bd.getIdCliente(pedido.username));
                    resposta.setMensagems(mensagemsProj);
                    sc.send_clients(resposta, thread_number);
                } else if (pedido.type.equals("ADD REWARD")) {
                    Resposta r = new Resposta("REWARDS ADDED");
                    for (int i = 0; i < pedido.getArrayRecompensas().size(); i++) {
                        String descricao;
                        int montante;
                        int id_Projecto;
                        System.out.println("DESCRICAO DA RECOMPENSA: ");
                        descricao = pedido.getArrayRecompensas().get(i).description;
                        System.out.println("MONTANTE A PARTIR DO QUAL O CLIENTE RECEBER A RECOMPENSA: ");
                        montante = pedido.getArrayRecompensas().get(i).montante;
                        id_Projecto = pedido.getId_prj();
                        bd.criarRecompensa(descricao, montante, id_Projecto);
                        for (int j = 0; j < pedido.getArrayRecompensas().get(i).alt.size(); j++) {
                            System.out.println(pedido.getArrayRecompensas().get(i).alt.get(j).getTipoAlt());
                            bd.criarVoto(bd.getIdRecompensa(id_Projecto, pedido.getArrayRecompensas().get(i).description), id_Projecto, pedido.getArrayRecompensas().get(i).alt.get(j).getTipoAlt());
                        }
                    }
                    sc.send_clients(r, thread_number);
                } else if (pedido.type.equals("REMOVE REWARD")) {
                    Resposta r1;
                    if (bd.deleteRecompensa(pedido.getId_Recompensa()) == 0) {
                        r1 = new Resposta("REWARD REMOVED");
                    } else {
                        r1 = new Resposta("REWARD NOT REMOVED");
                    }
                    sc.send_clients(r1, thread_number);

                } else if (pedido.type.equals("CANCEL PROJECT")) {
                    int result = bd.cancelarProjecto(pedido.getId_prj());
                    Resposta r = new Resposta("");
                    if (result == 1) {
                        r = new Resposta("PROJECT NOT CANCELED");
                    } else if (result == 0) {
                        r = new Resposta("PROJECT CANCELED");
                    }
                    sc.send_clients(r, thread_number);
                } else if (pedido.type.equals("PROJECT FINALIZATION")) {
                    Resposta r = null;
                    int respostaBD = bd.finalizarProjecto(pedido.getId_prj());
                    System.out.println("RespostaBD: " + respostaBD);
                    if (respostaBD == 1) {
                        r = new Resposta("PROJECT CANCELED");
                    } else if (respostaBD == 0) {
                        r = new Resposta("PROJECT CONCLUDED");

                    } else if (respostaBD == 2) {
                        r = new Resposta("UNKNOWN");
                    }
                    sc.send_clients(r, thread_number);
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }}

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

class VotoGAnhou implements Serializable
{
    int nvotos;
    String descRecom;
    String descAlter;

    public int getNvotos() {
        return nvotos;
    }

    public void setNvotos(int nvotos) {
        this.nvotos = nvotos;
    }

    public String getDescRecom() {
        return descRecom;
    }

    public void setDescRecom(String descRecom) {
        this.descRecom = descRecom;
    }

    public String getDescAlter() {
        return descAlter;
    }

    public void setDescAlter(String descAlter) {
        this.descAlter = descAlter;
    }
}

class Resposta implements Serializable
{
    String resposta;
    int saldo;
    private int idCliente;
    ArrayList <Voto> arrayAlter;
    ArrayList <Projecto> ArrProject;
    private ArrayList <Mensagem> mensagems;
    ArrayList <VotoGAnhou> ArrVG;


    public ArrayList<Projecto> getArrProject() {
        return ArrProject;
    }

    public void setArrProject(ArrayList<Projecto> arrProject) {
        ArrProject = arrProject;
    }

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

    public ArrayList<Mensagem> getMensagems() {
        return mensagems;
    }

    public void setMensagems(ArrayList<Mensagem> mensagems) {
        this.mensagems = mensagems;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}

class Voto implements Serializable
{
    int idVoto;
    String descricao;
    int id_recompensa;
    int ProjectID;
    int numvotos;

    public int getIdVoto() {
        return idVoto;
    }

    public void setIdVoto(int idVoto) {
        this.idVoto = idVoto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId_recompensa() {
        return id_recompensa;
    }

    public void setId_recompensa(int id_recompensa) {
        this.id_recompensa = id_recompensa;
    }

    public int getProjectID() {
        return ProjectID;
    }

    public void setProjectID(int projectID) {
        ProjectID = projectID;
    }

    public int getNumvotos() {
        return numvotos;
    }

    public void setNumvotos(int numvotos) {
        this.numvotos = numvotos;
    }

    public Voto(int idVoto, String desc) {
        this.idVoto = idVoto;
        this.descricao = desc;
    }
}