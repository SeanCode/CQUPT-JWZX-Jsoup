package me.xyp.app.model;

import java.util.List;

/**
 * Created by cc on 16/4/26.
 */
public class Article extends ArticleBaic {

    public String contentHtml;
    public List<Attachment> attachmentList;

    public static class Attachment {
        public String fileName;
        public String fileUrl;
    }

}
