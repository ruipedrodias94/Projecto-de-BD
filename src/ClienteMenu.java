
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
            entrada.nextLine();
            switch(opcao){
                case 1:
                {
                    System.out.println("Insira o username:");
                    String user = entrada.nextLine();
                    System.out.println("Insira password:");
                    String pass = entrada.nextLine();
                    Pedido p = new Pedido(user,pass,"LOGIN","name");
                    lt.send(p);
                    Resposta response = lt.receive();
                    if(response.resposta.equals("SUCCESS LOGIN"))
                    {
                        boolean login = true;
                        System.out.println("Login Efectuado com sucesso!");
                        while(login)
                        {
                            System.out.println("\n");
                            System.out.println("1 - Listar Projectos Actuais");
                            System.out.println("2 - Listar Projectos Antigos");
                            System.out.println("3 - Consultar Saldo");
                            System.out.println("4 - Consultar Recompensas");
                            System.out.println("5 - Criar Projecto");
                            System.out.println("6 - Doar para Projecto");
                            System.out.println("7 - Consultar Detalhes de Projecto");
                            System.out.println("8 - Adicionar / Remover Recompensas a Projecto");
                            System.out.println("9 - Enviar mensagem");
                            System.out.println("10 - Ver Caixa de entrada");
                            System.out.println("13 - Cancelar Projecto");
                            System.out.println("14 - Finalizar Projecto");
                            System.out.println("\n");
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
                                    p = new Pedido(user,null,"GET REWARDS",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    System.out.println("As recompensas que tem direito neste momento são:");
                                    for(int i=0;i<r.getRecompensas().size();i++)
                                    {
                                        System.out.println("ID Recompensa: "+r.getRecompensas().get(i).getId_Recompensa()+" Descricao: "+r.getRecompensas().get(i).getDescricao_Recompensa());
                                    }

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
                                    p = new Pedido(user,null,"LIST REWARDS PROJECT",null);
                                    p.setId_prj(ID_PROJ);
                                    lt.send(p);
                                    ArrayList <Recompensa> Ar;
                                    r = lt.receive();
                                    Ar = r.getRecompensas();
                                    System.out.println("Recompensas: ");
                                    for(int i=0;i<Ar.size();i++)
                                    {
                                        System.out.println("ID: "+Ar.get(i).getId_Recompensa()+" "+"Descriçao: "+Ar.get(i).getDescricao_Recompensa()+" Montante Necessario: "+Ar.get(i).getMontante_Recompensa());
                                    }
                                    System.out.println("Insira ID da recompensa que pretende:");
                                    int ID_Recompensa = entrada.nextInt();
                                    entrada.nextLine();
                                    int montante_necessario=0;
                                    for(int i=0;i<Ar.size();i++)
                                    {
                                        if(ID_Recompensa==Ar.get(i).getId_Recompensa())
                                        {
                                            montante_necessario = Ar.get(i).getMontante_Recompensa();
                                        }
                                    }
                                    p = new Pedido(user,null,"CHECK BALANCE",null);
                                    lt.send(p);
                                    Resposta r2 = lt.receive();
                                    if(r2.getSaldo()<montante_necessario)
                                    {
                                        System.out.println(" O seu saldo é inferior a recompensa pretendida! Saldo: "+r2.getSaldo());
                                        break;
                                    }

                                    p  = new Pedido(user,null,"LIST ALTERNATIVES",null);
                                    p.setId_Recompensa(ID_Recompensa);
                                    lt.send(p);
                                    ArrayList <Voto> aV = new ArrayList<>();
                                    r = lt.receive();
                                    aV = r.getArrayAlter();
                                    for(int i = 0 ;i<aV.size();i++)
                                    {
                                        System.out.println(" ID Aternativa: "+aV.get(i).idVoto+" Descricao: "+aV.get(i).descricao);
                                    }
                                    System.out.println("Insira o ID da alternativa que pretende: ");
                                    int idAlternativa = entrada.nextInt();
                                    entrada.nextLine();
                                    p = new Pedido(user,null,"MAKE DONATION",null);
                                    p.setId_Recompensa(ID_Recompensa);
                                    p.setId_prj(ID_PROJ);
                                    p.setId_Voto(idAlternativa);
                                    System.out.println("Insira o valor que pretende doar (pode ser mais que a recompensa mas nao pode ser menos): ");
                                    int montante_doacao = entrada.nextInt();
                                    entrada.nextLine();
                                    p.setMontanteDoar(montante_doacao);
                                    lt.send(p);
                                    Resposta finalAnswer = lt.receive();
                                    if(finalAnswer.resposta.equals("DONATION SUCCESS"))
                                    {
                                        System.out.println("Doacao efectuada com sucesso!");
                                    }
                                    else if(finalAnswer.resposta.equals("DONATION INSUCCESS"))
                                    {
                                        System.out.println("Erro! doacao nao concluida!");
                                    }
                                    break;
                                }
                                case 7:
                                {
                                    p = new Pedido(null,null,"LIST ALL PROJECTS",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    System.out.println("Projectos que pode consultar detalhes: \n");
                                    if(r.Projects.size()==0)
                                    {
                                        System.out.println("Sem Projectos Activos para Apresentar");
                                    }
                                    else {
                                        for (int i = 0; i < r.Projects.size(); i++) {
                                            System.out.println(r.Projects.get(i));
                                        }
                                    }
                                    System.out.println("Insira o ID do projecto que pretende saber os detalhes");
                                    int id_projecto = entrada.nextInt();
                                    entrada.nextLine();
                                    p = new Pedido(null,null,"LIST PROJECT DETAILS",null);
                                    p.setId_prj(id_projecto);
                                    lt.send(p);
                                    r = lt.receive();
                                    for(int i=0;i<r.getArrProject().size();i++)
                                    {
                                        if(r.getArrProject().get(i).getId_Projecto() == id_projecto){
                                            String nomeAux = r.getArrProject().get(i).getNome_Projecto();
                                            String descAux = r.getArrProject().get(i).getDescricao_Projecto();
                                            String estadoAux;
                                            int dinAngAux = r.getArrProject().get(i).getDinheiro_Angariado();
                                            int dinLimAux = r.getArrProject().get(i).getDinheiro_Limite();
                                            if(r.getArrProject().get(i).getEstado()==1)
                                            {
                                                estadoAux = "Activo";
                                            }
                                            else
                                            {
                                                estadoAux="Inactivo";
                                            }
                                            System.out.println("Nome Projecto: "+nomeAux+"\nDescricao: "+descAux+"\nEstado: "+estadoAux+"\nData Limite: "+r.getArrProject().get(i).getData_Limite()+
                                                    "\nDinheiro Angariado: "+dinAngAux+"\nDinheiro Limite: "+dinLimAux+"\n");
                                            System.out.print("=================================");
                                        }
                                    }
                                    break;
                                }
                                case 8:
                                {
                                    System.out.println("Pretende:  \n1 - Adicionar Recompensa\n2 - Remover Recompensa");
                                    int opcao1 = entrada.nextInt();
                                    entrada.nextLine();
                                    int idProjecto;
                                    p = new Pedido(user,null,"GET PROJECTS ID USER",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    for(int i=0;i<r.getArrProject().size();i++)
                                    {
                                        String nomeAux = r.getArrProject().get(i).getNome_Projecto();
                                        String descAux = r.getArrProject().get(i).getDescricao_Projecto();
                                        String estadoAux;
                                        int dinAngAux = r.getArrProject().get(i).getDinheiro_Angariado();
                                        int dinLimAux = r.getArrProject().get(i).getDinheiro_Limite();
                                        idProjecto = r.getArrProject().get(i).getId_Projecto();
                                        if(r.getArrProject().get(i).getEstado()==1)
                                        {
                                            estadoAux = "Activo";
                                        }
                                        else
                                        {
                                            estadoAux="Inactivo";
                                        }
                                        System.out.println("ID Projecto: "+idProjecto+"\nNome Projecto: "+nomeAux+"\nDescricao: "+descAux+
                                                "\nEstado: "+estadoAux+"\nData Limite: "+r.getArrProject().get(i).getData_Limite()+"" +
                                                "\nDinheiro Angariado: "+dinAngAux+"\nDinheiro Limite: "+dinLimAux+"\n=================");
                                    }
                                    idProjecto = entrada.nextInt();
                                    entrada.nextLine();
                                    if(opcao1==1)
                                    {
                                        p = new Pedido(user,null,"ADD REWARD",null);
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
                                            p.setId_prj(idProjecto);
                                            rec--;
                                        }
                                        lt.send(p);
                                        Resposta RewAns = lt.receive();
                                        if(RewAns.resposta.equals("REWARDS ADDED"))
                                        {
                                            System.out.println("Recompensa(s) adicionada(s) com sucesso");
                                        }
                                    }
                                    else if(opcao1==2)
                                    {
                                        System.out.println(idProjecto);
                                        p = new Pedido(user,null,"LIST REWARDS PROJECT",null);
                                        p.setId_prj(idProjecto);
                                        lt.send(p);
                                        r = lt.receive();
                                        System.out.println("Recompensas relativas ao projecto:");
                                        for(int i = 0;i<r.getRecompensas().size();i++)
                                        {

                                            System.out.println("ID: "+r.getRecompensas().get(i).getId_Recompensa()+" "+"Descriçao: "+r.getRecompensas().get(i).getDescricao_Recompensa()+" Montante Necessario: "+r.getRecompensas().get(i).getMontante_Recompensa());
                                        }
                                        System.out.println("Insira o ID da recompensa que pretende eliminar:");
                                        int id_recompensa = entrada.nextInt();
                                        p = new Pedido(user,null,"REMOVE REWARD",null);
                                        p.setId_Recompensa(id_recompensa);
                                        lt.send(p);
                                        r = lt.receive();
                                        if(r.resposta.equals("REWARD REMOVED"))
                                        {
                                            System.out.println("Recompensa removida com sucesso");
                                        }
                                        else if(r.resposta.equals("REWARD REMOVED"))
                                        {
                                            System.out.println("ERRO! Recompensa nao removida.");
                                        }

                                    }
                                    break;
                                }

                                case 9:
                                {
                                    System.out.println("Pretende enviar para um projecto ou responder a um Cliente? e - enviar / r - responder");
                                    String escolha = entrada.nextLine();
                                    entrada.nextLine();
                                    if(escolha.equals("e")){
                                        p = new Pedido(user,null,"GET PROJECTS",null);
                                        lt.send(p);
                                        Resposta r = lt.receive();

                                        ArrayList<Projecto> aux = new ArrayList<>();
                                        for (int i = 0; i < r.getArrProject().size(); i++){
                                            if (r.getArrProject().get(i).getCliente_idCliente() != r.getIdCliente()){
                                                aux.add(r.getArrProject().get(i));
                                            }
                                        }
                                        if(aux.size() == 0){
                                            System.out.println("Não tem projectos para enviar mensagens");
                                            break;
                                        }else{
                                            for (int i = 0; i < aux.size(); i++){
                                                System.out.println("ID: " + aux.get(i).getId_Projecto() +
                                                        "\nNOME: " + aux.get(i).getNome_Projecto() +
                                                        "===============");
                                            }
                                        }

                                        System.out.println("Para que projecto deseja mandar uma mensagem?");
                                        int id = entrada.nextInt();
                                        entrada.nextLine();
                                        System.out.println("Qual é o assunto? ");
                                        String assunto = entrada.nextLine();
                                        System.out.println("Corpo da mensagem: ");
                                        String corpoMessage = entrada.nextLine();
                                        p = new Pedido(user, null, "SEND MESSAGE", null);
                                        p.setAssuntoMessage(assunto);
                                        p.setCorpoMessage(corpoMessage);
                                        p.setId_prj(id);
                                        p.setTipo_mensagem(1);
                                        lt.send(p);

                                        Resposta rt = lt.receive();
                                        if (rt.resposta.equals("MESSAGE SUCESS")){
                                            System.out.println("Mensagem enviada com sucesso!");
                                        }else{
                                            System.out.println("Mensagem enviada com insucesso!");
                                        }
                                    }
                                    else{
                                        p = new Pedido(user, null, "SEE PROJECT MESSAGES BY USER", null);
                                        lt.send(p);

                                        Resposta r = lt.receive();
                                        if(r.getMensagems().size() != 0) {
                                            for (int i = 0; i < r.getMensagems().size(); i++) {
                                                if (r.getMensagems().get(i).getTipo() == 1) {
                                                    System.out.println("ID MENSAGEM: " + r.getMensagems().get(i).getId_Mensagem());
                                                    System.out.println("ASSUNTO: " + r.getMensagems().get(i).getAssunto_Mensagem());
                                                    System.out.println("DESCRICAO: " + r.getMensagems().get(i).getDescricao_Mensagem());
                                                    System.out.println("ID PROJECTO: " + r.getMensagems().get(i).getProjecto_idProjecto());
                                                    System.out.println("==========================================");
                                                }
                                            }
                                        }else {
                                            System.out.println("Nao tem mensagens na caixa de entrada.");
                                            break;
                                        }

                                        System.out.println("Para que mensagem deseja responder?");
                                        int id_msg = entrada.nextInt();
                                        entrada.nextLine();
                                        System.out.println("Qual é o assunto? ");
                                        String assunto = entrada.nextLine();
                                        System.out.println("Corpo da mensagem: ");
                                        String corpoMessage = entrada.nextLine();
                                        p = new Pedido(user, null, "ANSWER MESSAGE", null);
                                        p.setAssuntoMessage(assunto);
                                        p.setCorpoMessage(corpoMessage);
                                        p.setId_mensagem(id_msg);
                                        p.setTipo_mensagem(1);
                                        lt.send(p);

                                        Resposta rt = lt.receive();
                                        if (rt.resposta.equals("MESSAGE SUCESS")){
                                            System.out.println("Mensagem enviada com sucesso!");
                                        }else{
                                            System.out.println("Mensagem enviada com insucesso!");
                                        }

                                    }
                                    break;
                                }
                                case 10:
                                {
                                    p = new Pedido(user, null, "SEE PROJECT MESSAGES BY USER", null);
                                    lt.send(p);

                                    Resposta r = lt.receive();
                                    ArrayList<Mensagem> mensagemsAux = new ArrayList<>();
                                    for (int i = 0; i< r.getMensagems().size(); i++){
                                        if(r.getMensagems().get(i).getTipo() == 1){
                                            mensagemsAux.add(r.getMensagems().get(i));

                                        }
                                    }

                                    if(mensagemsAux.size() == 0){
                                        System.out.println("Nao tem mensagens na caixa de entrada.");
                                        break;
                                    }else{
                                        for (int i = 0; i< mensagemsAux.size(); i++){
                                            System.out.println("ID MENSAGEM: " + mensagemsAux.get(i).getId_Mensagem());
                                            System.out.println("ASSUNTO: " + mensagemsAux.get(i).getAssunto_Mensagem());
                                            System.out.println("DESCRICAO: " + mensagemsAux.get(i).getDescricao_Mensagem());
                                            System.out.println("ID PROJECTO: " + mensagemsAux.get(i).getProjecto_idProjecto());
                                            System.out.println("==========================================");
                                        }
                                    }
                                    break;
                                }

                                case 11:
                                {
                                    System.out.println("Quer ver as mensagens relativas a que projecto?");
                                    int id = entrada.nextInt();
                                    entrada.nextLine();
                                    p = new Pedido(user, null, "SEE PROJECT MESSAGES", null);
                                    p.setId_prj(id);
                                    lt.send(p);

                                    Resposta r = lt.receive();
                                    if (r.resposta.equals("SEE MESSAGES")){
                                        for (int i = 0; i< r.getMensagems().size(); i++ ){
                                            System.out.println("ID: " + r.getMensagems().get(i).getId_Mensagem() + "------<> ASSUNTO: " +
                                                    r.getMensagems().get(i).getAssunto_Mensagem());
                                        }
                                    }

                                    System.out.println("Pretende responder a que mensagem?");
                                    int id_mensagem = entrada.nextInt();
                                    entrada.nextLine();
                                    System.out.println("Qual é o assunto? ");
                                    String assunto = entrada.nextLine();
                                    System.out.println("Corpo da mensagem: ");
                                    String corpoMessage = entrada.nextLine();
                                    p = new Pedido(user, null, "SEND MESSAGE", null);
                                    p.setAssuntoMessage(assunto);
                                    p.setCorpoMessage(corpoMessage);
                                    p.setId_mensagem(id_mensagem);
                                    p.setTipo_mensagem(1);
                                    lt.send(p);

                                }
                                case 13:
                                {
                                    int idProjecto;
                                    p = new Pedido(user,null,"GET PROJECTS ID USER",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    for(int i=0;i<r.getArrProject().size();i++)
                                    {
                                        String nomeAux = r.getArrProject().get(i).getNome_Projecto();
                                        String descAux = r.getArrProject().get(i).getDescricao_Projecto();
                                        String estadoAux;
                                        int dinAngAux = r.getArrProject().get(i).getDinheiro_Angariado();
                                        int dinLimAux = r.getArrProject().get(i).getDinheiro_Limite();
                                        idProjecto = r.getArrProject().get(i).getId_Projecto();
                                        if(r.getArrProject().get(i).getEstado()==1)
                                        {
                                            estadoAux = "Activo";
                                        }
                                        else
                                        {
                                            estadoAux="Inactivo";
                                        }
                                        System.out.println("ID Projecto: "+idProjecto+" Nome Projecto: "+nomeAux+" Descricao: "+descAux+" Estado: "+estadoAux+" Data Limite: "+r.getArrProject().get(i).getData_Limite()+" Dinheiro Angariado: "+dinAngAux+" Dinheiro Limite: "+dinLimAux+"\n");
                                    }
                                    System.out.println("Insira ID do projecto que pretende cancelar: ");
                                    idProjecto = entrada.nextInt();
                                    entrada.nextLine();
                                    p = new Pedido(user,null,"CANCEL PROJECT",null);
                                    p.setId_prj(idProjecto);
                                    lt.send(p);
                                    Resposta ResCanPr = lt.receive();
                                    if(ResCanPr.resposta.equals("PROJECT NOT CANCELED"))
                                    {
                                        System.out.println("ERRO! Projecto nao cancelado");
                                    }
                                    else if(ResCanPr.resposta.equals("PROJECT CANCELED"))
                                    {
                                        System.out.println("Projecto cancelado com sucesso.");
                                    }
                                    break;

                                }

                                case 14:
                                {
                                    int idProjecto;
                                    p = new Pedido(user,null,"GET PROJECTS ID USER",null);
                                    lt.send(p);
                                    Resposta r = lt.receive();
                                    for(int i=0;i<r.getArrProject().size();i++)
                                    {
                                        String nomeAux = r.getArrProject().get(i).getNome_Projecto();
                                        String descAux = r.getArrProject().get(i).getDescricao_Projecto();
                                        String estadoAux;
                                        int dinAngAux = r.getArrProject().get(i).getDinheiro_Angariado();
                                        int dinLimAux = r.getArrProject().get(i).getDinheiro_Limite();
                                        idProjecto = r.getArrProject().get(i).getId_Projecto();
                                        if(r.getArrProject().get(i).getEstado()==1)
                                        {
                                            estadoAux = "Activo";
                                        }
                                        else
                                        {
                                            estadoAux="Inactivo";
                                        }
                                        System.out.println("ID Projecto: "+idProjecto+" Nome Projecto: "+nomeAux+" Descricao: "+descAux+" Estado: "+estadoAux+" Data Limite: "+r.getArrProject().get(i).getData_Limite()+" Dinheiro Angariado: "+dinAngAux+" Dinheiro Limite: "+dinLimAux+"\n");
                                    }
                                    System.out.println("Insira ID do projecto que pretende finalizar: ");
                                    idProjecto = entrada.nextInt();
                                    entrada.nextLine();
                                    p = new Pedido(user,null,"PROJECT FINALIZATION",null);
                                    p.setId_prj(idProjecto);
                                    lt.send(p);
                                    Resposta rFP = lt.receive();
                                    if(rFP.resposta.equals("PROJECT CANCELED"))
                                    {
                                        System.out.println("Projecto cancelado! Não reuniu o montante necessario.");
                                    }
                                    else if(rFP.resposta.equals("PROJECT CONCLUDED"))
                                    {
                                        System.out.println("Projecto Conluído! Dinheiro angariado enviado para o seu saldo!");
                                    }
                                    else if(rFP.resposta.equals("UNKKOWN"))
                                    {
                                        System.out.println("ERRO!");
                                    }

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
                    System.out.println("Insira o nome:");
                    String nome = entrada.nextLine();
                    System.out.println("Insira o username:");
                    String user = entrada.nextLine();
                    System.out.println("Insira password:");
                    String pass = entrada.nextLine();
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

    private int messageSucess;
    String assuntoMessage;
    String corpoMessage;
    int id_prj;
    int id_Recompensa;
    int id_Voto;
    int montanteDoar;
    int id_mensagem;
    private int tipo_mensagem;

    public int getMontanteDoar() {
        return montanteDoar;
    }

    public void setMontanteDoar(int montanteDoar) {
        this.montanteDoar = montanteDoar;
    }

    public int getId_Voto() {
        return id_Voto;
    }

    public void setId_Voto(int id_Voto) {
        this.id_Voto = id_Voto;
    }

    public int getId_Recompensa() {
        return id_Recompensa;
    }

    public void setId_Recompensa(int id_Recompensa) {
        this.id_Recompensa = id_Recompensa;
    }

    public int getId_prj() {
        return id_prj;
    }

    public void setId_prj(int id_prj) {
        this.id_prj = id_prj;
    }

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

    public String getAssuntoMessage() {
        return assuntoMessage;
    }

    public void setAssuntoMessage(String assuntoMessage) {
        this.assuntoMessage = assuntoMessage;
    }

    public String getCorpoMessage() {
        return corpoMessage;
    }

    public void setCorpoMessage(String corpoMessage) {
        this.corpoMessage = corpoMessage;
    }

    public int getMessageSucess() {
        return messageSucess;
    }

    public void setMessageSucess(int messageSucess) {
        this.messageSucess = messageSucess;
    }

    public int getId_mensagem() {
        return id_mensagem;
    }

    public void setId_mensagem(int id_mensagem) {
        this.id_mensagem = id_mensagem;
    }

    public int getTipo_mensagem() {
        return tipo_mensagem;
    }

    public void setTipo_mensagem(int tipo_mensagem) {
        this.tipo_mensagem = tipo_mensagem;
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
