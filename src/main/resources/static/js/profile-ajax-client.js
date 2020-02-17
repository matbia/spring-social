$('.btn-watch-user').on('click', function() {
    //Toggle
    $(this).hasClass('btn-info') ?
        $(this).addClass('btn-outline-info').removeClass('btn-info') : $(this).addClass('btn-info').removeClass('btn-outline-info');

    fetch('/user/watch/' + $(this).attr('data-id'), { method: 'POST', headers: { 'X-CSRF-TOKEN': token }})
        .then(res => {
            if(res.status == 403) {
                alert('You are blocked by this user');
                $(this).addClass('btn-outline-info').removeClass('btn-info');
            }
        })
        .catch(err => console.error(err));
});

$('.btn-block-user').on('click', function() {
    //Toggle
    $(this).hasClass('btn-danger') ?
        $(this).addClass('btn-outline-danger').removeClass('btn-danger') : $(this).addClass('btn-danger').removeClass('btn-outline-danger');

    fetch('/user/block/' + $(this).attr('data-id'), { method: 'POST', headers: { 'X-CSRF-TOKEN': token }})
       .catch(err => console.error(err));
});

$('.btn-delete-user').on('click', function(e) {
    if(!confirm('Are you sure you want to delete this user?')) e.preventDefault();
})