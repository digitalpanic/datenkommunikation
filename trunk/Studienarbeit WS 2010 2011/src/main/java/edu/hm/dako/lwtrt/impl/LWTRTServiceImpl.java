package edu.hm.dako.lwtrt.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Vector;
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
// TODO KOmmentare
public class LWTRTServiceImpl implements LWTRTService {

	// variable for logging
	private static Log log = LogFactory.getLog(LWTRTService.class);

	// variable for socketmap
	static ConcurrentHashMap<Integer, UdpSocketWrapper> socketmap = new ConcurrentHashMap<Integer, UdpSocketWrapper>();

	// variable for portnummer
	int port;

	private String adress;
	private UdpSocketWrapper receiveWrapper;
	private int sequenceNumber;

	private static volatile Vector<LWTRTPdu> pingBuffer = new Vector<LWTRTPdu>();

	protected static ConcurrentHashMap<Integer, LWTRTConnectionImpl> connectionMap = new ConcurrentHashMap<Integer, LWTRTConnectionImpl>();
	private int firstPort;

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
	 * @author Florian Leicher
	 */
	public void unregister() throws LWTRTException {
		UdpSocketWrapper udpswu;

		// TODO Prüfen, ob Logfiles korrekt geschrieben werden
		try {
			udpswu = LWTRTServiceImpl.socketmap.get((Integer) port);
			log.debug("Port " + port + " wurde aus Socketmap geholt");
			socketmap.remove((Integer) port);
			log.debug("Port " + port + " wurde aus Socketmap entfernt.");
			udpswu.close();
			log.debug("SocketWrapper wurde geschlossen.");
		} catch (Exception ex) {
			log.error("Fehler bei De-Regestrierung der Ports:" + port + " "
					+ ex);
			ex.printStackTrace();
		}

	}

	@Override
	// TODO Kommentare
	public LWTRTConnection connect(String remoteAddress, int remotePort)
			throws LWTRTException {
		UdpSocketWrapper udpSocketWra;
		udpSocketWra = socketmap.get(port);

		if (udpSocketWra == null) {
			try {
				udpSocketWra = new UdpSocketWrapper(port);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("--socketmap:" + udpSocketWra);
		LWTRTPdu Pdu = new LWTRTPdu();
		Pdu.setOpId(LWTRTPdu.OPID_CONNECT_REQ);
		sequenceNumber = 0;
		Pdu.setSequenceNumber(sequenceNumber);

		Calendar cal = Calendar.getInstance();
		long time;

		LWTRTPdu receivePdu = new LWTRTPdu();
		System.out.println("While schleife gestartet");
		for (int retries = 0; retries < 3; retries++) {
			try {
				udpSocketWra.send(Pdu);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			time = cal.getTimeInMillis() + 5000;

			try {
				while (cal.getTimeInMillis() < time) {

					udpSocketWra.receive(receivePdu);
					if (receivePdu != null) {
						System.out.println("While Schleife beendet!!!!!!");
						break;
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (receivePdu != null) {
				System.out.println("For Schleife beendet!!!!!!");
				break;
			}
		}

		LWTRTConnectionImpl con = new LWTRTConnectionImpl(adress, port,
				receivePdu.getRemoteAddress(), receivePdu.getRemotePort());
		LWTRTServiceImpl.connectionMap.put(port, con);
		log.debug("Antwort:" + receivePdu.getRemotePort() + "-"
				+ receivePdu.getOpId() + "-" + receivePdu.getSequenceNumber());
		recThread rt = new recThread(udpSocketWra, con);

		rt.start();
		return con;
	}

	@Override
	// TODO Kommentare anpassen
	public LWTRTConnection accept() throws LWTRTException {
		LWTRTPdu receivePDU = new LWTRTPdu();
		UdpSocketWrapper receiver = LWTRTServiceImpl.socketmap
				.get((Integer) port);
		log.debug("Receiver Port:"
				+ LWTRTServiceImpl.socketmap.get((Integer) port).getLocalPort());
		try {
			adress = (String) InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			try {

				receiver.receive(receivePDU);

			}
			// TODO Auto-generated catch block
			catch (IOException e) {
				e.printStackTrace();
			}
			if (receivePDU != null) {
				System.out.println(receivePDU.getOpId());
				break;
			}
		}
		LWTRTPdu respondePdu = new LWTRTPdu();
		respondePdu.setRemotePort(receivePDU.getRemotePort());
		respondePdu.setRemoteAddress(receivePDU.getRemoteAddress());
		respondePdu.setSequenceNumber(receivePDU.getSequenceNumber());
		respondePdu.setOpId(2);

		try {
			receiveWrapper = new UdpSocketWrapper(firstPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			receiveWrapper.send(respondePdu);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LWTRTServiceImpl.socketmap.put((Integer) firstPort, receiveWrapper);
		log.debug(receiveWrapper.getLocalPort());

		LWTRTConnectionImpl con = new LWTRTConnectionImpl(adress, firstPort,
				adress, firstPort);
		recThread receiverThread2 = new recThread(receiver, con);
		receiverThread2.start();

		LWTRTServiceImpl.connectionMap.put((Integer) firstPort, con);
		this.firstPort++;
		return con;
	}

	// TODO KOmmentare
	public class verteilerThread extends Thread {
		LWTRTPdu zuVerteilen;
		LWTRTConnectionImpl verteilen;

		public verteilerThread() {

		}

		@Override
		public void run() {

			log.debug("Verteiler gestartet");
			while (true) {
				if (!LWTRTServiceImpl.pingBuffer.isEmpty()) {
					zuVerteilen = LWTRTServiceImpl.pingBuffer.firstElement();
					this.verteilen = LWTRTServiceImpl.connectionMap
							.get((Integer) zuVerteilen.getRemotePort());
					synchronized (verteilen) {
						verteilen.pingBuffer.add(zuVerteilen);
					}
					LWTRTServiceImpl.pingBuffer.remove(zuVerteilen);
				}
				{
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}
	}

	/**
	 * Erstellt eine neue Klasse und startet Thread. Lauscht nach eingehenden
	 * UdpSockets und legt diese in ReceiveBuffer.
	 * 
	 * @author Florian Leicher
	 * 
	 */
	// TODO KOmmentare
	public class recThread extends Thread {
		UdpSocketWrapper udpsocket;
		LWTRTConnectionImpl connection;

		public recThread(UdpSocketWrapper udpsocket, LWTRTConnectionImpl con) {
			this.udpsocket = udpsocket;
			this.connection = con;
			log.debug("Receivertread für " + this + "gestartet "
					+ udpsocket.getLocalPort());
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			while (true) {
				LWTRTPdu receivePDU = new LWTRTPdu();

				try {
					udpsocket.receive(receivePDU);
					// TODO Auto-generated catch block
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Socket ist geschlossen!");
					this.stop();
				}
				{
					this.connection.pingBuffer.add(receivePDU);
					log.debug(" " + receivePDU.getOpId());
				}

			}
		}

	}

}
