package com.micropos.products.repository;

import com.micropos.datatype.product.Product;
import com.netflix.discovery.converters.Auto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JDRepository implements ProductRepository {
    private List<Product> products = null;

    @Autowired
    private CacheHelper cacheHelper;

    @Override
    public List<Product> allProducts() {
        if(cacheHelper.getProductCache().containsKey(0)){
            return (List<Product>) cacheHelper.getProductCache().get(0);
        }

        try {
            if (products == null){
                products = parseJD("Java");
                cacheHelper.getProductCache().put(0,products);
            }
        } catch (IOException e) {
            products = new ArrayList<>();
        }
        return products;
    }

    @Override
    public Product findProduct(String productId) {
        for (Product p : allProducts()) {
            if (p.getId().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    public static List<Product> parseJD(String keyword) throws IOException {
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        Document document = Jsoup.parse(new URL(url), 10000);
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");
        List<Product> list = new ArrayList<>();

        for (Element el : elements) {
            String id = el.attr("data-spu");
            String img = "https:".concat(el.getElementsByTag("img").eq(0).attr("data-lazy-img"));
            String price = el.getElementsByAttribute("data-price").text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            if (title.indexOf("，") >= 0)
                title = title.substring(0, title.indexOf("，"));

            Product product = new Product(id, title, Double.parseDouble(price), img);
            list.add(product);
        }
        return list;
    }
}
