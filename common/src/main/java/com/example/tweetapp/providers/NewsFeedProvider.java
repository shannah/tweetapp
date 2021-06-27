package com.example.tweetapp.providers;

import com.codename1.io.Log;
import com.codename1.io.rest.Rest;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.rad.io.ParsingService;
import com.codename1.rad.models.AbstractEntityListProvider;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.processing.Result;
import com.codename1.twitterui.models.TWTAuthor;
import com.codename1.twitterui.models.TWTAuthorImpl;
import com.codename1.twitterui.models.Tweet;
import com.codename1.twitterui.models.TweetImpl;
import com.codename1.util.StringUtil;
import com.codename1.xml.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A "provider" class that provides rows to EntityListViews that want to display a news feed.
 * This uses {@link Tweet} objects as the row models.
 *
 * This is used for the news feed in the HomePage.
 */
public class NewsFeedProvider extends AbstractEntityListProvider {
    @Override
    public Request getEntities(Request request) {

        // Fetch NASA RSS Feed
        Rest.get("https://www.nasa.gov/rss/dyn/breaking_news.rss")
                .onError(evt->{
                    // Propagate error back to the request
                    request.error(evt.getError());
                })
                .fetchAsBytes(v -> {
                    // The ParsingService will parse XML asynchronously on a background thread
                    // so that it doesn't impede the EDT
                    ParsingService parser = new ParsingService();

                    parser.parseXML(new InputStreamReader(new ByteArrayInputStream(v.getResponseData())))
                        .onResult((res, err) -> {
                            if (err != null) {
                                // Error parsing XML.  Propagate up to request.
                                request.error(err);
                                return;
                            }

                            // Create EntityList which will be returned to the request.
                            EntityList out = new EntityList();

                            // A date formatter to format dates in the RSS feed.
                            // (Determined format string based on the format in the Feed)
                            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm z");

                            // News items are all in <item> tags of the feed.  Get them all as a list
                            List<Element> items = (List<Element>)res.getDescendantsByTagName("item");
                            for (Element item : items) {
                                try {
                                    // Wrapping item in Result object makes it easier
                                    // to extract values in correct format and navigate around
                                    // nulls.
                                    Result ritem = Result.fromContent(item);

                                    // Tweet is an Entity defined in the tweet-app-ui-kit cn1lib
                                    // to encapsulate a "Tweet", which is basically a news item.
                                    // We will use these for the row models of the list.
                                    Tweet tweet = new TweetImpl();
                                    tweet.setText(ritem.getAsString("description"));
                                    String dateString = ritem.getAsString("pubDate");

                                    dateString = StringUtil.replaceAll(dateString, "EDT", "GMT-4:00");
                                    dateString = StringUtil.replaceAll(dateString, "EST", "GMT-5:00");
                                    tweet.setDatePosted(dateFormatter.parse(dateString));
                                    tweet.setImage(item.getFirstChildByTagName("enclosure").getAttribute("url"));
                                    tweet.setLink(ritem.getAsString("link"));

                                    // TWTAuthor is an Entity defined in the tweet-app-ui-kit cn1li
                                    // to encapsulate Tweet author details.
                                    TWTAuthor author = new TWTAuthorImpl();
                                    author.setName("NASA");
                                    author.setThumbnailUrl("https://pluspng.com/img-png/nasa-logo-png-file-nasa-logo-svg-1237.png");
                                    tweet.setAuthor(author);
                                    // Add tweet to the list.
                                    out.add(tweet);


                                } catch (Exception ex) {
                                    // Hopefully no exceptions here, but log them anyways
                                    Log.e(ex);
                                }
                            }

                            // Pass the EntityList to the request so that
                            // it can be used by the requesting EntityListView.
                            request.complete(out);
                        });


                });
        return request;
    }
}
