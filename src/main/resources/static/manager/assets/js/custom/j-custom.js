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
});

// -------------------------------------------------------
// Autocomplete generico per .js-autocomplete
// -------------------------------------------------------
$('.js-autocomplete').each(function() {
    var $input = $(this);
    var url = $input.data('url');
    if (!url) return;

    $input.autocomplete({
        source: function(request, response) {
            $.ajax({
                url: url,
                type: 'GET',
                dataType: 'json',
                data: { term: request.term },
                success: function(data) {
                    if (!data || data.length === 0) { response([]); return; }
                    response($.map(data, function(item) {
                        if (!item) return null;
                        return {
                            label: [item.nome, item.cognome, item.email].filter(Boolean).join(' '),
                            value: [item.nome, item.cognome].filter(Boolean).join(' '),
                            obj: item
                        };
                    }).filter(Boolean));
                },
                error: function() { response([]); }
            });
        },
        minLength: 2,
        select: function(event, ui) {
            if (!ui || !ui.item || !ui.item.obj) return false;
            var d = ui.item.obj;

            // Caso CRM Lead
            if ($('#idUtenteLead').length) {
                $('#idUtenteLead').val(d.id || '');
                $('#utenteid').val(d.id || '');
                $('#utentenome').val(d.nome || '');
                $('#utentecognome').val(d.cognome || '');
                $('#utenteemail').val(d.email || '');
                $('#utentetelefono').val(d.telefono && d.telefono.trim() !== '' ? d.telefono : (d.telefono2 || ''));
                showToast('Caricato', 'Anagrafica di ' + (d.cognome || 'utente') + ' inserita.');
                return false;
            }

            // Caso Sezione — aggiunge a utentiAssociatiString
            var $hidden = $('#utentiAssociatiString');
            if ($hidden.length) {
                var id = d.id;
                var nomeCompleto = [d.nome, d.cognome].filter(Boolean).join(' ');
                if ($hidden.val().indexOf('(' + id + ');') === -1) {
                    $hidden.val($hidden.val() + '(' + id + ');');
                    $('#utenti').append(
                        '<div id="user-badge-' + id + '" class="badge bg-secondary m-1 p-2 d-inline-flex align-items-center">' +
                        '<span>' + nomeCompleto + '</span>' +
                        '<i class="fas fa-times ms-2" style="cursor:pointer" onclick="RimuoviUtenteAssociato(' + id + ')" title="Rimuovi"></i>' +
                        '</div>'
                    );
                    showToast('Aggiunto', nomeCompleto + ' aggiunto.');
                }
                $(this).val('');
                return false;
            }

            return false;
        }
    });

    $input.data('ui-autocomplete')._renderItem = function(ul, item) {
        return $('<li>').append(
            "<div style='color:#333!important;padding:10px;background:#fff;border-bottom:1px solid #ddd;cursor:pointer;'>" + item.label + "</div>"
        ).appendTo(ul);
    };
});

window.FileManager = {
    apriForm: function(url, titolo) {
        const modalEl = document.getElementById('modalAccount');
        const titleEl = document.getElementById('modalAccountTitle');
        const bodyEl  = document.getElementById('modalAccountBody');
        if (!modalEl) { alert("Modal 'modalAccount' non trovata!"); return; }
        titleEl.innerText = titolo;
        bodyEl.innerHTML = '<div class="text-center p-5"><div class="spinner-border text-info"></div><p class="mt-2">Caricamento...</p></div>';
        bootstrap.Modal.getOrCreateInstance(modalEl).show();
        fetch(url)
            .then(function(r) { if (!r.ok) throw new Error("HTTP " + r.status); return r.text(); })
            .then(function(html) {
                $(bodyEl).html(html);
                if (typeof initTinyMCE === 'function') {
                    $(bodyEl).find('textarea.tinymce').each(function() { initTinyMCE('#' + this.id); });
                }
            })
            .catch(function(err) {
                bodyEl.innerHTML = '<div class="alert alert-danger m-3">Errore: ' + err.message + '</div>';
            });
    },
    refresh: function(idFolder) {
        var modalEl = document.getElementById('modalAccount');
        var modalInstance = bootstrap.Modal.getInstance(modalEl);
        if (modalInstance) modalInstance.hide();
        showToast('Completato', 'Operazione eseguita.');
        // NON ricaricare se siamo in una sezione/contenuto
        if (document.getElementById('divgallery') || document.getElementById('galleryString')) {
            return; // siamo in una pagina con gallery, non navigare
        }
        setTimeout(function() {
            if (typeof window.ricaricaLibreria === 'function') {
                window.ricaricaLibreria(idFolder);
            } else {
                location.reload();
            }
        }, 800);
    }
};

