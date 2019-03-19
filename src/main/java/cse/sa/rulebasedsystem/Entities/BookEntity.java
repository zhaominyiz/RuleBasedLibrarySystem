package cse.sa.rulebasedsystem.Entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "book", schema = "library", catalog = "")
public class BookEntity {
    private String publishId;
    private int id;
    private String name;
    private String publisher;
    private String img;
    private String author;
    private String isbn;
    private int num;
    private int res;
    private String description;
    private Timestamp addTime;
    private String position;
    private String type;
    private String version;

    @Basic
    @Column(name = "publishID", nullable = false, length = 255)
    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "publisher", nullable = false, length = 255)
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Basic
    @Column(name = "img", nullable = true, length = 255)
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Basic
    @Column(name = "author", nullable = false, length = 255)
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Basic
    @Column(name = "isbn", nullable = false, length = 255)
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Basic
    @Column(name = "num", nullable = false)
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Basic
    @Column(name = "res", nullable = false)
    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "add_time", nullable = true)
    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    @Basic
    @Column(name = "position", nullable = true, length = 255)
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Basic
    @Column(name = "type", nullable = true, length = 255)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "version", nullable = true, length = 255)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEntity that = (BookEntity) o;
        return id == that.id &&
                num == that.num &&
                res == that.res &&
                Objects.equals(publishId, that.publishId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(publisher, that.publisher) &&
                Objects.equals(img, that.img) &&
                Objects.equals(author, that.author) &&
                Objects.equals(isbn, that.isbn) &&
                Objects.equals(description, that.description) &&
                Objects.equals(addTime, that.addTime) &&
                Objects.equals(position, that.position) &&
                Objects.equals(type, that.type) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publishId, id, name, publisher, img, author, isbn, num, res, description, addTime, position, type, version);
    }
}
