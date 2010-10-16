package edu.hm.dako.chatsession;

import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.chatsession.listener.ChatServerListener;
import edu.hm.dako.chatsession.pdu.ChatAction;
import edu.hm.dako.chatsession.pdu.ChatMessage;
import edu.hm.dako.chatsession.pdu.ChatUserList;

/**
 * The Interface ServerSession.
 * 
 * @author Hochschule München
 * @version 1.0.0
 */
public interface ChatServerService extends BaseSessionService {

    /**
     * Wird aufgerufen um eine Userlist zu versenden
     * 
     * @param userlist
     */
    void sendUserList(ChatUserList userlist) throws ChatServiceException;

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
    void registerChatSessionListener(ChatServerListener listener) throws ChatServiceException;

    /**
     * Gibt den Usernamen der aktuellen Session zur+ck
     * @return
     */
    String getUserName();
}
