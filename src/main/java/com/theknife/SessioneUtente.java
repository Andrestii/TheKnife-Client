/**
 * Autori del progetto:
 *
 * - Lorenzo De Paoli
 *   Matricola: 753577
 *   Sede: VA
 *
 * - Andrea Onesti
 *   Matricola: 754771
 *   Sede: VA
 *
 * - Weili Wu
 *   Matricola: 752602
 *   Sede: VA
 */
package com.theknife;

/**
 * Singleton che rappresenta la sessione corrente dell'utente.
 * Contiene tutti i dati dell'utente loggato o registrato
 * e può essere utilizzato da qualsiasi controller per accedere
 * o modificare i dati della sessione.
 */
public class SessioneUtente {

    // -------------------------
    //     Singleton pattern
    // -------------------------
    private static SessioneUtente instance;

    private SessioneUtente() { }

    /**
     * Restituisce l'istanza unica della sessione utente.
     *
     * @return istanza di {@link SessioneUtente}
     */
    public static SessioneUtente getInstance() {
        if (instance == null) {
            instance = new SessioneUtente();
        }
        return instance;
    }

    // -------------------------
    //     Attributi utente
    // -------------------------
    private String nome;
    private String cognome;
    private String dataNascita;
    private String luogo;
    private String username;
    private String password;
    private Ruolo ruolo;

    // -------------------------
    //     Getter e Setter
    // -------------------------
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getDataNascita() { return dataNascita; }
    public void setDataNascita(String dataNascita) { this.dataNascita = dataNascita; }

    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }

    // -------------------------
    //     Metodi di utilità
    // -------------------------
    /**
     * Cancella completamente la sessione corrente eliminando l'istanza singleton.
     */
    public void clear() {
        instance = null;
    }

    /**
     * Resetta i dati della sessione mantenendo l'istanza attiva.
     */
    public void reset() {
        nome = null;
        cognome = null;
        dataNascita = null;
        luogo = null;
        username = null;
        password = null;
        ruolo = null;
    }

    /**
     * Restituisce una rappresentazione testuale della sessione,
     * utile per debug e logging.
     *
     * @return stringa descrittiva della sessione
     */
    public void stampaDettagli() { // Metodo di debug
    System.out.println("=== SESSIONE UTENTE ===");
    System.out.println("Ruolo: " + ruolo);
    System.out.println("Nome: " + nome);
    System.out.println("Cognome: " + cognome);
    System.out.println("Data di nascita: " + dataNascita);
    System.out.println("Luogo (posizione/domicilio): " + luogo);
    System.out.println("Username: " + username);
    System.out.println("Password: " + password);
    System.out.println("=========================");
}

    @Override
    public String toString() {
        return String.format("SessioneUtente[nome=%s, cognome=%s, username=%s, ruolo=%s]",
                nome, cognome, username, ruolo);
    }
}
