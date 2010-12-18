package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.BaseSessionService;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.chatsession.pdu.ChatPdu;
import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.ex.LWTRTException;

public abstract class BaseServiceImpl implements BaseSessionService {
	private static Log log = LogFactory.getLog(BaseServiceImpl.class);
	protected SessionStatus currentStatus = SessionStatus.NO_SESSION;

	protected int listeport;
	protected String username;
	protected LWTRTConnection connection;

	public enum SessionStatus {
		NO_SESSION(1), SESSION_ACTIVE(2), SESSION_DESTROYED(3);
		int code;

		SessionStatus(int code) {
			this.code = code;
		}
	}

	/**
	 * @param connection
	 * @autor Pavlo Bishko
	 */
	public void setConnection(LWTRTConnection connection) {
		this.connection = connection;
	}

	/**
	 * @param cpdu
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	protected void send(ChatPdu cpdu) throws ChatServiceException {
		try {
			connection.send(cpdu);
		} catch (LWTRTException e) {
			throw new ChatServiceException(e);
		}

	}

	public void setListenport(int listenport) {
		this.listeport = listenport;
	}

	public String getUsername() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

}
