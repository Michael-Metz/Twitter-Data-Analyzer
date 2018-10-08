package nlp;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


/**
 * This is a tweet object. It holds all the columns that is in the twitter data.
 *
 * the instance variables are as follows
 *
 * 1.                tweet properties - represent each column of a tweet
 * 2. serializable sentence/sentiment - additional properties that is used to hold sentiment info so we dont have to un nlp again
 * 3.    transient nlp sentences/date - these properties will not be stored in serial object. These are populated using nlp and hold
 *                                      all the annotation for sentences.
 *
 * #TODO consider using a hashmap ( key value pair ) for all the columns
 */
public class Tweet implements Comparable, Serializable {

    //column properties
    private String userId;
    private String userName;
    private String userScreenName;
    private String dateUserCreatedAccount;
    private String statusesPosted;
    private String amountOfFollowers;
    private String amountUserIsFollowing;
    private String userLocation;
    private String userTimezone;
    private String userLanguage;
    private String statusId;
    private String statusLanguage;
    private ZonedDateTime dateCreated;
    private String statusLocation;
    private String isFavorited;
    private String amountOfFavorites;
    private String isRetweeted;
    private String amountOfRetweets;
    private String isRetweetedByMe;
    private String isARetweet;
    private String retweetedStatusId;
    private String isPossiblySensitive;
    private String isTruncated;
    private String place;
    private String description;
    private String text;

    /**
     * Constructor that has every column of the csv file.
     *
     */
    public Tweet() {

    }

