package edu.hm.dako.lwtrt.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.LWTRTService;
import edu.hm.dako.lwtrt.ex.LWTRTException;
import edu.hm.dako.udp.wrapper.UdpSocketWrapper;

/**
 * The Implementation of LWTRTService.
 * 
 * @author Hochschule München
 * @version 1.0.0
 */
public class LWTRTServiceImpl implements LWTRTService {

	// variable for logging
	private static Log log = LogFactory.getLog(LWTRTService.class);

	// variable for socketmap
	static ConcurrentHashMap<Integer, UdpSocketWrapper> socketmap = new ConcurrentHashMap<Integer, UdpSocketWrapper>();

	// variable for portnummer
	int port;
	
	
	

	/**
	 * Registrieren einer Anwendung und Port aktivieren
	 * 
	 * @param localPort
	 *            Portnumber
	 * @throws LWTRTException
	 */
	public void register(int port) throws LWTRTException {
		try {
			UdpSocketWrapper udpsw = new UdpSocketWrapper(port);
			socketmap.put(port, udpsw);
			// TODO Wird der Port korrekt in die LogFiles geschrien?
			log.debug("Register LWTRTPort. Portnumber:"
					+ LWTRTServiceImpl.socketmap.get(port));
		} catch (Exception ex) {
			log.error("Fehler bei Regestrierung der Ports:" + ex);
			ex.printStackTrace();
		}
	}

	/**
	 * De-Regestriert den Port, entfernt diesen aus das Hashmap und schliesst
	 * den Wrapper.
	 * 
	 * 
	 * @param port
	 *            Port, welcher De-Regestriert und aus der Hashmap entfernt
	 *            werden soll.
	 * @throws LWTRTException
	 */
	public void unregister() throws LWTRTException {
		UdpSocketWrapper udpswu;

		//TODO Prüfen, ob Logfiles korrekt geschrieben werden
		try {
			udpswu = LWTRTServiceImpl.socketmap.get((Integer) port);
			log.debug("Port " + port + " wurde aus Socketmap geholt");
			socketmap.remove((Integer) port);
			log.debug("Port " + port + " wurde aus Socketmap entfernt.");
			udpswu.close();
			log.debug("SocketWrapper wurde geschlossen.");
		} catch (Exception ex) {
			log.error("Fehler bei De-Regestrierung der Ports:" + port
					+ " " + ex);
			ex.printStackTrace();
		}

	}

	@Override
	public LWTRTConnection connect(String remoteAddress, int remotePort)
			throws LWTRTException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public LWTRTConnection accept() throws LWTRTException {
		// TODO Auto-generated method stub
		return null;
	}

}
