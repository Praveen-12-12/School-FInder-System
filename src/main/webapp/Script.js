document.getElementById('registerForm').addEventListener('submit', function(e) {
  const phone = document.getElementById('phone').value;
  if (!/^\d+$/.test(phone)) {
    alert("Phone number must contain only numbers!");
    e.preventDefault();
  }
});

document.getElementById('phone').addEventListener('input', function(e) {
  this.value = this.value.replace(/[^0-9]/g, '');
});
