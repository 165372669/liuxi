package com.android.lucy.treasure.runnable.chapter;

import android.graphics.Rect;
import android.os.Handler;

import com.android.lucy.treasure.base.BaseReadThread;
import com.android.lucy.treasure.bean.BookCatalogInfo;
import com.android.lucy.treasure.bean.PagerConfigInfo;
import com.android.lucy.treasure.bean.ChapterPagerContentInfo;
import com.android.lucy.treasure.bean.PagerContentTextInfo;
import com.android.lucy.treasure.utils.Key;
import com.android.lucy.treasure.utils.MyHandler;
import com.android.lucy.treasure.utils.MyLogcat;
import com.android.lucy.treasure.utils.StringUtils;
import com.android.lucy.treasure.view.CircleProgress;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * www.pbtxt.com             平板电子书网
 * https://www.xxbiquge.com/ 新笔趣阁
 * www.80txt.com  八零电子书
 * 获取章节内容线程
 */

public class DDAChapterContentThread extends BaseReadThread {


    private final BookCatalogInfo bookCatalogInfo;
    private final PagerConfigInfo pagerConfigInfo;
    private final CircleProgress cv_chapter_progress;

    public DDAChapterContentThread(String url, Handler myHandler, BookCatalogInfo bookCatalogInfo, PagerConfigInfo pagerConfigInfo, CircleProgress cv_chapter_progress) {
        super(url, myHandler);
        this.bookCatalogInfo = bookCatalogInfo;
        this.pagerConfigInfo = pagerConfigInfo;
        this.cv_chapter_progress = cv_chapter_progress;
        flag = "chapter-" + bookCatalogInfo.getChapterId();
    }

    @Override
    public void resoloveUrl(Document doc) {
        cv_chapter_progress.startProgress(300);
        String url = bookCatalogInfo.getChapterUrl();
        url = StringUtils.shearSourceName(url);
        Elements divs;
        switch (url) {
            case Key.KEY_88DUSHU_CHAPTER:
                divs = doc.select("div[class^=yd_text2]");
                break;
            default:
                divs = doc.select("div[id^=content]");
                break;
        }
        String str = divs.toString();
        List<String> contents = new ArrayList<>();
        String reg = "(\\“[\u4e00-\u9fa5]|[\u4e00-\u9fa5])(.)*";//以汉字开始或者以“汉字开始
        //让规则封装成对象
        Pattern p = Pattern.compile(reg);
        //让正则对象和要作用的字符串相关联。获取匹配器对象。
        Matcher m = p.matcher(str);
        while (m.find()) {
            String s = m.group();
            if (s.startsWith("下载地址："))
                break;
            contents.add(s);
            //System.out.println(m.group());
        }
        cv_chapter_progress.startProgress(1000);
        if (contents.size() > 0) {
            spacingLineCount(contents);
            int pagerTotal = bookCatalogInfo.getStrs().size();
            bookCatalogInfo.setChapterPagerToatal(pagerTotal);//设置章节总数
            sendObj(MyHandler.CHAPTER_LOADING_OK, bookCatalogInfo.getChapterId());
        } else {
            //没有获取到数据。
            sendObj(MyHandler.CHAPTER_LOADING_NO, bookCatalogInfo.getChapterId());
        }
    }

    @Override
    public void errorHandle(IOException e) {
        if (count < 5) {
            readUrl();
        } else {
            //网页读取失败
            sendObj(MyHandler.CHAPTER_LOADING_ERROR, bookCatalogInfo.getChapterId());
        }
    }

