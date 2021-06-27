package com.example.tweetapp.controllers;

import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.schemas.Thing;
import com.codename1.twitterui.models.Tweet;
import com.codename1.twitterui.models.TweetWrapper;
import com.codename1.twitterui.views.TweetRowView;
import com.codename1.ui.FontImage;

public class HomePageViewController extends ViewController {
    /**
     * Creates a new ViewController with the given parent controller.
     *
     * @param parent
     */
    public HomePageViewController(Controller parent) {
        super(parent);
    }

    @Override
    protected void initControllerActions() {
        super.initControllerActions();

        ActionNode.builder()
                .icon(FontImage.MATERIAL_CHAT_BUBBLE_OUTLINE)
                .addToController(this, TweetRowView.TWEET_ACTIONS, evt -> {});

        ActionNode.builder()
                .icon(FontImage.MATERIAL_FORWARD)
                .addToController(this, TweetRowView.TWEET_ACTIONS, evt -> {});

        ActionNode.builder()
                .icon(FontImage.MATERIAL_FAVORITE_OUTLINE)
                .addToController(this, TweetRowView.TWEET_ACTIONS, evt -> {});

        ActionNode.builder()
                .icon(FontImage.MATERIAL_SHARE)
                .addToController(this, TweetRowView.TWEET_ACTIONS, etc -> {});



        ActionNode.builder()
            .label("Not interested in this")
            .icon(FontImage.MATERIAL_MOOD_BAD)
                .addToController(this, TweetRowView.TWEET_MENU_ACTIONS, evt -> {});


        ActionNode.builder()
            .icon(FontImage.MATERIAL_REMOVE)
            .label(e->{
                    Tweet tweet = TweetWrapper.wrap(e);
                    if (tweet.isEntity(Tweet.author)) {
                        return "Unfollow "+tweet.getAuthor().getName();
                    } else if (!tweet.isEmpty(Tweet.authorId)){
                        return "Unfollow "+tweet.getAuthorId();
                    }
                    return "Unfollow this user";
                })
                .addToController(this, TweetRowView.TWEET_MENU_ACTIONS, evt -> {});

        ActionNode.builder()
            .icon(FontImage.MATERIAL_VOLUME_OFF)
            .label(e->{
                    Tweet tweet = TweetWrapper.wrap(e);
                    if (tweet.isEntity(Tweet.author)) {
                        return "Mute "+tweet.getAuthor().getName();
                    } else if (!tweet.isEmpty(Tweet.authorId)){
                        return "Mute "+tweet.getAuthorId();
                    }
                    return "Mute this user";
                })
            .addToController(this, TweetRowView.TWEET_MENU_ACTIONS, evt -> {});
    }
}
