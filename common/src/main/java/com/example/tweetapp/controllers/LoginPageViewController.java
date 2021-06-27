package com.example.tweetapp.controllers;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.ToastBar;
import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.util.NonNull;
import com.codename1.twitterui.models.TWTUserProfile;
import com.codename1.twitterui.models.TWTUserProfileImpl;
import com.codename1.ui.Dialog;
import com.example.tweetapp.services.TweetAppClient;
import com.example.tweetapp.views.*;

public class LoginPageViewController extends ViewController {
    /**
     * Creates a new ViewController with the given parent controller.
     *
     * @param parent
     */
    public LoginPageViewController(Controller parent) {
        super(parent);
    }

    @Override
    protected void initControllerActions() {
        super.initControllerActions();
        ActionNode.builder()
                .addToController(this, LoginPage.LOGIN, this::handleSubmit);
    }

    /**
     * Handles the registration form submission
     * @param evt
     */
    private void handleSubmit(ActionNode.ActionNodeEvent evt) {
        LoginPageModel viewModel = LoginPageModelWrapper.wrap(evt.getEntity());
        System.out.println("Here");

        boolean validationFailed = false;
        if (NonNull.empty(viewModel.getPhoneEmailOrUsername())) {
            System.out.println("Setting error message");
            viewModel.setPhoneEmailOrUsernameErrorMessage("Please enter a non-empty username");
            validationFailed = true;
        }
        if (NonNull.empty(viewModel.getPassword())) {
            viewModel.setPasswordErrorMessage("Please enter a password");
            validationFailed = true;
        }

        if (validationFailed) {
            return;
        }

        InfiniteProgress infiniteProgress = new InfiniteProgress();
        Dialog progressDialog = infiniteProgress.showInfiniteBlocking();
        TweetAppClient client = lookup(TweetAppClient.class);
        client.createLoginRequest()
                .username(viewModel.getPhoneEmailOrUsername())
                .password(viewModel.getPassword())
                .login()
                .onResult((res, error) -> {
                    progressDialog.dispose();
                    if (error != null) {
                        ToastBar.showErrorMessage(error.getMessage());
                        return;
                    }
                    // Login was successful.  Set the `TWTUserProfile` lookup
                    // NOTE: We hardcode it here, but in real app, we would use the login
                    // details to construct an accurate user profile
                    TWTUserProfile userProfile = new TWTUserProfileImpl();
                    userProfile.setName("Steve Hannah");
                    userProfile.setIdentifier("@shannah78");
                    userProfile.setThumbnailUrl("https://www.codenameone.com/img/steve.jpg");
                    getApplicationController().addLookup(TWTUserProfile.class, userProfile);
                    new HomePageController(getApplicationController()).show();
                });




    }
}
