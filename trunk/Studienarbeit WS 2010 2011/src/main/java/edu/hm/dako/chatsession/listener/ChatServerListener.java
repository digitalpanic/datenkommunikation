package edu.hm.dako.chatsession.listener;

public interface ChatServerListener extends BaseChatListener {

   
    /**
     * Wird aufgerufen, wenn sich ein Client ausloggt
     * 
     * @param username
     */
    public void onLogout(String username);
}
