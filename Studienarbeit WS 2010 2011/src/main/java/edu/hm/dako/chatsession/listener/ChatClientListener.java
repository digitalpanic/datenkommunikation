package edu.hm.dako.chatsession.listener;

import edu.hm.dako.chatsession.pdu.ChatUserList;

public interface ChatClientListener extends BaseChatListener {
    /**
     * Wird aufgerufen, sobald eine neue ChatUserList empfangen wurde
     * 
     * @param userlist
     */
    public void onUserListEvent(ChatUserList userlist);
}