$(document).on('submit', '#folderForm', function(e) {
    e.preventDefault();
    var $form = $(this);
    var idFolder = $form.find('input[name="idfolder"]').val() || '1';
    if (typeof tinymce !== 'undefined') tinymce.triggerSave();
    $.ajax({
        url: $form.attr('action'),
        type: 'POST',
        data: new FormData(this),
        processData: false,
        contentType: false,
        success: function() { FileManager.refresh(idFolder); },
        error: function() { showToast('Errore', 'Salvataggio fallito', 'danger'); }
    });
});

// ============================================================
// FILE MANAGER - Funzioni globali Media Library
// ============================================================

function selezionaTutto() {
    var checkboxes = document.querySelectorAll('.inpt_c1');
    var tuttiChecked = Array.from(checkboxes).every(function(cb) { return cb.checked; });
    checkboxes.forEach(function(cb) {
        cb.checked = !tuttiChecked;
        cb.dispatchEvent(new Event('change'));
    });
}

function cancellaSelezionati() {
    var checked = document.querySelectorAll('.inpt_c1:checked');
    if (checked.length === 0) {
        showToast('Attenzione', 'Selezionare almeno un elemento', 'danger');
        return;
    }
    confirmAction('Eliminare i ' + checked.length + ' elementi selezionati?', function() {
        var promises = [];
        checked.forEach(function(cb) {
            var container = cb.closest('[data-id]');
            var tipo = container.getAttribute('data-tipo');
            var id   = container.getAttribute('data-id');
            var url  = '';
            if (tipo === 'folder')   url = '/admin/filemanager/folder/' + id + '/delete';
            if (tipo === 'immagine') url = '/admin/filemanager/images/' + id + '/delete';
            if (tipo === 'allegato') url = '/admin/filemanager/files/'  + id + '/delete';
            promises.push(fetch(url, { method: 'POST' }));
        });
        Promise.all(promises).then(function() { location.reload(); });
    });
}

function cancellaFolder(id) {
    confirmAction('Eliminare la cartella?', function() {
        fetch('/admin/filemanager/folder/' + id + '/delete', { method: 'POST' })
            .then(function() {
                var el = document.querySelector('[data-tipo="folder"][data-id="' + id + '"]');
                if (el) el.remove();
            });
    });
}

function cancellaImmagine(id) {
    confirmAction("Eliminare l'immagine?", function() {
        fetch('/admin/filemanager/images/' + id + '/delete', { method: 'POST' })
            .then(function() {
                var el = document.querySelector('[data-tipo="immagine"][data-id="' + id + '"]');
                if (el) el.remove();
            });
    });
}

function cancellaAllegato(id) {
    confirmAction('Eliminare il documento?', function() {
        fetch('/admin/filemanager/files/' + id + '/delete', { method: 'POST' })
            .then(function() {
                var el = document.querySelector('[data-tipo="allegato"][data-id="' + id + '"]');
                if (el) el.remove();
            });
    });
}

window.ricaricaLibreria = function(idfolder) {
    var folder = idfolder || (typeof currentFolder !== 'undefined' ? currentFolder : '1');
    window.location.href = '/admin/filemanager/' + folder;
};

document.addEventListener('change', function(e) {
    if (!e.target.classList.contains('inpt_c1')) return;
    var count = document.querySelectorAll('.inpt_c1:checked').length;
    var btn = document.getElementById('btnCancella');
    if (btn) btn.style.display = count > 0 ? '' : 'none';
});

// FILE MANAGER - Ricerca
document.addEventListener('DOMContentLoaded', function() {
    var searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            var q = this.value.toLowerCase();
            document.querySelectorAll('[data-nome]').forEach(function(el) {
                var nome = (el.getAttribute('data-nome') || '').toLowerCase();
                el.style.display = nome.includes(q) ? '' : 'none';
            });
        });
    }
});

