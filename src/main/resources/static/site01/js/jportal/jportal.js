// JavaScript Document
//ONLOAD COMPONENTI AGGIUNTIVI

function removeGDPRConsense() {

document.cookie='EU_COOKIE_LAW_CONSENT_Studiodomino=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/'; 
document.cookie='EU_COOKIE_LAW_CONSENT_STAT_Studiodomino=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/'; 
return false;
}


var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
return new bootstrap.Popover(popoverTriggerEl)
});

var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
  return new bootstrap.Tooltip(tooltipTriggerEl)
})
