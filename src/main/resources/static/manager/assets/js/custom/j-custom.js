$(document).ready(function(){

    // -------------------------------------------------------
    // Treeview
    // -------------------------------------------------------
    if ($("#tree").length > 0) {
        $("#tree").treeview({
            collapsed: true,
            animated: "medium",
            control: "#sidetreecontrol",
            persist: "cookie"
        });
    }

    // -------------------------------------------------------
    // DataTable helper centralizzato
    // -------------------------------------------------------
    function initDataTable(selector, options) {
        if ($(selector).length === 0) return;
        if ($.fn.DataTable.isDataTable(selector)) return;
        var defaults = {
            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "Tutti"]],
            pageLength: 25,
            language: { url: 'manager/assets/json/locales/datatables-italian.json' },
            responsive: true,
            dom: '<"d-flex justify-content-between align-items-center mb-2"lf>rtip'
        };
        $(selector).DataTable($.extend(true, {}, defaults, options));
    }

    // Elenco sezioni principale (tab attiva, sortable attivo)
    if ($('#elencoSezioniPrincipale').length > 0) {
        initDataTable('#elencoSezioniPrincipale', {
            ordering: false  // disabilitato perché c'è il sortable drag&drop
        });
    }

    // Tabelle nelle tab - inizializzate lazy al primo click
    document.querySelectorAll('button[data-bs-toggle="tab"]').forEach(function(tabBtn) {
        tabBtn.addEventListener('shown.bs.tab', function(e) {
            var target = e.target.getAttribute('data-bs-target');

            if (target === '#tab_02') {
                initDataTable('#elencoSottosezioni', {
                    order: [[1, 'asc']],
                    columnDefs: [
                        { orderable: false, targets: [0, 5] }
                    ]
                });
            }

            if (target === '#tab_03') {
                initDataTable('#elencoContenuti', {
                    order: [[1, 'asc']],
                    columnDefs: [
                        { orderable: false, targets: [0, 5] }
                    ]
                });
            }
        });
    });

    // -------------------------------------------------------
    // Sortable elenco sezioni principale
    // -------------------------------------------------------
    if ($("#elencoSezioniPrincipale tbody").length > 0) {
        $("#elencoSezioniPrincipale tbody").sortable({
            create: function(event, ui) {
                var ordine = "";
                $('#elencoSezioniPrincipale > tbody > tr').each(function() {
                    var val = $(this).find("input[type='checkbox'][name='row_sel']").val();
                    if (val) ordine += val + ";";
                });
                $("#ordineBaseSect").val(ordine);
            },
            update: function(event, ui) {
                var ordine = "";
                $('#elencoSezioniPrincipale > tbody > tr').each(function() {
                    var val = $(this).find("input[type='checkbox'][name='row_sel']").val();
                    if (val) ordine += val + ";";
                });
                $("#ordineSect").val(ordine);
                $.ajax({
                    type: 'POST',
                    url: '/admin/sezioni/ordina',
                    dataType: 'text',
                    data: {
                        ordineBaseSect: $("#ordineBaseSect").val(),
                        ordineSect: $("#ordineSect").val()
                    },
                    success: function() {
                        showToast('Azione completata', 'Ordinamento sezioni eseguito.');
                    },
                    error: function() {
                        showToast('Errore', 'Errore di comunicazione server.', 'danger');
                    }
                });
            }
        });
    }

    // -------------------------------------------------------
    // Conferma cancella sezione principale (toolbar)
    // -------------------------------------------------------
    document.querySelectorAll('.btn-confirm-delete-main').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            var titolo = this.getAttribute('data-titolo');
            var form = this.closest('form');
            confirmAction(
                'Eliminare la sezione "' + titolo + '"?',
                function() { form.submit(); }
            );
        });
    });

    // -------------------------------------------------------
    // Conferma cancella sottosezione (dropdown tabella)
    // -------------------------------------------------------
    document.querySelectorAll('.btn-confirm-delete-sez').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            var titolo = this.getAttribute('data-titolo');
            var form = this.closest('form');
            confirmAction(
                'Eliminare la sezione "' + titolo + '"?',
                function() { form.submit(); }
            );
        });
    });

    // -------------------------------------------------------
    // Conferma cancella contenuto
    // -------------------------------------------------------
    document.querySelectorAll('.btn-confirm-delete-cont').forEach(function(btn) {
        btn.addEventListener('click', function() {
            var id = this.getAttribute('data-id');
            var titolo = this.getAttribute('data-titolo');
            confirmAction(
                'Eliminare il contenuto "' + titolo + '"?',
                function() { deleteContenuto(id); }
            );
        });
    });

    // -------------------------------------------------------
    // Select all checkbox
    // -------------------------------------------------------
    document.querySelectorAll('.chSel_all').forEach(function(chk) {
        chk.addEventListener('change', function() {
            var target = this.getAttribute('data-target');
            document.querySelectorAll('.' + target).forEach(function(cb) {
                cb.checked = chk.checked;
            });
        });
    });

    // -------------------------------------------------------
    // Misc
    // -------------------------------------------------------

});

