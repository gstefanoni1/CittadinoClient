package client;


import datatypes.protocolmessages.Packet;

/**
 * Interfaccia per implementare pattern EventListener all'arrivo di messaggi
 * @author Tornaghi Omar
 * @version 1.0
 */
public interface PacketReceivedListener {
    /**
     * Metodo che viene chiamato all'arrivo di un pacchetto
     * @param packet pacchetto ricevuto
     */
    void onPacketReceived(Packet packet);
}
