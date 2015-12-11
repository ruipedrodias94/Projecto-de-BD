import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rui on 11-12-2015.
 */
public class Informations {

    private int socket_port;
    private String ip_servidorBD;
    private String pass;
    private String user;

    public void setSocket_port(int socket_port) {
        this.socket_port = socket_port;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Informations(){

        Properties props = new Properties();
        InputStream inputStream = null;

        try{

            inputStream = new FileInputStream("config.properties");
            props.load(inputStream);

            this.setServer_port(Integer.parseInt(props.getProperty("socket_port")));
            this.setIp_servidorBD(props.getProperty("ip_servidorBD"));
            this.setPass(props.getProperty("dbpass"));
            this.setUser(props.getProperty("dbuser"));

        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }

    }

    public int getSocket_port() {
        return socket_port;
    }

    public void setServer_port(int socket_port) {
        this.socket_port = socket_port;
    }

    public String getIp_servidorBD() {
        return ip_servidorBD;
    }

    public void setIp_servidorBD(String ip_servidorBD) {
        this.ip_servidorBD = ip_servidorBD;
    }
}
