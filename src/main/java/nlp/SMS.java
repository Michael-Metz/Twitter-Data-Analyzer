package nlp;

import org.w3c.dom.NamedNodeMap;

import java.util.Date;

public class SMS {

    String protocol;
    String address;
    String date;
    String type;
    String subject;
    String body;
    String toa;
    String sc_toa;
    String service_center;
    String read;
    String status;
    String locked;
    String date_sent;
    String readable_date;
    String contact_name;

    public SMS(NamedNodeMap namedNodeMap){
        this.protocol = namedNodeMap.getNamedItem("protocol").getNodeValue();
        this.address = namedNodeMap.getNamedItem("address").getNodeValue();
        this.date = namedNodeMap.getNamedItem("date").getNodeValue();
        this.type = namedNodeMap.getNamedItem("type").getNodeValue();
        this.subject  = namedNodeMap.getNamedItem("subject").getNodeValue();
        this.body = namedNodeMap.getNamedItem("body").getNodeValue();
        this.toa = namedNodeMap.getNamedItem("toa").getNodeValue();
        this.sc_toa = namedNodeMap.getNamedItem("sc_toa").getNodeValue();
        this.service_center = namedNodeMap.getNamedItem("service_center").getNodeValue();
        this.read  = namedNodeMap.getNamedItem("read").getNodeValue();
        this.status = namedNodeMap.getNamedItem("status").getNodeValue();
        this.locked = namedNodeMap.getNamedItem("locked").getNodeValue();
        this.date_sent = namedNodeMap.getNamedItem("date_sent").getNodeValue();
        this.readable_date = namedNodeMap.getNamedItem("readable_date").getNodeValue();
        this.contact_name = namedNodeMap.getNamedItem("contact_name").getNodeValue();
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getToa() {
        return toa;
    }

    public void setToa(String toa) {
        this.toa = toa;
    }

    public String getSc_toa() {
        return sc_toa;
    }

    public void setSc_toa(String sc_toa) {
        this.sc_toa = sc_toa;
    }

    public String getService_center() {
        return service_center;
    }

    public void setService_center(String service_center) {
        this.service_center = service_center;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getDate_sent() {
        return date_sent;
    }

    public void setDate_sent(String date_sent) {
        this.date_sent = date_sent;
    }

    public String getReadable_date() {
        return readable_date;
    }

    public void setReadable_date(String readable_date) {
        this.readable_date = readable_date;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }
}
