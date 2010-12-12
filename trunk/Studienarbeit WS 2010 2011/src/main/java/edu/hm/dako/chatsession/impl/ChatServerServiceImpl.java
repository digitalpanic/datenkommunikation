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

	public ChatServerServiceImpl(LWTRTConnection con, int port) 
    {
		this.connection = con;
	}

	@Override
	public void sendMessage(ChatMessage message) throws ChatServiceException {

		ChatPdu pdu = new ChatPdu();
    	pdu.setName(message.getUsername());
    	pdu.setOpId(ChatOpId.sendMessage_req_PDU);
    	pdu.setData(message);
    	try
    	{
    		connection.send(pdu);
    	}
    	
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}	
	}

	@Override
	public void sendAction(ChatAction action) throws ChatServiceException {

		ChatPdu pduAction = new ChatPdu();
    	pduAction.setData(action);
    	pduAction.setOpId(ChatOpId.sendAction_req_PDU);
    	try
    	{
    		connection.send(pduAction);
    	}
    	
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}	
	}

	@Override
	public void sendUserList(ChatUserList userlist) throws ChatServiceException {

		ChatPdu pduList = new ChatPdu();
    	pduList.setData(userlist);
    	pduList.setOpId(ChatOpId.sendList_req_PDU);
    	try
    	{
    		connection.send(pduList);
    	}
    	
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}	
	}

	@Override
	public void registerChatSessionListener(ChatServerListener listener)
			throws ChatServiceException {
		this.listener = listener;

	}

	@Override
	public void destroy() throws ChatServiceException {

		try
    	{
    		connection.disconnect();
    	}
    	
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}  	
	}

	@Override
	public String getUserName() {
		return super.getUsername();
	}
}