    public void xxbuquge() {
        if (baseUrl.startsWith("https://www.xxbiquge.com")) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                urlConnection = (HttpURLConnection) new URL(baseUrl).openConnection();
                urlConnection.setDoInput(true);
                MyLogcat.myLog("statusCode:" + urlConnection.getResponseCode());
                if (urlConnection.getResponseCode() == 200) {
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    resoloveUrl(Jsoup.parse(sb.toString()));
                }
            } catch (IOException e) {
                MyLogcat.myLog(baseUrl + "，网页读取失败！");
            } finally {
                try {
                    if (null != urlConnection) urlConnection.disconnect();
                    if (null != reader) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /*
    * 移除字符串中的空格
    * */
    private String removeEmpty(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch != ' ') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }


    /**
     * 计算行间距，赋值字宽，行数，页面,x,y
     *
     * @param books 章节内容集合
     * @return
     */
    public void spacingLineCount(List<String> books) {
        int pager = 0;//当前段落页数
        ArrayList<PagerContentTextInfo> pagerContentTextInfos = new ArrayList<>();
        int line = 1;//初始行数
        int lineWidth = 0; //一行宽度
        int z = 0;
        int lineTextLength = 0;//一行的字数
        float x = 0;
        float y = pagerConfigInfo.getChapterNameHeight();
        for (int i = 0; i < books.size(); i++) {
            if (getIsCancelled()) {
                bookCatalogInfo.clearStrs();
                return;
            }
            String str = books.get(i);
            str = removeEmpty(str);
            for (int j = 0; j < str.length(); j++) {
                char ch = str.charAt(j);
                String s = String.valueOf(ch);
                PagerContentTextInfo pagerContentTextInfo = new PagerContentTextInfo(s);
                int strWidth = pagerConfigInfo.getTextWidth() + 3;
                //段落的第一行加上两个字的宽度
                if (j == 0) {
                    lineWidth = lineWidth + strWidth * 2;
                    pagerContentTextInfo.setStringOneLine(true);
                }
                //字母或者数字，一些符号重新赋值宽度，使其紧凑。
                if (s.equals(".") || s.equals("“") || s.equals("”") || s.equals("*") ||
                        s.equals("(") || s.equals(")") || s.equals("+") || s.equals("-") ||
                        s.equals("/")) {
                    Rect rect = new Rect();
                    pagerConfigInfo.getmTextPaint().getTextBounds(s, 0, 1, rect);
                    strWidth = rect.width() + 2;
                }
                if (StringUtils.isNumOrLetters(s)) {
                    Rect rect = new Rect();
                    pagerConfigInfo.getmTextPaint().getTextBounds(s, 0, 1, rect);
                    strWidth = rect.width() + 5;
                }
                pagerContentTextInfo.setWidth(strWidth);
                lineWidth += strWidth;
                pagerContentTextInfos.add(pagerContentTextInfo);
                lineTextLength++;
                //累加宽度比控件宽度大了或者字符到最后一段落了。
                if (lineWidth >= pagerConfigInfo.getChapterContentWidth() || j == str.length() - 1) {
                    float spacing = 0;
                    //累加宽度比控件宽度大才增加间歇
                    if (lineWidth > pagerConfigInfo.getChapterContentWidth()) {
                        lineWidth -= strWidth;
                        //大于了宽度删除最后一个元素
                        pagerContentTextInfos.remove(pagerContentTextInfos.size() - 1);
                        j--;
                        lineTextLength--;
                        float spacingLine = pagerConfigInfo.getChapterContentWidth() - lineWidth;
                        spacing = spacingLine / (lineTextLength);
                    }
                    for (int q = 0; z < pagerContentTextInfos.size(); z++, q++) {
                        PagerContentTextInfo info = pagerContentTextInfos.get(z);
                        //如果是一行的首字母
                        if (q == 0) {
                            x = 0;
                            //如果是段落第一行首字母
                            if (info.isStringOneLine()) {
                                x = (pagerConfigInfo.getTextWidth() + 5) * 2;
                            }
                        } else {
                            PagerContentTextInfo text = pagerContentTextInfos.get(z - 1);
                            x = x + text.getWidth() + spacing;
                        }
                        info.setX(x);
                        info.setY(y);
                    }
                    line++;//行数加1
                    lineWidth = 0;
                    x = 0;
                    y = y + pagerConfigInfo.getTextHeight() * 2;
                    lineTextLength = 0;
                }
                //大于页面容纳最行大数，页面+1，行数归1
                if (line > pagerConfigInfo.getPagerLine()) {
                    pager++;
                    line = 1;
                    y = pagerConfigInfo.getChapterNameHeight();  //y坐标初始化
                    ChapterPagerContentInfo chapterPagerContentInfo = new ChapterPagerContentInfo(pagerContentTextInfos, pager);
                    bookCatalogInfo.getStrs().add(chapterPagerContentInfo);
                    pagerContentTextInfos = new ArrayList<>();
                    z = 0;
                }

            }
            //最后一个段落
            if (i == books.size() - 1 && str.length() > 0) {
                pager++;
                ChapterPagerContentInfo chapterPagerContentInfo = new ChapterPagerContentInfo(pagerContentTextInfos, pager);
                if (chapterPagerContentInfo.getPagerContentTextInfos().size() > 0) {
                    bookCatalogInfo.getStrs().add(chapterPagerContentInfo);
                }
            }
        }
    }


}
