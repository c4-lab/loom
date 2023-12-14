
onmessage = function(e) {
    if (e.data == "Start") {

        setInterval(function () {
            console.log("Worker sends a Tick")
            postMessage('Tick');
        }, 1000);
    }
}
