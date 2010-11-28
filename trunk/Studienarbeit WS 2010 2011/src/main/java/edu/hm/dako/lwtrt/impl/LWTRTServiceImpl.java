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
	 * Registrieren einer Anwendung und Listenport aktivieren
	 * <p>
	 * Nach der Registrierung ist der lokale Port für den Dienstnehmer gebunden.
	 * Ab dem Zeitpunkt der Registrierung werden eingehende Verbindungswünsche
	 * entgegen genommen. Akzeptiert der Dienstnehmer die Verbindungswünsche
	 * nicht rechtzeitig, wird der Verbindungsaufbau abgebrochen. Nach der
	 * Registrierung kann der Dienstnehmer beginnen aktiv eine Verbindung
	 * aufzubauen.
	 * <p>
	 * Ein Port kann nur einmal (auch durch andere Prozesse) registriert werden.
	 * 
	 * @param localPort
	 *            Pornumber
	 * @throws LWTRTException
	 *             Fehler falls Portregestrierung nicht erflogleich war.
	 * 
	 */
	public void register(int localPort) throws LWTRTException {
		try {
			UdpSocketWrapper udpSocketW = new UdpSocketWrapper(localPort);
			socketmap.put(localPort, udpSocketW);
			log.debug("INFO: Register LWTRTPort. Portnumber:"
					+ LWTRTServiceImpl.socketmap.get(port));
		} catch (Exception ex) {
			log.error("ERROR: Fehler bei Regestrierung der Ports:" + ex);
		}

	}

	@Override
	public void unregister() throws LWTRTException {
		// TODO Auto-generated method stub

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
