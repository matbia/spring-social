let token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
let pageCount = 0;
let currPage = 1;
let currMode = 'new';

let loadingEl = document.createElement('img');
loadingEl.setAttribute('src', '/img/loading.gif');
loadingEl.setAttribute('id', 'loading');
loadingEl.setAttribute('class', 'mx-auto d-block');

function loadComments(postId) {
    fetch('/feed/comments/' + postId)
        .then(res => res.text())
        .then(text => {
            document.getElementById('comments-container-' + postId).insertAdjacentHTML('afterbegin', text);

            //Parse messages
            document.querySelectorAll('.comment-text').forEach(
                el => el.innerHTML = parseMessage(el.innerText)
            );

            let handleCommentDelete = e => {
                let id = e.target.getAttribute('data-id');
                if(confirm('Are you sure you want to delete this comment?'))
                fetch('/feed/comment/delete/' + id)
                    .then(res => document.querySelector('#comment-' + id).remove())
                    .catch(err => console.error(err));
            }

            document.querySelectorAll('.btn-comment-delete').forEach(el => {
                el.removeEventListener('click', handleCommentDelete);
                el.addEventListener('click', handleCommentDelete);
            });
        })
        .catch(err => console.error(err));
}

function autoloading() {
    if((window.innerHeight + window.pageYOffset) >= document.body.offsetHeight && currPage < pageCount) {
        window.removeEventListener('scroll', autoloading); //Prevent redundant loading
        currPage++;
        loadPostsPage(currMode);
    }
}

