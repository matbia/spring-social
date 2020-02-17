$(function() {
    //Datepicker setup
    $('.input-date').datepicker({ dateFormat: 'dd.mm.yy', changeYear: true });

    $('.no-enter-submit').on('keyup keypress', function(e) {
        let keyCode = e.keyCode || e.which;
        if (keyCode === 13) {
            e.preventDefault();
            return false;
        }
    })

    //Bootstrap button toggle complement
    $('.btn-toggle').on('click', function() {
        $(this).hasClass('active') ?
            $(this).addClass('btn-outline-info').removeClass('btn-info') : $(this).addClass('btn-outline-info').removeClass('btn-info');
    });

    /*document.querySelectorAll('.btn-toggle').forEach(el =>
        el.addEventListener('click', e => {
            if (e.currentTarget.classList.contains('active')) {
                $(this).addClass('btn-outline-info').removeClass('btn-info')
            } else {
                $(this).addClass('btn-outline-info').removeClass('btn-info');
            }
        });
    );*/

    $('#file-input').on('change', function() {
        $('#file-input-name').html(this.files[0].name) //Chane input label to filename
        if(this.files[0].size / 1000000 > 10) {
            let errMsg = '<small id="filesize-error" class="form-text text-danger d-block">File size cannot exceed 10 MB</small>';
            $('#filesize-error').remove(); //Prevent duplicating error messages
            $(this).parent().append(errMsg);
            $('#file-submit').prop('disabled', true);
        } else {
            $('#filesize-error').remove();
            $('#file-submit').prop('disabled', false);
        }
    })
});


String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};

/* Helper methods */
function parseMessage(html) {
    let urlRegex = /(([a-z]+:\/\/)?(([a-z0-9\-]+\.)+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel|local|internal))(:[0-9]{1,5})?(\/[a-z0-9_\-\.~]+)*(\/([a-z0-9_\-\.]*)(\?[a-z0-9+_\-\.%=&amp;]*)?)?(#[a-zA-Z0-9!$&'()*+.=-_~:@/?]*)?)(\s+|$)/gi;
    let tagBody = '(?:[^"\'>]|"[^"]*"|\'[^\']*\')*';
    let tagOrComment = new RegExp(
        '<(?:'
        // Comment body.
        + '!--(?:(?:-*[^->])*--+|-?)'
        // Special "raw text" elements whose content should be elided.
        + '|script\\b' + tagBody + '>[\\s\\S]*?</script\\s*'
        + '|style\\b' + tagBody + '>[\\s\\S]*?</style\\s*'
        // Regular name
        + '|/?[a-z]'
        + tagBody
        + ')>',
        'gi');

    let oldHtml;
    do {
        oldHtml = html;
        html = html.replace(tagOrComment, '');
    } while (html !== oldHtml);
    return html.replace(/</g, '&lt;').replace(urlRegex, '<a target="_blank" rel="noopener noreferrer" href="$1">$1</a>').replaceAll('$NEWLINE', '<br />');
}