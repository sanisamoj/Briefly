document.getElementById('shortenButton').addEventListener('click', function() {
    const originalLink = document.getElementById('originalLink').value;
    if (!originalLink) {
        alert('Please enter a link.');
        return;
    }

    const requestUrl = `http://217.196.60.110:9098/generate?link=${encodeURIComponent(originalLink)}`;

    fetch(requestUrl, {
        method: 'POST',
    })
    .then(response => response.json())
    .then(data => {
        if (data.shortLink) {
            document.getElementById('shortLink').href = data.shortLink;
            document.getElementById('shortLink').textContent = data.shortLink;

            document.getElementById('qrCodeLink').href = data.qrCodeLink;
            document.getElementById('qrCodeLink').textContent = data.qrCodeLink;

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
