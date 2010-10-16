package edu.hm.dako.chatsession;

import edu.hm.dako.chatsession.ex.ChatServiceException;

public interface BaseSessionService {

    /**
     * Beendet die Session und loggt den Client aus
     */
    void destroy() throws ChatServiceException;

}