function filtraGriglia(tipo) {
    // Toggle: se già attivo, mostra tutto
    var card = document.getElementById('filterCard-' + tipo);
    var isActive = card && card.classList.contains('filter-active');

    // Reset tutte le card
    ['allegato', 'immagine', 'folder'].forEach(function(t) {
        var c = document.getElementById('filterCard-' + t);
        if (c) c.classList.remove('filter-active');
    });

    if (isActive) {
        // Deseleziona — mostra tutto
        document.querySelectorAll('[data-tipo]').forEach(function(el) {
            el.style.display = '';
        });
        return;
    }

    // Attiva filtro
    if (card) card.classList.add('filter-active');

    document.querySelectorAll('[data-tipo]').forEach(function(el) {
        el.style.display = el.getAttribute('data-tipo') === tipo ? '' : 'none';
    });

}

function RimuoviTutteImmagine() {
    confirmAction("Sei sicuro di voler rimuovere tutte le immagini associate?", function() {
        // 1. Svuota il contenitore visivo (il div con le anteprime)
        const divGallery = document.getElementById('divgallery');
        if (divGallery) {
            divGallery.innerHTML = '';
        }

        // 2. Svuota il campo nascosto che salva i dati nel DB
        const inputGallery = document.getElementById('galleryString');
        if (inputGallery) {
            inputGallery.value = '';
        }

        // 3. Notifica opzionale per l'utente
        console.log("Galleria svuotata. Ricordati di cliccare su Salva per confermare.");
    });
}

function apriLightbox(src, nome) {
    var modalEl = document.getElementById('modalLightbox');
    if (!modalEl) return;
    document.getElementById('lightboxImg').src = src;
    document.getElementById('lightboxNome').textContent = nome || '';
    bootstrap.Modal.getOrCreateInstance(modalEl).show();
}

// Funzione chiamata dall'onblur nell'HTML
function ControlloCampoTag(elemento) {
    if (!elemento) return;

    var $campo = $(elemento);
    var valore = $campo.val().trim();

    // Se il campo non è vuoto e l'ultimo carattere non è una virgola
    if (valore.length > 0 && valore.slice(-1) !== ',') {
        $campo.val(valore + ',');
    }
}

// La definiamo come funzione globale attaccandola a window
window.ApplicaExtraTagContenuti = function() {
    // Cerchiamo gli elementi usando ID puri, senza il $ di jQuery
    var trigger = document.getElementById("temp1_trigger");
    var form = document.getElementById("documento");

    if (trigger && form) {
        // Impostiamo il valore
        trigger.value = "1";

        console.log("Procedo con invio massivo...");

        // Invio diretto del form
        form.submit();
    } else {
        // Se non li trova, almeno sappiamo perché
        console.error("Errore: trigger o form non trovati nel DOM.");
    }
};

// Bootstrap Multiselect
if (typeof $.fn.multiselect === 'function') {
    $('select.multiselect').multiselect({
        includeSelectAllOption: true,
        selectAllText: 'Seleziona tutti',
        nonSelectedText: 'None selected',
        nSelectedText: ' selezionati',
        allSelectedText: 'Tutti selezionati',
        numberDisplayed: 0,
        buttonClass: 'btn btn-default',
        templates: {
            button: '<button type="button" class="multiselect dropdown-toggle btn btn-default" data-bs-toggle="dropdown" style="width:100%"><span class="multiselect-selected-text"></span></button>'
        }
    });
}
// ============================================================
// GALLERY IMMAGINI - Inserimento da Media Library
// ============================================================
window.InserisciImmagineLibreria = function(src, id, didascalia) {
    var galleryInput = document.getElementById('galleryString');
    var divGallery = document.getElementById('divgallery');
    if (!galleryInput || !divGallery) return;

    var current = galleryInput.value || '';
    if (current.indexOf('(' + id + ');') === -1) {
        galleryInput.value = current + '(' + id + ');';

        var div = document.createElement('div');
        div.className = 'float-left mb-1 mr-1';
        div.id = id;
        div.innerHTML =
            '<div class="img-fluid">' +
            '<a href="' + src + '" title="' + src + '">' +
            '<img src="' + src + '" width="200">' +
            '</a></div>' +
            '<a href="javascript:void(0);" onclick="cancellaImmagineInserita(\'' + id + '\')">Cancella</a>';
        divGallery.appendChild(div);
    }
};

