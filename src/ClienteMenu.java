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


