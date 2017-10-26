package name.z_dev.peditor.models;

/**
 * Created by D.Krylov on 03.08.2015.
 */
public class Account {
    int id;
    String name;
    String url;
    String login;
    String password;
    String apikey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
    public Account(){
    }

    public Account(String name, String url){
        this.name=name;
        this.url=url;
    }

    public Account(String name, String url, String login, String password, String apikey){
        this.name = name;
        this.url = url;
        this.login = login;
        this.password = password;
        this.apikey = apikey;
    }

}
