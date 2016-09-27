package plow_android.capic.com.plowandroid.beans;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Vincent on 27/01/2016.
 */
public class Download implements Serializable {
    public final static int STATUS_WAITING = 1;
    public final static int STATUS_IN_PROGRESS = 2;
    public final static int STATUS_FINISHED = 3;

    private Long id;
    private String name;
    private String link;
    private Long sizeFile;
    private Long sizePart;
    private Long sizeFileDownloaded;
    private Long sizePartDownloaded;
    private byte status;
    private byte progressPart;
    private byte progressFile;
    private Long averageSpeed;
    private Long currentSpeed;
    private Long timeSpent;
    private Long timeLeft;
    private int pidPlowdown;
    private int pidPython;
    private String filePath;
    private byte priority;
    private Date theoricalStartDateTime;
    private Date lifecycleInsertDate;
    private Date lifecycleUpdateDate;
    private Long hostId;

    public Download() {}

    public Download(Long id, String name, String link, Long sizeFile, Long sizePart, Long sizeFileDownloaded, Long sizePartDownloaded, byte status, byte progressPart, byte progressFile, Long averageSpeed, Long currentSpeed, Long timeSpent, Long timeLeft, int pidPlowdown, int pidPython, String filePath, byte priority/*, Date theoricalStartDateTime, Date lifecycleInsertDate, Date lifecycleUpdateDate*/, Long hostId) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.sizeFile = sizeFile;
        this.sizePart = sizePart;
        this.sizeFileDownloaded = sizeFileDownloaded;
        this.sizePartDownloaded = sizePartDownloaded;
        this.status = status;
        this.progressPart = progressPart;
        this.progressFile = progressFile;
        this.averageSpeed = averageSpeed;
        this.currentSpeed = currentSpeed;
        this.timeSpent = timeSpent;
        this.timeLeft = timeLeft;
        this.pidPlowdown = pidPlowdown;
        this.pidPython = pidPython;
        this.filePath = filePath;
        this.priority = priority;
        /*this.theoricalStartDateTime = theoricalStartDateTime;
        this.lifecycleInsertDate = lifecycleInsertDate;
        this.lifecycleUpdateDate = lifecycleUpdateDate;*/
        this.hostId = hostId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(Long sizeFile) {
        this.sizeFile = sizeFile;
    }

    public Long getSizePart() {
        return sizePart;
    }

    public void setSizePart(Long sizePart) {
        this.sizePart = sizePart;
    }

    public Long getSizeFileDownloaded() {
        return sizeFileDownloaded;
    }

    public void setSizeFileDownloaded(Long sizeFileDownloaded) {
        this.sizeFileDownloaded = sizeFileDownloaded;
    }

    public Long getSizePartDownloaded() {
        return sizePartDownloaded;
    }

    public void setSizePartDownloaded(Long sizePartDownloaded) {
        this.sizePartDownloaded = sizePartDownloaded;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getProgressPart() {
        return progressPart;
    }

    public void setProgressPart(byte progressPart) {
        this.progressPart = progressPart;
    }

    public byte getProgressFile() {
        return progressFile;
    }

    public void setProgressFile(byte progressFile) {
        this.progressFile = progressFile;
    }

    public Long getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Long averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Long getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(Long currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(Long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getPidPlowdown() {
        return pidPlowdown;
    }

    public void setPidPlowdown(int pidPlowdown) {
        this.pidPlowdown = pidPlowdown;
    }

    public int getPidPython() {
        return pidPython;
    }

    public void setPidPython(int pidPython) {
        this.pidPython = pidPython;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Date getTheoricalStartDateTime() {
        return theoricalStartDateTime;
    }

    public void setTheoricalStartDateTime(Date theoricalStartDateTime) {
        this.theoricalStartDateTime = theoricalStartDateTime;
    }

    public Date getLifecycleInsertDate() {
        return lifecycleInsertDate;
    }

    public void setLifecycleInsertDate(Date lifecycleInsertDate) {
        this.lifecycleInsertDate = lifecycleInsertDate;
    }

    public Date getLifecycleUpdateDate() {
        return lifecycleUpdateDate;
    }

    public void setLifecycleUpdateDate(Date lifecycleUpdateDate) {
        this.lifecycleUpdateDate = lifecycleUpdateDate;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public void fromJson(ObjectNode json) {
        if (json != null) {
            this.id = json.get("id") != null ? json.get("id").asLong() : -1;
            this.name = json.get("name") != null ? json.get("name").asText() : "";
            this.link = json.get("link") != null ? json.get("link").asText() : "";
            this.sizeFile = json.get("size_file") != null ? json.get("size_file").asLong() : 0;
            this.sizePart = json.get("size_part") != null ? json.get("size_part").asLong() : 0;
            this.sizeFileDownloaded = json.get("size_file_downloaded") != null ? json.get("size_file_downloaded").asLong() : 0;
            this.sizePartDownloaded = json.get("size_part_downloaded") != null ? json.get("size_part_downloaded").asLong() : 0;
            this.status = json.get("status") != null ? Byte.valueOf(json.get("status").asText()) : -1;
            this.progressPart = json.get("progress_part") != null ? Byte.valueOf(json.get("progress_part").asText()) : 0;
            this.progressFile = json.get("progress_file") != null ? Byte.valueOf(json.get("progress_file").asText()) : 0;
            this.averageSpeed = json.get("average_speed") != null ? json.get("average_speed").asLong() : 0;
            this.currentSpeed = json.get("current_speed") != null ? json.get("current_speed").asLong() : 0;
            this.timeSpent = json.get("time_spent") != null ? json.get("time_spent").asLong() : 0;
            this.timeLeft = json.get("time_left") != null ? json.get("time_left").asLong() : 0;
            this.pidPlowdown = json.get("pid_plowdown") != null ? json.get("pid_plowdown").asInt() : 0;
            this.pidPython = json.get("pid_python") != null ? json.get("pid_python").asInt() : 0;
            this.filePath = json.get("file_path") != null ? json.get("file_path").asText() : "";
            this.priority = json.get("priority") != null ? Byte.valueOf(json.get("priority").asText()) : -1;
        /*this.theoricalStartDateTime = theoricalStartDateTime;
        this.lifecycleInsertDate = lifecycleInsertDate;
        this.lifecycleUpdateDate = lifecycleUpdateDate;*/
            this.hostId = json.get("host_id") != null ? json.get("host_id").asLong() : -1;
        }
    }
}
