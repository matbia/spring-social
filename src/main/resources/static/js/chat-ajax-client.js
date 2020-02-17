let token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
let messageCount = 0;
let recipientId = 0;

function loadMessages(userId) {
    fetch('/chat/load/' + userId)
        .then(res => res.text())
        .then(text => {
            let chatEl = $('.chat-messages');
            let atBottom = chatEl.scrollTop() + chatEl.innerHeight() >= chatEl[0].scrollHeight;
            chatEl.html(text);
            if(atBottom) chatEl.scrollTop(chatEl[0].scrollHeight);

            //Parse URLs
            document.querySelectorAll('.message-text').forEach(
                el => el.innerHTML = parseMessage(el.innerText)
            );
        })
        .catch(err => console.error(err));
}

function loadUsers() {
    let handleUserLoad = e => {
        recipientId = e.currentTarget.getAttribute('data-id');
        alert(recipientId);
        loadMessages(recipientId);
        document.querySelector('#recipient-id').value = recipientId;
    };

    fetch('/chat/users')
        .then(res => res.text())
        .then(text => {
            document.querySelector('.chat-list').innerHTML = text;
            document.querySelectorAll('.chat-user').forEach(el => {
                el.removeEventListener('click', handleUserLoad);
                el.addEventListener('click', handleUserLoad);
            });
        })
        .catch(err => console.error(err));
}

function autoUpdate() {
    if(recipientId) {
        fetch('/chat/count/' + recipientId)
            .then(res => res.text())
            .then(text => {
                let newMessageCount = parseInt(text);
                if(newMessageCount > messageCount) loadMessages(recipientId);
                messageCount = parseInt(text);
            })
            .catch(err => console.error(err));
    }
}

$('#chat-form').on('submit', function(e) {
    e.preventDefault();
    let data = new FormData($(this)[0]);
    fetch('/chat/send/', { method: 'POST', headers: { 'X-CSRF-TOKEN': token }, body: data })
        .then((res) => {
            $(this).trigger('reset'); //Clear form
            $('.chat-messages').scrollTop($('.chat-messages')[0].scrollHeight);
            loadMessages(data.get('recipientId'));
        })
        .catch(error => console.error(error));
})

loadUsers();
window.setInterval(autoUpdate, 3500);
