package client;

import datatypes.protocolmessages.Packet;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.Map;

/**
 * Classe che gestisce la connessione tra client e server
 * @author Tornaghi Omar
 * @version 1.0
 */
public class ClientConnectionHandler extends IoHandlerAdapter {
    /**
     * Listeners a cui inviare i pacchetti ricevuti
     */
    private final Map<String, PacketReceivedListener> listeners;
    /**
     * Sessione TCP
     */
    IoSession session;

    public ClientConnectionHandler(Map<String, PacketReceivedListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * Metodo chiamato alla creazione della sessione
     * @param session sessione creata
     * @throws Exception e.g. invalid session
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        this.session = session;
    }

    /**
     * Metodo chiamato quando la sessione è aperta
     * @param ioSession sessione aperta
     * @throws Exception e.g. invalid session
     */
    @Override
    public void sessionOpened(IoSession ioSession) throws Exception {
    }

    /**
     * Metodo chiamato al verificarsi di un eccezione
     * @param ioSession sessione attiva
     * @param throwable eccezione da lanciare
     * @throws Exception e.g. invalid session
     */
    @Override
    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {

    }

    /**
     * Metodo chiamato quando arriva un messaggio
     * @param ioSession sessione attiva
     * @param message messaggio arrivato
     * @throws Exception e.g. sessione non disponibile
     */
    @Override
    public void messageReceived(IoSession ioSession, Object message) throws Exception {
        Packet packet = (Packet) message;
        PacketReceivedListener listener = listeners.get(packet.getPacketName());
        if(listener != null)
            listener.onPacketReceived(packet);
    }

}
