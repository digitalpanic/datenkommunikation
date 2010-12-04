package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatServerService;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.chatsession.listener.ChatServerListener;
import edu.hm.dako.chatsession.pdu.ChatAction;
import edu.hm.dako.chatsession.pdu.ChatMessage;
import edu.hm.dako.chatsession.pdu.ChatUserList;

/**
 * The Class ServerSessionImpl.
 * 
 * @author Hochschule München
 * @version 1.0.0
 */
public class ChatServerServiceImpl extends BaseServiceImpl implements
		ChatServerService {
	private static Log log = LogFactory.getLog(ChatServerServiceImpl.class);
	ChatServerListener listener;

	@Override
	public void sendMessage(ChatMessage message) throws ChatServiceException {

	}

	@Override
	public void sendAction(ChatAction action) throws ChatServiceException {

	}

	@Override
	public void sendUserList(ChatUserList userlist) throws ChatServiceException {

	}

	@Override
	public void registerChatSessionListener(ChatServerListener listener)
			throws ChatServiceException {
		this.listener = listener;

	}

	@Override
	public void destroy() throws ChatServiceException {

	}

	@Override
	public String getUserName() {
		return super.getUsername();
	}
}
