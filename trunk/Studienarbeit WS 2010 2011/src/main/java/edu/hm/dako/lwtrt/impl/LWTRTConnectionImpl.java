package edu.hm.dako.lwtrt.impl;

//Imports
import java.io.IOException;
import java.net.*;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.ex.LWTRTException;
import edu.hm.dako.lwtrt.pdu.LWTRTPdu;
import edu.hm.dako.udp.wrapper.UdpSocketWrapper;

/**
 * The Class ConnectionImpl.
 * 
 * @author Florian Leicher
 * @version 1.0.0
 */
public class LWTRTConnectionImpl implements LWTRTConnection {

	private static Log log = LogFactory.getLog(LWTRTConnection.class);

	private String remAdr;
	private String adress;

	private int seqNr;
	private int remPort;
	private int port;

	private Object userData;
	private UdpSocketWrapper udpw;

	private boolean discSuc;
	private boolean sendSuc;

	volatile Vector<LWTRTPdu> pngBuff = new Vector<LWTRTPdu>();
	private volatile Vector<LWTRTPdu> pickUpBuff = new Vector<LWTRTPdu>();

	private Timer timer;
	private RecThread rThread;

	public LWTRTConnectionImpl(String adress2, int port2, String remAdress2,
			int remPort2) {
		this.port = port2;
		this.setAdress(adress2);
		this.remAdr = remAdress2;
		this.remPort = remPort2;
		this.sendSuc = false;

		udpw = LWTRTServiceImpl.socketmap.get((Integer) port);

		rThread = new RecThread(this);
		rThread.start();
		this.seqNr = 1;
	}

	/**
	 * Akzeptieren des Verbindungsabbau.
	 * 
	 * @author Florian Leicher
	 */
	public void acceptDisconnection() throws LWTRTException {

		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remAdr);
		pdu.setRemotePort(remPort);
		pdu.setOpId(LWTRTPdu.OPID_DISCONNECT_RSP);

		try {
			udpw.send(pdu);
		} catch (SocketException ex) {
			ex.printStackTrace();
			log.error("Socketerror: " + ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error("IO Exceptoion: " + ex);
		}
	}

	/**
	 * Abbau der Verbindung
	 * 
	 * @author Florian Leicher
	 * @throws LWTRTException
	 * 
	 */
	public void disconnect() throws LWTRTException {

		timer.start("disconnect");

		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remAdr);
		pdu.setRemotePort(remPort);
		pdu.setOpId(LWTRTPdu.OPID_DISCONNECT_REQ);

		try {
			for (int i = 0; i < 3; i++) {
				udpw.send(pdu);
				for (int y = 0; y < 10; y++) {
					if (this.discSuc == true) {
						y = 10;
						i = 3;
						this.discSuc = false;
					}
					Thread.sleep(1000);
				}
			}
		} catch (IOException ex) {
			log.error("Fehler bei Disconnect " + ex);
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			log.error("Disconnect Thread wurde unterborchen. " + ex);
			ex.printStackTrace();
		}

