package com.example.androidserversocket;

import android.content.Context;

import java.util.ArrayList;


public class Appconfig {

    //    public static final String styleThumbsup = "thumbsupdown";
//    public static final String stylePickone = "pickone";
//    public static final String stylestars = "stars";
//    public static final String stylehotornot = "hotornot";
//    public static final String styleranking = "ranking";
//    public static final String styleyesno = "yesno";

    public static final String option1 = "option1";
    public static final String option2 = "option2";
    public static final String option3 = "option3";
    public static final String option4 = "option4";
    public static final String option5 = "option5";
    public static final String templateTxt = "Templatetxt";
    public static final String template1 = "Template1";
    public static final String template2 = "Template2";
    public static final String template3 = "Template3";
    public static final String template4 = "Template4";
    public static final String template5 = "Template5";
    public static final String text = "text";
    public static final String image = "image";
    public static final String audio = "audio";
    public static final String video = "video";
    public static boolean is_quick = false;
    public static boolean is_delete = false;
    public static String interestvalue = "";

    public static String theme_type = "0";
    public static String theme_type_apply = "0";

    public static String backgroundImgPath = "";
    public static String pollImagePath = "";
    public static String interestlist;

    //    Create Poll data store here for API call.
    // public static String title = "";
    public static String title_attributes = "";//[{"label_text":"Black","font_face":"Arial","font_color":"#FFFF","font_size":"12","sort_order":"0"}]
    public static String poll_data_type = ""; //0 = image / 1 = audio / 2 = video / 3 = text
    public static String poll_type = ""; //0 = pick one / 1 = stars / 2 = thumbs / 3 = hot not / 4 = ranking / 5 = yes/no
    public static String no_of_items = ""; //numeric value for upload medias.
    public static String template = ""; //Ex. left, center, right
    public static String is_forever = "0"; //0 = not forever 1 = forever
    public static String duration = "2"; // 0 = hours / 1 = days / 2 = weeks
    public static String duration_number = "1"; //numeric value for how many days you want to active your poll.
    public static String is_private = "0"; //0 = not private 1 = private
    public static String is_open = "0"; //0 = not open 1 = open
    public static String comments = "1"; //0 = not allowed comment 1 = allowed comment
    public static String report_type = "0"; //0 = pie / 1 = bar / 2 = circle
    public static String schedule = ""; //A date from you want to active your poll.
    public static String background_image = "";
    public static String background_color = "#ffffff";
    public static String media1 = "";
    public static String media2 = "";//Media depends on no_of_items like no_of_items = 2 then
    public static String media3 = "";
    public static String media4 = "";
    public static String media5 = "";
    // pass media as media1, media2
    public static String media_data1 = ""; //Media depends on no_of_items like no_of_items = 2 then pass media as media_data1,
    public static String media_data2 = ""; //[{"label_text":"Black","font_face":"Arial","font_color":"#FFFF","font_size":"12","sort_order":"0"}]
    public static String media_data3 = "";
    public static String media_data4 = "";
    public static String media_data5 = "";
    public static String mediaPath1 = "";
    public static String mediaPath2 = "";
    public static String mediaPath3 = "";
    public static String mediaPath4 = "";
    public static String mediaPath5 = "";
    public static String media_thumb1 = ""; // Thumbnail of image , audio and video
    public static String media_thumb2 = "";
    public static String media_thumb3 = "";
    public static String media_thumb4 = "";
    public static String media_thumb5 = "";
    public static String media_video = "";
    public static String media_coverPic = "";

    //=======================Default values for create poll ============================
    public static String default_bg_color = "";
    public static String default_label = "";
    public static String default_label_face = "";
    public static String default_label_size = "";
    public static String default_label_font_color = "";

    public static String default_title_face = "";
    public static String default_title_size = "";
    public static String default_title_font_color = "";
    public static String group_json = "";
    public static String user_json = "";
    public static String default_title_bg_color = "";

    /////////////////////////////
    public static String step1SelectionOption; // no of data option
    //    public static String step1SelectionStyle;  // style of poll eg. star, thumb, pic-one, hot, etc
    public static String step2SelectPollData;
    public static String step2SelectTemplate;
    //================= Hiral ======================//
    public static String SocialID = "";
    public static String Email = "";
    public static String AvatarImgURL = "";
    public static String FullName = "";
    public static String UserName = "";
    public static String isSocial = "";
    public static int Provider = 0;
    public static String SocialType = "";
    public static int LoginVia = 0;
    public static String TwitterStatus = "0";
    public static String description;
    public static String fontName;
    public static int fontsize;
//    public static String prevFullName;
//    public static String prevUserName;
//    public static String prevAvatar;
//    public static String prevPollLink;
//    public static String prevOwnerUserId;
//    public static String prevOwnerUserName;
//    public static String prevOwnerAvatar;
//    public static List<PreviewPoll> prevArrayPollOptions;

    // for preview poll response store
//    public static ResponsePollPreviewDetails mResponsePollPreviewDetails;
//    public static ResponsePollStatus responsePollStatus;
    public static String strSubmit = "Submit";
    public static String strViewResult = "View Results";
    public static String jobjMessages;
    public static boolean isHome = false;
//    public static ResponseListener UserDetailJson;

    public static String VideoPath = "";
    public static String OriginalCompressedVideoPath = "";
    public static String VideoPathCopy = "";
    public static String lable_texts = "";
    public static String font_face_type = "";
    public static int font_sizes = 14;
    public static String font_color = "#0e76bd";
    public static String title = "";
    public static String privacy_type = "";

    public static String Privacy = "0";

    public static int likes = 0;

    public static String allow_rating = "1";
    public static String allow_like = "";
    public static String allow_comment = "1";
    public static String allow_following = "";
    public static String allow_pushnotification = "";

    public static String interest = "0";
    public static String sCustomerID = "0";
    public static String textview_lable = "";

    public static String produce_soapbox_tag = "";
    public static String produce_soapbox_tag_simple = "";
    public static String produce_soapbox_description = "";


    public static String Video_option = "";
    public static long[] twitterFriendsList;
    //    public static String allInterest;
    // public static ArrayList<UserData> allInterests;
    public static ArrayList<String> countryList;
    public static ArrayList<String> attachment_image;
    public static String FacebookSocialID = "";
    public static String TwitterSocialID = "";
    public static String GoogleSocialID = "";
    public static String soapbox_comment = "";
    public static String soapbox_rating = "";
    public static String soapbox_privacy = "";
    public static String downloadedVideo = "";
    public static boolean isChanges = false;
    public static String OriginalVideoPath = "";

    public static boolean isSelectedfilter = false;

    public static String bloodgroup_type = "";
    public static String gender_type = "";
    public static String sample_type = "";
    public static String blood = "";


    //    public static ArrayList<InterestresponsePollStatusList> allInterests;
    Context context;

    public Appconfig(Context mxt) {

        context = mxt;
    }

    public static void clearData() {
        Appconfig.mediaPath1 = "";
        Appconfig.media_data1 = "";
        Appconfig.mediaPath2 = "";
        Appconfig.media_data2 = "";
        Appconfig.mediaPath3 = "";
        Appconfig.media_data3 = "";
        Appconfig.mediaPath4 = "";
        Appconfig.media_data4 = "";
        Appconfig.mediaPath5 = "";
        Appconfig.media_data5 = "";
        Appconfig.backgroundImgPath = "";
    }
}