function addMainListeners() {
    let handleLikeBtn = e => {
        let id = e.target.getAttribute('data-id');
        let counterEl = document.querySelector('#post-likes-count-' + id);

        //Toggle on the front-end
        if(e.target.classList.contains('text-success')) {
            e.target.classList.add('text-secondary');
            e.target.classList.remove('text-success');
            counterEl.textContent = parseInt(counterEl.textContent) - 1
        } else {
            e.target.classList.remove('text-secondary');
            e.target.classList.add('text-success');
            counterEl.textContent = parseInt(counterEl.textContent) + 1
        }

        //Toggle on the back-end
        fetch('/feed/like/' + id, { method: 'POST', headers: { 'X-CSRF-TOKEN': token }})
            .catch(err => console.error(err));
    }

    let handlePostDelete = e => {
        let id = e.target.getAttribute('data-id');
            if(confirm('Are you sure you want to delete this post?')) fetch('/feed/post/delete/' + id)
                .then(res => document.querySelector('#post-' + id).remove())
                .catch(err => console.error(err));
    };

    let handlePostEdit = e => {
        let id = e.target.getAttribute('data-id');
        let msgEl = document.querySelector('#post-msg-' + id);
        let tagsEl = document.querySelector('#post-tags-' + id);
        let tagsInputEl = document.querySelector('#post-edit-tags-' + id);
        let originalMsgTxt = msgEl.textContent;
        let editBtn = e.target;
        let saveBtn = document.querySelector('#post-edit-save-' + id);
        let cancelBtn = document.querySelector('#post-edit-cancel-' + id);
        let tagsStr = tagsInputEl.value.replaceAll('[', '').replaceAll(']', '');

        editBtn.style.display = 'none';
        saveBtn.style.display = 'inline';
        cancelBtn.style.display = 'inline';
        tagsEl.style.display = 'none';
        tagsInputEl.style.display = 'inline';
        tagsInputEl.value = tagsStr;
        msgEl.contentEditable = true;
        msgEl.classList.add('border');

        let handleCancelEdit = () => {
            msgEl.contentEditable = false;
            msgEl.classList.remove('border');
            editBtn.style.display = 'inline';
            saveBtn.style.display = 'none';
            cancelBtn.style.display = 'none';
            tagsInputEl.style.display = 'none';
            tagsInputEl.value = tagsStr;
            tagsEl.style.display = 'inline';
            msgEl.textContent = originalMsgTxt;
        };

        let handleSaveEdit = () => {
            tagsInputEl.style.display = 'none';
            editBtn.style.display = 'none';
            saveBtn.style.display = 'none';
            cancelBtn.style.display = 'none';
            tagsEl.style.display = 'inline';
            tagsEl.innerHTML = '';
            msgEl.setAttribute('contenteditable', false);
            msgEl.classList.remove('border');

            tagsInputEl.value.split(',').reverse().forEach(tag => {
                let tagHtml = '<span class="badge badge-primary ml-1">' + tag.trim() + '</span>';
                tagsEl.insertAdjacentHTML('afterbegin', tagHtml);
            });

            let data = new FormData();
            let formattedMsg = msgEl.innerHTML.replaceAll('<div>', '\n').replaceAll('</div>', '').replaceAll('<br>', '\n');
            data.append('msg', formattedMsg);
            data.append('tags', tagsInputEl.value);
            fetch('/feed/post/update/' + id, { method: 'POST', headers: { 'X-CSRF-TOKEN': token }, body: data })
                .catch(error => console.error(error));
        };

        saveBtn.removeEventListener('click', handleSaveEdit);
        saveBtn.addEventListener('click', handleSaveEdit);
        cancelBtn.removeEventListener('click', handleCancelEdit);
        cancelBtn.addEventListener('click', handleCancelEdit);

    };


    let handleCommentForm = e => {
        e.preventDefault();
        let id = e.target.getAttribute('data-id');
        let data = new FormData(document.getElementById('comment-form-' + id));

        fetch('/feed/comment/save/' + id, { method: 'POST', headers: { 'X-CSRF-TOKEN': token }, body: data })
            .then((res) => {
                let commentsBtn = document.querySelector('.btn-comments[data-id="' + id + '"]');
                let commentsCountEl = document.querySelector('#comments-count-' + id);
                commentsCountEl.textContent = parseInt(commentsCountEl.textContent) + 1; //Increase comments count
                e.target.reset(); //Clear form

                document.querySelector('#comments-container-' + id).innerHTML = '';
                loadComments(id);
                commentsBtn.textContent = 'Hide comments';
                commentsBtn.classList.add('btn-hide-comments');
                commentsBtn.classList.remove('btn-load-comments');
            })
            .catch(error => console.error(error));
    };

    let handleCommentsToggle = e => {
        let postId = e.target.getAttribute('data-id');
        document.querySelector('#comments-container-' + postId).innerHTML = '';
        if(e.target.classList.contains('btn-load-comments')) {
            loadComments(postId);
            e.target.textContent = 'Hide comments';
            e.target.classList.add('btn-hide-comments');
            e.target.classList.remove('btn-load-comments');
        } else {
            e.target.textContent = 'Show comments';
            e.target.classList.add('btn-load-comments')
            e.target.classList.remove('btn-hide-comments');
        }
    };

    document.querySelectorAll('.btn-like').forEach(el => {
        el.removeEventListener('click', handleLikeBtn);
        el.addEventListener('click', handleLikeBtn);
    });

    document.querySelectorAll('.post-delete').forEach(el => {
        el.removeEventListener('click', handlePostDelete);
        el.addEventListener('click', handlePostDelete);
    });

    document.querySelectorAll('.post-edit').forEach(el => {
        el.removeEventListener('click', handlePostEdit);
        el.addEventListener('click', handlePostEdit);
    });

    document.querySelectorAll('.comment-form').forEach(el => {
        el.removeEventListener('submit', handleCommentForm);
        el.addEventListener('submit', handleCommentForm);
    });

    document.querySelectorAll('.btn-comments').forEach(el => {
        el.removeEventListener('click', handleCommentsToggle);
        el.addEventListener('click', handleCommentsToggle);
    });
}

