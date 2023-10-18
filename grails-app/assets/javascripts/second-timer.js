
onmessage = function(e) {
    setInterval(function() {
        postMessage('Tick');
    }, 1000);
}
