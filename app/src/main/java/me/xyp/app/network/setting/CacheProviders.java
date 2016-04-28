package me.xyp.app.network.setting;


import java.util.List;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictDynamicKey;
import io.rx_cache.Reply;
import me.xyp.app.model.Article;
import me.xyp.app.model.ArticleBasic;
import me.xyp.app.model.Course;
import me.xyp.app.model.Exam;
import me.xyp.app.model.Grade;
import me.xyp.app.model.Student;
import rx.Observable;

/**
 * Created by cc on 16/4/23.
 */
public interface CacheProviders {

    Observable<Reply<List<Course>>> getCoursesByStuNum(Observable<List<Course>> oCourses, DynamicKey stuNum, EvictDynamicKey evictDynamicKey);

    Observable<Reply<List<ArticleBasic>>> getArticleBasicListByDirId(Observable<List<ArticleBasic>> oArticleBasics, DynamicKey dirId, EvictDynamicKey evictDynamicKey);

    Observable<Reply<Article>> getArticleById(Observable<Article> oArticle, DynamicKey id, EvictDynamicKey evictDynamicKey);

    Observable<Reply<Student>> getStudentInfoByStuNum(Observable<Student> oStudent, DynamicKey stuNum, EvictDynamicKey evictDynamicKey);

    Observable<Reply<Grade>> getGradeByStuNum(Observable<Grade> oGrade, DynamicKey stuNum, EvictDynamicKey evictDynamicKey);

    Observable<Reply<List<Exam>>> getExamScheduleByStuNum(Observable<List<Exam>> oExams, DynamicKey stuNum, EvictDynamicKey evictDynamicKey);

}
