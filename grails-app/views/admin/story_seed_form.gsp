<g:form id="dynamic-content" name="upload-story-form">
    <table class="table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Seed</th>
            </tr>
        </thead>
        <tbody>
    <g:each in="${stories}" var="story" status="i">
        <tr>
            <td>${story.id}</td>
            <td>${story.name}</td>
            <td><g:textField name="stories[${i}].seed" value="${story?.seed?.name}"/></td>
            <!-- Include hidden field to retain ID -->
            <g:hiddenField name="stories[${i}].id" value="${story.id}" />
        </tr>
    </g:each>
    </tbody>
</table>
</g:form>

<script type="text/javascript">
    function dynamicContentSubmit(elt) {
        var formData = $(elt).serializeArray();
        var stories = {};

        // Organize formData into structured stories
        formData.forEach(function(field) {
            // Extract index and property name from the field name pattern "stories[index].propertyName"
            var match = field.name.match(/stories\[(\d+)\]\.(id|seed)/);
            if (match) {
                var index = match[1];
                var property = match[2];

                if (!stories[index]) {
                    stories[index] = {};
                }

                stories[index][property] = field.value;
            }
        });

        $.ajax({
            url: "/loom/admin/updateStorySeeds",
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(stories),
            success: function (response) {
                console.log('Form submitted successfully');
                console.log(response)
            },
            error: function (xhr, status, error) {
                console.error('Error submitting form: ', error);
                // Handle error
            }
        });
    }
</script>