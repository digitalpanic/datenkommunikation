package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatServerService;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.chatsession.listener.ChatServerListener;
import edu.hm.dako.chatsession.pdu.ChatAction;
import edu.hm.dako.chatsession.pdu.ChatMessage;
import edu.hm.dako.chatsession.pdu.ChatPdu;
import edu.hm.dako.chatsession.pdu.ChatPdu.ChatOpId;
import edu.hm.dako.chatsession.pdu.ChatUserList;

import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.ex.LWTRTException;

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

	private LWTRTConnection connection;

	public ChatServerServiceImpl(LWTRTConnection con, int port) {
		this.connection = con;
	}

	/**
	 * @param message
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko & Maria Hoang
	 */
	@Override
	public void sendMessage(ChatMessage message) throws ChatServiceException {

		ChatPdu cpdu = new ChatPdu();
		cpdu.setName(message.getUsername());
		cpdu.setOpId(ChatOpId.sendMessage_req_PDU);
		cpdu.setData(message);
		try {
			this.connection.send(cpdu);
		}

		catch (LWTRTException e) {
			e.printStackTrace();
			log.error("send error!" + e.getMessage());
		}

	}

	/**
	 * @param action
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	@Override
	public void sendAction(ChatAction action) throws ChatServiceException {

		ChatPdu cpdu = new ChatPdu();
		cpdu.setOpId(ChatOpId.sendAction_req_PDU);
		cpdu.setData(action);
		try {
			this.connection.send(cpdu);
		}

		catch (LWTRTException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param userList
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	@Override
	public void sendUserList(ChatUserList userlist) throws ChatServiceException {

		ChatPdu cpdu = new ChatPdu();
		cpdu.setOpId(ChatOpId.sendList_req_PDU);
		cpdu.setData(userlist);
		try {
			this.connection.send(cpdu);
		}

		catch (LWTRTException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param listener
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	@Override
	public void registerChatSessionListener(ChatServerListener listener)
			throws ChatServiceException {
		this.listener = listener;

	}

	/**
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko & Maria Hoang
	 */
	@Override
	public void destroy() throws ChatServiceException {

		try {
			connection.disconnect();
		}

		catch (LWTRTException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getUserName() {
		return super.getUsername();
	}
}
