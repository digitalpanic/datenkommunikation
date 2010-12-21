package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatClientService;
import edu.hm.dako.chatsession.ChatClientServiceFactory;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.lwtrt.ex.LWTRTException;
import edu.hm.dako.lwtrt.impl.LWTRTServiceImpl;
import edu.hm.dako.test.mocks.LWTRTServiceMock;

public class ChatClientServiceFactoryImpl implements ChatClientServiceFactory {
	private static Log log = LogFactory.getLog(BaseServiceImpl.class);
	
	private LWTRTServiceMock sMock = new LWTRTServiceMock();
	//private LWTRTServiceImpl sMock = new LWTRTServiceImpl();
	

	/**
	 * @param port
	 * @return ChatClientServiceImpl
	 * @autor Pavlo Bishko & Maria Hoang
	 */
	@Override
	public ChatClientService register(int port) throws ChatServiceException {
		try{
			sMock.register(port);
		}catch(LWTRTException e){
			e.printStackTrace();			
		}
		return new ChatClientServiceImpl();
	}
}