function cancellaImmagineInserita(id) {
    var galleryInput = document.getElementById('galleryString');
    if (galleryInput) {
        galleryInput.value = galleryInput.value.replace('(' + id + ');', '');
    }
    var el = document.getElementById(id);
    if (el) el.remove();
}
window.InserisciAllegatoInPagina = function(data) {
    var lista = document.getElementById('sortableAllegato');
    if (!lista) return;

    var id = data.id;
    if (document.getElementById('li_allegato_' + id)) return; // evita duplicati

    var oggi = new Date();
    var dataFormattata = ('0'+oggi.getDate()).slice(-2) + '/' +
                         ('0'+(oggi.getMonth()+1)).slice(-2) + '/' +
                         oggi.getFullYear() + ' - ' +
                         ('0'+oggi.getHours()).slice(-2) + ':' +
                         ('0'+oggi.getMinutes()).slice(-2);

    var li = document.createElement('li');
    li.id = 'li_allegato_' + id;
    li.innerHTML =
        '<div id="boxallegato_' + id + '">' +
        '<a href="javascript:void(0);" ' +
        'onclick="cancellaAllegatoInserito(\'' + id + '\')" ' +
        'style="margin-right:6px;">Cancella</a>' +
        '<a href="/admin/filemanager/files/' + id + '/download" target="_blank" title="Scarica" style="margin-right:6px;">' +
        '<i class="fas fa-download"></i></a>' +
        '<i class="fas fa-file text-secondary" style="margin-right:6px;"></i>' +
        '<span>' + (data.l1 || 'Documento') + '</span>' +
        ' - (' + dataFormattata + ')' +
        ' - (Versione 0)' +
        '</div>';
    lista.appendChild(li);
};

/**
 * Rimuove l'allegato dal DOM e dalla stringa degli ID
 * (Logica identica a cancellaImmagineInserita)
 */
window.cancellaAllegatoInserito = function(id) {
    confirmAction('Rimuovere l\'allegato dalla sezione?', function() {
        var docId = document.getElementById('id') ? document.getElementById('id').value : '0';
        // Cerca il record docallegati per questo allegato+documento e scollega
        fetch('/admin/filemanager/files/scollegaByAllegato', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idAllegato: id, idDocumento: docId })
        }).then(function() {
            var el = document.getElementById('li_allegato_' + id);
            if (el) el.remove();
            showToast('Completato', 'Allegato rimosso.');
        });
    });
};

window.CollegaAllegatoDaLibreria = function(idAllegato, l1, type) {
    var docId = document.getElementById('id') ? document.getElementById('id').value : '0';
    if (!docId || docId === '0') {
        showToast('Attenzione', 'Salva prima la sezione prima di collegare allegati.', 'danger');
        return;
    }
    fetch('/admin/filemanager/files/collega', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idAllegato: idAllegato, idDocumento: docId })
    })
    .then(function(r) { return r.json(); })
    .then(function(data) {
        if (data.success) {
            window.InserisciAllegatoInPagina({ id: idAllegato, l1: l1, type: type });
            showToast('Completato', 'Allegato collegato.');
        } else {
            showToast('Errore', 'Errore nel collegamento.', 'danger');
        }
    });
};
function scollegaErimuovi(idDocAllegati, idAllegato) {
    confirmAction('Rimuovere l\'allegato dalla sezione?', function() {
        fetch('/admin/filemanager/files/' + idDocAllegati + '/scollega', { method: 'POST' })
            .then(function() {
                // Rimuovi dal DOM — cerca sia il formato DB che quello dinamico
                var elDb = document.getElementById('boxallegato_' + idDocAllegati);
                if (elDb) elDb.closest('li').remove();
                var elDyn = document.getElementById('li_allegato_' + idAllegato);
                if (elDyn) elDyn.remove();
                showToast('Completato', 'Allegato rimosso.');
            });
    });
}