function loadPostsPage(mode) {
    currMode = mode;
    //Load page count
    fetch('/feed/pageCount/' + mode)
       .then(res => res.text())
       .then(text => pageCount = parseInt(text))
       .catch(err => console.error(err));

    document.querySelector('.feed-posts').appendChild(loadingEl);
    fetch('/feed/' + mode + '/' + currPage)
        .then(res => res.text())
        .then(text => {
            document.querySelector('#loading').remove();
            document.querySelector('.feed-posts').insertAdjacentHTML('beforeend', text);
            addMainListeners();

            //Parse URLs
            document.querySelectorAll('.post-text').forEach(
                el => el.innerHTML = parseMessage(el.innerText)
            );

            //Load more post when the bottom of the page is reached
            window.addEventListener('scroll', autoloading);
        })
        .catch(err => console.error(err));
}

function search(tags) {
    currMode = 'search';
    document.querySelector('.feed-posts').innerHTML = '';

    document.querySelector('.feed-posts').appendChild(loadingEl);
    fetch('/feed/search?' + tags)
        .then(res => res.text())
        .then(text => {
            document.querySelector('#loading').remove();
            document.querySelector('.feed-posts').insertAdjacentHTML('beforeend', text);
            addMainListeners();

            //Parse URLs
            document.querySelectorAll('.post-text').forEach(
                el => el.innerHTML = parseMessage(el.innerText)
            );

            //Load more post when the bottom of the page is reached
            //window.addEventListener('scroll', autoloading);
        })
        .catch(err => console.error(err));
}

function loadPostsUser() {
    let postsEl = document.querySelector('#user-posts');
    postsEl.appendChild(loadingEl);
    fetch('/feed/user/' + postsEl.getAttribute('data-id'))
        .then(res => res.text())
        .then(text => {
            document.querySelector('#loading').remove();
            postsEl.insertAdjacentHTML('afterbegin', text);
            addMainListeners();

            //Parse URLs
            document.querySelectorAll('.post-text').forEach(el => el.innerHTML = parseMessage(el.textContent));
        })
        .catch(err => console.error(err));
}

window.onload = () => {
    //Routes
    if(window.location.pathname.includes('/feed/dashboard')) {
        //Prevent choosing both file and YT link
        document.querySelector('#file-input').addEventListener('change', () => document.querySelector('#yt-input').value = '');

        document.querySelector('#yt-input').addEventListener('input', () => {
            document.querySelector('#file-input').value = '';
            document.querySelector('#filesize-error').remove();
            document.querySelector('#file-input-name').innerHTML = 'Send file';
        });

        document.querySelector('.btn-search').addEventListener('click', () => {
            let searchInput = document.querySelector('.search-input').value;
            let query = ''
            searchInput.split(',').forEach(tag => {
                query += 'tags=' + tag.trim() + '&';
            });
            search(query);
        });

        document.querySelectorAll('.feed-tab').forEach(el => {
            el.addEventListener('click', (e) => {
                window.removeEventListener('scroll', autoloading);
                document.querySelector('.feed-posts').innerHTML = '';
                currPage = 1;

                document.querySelectorAll('.feed-tab').forEach(tab => tab.classList.remove('active', 'text-primary'));
                e.target.classList.add('active', 'text-primary');

                if(e.target.classList.contains('new')) {
                    loadPostsPage('new')
                }
                if(e.target.classList.contains('watched')) {
                    loadPostsPage('watched')
                }

                let searchEl = document.querySelector('.search-box');
                let postFormEl = document.querySelector('.feed-form-container');
                searchEl.style.display = 'none';
                if(e.target.classList.contains('search')) {
                    searchEl.style.display = 'block';
                    postFormEl.style.display = 'none';
                } else {
                    searchEl.style.display = 'none';
                    postFormEl.style.display = 'block';
                }
            });
        });
        loadPostsPage('new');
    }
    else if(window.location.pathname.includes('/user/profile')) loadPostsUser();
};
