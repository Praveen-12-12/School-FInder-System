const sign_in_btn = document.querySelector("#sign-in-btn");
const sign_up_btn = document.querySelector("#sign-up-btn");
const container = document.querySelector(".container");

sign_up_btn.addEventListener("click", () => {
  container.classList.add("sign-up-mode");
});

sign_in_btn.addEventListener("click", () => {
  container.classList.remove("sign-up-mode");
});

document.getElementById('registerForm').addEventListener('submit', function(e) {
  const phone = document.getElementById('phone').value;
  if (!/^\d+$/.test(phone)) {
    alert("Phone number must contain only digits!");
    e.preventDefault();
  }
});

document.getElementById('phone').addEventListener('input', function() {
  this.value = this.value.replace(/[^0-9]/g, '');
});