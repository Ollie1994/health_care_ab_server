package healthcareab.project.healthcare_booking_app.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "logs")
public class Log {
    @Id
    private String id;
    private LocalDateTime timeStamp;
    private String userExposed;
    private String performedBy;
    private Boolean success;
    private ActionPerformed actionPerformed;

    public Log(String id, LocalDateTime timeStamp, String userExposed, String performedBy, Boolean success, ActionPerformed actionPerformed) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.userExposed = userExposed;
        this.performedBy = performedBy;
        this.success = success;
        this.actionPerformed = actionPerformed;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getUserExposed() {
        return userExposed;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public Boolean getSuccess() {
        return success;
    }

    public ActionPerformed getActionPerformed() {
        return actionPerformed;
    }
}
