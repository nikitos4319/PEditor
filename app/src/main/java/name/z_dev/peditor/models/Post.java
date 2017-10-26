package name.z_dev.peditor.models;

/**
 * Created by Home on 04.08.2015.
 */
public class Post {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCrDate() {
        return crDate;
    }

    public void setCrDate(String crDate) {
        this.crDate = crDate;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAuthor() {
        return author;
    }

    public void setAuthor(long author) {
        this.author = author;
    }

    public boolean isComment_status() {
        return comment_status;
    }

    public void setComment_status(boolean comment_status) {
        this.comment_status = comment_status;
    }

    public boolean isPing_status() {
        return ping_status;
    }

    public void setPing_status(boolean ping_status) {
        this.ping_status = ping_status;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAuthorLink() {
        return authorLink;
    }

    public void setAuthorLink(String authorLink) {
        this.authorLink = authorLink;
    }

    long id;//
    String title;//
    String slug;//
    String content;//
    String link;//
    String crDate;//
    String modified;//
    String type;//
    long author;//
    boolean comment_status;//
    boolean ping_status;//
    boolean sticky;//
    String format;//
    String authorLink;
    String excerpt;//

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Post(String title, String url, String date, Long id) {
        this.title = title;
        this.link = url;
        this.crDate = date;
        this.id = id;
    }
    public Post(){

    }


}
