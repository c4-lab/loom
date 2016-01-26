<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="training-content-wrapper">
            <g:render template="/home/content"
                      model="[training: training, tts: tts, tailsList: tailsList, rawTails: rawTails]"/>
        </div>
    </div>
</g:applyLayout>