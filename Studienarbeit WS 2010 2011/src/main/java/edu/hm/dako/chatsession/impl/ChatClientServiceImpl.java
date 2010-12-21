package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatClientService;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.chatsession.listener.ChatClientListener;
import edu.hm.dako.chatsession.pdu.ChatAction;
import edu.hm.dako.chatsession.pdu.ChatMessage;
import edu.hm.dako.chatsession.pdu.ChatPdu;
import edu.hm.dako.lwtrt.ex.LWTRTException;
import edu.hm.dako.test.mocks.LWTRTServiceMock;

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
        
        this.username = name;
        LWTRTServiceMock lwtrtServImp = new LWTRTServiceMock();
        try{
            setConnection(lwtrtServImp.connect(rcvAdd, port));

        }catch(LWTRTException e){
            e.printStackTrace();
        }
        ChatPdu cpdu = new ChatPdu();
        cpdu.setOpId(ChatPdu.ChatOpId.createSession_req_PDU);
        cpdu.setName(name);
        try{
            connection.send(cpdu);
        }catch(LWTRTException e){
            e.printStackTrace();
        }

    }

	/**
	 * @param message
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	@Override
	public void sendMessage(ChatMessage message) throws ChatServiceException {
		ChatPdu cpdu = new ChatPdu();
		cpdu.setOpId(ChatPdu.ChatOpId.sendMessage_req_PDU);
		cpdu.setName(message.getUsername());
		cpdu.setData(message);
		try {
			connection.send(cpdu);
		} catch (LWTRTException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param action
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	@Override
	public void sendAction(ChatAction action) throws ChatServiceException {
		// Client don't need to send action
	}

	/**
	 * @param listener
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	@Override
	public void registerChatSessionListener(ChatClientListener listener) {
		this.listener = listener;

	}

	/**
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	@Override
	public void destroy() throws ChatServiceException {
		ChatPdu cpdu = new ChatPdu();
		cpdu.setName(this.username);
		cpdu.setOpId(ChatPdu.ChatOpId.destroySession_req_PDU);
		try {
			connection.send(cpdu);
			log.debug("logout ist unterwegs");
			connection.disconnect();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public String getUserName() {
		return super.getUsername();
	}

}
