// 1. Native Share Logic
function shareSong() {
    if (navigator.share) {
        navigator.share({
            title: document.title,
            text: 'Check out these worship lyrics!',
            url: window.location.href
        }).catch((error) => console.log('Error sharing:', error));
    } else {
        navigator.clipboard.writeText(window.location.href);
        alert("Link copied to clipboard!");
    }
}

// 2. Font Size Slider Logic
const slider = document.getElementById('fontSizeSlider');
const lyricsText = document.getElementById('lyricsText');

slider.addEventListener('input', function() {
    lyricsText.style.fontSize = this.value + 'px';
});

// 3. Fullscreen Logic Wrapper
const fullscreenWrapper = document.getElementById('fullscreenWrapper');
const fullscreenIcon = document.getElementById('fullscreenIcon');

function toggleFullScreen() {
    if (!document.fullscreenElement) {
        fullscreenWrapper.requestFullscreen().catch(err => {
            alert(`Error attempting to enable fullscreen mode: ${err.message}`);
        });
    } else {
        document.exitFullscreen();
    }
}

// Handle styling and icon swapping when entering/exiting fullscreen
document.addEventListener('fullscreenchange', (event) => {
    if (document.fullscreenElement) {
        // Apply background, padding, and CENTER TEXT for full screen
        fullscreenWrapper.style.backgroundColor = "#f8fafc";
        fullscreenWrapper.style.padding = "2rem 5%";
        fullscreenWrapper.style.overflowY = "auto";

        // Centers the lyrics!
        lyricsText.style.textAlign = "center";

        // Swap to exit icon
        fullscreenIcon.classList.replace('bi-arrows-fullscreen', 'bi-fullscreen-exit');
    } else {
        // Reset to standard page view styles
        fullscreenWrapper.style.backgroundColor = "";
        fullscreenWrapper.style.padding = "";
        fullscreenWrapper.style.overflowY = "";

        // Resets text alignment back to normal (left)
        lyricsText.style.textAlign = "";

        // Swap back to fullscreen icon
        fullscreenIcon.classList.replace('bi-fullscreen-exit', 'bi-arrows-fullscreen');
    }
});