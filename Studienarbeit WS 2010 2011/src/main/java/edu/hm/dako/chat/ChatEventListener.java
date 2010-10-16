package edu.hm.dako.chat;

public interface ChatEventListener {

    public void onMessage(String username, String message);

    public void onUserListUpdate(String[] userList);

    public void onAction(int actionId, String reserved);
}
