//Dropdown de anos
window.onload = function loadCalendar(){
    var max = new Date().getFullYear(),
        min = max - 60,
        select = document.getElementById('calendarioAnoDropDown');

    var optNull = document.createElement('option');
    optNull.value = null;
    optNull.innerHTML = "-";
    select.appendChild(optNull);

    for (var i = max; i >= min; i--) {
        var opt = document.createElement('option');
        opt.value = i;
        opt.innerHTML = i;
        select.appendChild(opt);
    }
}