package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.MessaggioUtente;
import com.studiodomino.jplatform.shared.entity.UtenteEsterno;
import com.studiodomino.jplatform.shared.repository.MessaggioUtenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessaggioUtenteService {

    private final MessaggioUtenteRepository messaggioRepository;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    /**
     * Invia nuovo messaggio
     */
    public MessaggioUtente inviaMessaggio(
            UtenteEsterno mittente,
            String idDestinatario,
            String nomeDestinatario,
            String cognomeDestinatario,
            String emailDestinatario,
            String oggetto,
            String messaggio,
            String tablenameDestinatario) {

        MessaggioUtente msg = MessaggioUtente.builder()
                .idparent("0")
                .idMittente(mittente.getId().toString())
                .nomeMittente(mittente.getNome())
                .cognomeMittente(mittente.getCognome())
                .emailMittente(mittente.getEmail())
                .tablenameMittente("utenteesterno")
                .idDestinatario(idDestinatario)
                .nomeDestinatario(nomeDestinatario)
                .cognomeDestinatario(cognomeDestinatario)
                .emailDestinatario(emailDestinatario)
                .tablenameDestinatario(tablenameDestinatario)
                .oggetto(oggetto)
                .messaggio(messaggio)
                .dataInvio(DATE_FORMAT.format(new Date()))
                .stato("0")
                .statoMittente(0)
                .statoDestinatario(0)
                .build();

        return messaggioRepository.save(msg);
    }

    /**
     * Rispondi a messaggio esistente
     */
    public MessaggioUtente rispondiMessaggio(
            String idParent,
            UtenteEsterno mittente,
            String idDestinatario,
            String nomeDestinatario,
            String cognomeDestinatario,
            String emailDestinatario,
            String oggetto,
            String messaggio,
            String tablenameDestinatario) {

        MessaggioUtente msg = MessaggioUtente.builder()
                .idparent(idParent)
                .idMittente(mittente.getId().toString())
                .nomeMittente(mittente.getNome())
                .cognomeMittente(mittente.getCognome())
                .emailMittente(mittente.getEmail())
                .tablenameMittente("utenteesterno")
                .idDestinatario(idDestinatario)
                .nomeDestinatario(nomeDestinatario)
                .cognomeDestinatario(cognomeDestinatario)
                .emailDestinatario(emailDestinatario)
                .tablenameDestinatario(tablenameDestinatario)
                .oggetto("Re: " + oggetto)
                .messaggio(messaggio)
                .dataInvio(DATE_FORMAT.format(new Date()))
                .stato("0")
                .statoMittente(0)
                .statoDestinatario(0)
                .build();

        return messaggioRepository.save(msg);
    }

    /**
     * Carica messaggi ricevuti con thread (risposte)
     */
    public List<MessaggioUtente> getMessaggiRicevutiConThread(
            String idUtente, String tablename) {

        List<MessaggioUtente> messaggi = messaggioRepository
                .findMessaggiPrincipaliRicevuti(idUtente, tablename);

        // Carica risposte per ogni messaggio
        for (MessaggioUtente msg : messaggi) {
            List<MessaggioUtente> risposte = messaggioRepository
                    .findRisposteMessaggio(msg.getId().toString());
            msg.setReplyMessaggi(risposte);
        }

        return messaggi;
    }

    /**
     * Marca messaggio come letto
     */
    public void marcaComeLetto(Integer idMessaggio) {
        messaggioRepository.findById(idMessaggio).ifPresent(msg -> {
            msg.marcaComeLetto();
            messaggioRepository.save(msg);
        });
    }

    /**
     * Conta nuovi messaggi per utente
     */
    public long contaMessaggiNonLetti(String idUtente, String tablename) {
        return messaggioRepository.countMessaggiNonLetti(idUtente, tablename);
    }

    /**
     * Archivia messaggio (destinatario)
     */
    public void archiviaMessaggio(Integer idMessaggio, boolean comeDestinatario) {
        messaggioRepository.findById(idMessaggio).ifPresent(msg -> {
            if (comeDestinatario) {
                msg.archiviaPerDestinatario();
            } else {
                msg.archiviaPerMittente();
            }
            messaggioRepository.save(msg);
        });
    }

    /**
     * Cancella messaggio (soft delete)
     */
    public void cancellaMessaggio(Integer idMessaggio, boolean comeDestinatario) {
        messaggioRepository.findById(idMessaggio).ifPresent(msg -> {
            if (comeDestinatario) {
                msg.cancellaPerDestinatario();
            } else {
                msg.cancellaPerMittente();
            }
            messaggioRepository.save(msg);
        });
    }

    /**
     * Carica conversazione tra due utenti
     */
    public List<MessaggioUtente> getConversazione(
            String idUtente1, String idUtente2) {
        return messaggioRepository.findConversazione(idUtente1, idUtente2);
    }
}