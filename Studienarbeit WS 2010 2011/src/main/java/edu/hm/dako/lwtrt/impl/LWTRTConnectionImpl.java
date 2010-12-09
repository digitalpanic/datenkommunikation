package edu.hm.dako.lwtrt.impl;

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

	private int seqnr;
	private int remPort;
	private int port;

	private long sequenceNumber;

	private Object userData;
	private UdpSocketWrapper udpw;

	private boolean disconnectSuccess;
	private boolean sendingSuccess;

	volatile Vector<LWTRTPdu> pingBuffer = new Vector<LWTRTPdu>();
	private volatile Vector<LWTRTPdu> pickupBuffer = new Vector<LWTRTPdu>();

	private Timer timer;
	private RecThread rThread;

	public LWTRTConnectionImpl(String adress2, int port2, String remAdress2,
			int remPort2) {
		this.port = port2;
		this.setAdress(adress2);
		this.remAdr = remAdress2;
		this.remPort = remPort2;
		this.sendingSuccess = false;

		udpw = LWTRTServiceImpl.socketmap.get((Integer) port);

		rThread = new RecThread(this);
		rThread.start();
		this.sequenceNumber = 1;
	}

	@Override
	// TODO Kommentare
	public void disconnect() throws LWTRTException {
		timer.start("disconnect");
		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remAdr);
		pdu.setRemotePort(remPort);
		pdu.setOpId(LWTRTPdu.OPID_DISCONNECT_REQ);

		// Senden und Prüfen, ob Response eingetroffen
		try {
			for (int i = 0; i < 3; i++) {
				udpw.send(pdu);
				for (int y = 0; y < 10; y++) {
					if (this.disconnectSuccess == true) {
						y = 10;
						i = 3;
						this.disconnectSuccess = false;
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

	@Override
	// TODO Kommentare
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

	@Override
	/**
	 * 
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
		pdu1.setSequenceNumber(sequenceNumber);
		try {
			for (int i = 0; i < 3; i++) {
				udpw.send(pdu1);
				for (int y = 0; y < 10; y++) {
					if (this.sendingSuccess == true) {
						y = 10;
						i = 3;
						this.sendingSuccess = false;
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

	@Override
	// TODO Kommentare
	public Object receive() throws LWTRTException {
		while (true) {
			if (!this.pingBuffer.isEmpty()) {
				LWTRTPdu lwtrtPdu = this.pingBuffer.firstElement();
				this.pingBuffer.remove(lwtrtPdu);
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

	@Override
	// TODO Kommentare
	public void ping() throws LWTRTException {
		timer.start("ping");
		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remAdr);
		pdu.setOpId(LWTRTPdu.OPID_PING_REQ);
		pdu.setRemotePort(remPort);
		pdu.setSequenceNumber(seqnr);
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

	// TODO Kommentare
	public void pingRSP() throws LWTRTException {
		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(this.remAdr);
		pdu.setRemotePort(this.remPort);
		pdu.setOpId(LWTRTPdu.OPID_PING_RSP);
		pdu.setSequenceNumber(seqnr);
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
		if (this.seqnr == 1) {
			this.seqnr = 0;
		} else if (this.seqnr == 0) {
			this.seqnr = 1;
		}
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getAdress() {
		return adress;
	}

	/**
	 * Thread for Reveiving
	 * 
	 * 
	 * @author Florian
	 * 
	 */
	private static class RecThread extends Thread {
		public static Logger log = Logger.getLogger(RecThread.class);
		private LWTRTConnectionImpl connection;

		private RecThread(LWTRTConnectionImpl connection) {
			this.connection = connection;
		}

		@SuppressWarnings("deprecation")
		public void run() {
			log.debug("Thread wurde gestartet");
			synchronized (connection) {
				if (!connection.pingBuffer.isEmpty()) {
					LWTRTPdu pdu = connection.pingBuffer.firstElement();
					log.debug("Erstes Bufferelement" + pdu.getOpId());

					switch (pdu.getOpId()) {
					case LWTRTPdu.OPID_CONNECT_REQ:
						log.debug("Connect Request für bestehende Verbindung erhalten. Paket wird verworfen");
						connection.pingBuffer.remove(pdu);
						break;
					case LWTRTPdu.OPID_CONNECT_RSP:
						log.debug("Connect Response für bestehende Verbindung erhalten. Paket wird verworfen");
						connection.pingBuffer.remove(pdu);
						break;
					case LWTRTPdu.OPID_DISCONNECT_REQ:
						LWTRTPdu respondeData2 = new LWTRTPdu();
						respondeData2
								.setSequenceNumber(pdu.getSequenceNumber());
						respondeData2.setRemoteAddress(connection.remAdr);
						respondeData2.setRemotePort(connection.remPort);

						respondeData2.setOpId(LWTRTPdu.OPID_DISCONNECT_RSP);

						try {
							connection.udpw.send(respondeData2);
							log.debug("Versendete Verbindungsabbauantwort");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						connection.pingBuffer.remove(pdu);
						try {
							connection.finalize();
						} catch (Throwable e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						this.stop();
						break;
					case LWTRTPdu.OPID_DISCONNECT_RSP:

						connection.pingBuffer.remove(pdu);
						try {
							connection.finalize();
						} catch (Throwable e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						this.stop();
						break;
					case LWTRTPdu.OPID_DATA_REQ:
						LWTRTPdu respondeData = new LWTRTPdu();
						respondeData.setSequenceNumber(pdu.getSequenceNumber());
						respondeData.setRemoteAddress(connection.remAdr);
						respondeData.setRemotePort(connection.remPort);
						respondeData.setOpId(LWTRTPdu.OPID_DATA_RSP);

						try {
							connection.udpw.send(respondeData);
							log.debug("Versendete Antwort");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						connection.pickupBuffer.add(pdu);
						connection.pingBuffer.remove(pdu);
						break;
					case LWTRTPdu.OPID_DATA_RSP:
						if (pdu.getSequenceNumber() == connection.sequenceNumber) {
							connection.sendingSuccess = true;
						}

						log.debug("Versand OK");
						connection.pingBuffer.remove(pdu);
						break;
					case LWTRTPdu.OPID_PING_REQ:
						try {
							connection.pingRSP();
							log.debug("Message erhalten:"
									+ pdu.getUserData().toString());
						} catch (LWTRTException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					case LWTRTPdu.OPID_PING_RSP:
						connection.pingBuffer.add(pdu);
						connection.pingBuffer.remove(pdu);
						break;
					}

				}

			}
		}
	}

	/*
	 * Getter und Setter
	 */

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
		return pingBuffer;
	}

	public void setPingBuffer(Vector<LWTRTPdu> pingBuffer) {
		this.pingBuffer = pingBuffer;
	}
}
