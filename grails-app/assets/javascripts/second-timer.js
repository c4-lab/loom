
onmessage = function(e) {
    if (e.data == "Start") {

        setInterval(function () {
            postMessage('Tick');
        }, 1000);
    }
}