		setSeqNr();
		timer.stop();

	}

	/**
	 * Sendeversuch mit evtl. Wiederholung
	 * 
	 * @author Florian Leicher
	 */
	public void send(Object pdu) throws LWTRTException {
		timer.start("send");

		LWTRTPdu pdu1 = new LWTRTPdu();
		pdu1.setRemoteAddress(remAdr);
		pdu1.setRemotePort(remPort);
		pdu1.setOpId(LWTRTPdu.OPID_DATA_REQ);
		pdu1.setUserData(userData);
		pdu1.setSequenceNumber(seqNr);

		try {
			for (int i = 0; i < 3; i++) {
				udpw.send(pdu1);
				for (int y = 0; y < 10; y++) {
					if (this.sendSuc == true) {
						y = 10;
						i = 3;
						this.sendSuc = false;
					}
					Thread.sleep(1000);
				}
			}

		} catch (IOException eio) {
			eio.printStackTrace();
			log.error(eio);
		} catch (InterruptedException eie) {
			eie.printStackTrace();
			log.error(eie);
		}

		setSeqNr();
		timer.stop();

	}

	/**
	 * Empfang und sortieren der Daten vom Sender.
	 * 
	 * @author Florian Leicher
	 * @throws LWTRTException
	 */
	public Object receive() throws LWTRTException {

		while (true) {

			if (!this.pngBuff.isEmpty()) {
				LWTRTPdu lwtrtPdu = this.pngBuff.firstElement();
				this.pngBuff.remove(lwtrtPdu);
				return lwtrtPdu.getUserData();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException iex) {
				iex.printStackTrace();
				log.error("Empfangsfehler: " + iex);
			}
		}
	}

	/**
	 * Anfrage zur Lebendüberwachung
	 * 
	 * @throws LWTRTException
	 * @author Florian Leicher
	 */
	public void ping() throws LWTRTException {

		timer.start("ping");

		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remAdr);
		pdu.setOpId(LWTRTPdu.OPID_PING_REQ);
		pdu.setRemotePort(remPort);
		pdu.setSequenceNumber(seqNr);

		setSeqNr();

		try {
			udpw.send(pdu);
		} catch (SocketException ex) {
			ex.printStackTrace();
			log.error("Socketerror: " + ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error("IO Exceptoion: " + ex);
		}

		timer.stop();
	}

	/**
	 * Antwort zur Lebendüberwachung
	 * 
	 * @author Florian Leicher
	 * @throws LWTRTException
	 */
	public void pingRSP() throws LWTRTException {

		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(this.remAdr);
		pdu.setRemotePort(this.remPort);
		pdu.setOpId(LWTRTPdu.OPID_PING_RSP);
		pdu.setSequenceNumber(seqNr);

		try {
			udpw.send(pdu);
		} catch (SocketException ex) {
			ex.printStackTrace();
			log.error("Socketerror: " + ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error("IO Exceptoion: " + ex);
		}

	}

	/**
	 * Ändert die bestehende Seqennumer von 1 auf 0 bzw. von 0 auf 1.
	 * 
	 * @author Florian Leicher
	 */
	private void setSeqNr() {
		if (this.seqNr == 1) {
			this.seqNr = 0;
		} else if (this.seqNr == 0) {
			this.seqNr = 1;
		}
	}

	/**
	 * Thread für den Empfang.
	 * 
	 * @author Florian
	 */
	private static class RecThread extends Thread {

		public static Logger log = Logger.getLogger(RecThread.class);
		private LWTRTConnectionImpl con;

		private RecThread(LWTRTConnectionImpl connection) {
			this.con = connection;
		}

		@SuppressWarnings("deprecation")
		public void run() {
			log.info("Thread wurde gestartet");
			synchronized (con) {

				if (!con.pngBuff.isEmpty()) {
					LWTRTPdu pdu = con.pngBuff.firstElement();
					log.info("Erstes Bufferelement" + pdu.getOpId());

					// Switch block für die verschiedenen Nachrichtentypen
					switch (pdu.getOpId()) {

					// OPID für Anfrage Verbindungsaufbau
					case LWTRTPdu.OPID_CONNECT_REQ:
						con.pngBuff.remove(pdu);
						break;

					// OPID für Antwort Verbindungsaufbau
					case LWTRTPdu.OPID_CONNECT_RSP:
						con.pngBuff.remove(pdu);
						break;

					// OPID für Anfrage Verbindungsabbau
					case LWTRTPdu.OPID_DISCONNECT_REQ:

						LWTRTPdu respondeData2 = new LWTRTPdu();
						respondeData2
								.setSequenceNumber(pdu.getSequenceNumber());
						respondeData2.setRemoteAddress(con.remAdr);
						respondeData2.setRemotePort(con.remPort);
						respondeData2.setOpId(LWTRTPdu.OPID_DISCONNECT_RSP);

						try {
							con.udpw.send(respondeData2);
						} catch (IOException ex) {
							log.error("Fehler beim Verbindungsaufbau: " + ex);
							ex.printStackTrace();
						}

						con.pngBuff.remove(pdu);

						try {
							con.finalize();
						} catch (Throwable ex2) {
							log.error("Fehler beim Verbindungsaufbau: " + ex2);
							ex2.printStackTrace();
						}

						this.stop();

						break;

					// OPID für Antwort Verbindungsabbau
					case LWTRTPdu.OPID_DISCONNECT_RSP:

						con.pngBuff.remove(pdu);

						try {
							con.finalize();
						} catch (Throwable ex) {
							log.error("Fehler beim Verbindungsabbau: " + ex);
							ex.printStackTrace();
						}

						this.stop();
						break;

					// OPID für Anfrage DatenSenden
					case LWTRTPdu.OPID_DATA_REQ:

						LWTRTPdu respondeData = new LWTRTPdu();
						respondeData.setSequenceNumber(pdu.getSequenceNumber());
						respondeData.setRemoteAddress(con.remAdr);
						respondeData.setRemotePort(con.remPort);
						respondeData.setOpId(LWTRTPdu.OPID_DATA_RSP);

						try {
							con.udpw.send(respondeData);
						} catch (IOException ex) {
							log.error("Fehler bei der Übermittlung der Nachricht: "
									+ ex);
							ex.printStackTrace();
						}
						con.pickUpBuff.add(pdu);
						con.pngBuff.remove(pdu);

						break;

					// OPID für Antwort DatenSenden
					case LWTRTPdu.OPID_DATA_RSP:

						if (pdu.getSequenceNumber() == con.seqNr) {
							con.sendSuc = true;
						}

						log.info("Versand Erfolgreich");
						con.pngBuff.remove(pdu);
						break;

					// OPID für Anfrage Ping
					case LWTRTPdu.OPID_PING_REQ:
						try {
							con.pingRSP();
						} catch (LWTRTException ex) {
							log.error("Fehler beim Versenden des Ping: " + ex);
							ex.printStackTrace();
						}

						// OPID für Antwort Ping
					case LWTRTPdu.OPID_PING_RSP:
						con.pngBuff.add(pdu);
						con.pngBuff.remove(pdu);
						break;
					}

				}

			}
		}
	}

	// Getter und Setter
	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getAdress() {
		return adress;
	}

	public String getRemAdr() {
		return remAdr;
	}

	public void setRemAdr(String remAdr) {
		this.remAdr = remAdr;
	}

	public int getRemPort() {
		return remPort;
	}

	public void setRemPort(int remPort) {
		this.remPort = remPort;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Vector<LWTRTPdu> getPingBuffer() {
		return pngBuff;
	}

	public void setPingBuffer(Vector<LWTRTPdu> pingBuffer) {
		this.pngBuff = pingBuffer;
	}
}
