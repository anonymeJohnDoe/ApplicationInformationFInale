package Request;

import javax.crypto.SecretKey;
import java.io.Serializable;

/**
 * Created by cyril rocca Gr 2227 INPRES .
 */
public class RequestControlID implements Serializable{

    public static final int REQUEST_INCONNU = 1;
    public static final int REQUEST_INFO_COURS = 2;
    public static final int LOGIN_GROUP = 3;
    public static final int REQUEST_INFO_XML = 4;

    private String protocole;
    private String result;
    private int type;
    private String data;
    private byte[] bytes;
    private byte[] bytes2;
    private double alea;
    private long temps;
    private SecretKey cle;

    public RequestControlID(String protocole, int type, String user, byte[] bytes, double alea, long temps) {
        this.protocole = protocole;
        this.type = type;
        this.data = user;
        this.bytes = bytes;
        this.alea = alea;
        this.temps = temps;
    }

    public RequestControlID(String protocole, int type) {
        this.protocole = protocole;
        this.type = type;

    }

    public RequestControlID() {
        this.type = 0;
        this.data = "";
        this.alea = 0;
        this.temps = 0;
    }

    public String getProtocole() {
        return protocole;
    }

    public void setProtocole(String protocole) {
        this.protocole = protocole;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes2() {
        return bytes2;
    }

    public void setBytes2(byte[] bytes2) {
        this.bytes2 = bytes2;
    }

    public double getAlea() {
        return alea;
    }

    public void setAlea(double alea) {
        this.alea = alea;
    }

    public long getTemps() {
        return temps;
    }

    public void setTemps(long temps) {
        this.temps = temps;
    }

    public SecretKey getCle() {
        return cle;
    }

    public void setCle(SecretKey cle) {
        this.cle = cle;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
