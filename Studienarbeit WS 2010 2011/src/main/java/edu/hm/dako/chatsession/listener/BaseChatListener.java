package edu.hm.dako.chatsession.listener;

import edu.hm.dako.chatsession.pdu.ChatAction;
import edu.hm.dako.chatsession.pdu.ChatMessage;

public abstract interface BaseChatListener {

    /**
     * Wird aufgerufen sobald eine Nachricht (Message) angekommen ist.
     * 
     * @param message
     */
    public void onMessageEvent(ChatMessage message);

    /**
     * Wird aufgerufen sobald eine Action angekommen ist.
     * 
     * @param message
     */
    public void onActionEvent(ChatAction action);

}
