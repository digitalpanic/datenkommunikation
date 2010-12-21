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
			// e.printStackTrace();
			log.error("send error!" + e.getMessage());
		}

		ChatPdu pdu = new ChatPdu();
		pdu.setName(message.getUsername());
		pdu.setOpId(ChatOpId.sendMessage_req_PDU);
		pdu.setData(message);
		try {
			connection.send(pdu);
		}

		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @param action
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko & Maria Hoang
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

		ChatPdu pduAction = new ChatPdu();
		pduAction.setData(action);
		pduAction.setOpId(ChatOpId.sendAction_req_PDU);
		try {
			connection.send(pduAction);
		}

		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @param userList
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko & Maria Hoang
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

		ChatPdu pduList = new ChatPdu();
		pduList.setData(userlist);
		pduList.setOpId(ChatOpId.sendList_req_PDU);
		try {
			connection.send(pduList);
		}

		catch (Exception ex) {
			ex.printStackTrace();
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
		// ???

	}

	/**
	 * @throws ChatServiceException
	 * @autor Maria Hoang & Pavlo Bishko
	 */
	@Override
	public void destroy() throws ChatServiceException {

		try {
			connection.disconnect();
		}

		catch (LWTRTException e) {
			e.printStackTrace();
		}

//		FL: Ich hab mir mal erlaubt dieses zweite diconnect auszukommentieren...
//		try {
//			connection.disconnect();
//		}
//
//		catch (Exception ex) {
//			ex.printStackTrace();
//		}

	}

	@Override
	/**
	 *  @author Maria Hoang
	 */
	public String getUserName() {
		return super.getUsername();
	}
}
