package demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Collections;

@EnableAutoConfiguration
@ComponentScan
@Controller
@Configuration
@EnableEurekaClient
@EnableHystrix
public class App {
    @Autowired
    ShortenService shortenService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @ModelAttribute
    ShortenForm setupForm() {
        return new ShortenForm();
    }

    @RequestMapping("/")
    String home() {
        return "index";
    }

    @RequestMapping(value = "shorten", method = RequestMethod.POST)
    String shorten(@Validated ShortenForm form, BindingResult result, RedirectAttributes attributes) throws IOException {
        if (result.hasErrors()) {
            return "index";
        }
        String shortenUrl = shortenService.shorten(form.getUrl());
        attributes.addFlashAttribute("shortenUrl", shortenUrl);
        return "redirect:/";
    }

    @RequestMapping(value = "{hash}", method = RequestMethod.GET)
    String redirect(@PathVariable String hash) {
        String url = shortenService.getUrl(hash);
        return "redirect:" + url;
    }
}

class ShortenForm {
    @NotEmpty
    @URL
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

@Service
@RefreshScope
class ShortenService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${urlshorten.api.url:http://localhost:8081}")
    String apiUrl;
    Logger logger = LoggerFactory.getLogger(ShortenService.class);

    @HystrixCommand(fallbackMethod = "defaultShorten")
    public String shorten(String url) throws IOException {
        logger.info("calling shorten {}", url);
        URI target = URI.create(apiUrl + "/" + URLDecoder.decode(url, "UTF-8")); // TOO //が/に短縮されるのを防止
        return restTemplate.postForObject(target, null, String.class);
    }

    public String defaultShorten(String url) throws IOException {
        logger.info("failed shorten {}", url);
        return "failed!";
    }

    @HystrixCommand(fallbackMethod = "defaultGetUrl")
    public String getUrl(String hash) {
        logger.info("calling getUrl {}", hash);
        // Tips: プレースホルダを利用するとURLエンコーディングが行われる。shortenメソッド内ではエンコーディング不要だった。
        return restTemplate.getForObject(apiUrl + "/{hash}", String.class, Collections.singletonMap("hash", hash));
    }

    public String defaultGetUrl(String hash) {
        logger.info("failed getUrl {}", hash);
        return "failed!";
    }
}