package edu.hm.dako.lwtrt.impl;

// Imports
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.LWTRTService;
import edu.hm.dako.lwtrt.ex.LWTRTException;
import edu.hm.dako.lwtrt.pdu.LWTRTPdu;
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

	// variable for first port
	private int firstPort;

	// variable for ip-adress
	private String adress;

	// Socket wrapper for reviving
	private UdpSocketWrapper recWra;

	// variable foe sequencenumber
	private int seqNr;

	// variable for Buffer
	private static volatile Vector<LWTRTPdu> buffer = new Vector<LWTRTPdu>();

	// hashmap for new connection
	protected static ConcurrentHashMap<Integer, LWTRTConnectionImpl> connectionMap = new ConcurrentHashMap<Integer, LWTRTConnectionImpl>();

	/**
	 * Registrieren einer Anwendung und Port aktivieren
	 * 
	 * @param localPort
	 *            Portnumber
	 * @throws LWTRTException
	 * @author Florian Leicher
	 */
	public void register(int port) throws LWTRTException {
		try {
			UdpSocketWrapper udpsw = new UdpSocketWrapper(port);
			socketmap.put(port, udpsw);
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
	 * @author Florian Leicher
	 */
	public void unregister() throws LWTRTException {
		UdpSocketWrapper udpswu;
		try {
			udpswu = LWTRTServiceImpl.socketmap.get((Integer) port);
			log.debug("Port " + port + " wurde aus Socketmap geholt");
			socketmap.remove((Integer) port);
			log.debug("Port " + port + " wurde aus Socketmap entfernt.");
			udpswu.close();
			log.debug("SocketWrapper wurde geschlossen.");
		} catch (Exception ex) {
			log.error("Fehler bei De-Regestrierung der Ports: " + port + " "
					+ ex);
			ex.printStackTrace();
		}

	}

	/**
	 * Stellt eine Verbidung her, welche die Verbindungssicherheit für die
	 * UDP-Verbindung gewährleistet.
	 * 
	 * 
	 * @param remoteAddress
	 *            IP Adresse des Remoterechners
	 * @param remotePort
	 *            port des Remoterechners
	 * @throws LWTRTException
	 * @author Florian Leicher
	 */
	public LWTRTConnection connect(String remoteAddress, int remotePort)
			throws LWTRTException {
		UdpSocketWrapper udpSocketWra;
		udpSocketWra = socketmap.get(port);

		if (udpSocketWra == null) {
			try {
				udpSocketWra = new UdpSocketWrapper(port);
			} catch (SocketException ex) {
				log.error("Fehler beim initialisieren des Ports: " + port + ""
						+ ex);
				ex.printStackTrace();
			}
		}

		LWTRTPdu Pdu = new LWTRTPdu();
		Pdu.setOpId(LWTRTPdu.OPID_CONNECT_REQ);
		// Sequeznummer beim start der Verbindung auf 0 setzen
		seqNr = 0;
		Pdu.setSequenceNumber(seqNr);

		Calendar cal = Calendar.getInstance();
		long time;

		LWTRTPdu receivePdu = new LWTRTPdu();
		log.debug("For Schleife gestartet");
		for (int retries = 0; retries < 3; retries++) {
			try {
				udpSocketWra.send(Pdu);
			} catch (IOException e1) {
				log.error(e1);
				e1.printStackTrace();
			}
			time = cal.getTimeInMillis() + 5000;

			try {
				while (cal.getTimeInMillis() < time) {

					udpSocketWra.receive(receivePdu);
					if (receivePdu != null) {
						break;
					}

				}
			} catch (IOException e) {
				log.error(e);
				e.printStackTrace();
			}
			if (receivePdu != null) {
				log.debug("For Schleife beendet");
				break;
			}
		}

		LWTRTConnectionImpl con = new LWTRTConnectionImpl(adress, port,
				receivePdu.getRemoteAddress(), receivePdu.getRemotePort());

		LWTRTServiceImpl.connectionMap.put(port, con);

		log.debug("Answer:" + receivePdu.getRemotePort() + " "
				+ receivePdu.getOpId() + " " + receivePdu.getSequenceNumber());

		recThread recThread = new recThread(udpSocketWra, con);

		recThread.start();
		return con;
	}

	/**
	 * Annahme einer eingehenden Verbindung
	 * 
	 * @throws LWTRTException
	 * @author Florian Leicher
	 */
	public LWTRTConnection accept() throws LWTRTException {
		LWTRTPdu receivePDU = new LWTRTPdu();
		UdpSocketWrapper receiver = LWTRTServiceImpl.socketmap
				.get((Integer) port);
		try {
			adress = (String) InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			log.error("Fehler beim setzen der Adresse" + " " + e1);
			e1.printStackTrace();
		}
		while (true) {
			try {
				receiver.receive(receivePDU);
			} catch (IOException ex) {
				log.error(ex);
				ex.printStackTrace();
			}
			if (receivePDU != null) {
				log.info(receivePDU.getOpId());
				break;
			}
		}

		LWTRTPdu respondePdu = new LWTRTPdu();
		respondePdu.setRemotePort(receivePDU.getRemotePort());
		respondePdu.setRemoteAddress(receivePDU.getRemoteAddress());
		respondePdu.setSequenceNumber(receivePDU.getSequenceNumber());
		respondePdu.setOpId(2);

		try {
			recWra = new UdpSocketWrapper(firstPort);
		} catch (SocketException ex) {
			log.error(ex);
			ex.printStackTrace();
		}

		try {
			recWra.send(respondePdu);
		} catch (IOException ex) {
			log.error(ex);
			ex.printStackTrace();
		}

		LWTRTServiceImpl.socketmap.put((Integer) firstPort, recWra);
		log.debug(recWra.getLocalPort());

		LWTRTConnectionImpl con = new LWTRTConnectionImpl(adress, firstPort,
				adress, firstPort);

		recThread receiverThread2 = new recThread(receiver, con);
		receiverThread2.start();

		LWTRTServiceImpl.connectionMap.put((Integer) firstPort, con);
		this.firstPort++;
		return con;
	}

	/**
	 * Neue innerclass für das sharing
	 * 
	 * @author Florian Leicher
	 */
	public class shareThread extends Thread {

		LWTRTPdu toShare;
		LWTRTConnectionImpl sharing;

		public shareThread() {
		}

		public void run() {

			log.debug("sThread started");
			while (true) {
				if (!LWTRTServiceImpl.buffer.isEmpty()) {
					toShare = LWTRTServiceImpl.buffer.firstElement();
					this.sharing = LWTRTServiceImpl.connectionMap
							.get((Integer) toShare.getRemotePort());
					synchronized (sharing) {
						sharing.pngBuff.add(toShare);
					}
					LWTRTServiceImpl.buffer.remove(toShare);
				}

				{
					try {
						Thread.sleep(50);
					} catch (InterruptedException ex) {
						log.error(ex);
						ex.printStackTrace();
					}
				}

			}

		}
	}

	/**
	 * Neue innerclass welche einen UDPSocket launscht und in den Buffer legt.
	 * 
	 * @author Florian Leicher
	 * 
	 */
	public class recThread extends Thread {
		UdpSocketWrapper udpsocket;
		LWTRTConnectionImpl connection;

		public recThread(UdpSocketWrapper udpsocket, LWTRTConnectionImpl con) {
			this.udpsocket = udpsocket;
			this.connection = con;
			log.debug("Receivertread gestartet " + udpsocket.getLocalPort());
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			while (true) {
				LWTRTPdu receivePDU = new LWTRTPdu();

				try {
					udpsocket.receive(receivePDU);
				} catch (IOException ex) {
					ex.printStackTrace();
					log.info("Socket wurde geschlossen! " + ex);
					this.stop();
				}
				{
					this.connection.pngBuff.add(receivePDU);
				}

			}
		}

	}

}
