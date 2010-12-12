package edu.hm.dako.lwtrt;

import edu.hm.dako.chatsession.BaseSessionService;
import edu.hm.dako.lwtrt.ex.LWTRTException;

/**
 * Mit dem {@link LWTRTService} wird dem Dienstnehmer ({@link BaseSessionService})
 * die Nutzung eines Ports ermöglicht.<p>
 * Ein Dienstnehmer hat die folgenden drei Schritte vorzunehmen:<p>
 * 1.  Einen Port registrieren
 * 2a. Eine Verbindung aktiv aufbauen (connect)
 * 2b. Auf eine Verbindung passiv warten (listen)
 * 3.  Nach der Nutzung die Registrierung des Ports aufheben.
 * 
 * @author dako.cs.hm.edu
 */
public interface LWTRTService {

	/**
	 * Für den Dienstnehmer wird ein Port registriert.<p>
	 * Nach der Registrierung ist der lokale Port exklusiv für den Dienstnehmer gebunden.
	 * Ab dem Zeitpunkt der Registrierung werden eingehende Verbindungswünsche entgegen
	 * genommen. Akzeptiert der Dienstnehmer die Verbindungswünsch nicht rechtzeitig,
	 * wird der Verbindungsaufbau abgebrochen. Nach der Registrierung kann der Dienstnehmer
	 * beginnen aktiv eine Verbindung aufzubauen.<p>
	 * Ein Port kann nur einmal (auch durch andere Prozesse) registriert werden.
	 * @param localPort Nummer des Ports (lokal) der registriert werden soll
	 * @throws LWTRTException Fehler beim Registrieren (zB Port wird schon genutzt oder schon ein Port registriert)
	 */
    public void register(int localPort) throws LWTRTException;
    
    /**
     * Die Registrierung eines Ports wird aufgehoben.<p>
     * Die exklusive Nutzung des Ports für den Dienstnehmer wird aufgehoben. Ab dem
     * Zeitpunkt der Deregistierung werden keine Verbindungswünsch mehr berücksichtigt.
     * Auch eingehende Verbindungswünsche in der Warteschlange werden abgebaut.
     * @throws LWTRTException Fehler beim Aufheben der Registrierung (zB es war kein Port registriert)
     */
    public void unregister() throws LWTRTException;
    
    /**
     * Aktiver Verbindungsaufbau.<p>
     * Über den zuvor registrierten Port wird aktiv eine Verbindung aufgebaut. Beim
     * Verbindungsaufbau wird auf eine Bestätigung des Verbindungspartners gewartet (Timeout).
     * Der Verbindungsaufbau kann somit nur erfolgreich abgeschlossen werden, wenn der
     * Verbindungspartner auf dem angegebnen Port auf eingehende Verbindungswünsche wartet.
     * @param remoteAddress Adresse des Verbindungspartners
     * @param remotePort Port-Nummer des Verbindungspartners
     * @return Verbindung (nach dem Verbindungsaufbau) die bereit zum Senden von Daten ist
     * @throws LWTRTException Fehler beim Verbindungsaufbau (zB. Timeout)
     */
    public LWTRTConnection connect(String remoteAddress, int remotePort) throws LWTRTException;

	
    /**
     * Passiver Verbindungsaufbau.<p>
     * Auf einem zuvor registrierten Port wird auf eingehende Verbindungswünsche passiv gewartet.
     * Geht von einem Verbindungspartner eine Anfrage ein, wird das Verbindungsobjekt dem
     * Dienstnehmer übergeben. Innerhalb des Timeouts muss der Dienstnehmer die Verbindung
     * akzeptieren. Erst nach dem explizieten Akzeptieren der Verbindung ist der Aufbau erfolgt.
     * Erfolgt innerhalb des Timeout keine Akzptierung, wird die Verbingung nicht augebaut.
     * @param port 
     * @see LWTRTConnection#acceptConnection()
     * @return Verbindung (vor dem Akzeptieren) - noch nicht bereit zum Senden von Daten
     * @throws LWTRTException Fehler beim Verbindungsaufbau
     */
    
    public LWTRTConnection accept() throws LWTRTException;

}
