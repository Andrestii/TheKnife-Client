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
 * Rappresenta una risposta generica del server verso il client.
 * <p>
 * Contiene lo stato dell'operazione (es. "OK" o "ERROR") e un payload opzionale
 * con i dati restituiti dal server (oggetti singoli o collezioni).
 * </p>
 */
public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public String status; // "OK" o "ERROR"
    private Object payload; // può contenere DTO o liste di DTO

    /**
     * Crea una nuova risposta del server.
     *
     * @param status  stato dell'operazione ("OK" o "ERROR")
     * @param payload contenuto della risposta (può essere {@code null})
     */
    public ServerResponse(String status, Object payload) {
        this.status = status;
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public Object getPayload() {
        return payload;
    }
}
