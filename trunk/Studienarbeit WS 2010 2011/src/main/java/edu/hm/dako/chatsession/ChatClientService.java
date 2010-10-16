package edu.hm.dako.chatsession;

import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.chatsession.listener.ChatClientListener;
import edu.hm.dako.chatsession.pdu.ChatAction;
import edu.hm.dako.chatsession.pdu.ChatMessage;

public interface ChatClientService extends BaseSessionService {

    /**
     * Baut eine Session mit dem angegeben Partner auf
     * 
     * @param rcvAdd IP oder Hostname des Partners
     * @param port Port des Partners
     * @param name Login-Name des Users
     */
    void create(String rcvAdd, int port, String name) throws ChatServiceException;

    /**
     * Wird aufgerufen um eine Nachricht (Message) zu versenden
     * 
     * @param ChatMessage the message
     */
    void sendMessage(ChatMessage message) throws ChatServiceException;

    /**
     * Wird aufgerufen um eine Action (ChatAction) zu versenden
     * 
     * @param ChatAction the action
     */
    void sendAction(ChatAction action) throws ChatServiceException;

    /**
     * Hier wird ein Listenerobjekt registriert, das die ankommenden Nachrichten bearbeitet
     * 
     * @param listener
     */
    void registerChatSessionListener(ChatClientListener listener) throws ChatServiceException;

}
