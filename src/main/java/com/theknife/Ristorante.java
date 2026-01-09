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

import java.io.Serializable;

/**
 * Modello che rappresenta un ristorante.
 * <p>
 * Contiene le informazioni anagrafiche, logistiche e commerciali del ristorante
 * ed è serializzabile per il trasferimento tra client e server.
 * </p>
 */
public class Ristorante implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String nazione;
    private String citta;
    private String indirizzo;
    private double latitudine;
    private double longitudine;
    private int fasciaPrezzo;
    private boolean delivery;
    private boolean prenotazione;
    private String tipoCucina;
    private String idRistoratore;

    /**
     * Crea un nuovo ristorante con tutti i dati principali.
     *
     * @param id            identificativo univoco del ristorante
     * @param nome          nome del ristorante
     * @param nazione       nazione in cui si trova il ristorante
     * @param citta         città del ristorante
     * @param indirizzo     indirizzo completo
     * @param latitudine    coordinata geografica di latitudine
     * @param longitudine   coordinata geografica di longitudine
     * @param fasciaPrezzo  fascia di prezzo del ristorante
     * @param delivery      indica se è disponibile il servizio di delivery
     * @param prenotazione  indica se è possibile prenotare
     * @param tipoCucina    tipologia di cucina offerta
     * @param idRistoratore identificativo del ristoratore proprietario
     */
    public Ristorante(int id, String nome, String nazione, String citta, String indirizzo,
            double latitudine, double longitudine, int fasciaPrezzo,
            boolean delivery, boolean prenotazione, String tipoCucina,
            String idRistoratore) {

        this.id = id;
        this.nome = nome;
        this.nazione = nazione;
        this.citta = citta;
        this.indirizzo = indirizzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.fasciaPrezzo = fasciaPrezzo;
        this.delivery = delivery;
        this.prenotazione = prenotazione;
        this.tipoCucina = tipoCucina;
        this.idRistoratore = idRistoratore;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNazione() {
        return nazione;
    }

    public String getCitta() {
        return citta;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public double getLat() {
        return latitudine;
    }

    public double getLon() {
        return longitudine;
    }

    public int getPrezzo() {
        return fasciaPrezzo;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public boolean isPrenotazione() {
        return prenotazione;
    }

    public String getTipoCucina() {
        return tipoCucina;
    }

    public String getIdRistoratore() {
        return idRistoratore;
    }

    @Override
    public String toString() {
        return nome + " - " + citta + " [" + tipoCucina + "]";
    }
}
