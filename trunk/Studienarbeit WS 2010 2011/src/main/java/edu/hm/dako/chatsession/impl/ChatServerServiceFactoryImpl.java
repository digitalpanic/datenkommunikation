package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatServerService;
import edu.hm.dako.chatsession.ChatServerServiceFactory;
import edu.hm.dako.chatsession.ex.ChatServiceException;

/**
 * The Enum ServerSessionFactoryImpl.
 * 
 * @author Hochschule München
 * @version 1.0.0
 */
public class ChatServerServiceFactoryImpl implements ChatServerServiceFactory {

	private static Log log = LogFactory
			.getLog(ChatServerServiceFactoryImpl.class);

	public void register(int port) throws ChatServiceException {

	}

	public ChatServerService getSession() throws ChatServiceException {
		return null;
	}
}
