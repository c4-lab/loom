<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Demographics Question</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">

    <link rel="stylesheet" href="//unpkg.com/bootstrap@3.3.7/dist/css/bootstrap.min.css" type="text/css" />
    <link rel="stylesheet" href="//unpkg.com/bootstrap-select@1.12.4/dist/css/bootstrap-select.min.css" type="text/css" />
    <link rel="stylesheet" href="//unpkg.com/bootstrap-select-country@4.0.0/dist/css/bootstrap-select-country.min.css" type="text/css" />

    <script src="//unpkg.com/jquery@3.4.1/dist/jquery.min.js"></script>
    <script src="//unpkg.com/bootstrap@3.3.7/dist/js/bootstrap.min.js"></script>
    <script src="//unpkg.com/bootstrap-select@1.12.4/dist/js/bootstrap-select.min.js"></script>
    <script src="//unpkg.com/bootstrap-select-country@4.0.0/dist/js/bootstrap-select-country.min.js"></script>


%{--    <asset:stylesheet src="bootstrap.css"/>--}%
%{--    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">--}%
%{--    <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">--}%
%{--    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">--}%
    <asset:stylesheet src="application.css"/>
%{--    <asset:stylesheet src="blue.css"/>--}%
%{--    <asset:stylesheet src="_all-skins.min.css"/>--}%
%{--    <asset:stylesheet src="morris.css"/>--}%
%{--    <asset:stylesheet src="dragndrop.css"/>--}%
%{--    <asset:stylesheet src="jquery-ui.css"/>--}%
%{--    <asset:stylesheet src="bootstrap.vertical-tabs.css"/>--}%
</head>

<body class="hold-transition skin-blue sidebar-mini">
%{--<div class="wrapper">--}%

    <header class="main-header">
        <nav class="navbar navbar-static-top" role="navigation">

            <asset:image src="loom-icon.png" alt="Loom" height="95" class="center-block"/>
            <sec:ifAllGranted roles="ROLE_ADMIN">

                <p class="text-center"><g:link controller="logout">Logout</g:link></p>

            </sec:ifAllGranted>



        </nav>
    </header>
%{--</div>--}%
%{--    <asset:javascript type="module" src="bootstrap-select-country.js" />--}%
    <div class="wrapper" >
        <div class="content-wrapper container">
            <section class="content">
                <div class="row " style="width:80%; height:50%; ">
                    <div class="col-md-12">
                        <g:form controller="training" action="demographicsComplete" >

                            <p><b>1. What gender do you identify as?</b></p>
                            <select id="gender" name="gender" onchange="chooseGenderOther()" required>
                                <option value="">No selection</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Nonbinary">Non-binary</option>
                                <option value="Other">Prefer to self-describe</option>
                            </select>
                            <input id="other_gender" name="gender" type="text" hidden>
                            <p></p>
                            <p><b>2. Which of the following best describes your ancestry?</b></p>
                            <select name="race" id="race" onchange="chooseRaceOther()" required>
                                <option value="">No selection</option>
                                <option value="european">European</option>
                                <option value="hispanic_latino">Hispanic or Latino</option>
                                <option value="african">African</option>
                                <option value="indigenous_american">Indigenous American</option>
                                <option value="asian">Asian</option>
                                <option value="pacific_islander">Pacific Islander</option>
                                <option value="multiracial">Multiracial</option>
                                <option value="other">Other (please specify)</option>
                            </select>
                            <input id="other_race" name="race" type="text" hidden>

                            <p></p>
                            <p><b>2. What is your age?</b></p>
                            <input type="number" min="18" max="120" value="18" name="age">
                            <p></p>
                            <p><b>3. Which language did you speak the most growing up as a child? (Please select other if language is not listed)</b></p>
                            <select id="language" class="selectpicker" name="language" data-placeholder="Choose a Language..." data-live-search="true" data-size="5" onchange="chooseLanguageOther()" required>
                                <option value="">No selection</option>
                                <option value="Afrikaans">Afrikaans</option>
                                <option value="Albanian">Albanian</option>
                                <option value="Arabic">Arabic</option>
                                <option value="Armenian">Armenian</option>
                                <option value="Basque">Basque</option>
                                <option value="Bengali">Bengali</option>
                                <option value="Bulgarian">Bulgarian</option>
                                <option value="Catalan">Catalan</option>
                                <option value="Cambodian">Cambodian</option>
                                <option value="Chinese (Cantonese)">Chinese (Cantonese)</option>
                                <option value="Chinese (Mandarin)">Chinese (Mandarin)</option>
                                <option value="Croatian">Croatian</option>
                                <option value="Czech">Czech</option>
                                <option value="Danish">Danish</option>
                                <option value="Dutch">Dutch</option>
                                <option value="English">English</option>
                                <option value="Estonian">Estonian</option>
                                <option value="Fiji">Fiji</option>
                                <option value="Finnish">Finnish</option>
                                <option value="French">French</option>
                                <option value="Georgian">Georgian</option>
                                <option value="German">German</option>
                                <option value="Greek">Greek</option>
                                <option value="Gujarati">Gujarati</option>
                                <option value="Hebrew">Hebrew</option>
                                <option value="Hindi">Hindi</option>
                                <option value="Hungarian">Hungarian</option>
                                <option value="Icelandic">Icelandic</option>
                                <option value="Indonesian">Indonesian</option>
                                <option value="Irish">Irish</option>
                                <option value="Italian">Italian</option>
                                <option value="Japanese">Japanese</option>
                                <option value="Javanese">Javanese</option>
                                <option value="Korean">Korean</option>
                                <option value="Latin">Latin</option>
                                <option value="Latvian">Latvian</option>
                                <option value="Lithuanian">Lithuanian</option>
                                <option value="Macedonian">Macedonian</option>
                                <option value="Malay">Malay</option>
                                <option value="Malayalam">Malayalam</option>
                                <option value="Maltese">Maltese</option>
                                <option value="Maori">Maori</option>
                                <option value="Marathi">Marathi</option>
                                <option value="Mongolian">Mongolian</option>
                                <option value="Nepali">Nepali</option>
                                <option value="Norwegian">Norwegian</option>
                                <option value="Persian">Persian</option>
                                <option value="Polish">Polish</option>
                                <option value="Portuguese">Portuguese</option>
                                <option value="Punjabi">Punjabi</option>
                                <option value="Quechua">Quechua</option>
                                <option value="Romanian">Romanian</option>
                                <option value="Russian">Russian</option>
                                <option value="Samoan">Samoan</option>
                                <option value="Sanskrit">Sanskrit</option>
                                <option value="Serbian">Serbian</option>
                                <option value="Slovak">Slovak</option>
                                <option value="Slovenian">Slovenian</option>
                                <option value="Spanish">Spanish</option>
                                <option value="Swahili">Swahili</option>
                                <option value="Swedish ">Swedish </option>
                                <option value="Tamil">Tamil</option>
                                <option value="Tatar">Tatar</option>
                                <option value="Telugu">Telugu</option>
                                <option value="Thai">Thai</option>
                                <option value="Tibetan">Tibetan</option>
                                <option value="Tonga">Tonga</option>
                                <option value="Turkish">Turkish</option>
                                <option value="Ukrainian">Ukrainian</option>
                                <option value="Urdu">Urdu</option>
                                <option value="Uzbek">Uzbek</option>
                                <option value="Vietnamese">Vietnamese</option>
                                <option value="Welsh">Welsh</option>
                                <option value="Xhosa">Xhosa</option>
                                <option value="Other">Other</option>
                            </select>
                            <input id="other_language" type="text" name="language" hidden>

                            <p></p>
                            <p><b>4. What is the highest level of education you’ve obtained?</b></p>
                            <select id="education" name="education" required>
                                <option value="">No selection</option>
                                <option value="Some High School">Some High School</option>
                                <option value="High School Degree / GED">High School Degree / GED</option>
                                <option value="Associates Degree / Some College">Associates Degree / Some College</option>
                                <option value="Bachelor's Degree">Bachelor's Degree</option>
                                <option value="Trade School">Trade School</option>
                                <option value="Master's Degree">Master's Degree</option>
                                <option value="Professional Degree">Professional Degree (MD, DDS, DVM, LLB, JD, DD, etc.)</option>
                                <option value="Doctoral Degree">Doctoral Degree (Ph.D. or Ed.D)</option>

                            </select>
                            <p></p>
                            <p><b>5. What is your annual income?</b></p>
                            <p></p>
                            <select id="income" name="income" required>
                                <option value="">No selection</option>
                                <option value="Less than $25,000">Less than $25,000</option>
                                <option value="$25,000 - $50,000">$25,000 - $50,000</option>
                                <option value="$50,000 - $100,000">$50,000 - $100,000</option>
                                <option value="$100,000 - $200,000">$100,000 - $200,000</option>
                                <option value="More than $200,000">More than $200,000</option>
                            </select>
                            <p></p>
                            <p><b>6. Please select the option that best describes your political party affiliation:</b></p>
                            <select id="political" name="political" onchange="choosePoliticalOther()" required>
                                <option value="">No selection</option>
                                <option value="Strong Democrat">Strong Democrat</option>
                                <option value="Weak Democrat">Not very strong Democrat</option>
                                <option value="Lean Democrat">Independent, lean toward Democrat</option>
                                <option value="Independent">Independent or unaffiliated</option>
                                <option value="Lean Republican">Independent, lean toward Republican</option>
                                <option value="Weak Republican">Not very strong Republican</option>
                                <option value="Strong Republican">Strong Republican</option>
                                <option value="Other">Other (please specify)</option>
                            </select>
                            <input id="other_political" type="text" name="political" hidden>
                            <p></p>
                            <g:hiddenField name="trainingSetId" value="${trainingSetId}"/>
                            <g:hiddenField name="assignmentId" value="${assignmentId}"/>
                            <g:submitButton name="submit" class="btn btn-success" value="Submit"/>
                        </g:form>
                    </div>


                </div>
            </section>
        </div>
    </div>


<script type="text/javascript">
    $('.countrypicker').countrypicker();
    function chooseGenderOther(){
        if($('#gender').val()==="Other"){
            $('#other_gender').show();
            $('#other_gender').prop('required',true);
        }else{
            $('#other_gender').removeAttr('required');
            $('#other_gender').hide();
        }
    }
    function chooseCountryOther(){
        if($('#country').val()==="Other"){
            $('#other_country').show();
            $('#other_country').prop('required',true);
        }else{
            $('#other_country').removeAttr('required');

            $('#other_country').hide();
        }
    }
    function chooseRaceOther(){
        if($('#race').val()==="other"){
            $('#other_race').show();
            $('#other_race').prop('required',true);
        }else{
            $('#other_race').removeAttr('required');

            $('#other_race').hide();
        }
    }
    function chooseLanguageOther(){
        if($('#language').val()==="Other"){
            $('#other_language').show();
            $('#other_language').prop('required',true);
        }else{
            $('#other_language').removeAttr('required');

            $('#other_language').hide();
        }
    }

    function choosePoliticalOther(){
        if($('#political').val()==="Other"){
            $('#other_political').show();
            $('#other_political').prop('required',true);
        }else{
            $('#other_political').removeAttr('required');
            $('#other_political').hide();
        }
    }

    $(document).ready(function() {
        chooseGenderOther()
        chooseCountryOther()
        chooseLanguageOther()
        choosePoliticalOther()
        chooseRaceOther()
    })


</script>

%{--<footer class="main-footer">--}%
%{--    <div class="pull-right hidden-xs">--}%
%{--        <b>Version</b> 1.0.0--}%
%{--    </div>--}%
%{--    <strong>Copyright &copy; 2016 <a href="javascript:void(0);">Loom</a>.--}%
%{--    </strong> All rights reserved.--}%
%{--</footer>--}%
</body>
</html>