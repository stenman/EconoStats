package se.perfektum.econostats.dao.googledrive;

public enum MimeTypes {
    GOOGLE_API_SPREADSHEET("application/vnd.google-apps.spreadsheet"),
    TEXT_ODS("text/ods"),
    APPLICATION_JSON("application/json");

    private final String name;

    MimeTypes(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