    /**
     * Copy Constructor
     * @param tweet
     */
    public Tweet(Tweet tweet){
        this.userId = tweet.userId;
        this.userName = tweet.userName;
        this.userScreenName = tweet.userScreenName;
        this.dateUserCreatedAccount = tweet.dateUserCreatedAccount;
        this.statusesPosted = tweet.statusesPosted;
        this.amountOfFollowers = tweet.amountOfFollowers;
        this.amountUserIsFollowing = tweet.amountUserIsFollowing;
        this.userLocation = tweet.userLocation;
        this.userTimezone = tweet.userTimezone;
        this.userLanguage = tweet.userLanguage;
        this.statusId = tweet.statusId;
        this.statusLanguage = tweet.statusLanguage;
        this.dateCreated = tweet.dateCreated;
        this.statusLocation = tweet.statusLocation;
        this.isFavorited = tweet.isFavorited;
        this.amountOfFavorites = tweet.amountOfFavorites;
        this.isRetweeted = tweet.isRetweeted;
        this.amountOfRetweets = tweet.amountOfRetweets;
        this.isRetweetedByMe = tweet.isRetweetedByMe;
        this.isARetweet = tweet.isARetweet;
        this.retweetedStatusId = tweet.retweetedStatusId;
        this.isPossiblySensitive = tweet.isPossiblySensitive;
        this.isTruncated = tweet.isTruncated;
        this.place = tweet.place;
        this.description = tweet.description;
        this.text = tweet.text;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public void setDateUserCreatedAccount(String dateUserCreatedAccount) {
        this.dateUserCreatedAccount = dateUserCreatedAccount;
    }

    public void setStatusesPosted(String statusesPosted) {
        this.statusesPosted = statusesPosted;
    }

    public void setAmountOfFollowers(String amountOfFollowers) {
        this.amountOfFollowers = amountOfFollowers;
    }

    public void setAmountUserIsFollowing(String amountUserIsFollowing) {
        this.amountUserIsFollowing = amountUserIsFollowing;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public void setUserTimezone(String userTimezone) {
        this.userTimezone = userTimezone;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public void setStatusLanguage(String statusLanguage) {
        this.statusLanguage = statusLanguage;
    }

    public void setStatusLocation(String statusLocation) {
        this.statusLocation = statusLocation;
    }

    public void setIsFavorited(String isFavorited) {
        this.isFavorited = isFavorited;
    }

    public void setAmountOfFavorites(String amountOfFavorites) {
        this.amountOfFavorites = amountOfFavorites;
    }

    public void setIsRetweeted(String isRetweeted) {
        this.isRetweeted = isRetweeted;
    }

    public void setAmountOfRetweets(String amountOfRetweets) {
        this.amountOfRetweets = amountOfRetweets;
    }

    public void setIsRetweetedByMe(String isRetweetedByMe) {
        this.isRetweetedByMe = isRetweetedByMe;
    }

    public void setIsARetweet(String isARetweet) {
        this.isARetweet = isARetweet;
    }

    public void setRetweetedStatusId(String retweetedStatusId) {
        this.retweetedStatusId = retweetedStatusId;
    }

    public void setIsPossiblySensitive(String isPossiblySensitive) {
        this.isPossiblySensitive = isPossiblySensitive;
    }

    public void setIsTruncated(String isTruncated) {
        this.isTruncated = isTruncated;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public String getDateUserCreatedAccount() {
        return dateUserCreatedAccount;
    }

    public String getStatusesPosted() {
        return statusesPosted;
    }

    public String getAmountOfFollowers() {
        return amountOfFollowers;
    }

    public String getAmountUserIsFollowing() {
        return amountUserIsFollowing;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getUserTimezone() {
        return userTimezone;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public String getStatusId() {
        return statusId;
    }

    public String getStatusLanguage() {
        return statusLanguage;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatusLocation() {
        return statusLocation;
    }

    public String getIsFavorited() {
        return isFavorited;
    }

    public String getAmountOfFavorites() {
        return amountOfFavorites;
    }

    public String getIsRetweeted() {
        return isRetweeted;
    }

    public String getAmountOfRetweets() {
        return amountOfRetweets;
    }

    public String getIsRetweetedByMe() {
        return isRetweetedByMe;
    }

    public String getIsARetweet() {
        return isARetweet;
    }

    public String getRetweetedStatusId() {
        return retweetedStatusId;
    }

    public String getIsPossiblySensitive() {
        return isPossiblySensitive;
    }

    public String getIsTruncated() {
        return isTruncated;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "\nuserId='" + userId + '\'' +
                "\n, userName='" + userName + '\'' +
                "\n, userScreenName='" + userScreenName + '\'' +
                "\n, dateUserCreatedAccount='" + dateUserCreatedAccount + '\'' +
                "\n, statusesPosted='" + statusesPosted + '\'' +
                "\n, amountOfFollowers='" + amountOfFollowers + '\'' +
                "\n, amountUserIsFollowing='" + amountUserIsFollowing + '\'' +
                "\n, userLocation='" + userLocation + '\'' +
                "\n, userTimezone='" + userTimezone + '\'' +
                "\n, userLanguage='" + userLanguage + '\'' +
                "\n, statusId='" + statusId + '\'' +
                "\n, statusLanguage='" + statusLanguage + '\'' +
                "\n, dateCreated='" + dateCreated + '\'' +
                "\n, statusLocation='" + statusLocation + '\'' +
                "\n, isFavorited='" + isFavorited + '\'' +
                "\n, amountOfFavorites='" + amountOfFavorites + '\'' +
                "\n, isRetweeted='" + isRetweeted + '\'' +
                "\n, amountOfRetweets='" + amountOfRetweets + '\'' +
                "\n, isRetweetedByMe='" + isRetweetedByMe + '\'' +
                "\n, isARetweet='" + isARetweet + '\'' +
                "\n, retweetedStatusId='" + retweetedStatusId + '\'' +
                "\n, isPossiblySensitive='" + isPossiblySensitive + '\'' +
                "\n, isTruncated='" + isTruncated + '\'' +
                "\n, place='" + place + '\'' +
                "\n, description='" + description + '\'' +
                "\n, text='" + text + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return Objects.equals(userId, tweet.userId) &&
                Objects.equals(dateCreated, tweet.dateCreated) &&
                Objects.equals(text, tweet.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, dateCreated, text);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Object o) {

        if(!(o instanceof Tweet))
            throw new ClassCastException();

        Tweet other = (Tweet) o;
        if(this.dateCreated.isBefore(other.dateCreated)) //this is smaller
            return -1;
        if(this.dateCreated.isAfter(other.dateCreated)) //this is bigger
            return 1;
        else if(this.equals(other)) //this is the same
            return 0;
        return 0;
    }

}
