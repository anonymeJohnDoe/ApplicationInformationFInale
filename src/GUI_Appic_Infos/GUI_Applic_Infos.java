package GUI_Appic_Infos;

import Data.Monnaie;
import Request.RequestControlID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Properties;

public class GUI_Applic_Infos extends JFrame
{
    private JPanel rootPanel;
    private JButton buttonInfoCoursMonetaire;
    private JList listLogs;
    private JList<Monnaie> listMonnaie;

    private int bytesRead;
    private int current = 0;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private int _fileSize;
    private String _pathXmlFile;
    private String _host;
    private int _port;
    private String _separator;
    private String _endOfLine;
    private Socket _connexion = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private DefaultListModel<String> dlmString;
    private DefaultListModel<Monnaie> dlmMonnaie;
    private String codeProvider = "BC"; //CryptixCrypto";
    private MessageDigest md;
    private ArrayList<String> _arrayOfArg = new ArrayList<>();



    public GUI_Applic_Infos() throws IOException {
        super("Application Informations");

        // Binder JList à un model
        dlmString = new DefaultListModel<String>();
        listLogs.setModel(dlmString);

        dlmMonnaie = new DefaultListModel<Monnaie>();
        listMonnaie.setModel(dlmMonnaie);

        // look de gui
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(rootPanel);
        setExtendedState(JFrame.MAXIMIZED_HORIZ);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);    // centrer la fenetre
        pack();
        setVisible(true);

        // lire config.properties
        ReadPropertyFile();

        // se connecter au serveur
        try {
            _connexion = new Socket(_host, _port);
            oos = new ObjectOutputStream(_connexion.getOutputStream());
            ois = new ObjectInputStream(_connexion.getInputStream());
            dlmString.addElement("Connexion Serveur Informations OK");
            System.out.println("Connexion Serveur Informations OK");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // listeners des boutons
        buttonInfoCoursMonetaire.addActionListener(e -> {
            try
            {
                btnInfoCoursMonetaires_handler();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        });
    }

    private void btnInfoCoursMonetaires_handler() throws IOException {

        RequestControlID request = null;
        RequestControlID response = null;

        request = new RequestControlID("INFOP", RequestControlID.REQUEST_INFO_XML);

        try {
            response = SendRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dlmString.addElement("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        dlmString.addElement("String reponse serveur : *" + response.toString() + "*");

        AnalyseReponse(response);

    }





    private void AnalyseReponse(RequestControlID response) {
        switch(response.getType()) {
            case RequestControlID.REQUEST_INFO_COURS:



                /////
                if(response.getResult().equals("ACK")) {

                    dlmString.clear();
                    dlmString.addElement("Les cours des principales unités monétaires (Euro, Dollar US, Yen, Franc suisse, Livre sterling) :");

                    // Lire reponse du serveur
                    String data = response.getData();
                    String tokfull = data.replaceAll(_endOfLine, "");
                    String[] tok = tokfull.split(_separator);

                    // recup nombre des monaies different
                    String nbreMonDiff = tok[0];

                    for(int i=0, j=1; i < Integer.parseInt(nbreMonDiff); i++, j++)
                    {

                        String[] data_monnaie = tok[j].split("\\|");
                        String nom_monnaie = data_monnaie[0];
                        String cours_monnaie = data_monnaie[1];
                        dlmString.addElement("Monnaie : " + nom_monnaie + ", Cours : " + cours_monnaie);

                    }

                } else {
                    dlmString.addElement("Erreur lors d'obtention des cours monetaires");
                }

                break;


                case RequestControlID.REQUEST_INFO_XML :

                    try {
                        Files.write(Paths.get(_pathXmlFile), response.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    dlmString.addElement("Fichier XML Reçu et Reconstruit");

                    try {
                       if(ValideXml()){
                           dlmString.addElement("Fichier XML validé");
                            LoadXML();
                       }else {
                           dlmString.addElement("ERREUR FICHIER XML INVALIDATE !");
                       }
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (org.xml.sax.SAXException e) {
                        e.printStackTrace();
                    }


                    break;
            default :
                break;
        }

    }

    private void LoadXML() {
        try {

            dlmMonnaie.clear();

            File fXmlFile = new File(_pathXmlFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("monnaie");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    String nameOfMonnaie = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String valueOfMonnaie = eElement.getElementsByTagName("taux_change").item(0).getTextContent();

                    dlmMonnaie.addElement(new Monnaie(nameOfMonnaie, valueOfMonnaie));

                    System.out.println("Name : " + nameOfMonnaie);
                    System.out.println("Value : " + valueOfMonnaie);
                }
            }

            dlmString.addElement("Fichier XML Chargé !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean ValideXml() throws ParserConfigurationException, org.xml.sax.SAXException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setErrorHandler(
                    new ErrorHandler() {
                        public void warning(SAXParseException e) {
                            System.out.println("WARNING : " + e.getMessage()); // do nothing
                        }

                        public void error(SAXParseException e) {
                            System.out.println("ERROR : " + e.getMessage());
                        }

                        public void fatalError(SAXParseException e){
                            System.out.println("FATAL : " + e.getMessage());
                        }
                    }
            );
            builder.parse(new InputSource(_pathXmlFile));
            return true;
        }
        catch (ParserConfigurationException pce) {
            System.out.println("ERROR : " + pce.getMessage());
        }
        catch (IOException io) {
            System.out.println("ERROR : " + io.getMessage());
        }

        return false;
    }

    private RequestControlID SendRequest(RequestControlID request) throws IOException {

        oos.writeObject(request);
        oos.flush();

        System.out.println("Commande [" + request + "] envoyée au serveur");

        //On attend la réponse
        RequestControlID response = read();
        System.out.println("\t * " + response + " : Réponse reçue " + response);

        return response;
    }

    private String MakeRequest(String cmd, ArrayList<String> arrayOfArg) {
        String request = cmd;

        for(String str : arrayOfArg) {
            request += _separator + str;
        }

        request += _endOfLine;


        return request;
    }

    //Méthode pour lire les réponses du serveur
    private RequestControlID read() throws IOException{
        RequestControlID response = null;
        try {
            response = (RequestControlID)ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void ReadPropertyFile(){

        //Lecture PROPERTY FILE
        Properties _propFile = new Properties();
        InputStream _InStream = null;
        try
        {
            _InStream = new FileInputStream("config.properties");
            _propFile.load(_InStream);

            _port = Integer.parseInt(_propFile.getProperty("PORT"));
            _host = _propFile.getProperty("HOST");
            _separator = _propFile.getProperty("SEPARATOR");
            _endOfLine = _propFile.getProperty("ENDOFLINE");
            _pathXmlFile = _propFile.getProperty("XML_FILE");
            _fileSize = Integer.parseInt( _propFile.getProperty("FILE_SIZE"));

            _InStream.close();

        } catch (IOException e) {
            System.err.println("Error Reading Properties Files [" + e + "]");
        }

    }

    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}
