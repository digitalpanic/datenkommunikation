package edu.hm.dako.chatsession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.chatsession.ChatServerService;
import edu.hm.dako.chatsession.ChatServerServiceFactory;
import edu.hm.dako.chatsession.ex.ChatServiceException;
import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.impl.LWTRTConnectionImpl;
import edu.hm.dako.lwtrt.impl.LWTRTServiceImpl;

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

	 private ChatServerServiceImpl createChatServerService(LWTRTConnection con) throws ChatServiceException {
	        // TODO
	    	System.out.println("wo1");
	    	return new ChatServerServiceImpl(con, port);  //neu
	    }

	    public void register(int port) throws ChatServiceException {
	    // TODO
	    	this.port = port;
	    	System.out.println("wo2");
	    	try
	    	{
	    		System.out.println("try");
	    		LWTRTServiceImpl.INSTANCE.register(port);
	    	}
	    	
	    	catch (Exception e)
	    	{
	    		System.out.println("catch");
	    		e.printStackTrace();
	    	} 	
	    }

	    public ChatServerService getSession() throws ChatServiceException {
	        // TODO
	    	ChatServerService css = null;
	    	System.out.println("wo3");
	    	try
			{
	    		LWTRTConnection con = LWTRTServiceImpl.INSTANCE.accept(port);
	    		css = new ChatServerServiceImpl(con, port); 
			}
			
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("Server couldn't accept the request");
			}
			
	        return css;
	    }
	}
