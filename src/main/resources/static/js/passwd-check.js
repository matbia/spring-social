let passwdMsgEl = document.querySelector('#passwd-msg');
let submitBtnEl = document.querySelector('#submit-btn');
let passwdEl = document.querySelector('#passwd');
let passwdReEl = document.querySelector('#passwd-re');

let handlePasswordChange = () => {
    if(passwdEl.value != passwdReEl.value) {
        passwdMsgEl.textContent = 'Passwords don\'t match';
        submitBtnEl.disabled = true;
    } else if(passwdEl.value.length < 8) {
        passwdMsgEl.textContent = 'Password must be at least 8 characters long';
        submitBtnEl.disabled = true;
    } else {
        passwdMsgEl.innerHTML = '&nbsp;';
        submitBtnEl.disabled = false;
    }
};

document.querySelector('.passwd-input').addEventListener('input', handlePasswordChange);
passwdReEl.addEventListener('input', handlePasswordChange);