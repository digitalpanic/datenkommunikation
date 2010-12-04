package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatClientService;
import edu.hm.dako.chatsession.ChatClientServiceFactory;
import edu.hm.dako.chatsession.ex.ChatServiceException;

public class ChatClientServiceFactoryImpl implements ChatClientServiceFactory {
	private static Log log = LogFactory.getLog(BaseServiceImpl.class);

	@Override
	public ChatClientService register(int port) throws ChatServiceException {
		ChatClientServiceImpl client = new ChatClientServiceImpl();
		client.setListenport(port);
		return client;
	}

}
