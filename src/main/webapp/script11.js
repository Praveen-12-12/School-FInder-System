const container = document.getElementById('container');
const registerBtn = document.getElementById('register');
const loginBtn = document.getElementById('login');

registerBtn.addEventListener('click', () => {
    container.classList.add("active");
});

loginBtn.addEventListener('click', () => {
    container.classList.remove("active");
});

document.getElementById("phoneNumber").addEventListener("input", function(event) {
    this.value = this.value.replace(/\D/g, ''); // Removes non-numeric characters
});