package com.dyhdyh.magnetw.service;

import com.dyhdyh.magnetw.model.MagnetInfo;
import com.dyhdyh.magnetw.model.MagnetRule;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * author  dengyuhan
 * created 2018/3/6 16:04
 */
@Service
public class MagnetWService {
    @Cacheable(value = "magnetList", key = "T(String).format('%s-%s-%d',#rule.source,#keyword,#page)")
    public List<MagnetInfo> parser(MagnetRule rule, String keyword, int page) throws IOException, XPathExpressionException, ParserConfigurationException, XPatherException {
        return parser(rule.getUrl(), rule.getSource(), keyword, page, rule.getGroup(), rule.getMagnet(), rule.getName(), rule.getSize(), rule.getCount());
    }

    @Cacheable(value = "magnetList", key = "T(String).format('%s-%s-%d',#url,#keyword,#page)")
    public List<MagnetInfo> parser(String rootUrl, String url, String keyword, int page, String group, String magnet, String name, String size, String count) throws IOException, XPathExpressionException, ParserConfigurationException, XPatherException {
        String newUrl = transformUrl(url, keyword, page);
        String html = Jsoup.connect(newUrl).get().body().html();


        XPath xPath = XPathFactory.newInstance().newXPath();
        TagNode tagNode = new HtmlCleaner().clean(html);
        Document dom = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

        NodeList result = (NodeList) xPath.evaluate(group, dom, XPathConstants.NODESET);
        List<MagnetInfo> infos = new ArrayList<MagnetInfo>();
        for (int i = 0; i < result.getLength(); i++) {
            Node node = result.item(i);
            if (node != null) {
                if (StringUtils.isEmpty(node.getTextContent().trim())) {
                    continue;
                }
                MagnetInfo info = new MagnetInfo();
                Node magnetNote = (Node) xPath.evaluate(magnet, node, XPathConstants.NODE);
                //磁力链
                String magnetValue = magnetNote.getTextContent();
                info.setMagnet(transformMagnet(magnetValue));
                //名称
                Node nameNote = ((Node) xPath.evaluate(name, node, XPathConstants.NODE));
                String nameValue = nameNote.getTextContent();
                info.setName(nameValue);
                String nameHref = nameNote.getAttributes().getNamedItem("href").getTextContent();
                info.setDetailUrl(transformDetailUrl(rootUrl, nameHref));
                //大小
                Node sizeNote = ((Node) xPath.evaluate(size, node, XPathConstants.NODE));
                if (sizeNote != null) {
                    String sizeValue = sizeNote.getTextContent();
                    info.setFormatSize(sizeValue);

                    info.setSize(transformSize(sizeValue));
                }
                //时间
                String countValue = ((Node) xPath.evaluate(count, node, XPathConstants.NODE)).getTextContent();
                info.setCount(countValue);
                //一些加工的额外信息
                String resolution = transformResolution(nameValue);
                info.setResolution(resolution);

                infos.add(info);
            }
        }
        return infos;
    }


    public int transformPage(Integer page) {
        return page == null || page <= 0 ? 1 : page;
    }

    /**
     * 数据源链接转换
     *
     * @param url
     * @param keyword
     * @param page
     * @return
     */
    private String transformUrl(String url, String keyword, int page) {
        return url.replaceFirst("XXX", keyword)
                .replaceFirst("PPP", String.valueOf(page));
    }


    private String transformDetailUrl(String url, String magnetValue) {
        return magnetValue.startsWith("http") ? magnetValue : url + magnetValue;
    }

    /**
     * 磁力链转换
     *
     * @param url
     * @return
     */
    private String transformMagnet(String url) {
        String regex = "magnet:?[^\\\"]+";
        boolean matches = Pattern.matches(regex, url);
        if (matches) {
            return url;
        } else {
            String newMagnet;
            try {
                StringBuffer sb = new StringBuffer(url);
                int htmlIndex = url.lastIndexOf(".html");
                if (htmlIndex != -1) {
                    sb.delete(htmlIndex, sb.length());
                }
                int paramIndex = url.indexOf("&");
                if (paramIndex != -1) {
                    sb.delete(paramIndex, sb.length());
                }
                if (sb.length() >= 40) {
                    newMagnet = sb.substring(sb.length() - 40, sb.length());
                } else {
                    newMagnet = url;
                }
            } catch (Exception e) {
                e.printStackTrace();
                newMagnet = url;
            }
            return String.format("magnet:?xt=urn:btih:%s", newMagnet);
        }
    }


    private String transformResolution(String name) {
        String regex720 = ".*(1280|720p|720P).*";
        String regex1080 = ".*(1920|1080p|1080P).*";
        boolean matches720 = Pattern.matches(regex720, name);
        if (matches720) {
            return "720P";
        }
        boolean matches1080 = Pattern.matches(regex1080, name);
        if (matches1080) {
            return "1080P";
        }
        return "";
    }


    private long transformSize(String formatSize) {
        long newSize = 0;
        try {
            long baseNumber = 0;
            String newFormatSize = formatSize.toUpperCase().replace(" ", "").replace(" ", "");
            if (newFormatSize.endsWith("GB")) {
                baseNumber = 1024 * 1024 * 1024;
                newFormatSize = newFormatSize.replace("GB", "");
            } else if (newFormatSize.endsWith("MB")) {
                baseNumber = 1024 * 1024;
                newFormatSize = newFormatSize.replace("MB", "");
            } else if (newFormatSize.endsWith("KB")) {
                baseNumber = 1024;
                newFormatSize = newFormatSize.replace("KB", "");
            }
            float size = Float.parseFloat(newFormatSize);
            newSize = (long) (size * baseNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newSize;
    }

}
