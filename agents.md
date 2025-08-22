Project use Java 8. No use of NodeJS or Python.



### Tests

Rules for testing:
- All features should have a unit test or tests.
- Should be used JUnit with matchers from Hamcrest library.
- Should be used Mockito library when needed.

Each test method should have 3-4 sections, each section devided by commont line and empty line:
- Initialization - use `final` keyword to hardcode constants that will be used in test.
- Mocks - Crete mocks with Mockito library. This section is optional.
- Execution - Execute test
- Assertion - Do assertion with Hamcrest library.

Example:

    @Test
    public void parseDecodesRedirectLinks() {
        // Initialization.
        final String expectedTwitter = "https://twitter.com/StephenJohnPeel";
        final String expectedOther = "https://example.com";
        final String html = "<div id='links-section'"
                + "><a class='yt-core-attributed-string__link' href='https://www.youtube.com/redirect?event=channel_description&redir_token=token&q=https%3A%2F%2Ftwitter.com%2FStephenJohnPeel'></a>"
                + "<a class='yt-core-attributed-string__link' href='https://www.youtube.com/redirect?event=channel_description&redir_token=token&q=https%3A%2F%2Fexample.com'></a>"
                + "</div>";
        
        // Execution.
        Parser parser = new Parser();
        ChannelAbout channel = parser.parse("https://www.youtube.com/@some/about", html);
        
        // Assertion.
        assertThat(channel.getLinkToTwitter(), is(expectedTwitter));
        assertThat(channel.getOtherLinks(), is(expectedOther));
    }



### GIT commits format

The first line in commit is short description of the commit.

Then should be empty line.

Then should be full description what and why was done. Put notes about the task that you solved and how it was soved. Put any notes that you have in mind.