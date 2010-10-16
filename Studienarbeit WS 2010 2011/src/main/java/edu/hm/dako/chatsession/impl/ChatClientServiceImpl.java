package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatClientService;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.chatsession.listener.ChatClientListener;
import edu.hm.dako.chatsession.pdu.ChatAction;
import edu.hm.dako.chatsession.pdu.ChatMessage;

/**
 * The Class ClientSessionImpl.
 * 
 * @author Hochschule München
 * @version 1.0.0
 */
public class ChatClientServiceImpl extends BaseServiceImpl implements
		ChatClientService {
	private static Log log = LogFactory.getLog(ChatClientServiceImpl.class);
	protected ChatClientListener listener;

	@Override
	public void create(String rcvAdd, int port, String name)
			throws ChatServiceException {
		if (currentStatus != SessionStatus.NO_SESSION) {
			throw new ChatServiceException(
					"Aufruf nicht m+glich. Falscher Status. Aktueller Status:"
							+ currentStatus.toString());
		}

	}

	@Override
	public void sendMessage(ChatMessage message) throws ChatServiceException {

	}

	@Override
	public void sendAction(ChatAction action) throws ChatServiceException {

	}

	@Override
	public void registerChatSessionListener(ChatClientListener listener) {
		this.listener = listener;

	}

	@Override
	public void destroy() throws ChatServiceException {

	}

}
