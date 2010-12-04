package edu.hm.dako.lwtrt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Diese Klasse implemetiert einen Timer um die Zeit die benötigt wird ein
 * Codestück (task) auszüführen zu tracken.
 * 
 * 
 * @author Florian Leicher
 * @version 1.0.0
 * 
 */
public class Timer {
	// Aktuelle Systemzeit in nanosekunden
	private long time = 0;
	// Task, auf welchen der Timer angesetzt wird
	private String task;
	// Loggingvariable
	private static Log log = LogFactory.getLog(Timer.class);

	/**
	 * Diese Methode startet den Timer und gibt eine entsprechende Meldung in
	 * das Logfile aus
	 * 
	 * @param task
	 *            Gibt den Namen des Tasks an, auf welchen der Timer angewendet
	 *            wird.
	 */
	public void start(String task) {
		this.task = task;
		time = System.nanoTime();
		log.info("Timer für den Task " + task + " ist mit " + time
				+ " gestartet");
	}

	/**
	 * Methode zum Stoppen des Timers.
	 * 
	 */
	public void stop() {
		time = System.nanoTime() - time;
		log.info("Timer für den Task " + task + " hat nach " + time
				+ " gestoppt");
	}

	/**
	 * Getter
	 * 
	 * @return Aktueller Timerstand
	 */
	public long getTime() {
		return time;
	}

}