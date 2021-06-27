package com.example.tweetapp.services;

import com.codename1.io.Preferences;
import com.codename1.rad.util.NonNull;
import com.codename1.util.AsyncResource;

import java.util.Date;

/**
 * A client for interacting with the server.
 */
public class TweetAppClient {

    /**
     * Flag to indicate that we are currently logged in.
     */
    private boolean loggedIn;

    /**
     * The currently logged in user Id.  In this mock implementation the user Id
     * is just the email address or phone number.
     */
    private String loggedInUserId;

    public boolean isLoggedIn(){
        return loggedIn;
    }

    public String getLoggedInUserId() {
        return loggedInUserId;
    }

    public abstract static class Response {
        /**
         * Whether the signup was successful
         */
        private boolean success;

        /**
         * The response code.  200 for success.
         * Make up error codes to fit needs.
         */
        private int responseCode;

        /**
         * A message related to the response code.  Contains error message
         * in case of errors.
         */
        private String message;

        public boolean isSuccess() {
            return success;
        }

        void setSuccess(boolean success) {
            this.success = success;
        }

        public int getResponseCode() {
            return responseCode;
        }

        void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

        public String getMessage() {
            return message;
        }

        void setMessage(String message) {
            this.message = message;
        }
    }


    /**
     * A response object that is passed to the SignupRequest callback
     * upon completion.
     */
    public static class SignupResponse extends Response {

        /**
         * Reference to request that this response is for.
         */
        private SignupRequest request;

        public SignupRequest getRequest() {
            return request;
        }


    }



    /**
     * Encapsulates a signup request to send to the server.  Modify this
     * class to include the information you require in your signup process.
     *
     */
    public  class SignupRequest extends AsyncResource<SignupResponse> {
        /**
         * The email address of the user.
         */
        private String email,
        /**
         * The phone number of the user.
          */
        phone,

        /**
         * The name of the user.
         */
        name;

        /**
         * The birth date of the user.
         */
        private Date birthDate;

        /**
         * Send the signup request.
         * @return
         */
        public SignupRequest signup() {
            return TweetAppClient.this.signup(this);
        }

        public SignupRequest email(String email) {
            this.email = email;
            return this;
        }

        public SignupRequest phone(String phone) {
            this.phone = phone;
            return this;
        }

        public SignupRequest birthDate(Date birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public SignupRequest name(String name) {
            this.name = name;
            return this;
        }

    }

    /**
     * Creates a new signup request.
     * @return
     */
    public SignupRequest createSignupRequest() {
        return new SignupRequest();
    }

    /**
     * Sends a signup request to the server.
     * @param request
     * @return
     */
    private SignupRequest signup(SignupRequest request) {

        // This is just mocking the signup process.
        // Change this to contact the server and sign up.
        SignupResponse response = new SignupResponse();
        response.setResponseCode(200);
        response.setMessage("Success");
        response.request = request;
        response.setSuccess(true);
        request.complete(response);

        // To log in we set the loggedInUserId and loggedIn
        // boolean flag.
        loggedInUserId = NonNull.nonNull(request.email, request.phone);
        setLoggedIn(true);
        return request;
    }


    private void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;

        // We stored the logged in status in Preferences only for simplicity
        // because this is a demo app.  In a real app you would probably store
        // a token instead and use it to test whether the user is currently logged in.
        Preferences.set("loggedIn", loggedIn);
    }


    public class LoginResponse extends Response {

        private LoginRequest request;

        public LoginRequest getRequest() {
            return request;
        }
    }
    public class LoginRequest extends AsyncResource<LoginResponse> {
        private String username, password;

        public LoginRequest username(String username) {
            this.username = username;
            return this;
        }

        public LoginRequest password(String password) {
            this.password = password;
            return this;
        }

        public LoginRequest login() {
            return TweetAppClient.this.login(this);
        }
    }

    public LoginRequest createLoginRequest() {
        return new LoginRequest();
    }

    private LoginRequest login(LoginRequest req) {
        LoginResponse response = new LoginResponse();
        response.request = req;
        response.setSuccess(true);
        response.setMessage("Success");
        response.setResponseCode(200);
        setLoggedIn(true);
        loggedInUserId = req.username;

        req.complete(response);
        return req;

    }

    public TweetAppClient() {

        // Logged in status tracked in preferences.
        // In real app, you wouldn't do it this way.  You would track a token
        // and use it to query the logged in status.
        loggedIn = Preferences.get("loggedIn", false);
        if (loggedIn) {
            // Hardcoded logged in user because this is a demo app.
            // In real app you would set logged in user according to
            // who is actually logged in.
            loggedInUserId = "@shannah78";
        }
    }
}
