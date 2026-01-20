package healthcareab.project.healthcare_booking_app.utils;

public class UserIdSalter {

    //https://chatgpt.com/share/696f9b62-e534-8013-b527-fd8bbbd62c07

    private static final String SALT = "_x9_";
    private static final int INSERT_INDEX = 2;

    private UserIdSalter() {
        }
    /**
     * Saltar ett användar-ID för loggning.
     *
     * Exempel:
     *  123456 -> 12__X9__3456
     */

    public static String salt(String userId) {
        if (userId == null || userId.isEmpty()) {
            return userId;
        }

        int index = Math.min(INSERT_INDEX, userId.length());
        return userId.substring(0, index)
                + SALT
                + userId.substring(index);
    }
    /**
     * Återställer ett saltat användar-ID.
     * Endast för felsökning / intern spårning.
     */

    public static String unsalt(String saltedUserId) {
        if (saltedUserId == null || saltedUserId.isEmpty()) {
            return saltedUserId;
        }
        return saltedUserId.replace(SALT, "");
    }
}
