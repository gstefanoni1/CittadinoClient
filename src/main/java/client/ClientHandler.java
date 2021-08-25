package client;

import datatypes.CentroVaccinale;
import datatypes.EventoAvverso;
import datatypes.Vaccinato;
import datatypes.Vaccinazione;
import datatypes.protocolmessages.*;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe che gestisce il lato client e il protocollo di comunicazione
 * @author Tornaghi Omar
 * @version 1.0
 */
public class ClientHandler {
    /**
     * Riferimento a clientHandler
     */
    private static ClientHandler instance = null;
    /**
     * PORT porta del server in ascolto
     */
    private static final int PORT = 9123;
    /**
     * Connector per connettersi al server
     */
    private NioSocketConnector connector = null;
    /**
     * Sessione TCP
     */
    private IoSession session = null;
    /**
     * Hashmap per listeners(controllers)
     */
    private final Map<String, PacketReceivedListener> listeners;

    private ClientHandler(){
        listeners = new ConcurrentHashMap<>();
    }

    /**
     * Ottiene l'istanza di @ClientHandler
     * @return istanza di @ClientHandler
     */
    public static synchronized ClientHandler getInstance(){
        if(instance == null) instance = new ClientHandler();
        return instance;
    }

    private static SSLContext getSslContext() throws Exception {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(ClientHandler.class.getClassLoader().getResourceAsStream("tls/myKeyStore.jks"), "password".toCharArray());
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, "password".toCharArray());

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                new SecureRandom());
        return sslContext;
    }

    /**
     * Si connette a un server
     * @return true se la connessione è andata a buon fine, false altrimenti
     */
    public boolean connect() throws Exception {
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(2000);
        SslFilter sslFilter = new SslFilter(getSslContext());
        sslFilter.setUseClientMode(true);
        connector.getFilterChain().addFirst("sslFilter", sslFilter);
        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setHandler(new ClientConnectionHandler(listeners));
        try {
            ConnectFuture future = connector.connect(new InetSocketAddress(PORT));
            future.awaitUninterruptibly();
            session = future.getSession();
        } catch (RuntimeIoException e) {
            System.err.println("Failed to connect.");
            connector.dispose();
            connector = null;
            session = null;
            return false;
        }
        return true;
    }

    /**
     * Si disconnette dal server
     * @return true se la disconnessione è avvenuta con successo, false altrimenti
     */
    public boolean disconnect(){
        try {
            session.getCloseFuture().awaitUninterruptibly();
            connector.dispose();
        }
        catch(Exception ex){ return false; }
        return true;
    }

    /**
     * Verifica stato della connessione
     * @return true se il client è connesso, false altrimenti
     */
    public boolean isConnected(){
        return session != null && session.isConnected();
    }

    /**
     * Aggiunge un listener che verrà informato all'arrivo di un pacchetto
     * @param event pacchetto da ricevere
     * @param ipr ricevente
     */
    public void addListener(String event, PacketReceivedListener ipr){
        listeners.put(event,ipr);
    }

    /**
     * Invia al server una richiesta di registrazione vaccinato
     * @param vaccinato il vaccinato da registrare
     * @param key identificativo della vaccinazione
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean requestUserRegistration(Vaccinato vaccinato, String key){
        if(session == null) return false;
        session.write(new UserRegistrationRequest(vaccinato, key));
        return true;
    }

    /**
     * Invia al server una richiesta di login
     * @param username username del cittadino
     * @param password password del cittadino
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean requestUserLogin(String username, String password) {
        if(session == null) return false;
        session.write(new UserLoginRequest(username, password));
        return true;
    }

    /**
     * Invia al server una richiesta di inserimento di un nuovo centro vaccinale
     * @param cv centro vaccinale da inserire
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean insertCV(CentroVaccinale cv){
        if(session == null) return false;
        session.write(new RegistrationCVRequest(cv));
        return true;
    }

    /**
     * Invia al server una richiesta di inserimento di una nuova vaccinazione
     * @param v vaccinazione da inserire
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean insertVaccination(Vaccinazione v){
        if(session == null) return false;
        session.write(new RegistrationVaccinatedRequest(v));
        return true;
    }

    /**
     * Invia al server una richiesta di inserimento di un nuovo evento avverso
     * @param ev evento avverso da inserire
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean insertEV(EventoAvverso ev){
        if(session == null) return false;
        session.write(new RegistrationEVRequest(ev));
        return true;
    }

    /**
     * Invia al server una richiesta per ottenere tutti i centri vaccinali
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean getAllCV(){
        if(session == null) return false;
        session.write(new GetAllCVRequest());
        return true;
    }

    /**
     * Invia al server una richiesta per ottenere tutti i centri vaccinali con nome uguale a name
     * @param name nome da ricercare
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean getCVByName(String name){
        if(session == null) return false;
        session.write(new GetCVByNameRequest(name));
        return true;
    }

    /**
     * Invia al server una richiesta per ottenere tutti i icentri vaccinali con comune e tipologia specificati da municipality e typology
     * @param municipality comune da ricercare
     * @param typology tipologia da ricercare
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean getCVByMunicipalityTypology(String municipality, String typology){
        if(session == null) return false;
        session.write(new GetCVByMunicipalityTypologyRequest(municipality, typology));
        return true;
    }

    /**
     * Invia al server una richiesta per ottenere una vaccinazione identificata dall'attributo key
     * @param key identificativo della vaccinazione
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean getVaccinationByKey(String key){
        if(session == null) return false;
        session.write(new GetVaccinationByKeyRequest(key));
        return true;
    }

    /**
     * Invia al server una richiesta per ottenere le tipologie di eventi avversi
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean getEvTypologies(){
        if(session == null) return false;
        session.write(new GetEVTypologiesRequest());
        return true;
    }

    /**
     * Invia al server una richiesta per ottenere i vaccini
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean getVaccines(){
        if(session == null) return false;
        session.write(new GetVaccinesRequest());
        return true;
    }

    /**
     * Invia al server una richiesta per ottenere il report di un centro vaccinale
     * @param cv centro vaccinale interessato
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean getReport(CentroVaccinale cv){
        if(session == null) return false;
        session.write(new GetReportRequest(cv));
        return true;
    }

    /**
     * Invia al server una richiesta per verificare l'userId di un cittadino
     * @param userId userId da verificare
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean requestUserIdCheck(String userId){
        if(session == null) return false;
        session.write(new CheckUserIdRequest(userId));
        return true;
    }
    /**
     * Invia al server una richiesta per verificare l'email di un cittadino
     * @param email email da verificare
     * @return true se la richiesta va a buon fine, false altrimenti
     */
    public boolean requestEmailCheck(String email){
        if(session == null) return false;
        session.write(new CheckEmailRequest(email));
        return true;
    }

}
