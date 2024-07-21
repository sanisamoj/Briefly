document.getElementById('shortenButton').addEventListener('click', function() {
    const originalLink = document.getElementById('originalLink').value;
    if (!originalLink) {
        alert('Please enter a link.');
        return;
    }

    const requestUrl = `https://www.sanisamojrepository.com/briefly/generate?link=${encodeURIComponent(originalLink)}`;

    fetch(requestUrl, {
        method: 'POST',
    })
    .then(response => response.json())
    .then(data => {
        if (data.shortLink) {
            document.getElementById('shortLink').href = data.shortLink;
            document.getElementById('shortLink').textContent = data.shortLink;

            document.getElementById('qrCodeImage').src = data.qrCodeLink;

            document.getElementById('expiresAt').textContent = new Date(data.expiresAt).toLocaleString();

            document.getElementById('result').classList.remove('hidden');
        } else {
            alert('Failed to shorten the link.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while shortening the link.');
    });
});
