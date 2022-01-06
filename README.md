# demo-spring-react-example: dsrk


Storybook is here:
* Production: <https://happycows.github.io/demo-spring-react-example-docs/>
* QA:  <https://happycows.github.io/demo-spring-react-example-docs-qa/>

The GitHub actions script to deploy the Storybook to QA requires that a repository secret called `TOKEN` be set up; this should be an access token for the repository.   This secret can be obtained by visiting the settings page for either the organization, or a user with access to the organization, visiting Developer Settings, and then Personal Access Tokens. 

![image](https://user-images.githubusercontent.com/1119017/147836507-0190801c-ce94-4e5a-9abe-6a1d2d0455af.png)


# Test setup

For testing, you need to set a repository secret `TEST_PROPERTIES` to be the contents of `.env.SAMPLE`.   It is not necessary to have
valid values for each of the environment variables, but if they are undefined, the tests will fail.

# Setup before running application

* Obtain a Google client id and client secret
  - This is done at the Google Developer Console <https://console.cloud.google.com/> via the left navigation under `APIs and Services`, then `Credentials`, then `Create Credentials`
  - The callback url should be: `http://localhost:8080/login/oauth2/code/google`.  (Note: `http` not `https` for localhost).
  - You will also need to add a callback URL for Heroku if you are deploying there, e.g. `https://myappname.herokuapp.com/login/oauth2/code/google` (Note the `https` in the Heroku case.)


# Getting Started on localhost

* The backend and frontend should be run separately, so first start by opening two separate terminal windows.
* In the first window, start up the backend with `mvn spring-boot:run`
* In the second window, `cd frontend` then:
  - If running for the first time, do `npm install` to install dependencies.
  - After that, do `npm start`
* Then, the app should be available on <http://localhost:8080>

If it doesn't work at first, e.g. you have a blank page on  <http://localhost:8080>, give it a minute and a few page refreshes.  Sometimes it takes a moment for everything to settle in.

# Accessing swagger

To access the swagger API endpoints, use:

* < <http://localhost:8080/swagger-ui/index.html>

# To run React Storybook

* cd into frontend
* use: npm run storybook
* This should put the storybook on http://localhost:6006
* Additional stories are added under frontend/src/stories

* For documentation on React Storybook, see: https://storybook.js.org/
