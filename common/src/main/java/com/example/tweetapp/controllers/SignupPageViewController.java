package com.example.tweetapp.controllers;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.ToastBar;
import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.util.NonNull;
import com.codename1.ui.Dialog;
import com.example.tweetapp.services.TweetAppClient;
import com.example.tweetapp.views.SignupPage;
import com.example.tweetapp.views.SignupPageModel;
import com.example.tweetapp.views.SignupPageModelWrapper;
import com.example.tweetapp.views.HomePageController;

public class SignupPageViewController extends ViewController {
    /**
     * Creates a new ViewController with the given parent controller.
     *
     * @param parent
     */
    public SignupPageViewController(Controller parent) {
        super(parent);
    }

    @Override
    protected void initControllerActions() {
        super.initControllerActions();
        ActionNode.builder()
                .addToController(this, SignupPage.NEXT, this::handleSubmit);
    }

    /**
     * Handles the registration form submission
     * @param evt
     */
    private void handleSubmit(ActionNode.ActionNodeEvent evt) {
        // Get reference to the view's model via the event.
        SignupPageModel viewModel = SignupPageModelWrapper.wrap(evt.getEntity());



        // Do some validation
        boolean failedValidation = false;
        if (viewModel.isUseEmail() && NonNull.empty(viewModel.getEmail())) {
            viewModel.setPhoneOrEmailErrorMessage("Email address cannot be empty");
            failedValidation = true;
        } else if (!viewModel.isUseEmail() && NonNull.empty(viewModel.getPhone())) {
            viewModel.setPhoneOrEmailErrorMessage("Phone cannot be empty");
            failedValidation = true;
        } else {
            viewModel.setPhoneOrEmailErrorMessage("");
        }

        if (NonNull.empty(viewModel.getName())) {
            viewModel.setNameErrorMessage("Name cannot be empty");
            failedValidation = true;
        } else {
            viewModel.setNameErrorMessage("");
        }

        if (NonNull.empty(viewModel.getBirthDate())) {
            viewModel.setBirthDateErrorMessage("Birthdate cannot be empty");
            failedValidation = true;
        } else {
            viewModel.setBirthDateErrorMessage("");
        }

        if (failedValidation) {
            return;
        }

        // Get reference to the webservice client
        TweetAppClient client = lookup(TweetAppClient.class);

        TweetAppClient.SignupRequest request = client.createSignupRequest()
                .name(viewModel.getName())
                .birthDate(viewModel.getBirthDate());

        if (viewModel.isUseEmail()) {
            request.email(viewModel.getEmail());
        } else {
            request.phone(viewModel.getPhone());
        }

        InfiniteProgress progess = new InfiniteProgress();
        Dialog progressDialog = progess.showInfiniteBlocking();
        request.signup().onResult((res, err) -> {
            progressDialog.dispose();
            if (err != null) {
                ToastBar.showErrorMessage(err.getMessage());
                return;
            }
            new HomePageController(getApplicationController()).show();

        });






    }
}
