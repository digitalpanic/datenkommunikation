package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatServerService;
import edu.hm.dako.chatsession.ChatServerServiceFactory;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.impl.LWTRTConnectionImpl;
import edu.hm.dako.lwtrt.impl.LWTRTServiceImpl;
import edu.hm.dako.test.mocks.LWTRTServiceMock;

/**
 * The Enum ServerSessionFactoryImpl.
 * 
 * @author Hochschule München
 * @version 1.0.0
 */

public class ChatServerServiceFactoryImpl implements ChatServerServiceFactory {

	private static Log log = LogFactory
			.getLog(ChatServerServiceFactoryImpl.class);
	
	 public int port;
	 //private LWTRTServiceMock sMock = new LWTRTServiceMock();
	 private LWTRTServiceImpl sMock = new LWTRTServiceImpl();
	 
	 /**
	 * @param con
	 * @return csService
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	private ChatServerServiceImpl createChatServerService(LWTRTConnection con) throws ChatServiceException {
	        // TODO
		ChatServerServiceImpl csService = new ChatServerServiceImpl(con, port);
		//csService.set????
		return csService;
	    	
	    }
	


	/**
	 * @param port
	 * @throws ChatServiceException
	 * @autor Pavlo Bishko
	 */
	    public void register(int port) throws ChatServiceException {
	    // TODO
	    	try
	    	{
	    		sMock.register(port);
	    	}
	    	
	    	catch (Exception e)
	    	{
	    		throw new ChatServiceException(e);
	    	} 	
	    }

	    
	    /**
		 * @return connect
		 * @throws ChatServiceException
		 * @autor Pavlo Bishko
		 */
	    public ChatServerService getSession() throws ChatServiceException {
	        // TODO
	    	LWTRTConnection connect;
	    	//LWTRTConnectionImpl connect;
	    	try
			{
		    		connect = sMock.accept();
			}
			
			catch (Exception e)
			{
				throw new ChatServiceException("server couldn't accept the request!", e);
			}
			
	        return createChatServerService(connect);
	    }
	
	    

	
	}
