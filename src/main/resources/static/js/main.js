import BulmaModal from './BulmaModal.js'

window.onload = function runScript () {
    const modals = document.querySelectorAll("[data-toggle='modal']");
    modals.forEach((modal) => new BulmaModal(modal));
    document.querySelector(".modal").addEventListener("modal:show", (event) => {
        console.log(event)
    });
}
