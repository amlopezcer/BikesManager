package com.amlopezc.bikesmanager.net;

/**
 * Some constants for the HTTP connection
 */
public final class HttpConstants {

    //To identify HTTP operations in the caller activities (to process the result)
    public static final int OPERATION_GET = 1;
    public static final int OPERATION_PUT = 2;
    public static final int OPERATION_POST = 3;
    public static final int OPERATION_DELETE = 4;

    //To select the entity involved and fulfill the URL and the connection
    public static final String ENTITY_STATION = "bikestation";
    public static final String ENTITY_USER = "bikeuser";

    // To distinguish GET ops
    public static final String GET_FIND_ALL = null; //no need of data to complete URLs
    public static final String GET_FIND_USERID = "user/%s"; //the user to find

    // To distinguish PUT (bikes) ops in the URL
    public static final String PUT_TAKE_BIKE = "take";
    public static final String PUT_LEAVE_BIKE = "leave";
    public static final String PUT_BOOK_BIKE = "book_bike";
    public static final String PUT_BOOK_MOORINGS = "book_moorings";

    // To distinguish PUT (user) ops in the URL
    public static final String PUT_USER_BASIC_DATA = "basicdata";

    //Standard server responses (used in POST and PUT ops)
    public static final String SERVER_RESPONSE_OK = "SERVER_OK";
    public static final String SERVER_RESPONSE_KO = "SERVER_KO";

    // Suppress default constructor for non-instantiability
    private HttpConstants() {}

}
