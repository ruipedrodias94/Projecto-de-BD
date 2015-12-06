import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by jorgearaujo on 01/12/15.
 */
public class ClienteMenu {
    public static void main(String[] args) {
        //menu vai ser feito por aqui
        LigacaoTCP lt = new LigacaoTCP();
        lt.ligaCliente();
        int opcao;
        Scanner entrada = new Scanner(System.in);
        while(true)
        {
            System.out.println("1 - Login");
            System.out.println("2 - Registar");
            System.out.println("0 - Sair");
            opcao = entrada.nextInt();
            switch(opcao){
                case 1:
                {
                    System.out.println("Insira o username:");
                    String user = entrada.next();
                    System.out.println("Insira password:");
                    String pass = entrada.next();
                    Pedido p = new Pedido(user,pass,"LOGIN","name");
                    lt.send(p);
                    Resposta response = lt.receive();
                    if(response.resposta.equals("SUCCESS LOGIN"))
                    {
                        boolean login = true;
                        System.out.println("Login Efectuado com sucesso!");
                        while(login)
                        {
                            System.out.println("1 - Listar Projectos Actuais");
                            System.out.println("2 - Listar Projectos Antigos");
                            System.out.println("3 - Consultar Saldo");
                            System.out.println("4 - Consultar Recompensas");
                            System.out.println("5 - Criar Projecto");
                            System.out.println("6 - Doar para Projecto");
                            opcao = entrada.nextInt();
                            switch (opcao)
                            {
                                case 1:
                                {
                                    p = new Pedido(null,null,"LIST ALL PROJECTS",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    if(r.Projects.size()==0)
                                    {
                                        System.out.println("Sem Projectos Activos para Apresentar");
                                    }
                                    else {
                                        for (int i = 0; i < r.Projects.size(); i++) {
                                            System.out.println(r.Projects.get(i));
                                        }
                                    }
                                    break;
                                }
                                case 2:
                                {
                                    p = new Pedido(null,null,"LIST ALL PAST PROJECTS",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    if(r.Projects.size()==0)
                                    {
                                        System.out.println("Sem Projectos Antigos para Apresentar");
                                    }
                                    else {
                                        for (int i = 0; i < r.Projects.size(); i++) {
                                            System.out.println(r.Projects.get(i));
                                        }
                                    }
                                    break;
                                }
                                case 3:
                                {
                                    p = new Pedido(user,null,"CHECK BALANCE",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    System.out.println("O seu saldo actual é: "+r.getSaldo());
                                    break;
                                }
                                case 4:
                                {

                                    break;
                                }
                                case 5:{
                                    String nomeProj;
                                    while(true)
                                    {
                                        p = new Pedido(user,null,"CHECK PROJECT EXISTS",null);
                                        System.out.println("Insira um nome para o Projecto:");
                                        p.setProjectName(entrada.next());
                                        nomeProj = p.getProjectName();
                                        lt.send(p);
                                        Resposta r = lt.receive();
                                        if(r.resposta.equals("PROJECT NAME OK"))
                                        {break;}
                                        else
                                        {
                                            System.out.println("Nome de Projecto já existe por favor insira outro:");
                                        }


                                    }
                                    p = new Pedido(user,null,"NEW PROJECT",null);
                                    p.setProjectName(nomeProj);
                                    entrada.nextLine();
                                    System.out.println("Insira uma descrição do projecto:");
                                    p.setDescriptionProject(entrada.nextLine());
                                    System.out.println("Insira uma data limite para o projecto.");
                                    System.out.println("Ano:");
                                    p.setYear(entrada.nextInt());
                                    System.out.println("Mês:");
                                    p.setMonth(entrada.nextInt());
                                    System.out.println("Dia:");
                                    p.setDay(entrada.nextInt());
                                    System.out.println("Insira uma quantia a atingir:");
                                    p.setLimit_cash(entrada.nextInt());
                                    System.out.println("Quantas Recompensas pretende adicionar ao projecto?");
                                    int rec = entrada.nextInt();
                                    entrada.nextLine();
                                    while(rec>0)
                                    {

                                        System.out.println("Insira uma descrição da Recompensa:");
                                        String desc_aux = entrada.nextLine();
                                        System.out.println("A partir de que montante quer que a recompensa seja oferecida?");
                                        int mont_aux = entrada.nextInt();
                                        entrada.nextLine();
                                        System.out.println("Pretende que a recompensa tenha alternativas? Sim - s; Não - n");
                                        String altB = entrada.nextLine();
                                        ArrayList <Alternativa> aA = new ArrayList<>();
                                        if(altB.equals("s"))
                                        {
                                            System.out.println("Quantas?");
                                            int qtdAlt = entrada.nextInt();
                                            entrada.nextLine();
                                            while(qtdAlt>0)
                                            {
                                                System.out.println("Insira uma descrição da alternativa:");
                                                Alternativa a = new Alternativa();
                                                a.setTipoAlt(entrada.nextLine());
                                                aA.add(a);
                                                qtdAlt--;

                                            }

                                        }
                                        Recompensa_proj rP = new Recompensa_proj(desc_aux,mont_aux);
                                        rP.setAlt(aA);
                                        p.getArrayRecompensas().add(rP);
                                        rec--;

                                    }
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    if(r.resposta.equals("PROJECTO CRIADO COM SUCESSO"))
                                    {
                                        System.out.println("Projecto Criado Com Sucesso!\n");
                                    }
                                    break;
                                }
                                case 6:{
                                    //Lista Projectos a que se pode fazer pledge
                                    p = new Pedido(null,null,"LIST ALL PROJECTS",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    System.out.println("Projectos a que pode fazer doação: \n");
                                    if(r.Projects.size()==0)
                                    {
                                        System.out.println("Sem Projectos Activos para Apresentar");
                                    }
                                    else {
                                        for (int i = 0; i < r.Projects.size(); i++) {
                                            System.out.println(r.Projects.get(i));
                                        }
                                    }
                                    System.out.println("Insira o ID do projecto a que pretende fazer doação:");
                                    int ID_PROJ = entrada.nextInt();
                                    break;

                                }


                            }

                        }
                    }
                    else
                    {
                        System.out.println("Username ou password errados!");
                    }
                    break;
                }
                case 2:
                {
                    //TODO tentativas de regist
                    System.out.println("Insira o nome:");
                    String nome = entrada.next();
                    System.out.println("Insira o username:");
                    String user = entrada.next();
                    System.out.println("Insira password:");
                    String pass = entrada.next();
                    Pedido pedido = new Pedido(user,pass,"REGISTRY",nome);
                    lt.send(pedido);
                    Resposta response = lt.receive();
                    if(response.resposta.equals("REGISTRY SUCCESS"))
                    {
                        System.out.println("Cliente Registado com sucesso!\n Faça login...");
                    }
                    else if(response.resposta.equals("REGISTRY INSUCCESS"))
                    {
                        System.out.println("Username já usado! por favor escolha outro...");
                    }
                    break;
                }

            }

        }
    }}


 class LigacaoTCP{
     String hostname = "localhost";
     int port = 6000;
     Socket s;

     public void ligaCliente() {
         try {
             s = new Socket(InetAddress.getLocalHost(), port);
             System.out.println("socket: " + s);

         } catch (IOException e) {
             e.printStackTrace();
         }
     }

     public void send(Pedido r){

         try {
             OutputStream os = s.getOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(os);
             oos.writeObject(r);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

     public Resposta receive()
     {    ObjectInputStream ois = null;
         Resposta r = null;
         try {
             InputStream in = s.getInputStream();
             ois = new ObjectInputStream(in);
             r = (Resposta) ois.readObject();
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }

         return r;
     }



 }

class Pedido implements Serializable
{
    String username;
    String password;
    String type;
    String name;
    String projectName;
    String DescriptionProject;
    int Day;
    int Month;

    public ArrayList<Recompensa_proj> getArrayRecompensas() {
        return arrayRecompensas;
    }

    public void setArrayRecompensas(ArrayList<Recompensa_proj> arrayRecompensas) {
        this.arrayRecompensas = arrayRecompensas;
    }

    int Year;
    int limit_cash;
    ArrayList <Recompensa_proj> arrayRecompensas = new ArrayList<>();

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescriptionProject() {
        return DescriptionProject;
    }

    public void setDescriptionProject(String descriptionProject) {
        DescriptionProject = descriptionProject;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getLimit_cash() {
        return limit_cash;
    }

    public void setLimit_cash(int limit_cash) {
        this.limit_cash = limit_cash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Pedido(String username, String password, String type,String name) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.name = name;
    }

}

class Recompensa_proj implements Serializable
{
    String description;
    int montante;
    ArrayList <Alternativa> alt = new ArrayList<>();

    public ArrayList<Alternativa> getAlt() {
        return alt;
    }

    public void setAlt(ArrayList<Alternativa> alt) {
        this.alt = alt;
    }

    public Recompensa_proj(String description, int montante) {
        this.description = description;
        this.montante = montante;
    }
}

class Alternativa implements Serializable
{
    String TipoAlt;

    public String getTipoAlt() {
        return TipoAlt;
    }

    public void setTipoAlt(String tipoAlt) {
        TipoAlt = tipoAlt;
    }
}
