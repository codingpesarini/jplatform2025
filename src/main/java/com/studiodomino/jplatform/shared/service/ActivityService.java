package com.studiodomino.jplatform.shared.service;

import com.studiodomino.jplatform.shared.entity.Activity;
import com.studiodomino.jplatform.shared.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    /**
     * Crea nuova attività
     */
    public Activity creaAttivita(
            Integer idutente,
            Integer idmsgutente,
            String tipoattivita,
            String statourgenza,
            String note) {

        String now = DATE_FORMAT.format(new Date());

        Activity activity = Activity.builder()
                .idutente(idutente)
                .idmsgutente(idmsgutente)
                .tipoattivita(tipoattivita)
                .dataapertura(now)
                .datacompletamento("")
                .statourgenza(statourgenza)
                .statocompletamento("aperta")
                .note(note)
                .noteoperative("")
                .logattivita("Attività creata: " + now)
                .allegati("")
                .build();

        return activityRepository.save(activity);
    }

    /**
     * Avvia attività
     */
    public void avviaAttivita(Integer idActivity) {
        activityRepository.findById(idActivity).ifPresent(activity -> {
            activity.avviaAttivita();
            String entry = "Attività avviata: " + DATE_FORMAT.format(new Date());
            activity.aggiungiLogEntry(entry);
            activityRepository.save(activity);
        });
    }

    /**
     * Completa attività
     */
    public void completaAttivita(Integer idActivity, String noteFinal) {
        activityRepository.findById(idActivity).ifPresent(activity -> {
            String now = DATE_FORMAT.format(new Date());
            activity.completaAttivita(now);

            if (noteFinal != null && !noteFinal.isEmpty()) {
                activity.setNoteoperative(
                        activity.getNoteoperative() + "\n" + noteFinal
                );
            }

            activity.aggiungiLogEntry("Attività completata: " + now);
            activityRepository.save(activity);
        });
    }

    /**
     * Sospendi attività
     */
    public void sospendiAttivita(Integer idActivity, String motivo) {
        activityRepository.findById(idActivity).ifPresent(activity -> {
            activity.sospendiAttivita();
            String entry = String.format(
                    "Attività sospesa: %s - Motivo: %s",
                    DATE_FORMAT.format(new Date()),
                    motivo
            );
            activity.aggiungiLogEntry(entry);
            activityRepository.save(activity);
        });
    }

    /**
     * Annulla attività
     */
    public void annullaAttivita(Integer idActivity, String motivo) {
        activityRepository.findById(idActivity).ifPresent(activity -> {
            activity.annullaAttivita();
            String entry = String.format(
                    "Attività annullata: %s - Motivo: %s",
                    DATE_FORMAT.format(new Date()),
                    motivo
            );
            activity.aggiungiLogEntry(entry);
            activityRepository.save(activity);
        });
    }

    /**
     * Aggiorna urgenza
     */
    public void aggiornaUrgenza(Integer idActivity, String nuovaUrgenza) {
        activityRepository.findById(idActivity).ifPresent(activity -> {
            String vecchiaUrgenza = activity.getStatourgenza();
            activity.setStatourgenza(nuovaUrgenza);

            String entry = String.format(
                    "Urgenza modificata: %s - Da '%s' a '%s'",
                    DATE_FORMAT.format(new Date()),
                    vecchiaUrgenza,
                    nuovaUrgenza
            );
            activity.aggiungiLogEntry(entry);
            activityRepository.save(activity);
        });
    }

    /**
     * Aggiungi nota operativa
     */
    public void aggiungiNotaOperativa(Integer idActivity, String nota) {
        activityRepository.findById(idActivity).ifPresent(activity -> {
            String timestamp = DATE_FORMAT.format(new Date());
            String notaCompleta = String.format("[%s] %s", timestamp, nota);

            String noteEsistenti = activity.getNoteoperative();
            if (noteEsistenti == null || noteEsistenti.isEmpty()) {
                activity.setNoteoperative(notaCompleta);
            } else {
                activity.setNoteoperative(noteEsistenti + "\n" + notaCompleta);
            }

            activity.aggiungiLogEntry("Nota operativa aggiunta: " + timestamp);
            activityRepository.save(activity);
        });
    }

    /**
     * Aggiungi allegato
     */
    public void aggiungiAllegato(Integer idActivity, String pathAllegato) {
        activityRepository.findById(idActivity).ifPresent(activity -> {
            String allegatiEsistenti = activity.getAllegati();

            if (allegatiEsistenti == null || allegatiEsistenti.isEmpty()
                    || allegatiEsistenti.equals("0")) {
                activity.setAllegati(pathAllegato);
            } else {
                activity.setAllegati(allegatiEsistenti + "," + pathAllegato);
            }

            String entry = "Allegato aggiunto: " + DATE_FORMAT.format(new Date());
            activity.aggiungiLogEntry(entry);
            activityRepository.save(activity);
        });
    }

    /**
     * Ottieni dashboard attività utente
     */
    public ActivityDashboard getDashboardUtente(Integer idutente) {
        long aperte = activityRepository.countAttivitaAperteByUtente(idutente);
        List<Activity> urgenti = activityRepository.findAttivitaUrgentiByUtente(idutente);
        List<Activity> recenti = activityRepository.findByIdutenteOrderByIdDesc(idutente)
                .stream()
                .limit(10)
                .toList();

        return new ActivityDashboard(aperte, urgenti, recenti);
    }

    /**
     * Ottieni attività per utente
     */
    public List<Activity> getAttivitaUtente(Integer idutente) {
        return activityRepository.findByIdutenteOrderByIdDesc(idutente);
    }

    /**
     * Ottieni attività aperte per utente
     */
    public List<Activity> getAttivitaAperte(Integer idutente) {
        return activityRepository.findAttivitaAperteByUtente(idutente);
    }

    /**
     * Ottieni attività urgenti per utente
     */
    public List<Activity> getAttivitaUrgenti(Integer idutente) {
        return activityRepository.findAttivitaUrgentiByUtente(idutente);
    }

    /**
     * DTO per dashboard
     */
    public record ActivityDashboard(
            long attivitaAperte,
            List<Activity> attivitaUrgenti,
            List<Activity> attivitaRecenti
    ) {}
}