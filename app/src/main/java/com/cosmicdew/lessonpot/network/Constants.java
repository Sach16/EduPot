package com.cosmicdew.lessonpot.network;

/**
 * Created by S.K. Pissay on 5/3/16.
 */
public class Constants {

    public static final String AUTHKEY = "user-key";
    public static final String LOCATIONS_API = "locations";
    public static final String LOCATION_DETAILS = "location_details";
    public static final String SEARCH_QUERY = "q";

    public static final String ENTITY_ID = "entity_id";
    public static final String ENTITY_TYPE = "entity_type";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";

    private String API_KEY = "07b2294e08dd61ee580be44c23a91c1b";

    public String getApiKey() {
        return API_KEY;
    }

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String BEARER = "Bearer ";
    public static final String DEVICE = "device/";
    public static final String DEVICES = "devices/";
    public static final String SESSIONS = "sessions/";
    public static final String USERS = "users/";
    public static final String USER = "user/";
    public static final String ROLES = "roles/";
    public static final String BOARDCHOICES = "boardchoices/";
    public static final String STATES = "states/";
    public static final String SYSTEMBOARDS = "systemboards/";
    public static final String SYSTEMBOARDCLASSES = "systemboardclasses/";
    public static final String VIEWED = "viewed/"; //Viewed Tab
    public static final String SHARED = "shared/"; //Recieved Tab
    public static final String CREATED = "created/"; //Mine Tab
    public static final String GMR = "gmr/";
    public static final String GS = "gs/";
    public static final String BOARDCLASSES = "boardclasses/";
    public static final String USERBOARDCLASSES = "userboardclasses/";
    public static final String SYLLABI = "syllabi/";
    public static final String CHAPTERS = "chapters/";
    public static final String LESSONS = "lessons/";
    public static final String ATTACHMENTS = "attachments/";
    public static final String ATTACHMENT_TYPE = "attachment_type";
    public static final String ATTACHMENT = "attachment";
    public static final String PROFILE_PIC  = "profile_pic";
    public static final String CONNECTIONS = "connections/";
    public static final String CONNECTION_REQUESTS = "connection_requests/";
    public static final String VIEWS = "views/";
    public static final String POST = "post/";
    public static final String SOURCES = "sources/";
    public static final String AUDIENCE = "audience/";
    public static final String ALL = "all/";
    public static final String BOARDCLASS = "boardclass/";
    public static final String SYLLABUS = "syllabus/";
    public static final String SHARE = "share/";
    public static final String USERFILTER = "userfilter/";
    public static final String CREDENTIALS = "credentials/";
    public static final String USERDEVICES = "userdevices/";
    public static final String LESSONSHARES = "lessonshares/";
    public static final String APPCONFIG = "appconfig/";

    public static final String PHONE = "phone";
    public static final String OTP = "otp";

    public static final String LOGIN_PHONE_NUMBER = "phone";
    public static final String PIN = "pin";
    public static final String NAME = "name";
    public static final String COMMENTS = "comments";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    public static final String TERMS_ACCEPTED = "terms_accepted";
    public static final String BOARDCHOICE_TXT = "boardchoice";
    public static final String STATE_TXT = "state";
    public static final String BOARD_TXT = "board";
    public static final String SESSION_ID = "session-id";
    public static final String USER_ID = "user-id";
    public static final String BOARDCLASS_ID = "boardclass-id";
    public static final String CHAPTER_ID = "chapter-id";
    public static final String SYLLABUS_ID = "syllabus-id";
    public static final String AUDIO = "audio";
    public static final String IMAGE = "image";
    public static final String TEXT = "text";
    public static final String LENGTH = "length";
    public static final String SLOT = "slot";
    public static final String CONNECTION = "connection";
    public static final String IS_APPROVED = "is_approved";
    public static final String IS_SPAM = "is_spam";
    public static final String FLAT = "flat";
    public static final String SOURCE = "source";
    public static final String USERS_TXT = "users";
    public static final String USER_TXT = "user";
    public static final String NOTIFICATION_TOKEN = "notification_token";
    public static final String DEVICE_ID = "device-id";
    public static final String TAG = "tag";
    public static final String OBJECT = "object";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String SCHOOL_NAME = "school_name";
    public static final String SCHOOL_LOCATION = "school_location";
    public static final String LESSON_ORDER = "lesson_order";
    public static final String ORDERING = "ordering";
    public static final String LESSON__POSITION = "lesson__position";
    public static final String POSITION = "position";

    public static final String INSCLUDE = "include";
    public static final String MESSAGES = "messages";
    public static final String DATA = "data";

    public static final String CONNECTION_REQUEST = "connection:request";
    public static final String CONNECTION_APPROVED = "connection:approved";
    public static final String LESSON_SHARE = "lesson:share";
    public static final String LESSON_SHARES_FROM_CONNECTION = "lesson:shares_from_connection";
    public static final String LESSON_SHARES_FROM_SYLLABUS = "lesson:shares_from_syllabus";
    public static final String LESSON_EDIT = "lesson:edit";

    public static String apiMethodEx(String apiMethod, String uuid) {
        StringBuffer lBuf = new StringBuffer();
        if (null != uuid) {
            return lBuf.append(apiMethod).append("/").append(uuid).toString();
        } else {
            return lBuf.append(apiMethod).toString();
        }
    }
}