// -------------------------------------------------------
// Toast
// -------------------------------------------------------
function showToast(title, message, type = 'success') {
    const toastId = 'toast_' + Date.now();
    const bgClass = type === 'success' ? 'bg-success' : 'bg-danger';
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0"
             role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <strong>${title}</strong><br>${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto"
                        data-bs-dismiss="toast"></button>
            </div>
        </div>`;
    $('#toast-container').append(toastHtml);
    const toastEl = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastEl, { delay: 5000 }); // 5s per leggere i campi
    toast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
}

// -------------------------------------------------------
// Confirm modal
// -------------------------------------------------------
function confirmAction(message, onConfirm, btnLabel = 'Conferma', btnClass = 'btn-danger') {
    const modal = document.getElementById('confirmModal');
    const btnConfirm = document.getElementById('confirmModalBtn');
    const msgEl = document.getElementById('confirmModalMessage');

    msgEl.textContent = message;
    btnConfirm.textContent = btnLabel;
    btnConfirm.className = 'btn ' + btnClass;

    const newBtn = btnConfirm.cloneNode(true);
    btnConfirm.parentNode.replaceChild(newBtn, btnConfirm);

    newBtn.addEventListener('click', function() {
        bootstrap.Modal.getInstance(modal).hide();
        onConfirm();
    });

    new bootstrap.Modal(modal).show();
}

// -------------------------------------------------------
// Delete contenuto via AJAX
// -------------------------------------------------------
function deleteContenuto(id) {
    $.ajax({
        type: 'POST',
        url: '/admin/contenuti/' + id + '/delete',
        success: function() {
            showToast('Completato', 'Contenuto eliminato.');
            setTimeout(function() { location.reload(); }, 1000);
        },
        error: function() {
            showToast('Errore', 'Errore durante la cancellazione.', 'danger');
        }
    });
}

// -------------------------------------------------------
// Cancella sezioni multiple
// -------------------------------------------------------
function cancellaSezioniSelezionate() {
    var ids = [];
    document.querySelectorAll('.inpt_c1:checked').forEach(function(cb) {
        ids.push(cb.value);
    });
    if (ids.length === 0) {
        showToast('Attenzione', 'Nessuna sezione selezionata.', 'danger');
        return;
    }
    confirmAction(
        'Eliminare le ' + ids.length + ' sezioni selezionate?',
        function() {
            $.ajax({
                type: 'POST',
                url: '/admin/sezioni/deleteMultiplo',
                data: { delSectID: ids },
                traditional: true,
                success: function() {
                    showToast('Completato', 'Sezioni eliminate.');
                    setTimeout(function() { location.reload(); }, 1000);
                },
                error: function() {
                    showToast('Errore', 'Errore durante la cancellazione.', 'danger');
                }
            });
        }
    );
}

// -------------------------------------------------------
// Cancella contenuti multipli
// -------------------------------------------------------
function cancellaContenutiSelezionati() {
    var ids = [];
    document.querySelectorAll('.inpt_c2:checked').forEach(function(cb) {
        ids.push(cb.value);
    });
    if (ids.length === 0) {
        showToast('Attenzione', 'Nessun contenuto selezionato.', 'danger');
        return;
    }
    confirmAction(
        'Eliminare i ' + ids.length + ' contenuti selezionati?',
        function() {
            $.ajax({
                type: 'POST',
                url: '/admin/contenuti/deleteMultiplo',
                data: { delContID: ids },
                traditional: true,
                success: function() {
                    showToast('Completato', 'Contenuti eliminati.');
                    setTimeout(function() { location.reload(); }, 1000);
                },
                error: function() {
                    showToast('Errore', 'Errore durante la cancellazione.', 'danger');
                }
            });
        }
    );
}

function AttivaExtraTag() {
    var el = document.getElementById('regolaExtraTag1');
    if (el) { // <--- Questo controllo salva tutto
        const val = el.value;
        const show = val === '1';
        ['ZonaExtraTagOrdine_maxEl','ZonaExtraTagOrdine','SpazioEreditaExtraTag','ZonaExtraTag']
            .forEach(id => {
                const target = document.getElementById(id);
                if (target) target.style.display = show ? '' : 'none';
            });
    }
}
document.addEventListener('DOMContentLoaded', function() {
    setTimeout(AttivaExtraTag, 100);
});


// ============================================================
// TINYMCE - configurazione globale
// ============================================================
function initTinyMCE(selector) {
    if (typeof tinymce === 'undefined') return;
    // Evita doppia inizializzazione
    var id = selector.replace('#', '');
    if (tinymce.get(id)) return;

    tinymce.init({
        selector: selector,
        language: 'it',
        language_url: '/manager/assets/js/plugins/tinymce/langs/it.js',
        height: 350,
        menubar: false,
        statusbar: false,
        content_style: 'body { font-family: Inter, sans-serif; font-size: 14px; }',
        plugins: 'advlist autolink lists link image charmap preview anchor ' +
                 'searchreplace code fullscreen table wordcount',
        toolbar: 'undo redo | formatselect | bold italic underline | ' +
                 'forecolor backcolor | alignleft aligncenter alignright alignjustify | ' +
                 'bullist numlist outdent indent | link image table | code fullscreen',
        setup: function(editor) {
            editor.on('change', function() {
                editor.save();
            });
        }
    });
}

function toggleEditor(id) {

    if (tinymce.get(id)) {
        tinymce.get(id).remove(); // chiude editor
    } else {
        initTinyMCE('#' + id); // riapre editor
    }

}

// ============================================================
// INVIAFORM - validazione + submit
// ============================================================
function InviaForm(formId) {

    // Sincronizza TinyMCE in modo sicuro (compatibile con tutte le versioni)
        if (typeof tinymce !== 'undefined') {
            try {
                // TinyMCE 4.x: triggerSave()
                // TinyMCE 5.x/6.x: editors è un array ma può essere vuoto
                if (typeof tinymce.triggerSave === 'function') {
                    tinymce.triggerSave();
                } else if (tinymce.editors && tinymce.editors.length > 0) {
                    tinymce.editors.forEach(function(editor) { editor.save(); });
                } else if (typeof tinymce.get === 'function') {
                    // fallback: salva tutti gli editor tramite tinymce.get()
                    var allEditors = tinymce.editors;
                    for (var id in allEditors) {
                        if (allEditors.hasOwnProperty(id)) {
                            allEditors[id].save();
                        }
                    }
                }
            } catch(e) {
                console.warn('TinyMCE save error:', e);
            }
        }

    var form = document.getElementById(formId);
    if (!form) {
        showToast('Errore', 'Form "' + formId + '" non trovato.', 'danger');
        return;
    }

    form.classList.add('was-validated');

    if (!form.checkValidity()) {
        var campiNonValidi = [];
        form.querySelectorAll(':invalid').forEach(function(el) {
            var label = form.querySelector('label[for="' + el.id + '"]')
                     || el.closest('.form-group')?.querySelector('label');
            var nomeCampo = label
                ? label.textContent.replace('*', '').trim()
                : (el.name || el.placeholder || 'Campo sconosciuto');
            campiNonValidi.push(nomeCampo);
        });

        var messaggio = 'Alcuni campi obbligatori richiedono un tuo intervento!';
        if (campiNonValidi.length > 0) {
            messaggio += '<br><small>' + campiNonValidi.join(', ') + '</small>';
        }

        showToast('Attenzione!', messaggio, 'danger');

        var primoInvalido = form.querySelector(':invalid');
        if (primoInvalido) {
            primoInvalido.scrollIntoView({ behavior: 'smooth', block: 'center' });
            primoInvalido.focus();
        }
        return;
    }

    form.submit();
}

// ============================================================
// DOCUMENT READY
// ============================================================
$(document).ready(function() {

    // TinyMCE - Tab 02 Abstract e Tab 03 Contenuto
    if ($('#testo').length)     initTinyMCE('#testo');

   var localeIT = {
       firstDayOfWeek: 1,
       weekdays: {
           shorthand: ["Dom","Lun","Mar","Mer","Gio","Ven","Sab"],
           longhand:  ["Domenica","Lunedì","Martedì","Mercoledì","Giovedì","Venerdì","Sabato"]
       },
       months: {
           shorthand: ["Gen","Feb","Mar","Apr","Mag","Giu","Lug","Ago","Set","Ott","Nov","Dic"],
           longhand:  ["Gennaio","Febbraio","Marzo","Aprile","Maggio","Giugno",
                       "Luglio","Agosto","Settembre","Ottobre","Novembre","Dicembre"]
       }
   };

   function aggiungiBtnOggi(fp) {
       var btn = document.createElement("button");
       btn.textContent = "Oggi";
       btn.type = "button";
       btn.className = "flatpickr-oggi";
       btn.style.cssText = "width:100%;padding:6px;background:#1a73e8;color:#fff;border:none;cursor:pointer;font-size:13px;";
       btn.addEventListener("click", function() {
           fp.setDate(new Date(), true);
           fp.close();
       });
       fp.calendarContainer.appendChild(btn);
   }

   // Data visualizzata — formato: 25 Marzo, 2021
   flatpickr(".dataVisualizzata", {
       dateFormat: "j F, Y",
       allowInput: true,
       locale: localeIT,
       onReady: function(selectedDates, dateStr, fp) {
           aggiungiBtnOggi(fp);
       }
   });

   // Data riferimento — formato: 25-03-2021
   flatpickr(".dataBase", {
       dateFormat: "d-m-Y",
       allowInput: true,
       locale: localeIT,
       onReady: function(selectedDates, dateStr, fp) {
           aggiungiBtnOggi(fp);
       }
   });

    // TinyMCE - Tab 07 Text1-10: lazy init all'apertura del tab
    document.querySelectorAll('button[data-bs-toggle="tab"]').forEach(function(tabBtn) {
        tabBtn.addEventListener('shown.bs.tab', function(e) {
            if (e.target.getAttribute('data-bs-target') === '#tab_07') {
                for (var i = 1; i <= 10; i++) {
                    if ($('#text' + i).length) initTinyMCE('#text' + i);
                }
            }
        });
    });

    // Handler per btn-toggle-editor
    $(document).on('click', '.btn-toggle-editor', function() {
        var target = $(this).data('target');
        if (target) toggleEditor(target);
    });

    // Genera AUTHCODE per Google Authenticator
    window.GeneraAuthCodeGoogleAuthenticatorUtente = function (msg) {
        if (msg && !confirm(msg)) return;

        const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        let secret = "";
        for (let i = 0; i < 32; i++) {
            secret += chars[Math.floor(Math.random() * chars.length)];
        }

        const input = document.getElementById("role20");
        if (input) input.value = secret;

        // opzionale: aggiorna QR se presente
        const qr = document.getElementById("qrImg");
        if (qr && qr.getAttribute("data-src")) {
            qr.src = qr.getAttribute("data-src") + "&_=" + Date.now();
            qr.style.display = "";
        }
    };

    // -------------------------------------------------------
    // Autocomplete Universale (CRM, Anagrafiche, ecc.)
    // -------------------------------------------------------
    // -------------------------------------------------------
        // Autocomplete Universale (CRM, Anagrafiche, ecc.)
        // -------------------------------------------------------
       // -------------------------------------------------------
       // Autocomplete Lead - Versione Definitiva
       // -------------------------------------------------------
       $(document).ready(function() {
           var $ricerca = $('#ricercaUtenteNuovoLead');

           if ($ricerca.length > 0) {
               $ricerca.autocomplete({
                   source: function(request, response) {
                       $.ajax({
                           url: "/admin/crm/utenti/search",
                           type: 'GET',
                           dataType: 'json',
                           data: { term: request.term },
                           success: function(data) {
                               if (!data || data.length === 0) { response([]); return; }
                               var suggerimenti = $.map(data, function(item) {
                                   if (!item) return null;
                                   return {
                                       label: [item.nome, item.cognome, item.email].filter(Boolean).join(" "),
                                       value: [item.nome, item.cognome].filter(Boolean).join(" "),
                                       obj: item
                                   };
                               });
                               response(suggerimenti.filter(Boolean));
                           },
                           error: function(xhr) {
                               console.error("Errore:", xhr.status, xhr.responseText);
                               response([]);
                           }
                       });
                   },
                   minLength: 2,
                   select: function(event, ui) {
                       if (!ui || !ui.item || !ui.item.obj) return false;
                       var d = ui.item.obj;

                       $("#idUtenteLead").val(d.id || '');
                       $("#utenteid").val(d.id || '');
                       $("#utentenome").val(d.nome || '');
                       $("#utentecognome").val(d.cognome || '');
                       $("#utenteemail").val(d.email || '');
                       // telefono: prova prima telefono, poi telefono2
                       $("#utentetelefono").val(d.telefono && d.telefono.trim() !== '' ? d.telefono : (d.telefono2 || ''));

                       showToast('Caricato', 'Anagrafica di ' + (d.cognome || 'utente') + ' inserita.');
                       return false;
                   }
               });

               $ricerca.data("ui-autocomplete")._renderItem = function(ul, item) {
                   return $("<li>")
                       .append("<div style='color:#333!important;padding:10px;background:#fff;border-bottom:1px solid #ddd;cursor:pointer;'>" + item.label + "</div>")
                       .appendTo(ul);
               };
           }
       });

});