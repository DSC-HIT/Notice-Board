package dschik.noticeboard;

import androidx.annotation.Keep;

@Keep
public class UserObj {

    private String name;
    private String email;
    private String phone;
    private String progress;
    private String dept;
    private String sec;
    private String yr;

    public UserObj() {
    }


    public UserObj(String name, String email, String phone, String progress, String dept, String sec, String yr) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.progress = progress;
        this.dept = dept;
        this.sec = sec;
        this.yr = yr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getYr() {
        return yr;
    }

    public void setYr(String yr) {
        this.yr = yr;
    }


}
