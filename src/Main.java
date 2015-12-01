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


        //Esta vai ser a nossa interface!
        DataBase dataBase = new DataBase();


        //Registar Cliente
        //dataBase.registarConta("Rui Pedro", "ruipedrodias", "12345", 10000);

        //Login
        //login = dataBase.login("ruipedrodias", "12345");

        //Get id_Cliente
        id_Cliente = dataBase.getIdCliente("ruipedrodias");
        System.out.println(id_Cliente);

        //Listar os projectos, temos de ter uma variavel para o estado  activo --- 1 inactivo -- 0;
        projectos = dataBase.listarProjectos(0);
        for (int i = 0; i< projectos.size();i++){
            System.out.println(projectos.get(i));
        }

        //Consultar saldo
        saldo = dataBase.consultarSaldoProjecto(5);
        System.out.print(saldo);

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

        detalhes = dataBase.listarDetalhes_Projecto(4);
        System.out.println(detalhes);

        //Fazer doacao
        //dataBase.fazerDoacao(5, 10, id_Cliente);

        //Cancelar projecto
        //dataBase.cancelarProjecto(4);

        //Finalizar projectos
        dataBase.finalizarProjectos();
    }
}
