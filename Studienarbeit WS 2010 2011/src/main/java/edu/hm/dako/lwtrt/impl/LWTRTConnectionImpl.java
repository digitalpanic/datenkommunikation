package edu.hm.dako.lwtrt.impl;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.lwtrt.LWTRTConnection;
import edu.hm.dako.lwtrt.ex.LWTRTException;
import edu.hm.dako.lwtrt.pdu.LWTRTPdu;
import edu.hm.dako.udp.wrapper.UdpSocketWrapper;

/**
 * The Class ConnectionImpl.
 * 
 * @author Hochschule MÃ¼nchen
 * @version 1.0.0
 */
public class LWTRTConnectionImpl implements LWTRTConnection {

	private static Log log = LogFactory.getLog(LWTRTConnection.class);
	private String remoteAddress;
	private int seqnr;
	private int remotePort;
	private int port;
	private UdpSocketWrapper udpw;
	private boolean disconsuc;
	private Timer timer;

	public LWTRTConnectionImpl() {
		udpw = LWTRTServiceImpl.socketmap.get((Integer) port);
		// TODO Weiter ausfüllen mit leben
	}

	@Override
	// TODO Kommentare
	public void disconnect() throws LWTRTException {
		timer.start("disconnect");
		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remoteAddress);
		pdu.setRemotePort(remotePort);
		pdu.setOpId(LWTRTPdu.OPID_DISCONNECT_REQ);

		// Senden und Prüfen, ob Response eingetroffen
		try {
			for (int i = 0; i < 3; i++) {
				udpw.send(pdu);
				for (int y = 0; y < 10; y++) {
					if (this.disconsuc == true) {
						y = 10;
						i = 3;
						this.disconsuc = false;
					}
					Thread.sleep(1000);
				}
			}
		} catch (IOException ex) {
			log.error("ERROR: Fehler bei Disconnect " + ex);
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			log.error("ERROR: Disconnect Thread wurde unterborchen. " + ex);
			ex.printStackTrace();
		}
		setSeqNr();
		timer.stop();
	}

	@Override
	//TODO Kommentare
	public void acceptDisconnection() throws LWTRTException {
		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remoteAddress);
		pdu.setRemotePort(remotePort);
		pdu.setOpId(LWTRTPdu.OPID_DISCONNECT_RSP);
		try {
			udpw.send(pdu);
		} catch (SocketException ex) {
			ex.printStackTrace();
			log.error("ERROR: Socketerror: " + ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error("ERROR: IO Exceptoion: " + ex);
		}
	}

	@Override
	public void send(Object pdu) throws LWTRTException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object receive() throws LWTRTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	// TODO Kommentare
	public void ping() throws LWTRTException {
		timer.start("ping");
		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(remoteAddress);
		pdu.setOpId(LWTRTPdu.OPID_PING_REQ);
		pdu.setRemotePort(remotePort);
		pdu.setSequenceNumber(seqnr);
		setSeqNr();

		try {
			udpw.send(pdu);
		} catch (SocketException ex) {
			ex.printStackTrace();
			log.error("ERROR: Socketerror: " + ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error("ERROR: IO Exceptoion: " + ex);
		}
		timer.stop();
	}

	// TODO Kommentare
	public void pingRSP() throws LWTRTException {
		LWTRTPdu pdu = new LWTRTPdu();
		pdu.setRemoteAddress(this.remoteAddress);
		pdu.setRemotePort(this.remotePort);
		pdu.setOpId(LWTRTPdu.OPID_PING_RSP);
		pdu.setSequenceNumber(seqnr);
		try {
			udpw.send(pdu);
		} catch (SocketException ex) {
			ex.printStackTrace();
			log.error("ERROR: Socketerror: " + ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			log.error("ERROR: IO Exceptoion: " + ex);
		}
	}

	/**
	 * Ändert die bestehende Seqennumer von 1 auf 0 bzw. von 0 auf 1.
	 * 
	 */
	private void setSeqNr() {
		if (this.seqnr == 1) {
			this.seqnr = 0;
		} else if (this.seqnr == 0) {
			this.seqnr = 1;
		}
	}

}
