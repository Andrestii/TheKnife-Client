package com.theknife;

import java.io.Serializable;

/**
 * Modello che rappresenta un utente del sistema.
 * <p>
 * Contiene i dati anagrafici e le credenziali dell'utente ed è serializzabile
 * per il trasferimento tra client e server.
 * </p>
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cognome;
    private String dataNascita;
    private String domicilio;
    private String username;
    private String password;
    private String ruolo;

    /**
     * Crea un nuovo utente con tutti i dati principali.
     *
     * @param nome        nome dell'utente
     * @param cognome     cognome dell'utente
     * @param dataNascita data di nascita dell'utente
     * @param domicilio   domicilio dell'utente
     * @param username    username dell'utente
     * @param password    password dell'utente
     * @param ruolo       ruolo dell'utente (es. CLIENTE, RISTORATORE)
     */
    public Utente(String nome, String cognome, String dataNascita, String domicilio, String username, String password,
            String ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
        this.username = username;
        this.password = password;
        this.ruolo = ruolo;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRuolo() {
        return ruolo;
    }

    @Override
    public String toString() {
        return nome + ";" + cognome + ";" + dataNascita + ";" + domicilio + ";" + username + ";" + password + ";"
                + ruolo;
    }
}
